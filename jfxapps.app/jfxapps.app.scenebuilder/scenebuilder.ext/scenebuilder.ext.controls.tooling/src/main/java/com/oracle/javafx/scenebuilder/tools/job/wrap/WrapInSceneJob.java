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

import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
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
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplaceObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.selection.job.SetDocumentRootJob;

/**
 * Job used to wrap selection in a Scene using its ROOT property.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class WrapInSceneJob extends AbstractWrapInJob {

    private final FXOMDocument fxomDocument;
    private final ReplaceObjectJob.Factory replaceObjectJobFactory;
    private final FXOMObjectMask.Factory designMaskFactory;

    protected WrapInSceneJob(JobExtensionFactory extensionFactory, FxmlDocumentManager documentManager,
            Selection selection, FXOMObjectMask.Factory designMaskFactory, IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            ReplaceObjectJob.Factory replaceObjectJobFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, objectSelectionGroupFactory);
        this.replaceObjectJobFactory = replaceObjectJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        newContainerClass = javafx.scene.Scene.class;
    }

    @Override
    protected boolean canWrapIn() {
        if (!super.canWrapIn()) { // (1)
            return false;
        }

        // Can wrap in ROOT property single selection only
        final Selection selection = getSelection();
        assert selection.getGroup() instanceof DSelectionGroupFactory; // Because of (1)
        final DSelectionGroupFactory osg = (DSelectionGroupFactory) selection.getGroup();
        if (osg.getItems().size() != 1) {
            return false;
        }

        // Selected object must be root object
        final FXOMObject parent = osg.getAncestor();
        if (parent != null) { // selection != root object
            return false;
        }

        return true;
    }

    @Override
    protected List<AbstractJob> wrapChildrenJobs(final List<FXOMObject> children) {
        final List<AbstractJob> jobs = new ArrayList<>();

        final HierarchyMask newContainerMask = designMaskFactory.getMask(newContainer);
        assert newContainerMask.isAcceptingAccessory(newContainerMask.getMainAccessory());

        final List<FXOMObject> containerContent = newContainerMask.getAccessories(newContainerMask.getMainAccessory(), false);

        assert !containerContent.isEmpty();

        final FXOMObject dummyPane = containerContent.get(0);
        assert dummyPane != null;

        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Replace the dummyPane with the new child
        assert children.size() == 1;
        final FXOMObject child = children.iterator().next();
        jobs.add(replaceObjectJobFactory.getJob(dummyPane, child));

        return jobs;
    }

    @Override
    protected FXOMInstance makeNewContainerInstance(final Class<?> containerClass) {
        assert containerClass == javafx.scene.Scene.class;
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result = new FXOMInstance(newDocument, containerClass);
        // Scenes must have a root -- add a dummy one for now
        final FXOMInstance dummyPane = new FXOMInstance(newDocument, javafx.scene.layout.Pane.class);
        final PropertyName newContainerPropertyName = new PropertyName("root"); //NOCHECK
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newDocument, newContainerPropertyName);
        dummyPane.addToParentProperty(0, newContainerProperty);
        newContainerProperty.addToParentInstance(0, result);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(fxomDocument);

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<WrapInSceneJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInSceneJob} job
         * @return the job to execute
         */
        public WrapInSceneJob getJob() {
            return create(WrapInSceneJob.class, null);
        }
    }
}
