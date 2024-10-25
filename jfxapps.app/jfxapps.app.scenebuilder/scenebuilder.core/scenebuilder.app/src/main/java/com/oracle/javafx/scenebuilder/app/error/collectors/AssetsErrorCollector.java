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
package com.oracle.javafx.scenebuilder.app.error.collectors;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.error.AbstractErrorCollector;
import com.gluonhq.jfxapps.core.api.error.ErrorReportEntry;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.FileSystem.WatchingCallback;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.fxom.FXOMAssetIndex;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.app.error.CSSParsingReportImpl;
import com.oracle.javafx.scenebuilder.app.error.FxmlErrorReportEntryImpl;
import com.oracle.javafx.scenebuilder.app.error.Type;

@ApplicationInstanceSingleton
public class AssetsErrorCollector extends AbstractErrorCollector implements WatchingCallback {

    private final I18N i18n;
    private final SbFXOMObjectMask.Factory designHierarchyMaskFactory;
    private final ApplicationInstanceEvents documentManager;
    private final Map<Path, CSSParsingReportImpl> cssParsingReports = new HashMap<>();
    private final FileSystem fileSystem;
    private final MainInstanceWindow documentWindow;

    public AssetsErrorCollector(
            I18N i18n,
            FileSystem fileSystem,
            MainInstanceWindow documentWindow,
            ApplicationInstanceEvents documentManager,
            SbFXOMObjectMask.Factory designHierarchyMaskFactory) {
        super();
        this.i18n = i18n;
        this.fileSystem = fileSystem;
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
    }

    @Override
    public ErrorCollectorResult collect() {

        final ErrorCollectorResult result = new ErrorCollectorResult();

        final FXOMAssetIndex assetIndex = new FXOMAssetIndex(documentManager.fxomDocument().get());
        for (Map.Entry<Path, FXOMNode> e : assetIndex.getFileAssets().entrySet()) {
            final Path assetPath = e.getKey();
            if (assetPath.toFile().canRead() == false) {
                final ErrorReportEntry newEntry = new FxmlErrorReportEntryImpl(i18n, e.getValue(), Type.UNRESOLVED_LOCATION, designHierarchyMaskFactory);
                result.add(e.getValue(), newEntry);
            } else {
                final String assetPathName = assetPath.toString();
                if (assetPathName.toLowerCase(Locale.ROOT).endsWith(".css")) { // NOCHECK
                    // assetPath is a CSS file : check its parsing report
                    final CSSParsingReportImpl r = getCSSParsingReport(assetPath);

                    fileSystem.watch(documentWindow, Set.of(assetPath), this);

                    assert r != null;
                    if (r.isEmpty() == false) {
                        final ErrorReportEntry newEntry = new FxmlErrorReportEntryImpl(i18n, e.getValue(),
                                Type.INVALID_CSS_CONTENT, r, designHierarchyMaskFactory);
                        result.add(e.getValue(), newEntry);
                    }
                }
            }
        }
        return result;
    }


    private CSSParsingReportImpl getCSSParsingReport(Path assetPath) {
        CSSParsingReportImpl result = cssParsingReports.get(assetPath);
        if (result == null) {
            result = new CSSParsingReportImpl(assetPath);
            cssParsingReports.put(assetPath, result);
        }
        return result;
    }

    @Override
    public Object getOwnerKey() {
        return this;
    }

    @Override
    public void created(Path path) {
        handlePathUpdated(path);
    }

    @Override
    public void deleted(Path path) {
        handlePathUpdated(path);
    }

    @Override
    public void modified(Path path) {
        handlePathUpdated(path);
    }

    private void handlePathUpdated(Path path) {
        if (cssParsingReports.containsKey(path)) {
            cssParsingReports.remove(path);
            notifyReportIsDirty();
        }
    }
}