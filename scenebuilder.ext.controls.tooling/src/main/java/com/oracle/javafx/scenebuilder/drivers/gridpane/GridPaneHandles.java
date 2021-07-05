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
package com.oracle.javafx.scenebuilder.drivers.gridpane;

import java.util.Collections;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.SelectAndMoveInGridGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractNodeHandles;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class GridPaneHandles extends AbstractNodeHandles<GridPane> {

    private final GridPaneMosaic mosaic
            = new GridPaneMosaic("handles", //NOCHECK
                    true /* shouldShowTray */,
                    true /* shouldCreateSensors */ );
	private final ApplicationContext context;
    private final Selection selection;

    public GridPaneHandles(
    		ApplicationContext context,
    		Selection selection,
    		Content contentPanelController) {
        super(context, contentPanelController, GridPane.class);
        this.context = context;
        this.selection = selection;
        getRootNode().getChildren().add(0, mosaic.getTopGroup()); // Below handles
    }
    
    @Override
    public void initialize() {
        mosaic.setGridPane((GridPane)getFxomObject().getSceneGraphObject());
    }

    @Override
    public void update() {
        AbstractSelectionGroup group = selection.getGroup();
        
        if (group == null || !(group instanceof GridSelectionGroup)) {
            mosaic.setSelectedColumnIndexes(Collections.emptySet());
            mosaic.setSelectedRowIndexes(Collections.emptySet());
        } else {
            GridSelectionGroup gsg = (GridSelectionGroup)group;
            switch(gsg.getType()) {
                case COLUMN:
                    mosaic.setSelectedColumnIndexes(gsg.getIndexes());
                    mosaic.setSelectedRowIndexes(Collections.emptySet());
                    break;
                case ROW:
                    mosaic.setSelectedColumnIndexes(Collections.emptySet());
                    mosaic.setSelectedRowIndexes(gsg.getIndexes());
                    break;
                default:
                    assert false;
                    break;
            }
        }
    }


    /*
     * AbstractNodeHandles
     */
    @Override
    public void layoutDecoration() {
        super.layoutDecoration();

        if (mosaic.getGridPane() != getSceneGraphObject()) {
            mosaic.setGridPane(getSceneGraphObject());
        } else {
            mosaic.update();
        }

        // Mosaic update may have created new trays and new sensors.
        // Attach this handles to them.
        for (Node node : this.mosaic.getNorthTrayNodes()) {
            attachHandles(node);
        }
        for (Node node : this.mosaic.getSouthTrayNodes()) {
            attachHandles(node);
        }
        for (Node node : this.mosaic.getEastTrayNodes()) {
            attachHandles(node);
        }
        for (Node node : this.mosaic.getWestTrayNodes()) {
            attachHandles(node);
        }
        for (Node node : this.mosaic.getHgapSensorNodes()) {
            attachHandles(node);
        }
        for (Node node : this.mosaic.getVgapSensorNodes()) {
            attachHandles(node);
        }

        // Update mosaic transform
        mosaic.getTopGroup().getTransforms().clear();
        mosaic.getTopGroup().getTransforms().add(getSceneGraphObjectToDecorationTransform());
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        AbstractGesture result = findGestureInTrays(node);
        if (result == null) {
            result = findGestureInSensors(node);
        }

        return result;
    }


    private AbstractGesture findGestureInTrays(Node node) {
        final GridSelectionGroup.Type feature;

        int trayIndex = mosaic.getNorthTrayNodes().indexOf(node);
        if (trayIndex != -1) {
            feature = GridSelectionGroup.Type.COLUMN;
        } else {
            trayIndex = mosaic.getSouthTrayNodes().indexOf(node);
            if (trayIndex != -1) {
                feature = GridSelectionGroup.Type.COLUMN;
            } else {
                trayIndex = mosaic.getWestTrayNodes().indexOf(node);
                if (trayIndex != -1) {
                    feature = GridSelectionGroup.Type.ROW;
                } else {
                    trayIndex = mosaic.getEastTrayNodes().indexOf(node);
                    feature = GridSelectionGroup.Type.ROW;
                }
            }
        }

        final AbstractGesture result;
        if (trayIndex == -1) {
            result = super.findGesture(node);
        } else {
            result = new SelectAndMoveInGridGesture(getContentPanelController(),
                    getFxomInstance(), feature, trayIndex);
        }

        return result;
    }


    private AbstractGesture findGestureInSensors(Node node) {
        final AbstractGesture result;

        int sensorIndex = mosaic.getHgapSensorNodes().indexOf(node);
        if (sensorIndex != -1) {
            result = new ResizeColumnGesture(context, this, sensorIndex);
        } else {
            sensorIndex = mosaic.getVgapSensorNodes().indexOf(node);
            if (sensorIndex != -1) {
                result = new ResizeRowGesture(context, this, sensorIndex);
            } else {
                result = super.findGesture(node);
            }
        }

        return result;
    }


    /*
     * Private
     */

    /*
     * Wrapper to avoid the 'leaking this in constructor' warning emitted by NB.
     */
    private void attachHandles(Node node) {
        if (AbstractHandles.lookupHandles(node) == null) {
            attachHandles(node, this);
        }
    }
}
