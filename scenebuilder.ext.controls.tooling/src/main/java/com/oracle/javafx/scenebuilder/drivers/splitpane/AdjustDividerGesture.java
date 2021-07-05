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
package com.oracle.javafx.scenebuilder.drivers.splitpane;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.CardinalPoint;
import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HudWindow;
import com.oracle.javafx.scenebuilder.api.content.ModeManager;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.mode.Layer;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;

/**
 *
 *
 */
public class AdjustDividerGesture extends AbstractMouseGesture {

    private final FXOMInstance splitPaneInstance;
    private final int dividerIndex;
    private final SplitPaneDesignInfoX di = new SplitPaneDesignInfoX();
    private double[] originalDividerPositions;
	private final ApplicationContext context;
    private Layer<Handles> handleLayer;

    private static final PropertyName dividerPositionsName
            = new PropertyName("dividerPositions"); //NOCHECK

    public AdjustDividerGesture(
    		ApplicationContext context,
    		Content content,
            FXOMInstance splitPaneInstance, int dividerIndex) {
        super(content);
        this.context = context;

        assert splitPaneInstance.getSceneGraphObject() instanceof SplitPane;
        this.splitPaneInstance = splitPaneInstance;
        this.dividerIndex = dividerIndex;
        
        ModeManager modeManager = context.getBean(ModeManager.class);
        if (modeManager.hasModeEnabled()) {
            handleLayer = modeManager.getEnabledMode().getLayer(Handles.class);
        }
        assert handleLayer != null;
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
        final Metadata metadata = Metadata.getMetadata();
        final Editor editorController
                = contentPanelController.getEditorController();
        final ValuePropertyMetadata dividerPositionsMeta
                = metadata.queryValueProperty(splitPaneInstance, dividerPositionsName);
        final Job j = new ModifyObjectJob(context,
                splitPaneInstance,
                dividerPositionsMeta,
                newDividerPositions,
                editorController).extend();
        if (j.isExecutable()) {
            editorController.getJobManager().push(j);
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
}
