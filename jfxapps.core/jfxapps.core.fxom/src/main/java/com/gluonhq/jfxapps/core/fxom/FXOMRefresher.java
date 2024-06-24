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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * This object will refresh an FXOMDocument.
 * Refreshing means generating an fxml representation of an FXOMDocument A,
 * then creating a new FXOMDOcument B from the generated fxml
 * and then updating the original FXOMDocument A content with the objects
 * contained in the FXOMDocument B<br/>
 * Take a look at {@link com.gluonhq.jfxapps.core.fxom.ext.FXOMRefresher} to provide
 * a custom extension to this process using the {@link ServiceLoader} mechanism
 */
class FXOMRefresher {

    private static ServiceLoader<com.gluonhq.jfxapps.core.fxom.ext.FXOMRefresher> extensions;

    static {
        extensions = ServiceLoader.load(com.gluonhq.jfxapps.core.fxom.ext.FXOMRefresher.class);
    }

    public void refresh(FXOMDocument document) {
        String fxmlText = null;
        try {
            fxmlText = document.getFxmlText(false);
            final FXOMDocument newDocument
                    = new FXOMDocument(fxmlText,
                    document.getLocation(),
                    document.getClassLoader(),
                    document.getResources(),
                    false /* normalized */);
            final TransientStateBackup backup = new TransientStateBackup(document);
            // if the refresh should not take place (e.g. due to an error), remove a property from intrinsic
            if (newDocument.getSceneGraphRoot() == null && newDocument.getFxomRoot() == null) {
                removeIntrinsicProperty(document);
            } else {
                refreshDocument(document, newDocument);
            }
            backup.restore();

            //synchronizeDividerPositions(document);
            extensions.forEach(e -> e.refresh(document));

        } catch (RuntimeException | IOException x) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Bug in ");
            sb.append(getClass().getSimpleName());
            if (fxmlText != null) {
                try {
                    final File fxmlFile = File.createTempFile("DTL-5996-", ".fxml");
                    try (PrintWriter pw = new PrintWriter(fxmlFile, "UTF-8")) {
                        pw.write(fxmlText);
                        sb.append(": FXML dumped in ");
                        sb.append(fxmlFile.getPath());
                    }
                } catch (IOException xx) {
                    sb.append(": no FXML dumped");
                }
            } else {
                sb.append(": no FXML dumped");
            }
            throw new IllegalStateException(sb.toString(), x);
        }
    }

    private void removeIntrinsicProperty(FXOMDocument document) {
        FXOMInstance fxomRoot = (FXOMInstance) document.getFxomRoot();
        if (fxomRoot != null) {
            FXOMPropertyC propertyC = (FXOMPropertyC) fxomRoot.getProperties().get(new PropertyName("children"));
            if (propertyC.getChildren().get(0) instanceof FXOMIntrinsic) {
                FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) propertyC.getChildren().get(0);
                fxomIntrinsic.removeCharsetProperty();
            }
        }
    }

    /*
     * Private (stylesheet)
     */

    private void refreshDocument(FXOMDocument currentDocument, FXOMDocument newDocument) {
        // Transfers scene graph object from newDocument to currentDocument
        currentDocument.setSceneGraphRoot(newDocument.getSceneGraphRoot());
        // Transfers display node from newDocument to currentDocument
        currentDocument.setDisplayNode(newDocument.getDisplayNode());
        // Transfers display stylesheets from newDocument to currentDocument
        currentDocument.setDisplayStylesheets(newDocument.getDisplayStylesheets());
        // Simulates Scene's behavior : automatically adds "root" styleclass if
        // if the scene graph root is a Parent instance or wraps a Parent instance
        if (currentDocument.getSceneGraphRoot() instanceof Parent) {
            final Parent rootParent = (Parent) currentDocument.getSceneGraphRoot();
            rootParent.getStyleClass().add(0, "root");
        } else if (currentDocument.getSceneGraphRoot() instanceof Scene
                || currentDocument.getSceneGraphRoot() instanceof Window) {
            Node displayNode = currentDocument.getDisplayNode();
            if (displayNode != null && displayNode instanceof Parent) {
                displayNode.getStyleClass().add(0, "root");
            }
        }
        // Recurses
        if (currentDocument.getFxomRoot() != null) {
            refreshFxomObject(currentDocument.getFxomRoot(), newDocument.getFxomRoot());
        }
    }


    private void refreshFxomObject(FXOMObject currentObject, FXOMObject newObject) {
        assert currentObject != null;
        assert newObject != null;
        assert currentObject.getClass() == newObject.getClass();
        currentObject.setSceneGraphObject(newObject.getSceneGraphObject().get());
        if (currentObject instanceof FXOMInstance) {
            refreshFxomInstance((FXOMInstance) currentObject, (FXOMInstance) newObject);
        } else if (currentObject instanceof FXOMCollection) {
            refreshFxomCollection((FXOMCollection) currentObject, (FXOMCollection) newObject);
        } else if (currentObject instanceof FXOMIntrinsic) {
            refreshFxomIntrinsic((FXOMIntrinsic) currentObject, (FXOMIntrinsic) newObject);
        } else if (currentObject instanceof FXOMDefine) {
            refreshFxomDefine((FXOMDefine) currentObject, (FXOMDefine) newObject);
        } else if (currentObject instanceof FXOMScript) {
            refreshFxomScript((FXOMScript) currentObject, (FXOMScript) newObject);
        } else if (currentObject instanceof FXOMComment) {
            refreshFxomComment((FXOMComment) currentObject, (FXOMComment) newObject);
        } else {
            assert false : "Unexpected fxom object " + currentObject;
        }

//        assert currentObject.equals(newObject) : "currentValue=" + currentObject +
//                                               "  newValue=" + newObject;
    }


    private void refreshFxomInstance(FXOMInstance currentInstance, FXOMInstance newInstance) {
        assert currentInstance != null;
        assert newInstance != null;
        assert currentInstance.getClass() == newInstance.getClass();
        currentInstance.setDeclaredClass(newInstance.getDeclaredClass());
        final Set<PropertyName> currentNames = currentInstance.getProperties().keySet();
        final Set<PropertyName> newNames = newInstance.getProperties().keySet();
        assert currentNames.equals(newNames);
        for (PropertyName name : currentNames) {
            final FXOMProperty currentProperty = currentInstance.getProperties().get(name);
            final FXOMProperty newProperty = newInstance.getProperties().get(name);
            refreshFxomProperty(currentProperty, newProperty);
        }
    }

    private void refreshFxomCollection(FXOMCollection currentCollection, FXOMCollection newCollection) {
        assert currentCollection != null;
        assert newCollection != null;
        currentCollection.setDeclaredClass(newCollection.getDeclaredClass());
        refreshFxomObjects(currentCollection.getItems(), newCollection.getItems());
    }

    private void refreshFxomIntrinsic(FXOMIntrinsic currentIntrinsic, FXOMIntrinsic newIntrinsic) {
        assert currentIntrinsic != null;
        assert newIntrinsic != null;
        if (newIntrinsic.getSceneGraphObject().isFromExternalSource()) {
            currentIntrinsic.setSourceSceneGraphObject(newIntrinsic.getSceneGraphObject().get());
        } else {
            currentIntrinsic.setSceneGraphObject(newIntrinsic.getSceneGraphObject().get());
        }
        currentIntrinsic.getProperties().clear();
        currentIntrinsic.fillProperties(newIntrinsic.getProperties());
    }

    private void refreshFxomComment(FXOMComment currentComment, FXOMComment newComment) {
        assert currentComment != null;
        assert newComment != null;
        currentComment.setComment(newComment.getComment());
    }

    private void refreshFxomScript(FXOMScript currentScript, FXOMScript newScript) {
        assert currentScript != null;
        assert newScript != null;
        currentScript.setScript(newScript.getScript());
    }

    private void refreshFxomDefine(FXOMDefine currentDefine, FXOMDefine newDefine) {
        assert currentDefine != null;
        assert newDefine != null;
        refreshFxomObjects(currentDefine.getItems(), newDefine.getItems());
    }



    private void refreshFxomProperty(FXOMProperty currentProperty, FXOMProperty newProperty) {
        assert currentProperty != null;
        assert newProperty != null;
        assert currentProperty.getName().equals(newProperty.getName());
        if (currentProperty instanceof FXOMPropertyT) {
            assert newProperty instanceof FXOMPropertyT;
            assert ((FXOMPropertyT) currentProperty).getValue().equals(((FXOMPropertyT) newProperty).getValue());
        }
        refreshFxomObjects(currentProperty.getChildren(), newProperty.getChildren());

    }


    private void refreshFxomObjects(List<FXOMObject> currentObjects, List<FXOMObject> newObjects) {
        assert currentObjects != null;
        assert newObjects != null;
        assert currentObjects.size() == newObjects.size();
        for (int i = 0, count = currentObjects.size(); i < count; i++) {
            final FXOMObject currentObject = currentObjects.get(i);
            final FXOMObject newObject = newObjects.get(i);
            if (currentObject instanceof FXOMIntrinsic || newObject instanceof FXOMIntrinsic) {
                handleRefreshIntrinsic(currentObject, newObject);
            } else {
                refreshFxomObject(currentObject, newObject);
            }
        }
    }

    private void handleRefreshIntrinsic(FXOMObject currentObject, FXOMObject newObject) {
//        if (currentObject instanceof FXOMIntrinsic && newObject instanceof FXOMIntrinsic) {
//            refreshFxomObject(currentObject, newObject);
//        } else if (newObject instanceof FXOMIntrinsic) {
//            FXOMInstance fxomInstance = ((FXOMIntrinsic) newObject).createFxomInstanceFromIntrinsic();
//            refreshFxomObject(currentObject, fxomInstance);
//        } else if (currentObject instanceof FXOMIntrinsic) {
//            FXOMInstance fxomInstance = ((FXOMIntrinsic) currentObject).createFxomInstanceFromIntrinsic();
//            refreshFxomObject(fxomInstance, newObject);
//        }
        refreshFxomObject(currentObject, newObject);
    }

//    /*
//     * The case of SplitPane.dividerPositions property
//     * -----------------------------------------------
//     *
//     * When user adds a child to a SplitPane, this adds a new entry in
//     * SplitPane.children property but also adds a new value to
//     * SplitPane.dividerPositions by side-effect.
//     *
//     * The change in SplitPane.dividerPositions is performed at scene graph
//     * level by FX. Thus it is unseen by FXOM.
//     *
//     * So in that case we perform a special operation which copies value of
//     * SplitPane.dividerPositions into FXOMProperty representing
//     * dividerPositions in FXOM.
//     */
//
//    private void synchronizeDividerPositions(FXOMDocument document) {
//        final FXOMObject fxomRoot = document.getFxomRoot();
//        if (fxomRoot != null) {
//            final Metadata metadata
//                    = Api.get().getMetadata();
//            final PropertyName dividerPositionsName
//                    = new PropertyName("dividerPositions");
//            final List<FXOMObject> candidates
//                    = fxomRoot.collectObjectWithSceneGraphObjectClass(SplitPane.class);
//
//            for (FXOMObject fxomObject : candidates) {
//                if (fxomObject instanceof FXOMInstance) {
//                    final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
//                    assert fxomInstance.getSceneGraphObject().isInstanceOf(SplitPane.class);
//                    final SplitPane splitPane
//                            = fxomInstance.getSceneGraphObject().getAs(SplitPane.class);
//                    splitPane.layout();
//                    final ValuePropertyMetadata vpm
//                            = metadata.queryValueProperty(fxomInstance, dividerPositionsName);
//                    assert vpm instanceof ListValuePropertyMetadata
//                            : "vpm.getClass()=" + vpm.getClass().getSimpleName();
//                    final DoubleArrayPropertyMetadata davpm
//                            = (DoubleArrayPropertyMetadata) vpm;
//                    davpm.synchronizeWithSceneGraphObject(fxomInstance);
//                }
//            }
//        }
//    }


//
//
//    private void reloadStylesheets(final Parent p) {
//        assert p != null;
//        assert p.getScene() != null;
//
//        if (p.getStylesheets().isEmpty() == false) {
//            final List<String> stylesheets = new ArrayList<>();
//            stylesheets.addAll(p.getStylesheets());
////            p.getStylesheets().clear();
////            p.impl_processCSS(true);
//            p.getStylesheets().setAll(stylesheets);
////            p.impl_processCSS(true);
//        }
//        for (Node child : p.getChildrenUnmodifiable()) {
//            if (child instanceof Parent) {
//                reloadStylesheets((Parent)child);
//            }
//        }
//
//    }
}
