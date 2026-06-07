/*
 * Copyright (c) 2016, 2026, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2026, Pascal Treilhes and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.fxom.api.editor.selection.FxomSelection;
import com.treilhes.jfxplace.fxom.api.editor.selection.ObjectSelectionGroup;
import com.treilhes.jfxplace.fxom.api.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.fxom.api.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMObject;
import com.treilhes.jfxplace.fxom.model.FXOMPropertyC;
import com.treilhes.jfxplace.fxom.model.util.PropertyName;

/**
 * Main class used for the wrap jobs using the new container CONTENT property.
 */
public abstract class AbstractWrapInContentJob extends AbstractWrapInJob {



    private final SbFXOMObjectMask.Factory designMaskFactory;
    private final FxomJobsFactory fxomJobsFactory;

    //@formatter:off
    public AbstractWrapInContentJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            FxomSelection selection,
            SbFXOMObjectMask.Factory designMaskFactory,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        //@formatter:on
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, fxomJobsFactory,
                selectionJobFactory, objectSelectionGroupFactory);
        this.designMaskFactory = designMaskFactory;
        this.fxomJobsFactory = fxomJobsFactory;
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
        final PropertyName newContainerPropertyName = newContainerMask.getMainAccessory().getName();

        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        assert children.size() == 1;
        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Add the children to the new container
        jobs.addAll(addChildrenJobs(newContainerProperty, children));

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final var addPropertyJob = fxomJobsFactory.addProperty(newContainerProperty,newContainer,-1);
        jobs.add(addPropertyJob);

        return jobs;
    }
}
