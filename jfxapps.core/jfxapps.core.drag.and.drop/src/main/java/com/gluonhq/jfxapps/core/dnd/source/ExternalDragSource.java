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
package com.gluonhq.jfxapps.core.dnd.source;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardDataFormat;
import com.gluonhq.jfxapps.core.api.dnd.AbstractDragSource;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.util.ClassUtils;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 *
 */
@Prototype
public final class ExternalDragSource extends AbstractDragSource {

    private final I18N i18n;
    private Dragboard dragboard;
    private FXOMDocument targetDocument;
    private List<FXOMObject> draggedObjects; // Initialized lazily
    private List<File> inputFiles; // Initialized lazily
//    private boolean nodeOnly; // Iniitalized lazily
//    private boolean singleImageViewOnly; // Initiated lazily
//    private boolean singleTooltipOnly; // Initiated lazily
//    private boolean singleContextMenuOnly; // Initiated lazily

    private boolean empty; // Initiated lazily
    private boolean single; // Initiated lazily
    private Class<?> singleType; // Initiated lazily

    private int errorCount;
    private Exception lastException;
    private Optional<List<ClipboardDataFormat>> dataFormats;

    protected ExternalDragSource(
            I18N i18n,
            ApplicationInstanceEvents documentManager,
            MainInstanceWindow ownerWindow,
            Optional<List<ClipboardDataFormat>> dataFormats) {
        super(ownerWindow.getScene().getWindow());
        this.i18n = i18n;
        this.targetDocument = documentManager.fxomDocument().get();
        this.dataFormats = dataFormats;
        assert targetDocument != null;
    }

    public void setDragSourceParameters(Dragboard dragboard) {
        assert dragboard != null;
        this.dragboard = dragboard;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public Exception getLastException() {
        return lastException;
    }


    /*
     * AbstractDragSource
     */

    @Override
    public boolean isAcceptable() {
        // All external drag sources are 'acceptable'
        return true;
    }


    @Override
    public List<? extends FXOMObject> getDraggedObjects() {

        AtomicInteger errorCount = new AtomicInteger();
        AtomicReference<Exception> lastException = new AtomicReference<>();

        List<? extends FXOMObject> draggedObjectsX = dataFormats.orElse(Collections.emptyList()).stream()
            .filter(cpf -> cpf.hasDecodableContent(dragboard))
            .map(cpf -> cpf.quietDecode(dragboard, e -> {
                errorCount.incrementAndGet();
                lastException.set(e);
            }))
            .flatMap(List::stream)
            .collect(Collectors.toList());

        if (draggedObjects == null) {
            draggedObjects = dataFormats.orElse(Collections.emptyList()).stream()
                .filter(cpf -> cpf.hasDecodableContent(dragboard))
                .map(cpf -> cpf.quietDecode(dragboard, e -> {
                    errorCount.incrementAndGet();
                    lastException.set(e);
                }))
                .flatMap(List::stream)
                .collect(Collectors.toList());
            inputFiles = new ArrayList<>();
            inputFiles.addAll(dragboard.getFiles());

            // We put all the Node dragged objects in a Scene and layout them
            // so that ContainerXYDropTarget can measure them.
            // We stack and shift them a little so that they are all visible.
            final Group group = new Group();
            double dxy = 0.0;
            for (FXOMObject o : draggedObjects) {
                if (o.getSceneGraphObject().isNode()) {
                    final Node sceneGraphNode = o.getSceneGraphObject().getAs(Node.class);
                    sceneGraphNode.setLayoutX(dxy);
                    sceneGraphNode.setLayoutY(dxy);
                    dxy += 20.0;

                    group.getChildren().add(sceneGraphNode);
                }
            }
            final Scene scene = new Scene(group); // Unused but required
            scene.getClass(); // used to dummy thing to silence FindBugs
            group.applyCss();
            group.layout();

            // Initialize
            if (draggedObjects.isEmpty()) {
                empty = true;
            } else if (draggedObjects.size() == 1) {
                empty = false;
                single = true;
            }

            singleType = ClassUtils.findSharedBaseClass(draggedObjects, o -> o.getSceneGraphObject().getObjectClass());

        }

        return draggedObjects;
    }


    @Override
    public FXOMObject getHitObject() {
        final FXOMObject result;

        if (getDraggedObjects().isEmpty()) {
            result = null;
        } else {
            result = getDraggedObjects().iterator().next();
        }

        return result;
    }

    @Override
    public double getHitX() {
        final double result;

        final FXOMObject hitObject = getHitObject();
        if (hitObject == null) {
            result = Double.NaN;
        } else if (hitObject.getSceneGraphObject().isNode()) {
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
        } else if (hitObject.getSceneGraphObject().isNode()) {
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
        throw new UnsupportedOperationException("should not be called"); //NOCHECK
    }

    @Override
    public Image makeDragView() {
        throw new UnsupportedOperationException("should not be called"); //NOCHECK
    }

    @Override
    public Node makeShadow() {
        final Group result = new Group();

        result.getStylesheets().add(AbstractDragSource.getStylesheet().toString());

        for (FXOMObject draggedObject : getDraggedObjects()) {
            if (draggedObject.getSceneGraphObject().isNode()) {
                final Node sceneGraphNode = draggedObject.getSceneGraphObject().getAs(Node.class);
                final DragSourceShadow shadowNode = new DragSourceShadow();
                shadowNode.setupForNode(sceneGraphNode);
                shadowNode.getTransforms().add(sceneGraphNode.getLocalToSceneTransform());
                result.getChildren().add(shadowNode);
            }
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
        final String result;

        if (inputFiles.size() == 1) {
            final Path inputPath = Paths.get(inputFiles.get(0).toURI());
            result = i18n.getString("drop.job.insert.from.single.file",
                    inputPath.getFileName());
        } else {
            result = i18n.getString("drop.job.insert.from.multiple.files",
                    inputFiles.size());
        }

        return result;
    }

//    @Override
//    public boolean isNodeOnly() {
//        return isSingleType(Node.class);
//    }

    @Override
    public boolean isSingle() {
        getDraggedObjects();
        return single;
    }

    @Override
    public boolean isEmpty() {
        getDraggedObjects();
        return empty;
    }

    @Override
    public boolean isSingleType() {
        getDraggedObjects();
        return singleType != null;
    }

    @Override
    public boolean isSingleType(Class<?> type) {
        getDraggedObjects();
        return singleType == type;
    }

//    @Override
//    public boolean isSingleImageViewOnly() {
//        // singleImageViewOnly is initialized lazily by getDraggedObjects()
//        getDraggedObjects();
//        return singleImageViewOnly;
//    }
//
//    @Override
//    public boolean isSingleTooltipOnly() {
//        // singleTooltipOnly is initialized lazily by getDraggedObjects()
//        getDraggedObjects();
//        return singleTooltipOnly;
//    }
//
//    @Override
//    public boolean isSingleContextMenuOnly() {
//        // singleContextMenuOnly is initialized lazily by getDraggedObjects()
//        getDraggedObjects();
//        return singleContextMenuOnly;
//    }

    /*
     * Object
     */

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": dragboard=(" + dragboard + ")"; //NOCHECK
    }


//    /*
//     * Private
//     */
//
//    /*
//     * Utilities that should probably go somewhere else.
//     */
//
//    static FXOMDocument makeFxomDocumentFromImageURL(Image image,
//            double fitSize) throws IOException {
//
//        assert image != null;
//        assert fitSize > 0.0;
//
//        final double imageWidth = image.getWidth();
//        final double imageHeight = image.getHeight();
//
//        final double fitWidth, fitHeight;
//        final double imageSize = Math.max(imageWidth, imageHeight);
//        if (imageSize < fitSize) {
//            fitWidth = 0;
//            fitHeight = 0;
//        } else {
//            final double widthScale  = fitSize / imageSize;
//            final double heightScale = fitSize / imageHeight;
//            final double scale = Math.min(widthScale, heightScale);
//            fitWidth = Math.floor(imageWidth * scale);
//            fitHeight = Math.floor(imageHeight * scale);
//        }
//
//        return makeFxomDocumentFromImageURL(image, fitWidth, fitHeight);
//    }
//
//    static final PropertyName imageName = new PropertyName("image"); //NOCHECK
//    static final PropertyName fitWidthName = new PropertyName("fitWidth"); //NOCHECK
//    static final PropertyName fitHeightName = new PropertyName("fitHeight"); //NOCHECK
//
//    static FXOMDocument makeFxomDocumentFromImageURL(Image image, double fitWidth, double fitHeight) {
//        final FXOMDocument result = new FXOMDocument();
//        final FXOMInstance imageView = new FXOMInstance(result, ImageView.class);
//
//        final ImagePropertyMetadata imageMeta = ImageViewMetadata.imagePropertyMetadata;
//        final DoublePropertyMetadata fitWidthMeta = ImageViewMetadata.fitWidthPropertyMetadata;
//        final DoublePropertyMetadata fitHeightMeta = ImageViewMetadata.fitHeightPropertyMetadata;
//
//        imageMeta.setValue(imageView, new DesignImage(image));
//        fitWidthMeta.setValue(imageView, fitWidth);
//        fitHeightMeta.setValue(imageView, fitHeight);
//
//        result.setFxomRoot(imageView);
//
//        return result;
//    }



    @Override
    public TransferMode getTransferMode() {
        return TransferMode.COPY;
    }

}
