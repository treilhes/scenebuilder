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
package com.oracle.javafx.scenebuilder.tools.driver.splitpane;

import java.util.ArrayList;
import java.util.List;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.CardinalPoint;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.content.mode.Layer;
import com.oracle.javafx.scenebuilder.api.content.mode.ModeManager;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.misc.HudWindow;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class AdjustDividerGesture extends AbstractMouseGesture {

    private static final PropertyName dividerPositionsName
        = new PropertyName("dividerPositions"); //NOCHECK

    private FXOMInstance splitPaneInstance;
    private int dividerIndex;

    private final SplitPaneDesignInfoX di = new SplitPaneDesignInfoX();
    private double[] originalDividerPositions;

	private final JobManager jobManager;
	private final IMetadata metadata;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

	@SuppressWarnings("rawtypes")
    private Layer<Handles> handleLayer;

    protected AdjustDividerGesture(
    		Content content,
    		IMetadata metadata,
    		JobManager jobManager,
    		ModeManager modeManager,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
        super(content);
        this.metadata = metadata;
        this.jobManager = jobManager;
        this.modifyObjectJobFactory = modifyObjectJobFactory;

        if (modeManager.hasModeEnabled()) {
            handleLayer = modeManager.getEnabledMode().getLayer(Handles.class);
        }
        assert handleLayer != null;
    }

    protected void setupGestureParameters(FXOMInstance splitPaneInstance, int dividerIndex) {
        assert splitPaneInstance.getSceneGraphObject() instanceof SplitPane;
        this.splitPaneInstance = splitPaneInstance;
        this.dividerIndex = dividerIndex;
    }

    /*
     * AbstractMouseGesture
     */

    @Override
    protected void mousePressed() {
        // Everthing is done in mouseDragStarted
    }

    @Override
    protected void mouseDragStarted() {
        originalDividerPositions = getSplitPane().getDividerPositions();
        setupAndOpenHudWindow();
        handleLayer.disable();
        // Now same as mouseDragged
        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        final SplitPane splitPane = (SplitPane)splitPaneInstance.getSceneGraphObject();
        final double sceneX = getLastMouseEvent().getSceneX();
        final double sceneY = getLastMouseEvent().getSceneY();
        final double[] newDividerPositions
                = di.simulateDividerMove(splitPaneInstance, dividerIndex, sceneX, sceneY);
        splitPane.setDividerPositions(newDividerPositions);
        splitPane.layout();
        updateHudWindow();
        contentPanelController.getHudWindowController().updatePopupLocation();
    }

    @Override
    protected void mouseDragEnded() {
        /*
         * Three steps
         *
         * 1) Copy the updated divider positions
         * 2) Reverts to initial divider positions
         *    => this step is equivalent to userDidCancel()
         * 3) Push a BatchModifyObjectJob to officially update dividers
         */

        // Step #1
        final List<Double> newDividerPositions = new ArrayList<>();
        for (double p : getSplitPane().getDividerPositions()) {
            newDividerPositions.add(Double.valueOf(p));
        }

        // Step #2
        userDidCancel();

        // Step #3
        final ValuePropertyMetadata dividerPositionsMeta
                = metadata.queryValueProperty(splitPaneInstance, dividerPositionsName);
        final AbstractJob j = modifyObjectJobFactory.getJob(splitPaneInstance,dividerPositionsMeta,newDividerPositions);
        if (j.isExecutable()) {
            jobManager.push(j);
        } // else divider has been release to its original position
    }

    @Override
    protected void mouseReleased() {
        // Everything is done in mouseDragEnded
    }

    @Override
    protected void keyEvent(KeyEvent e) {
    }

    @Override
    protected void userDidCancel() {
        getSplitPane().setDividerPositions(originalDividerPositions);
        contentPanelController.getHudWindowController().closeWindow();
        handleLayer.enable();
        getSplitPane().layout();
    }


    /*
     * Private
     */

    private SplitPane getSplitPane() {
        assert splitPaneInstance.getSceneGraphObject() instanceof SplitPane;
        return (SplitPane) splitPaneInstance.getSceneGraphObject();
    }

    private void setupAndOpenHudWindow() {
        final HudWindow hudWindowController
                = contentPanelController.getHudWindowController();

        hudWindowController.setRowCount(1);
        hudWindowController.setNameAtRowIndex("dividerPosition", 0); //NOCHECK
        updateHudWindow();

        final CardinalPoint cp;
        switch(getSplitPane().getOrientation()) {
            default:
            case HORIZONTAL:
                cp = CardinalPoint.S;
                break;
            case VERTICAL:
                cp = CardinalPoint.E;
                break;
        }
        hudWindowController.setRelativePosition(cp);
        hudWindowController.openWindow((Node)splitPaneInstance.getClosestMainGraphNode().getSceneGraphObject());
    }

    private void updateHudWindow() {
        final HudWindow hudWindowController
                = contentPanelController.getHudWindowController();

        double dividerPosition = getSplitPane().getDividerPositions()[dividerIndex];
        String str = String.format("%.2f %%", dividerPosition * 100); //NOCHECK
        hudWindowController.setValueAtRowIndex(str, 0);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<AdjustDividerGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public AdjustDividerGesture getGesture(FXOMInstance splitPaneInstance, int dividerIndex) {
            return create(AdjustDividerGesture.class, g -> g.setupGestureParameters(splitPaneInstance, dividerIndex));
        }
    }

}
