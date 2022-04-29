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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ToggleFxRootJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.tools.mask.BorderPaneHierarchyMask;

import javafx.scene.layout.BorderPane;

/**
 * Job used to wrap selection in a BorderPane. Will use the CENTER property.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class WrapInBorderPaneJob extends AbstractWrapInJob {

    private final AddPropertyJob.Factory addPropertyJobFactory;
    private final BorderPaneHierarchyMask.Factory borderPaneMaskFactory;

    public WrapInBorderPaneJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            DesignHierarchyMask.Factory designMaskFactory,
            BorderPaneHierarchyMask.Factory borderPaneMaskFactory,
            Metadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, objectSelectionGroupFactory);
        this.addPropertyJobFactory = addPropertyJobFactory;
        this.borderPaneMaskFactory = borderPaneMaskFactory;
        newContainerClass = BorderPane.class;
    }

    @Override
    protected boolean canWrapIn() {
        final boolean result;
        if (super.canWrapIn()) { // (1)
            // Can wrap in CENTER property single selection only
            final Selection selection = getSelection();
            assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            result = osg.getItems().size() == 1;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    protected List<AbstractJob> wrapChildrenJobs(final List<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();

        final BorderPaneHierarchyMask newContainerMask = borderPaneMaskFactory.getMask(newContainer);

        assert newContainerMask.isAcceptingAccessory(newContainerMask.getCenterAccessory());

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName = newContainerMask.getCenterAccessory().getName();

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
        final AbstractJob addPropertyJob = addPropertyJobFactory.getJob(newContainerProperty, newContainer, -1);
        jobs.add(addPropertyJob);

        return jobs;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<WrapInBorderPaneJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInBorderPaneJob} job
         * @return the job to execute
         */
        public WrapInBorderPaneJob getJob() {
            return create(WrapInBorderPaneJob.class, null);
        }
    }

}
