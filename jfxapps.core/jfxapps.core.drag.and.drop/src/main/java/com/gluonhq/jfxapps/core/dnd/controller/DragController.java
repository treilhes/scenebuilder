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
package com.gluonhq.jfxapps.core.dnd.controller;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.di.SbPlatform;
import com.gluonhq.jfxapps.core.api.dnd.Drag;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.dnd.DropTarget;
import com.gluonhq.jfxapps.core.api.editor.selection.DefaultSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPath;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.TransferMode;

/**
 *
 */
@ApplicationInstanceSingleton
public class DragController implements Drag {

    private static final Logger logger = LoggerFactory.getLogger(DragController.class);

    private final JobManager jobManager;
    private final DefaultSelectionGroupFactory defaultSelectionGroupFactory;
    private final SelectionJobsFactory selectionJobFactory;
    private final BatchJob.Factory batchJobFactory;
    private final Selection selection;
    private final DocumentManager documentManager;

    private final ObjectProperty<DragSource> dragSourceProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<DropTarget> dropTargetProperty = new SimpleObjectProperty<>(null);

    private LiveUpdater liveUpdater;
    private Job backupSelectionJob;
    private boolean liveUpdateEnabled;
    private boolean dropAccepted;
    private DropTarget committedDropTarget;
    private Timer mouseTimer;



    // @formatter:off
    public DragController(
            Selection selection,
            JobManager jobManager,
            DocumentManager documentManager,
            DefaultSelectionGroupFactory defaultSelectionGroupFactory,
            SelectionJobsFactory selectionJobFactory,
            BatchJob.Factory batchJobFactory) {
     // @formatter:on
        this.selection = selection;
        this.jobManager = jobManager;
        this.documentManager = documentManager;
        this.defaultSelectionGroupFactory = defaultSelectionGroupFactory;
        this.selectionJobFactory = selectionJobFactory;
        this.batchJobFactory = batchJobFactory;
    }

    @Override
    public void begin(DragSource dragSource) {
        assert dragSource != null;
        assert dragSource.isAcceptable();
        assert getDragSource() == null;
        assert getDropTarget() == null;
        assert liveUpdater == null;
        assert backupSelectionJob == null;
        assert dropAccepted == false;
        assert committedDropTarget == null;
        assert mouseTimer == null;

        liveUpdater = new LiveUpdater(dragSource);
        dragSourceProperty.set(dragSource);
        dropTargetProperty.set(null);

        // Backup and clear the selection
        backupSelectionJob = selectionJobFactory.backupSelection();
        selection.clear();

        logger.info("Drop session started for {} objects", dragSource.getDraggedObjects().size());
    }

    @Override
    public void end() {
        assert getDragSource() != null;

        liveUpdater.setDropTarget(null);

        /*
         * Note 1: we reset the drop target before performing the drop operation. This
         * makes content panel hide the drop target ring before fxom update and scene
         * graph refresh. Note 2: dropAccepted is reset before dropTargetProperty so
         * that listeners can invoke isDropAccepted().
         */
        dropAccepted = false;
        dropTargetProperty.set(null);

        if (committedDropTarget != null) {
            assert committedDropTarget.acceptDragSource(getDragSource());

            final AbstractJob dropJob = committedDropTarget.makeDropJob(getDragSource());

            final SelectionGroup selectionGroup = defaultSelectionGroupFactory
                    .getGroup(getDragSource().getDraggedObjects(), null, null);

            final Job selectJob = selectionJobFactory.updateSelection(selectionGroup);
            final BatchJob batchJob = batchJobFactory.getJob(dropJob.getDescription());

            if (committedDropTarget.isSelectRequiredAfterDrop()) {
                batchJob.addSubJob(backupSelectionJob);
            }

            batchJob.addSubJob(dropJob);

            if (committedDropTarget.isSelectRequiredAfterDrop()) {
                batchJob.addSubJob(selectJob);
            }
            jobManager.push(batchJob);
        }

        if (mouseTimer != null) {
            mouseTimer.cancel();
            mouseTimer = null;
        }

        logger.info("Drop session ended for {} objects", getDragSource().getDraggedObjects().size());

        liveUpdater = null;
        backupSelectionJob = null;
        committedDropTarget = null;
        dragSourceProperty.set(null);

    }

    @Override
    public DragSource getDragSource() {
        return dragSourceProperty.get();
    }

    @Override
    public Property<DragSource> dragSourceProperty() {
        return dragSourceProperty;
    }

    @Override
    public void setDropTarget(DropTarget newDropTarget) {
        assert getDragSource() != null;
        assert (newDropTarget == null) || (this.committedDropTarget == null);

        /*
         * Update drop target property. Note that this.dropAccepted is updated before so
         * that drop target listeners can invoke isDropAccepted().
         */
        if (newDropTarget == null) {
            dropAccepted = false;
        } else if (isDragSourceInParentChain(newDropTarget)) {
            dropAccepted = false;
        } else {
            dropAccepted = newDropTarget.acceptDragSource(getDragSource());
        }
        dropTargetProperty.set(newDropTarget);

        trackMouse();

        if (dropAccepted) {
            assert getDropTarget() != null;
            assert getDropTarget().acceptDragSource(getDragSource());
            assert getDragSource().getDraggedObjects().isEmpty() == false;

            final Set<? extends FXOMObject> items = getDragSource().getDraggedObjects();

            items.stream().findFirst().ifPresent(firstObject -> {

                final FXOMObject currentParent = firstObject.getParentObject();
                final FXOMObject nextParent = getDropTarget().getTargetObject();

                logger.debug("Drop accepted from {} to {} for {} objects",
                        currentParent == null ? "null" : currentParent.getClass().getName(),
                        nextParent == null ? "null" : nextParent.getClass().getName(),
                        items.size());

                if ((currentParent == nextParent) && liveUpdateEnabled) {
                    liveUpdater.setDropTarget(newDropTarget);
                }
            });

        }
    }

    @Override
    public DropTarget getDropTarget() {
        return dropTargetProperty.get();
    }

    @Override
    public Property<DropTarget> dropTargetProperty() {
        return dropTargetProperty;
    }

    @Override
    public boolean isDropAccepted() {
        return dropAccepted;
    }

    @Override
    public TransferMode[] getAcceptedTransferModes() {
        final TransferMode[] result;

        if (getDropTarget() == null) {
            result = TransferMode.NONE;
        } else if (dropAccepted) {
//            if (getDragSource() instanceof DocumentDragSource) {
//                result = new TransferMode[] { TransferMode.MOVE };
//            } else {
//                result = new TransferMode[] { TransferMode.COPY };
//            }
            result = new TransferMode[] { getDragSource().getTransferMode() };
        } else {
            result = TransferMode.NONE;
        }

        assert (result.length == 0) || (getDropTarget() != null);

        return result;
    }

    @Override
    public void commit() {
        assert isDropAccepted();
        assert committedDropTarget == null;

        committedDropTarget = getDropTarget();
    }

    public boolean isLiveUpdated() {
        return getDropTarget() == liveUpdater.getDropTarget();
    }

    /*
     * Private
     */

    private void mouseDidStopMoving() {
        if (dropAccepted && (getDropTarget() != liveUpdater.getDropTarget()) && liveUpdateEnabled) {
            liveUpdater.setDropTarget(getDropTarget());
        }
    }

    private static final long MOUSE_TIMER_DELAY = 500; // ms

    private void trackMouse() {
        final boolean runAsDaemon = true;

        if (mouseTimer == null) {
            mouseTimer = new Timer(runAsDaemon);
        } else {
            mouseTimer.cancel();
            mouseTimer = new Timer(runAsDaemon);
        }

        mouseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SbPlatform.runOnFxThreadWithActiveScope(() -> {
                    mouseTimer = null;
                    mouseDidStopMoving();
                });
            }
        }, MOUSE_TIMER_DELAY);
    }

    /**
     * Returns true if one of the dragged object is in the parent chain of the
     * specified drop target, false otherwise.
     *
     * @param newDropTarget the drop target
     * @return true if one of the dragged object is in the parent chain of the
     *         specified drop target, false otherwise
     */
    private boolean isDragSourceInParentChain(DropTarget newDropTarget) {
        assert newDropTarget != null;
        boolean result;

        FXOMDocument document = documentManager.fxomDocument().get();
        //if (newDropTarget instanceof RootDropTarget) {
        if (document == null || document.getFxomRoot() == null) {
            // dragSource is dragged over an empty document
            result = false;
        } else {
            final Set<? extends FXOMObject> draggedObjects = getDragSource().getDraggedObjects();
            final FXOMPath dropTargetPath = new FXOMPath(newDropTarget.getTargetObject());

            result = false;
            for (FXOMObject draggedObject : draggedObjects) {
                final FXOMPath draggedObjectPath = new FXOMPath(draggedObject);
                final FXOMPath commonPath = draggedObjectPath.getCommonPathWith(dropTargetPath);
                // If one of the dragged objects is in the parent chain
                // of the drop target, we abort the DND gesture
                if (commonPath.equals(draggedObjectPath)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}
