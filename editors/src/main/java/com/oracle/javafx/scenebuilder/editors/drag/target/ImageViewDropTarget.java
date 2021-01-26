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

package com.oracle.javafx.scenebuilder.editors.drag.target;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignImage;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.BackupSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.UpdateSelectionJob;

import javafx.scene.image.ImageView;

/**
 *
 */
public class ImageViewDropTarget extends AbstractDropTarget {

    private final FXOMInstance targetImageView;

    public ImageViewDropTarget(FXOMObject targetImageView) {
        assert targetImageView instanceof FXOMInstance;
        assert targetImageView.getSceneGraphObject() instanceof ImageView;

        this.targetImageView = (FXOMInstance) targetImageView;
    }


    /*
     * ImageViewDropTarget
     */

    @Override
    public FXOMObject getTargetObject() {
        return targetImageView;
    }

    @Override
    public boolean acceptDragSource(DragSource dragSource) {
        assert dragSource != null;
        return dragSource.isSingleImageViewOnly();
    }

    @Override
    public Job makeDropJob(ApplicationContext context, DragSource dragSource, Editor editorController) {

        assert dragSource != null;
        assert dragSource.isSingleImageViewOnly(); // (1)

        final FXOMObject draggedObject
                = dragSource.getDraggedObjects().get(0);
        assert draggedObject instanceof FXOMInstance; // because (1)
        final FXOMInstance draggedInstance
                = (FXOMInstance) draggedObject;
        final PropertyName imageName
                = new PropertyName("image"); //NOI18N
        final ValuePropertyMetadata vpm
                = Metadata.getMetadata().queryValueProperty(draggedInstance, imageName);
        assert vpm instanceof ImagePropertyMetadata;
        final ImagePropertyMetadata imageVPM
                = (ImagePropertyMetadata) vpm;
        final DesignImage image
                = imageVPM.getValue(draggedInstance);

        final BatchJob result = new BatchJob(context, editorController);
        result.addSubJob(new BackupSelectionJob(context, editorController).extend());
        result.addSubJob(new ModifyObjectJob(context, targetImageView, imageVPM, image, editorController).extend());
        result.addSubJob(new UpdateSelectionJob(context, targetImageView, editorController).extend());

        return result.extend();
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        /*
         * Unlike for other drop targets, AbstractDragSource.draggedObjects
         * should not be selected after drop operation.
         * It's targetImageView that must be selected.
         * AbstractDragSource.draggedObjects() are not inserted in the scene
         * graph.
         */
        return false;
    }

}
