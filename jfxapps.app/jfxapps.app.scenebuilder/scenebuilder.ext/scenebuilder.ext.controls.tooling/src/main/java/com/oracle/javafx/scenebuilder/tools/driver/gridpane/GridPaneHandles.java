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
package com.oracle.javafx.scenebuilder.tools.driver.gridpane;

import java.util.Collections;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.InitializingBean;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.content.gesture.AbstractGesture;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractNodeHandles;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.gesture.SelectAndMoveInGridGesture;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 */
@Prototype
public class GridPaneHandles extends AbstractNodeHandles<GridPane> implements InitializingBean {

    private final GridPaneMosaic mosaic
            = new GridPaneMosaic("handles", //NOCHECK
                    true /* shouldShowTray */,
                    true /* shouldCreateSensors */ );

    private final Selection selection;
    private final SelectAndMoveInGridGesture.Factory selectAndMoveInGridGestureFactory;
    private final ResizeColumnGesture.Factory resizeColumnGestureFactory;
    private final ResizeRowGesture.Factory resizeRowGestureFactory;

    public GridPaneHandles(
            Driver driver,
            Workspace workspace,
            FxmlDocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            ResizeGesture.Factory resizeGestureFactory,
    		Selection selection,
    		SelectAndMoveInGridGesture.Factory selectAndMoveInGridGestureFactory,
    		ResizeColumnGesture.Factory resizeColumnGestureFactory,
    		ResizeRowGesture.Factory resizeRowGestureFactory) {
        super(driver, workspace, documentManager, discardGestureFactory, resizeGestureFactory,  GridPane.class);

        this.selection = selection;
        this.selectAndMoveInGridGestureFactory = selectAndMoveInGridGestureFactory;
        this.resizeColumnGestureFactory = resizeColumnGestureFactory;
        this.resizeRowGestureFactory = resizeRowGestureFactory;
    }

    //FIXME replace with jsr250 annotation @PostConstrut
    @Override
    public void afterPropertiesSet() throws Exception {
        getRootNode().getChildren().add(0, mosaic.getTopGroup()); // Below handles
    }

    @Override
    public void initialize() {
        mosaic.setGridPane(getFxomObject().getSceneGraphObject().getAs(GridPane.class));
    }

    @Override
    public void update() {
        SelectionGroup group = selection.getGroup();

        if (group != null && (group instanceof GridSelectionGroup gsg)) {
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
        } else {
            mosaic.setSelectedColumnIndexes(Collections.emptySet());
            mosaic.setSelectedRowIndexes(Collections.emptySet());
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
            result = selectAndMoveInGridGestureFactory.getGesture(getFxomInstance(), feature, trayIndex);
        }

        return result;
    }


    private AbstractGesture findGestureInSensors(Node node) {
        final AbstractGesture result;

        int sensorIndex = mosaic.getHgapSensorNodes().indexOf(node);
        if (sensorIndex != -1) {
            result = resizeColumnGestureFactory.getGesture(this, sensorIndex);
        } else {
            sensorIndex = mosaic.getVgapSensorNodes().indexOf(node);
            if (sensorIndex != -1) {
                result = resizeRowGestureFactory.getGesture(this, sensorIndex);
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
