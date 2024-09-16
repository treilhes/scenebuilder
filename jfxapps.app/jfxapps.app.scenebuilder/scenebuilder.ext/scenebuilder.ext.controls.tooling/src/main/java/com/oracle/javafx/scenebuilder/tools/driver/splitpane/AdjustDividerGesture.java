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
package com.oracle.javafx.scenebuilder.tools.driver.splitpane;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.CardinalPoint;
import com.gluonhq.jfxapps.core.api.content.gesture.AbstractMouseGesture;
import com.gluonhq.jfxapps.core.api.content.gesture.GestureFactory;
import com.gluonhq.jfxapps.core.api.content.mode.Layer;
import com.gluonhq.jfxapps.core.api.content.mode.ModeManager;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.HudWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;

/**
 *
 *
 */
@ApplicationInstancePrototype
public class AdjustDividerGesture extends AbstractMouseGesture {

    private static final PropertyName dividerPositionsName = new PropertyName("dividerPositions"); // NOCHECK

    private FXOMInstance splitPaneInstance;
    private int dividerIndex;

    private final SplitPaneDesignInfoX di = new SplitPaneDesignInfoX();
    private double[] originalDividerPositions;

    private final JobManager jobManager;
    private final SbMetadata metadata;
    private final HudWindow hudWindow;
    private final FxomJobsFactory fxomJobsFactory;

    @SuppressWarnings("rawtypes")
    private Layer<Handles> handleLayer;

    //@formatter:off
    protected AdjustDividerGesture(
            Workspace workspace,
            SbMetadata metadata,
            JobManager jobManager,
            ModeManager modeManager,
            HudWindow hudWindow,
            FxomJobsFactory fxomJobsFactory) {
        //@formatter:on
        super(workspace);
        this.metadata = metadata;
        this.jobManager = jobManager;
        this.hudWindow = hudWindow;
        this.fxomJobsFactory = fxomJobsFactory;

        if (modeManager.hasModeEnabled()) {
            handleLayer = modeManager.getEnabledMode().getLayer(Handles.class);
        }
        assert handleLayer != null;
    }

    protected void setupGestureParameters(FXOMInstance splitPaneInstance, int dividerIndex) {
        assert splitPaneInstance.getSceneGraphObject().isInstanceOf(SplitPane.class);
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
        final SplitPane splitPane = splitPaneInstance.getSceneGraphObject().getAs(SplitPane.class);
        final double sceneX = getLastMouseEvent().getSceneX();
        final double sceneY = getLastMouseEvent().getSceneY();
        final double[] newDividerPositions
                = di.simulateDividerMove(splitPaneInstance, dividerIndex, sceneX, sceneY);
        splitPane.setDividerPositions(newDividerPositions);
        splitPane.layout();
        updateHudWindow();
        hudWindow.updatePopupLocation();
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
        final var dividerPositionsMeta = metadata.queryValueProperty(splitPaneInstance, dividerPositionsName);
        final Job j = fxomJobsFactory.modifyObject(splitPaneInstance,dividerPositionsMeta,newDividerPositions);
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
        hudWindow.closeWindow();
        handleLayer.enable();
        getSplitPane().layout();
    }


    /*
     * Private
     */

    private SplitPane getSplitPane() {
        assert splitPaneInstance.getSceneGraphObject().isInstanceOf(SplitPane.class);
        return splitPaneInstance.getSceneGraphObject().getAs(SplitPane.class);
    }

    private void setupAndOpenHudWindow() {
        hudWindow.setRowCount(1);
        hudWindow.setNameAtRowIndex("dividerPosition", 0); //NOCHECK
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
        hudWindow.setRelativePosition(cp);
        hudWindow.openWindow(splitPaneInstance.getClosestMainGraphNode().getSceneGraphObject().getAs(Node.class));
    }

    private void updateHudWindow() {
        double dividerPosition = getSplitPane().getDividerPositions()[dividerIndex];
        String str = String.format("%.2f %%", dividerPosition * 100); //NOCHECK
        hudWindow.setValueAtRowIndex(str, 0);
    }

    @ApplicationSingleton
    public static class Factory extends GestureFactory<AdjustDividerGesture> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }
        public AdjustDividerGesture getGesture(FXOMInstance splitPaneInstance, int dividerIndex) {
            return create(AdjustDividerGesture.class, g -> g.setupGestureParameters(splitPaneInstance, dividerIndex));
        }
    }

}
