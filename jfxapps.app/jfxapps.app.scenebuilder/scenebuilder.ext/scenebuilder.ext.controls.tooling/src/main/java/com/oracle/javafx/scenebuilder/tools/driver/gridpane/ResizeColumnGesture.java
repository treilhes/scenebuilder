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

package com.oracle.javafx.scenebuilder.tools.driver.gridpane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.Deprecation;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.list.ColumnConstraintsListPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ResizeColumnGesture extends AbstractMouseGesture {

    private static final PropertyName columnConstraintsName
            = new PropertyName("columnConstraints"); //NOCHECK

    private static final ValuePropertyMetadata columnConstraintsMeta = new ColumnConstraintsListPropertyMetadata.Builder()
            .withName(columnConstraintsName)
            .withReadWrite(true)
            .withDefaultValue(Collections.emptyList())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private GridPaneHandles gridPaneHandles;
    private FXOMInstance fxomInstance;
    private int columnIndex;
    private GridPane gridPane;
    private GridPaneColumnResizer resizer;

    private final JobManager jobManager;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

	protected ResizeColumnGesture(
            Content content,
            JobManager jobManager,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
        super(content);
        this.jobManager = jobManager;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

	protected void setupGestureParameters(GridPaneHandles gridPaneHandles, int columnIndex) {
        assert columnIndex >= 0;

        this.gridPaneHandles = gridPaneHandles;
        this.fxomInstance = gridPaneHandles.getFxomInstance(); // Shortcut
        this.gridPane = (GridPane) fxomInstance.getSceneGraphObject(); // Shortcut
        this.columnIndex = columnIndex;

        assert this.columnIndex < Deprecation.getGridPaneColumnCount(this.gridPane);
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
        assert resizer == null;

        resizer = new GridPaneColumnResizer(gridPane, columnIndex);

        // Now same as mouseDragged
        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        assert resizer != null;

        final double startSceneX = getMousePressedEvent().getSceneX();
        final double startSceneY = getMousePressedEvent().getSceneY();
        final double currentSceneX = getLastMouseEvent().getSceneX();
        final double currentSceneY = getLastMouseEvent().getSceneY();
        final Point2D start = CoordinateHelper.sceneToLocal(fxomInstance, startSceneX, startSceneY, true /* rootScene */);
        final Point2D current = CoordinateHelper.sceneToLocal(fxomInstance, currentSceneX, currentSceneY, true /* rootScene */);
        final double dx = current.getX() - start.getX();

        resizer.updateWidth(dx);
        gridPane.layout();
        gridPaneHandles.layoutDecoration();
    }

    @Override
    protected void mouseDragEnded() {
        assert resizer != null;

        /*
         * Three steps
         *
         * 1) Collects the modified column constraints list
         * 2) Reverts to initial sizing
         *    => this step is equivalent to userDidCancel()
         * 3) Push a BatchModifyObjectJob to officially resize the columns
         */

        // Step #1
        final List<ColumnConstraints> newConstraints
                = cloneColumnConstraintsList(gridPane);

        // Step #2
        userDidCancel();

        // Step #3
        final AbstractJob j = modifyObjectJobFactory.getJob(I18N.getString("label.action.edit.resize.column"), fxomInstance, columnConstraintsMeta, newConstraints);
        jobManager.push(j);

        gridPaneHandles.layoutDecoration();
        resizer = null; // For sake of symetry...
    }

    @Override
    protected void mouseReleased() {
        // Everything is done in mouseDragEnded
    }

    @Override
    protected void keyEvent(KeyEvent e) {
        // Nothing special here
    }

    @Override
    protected void userDidCancel() {
        resizer.revertToOriginalSize();
        gridPane.layout();
    }



    /*
     * Private
     */

    private List<ColumnConstraints> cloneColumnConstraintsList(GridPane gridPane) {
        final List<ColumnConstraints> result = new ArrayList<>();

        for (ColumnConstraints cc : gridPane.getColumnConstraints()) {
            result.add(cloneColumnConstraints(cc));
        }

        return result;
    }


    private ColumnConstraints cloneColumnConstraints(ColumnConstraints cc) {
        final ColumnConstraints result = new ColumnConstraints();

        result.setFillWidth(cc.isFillWidth());
        result.setHalignment(cc.getHalignment());
        result.setHgrow(cc.getHgrow());
        result.setMaxWidth(cc.getMaxWidth());
        result.setMinWidth(cc.getMinWidth());
        result.setPercentWidth(cc.getPercentWidth());
        result.setPrefWidth(cc.getPrefWidth());

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<ResizeColumnGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public ResizeColumnGesture getGesture(GridPaneHandles gridPaneHandles, int columnIndex) {
            return create(ResizeColumnGesture.class, g -> g.setupGestureParameters(gridPaneHandles, columnIndex));
        }
    }

}
