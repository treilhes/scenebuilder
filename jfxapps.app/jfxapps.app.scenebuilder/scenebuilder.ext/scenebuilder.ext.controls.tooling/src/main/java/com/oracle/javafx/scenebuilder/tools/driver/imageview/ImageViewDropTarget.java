/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.tools.driver.imageview;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.control.droptarget.AbstractDropTarget;
import com.gluonhq.jfxapps.core.api.control.droptarget.DropTargetFactory;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.DesignImage;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.job.editor.BatchJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.fxml.selection.job.UpdateSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.BackupSelectionJob;

import javafx.scene.image.ImageView;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ImageViewDropTarget extends AbstractDropTarget {

    private final IMetadata metadata;
    private final BatchJob.Factory batchJobFactory;
    private final BackupSelectionJob.Factory backupSelectionJobFactory;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final UpdateSelectionJob.Factory updateSelectionJobFactory;

    private FXOMInstance targetImageView;

    // @formatter:off
    protected ImageViewDropTarget(
            IMetadata metadata,
            BatchJob.Factory batchJobFactory,
            BackupSelectionJob.Factory backupSelectionJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            UpdateSelectionJob.Factory updateSelectionJobFactory) {
     // @formatter:on
        this.metadata = metadata;
        this.batchJobFactory = batchJobFactory;
        this.backupSelectionJobFactory = backupSelectionJobFactory;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
        this.updateSelectionJobFactory = updateSelectionJobFactory;
    }

    protected void setDropTargetParameters(FXOMObject targetImageView) {
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
        return dragSource.isSingleImageViewOnly() && dragSource instanceof ExternalDragSource;
    }

    @Override
    public AbstractJob makeDropJob(DragSource dragSource) {

        assert dragSource != null;
        assert dragSource.isSingleImageViewOnly(); // (1)

        final FXOMObject draggedObject = dragSource.getDraggedObjects().get(0);
        assert draggedObject instanceof FXOMInstance; // because (1)
        final FXOMInstance draggedInstance = (FXOMInstance) draggedObject;
        final PropertyName imageName = new PropertyName("image"); // NOCHECK
        final ValuePropertyMetadata vpm = metadata.queryValueProperty(draggedInstance, imageName);
        assert vpm instanceof ImagePropertyMetadata;
        final ImagePropertyMetadata imageVPM = (ImagePropertyMetadata) vpm;
        final DesignImage image = imageVPM.getValue(draggedInstance);

        final BatchJob result = batchJobFactory.getJob();
        result.addSubJob(backupSelectionJobFactory.getJob());
        result.addSubJob(modifyObjectJobFactory.getJob(targetImageView, imageVPM, image));
        result.addSubJob(updateSelectionJobFactory.getJob(targetImageView));

        return result;
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        /*
         * Unlike for other drop targets, AbstractDragSource.draggedObjects should not
         * be selected after drop operation. It's targetImageView that must be selected.
         * AbstractDragSource.draggedObjects() are not inserted in the scene graph.
         */
        return false;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends DropTargetFactory<ImageViewDropTarget> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public ImageViewDropTarget getDropTarget(FXOMObject targetImageView) {
            return create(ImageViewDropTarget.class, j -> j.setDropTargetParameters(targetImageView));
        }
    }
}
