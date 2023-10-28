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

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key;

import java.util.HashMap;
import java.util.Map;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractKeyGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.misc.Workspace;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.RelocateSelectionJob;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class MoveWithKeyGesture extends AbstractKeyGesture {

    private final Selection selection;
	private final JobManager jobManager;
	private final RelocateSelectionJob.Factory relocateSelectionJobFactory;

	private double vectorX;
    private double vectorY;

    protected MoveWithKeyGesture(
            Workspace workspace,
            Selection selection,
    		JobManager jobManager,
    		RelocateSelectionJob.Factory relocateSelectionJobFactory) {
        super(workspace);
        this.selection = selection;
        this.jobManager = jobManager;
        this.relocateSelectionJobFactory = relocateSelectionJobFactory;
        assert selection.isMovable(); // (1)
    }

    /*
     * AbstractKeyGesture
     */

    @Override
    protected void keyPressed() {

        final double extend = getLastKeyEvent().isShiftDown() ? 10.0 : 1.0;
        final double moveX = extend * vectorX;
        final double moveY = extend * vectorY;

        assert selection.getGroup() instanceof DefaultSelectionGroupFactory; // Because (1)
        final DefaultSelectionGroupFactory osg
                = (DefaultSelectionGroupFactory) selection.getGroup();

        /*
         * Updates layoutX/layoutY of the selected scene graph objects.
         */
        for (FXOMObject selectedObject : osg.getFlattenItems()) {
            assert selectedObject.isNode(); // Because (1)
            final Node node = (Node) selectedObject.getSceneGraphObject();
            node.setLayoutX(node.getLayoutX() + moveX);
            node.setLayoutY(node.getLayoutY() + moveY);
        }
    }

    @Override
    protected void keyReleased() {
        keyPressed();

        assert selection.getGroup() instanceof DefaultSelectionGroupFactory; // Because (1)

        final DefaultSelectionGroupFactory osg
                = (DefaultSelectionGroupFactory) selection.getGroup();

        // Builds a RelocateSelectionJob
        final Map<FXOMObject, Point2D> locationMap = new HashMap<>();
        for (FXOMObject selectedObject : osg.getItems()) {
            assert selectedObject.isNode(); // Because (1)
            assert selectedObject instanceof FXOMInstance;

            final Node node = (Node) selectedObject.getSceneGraphObject();
            final Point2D layoutXY = new Point2D(node.getLayoutX(), node.getLayoutY());
            locationMap.put(selectedObject, layoutXY);
        }

        final RelocateSelectionJob newRelocateJob = relocateSelectionJobFactory.getJob(locationMap);

        // ... and pushes it
        // If the current job is already a RelocateSelectionJob,
        // then we see if the new job can be merged with it.

        AbstractJob currentJob = jobManager.getCurrentJob();

        if (currentJob instanceof RelocateSelectionJob) {
            final RelocateSelectionJob currentRelocateJob = (RelocateSelectionJob) currentJob;
            if (currentRelocateJob.canBeMergedWith(newRelocateJob)) {
                newRelocateJob.execute();
                currentRelocateJob.mergeWith(newRelocateJob);
            } else {
                jobManager.push(newRelocateJob);
            }
        } else {
            jobManager.push(newRelocateJob);
        }
    }

    /*
     * AbstractGesture
     */
    @Override
    public void start(InputEvent e, Observer observer) {
        super.start(e, observer);

        switch(getFirstKeyPressedEvent().getCode()) {
            case DOWN:
                vectorX = +0.0;
                vectorY = +1.0;
                break;
            case UP:
                vectorX = +0.0;
                vectorY = -1.0;
                break;
            case LEFT:
                vectorX = -1.0;
                vectorY = +0.0;
                break;
            case RIGHT:
                vectorX = +1.0;
                vectorY = +0.0;
                break;
            default:
                assert false : "Unexpected key code" + getFirstKeyPressedEvent().getCode();
                vectorX = 0.0;
                vectorY = 0.0;
                break;
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<MoveWithKeyGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public MoveWithKeyGesture getGesture() {
            return create(MoveWithKeyGesture.class, null); // g -> g.setupGestureParameters());
        }
    }

}
