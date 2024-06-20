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

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.fxml.selection.job.SetDocumentRootJob;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Job used to wrap selection in a TabPane.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class WrapInTabPaneJob extends AbstractWrapInJob {

    private final AddPropertyJob.Factory addPropertyJobFactory;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;

    protected WrapInTabPaneJob(JobExtensionFactory extensionFactory, FxmlDocumentManager documentManager,
            Selection selection, FXOMObjectMask.Factory designMaskFactory, IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, objectSelectionGroupFactory);
        this.addPropertyJobFactory = addPropertyJobFactory;
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.designMaskFactory = designMaskFactory;
        newContainerClass = TabPane.class;
    }

    @Override
    protected boolean canWrapIn() {
        final boolean result;
        if (super.canWrapIn()) { // (1)
            // Can wrap in CONTENT property single selection only
            final Selection selection = getSelection();
            assert selection.getGroup() instanceof DSelectionGroupFactory; // Because of (1)
            final DSelectionGroupFactory osg = (DSelectionGroupFactory) selection.getGroup();
            result = osg.getItems().size() == 1;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    protected List<AbstractJob> wrapChildrenJobs(final List<FXOMObject> children) {

        final List<AbstractJob> jobs = new ArrayList<>();

        final HierarchyMask newContainerMask = designMaskFactory.getMask(newContainer);
        assert newContainerMask.hasMainAccessory();

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName
                = newContainerMask.getMainAccessory().getName();
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        // Create the Tab sub container
        final FXOMInstance tabContainer = makeNewContainerInstance(Tab.class);
        final HierarchyMask tabContainerMask = designMaskFactory.getMask(tabContainer);
        assert tabContainerMask.isAcceptingAccessory(tabContainerMask.getMainAccessory());

        // Retrieve the Tab sub container property name to be used
        final PropertyName tabContainerPropertyName = tabContainerMask.getMainAccessory().getName();

        // Create the Tab sub container property
        final FXOMPropertyC tabContainerProperty = new FXOMPropertyC(
                tabContainer.getFxomDocument(), tabContainerPropertyName);

        // Add the Tab sub container to the new container
        final AbstractJob addTabValueJob = addPropertyValueJobFactory.getJob(tabContainer, newContainerProperty, -1);
        jobs.add(addTabValueJob);

        assert children.size() == 1;
        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Add the children to the Tab sub container
        final List<AbstractJob> addChildrenJobs
                = addChildrenJobs(tabContainerProperty, children);
        jobs.addAll(addChildrenJobs);

        // Add the Tab sub container property to the tab container instance
        assert tabContainerProperty.getParentInstance() == null;
        final AbstractJob addTabContainerPropertyJob = addPropertyJobFactory.getJob(tabContainerProperty,tabContainer,-1);
        jobs.add(addTabContainerPropertyJob);

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final AbstractJob addNewContainerPropertyJob = addPropertyJobFactory.getJob(newContainerProperty, newContainer, -1);
        jobs.add(addNewContainerPropertyJob);

        return jobs;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<WrapInTabPaneJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInTabPaneJob} job
         * @return the job to execute
         */
        public WrapInTabPaneJob getJob() {
            return create(WrapInTabPaneJob.class, null);
        }
    }
}
