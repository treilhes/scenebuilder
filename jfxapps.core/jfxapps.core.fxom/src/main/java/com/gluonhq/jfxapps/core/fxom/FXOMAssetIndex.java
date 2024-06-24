/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.gluonhq.jfxapps.core.fxom;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.collector.SceneGraphCollector;
import com.gluonhq.jfxapps.core.fxom.util.FXMLFormatUtils;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.fxml.FXMLLoader;

/**
 *
 */
public class FXOMAssetIndex {

    private final FXOMDocument fxomDocument;
    private final Map<Path, FXOMNode> fileAssets;

    public FXOMAssetIndex(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        this.fxomDocument = fxomDocument;
        this.fileAssets = Collections.unmodifiableMap(collectAssets());
    }

    public Map<Path, FXOMNode> getFileAssets() {
        return fileAssets;
    }

    /*
     * Private
     */

    private static final PropertyName valueName = new PropertyName("value"); //NOCHECK

    private Map<Path, FXOMNode> collectAssets() {
        final Map<Path, FXOMNode> result = new HashMap<>();

        if (fxomDocument.getFxomRoot() != null) {
            final FXOMObject fxomRoot = fxomDocument.getFxomRoot();

            /*
             * Collects properties containing prefixed values (ie @ expression).
             */
            for (FXOMPropertyT p : fxomRoot.collect(PropertyCollector.allSimpleProperties())) {
                for (String s : FXMLFormatUtils.splitArrayValue(p.getValue())) {
                    final Path path = extractPath(s);
                    if (path != null) {
                        result.put(path, p);
                    }
                }
            }


            /*
             * Collects URL instances.
             */
            for (FXOMObject fxomObject : fxomRoot.collect(SceneGraphCollector.sceneGraphObjectByClass(URL.class))) {
                if (fxomObject instanceof FXOMInstance) {
                    final FXOMInstance urlInstance = (FXOMInstance) fxomObject;
                    final FXOMProperty valueProperty = urlInstance.getProperties().get(valueName);
                    if (valueProperty instanceof FXOMPropertyT) {
                        FXOMPropertyT valuePropertyT = (FXOMPropertyT) valueProperty;
                        final Path path = extractPath(valuePropertyT.getValue());
                        if (path != null) {
                            result.put(path, valuePropertyT);
                        }
                    } else {
                        assert false : "valueProperty.getName()=" + valueProperty.getName();
                    }
                }
            }

            /*
             * Collects fx:include
             */
            for (FXOMIntrinsic fxomInclude : fxomRoot.collect(FxCollector.allFxIncludes())) {
                final String equivalentValue = FXMLLoader.RELATIVE_PATH_PREFIX + fxomInclude.getSource();
                final Path path = extractPath(equivalentValue);
                if (path != null) {
                    result.put(path, fxomInclude);
                }
            }
        }

        return result;
    }


    private Path extractPath(String stringValue) {
        Path result;

        final PrefixedValue pv = new PrefixedValue(stringValue);
        if (pv.isPlainString()) {
            try {
                final File file = URLUtils.getFile(pv.getSuffix());
                if (file == null) { // Not a file URL
                    result = null;
                } else {
                    result = file.toPath();
                }
            } catch(URISyntaxException x) {
                result = null;
            }
        } else if (pv.isDocumentRelativePath()) {
            final URL documentLocation = fxomDocument.getLocation();
            if (documentLocation == null) {
                result = null;
            } else {
                final URL url = pv.resolveDocumentRelativePath(documentLocation);
                if (url == null) {
                    result = null;
                } else {
                    try {
                        result = Paths.get(url.toURI());
                    } catch(FileSystemNotFoundException|URISyntaxException x) {
                        result = null;
                    }
                }
            }
        } else if (pv.isClassLoaderRelativePath()) {
            final ClassLoader classLoader = fxomDocument.getClassLoader();
            if (classLoader == null) {
                result = null;
            } else {
                final URL url = pv.resolveClassLoaderRelativePath(classLoader);
                if (url == null) {
                    result = null;
                } else {
                    try {
                        final File file = URLUtils.getFile(url);
                        if (file == null) { // Not a file URL
                            result = null;
                        } else {
                            result = file.toPath();
                        }
                    } catch(URISyntaxException x) {
                        result = null;
                    }
                }

            }
        } else {
            result = null;
        }

        return result;
    }

}
