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
import java.util.Collection;
import java.util.List;

import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

/**
 * Main class used for the wrap jobs using the new container SUB COMPONENT
 * property.
 */
public abstract class AbstractWrapInSubComponentJob extends AbstractWrapInJob {

    private final FXOMObjectMask.Factory designMaskFactory;
    private final FxomJobsFactory fxomJobsFactory;

    //@formatter:off
    public AbstractWrapInSubComponentJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            FXOMObjectMask.Factory designMaskFactory,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        //@formatter:on
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, fxomJobsFactory,
                selectionJobsFactory, objectSelectionGroupFactory);
        this.designMaskFactory = designMaskFactory;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    @Override
    protected List<Job> wrapChildrenJobs(final List<FXOMObject> children) {

        final List<Job> jobs = new ArrayList<>();

        final var newContainerMask = designMaskFactory.getMask(newContainer);
        assert newContainerMask.hasMainAccessory();

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName
                = newContainerMask.getMainAccessory().getName();
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Sort the children before adding them to their new container
        final Collection<FXOMObject> sorted = sortChildren(children);
        // Add the children to the new container
        jobs.addAll(addChildrenJobs(newContainerProperty, sorted));

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final Job addPropertyJob = fxomJobsFactory.addProperty(newContainerProperty,newContainer,-1);
        jobs.add(addPropertyJob);

        return jobs;
    }

    protected Collection<FXOMObject> sortChildren(List<FXOMObject> children) {
        return children;
    }
}
