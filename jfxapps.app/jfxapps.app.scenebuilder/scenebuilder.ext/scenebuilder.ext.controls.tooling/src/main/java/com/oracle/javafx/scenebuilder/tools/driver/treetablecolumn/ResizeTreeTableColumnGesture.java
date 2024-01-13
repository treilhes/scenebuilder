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

package com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.geometry.Point2D;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ResizeTreeTableColumnGesture extends AbstractMouseGesture {

    private FXOMInstance columnInstance;
    private TreeTableColumnResizer resizer;
	private final BatchJob.Factory batchJobFactory;
    private final JobManager jobManager;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final IMetadata metadata;

    protected ResizeTreeTableColumnGesture(
            Content content,
            JobManager jobManager,
            IMetadata metadata,
            BatchJob.Factory batchJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
        super(content);
        this.jobManager = jobManager;
        this.metadata = metadata;
        this.batchJobFactory = batchJobFactory;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

    protected void setupGestureParameters(FXOMInstance fxomInstance) {
        assert fxomInstance != null;
        assert fxomInstance.getSceneGraphObject() instanceof TreeTableColumn;

        this.columnInstance = fxomInstance;
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
        assert columnInstance.getSceneGraphObject() instanceof TreeTableColumn;

        resizer = new TreeTableColumnResizer((TreeTableColumn<?,?>)columnInstance.getSceneGraphObject());

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
        final TreeTableView<?> treeTableView = resizer.getTreeTableColumn().getTreeTableView();
        final Point2D start = CoordinateHelper.sceneToLocal(columnInstance.getParentObject(), startSceneX, startSceneY, true /* rootScene */);
        final Point2D current = CoordinateHelper.sceneToLocal(columnInstance.getParentObject(), currentSceneX, currentSceneY, true /* rootScene */);
        final double dx = current.getX() - start.getX();

        resizer.updateWidth(dx);
        treeTableView.layout();
    }

    @Override
    protected void mouseDragEnded() {
        assert resizer != null;

        /*
         * Three steps
         *
         * 1) Collects sizing properties that have changed
         * 2) Reverts to initial sizing
         *    => this step is equivalent to userDidCancel()
         * 3) Push a BatchModifyObjectJob to officially resize the columns
         */

        // Step #1
        final Map<PropertyName, Object> changeMap = resizer.getChangeMap();
        final Map<PropertyName, Object> changeMapNext = resizer.getChangeMapNext();


        // Step #2
        userDidCancel();

        // Step #3

        final BatchJob batchJob
                = batchJobFactory.getJob(I18N.getString("label.action.edit.resize.column"), true);
        if (changeMap.isEmpty() == false) {
            batchJob.addSubJobs(makeResizeJob(columnInstance, changeMap));
        }
        if (changeMapNext.isEmpty() == false) {
            batchJob.addSubJobs(makeResizeJob(columnInstance.getNextSlibing(), changeMapNext));
        }
        if (batchJob.isExecutable()) {
            jobManager.push(batchJob);
        }

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
        resizer.getTreeTableColumn().getTreeTableView().layout();
    }


    /*
     * Private
     */

    private List<AbstractJob> makeResizeJob(FXOMObject columnObject, Map<PropertyName, Object> changeMap) {
        assert columnObject.getSceneGraphObject() instanceof TreeTableColumn;
        assert columnObject instanceof FXOMInstance;

        final List<AbstractJob> result = new ArrayList<>();

        final Map<ValuePropertyMetadata, Object> metaValueMap = new HashMap<>();
        for (Map.Entry<PropertyName,Object> e : changeMap.entrySet()) {
            final ValuePropertyMetadata vpm = metadata.queryValueProperty(columnInstance, e.getKey());
            assert vpm != null;
            metaValueMap.put(vpm, e.getValue());
        }

        for (Map.Entry<ValuePropertyMetadata, Object> e : metaValueMap.entrySet()) {
            final AbstractJob job = modifyObjectJobFactory.getJob((FXOMInstance) columnObject,e.getKey(),e.getValue());
            result.add(job);
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<ResizeTreeTableColumnGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public ResizeTreeTableColumnGesture getGesture(FXOMInstance fxomInstance) {
            return create(ResizeTreeTableColumnGesture.class, g -> g.setupGestureParameters(fxomInstance));
        }
    }

}