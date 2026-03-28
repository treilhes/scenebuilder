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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.core.api.fxom.editor.selection.FxomSelection;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.jobs.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;

import javafx.scene.control.DialogPane;

/**
 * Job used to wrap selection in a DialogPane. Will use the CONTENT property.
 */
@ApplicationInstancePrototype
public final class WrapInDialogPaneJob extends AbstractWrapInJob {

    private final FxomJobsFactory fxomJobsFactory;
    private final SbFXOMObjectMask.Factory designMaskFactory;

    //@formatter:off
    public WrapInDialogPaneJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            FxomSelection selection,
            SbMetadata metadata,
            SbFXOMObjectMask.Factory designMaskFactory,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        //@formatter:on
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, fxomJobsFactory,
                selectionJobsFactory, objectSelectionGroupFactory);
        this.fxomJobsFactory = fxomJobsFactory;
        this.designMaskFactory = designMaskFactory;
        newContainerClass = DialogPane.class;
    }

    @Override
    protected boolean canWrapIn() {
        final boolean result;
        if (super.canWrapIn()) { // (1)
            // Can wrap in CONTENT property single selection only
            final var selection = getSelection();
            assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            result = osg.getItems().size() == 1;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    protected List<Job> wrapChildrenJobs(final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();

        final var newContainerMask = designMaskFactory.getMask(newContainer);
        assert newContainerMask.isAcceptingAccessory(newContainerMask.getMainAccessory());

        // Retrieve the new container property name to be used
        final var newContainerPropertyName = newContainerMask.getMainAccessory().getName();

        // Create the new container property
        final var newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        assert children.size() == 1;
        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Add the children to the new container
        jobs.addAll(addChildrenJobs(newContainerProperty, children));

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final var addPropertyJob = fxomJobsFactory.addProperty(newContainerProperty, newContainer, -1);
        jobs.add(addPropertyJob);

        return jobs;
    }

    @ApplicationInstanceSingleton
    public final static class Factory extends JobFactory<WrapInDialogPaneJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInDialogPaneJob} job
         * @return the job to execute
         */
        public WrapInDialogPaneJob getJob() {
            return create(WrapInDialogPaneJob.class, null);
        }
    }
}
