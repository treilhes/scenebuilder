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

package com.oracle.javafx.scenebuilder.selection.job.togglegroup;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.FitToParentObjectJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.control.ToggleGroup;

/**
 * This job allocate a toggle group id to all currently selected {@link FXOMObject}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ModifySelectionToggleGroupJob extends BatchDocumentJob {

    private String toggleGroupId;
    private final FXOMDocument fxomDocument;
    private Selection selection;
    private ModifyToggleGroupJob.Factory modifyToggleGroupJobFactory;

    protected ModifySelectionToggleGroupJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            ModifyToggleGroupJob.Factory modifyToggleGroupJobFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert fxomDocument != null;
        this.selection = selection;
        this.modifyToggleGroupJobFactory = modifyToggleGroupJobFactory;
    }

    protected void setJobParameters(String toggleGroupId) {
        this.toggleGroupId = toggleGroupId;
    }

    /*
     * BatchSelectionJob
     */

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        /*
         * Checks that toggleGroupId is:
         *  0) either null
         *  1) either an unused fx:id
         *  2) either the fx:id of an existing ToggleGroup instance
         */

        final boolean executable;
        if (toggleGroupId == null) {
            executable = true;
        } else {
            final FXOMObject toggleGroupObject = fxomDocument.searchWithFxId(toggleGroupId);
            if (toggleGroupObject == null) {
                // Case #1
                executable = true;
            } else if (toggleGroupObject instanceof FXOMInstance) {
                // Case #2
                final FXOMInstance toggleGroupInstance = (FXOMInstance) toggleGroupObject;
                executable = toggleGroupInstance.getDeclaredClass() == ToggleGroup.class;
            } else {
                executable = false;
            }
        }

        /*
         * Creates some ModifyToggleGroupJob instances
         */
        if (executable) {
            if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
                for (FXOMObject fxomObject : osg.getItems()) {
                    final AbstractJob subJob = modifyToggleGroupJobFactory.getJob(fxomObject, toggleGroupId);
                    if (subJob.isExecutable()) {
                        result.add(subJob);
                    }
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return I18N.getString("job.set.toggle.group");
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<ModifySelectionToggleGroupJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link FitToParentObjectJob} job
         * @param toggleGroupId the toggleGroupId
         * @return the job to execute
         */
        public ModifySelectionToggleGroupJob getJob(String toggleGroupId) {
            return create(ModifySelectionToggleGroupJob.class, j -> j.setJobParameters(toggleGroupId));
        }
    }
}
