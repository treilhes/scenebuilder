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
package org.scenebuilder.fxml.api.error;

public class ErrorCollectors {

//    private void verifyAssets() {
//        final FXOMAssetIndex assetIndex = new FXOMAssetIndex(documentManager.fxomDocument().get());
//        for (Map.Entry<Path, FXOMNode> e : assetIndex.getFileAssets().entrySet()) {
//            final Path assetPath = e.getKey();
//            if (assetPath.toFile().canRead() == false) {
//                final ErrorReportEntry newEntry = new ErrorReportEntryImpl(e.getValue(),
//                        ErrorReportEntry.Type.UNRESOLVED_LOCATION);
//                addEntry(e.getValue(), newEntry);
//            } else {
//                final String assetPathName = assetPath.toString();
//                if (assetPathName.toLowerCase(Locale.ROOT).endsWith(".css")) { // NOCHECK
//                    // assetPath is a CSS file : check its parsing report
//                    final CSSParsingReportImpl r = getCSSParsingReport(assetPath);
//                    assert r != null;
//                    if (r.isEmpty() == false) {
//                        final ErrorReportEntry newEntry = new ErrorReportEntryImpl(e.getValue(),
//                                ErrorReportEntry.Type.INVALID_CSS_CONTENT, r);
//                        addEntry(e.getValue(), newEntry);
//                    }
//                }
//            }
//        }
//    }
//
//    private void verifyUnresolvedObjects() {
//        for (FXOMObject fxomObject : FXOMNodes.serializeObjects(documentManager.fxomDocument().get().getFxomRoot())) {
//
//            if (fxomObject.isVirtual()) {
//                continue;
//            }
//
//            final Object sceneGraphObject;
//            if (fxomObject instanceof FXOMIntrinsic) {
//                final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
//                sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
//                if (!(fxomObject instanceof FXOMInclude)) {
//                    String reference = fxomIntrinsic.getSource();
//                    final FXOMObject referee = fxomIntrinsic.getFxomDocument().searchWithFxId(reference);
//
//                    if (referee == null) {
//                        final ErrorReportEntry newEntry = new ErrorReportEntryImpl(fxomObject,
//                                ErrorReportEntry.Type.UNRESOLVED_REFERENCE);
//                        addEntry(fxomObject, newEntry);
//                    }
//
//                }
//
//            } else {
//                sceneGraphObject = fxomObject.getSceneGraphObject().get();
//            }
//            if (!fxomObject.isVirtual() && sceneGraphObject == null) {
//                final ErrorReportEntry newEntry = new ErrorReportEntryImpl(fxomObject,
//                        ErrorReportEntry.Type.UNRESOLVED_CLASS);
//                addEntry(fxomObject, newEntry);
//            }
//        }
//    }
//
//    private void verifyBindingExpressions() {
//        for (FXOMPropertyT p : documentManager.fxomDocument().get().getFxomRoot().collectPropertiesT()) {
//            final PrefixedValue pv = new PrefixedValue(p.getValue());
//            if (pv.isBindingExpression()) {
//                final ErrorReportEntry newEntry = new ErrorReportEntryImpl(p,
//                        ErrorReportEntry.Type.UNSUPPORTED_EXPRESSION);
//                addEntry(p, newEntry);
//            }
//        }
//    }
//
//    private void addEntry(FXOMNode fxomNode, ErrorReportEntry newEntry) {
//        List<ErrorReportEntry> nodeEntries = entries.get(fxomNode);
//        if (nodeEntries == null) {
//            nodeEntries = new ArrayList<>();
//            entries.put(fxomNode, nodeEntries);
//        }
//        nodeEntries.add(newEntry);
//    }
}
