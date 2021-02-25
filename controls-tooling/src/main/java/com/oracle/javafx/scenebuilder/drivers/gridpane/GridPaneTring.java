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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractNodeTring;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.draganddrop.target.GridPaneDropTarget;
import com.oracle.javafx.scenebuilder.draganddrop.target.GridPaneDropTarget.ColumnArea;
import com.oracle.javafx.scenebuilder.draganddrop.target.GridPaneDropTarget.RowArea;

import javafx.scene.layout.GridPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class GridPaneTring extends AbstractNodeTring<GridPane> {

    private final GridPaneMosaic mosaic
            = new GridPaneMosaic("tring", //NOI18N
                    false /* shouldShowTray */,
                    false /* shouldCreateSensors */ );

    public GridPaneTring(Content contentPanelController) {
        super(contentPanelController, GridPane.class);
        getRootNode().getChildren().add(0, mosaic.getTopGroup()); // Below handles
    }

    
    @Override
    public void defineDropTarget(DropTarget dropTarget) {
        assert dropTarget != null;
        // FIXME dropTarget is an AccessoryTarget on drag drop
        assert dropTarget instanceof GridPaneDropTarget;
        
        GridPaneDropTarget gridPaneDropTarget = (GridPaneDropTarget)dropTarget;
        mosaic.setGridPane((GridPane)gridPaneDropTarget.getTargetObject().getSceneGraphObject());
        
        final int targetColumnIndex
                = gridPaneDropTarget.getTargetColumnIndex();
        final int targetRowIndex
                = gridPaneDropTarget.getTargetRowIndex();
        final ColumnArea targetColumnArea
                = gridPaneDropTarget.getTargetColumnArea();
        final RowArea targetRowArea
                = gridPaneDropTarget.getTargetRowArea();

        if ((targetColumnArea == ColumnArea.CENTER) && (targetRowArea == RowArea.CENTER)) {
            mosaic.setTargetCell(targetColumnIndex, targetRowIndex);
        } else {
            final int targetGapColumnIndex;
            switch(targetColumnArea) {
                case LEFT:
                    targetGapColumnIndex = targetColumnIndex;
                    break;
                default:
                case CENTER:
                    targetGapColumnIndex = -1;
                    break;
                case RIGHT:
                    targetGapColumnIndex = targetColumnIndex+1;
                    break;
            }
            final int targetGapRowIndex;
            switch(targetRowArea) {
                case TOP:
                    targetGapRowIndex = targetRowIndex;
                    break;
                default:
                case CENTER:
                    targetGapRowIndex = -1;
                    break;
                case BOTTOM:
                    targetGapRowIndex = targetRowIndex+1;
                    break;
            }
            mosaic.setTargetGap(targetGapColumnIndex, targetGapRowIndex);
        }
    }

    @Override
    public void initialize() {
        
    }

    /*
     * AbstractGenericTring
     */

    @Override
    protected void layoutDecoration() {

        super.layoutDecoration();

        if (mosaic.getGridPane() != getSceneGraphObject()) {
            mosaic.setGridPane(getSceneGraphObject());
        } else {
            mosaic.update();
        }

        // Update mosaic transform
        mosaic.getTopGroup().getTransforms().clear();
        mosaic.getTopGroup().getTransforms().add(getSceneGraphObjectToDecorationTransform());
    }
}
