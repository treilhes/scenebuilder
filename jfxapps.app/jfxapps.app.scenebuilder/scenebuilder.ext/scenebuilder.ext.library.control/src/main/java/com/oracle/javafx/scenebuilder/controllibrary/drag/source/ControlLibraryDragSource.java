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
package com.oracle.javafx.scenebuilder.controllibrary.drag.source;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstanceWindow;
import com.gluonhq.jfxapps.core.api.dnd.DragSourceFactory;
import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.library.LibraryItem;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.AbstractDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DragSourceShadow;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ControlLibraryDragSource extends AbstractDragSource {

    private final FXOMDocument targetDocument;
    private LibraryItem libraryItem;
    private FXOMObject libraryItemObject; // Populated lazily
    private List<FXOMObject> draggedObjects; // Opmization

    protected ControlLibraryDragSource(FxmlDocumentManager documentManager, ApplicationInstanceWindow ownerWindow) {
        super(ownerWindow.getScene().getWindow());
        this.targetDocument = documentManager.fxomDocument().get();
        assert targetDocument != null;
    }

    protected void setDragSourceParameters(LibraryItem libraryItem) {
        assert libraryItem != null;
        this.libraryItem = libraryItem;
    }

    public LibraryItem getLibraryItem() {
        return libraryItem;
    }

    public FXOMObject getLibraryItemObject() {
        if (libraryItemObject == null) {
            // TODO classloader provided by fxmlDocument, good or not?
            final FXOMDocument itemDocument = libraryItem.instantiate(targetDocument.getClassLoader());
            assert itemDocument != null;
            assert itemDocument.getFxomRoot() != null;
            libraryItemObject = itemDocument.getFxomRoot();
            libraryItemObject.moveToFxomDocument(targetDocument);
            assert itemDocument.getFxomRoot() == null;

            if (libraryItemObject.getSceneGraphObject().isInstanceOf(Node.class)) {
                // We put the library item node in a Scene and layout it.
                // This will allow ContainerXYDropTarget to measure this
                // library item by calling Node.getLayoutBounds().
                final Node sceneGraphNode = libraryItemObject.getSceneGraphObject().getAs(Node.class);
                final Group group = new Group();
                group.getChildren().add(sceneGraphNode);
                final Scene scene = new Scene(group); // Not used but required
                scene.getClass(); // used to dummy thing to silence FindBugs
                group.applyCss();
                group.layout();
            }
        }

        return libraryItemObject;
    }

    /*
     * AbstractDragSource
     */

    @Override
    public boolean isAcceptable() {
        // All library drag sources are 'acceptable'
        return true;
    }


    @Override
    public List<FXOMObject> getDraggedObjects() {
        if (draggedObjects == null) {
            draggedObjects = new ArrayList<>();
            draggedObjects.add(getLibraryItemObject());
        }

        return draggedObjects;
    }

    @Override
    public FXOMObject getHitObject() {
        return getDraggedObjects().get(0);
    }

    @Override
    public double getHitX() {
        final double result;

        final FXOMObject hitObject = getHitObject();
        if (hitObject == null) {
            result = Double.NaN;
        } else if (hitObject.isNode()) {
            final Node hitNode = hitObject.getSceneGraphObject().getAs(Node.class);
            final Bounds b = hitNode.getLayoutBounds();
            result = (b.getMinX() + b.getMaxX()) / 2.0;
        } else {
            result = 0.0;
        }

        return result;
    }

    @Override
    public double getHitY() {
        final double result;

        final FXOMObject hitObject = getHitObject();
        if (hitObject == null) {
            result = Double.NaN;
        } else if (hitObject.isNode()) {
            final Node hitNode = hitObject.getSceneGraphObject().getAs(Node.class);
            final Bounds b = hitNode.getLayoutBounds();
            result = (b.getMinY() + b.getMaxY()) / 2.0;
        } else {
            result = 0.0;
        }

        return result;
    }

    @Override
    public ClipboardContent makeClipboardContent() {
        final ClipboardContent result = new ClipboardContent();

        // Add to content a string which is the Lib Item as an FXML string
        result.putString(libraryItem.getFxmlText());

        return result;
    }

    @Override
    public Image makeDragView() {
        // We construct an image made of a Label that reads the class name
        // of the Library Item, and set as Label graphic the appropriate icon.
        URL iconURL = libraryItem.getIconURL();

        if (iconURL == null) {
            iconURL = ImageUtils.getNodeIconURL("MissingIcon.png");
        }

        final Image imageFromIcon = new Image(iconURL.toExternalForm());
//        final Label visualNode = new Label(libraryItem.getName());
        final Label visualNode = new Label();
        visualNode.setGraphic(new ImageView(imageFromIcon));
        visualNode.getStylesheets().add(AbstractDragSource.getStylesheet().toString());
        visualNode.getStyleClass().add("drag-preview"); //NOCHECK

        return ImageUtils.getImageFromNode(visualNode);
    }

    @Override
    public Node makeShadow() {
        final Group result = new Group();

        result.getStylesheets().add(AbstractDragSource.getStylesheet().toString());

        if (getLibraryItemObject().getSceneGraphObject().isInstanceOf(Node.class)) {
            final Node sceneGraphNode = getLibraryItemObject().getSceneGraphObject().get();
            final DragSourceShadow shadowNode = new DragSourceShadow();
            shadowNode.setupForNode(sceneGraphNode);
            result.getChildren().add(shadowNode);
        }

        // Translate the group so that it is centered above (layoutX, layoutY)
        final Bounds b = result.getBoundsInParent();
        final double centerX = (b.getMinX() + b.getMaxX()) / 2.0;
        final double centerY = (b.getMinY() + b.getMaxY()) / 2.0;
        result.setTranslateX(-centerX);
        result.setTranslateY(-centerY);

        return result;
    }

    @Override
    public String makeDropJobDescription() {
        return I18N.getString("drop.job.insert.library.item",
                getLibraryItem().getName());
    }

    @Override
    public boolean isNodeOnly() {
        return getLibraryItemObject().isNode();
    }

    @Override
    public boolean isSingleImageViewOnly() {
        final boolean result;

        if (getLibraryItemObject() instanceof FXOMInstance) {
            result = getLibraryItemObject().getSceneGraphObject().isInstanceOf(ImageView.class);
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public boolean isSingleTooltipOnly() {
        final boolean result;

        if (getLibraryItemObject() instanceof FXOMInstance) {
            result = getLibraryItemObject().getSceneGraphObject().isInstanceOf(Tooltip.class);
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public boolean isSingleContextMenuOnly() {
        final boolean result;

        if (getLibraryItemObject() instanceof FXOMInstance) {
            result = getLibraryItemObject().getSceneGraphObject().isInstanceOf(ContextMenu.class);
        } else {
            result = false;
        }

        return result;
    }



    /*
     * Object
     */

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": libraryItem=(" + libraryItem + ")"; //NOCHECK
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends DragSourceFactory<ControlLibraryDragSource> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public ControlLibraryDragSource getDragSource(LibraryItem libraryItem) {
            return create(ControlLibraryDragSource.class, j -> j.setDragSourceParameters(libraryItem));
        }

    }
}