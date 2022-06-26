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

import org.scenebuilder.fxml.api.HierarchyMask;
import org.scenebuilder.fxml.api.HierarchyMask.Accessory;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.util.StringUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.ModifyObjectJob;

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
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UseSizeJob extends AbstractJob {

    private final List<AbstractJob> subJobs = new ArrayList<>();
    private String description; // final but initialized lazily
    private double width;
    private double height;
    private FXOMObject fxomObject;

    private final FXOMDocument fxomDocument;
    private final Metadata metadata;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;

    // @formatter:off
    protected UseSizeJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Metadata metadata,
            DesignHierarchyMask.Factory designMaskFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
     // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
        this.designMaskFactory = designMaskFactory;
    }

    protected void setJobParameters(double width, double height, FXOMObject fxomObject) {
        this.width = width;
        this.height = height;
        if (fxomObject != null) {
            this.fxomObject = fxomObject;
        } else {
            if (fxomDocument == null) {
                this.fxomObject = null;
            } else {
                fxomObject = fxomDocument.getFxomRoot();

                if (fxomObject != null && fxomObject.getSceneGraphObject() instanceof Scene) {
                    // Set the size of the scene's root
                    HierarchyMask mask = designMaskFactory.getMask(fxomObject);
                    // TODO to check
                    Accessory accessory = mask.getAccessory(DesignHierarchyMask.AccessoryProperty.ROOT);
                    List<FXOMObject> fxomObjects = mask.getAccessories(accessory, false);

                    assert fxomObjects.size() == 1;
                    fxomObject = fxomObjects.get(0);
                }

                this.fxomObject = fxomObject;
            }
        }

        buildSubJobs();
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
        for (AbstractJob subJob : subJobs) {
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
        for (AbstractJob subJob : subJobs) {
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

    private List<AbstractJob> modifyHeightJobs(final FXOMInstance candidate) {
        final List<AbstractJob> result = new ArrayList<>();

        final PropertyName maxHeight = new PropertyName("maxHeight"); // NOCHECK
        final PropertyName minHeight = new PropertyName("minHeight"); // NOCHECK
        final PropertyName prefHeight = new PropertyName("prefHeight"); // NOCHECK

        final ValuePropertyMetadata maxHeightVPM = metadata.queryValueProperty(candidate, maxHeight);
        final ValuePropertyMetadata minHeightVPM = metadata.queryValueProperty(candidate, minHeight);
        final ValuePropertyMetadata prefHeightVPM = metadata.queryValueProperty(candidate, prefHeight);

        final AbstractJob maxHeightJob = modifyObjectJobFactory.getJob(candidate, maxHeightVPM, Region.USE_PREF_SIZE);
        final AbstractJob minHeightJob = modifyObjectJobFactory.getJob(candidate, minHeightVPM, Region.USE_PREF_SIZE);
        final AbstractJob prefHeightJob = modifyObjectJobFactory.getJob(candidate, prefHeightVPM, height);

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

        final PropertyName maxWidth = new PropertyName("maxWidth"); // NOCHECK
        final PropertyName minWidth = new PropertyName("minWidth"); // NOCHECK
        final PropertyName prefWidth = new PropertyName("prefWidth"); // NOCHECK

        final ValuePropertyMetadata maxWidthVPM = metadata.queryValueProperty(candidate, maxWidth);
        final ValuePropertyMetadata minWidthVPM = metadata.queryValueProperty(candidate, minWidth);
        final ValuePropertyMetadata prefWidthVPM = metadata.queryValueProperty(candidate, prefWidth);

        final AbstractJob maxWidthJob = modifyObjectJobFactory.getJob(candidate, maxWidthVPM, Region.USE_PREF_SIZE);
        final AbstractJob minWidthJob = modifyObjectJobFactory.getJob(candidate, minWidthVPM, Region.USE_PREF_SIZE);
        final AbstractJob prefWidthJob = modifyObjectJobFactory.getJob(candidate, prefWidthVPM, width);

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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static final class Factory extends JobFactory<UseSizeJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UseSizeJob} job
         *
         * @param width the new width
         * @param height the new height
         * @param fxomObject the target {@link FXOMObject}, if null the target is {@link FXOMDocument#getFxomRoot()}
         * @return the job to execute
         */
        public UseSizeJob getJob(double width, double height, FXOMObject fxomObject) {
            return create(UseSizeJob.class, j -> j.setJobParameters(width, height, fxomObject));
        }

        /**
         * Create an {@link UseSizeJob} job that target {@link FXOMDocument#getFxomRoot()}
         *
         * @param width the new width
         * @param height the new height
         * @return the job to execute
         */
        public UseSizeJob getJob(double width, double height) {
            return create(UseSizeJob.class, j -> j.setJobParameters(width, height, null));
        }
    }
}
