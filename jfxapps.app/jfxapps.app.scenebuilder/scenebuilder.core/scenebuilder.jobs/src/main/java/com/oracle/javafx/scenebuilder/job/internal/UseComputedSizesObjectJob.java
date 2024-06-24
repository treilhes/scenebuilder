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
package com.oracle.javafx.scenebuilder.job.internal;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

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
@Prototype
public final class UseComputedSizesObjectJob extends BatchDocumentJob {

    private final SbMetadata metadata;
    private final FxomJobsFactory fxomJobsFactory;

    private FXOMInstance fxomInstance;

 // @formatter:off
    protected UseComputedSizesObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(FXOMInstance fxomInstance) {
        assert fxomInstance != null;
        this.fxomInstance = fxomInstance;
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();

        // RowConstraints: only height property is meaningfull
        if (fxomInstance.getSceneGraphObject().isInstanceOf(RowConstraints.class)) {
            result.addAll(modifyHeightJobs(fxomInstance));
        } //
          // ColumnConstraints: only width property is meaningfull
        else if (fxomInstance.getSceneGraphObject().isInstanceOf(ColumnConstraints.class)) {
            result.addAll(modifyWidthJobs(fxomInstance));
        } //
          // Region: both height and width properties are meaningfull
        else if (fxomInstance.getSceneGraphObject().isInstanceOf(Region.class)) {
            // First remove anchors if any
            result.addAll(removeAnchorsJobs());
            // Then modify height / width
            result.addAll(modifyHeightJobs(fxomInstance));
            result.addAll(modifyWidthJobs(fxomInstance));
        } //
          // Use computed sizes on ImageView => set the fit size to (0,0)
        else if (fxomInstance.getSceneGraphObject().isInstanceOf(ImageView.class)) {
            result.addAll(modifyFitHeightJob(fxomInstance));
            result.addAll(modifyFitWidthJob(fxomInstance));
        } //
          // TableColumnBase: only width property is meaningfull
        else if (fxomInstance.getSceneGraphObject().isInstanceOf(TableColumnBase.class)) {
            result.addAll(modifyWidthJobs(fxomInstance));
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Use Computed Sizes on ");
        final Object sceneGraphObject = fxomInstance.getSceneGraphObject().get();
        assert sceneGraphObject != null;
        sb.append(sceneGraphObject.getClass().getSimpleName());
        return sb.toString();
    }

    private List<Job> removeAnchorsJobs() {
        final List<Job> result = new ArrayList<>();
        final FXOMObject parentObject = fxomInstance.getParentObject();

        if (parentObject != null && parentObject.getSceneGraphObject().isInstanceOf(AnchorPane.class)) {
            // switch off AnchorPane Constraints when parent is AnchorPane
            final var topAnchorPN = new PropertyName("topAnchor", AnchorPane.class);
            final var topAnchorVPM = metadata.queryValueProperty(fxomInstance, topAnchorPN);
            final var rightAnchorPN = new PropertyName("rightAnchor", AnchorPane.class);
            final var rightAnchorVPM = metadata.queryValueProperty(fxomInstance, rightAnchorPN);
            final var bottomAnchorPN = new PropertyName("bottomAnchor", AnchorPane.class);
            final var bottomAnchorVPM = metadata.queryValueProperty(fxomInstance, bottomAnchorPN);
            final var leftAnchorPN = new PropertyName("leftAnchor", AnchorPane.class);
            final var leftAnchorVPM = metadata.queryValueProperty(fxomInstance, leftAnchorPN);
            for (var vpm : new ValuePropertyMetadata[] { topAnchorVPM, rightAnchorVPM,
                    bottomAnchorVPM, leftAnchorVPM }) {

                if (vpm.getValueObject(fxomInstance) != null) {
                    final Job subJob = fxomJobsFactory.modifyObject(fxomInstance, vpm, null);
                    result.add(subJob);
                }
            }
        }
        return result;
    }

    private List<Job> modifyHeightJobs(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final var maxHeight = new PropertyName("maxHeight");
        final var minHeight = new PropertyName("minHeight");
        final var prefHeight = new PropertyName("prefHeight");

        final var maxHeightVPM = metadata.queryValueProperty(candidate, maxHeight);
        final var minHeightVPM = metadata.queryValueProperty(candidate, minHeight);
        final var prefHeightVPM = metadata.queryValueProperty(candidate, prefHeight);

        final var maxHeightJob = fxomJobsFactory.modifyObject(candidate, maxHeightVPM, -1.0);
        final var minHeightJob = fxomJobsFactory.modifyObject(candidate, minHeightVPM, -1.0);
        final var prefHeightJob = fxomJobsFactory.modifyObject(candidate, prefHeightVPM, -1.0);

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

    private List<Job> modifyWidthJobs(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final var maxWidth = new PropertyName("maxWidth");
        final var minWidth = new PropertyName("minWidth");
        final var prefWidth = new PropertyName("prefWidth");

        final var maxWidthVPM = metadata.queryValueProperty(candidate, maxWidth);
        final var minWidthVPM = metadata.queryValueProperty(candidate, minWidth);
        final var prefWidthVPM = metadata.queryValueProperty(candidate, prefWidth);

        final var maxWidthJob = fxomJobsFactory.modifyObject(candidate, maxWidthVPM, -1.0);
        final var minWidthJob = fxomJobsFactory.modifyObject(candidate, minWidthVPM, -1.0);
        final var prefWidthJob = fxomJobsFactory.modifyObject(candidate, prefWidthVPM, -1.0);

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

    private List<Job> modifyFitHeightJob(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final var fitHeight = new PropertyName("fitHeight");
        final var fitHeightVPM = metadata.queryValueProperty(candidate, fitHeight);
        final var fitHeightJob = fxomJobsFactory.modifyObject(candidate, fitHeightVPM, 0.0);

        if (fitHeightJob.isExecutable()) {
            result.add(fitHeightJob);
        }
        return result;
    }

    private List<Job> modifyFitWidthJob(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final var fitWidth = new PropertyName("fitWidth");
        final var fitWidthVPM = metadata.queryValueProperty(candidate, fitWidth);
        final var fitWidthJob = fxomJobsFactory.modifyObject(candidate, fitWidthVPM, 0.0);

        if (fitWidthJob.isExecutable()) {
            result.add(fitWidthJob);
        }
        return result;
    }
}
