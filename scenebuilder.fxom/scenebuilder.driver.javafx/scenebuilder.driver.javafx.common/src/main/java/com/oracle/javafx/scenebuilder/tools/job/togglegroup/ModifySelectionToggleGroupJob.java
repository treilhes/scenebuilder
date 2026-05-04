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

package com.oracle.javafx.scenebuilder.tools.job.togglegroup;

import java.util.ArrayList;
import java.util.List;

import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.FxomSelection;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.treilhes.jfxplace.core.api.fxom.job.base.BatchDocumentJob;
import com.treilhes.jfxplace.core.api.fxom.subjects.FxomEvents;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.core.api.job.base.AbstractJob;
import com.treilhes.jfxplace.core.fxom.FXOMDocument;
import com.treilhes.jfxplace.core.fxom.FXOMInstance;
import com.treilhes.jfxplace.core.fxom.FXOMObject;
import com.treilhes.jfxplace.core.fxom.collector.FxCollector;

import javafx.scene.control.ToggleGroup;

/**
 * This job allocate a toggle group id to all currently selected {@link FXOMObject}
 */
@ApplicationInstancePrototype
public final class ModifySelectionToggleGroupJob extends BatchDocumentJob {

    private final I18N i18n;
    private final FxomSelection selection;
    private final FXOMDocument fxomDocument;
    private final ModifyToggleGroupJob.Factory modifyToggleGroupJobFactory;
    private String toggleGroupId;


    protected ModifySelectionToggleGroupJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            FxomSelection selection,
            ModifyToggleGroupJob.Factory modifyToggleGroupJobFactory) {
        super(extensionFactory, documentManager);
        this.i18n = i18n;
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
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

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
            final FXOMObject toggleGroupObject = fxomDocument
                    .collect(FxCollector.fxIdFindFirst(toggleGroupId)).get();

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
            if (selection.getGroup() instanceof ObjectSelectionGroup osg) {
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
        return i18n.getString("job.set.toggle.group");
    }

    @ApplicationInstanceSingleton
    public static final class Factory extends JobFactory<ModifySelectionToggleGroupJob> {
        public Factory(EmContext sbContext) {
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
