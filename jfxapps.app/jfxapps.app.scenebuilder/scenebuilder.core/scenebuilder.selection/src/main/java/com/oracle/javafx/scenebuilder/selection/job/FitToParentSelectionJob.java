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
package com.oracle.javafx.scenebuilder.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.internal.FitToParentObjectJob;

import javafx.scene.layout.AnchorPane;

/**
 * Only if parent object is {@link AnchorPane}
 * Force the selected objects {@link FXOMObject} to the same size of the parent {@link AnchorPane}
 * Subjob {@link FitToParentObjectJob}
 */
@Prototype
public final class FitToParentSelectionJob extends BatchDocumentJob {

    private static Logger logger = LoggerFactory.getLogger(FitToParentSelectionJob.class);

    private final Selection selection;
    private final FitToParentObjectJob.Factory fitToParentObjectJobFactory;

 // @formatter:off
    protected FitToParentSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            FitToParentObjectJob.Factory fitToParentObjectJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
        this.fitToParentObjectJobFactory = fitToParentObjectJobFactory;
    }

    public void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        final Set<FXOMInstance> candidates = new HashSet<>();

        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            for (FXOMObject fxomObject : osg.getItems()) {
                if (fxomObject instanceof FXOMInstance) {
                    candidates.add((FXOMInstance) fxomObject);
                }
            }

            for (FXOMInstance candidate : candidates) {
                final AbstractJob subJob = fitToParentObjectJobFactory.getJob(candidate);
                if (subJob.isExecutable()) {
                    result.add(subJob);
                } else {
                    // Should we do best effort here ?
                    // Do not clear subJobs list but keep only executable ones
                    result.clear(); // -> isExecutable() will return false
                    break;
                }
            }
        } else {
            logger.warn("Not implemented for : " + selection.getGroup());
//            assert selection.getGroup() == null :
//                    "Add implementation for " + selection.getGroup();
        }


        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        switch (getSubJobs().size()) {
            case 0:
                result = "Unexecutable Fit To Parent"; // NO18N
                break;
            case 1:
                result = getSubJobs().get(0).getDescription();
                break;
            default:
                result = makeMultipleSelectionDescription();
                break;
        }
        return result;
    }

    private String makeMultipleSelectionDescription() {
        final StringBuilder result = new StringBuilder();
        result.append("Fit To Parent ");
        result.append(getSubJobs().size());
        result.append(" Objects");
        return result.toString();
    }
}