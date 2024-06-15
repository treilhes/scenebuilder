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
package com.oracle.javafx.scenebuilder.fxml.job.editor;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.util.StringUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.api.mask.SbAccessoryProperty;
import com.oracle.javafx.scenebuilder.api.mask.SbDesignHierarchyMask;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

/**
 * Job to use for setting the size of the given FXOMObject; when not provided
 * deal with the top level item of the layout. The job will set the preferred
 * width and height to the given value while min and max width and height are
 * set to Region.USE_PREF_SIZE. No action is taken unless the FXOMObject is an
 * instance of Region or WebView.
 */
@Prototype
public final class UseSizeJob extends AbstractJob {

    private final List<Job> subJobs = new ArrayList<>();
    private String description; // final but initialized lazily
    private double width;
    private double height;
    private FXOMObject fxomObject;

    private final FXOMDocument fxomDocument;
    private final SbMetadata metadata;
    private final FxomJobsFactory fxomJobsFactory;
    private final SbDesignHierarchyMask.Factory sbDesignHierarchyMask;

    // @formatter:off
    protected UseSizeJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            SbMetadata metadata,
            SbDesignHierarchyMask.Factory sbDesignHierarchyMask,
            FxomJobsFactory fxomJobsFactory) {
     // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
        this.sbDesignHierarchyMask = sbDesignHierarchyMask;
    }

    public void setJobParameters(double width, double height, FXOMObject fxomObject) {
        this.width = width;
        this.height = height;
        if (fxomObject != null) {
            this.fxomObject = fxomObject;
        } else {
            if (fxomDocument == null) {
                this.fxomObject = null;
            } else {
                fxomObject = fxomDocument.getFxomRoot();

                if (fxomObject != null && fxomObject.getSceneGraphObject().isInstanceOf(Scene.class)) {
                    // Set the size of the scene's root
                    var mask = sbDesignHierarchyMask.getMask(fxomObject);
                    // TODO to check, do we have a way to not specify property by name?
                    var accessory = mask.getAccessory(SbAccessoryProperty.ROOT);
                    List<FXOMObject> fxomObjects = mask.getAccessories(accessory, false);

                    assert fxomObjects.size() == 1;
                    fxomObject = fxomObjects.get(0);
                }

                this.fxomObject = fxomObject;
            }
        }

        buildSubJobs();

        if (getDescription() == null) {
            setDescription(I18N.getString("job.set.size", StringUtils.getStringFromDouble(width),
                    StringUtils.getStringFromDouble(height)));
        }

    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return subJobs.isEmpty() == false;
    }

    @Override
    public void doExecute() {
        fxomDocument.beginUpdate();
        for (Job subJob : subJobs) {
            subJob.execute();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void doUndo() {
        fxomDocument.beginUpdate();
        for (int i = subJobs.size() - 1; i >= 0; i--) {
            subJobs.get(i).undo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        fxomDocument.beginUpdate();
        for (Job subJob : subJobs) {
            subJob.redo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        if (description == null) {
            description = I18N.getString("job.set.size", StringUtils.getStringFromDouble(width),
                    StringUtils.getStringFromDouble(height));
        }
        return description;
    }

    private void buildSubJobs() {

        if (fxomDocument != null && (fxomObject instanceof FXOMInstance)) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final Object sceneGraphObject = fxomInstance.getSceneGraphObject();

            if (sceneGraphObject instanceof WebView || sceneGraphObject instanceof Region) {
                subJobs.addAll(modifyHeightJobs(fxomInstance));
                subJobs.addAll(modifyWidthJobs(fxomInstance));
            }
        }
    }

    private List<Job> modifyHeightJobs(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final var maxHeight = new PropertyName("maxHeight"); // NOCHECK
        final var minHeight = new PropertyName("minHeight"); // NOCHECK
        final var prefHeight = new PropertyName("prefHeight"); // NOCHECK

        final var maxHeightVPM = metadata.queryValueProperty(candidate, maxHeight);
        final var minHeightVPM = metadata.queryValueProperty(candidate, minHeight);
        final var prefHeightVPM = metadata.queryValueProperty(candidate, prefHeight);

        final var maxHeightJob = fxomJobsFactory.modifyObject(candidate, maxHeightVPM, Region.USE_PREF_SIZE);
        final var minHeightJob = fxomJobsFactory.modifyObject(candidate, minHeightVPM, Region.USE_PREF_SIZE);
        final var prefHeightJob = fxomJobsFactory.modifyObject(candidate, prefHeightVPM, height);

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

        final var maxWidth = new PropertyName("maxWidth"); // NOCHECK
        final var minWidth = new PropertyName("minWidth"); // NOCHECK
        final var prefWidth = new PropertyName("prefWidth"); // NOCHECK

        final var maxWidthVPM = metadata.queryValueProperty(candidate, maxWidth);
        final var minWidthVPM = metadata.queryValueProperty(candidate, minWidth);
        final var prefWidthVPM = metadata.queryValueProperty(candidate, prefWidth);

        final var maxWidthJob = fxomJobsFactory.modifyObject(candidate, maxWidthVPM, Region.USE_PREF_SIZE);
        final var minWidthJob = fxomJobsFactory.modifyObject(candidate, minWidthVPM, Region.USE_PREF_SIZE);
        final var prefWidthJob = fxomJobsFactory.modifyObject(candidate, prefWidthVPM, width);

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
}
