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
package com.oracle.javafx.scenebuilder.job.editor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

/**
 * Job to use for setting the size of the given FXOMObject; when not provided
 * deal with the top level item of the layout. The job will set the preferred
 * width and height to the given value while min and max width and height are
 * set to Region.USE_PREF_SIZE.
 * No action is taken unless the FXOMObject is an instance of Region or WebView.
 */
public class UseSizeJob extends Job {

    private final List<Job> subJobs = new ArrayList<>();
    private String description; // final but initialized lazily
    private final double width;
    private final double height;
    private final Editor editor;
    private final FXOMObject fxomObject;
    private final FXOMDocument fxomDocument;

    public UseSizeJob(ApplicationContext context, Editor editor, double width, double height, FXOMObject fxomObject) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.editor = editor;
        this.width = width;
        this.height = height;
        this.fxomObject = fxomObject;
        buildSubJobs();
    }

    public UseSizeJob(ApplicationContext context, Editor editor, double width, double height) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.editor = editor;
        this.width = width;
        this.height = height;
        if (fxomDocument == null) {
            this.fxomObject = null;
        } else {
            FXOMObject fxomObject = fxomDocument.getFxomRoot();

            if (fxomObject != null && fxomObject.getSceneGraphObject() instanceof Scene) {
                // Set the size of the scene's root
                DesignHierarchyMask mask = new DesignHierarchyMask(fxomObject);
                //TODO to check
                Accessory accessory = mask.getAccessoryForPropertyName(DesignHierarchyMask.AccessoryProperty.ROOT);
                fxomObject = mask.getAccessory(accessory);
                
                assert fxomObject != null;
            }

            this.fxomObject = fxomObject;
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
    public void execute() {
        fxomDocument.beginUpdate();
        for (Job subJob : subJobs) {
            subJob.execute();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void undo() {
        fxomDocument.beginUpdate();
        for (int i = subJobs.size() - 1; i >= 0; i--) {
            subJobs.get(i).undo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void redo() {
        fxomDocument.beginUpdate();
        for (Job subJob : subJobs) {
            subJob.redo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        if (description == null) {
            description = I18N.getString("job.set.size",
                    JobUtils.getStringFromDouble(width),
                    JobUtils.getStringFromDouble(height));
        }
        return description;
    }

    private void buildSubJobs() {

        if (fxomDocument != null && (fxomObject instanceof FXOMInstance)) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final Object sceneGraphObject = fxomInstance.getSceneGraphObject();

            if (sceneGraphObject instanceof WebView
                    || sceneGraphObject instanceof Region) {
                subJobs.addAll(modifyHeightJobs(fxomInstance));
                subJobs.addAll(modifyWidthJobs(fxomInstance));
            }
        }
    }

    private List<Job> modifyHeightJobs(final FXOMInstance candidate) {
        final List<Job> result = new ArrayList<>();

        final PropertyName maxHeight = new PropertyName("maxHeight"); //NOCHECK
        final PropertyName minHeight = new PropertyName("minHeight"); //NOCHECK
        final PropertyName prefHeight = new PropertyName("prefHeight"); //NOCHECK

        final ValuePropertyMetadata maxHeightVPM
                = Metadata.getMetadata().queryValueProperty(candidate, maxHeight);
        final ValuePropertyMetadata minHeightVPM
                = Metadata.getMetadata().queryValueProperty(candidate, minHeight);
        final ValuePropertyMetadata prefHeightVPM
                = Metadata.getMetadata().queryValueProperty(candidate, prefHeight);

        final Job maxHeightJob = new ModifyObjectJob(getContext(),
                candidate, maxHeightVPM, Region.USE_PREF_SIZE, editor).extend();
        final Job minHeightJob = new ModifyObjectJob(getContext(),
                candidate, minHeightVPM, Region.USE_PREF_SIZE, editor).extend();
        final Job prefHeightJob = new ModifyObjectJob(getContext(),
                candidate, prefHeightVPM, height, editor).extend();

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

        final PropertyName maxWidth = new PropertyName("maxWidth"); //NOCHECK
        final PropertyName minWidth = new PropertyName("minWidth"); //NOCHECK
        final PropertyName prefWidth = new PropertyName("prefWidth"); //NOCHECK

        final ValuePropertyMetadata maxWidthVPM
                = Metadata.getMetadata().queryValueProperty(candidate, maxWidth);
        final ValuePropertyMetadata minWidthVPM
                = Metadata.getMetadata().queryValueProperty(candidate, minWidth);
        final ValuePropertyMetadata prefWidthVPM
                = Metadata.getMetadata().queryValueProperty(candidate, prefWidth);

        final Job maxWidthJob = new ModifyObjectJob(getContext(),
                candidate, maxWidthVPM, Region.USE_PREF_SIZE, editor).extend();
        final Job minWidthJob = new ModifyObjectJob(getContext(),
                candidate, minWidthVPM, Region.USE_PREF_SIZE, editor).extend();
        final Job prefWidthJob = new ModifyObjectJob(getContext(),
                candidate, prefWidthVPM, width, editor).extend();

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
