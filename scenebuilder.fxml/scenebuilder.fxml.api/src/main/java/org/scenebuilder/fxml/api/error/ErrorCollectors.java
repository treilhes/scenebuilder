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
//                sceneGraphObject = fxomObject.getSceneGraphObject();
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
