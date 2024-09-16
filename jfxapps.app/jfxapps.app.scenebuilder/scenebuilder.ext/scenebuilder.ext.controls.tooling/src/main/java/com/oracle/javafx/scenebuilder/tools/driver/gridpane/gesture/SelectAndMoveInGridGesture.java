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
package com.oracle.javafx.scenebuilder.tools.driver.gridpane.gesture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.action.editor.EditorPlatform;
import com.gluonhq.jfxapps.core.api.content.gesture.AbstractMouseDragGesture;
import com.gluonhq.jfxapps.core.api.content.gesture.GestureFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SelectAndMoveInGridGesture extends AbstractMouseDragGesture {

    private final static Logger logger = LoggerFactory.getLogger(SelectAndMoveInGridGesture.class);
    private final Selection selection;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;

    private FXOMInstance gridPaneInstance;
    private GridSelectionGroup.Type feature;
    private int featureIndex;


    protected SelectAndMoveInGridGesture(
            Content content,
            Selection selection,
            GridSelectionGroup.Factory gridSelectionGroupFactory) {
        super(content);
        this.selection = selection;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
    }

    private void setupGestureParameters(FXOMInstance gridPaneInstance, GridSelectionGroup.Type feature,
            int featureIndex) {
        assert gridPaneInstance.getSceneGraphObject().isInstanceOf(GridPane.class);
        this.gridPaneInstance = gridPaneInstance;
        this.feature = feature;
        this.featureIndex = featureIndex;
    }

    public FXOMInstance getGridPaneInstance() {
        return gridPaneInstance;
    }

    public GridSelectionGroup.Type getFeature() {
        return feature;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }



    /*
     * AbstractMouseDragGesture
     */

    @Override
    protected void mousePressed(MouseEvent e) {

        /*
         *             |        Object      |                      GridSelectionGroup                      |
         *             |      Selection     |                                                              |
         *             |        Group       +-----------------------------------------+--------------------+
         *             |                    |               feature type              |    feature type    |
         *             |                    |                  matches                |   does not match   |
         *             |                    +--------------------+--------------------+                    |
         *             |                    |       feature      |       feature      |                    |
         *             |                    |  not yet selected  |  already selected  |                    |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |                    |
         *             |   select feature   |   select feature   |                    |   select feature   |
         *   shift up  | start drag gesture | start drag gesture | start drag gesture | start drag gesture |
         *             |                    |                    |                    |                    |
         *             |         (A)        |       (B.1.1)      |       (B.1.2)      |       (B.2)        |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |                    |
         *             |   select feature   |     add feature    |   remove feature   |   select feature   |
         *  shift down | start drag gesture |    to selection    |   from selection   | start drag gesture |
         *             |                    | start drag gesture |ignore drag gesture |                    |
         *             |         (C)        |       (D.1.1)      |       (D.1.2)      |       (D.2)        |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         */

        final boolean extendKeyDown
                = EditorPlatform.isContinuousSelectKeyDown(e)
                || EditorPlatform.isNonContinousSelectKeyDown(e);

        //TODO may be simplified
        if (selection.getGroup() instanceof GridSelectionGroup) {
            if (extendKeyDown) { // Case D.1.* and D.2
                selection.toggleSelection(gridSelectionGroupFactory.getGroup(gridPaneInstance, feature, featureIndex));
            } else { // Cases B.1.*, B.2 or B.2
                selection.select(gridSelectionGroupFactory.getGroup(gridPaneInstance, feature, featureIndex));
            }
        } else { // Cases A and B
            assert selection.getGroup() instanceof DSelectionGroupFactory;
            selection.select(gridSelectionGroupFactory.getGroup(gridPaneInstance, feature, featureIndex));
        }
    }

    @Override
    protected void mouseDragDetected(MouseEvent e) {

        /*
         *             |        Object      |                      GridSelectionGroup                      |
         *             |      Selection     |                                                              |
         *             |        Group       +-----------------------------------------+--------------------+
         *             |                    |               feature type              |    feature type    |
         *             |                    |                  matches                |   does not match   |
         *             |                    +--------------------+--------------------+                    |
         *             |                    |       feature      |       feature      |                    |
         *             |                    |  not yet selected  |  already selected  |                    |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |                    |
         *             |   select feature   |   select feature   |                    |   select feature   |
         *   shift up  | start drag gesture | start drag gesture | start drag gesture | start drag gesture |
         *             |                    |                    |                    |                    |
         *             |         (A)        |       (B.1.1)      |       (B.1.2)      |       (B.2)        |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |                    |
         *             |   select feature   |     add feature    |   remove feature   |   select feature   |
         *  shift down | start drag gesture |    to selection    |   from selection   | start drag gesture |
         *             |                    | start drag gesture |ignore drag gesture |                    |
         *             |         (C)        |       (D.1.1)      |       (D.1.2)      |       (D.2)        |
         * ------------+--------------------+--------------------+--------------------+--------------------+
         */


        // TODO check why commented ? me or legacy ?
        if (selection.isSelected(gridSelectionGroupFactory.getGroup(gridPaneInstance, feature, featureIndex))) {
            // Case A, B.*.*, C, D.1.1 and D.2

//            final EditorController editorController
//                    = contentPanelController.getEditorController();
//            final Window ownerWindow
//                    = contentPanelController.getPanelRoot().getScene().getWindow();
//            final GridDragSource dragSource = new GridDragSource();
//
//            final Node glassLayer = contentPanelController.getGlassLayer();
//            final Dragboard db = glassLayer.startDragAndDrop(TransferMode.COPY_OR_MOVE);
//            db.setContent(dragSource.makeClipboardContent());
//            db.setDragView(dragSource.makeDragView());
//
//            assert editorController.getDragController().getDragSource() == null;
//            editorController.getDragController().begin(dragSource);
            logger.debug("SelectAndMoveInGridGesture.mouseDragDetected: will start column/row drag...");
        }
        // else Case D.1.2 : drag gesture is ignored

    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        // Nothing to do
    }

    @Override
    protected void mouseExited(MouseEvent e) {
        // Should be not called because mouse should exit glass layer
        // during this gesture
//        assert false;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<SelectAndMoveInGridGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public SelectAndMoveInGridGesture getGesture(FXOMInstance gridPaneInstance, GridSelectionGroup.Type feature,int featureIndex) {
            return create(SelectAndMoveInGridGesture.class, g -> g.setupGestureParameters(gridPaneInstance, feature, featureIndex));
        }
    }
}
