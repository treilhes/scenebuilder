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
package com.gluonhq.jfxapps.core.selection.job;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.selection.ObjectSelectionGroup;

/**
 * This job pull the selected {@link FXOMObject} objects at the top in the
 * parent collection (index==0)
 */
@Prototype
public final class SendToBackJob extends InlineDocumentJob {

    private final Selection selection;
    private final FxomJobsFactory fxomJobsFactory;

    // @formatter:off
    protected SendToBackJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            FxomJobsFactory fxomJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters() {
    }

    @Override
    public boolean isExecutable() {

        if (selection.getGroup() instanceof ObjectSelectionGroup == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
        for (FXOMObject item : osg.getSortedItems()) {
            final FXOMObject previousSlibing = item.getPreviousSlibing();
            if (previousSlibing == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {

        assert isExecutable(); // (1)
        final List<Job> result = new ArrayList<>();

        assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
        final List<FXOMObject> candidates = osg.getSortedItems();

        for (int i = candidates.size() - 1; i >= 0; i--) {
            final FXOMObject candidate = candidates.get(i);
            final FXOMObject previousSlibing = candidate.getPreviousSlibing();
            if (previousSlibing != null) {
                final FXOMProperty parentProperty = candidate.getParentProperty();
                final FXOMObject beforeChild = parentProperty.getChildren().get(0);
                final Job subJob = fxomJobsFactory.reIndexObject(candidate, beforeChild);
                if (subJob.isExecutable()) {
                    subJob.execute();
                    result.add(subJob);
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        switch (getSubJobs().size()) {
        case 0:
            result = "Unexecutable Send To Back"; // NO18N
            break;
        case 1: // one arrange Z order
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
        result.append("Send To Back ");
        result.append(getSubJobs().size());
        result.append(" Objects");
        return result.toString();
    }

}
