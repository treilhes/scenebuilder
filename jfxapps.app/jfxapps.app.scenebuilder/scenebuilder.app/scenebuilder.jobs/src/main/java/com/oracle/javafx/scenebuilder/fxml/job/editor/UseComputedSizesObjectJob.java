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
package com.oracle.javafx.scenebuilder.fxml.job.editor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.ModifyObjectJob;

import javafx.scene.control.TableColumnBase;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

/**
 * This job apply the constant USE_COMPUTED_SIZE on width and height of the provided {@link FXOMInstance}
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UseComputedSizesObjectJob extends BatchDocumentJob {

    private FXOMInstance fxomInstance;
    private final IMetadata metadata;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

 // @formatter:off
    protected UseComputedSizesObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            IMetadata metadata,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.metadata = metadata;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

    protected void setJobParameters(FXOMInstance fxomInstance) {
        assert fxomInstance != null;
        this.fxomInstance = fxomInstance;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();
        final Object sceneGraphObject = fxomInstance.getSceneGraphObject();

        // RowConstraints: only height property is meaningfull
        if (sceneGraphObject instanceof RowConstraints) {
            result.addAll(modifyHeightJobs(fxomInstance));
        } //
          // ColumnConstraints: only width property is meaningfull
        else if (sceneGraphObject instanceof ColumnConstraints) {
            result.addAll(modifyWidthJobs(fxomInstance));
        } //
          // Region: both height and width properties are meaningfull
        else if (sceneGraphObject instanceof Region) {
            // First remove anchors if any
            result.addAll(removeAnchorsJobs());
            // Then modify height / width
            result.addAll(modifyHeightJobs(fxomInstance));
            result.addAll(modifyWidthJobs(fxomInstance));
        } //
          // Use computed sizes on ImageView => set the fit size to (0,0)
        else if (sceneGraphObject instanceof ImageView) {
            result.addAll(modifyFitHeightJob(fxomInstance));
            result.addAll(modifyFitWidthJob(fxomInstance));
        } //
          // TableColumnBase: only width property is meaningfull
        else if (sceneGraphObject instanceof TableColumnBase) {
            result.addAll(modifyWidthJobs(fxomInstance));
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Use Computed Sizes on ");
        final Object sceneGraphObject = fxomInstance.getSceneGraphObject();
        assert sceneGraphObject != null;
        sb.append(sceneGraphObject.getClass().getSimpleName());
        return sb.toString();
    }

    private List<AbstractJob> removeAnchorsJobs() {
        final List<AbstractJob> result = new ArrayList<>();
        final FXOMObject parentObject = fxomInstance.getParentObject();

        if (parentObject != null && parentObject.getSceneGraphObject() instanceof AnchorPane) {
            // switch off AnchorPane Constraints when parent is AnchorPane
            final PropertyName topAnchorPN = new PropertyName("topAnchor", AnchorPane.class);
            final ValuePropertyMetadata topAnchorVPM = metadata.queryValueProperty(fxomInstance, topAnchorPN);
            final PropertyName rightAnchorPN = new PropertyName("rightAnchor", AnchorPane.class);
            final ValuePropertyMetadata rightAnchorVPM = metadata.queryValueProperty(fxomInstance, rightAnchorPN);
            final PropertyName bottomAnchorPN = new PropertyName("bottomAnchor", AnchorPane.class);
            final ValuePropertyMetadata bottomAnchorVPM = metadata.queryValueProperty(fxomInstance, bottomAnchorPN);
            final PropertyName leftAnchorPN = new PropertyName("leftAnchor", AnchorPane.class);
            final ValuePropertyMetadata leftAnchorVPM = metadata.queryValueProperty(fxomInstance, leftAnchorPN);
            for (ValuePropertyMetadata vpm : new ValuePropertyMetadata[] { topAnchorVPM, rightAnchorVPM,
                    bottomAnchorVPM, leftAnchorVPM }) {

                if (vpm.getValueObject(fxomInstance) != null) {
                    final AbstractJob subJob = modifyObjectJobFactory.getJob(fxomInstance, vpm, null);
                    result.add(subJob);
                }
            }
        }
        return result;
    }

    private List<AbstractJob> modifyHeightJobs(final FXOMInstance candidate) {
        final List<AbstractJob> result = new ArrayList<>();

        final PropertyName maxHeight = new PropertyName("maxHeight");
        final PropertyName minHeight = new PropertyName("minHeight");
        final PropertyName prefHeight = new PropertyName("prefHeight");

        final ValuePropertyMetadata maxHeightVPM = metadata.queryValueProperty(candidate, maxHeight);
        final ValuePropertyMetadata minHeightVPM = metadata.queryValueProperty(candidate, minHeight);
        final ValuePropertyMetadata prefHeightVPM = metadata.queryValueProperty(candidate, prefHeight);

        final AbstractJob maxHeightJob = modifyObjectJobFactory.getJob(candidate, maxHeightVPM, -1.0);
        final AbstractJob minHeightJob = modifyObjectJobFactory.getJob(candidate, minHeightVPM, -1.0);
        final AbstractJob prefHeightJob = modifyObjectJobFactory.getJob(candidate, prefHeightVPM, -1.0);

        if (maxHeightJob.isExecutable()) {
            result.add(maxHeightJob);
        }
        if (minHeightJob.isExecutable()) {
            result.add(minHeightJob);
        }
        if (prefHeightJob.isExecutable()) {
            result.add(prefHeightJob);
        }
        return result;
    }

    private List<AbstractJob> modifyWidthJobs(final FXOMInstance candidate) {
        final List<AbstractJob> result = new ArrayList<>();

        final PropertyName maxWidth = new PropertyName("maxWidth");
        final PropertyName minWidth = new PropertyName("minWidth");
        final PropertyName prefWidth = new PropertyName("prefWidth");

        final ValuePropertyMetadata maxWidthVPM = metadata.queryValueProperty(candidate, maxWidth);
        final ValuePropertyMetadata minWidthVPM = metadata.queryValueProperty(candidate, minWidth);
        final ValuePropertyMetadata prefWidthVPM = metadata.queryValueProperty(candidate, prefWidth);

        final AbstractJob maxWidthJob = modifyObjectJobFactory.getJob(candidate, maxWidthVPM, -1.0);
        final AbstractJob minWidthJob = modifyObjectJobFactory.getJob(candidate, minWidthVPM, -1.0);
        final AbstractJob prefWidthJob = modifyObjectJobFactory.getJob(candidate, prefWidthVPM, -1.0);

        if (maxWidthJob.isExecutable()) {
            result.add(maxWidthJob);
        }
        if (minWidthJob.isExecutable()) {
            result.add(minWidthJob);
        }
        if (prefWidthJob.isExecutable()) {
            result.add(prefWidthJob);
        }
        return result;
    }

    private List<AbstractJob> modifyFitHeightJob(final FXOMInstance candidate) {
        final List<AbstractJob> result = new ArrayList<>();

        final PropertyName fitHeight = new PropertyName("fitHeight");
        final ValuePropertyMetadata fitHeightVPM = metadata.queryValueProperty(candidate, fitHeight);
        final AbstractJob fitHeightJob = modifyObjectJobFactory.getJob(candidate, fitHeightVPM, 0.0);

        if (fitHeightJob.isExecutable()) {
            result.add(fitHeightJob);
        }
        return result;
    }

    private List<AbstractJob> modifyFitWidthJob(final FXOMInstance candidate) {
        final List<AbstractJob> result = new ArrayList<>();

        final PropertyName fitWidth = new PropertyName("fitWidth");
        final ValuePropertyMetadata fitWidthVPM = metadata.queryValueProperty(candidate, fitWidth);
        final AbstractJob fitWidthJob = modifyObjectJobFactory.getJob(candidate, fitWidthVPM, 0.0);

        if (fitWidthJob.isExecutable()) {
            result.add(fitWidthJob);
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<UseComputedSizesObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UseComputedSizesObjectJob} job
         *
         * @param fxomInstance the target {@link FXOMInstance}
         * @return the job to execute
         */
        public UseComputedSizesObjectJob getJob(FXOMInstance fxomInstance) {
            return create(UseComputedSizesObjectJob.class, j -> j.setJobParameters(fxomInstance));
        }
    }
}
