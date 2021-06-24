/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.ErrorReport;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMAssetIndex;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.metadata.util.PrefixedValue;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ErrorReportImpl implements ErrorReport {

    private final Map<FXOMNode, List<ErrorReportEntry>> entries = new HashMap<>();
    private final Map<Path, CSSParsingReportImpl> cssParsingReports = new HashMap<>();
    private DocumentManager documentManager;
    private boolean dirty = true;
    
    public ErrorReportImpl(
            @Autowired DocumentManager documentManager
            ) {
        super();
        this.documentManager = documentManager;
        this.documentManager.fxomDocument().subscribe(fd -> forget());
    }

    @Override
    public void forget() {
        this.entries.clear();
        this.dirty = true;
    }

    @Override
    public List<ErrorReportEntry> query(FXOMObject fxomObject, boolean recursive) {
        final List<ErrorReportEntry> result;

        updateReport();

        final List<ErrorReportEntry> collected = new ArrayList<>();
        if (recursive) {
            collectEntries(fxomObject, collected);
        } else {
            if (entries.get(fxomObject) != null) {
                collected.addAll(entries.get(fxomObject));
            }
            if (fxomObject instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
                for (FXOMProperty fxomProperty : fxomInstance.getProperties().values()) {
                    if (entries.get(fxomProperty) != null) {
                        collected.addAll(entries.get(fxomProperty));
                    }
                }
            }
        }

        if (collected.isEmpty()) {
            result = null;
        } else {
            result = collected;
        }

        assert (result == null) || (result.size() >= 1);

        return result;
    }

    public Map<FXOMNode, List<ErrorReportEntry>> getEntries() {
        updateReport();
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public void cssFileDidChange(Path cssPath) {
        if (cssParsingReports.containsKey(cssPath)) {
            cssParsingReports.remove(cssPath);
            forget();
        }
    }


    /*
     * Private
     */


    private void updateReport() {
        if (dirty) {
            assert entries.isEmpty();
            if (documentManager.fxomDocument().get() != null) {
                verifyAssets();
                verifyUnresolvedObjects();
                verifyBindingExpressions();
            }
            dirty = false;
        }
    }


    private void verifyAssets() {
        final FXOMAssetIndex assetIndex = new FXOMAssetIndex(documentManager.fxomDocument().get());
        for (Map.Entry<Path, FXOMNode> e : assetIndex.getFileAssets().entrySet()) {
            final Path assetPath = e.getKey();
            if (assetPath.toFile().canRead() == false) {
                final ErrorReportEntry newEntry
                        = new ErrorReportEntryImpl(e.getValue(), ErrorReportEntry.Type.UNRESOLVED_LOCATION);
                addEntry(e.getValue(), newEntry);
            } else {
                final String assetPathName = assetPath.toString();
                if (assetPathName.toLowerCase(Locale.ROOT).endsWith(".css")) { //NOI18N
                    // assetPath is a CSS file : check its parsing report
                    final CSSParsingReportImpl r = getCSSParsingReport(assetPath);
                    assert r != null;
                    if (r.isEmpty() == false) {
                        final ErrorReportEntry newEntry
                                = new ErrorReportEntryImpl(e.getValue(), ErrorReportEntry.Type.INVALID_CSS_CONTENT, r);
                        addEntry(e.getValue(), newEntry);
                    }
                }
            }
        }
    }

    private void verifyUnresolvedObjects() {
        for (FXOMObject fxomObject : FXOMNodes.serializeObjects(documentManager.fxomDocument().get().getFxomRoot())) {
            final Object sceneGraphObject;
            if (fxomObject instanceof FXOMIntrinsic) {
                final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
                sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
            } else {
                sceneGraphObject = fxomObject.getSceneGraphObject();
            }
            if (sceneGraphObject == null) {
                final ErrorReportEntry newEntry
                        = new ErrorReportEntryImpl(fxomObject, ErrorReportEntry.Type.UNRESOLVED_CLASS);
                addEntry(fxomObject, newEntry);
            }
        }
    }

    private void verifyBindingExpressions() {
        for (FXOMPropertyT p : documentManager.fxomDocument().get().getFxomRoot().collectPropertiesT()) {
            final PrefixedValue pv = new PrefixedValue(p.getValue());
            if (pv.isBindingExpression()) {
                final ErrorReportEntry newEntry
                        = new ErrorReportEntryImpl(p, ErrorReportEntry.Type.UNSUPPORTED_EXPRESSION);
                addEntry(p, newEntry);
            }
        }
    }

    private void addEntry(FXOMNode fxomNode, ErrorReportEntry newEntry) {
        List<ErrorReportEntry> nodeEntries = entries.get(fxomNode);
        if (nodeEntries == null) {
            nodeEntries = new ArrayList<> ();
            entries.put(fxomNode, nodeEntries);
        }
        nodeEntries.add(newEntry);
    }

    private CSSParsingReportImpl getCSSParsingReport(Path assetPath) {
        CSSParsingReportImpl result = cssParsingReports.get(assetPath);
        if (result == null) {
            result = new CSSParsingReportImpl(assetPath);
            cssParsingReports.put(assetPath, result);
        }
        return result;
    }

    private void collectEntries(FXOMNode fxomNode, List<ErrorReportEntry> collected) {
        assert fxomNode != null;
        assert collected != null;

        final List<ErrorReportEntry> nodeEntries = entries.get(fxomNode);
        if (nodeEntries != null) {
            collected.addAll(nodeEntries);
        }

        if (fxomNode instanceof FXOMCollection) {
            final FXOMCollection fxomCollection = (FXOMCollection) fxomNode;
            for (FXOMObject item : fxomCollection.getItems()) {
                collectEntries(item, collected);
            }
        } else if (fxomNode instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomNode;
            for (FXOMProperty fxomProperty : fxomInstance.getProperties().values()) {
                collectEntries(fxomProperty, collected);
            }
        } else if (fxomNode instanceof FXOMPropertyC) {
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomNode;
            for (FXOMObject value : fxomPropertyC.getValues()) {
                collectEntries(value, collected);
            }
        }
    }
}
