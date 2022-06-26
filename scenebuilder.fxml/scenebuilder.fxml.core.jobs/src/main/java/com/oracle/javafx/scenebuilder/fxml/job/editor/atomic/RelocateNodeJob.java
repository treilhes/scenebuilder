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
package com.oracle.javafx.scenebuilder.fxml.job.editor.atomic;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.util.AnchorUtils;
import com.oracle.javafx.scenebuilder.util.MathUtils;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * Update the layout position of a given {@link FXOMObject}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RelocateNodeJob extends AbstractJob {

    private static final PropertyName layoutXName = new PropertyName("layoutX"); //NOCHECK
    private static final PropertyName layoutYName = new PropertyName("layoutY"); //NOCHECK
    private static final PropertyName leftAnchorName   = new PropertyName("leftAnchor",   AnchorPane.class); //NOCHECK
    private static final PropertyName rightAnchorName  = new PropertyName("rightAnchor",  AnchorPane.class); //NOCHECK
    private static final PropertyName topAnchorName    = new PropertyName("topAnchor",    AnchorPane.class); //NOCHECK
    private static final PropertyName bottomAnchorName = new PropertyName("bottomAnchor", AnchorPane.class); //NOCHECK


    private final Metadata metadata;

    private FXOMInstance fxomInstance;
    private double oldLayoutX;
    private double oldLayoutY;
    private Double oldLeftAnchor;
    private Double oldRightAnchor;
    private Double oldTopAnchor;
    private Double oldBottomAnchor;

    private double newLayoutX;
    private double newLayoutY;
    private Double newLeftAnchor;
    private Double newRightAnchor;
    private Double newTopAnchor;
    private Double newBottomAnchor;

    private DoublePropertyMetadata layoutXMeta;
    private DoublePropertyMetadata layoutYMeta;
    private DoublePropertyMetadata leftAnchorMeta;
    private DoublePropertyMetadata rightAnchorMeta;
    private DoublePropertyMetadata topAnchorMeta;
    private DoublePropertyMetadata bottomAnchorMeta;

    protected RelocateNodeJob(
            JobExtensionFactory extensionFactory,
            Metadata metadata) {
        super(extensionFactory);
        this.metadata = metadata;
    }

    protected void setJobParameters(FXOMInstance fxomInstance, double newLayoutX, double newLayoutY) {
        assert fxomInstance != null;
        assert fxomInstance.getSceneGraphObject() instanceof Node;

        this.fxomInstance = fxomInstance;
        this.newLayoutX = newLayoutX; // Root scene coordinates
        this.newLayoutY = newLayoutY; // Root scene coordinates

        final Class<?> sgoClass = fxomInstance.getMetadataClass();
        this.layoutXMeta = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, layoutXName);
        this.layoutYMeta = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, layoutYName);
        this.leftAnchorMeta   = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, leftAnchorName  );
        this.rightAnchorMeta  = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, rightAnchorName );
        this.topAnchorMeta    = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, topAnchorName   );
        this.bottomAnchorMeta = (DoublePropertyMetadata) metadata.queryProperty(sgoClass, bottomAnchorName);
    }

    public FXOMInstance getFxomInstance() {
        return fxomInstance;
    }

    public double getNewLayoutX() {
        return newLayoutX;
    }

    public double getNewLayoutY() {
        return newLayoutY;
    }

    public void mergeWith(RelocateNodeJob youngerJob) {
        assert ! (MathUtils.equals(this.newLayoutX, youngerJob.newLayoutX)
               && MathUtils.equals(this.newLayoutY, youngerJob.newLayoutY));
        this.newLayoutX = youngerJob.newLayoutX;
        this.newLayoutY = youngerJob.newLayoutY;
        updateNewAnchors();
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public void doExecute() {
        this.oldLayoutX = layoutXMeta.getValue(fxomInstance);
        this.oldLayoutY = layoutYMeta.getValue(fxomInstance);
        this.oldLeftAnchor   = leftAnchorMeta.getValue(fxomInstance);
        this.oldRightAnchor  = rightAnchorMeta.getValue(fxomInstance);
        this.oldTopAnchor    = topAnchorMeta.getValue(fxomInstance);
        this.oldBottomAnchor = bottomAnchorMeta.getValue(fxomInstance);

        updateNewAnchors();

        doRedo();
    }

    @Override
    public void doUndo() {
        this.layoutXMeta.setValue(fxomInstance, oldLayoutX);
        this.layoutYMeta.setValue(fxomInstance, oldLayoutY);
        if (oldLeftAnchor != null) {
            leftAnchorMeta.setValue(fxomInstance, oldLeftAnchor);
        }
        if (oldRightAnchor != null) {
            rightAnchorMeta.setValue(fxomInstance, oldRightAnchor);
        }
        if (oldTopAnchor != null) {
            topAnchorMeta.setValue(fxomInstance, oldTopAnchor);
        }
        if (oldBottomAnchor != null) {
            bottomAnchorMeta.setValue(fxomInstance, oldBottomAnchor);
        }
    }

    @Override
    public void doRedo() {
        this.layoutXMeta.setValue(fxomInstance, newLayoutX);
        this.layoutYMeta.setValue(fxomInstance, newLayoutY);
        if (newLeftAnchor != null) {
            leftAnchorMeta.setValue(fxomInstance, newLeftAnchor);
        }
        if (newRightAnchor != null) {
            rightAnchorMeta.setValue(fxomInstance, newRightAnchor);
        }
        if (newTopAnchor != null) {
            topAnchorMeta.setValue(fxomInstance, newTopAnchor);
        }
        if (newBottomAnchor != null) {
            bottomAnchorMeta.setValue(fxomInstance, newBottomAnchor);
        }
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName(); // Not expected to reach the user
    }


    /*
     * Private
     */

    private void updateNewAnchors() {
        if ((this.oldLeftAnchor == null) && (this.oldRightAnchor == null)) {
            this.newLeftAnchor = null;
            this.newRightAnchor = null;
        } else {
            final Node sceneGraphNode = (Node)fxomInstance.getSceneGraphObject();
            final Bounds nodeLayoutBounds = sceneGraphNode.getLayoutBounds();
            final Bounds parentLayoutBounds = sceneGraphNode.getParent().getLayoutBounds();
            if (this.oldLeftAnchor != null) {
                this.newLeftAnchor = AnchorUtils.computeLeftAnchor(parentLayoutBounds, nodeLayoutBounds, newLayoutX);
            } else {
                this.newLeftAnchor = null;
            }
            if (this.oldRightAnchor != null) {
                this.newRightAnchor = AnchorUtils.computeRightAnchor(parentLayoutBounds, nodeLayoutBounds, newLayoutX);
            } else {
                this.newRightAnchor = null;
            }
        }
        if ((this.oldTopAnchor == null) && (this.oldBottomAnchor == null)) {
            this.newTopAnchor = null;
            this.newBottomAnchor = null;
        } else {
            final Node sceneGraphNode = (Node)fxomInstance.getSceneGraphObject();
            final Bounds nodeLayoutBounds = sceneGraphNode.getLayoutBounds();
            final Bounds parentLayoutBounds = sceneGraphNode.getParent().getLayoutBounds();
            if (this.oldTopAnchor != null) {
                this.newTopAnchor = AnchorUtils.computeTopAnchor(parentLayoutBounds, nodeLayoutBounds, newLayoutY);
            } else {
                this.newTopAnchor = null;
            }
            if (this.oldBottomAnchor != null) {
                this.newBottomAnchor = AnchorUtils.computeBottomAnchor(parentLayoutBounds, nodeLayoutBounds, newLayoutY);
            } else {
                this.newBottomAnchor = null;
            }
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<RelocateNodeJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  RelocateNodeJob} job.
         *
         * @param fxomInstance the fxom instance
         * @param newLayoutX the new layout X
         * @param newLayoutY the new layout Y
         * @return the job to execute
         */
        public RelocateNodeJob getJob(FXOMInstance fxomInstance, double newLayoutX, double newLayoutY) {
            return create(RelocateNodeJob.class, j -> j.setJobParameters(fxomInstance, newLayoutX, newLayoutY));
        }
    }
}
