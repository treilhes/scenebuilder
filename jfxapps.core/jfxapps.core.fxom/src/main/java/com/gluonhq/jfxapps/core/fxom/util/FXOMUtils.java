/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.fxom.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.collector.SceneGraphCollector;

public class FXOMUtils {

    private static final PropertyName valueName = new PropertyName("value"); //NOCHECK

	private FXOMUtils() {}

	/**
	 * Check if the Fxml document has external dependencies.
	 *
	 * @param fxmlFile the fxml file
	 * @param classloader the classloader
	 * @param resources the resources
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// FIXME only used by libraries, move this function to libraries common parent if any or create one
	public static boolean fxmlHasDependencies(File fxmlFile, ClassLoader classloader, ResourceBundle resources) throws IOException {
        boolean res = false;
        URL location;

        location = fxmlFile.toURI().toURL();
        FXOMDocument fxomDocument =
                new FXOMDocument(FXOMDocument.readContentFromURL(location), location,
                        classloader, resources);
        res = hasDependencies(fxomDocument.getFxomRoot());

        return res;
    }

    public static boolean hasDependencies(FXOMObject rootFxomObject) {
        final List<Path> targetPaths = getDependenciesPaths(rootFxomObject);
        return targetPaths.size() > 0;
    }

    private static List<Path> getDependenciesPaths(FXOMObject rootFxomObject) {

        final List<Path> targetPaths = new ArrayList<>();

        for (FXOMPropertyT p : rootFxomObject.collect(PropertyCollector.allSimpleProperties())) {
            final Path path = extractPath(p);
            if (path != null) {
                targetPaths.add(path);
            }
        }

        for (FXOMObject fxomObject : rootFxomObject.collect(SceneGraphCollector.sceneGraphObjectByClass(URL.class))) {
            if (fxomObject instanceof FXOMInstance) {
                final FXOMInstance urlInstance = (FXOMInstance) fxomObject;
                final FXOMProperty valueProperty = urlInstance.getProperties().get(valueName);
                if (valueProperty instanceof FXOMPropertyT) {
                    FXOMPropertyT valuePropertyT = (FXOMPropertyT) valueProperty;
                    final Path path = extractPath(valuePropertyT);
                    if (path != null) {
                        targetPaths.add(path);
                    }
                } else {
                    assert false : "valueProperty.getName() = " + valueProperty.getName();
                }
            }
        }

        return targetPaths;
    }


    private static Path extractPath(FXOMPropertyT p) {
        Path result;

        final PrefixedValue pv = new PrefixedValue(p.getValue());
        if (pv.isPlainString()) {
            try {
                final URL url = new URL(pv.getSuffix());
                result = Paths.get(url.toURI());
            } catch(MalformedURLException|URISyntaxException x) {
                result = null;
            }
        } else if (pv.isDocumentRelativePath()) {
            final URL documentLocation = p.getFxomDocument().getLocation();
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
            final ClassLoader classLoader = p.getFxomDocument().getClassLoader();
            if (classLoader == null) {
                result = null;
            } else {
                final URL url = pv.resolveClassLoaderRelativePath(classLoader);
                if (url == null) {
                    result = null;
                } else {
                    try {
                        result = Paths.get(url.toURI());
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
