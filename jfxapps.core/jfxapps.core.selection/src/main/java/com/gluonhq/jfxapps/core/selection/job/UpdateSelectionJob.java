/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

package com.gluonhq.jfxapps.core.selection.job;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;


/**
 * Update the currently scoped document selection {@link Selection} with the provided list of {@link FXOMObject}
 */
@Prototype
public final class UpdateSelectionJob extends AbstractJob {

    private SelectionGroup oldSelectionGroup;
    private SelectionGroup newSelectionGroup;

    private final FXOMDocument fxomDocument;
    private final Selection selection;

    protected UpdateSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = selection;
    }

    protected void setJobParameters(SelectionGroup group) {
        newSelectionGroup = group;
    }
//    protected void setJobParameters(Collection<FXOMObject> newSelectedObjects) {
//        assert newSelectedObjects != null; // But possibly empty
//        if (newSelectedObjects.isEmpty()) {
//            newSelectionGroup = null;
//        } else {
//            newSelectionGroup = objectSelectionGroupFactory.getGroup(newSelectedObjects, newSelectedObjects.iterator().next(), null);
//        }
//    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public void doExecute() {
        // Saves the current selection
        try {
            if (selection.getGroup() == null) {
                this.oldSelectionGroup = null;
            } else {
                this.oldSelectionGroup = selection.getGroup().clone();
            }
        } catch(CloneNotSupportedException x) {
            throw new RuntimeException("Bug", x);
        }

        // Now same as redo()
        redo();
    }

    @Override
    public void doUndo() {
        selection.select(oldSelectionGroup);
        assert selection.isValid(fxomDocument);
    }

    @Override
    public void doRedo() {
        selection.select(newSelectionGroup);
        assert selection.isValid(fxomDocument);
    }

    @Override
    public String getDescription() {
        // Not expected to reach the user
        return getClass().getSimpleName();
    }

    @Singleton
    public static class Factory extends JobFactory<UpdateSelectionJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  UpdateSelectionJob} job
         * @param group the selection group to select
         * @return the job to execute
         */
        public UpdateSelectionJob getJob(SelectionGroup group) {
            return create(UpdateSelectionJob.class, j -> j.setJobParameters(group));
        }

//        /**
//         * Create an {@link  UpdateSelectionJob} job
//         * @param newSelectedObjects the objects {@link FXOMObject} to select
//         * @return the job to execute
//         */
//        public UpdateSelectionJob getJob(Collection<? extends OMObject> newSelectedObjects) {
//            return create(UpdateSelectionJob.class, j -> j.setJobParameters(newSelectedObjects));
//        }
//
//        public UpdateSelectionJob getJob(OMObject fxomObject) {
//            return create(UpdateSelectionJob.class, j -> j.setJobParameters(List.of(fxomObject)));
//        }
    }
}
