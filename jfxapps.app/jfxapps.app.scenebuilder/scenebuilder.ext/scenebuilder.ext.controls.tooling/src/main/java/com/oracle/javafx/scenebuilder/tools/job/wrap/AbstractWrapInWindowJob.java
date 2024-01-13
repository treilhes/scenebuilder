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

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.fxml.selection.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ToggleFxRootJob;

/**
 * Main class used for the wrap jobs using the new window's SCENE property.
 */
public abstract class AbstractWrapInWindowJob extends AbstractWrapInJob {
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final AddPropertyJob.Factory addPropertyJobFactory;

    public AbstractWrapInWindowJob(JobExtensionFactory extensionFactory, FxmlDocumentManager documentManager,
            Selection selection, DesignHierarchyMask.Factory designMaskFactory, IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, objectSelectionGroupFactory);
        this.designMaskFactory = designMaskFactory;
        this.addPropertyJobFactory = addPropertyJobFactory;
    }

    @Override
    protected boolean canWrapIn() {
        final Selection selection = getSelection();
        if (selection.isEmpty()) {
            return false;
        }

        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof DefaultSelectionGroupFactory) == false) {
            return false;
        }

        final DefaultSelectionGroupFactory osg = (DefaultSelectionGroupFactory) asg;
        if (osg.hasSingleParent() == false) {
            return false;
        }

        // Can wrap in SCENE property single selection only
        if (osg.getItems().size() != 1) {
            return false;
        }

        // Selected object must be an instance of javafx.scene.Scene
        for (FXOMObject fxomObject : osg.getItems()) {
            if ((fxomObject.getSceneGraphObject() instanceof javafx.scene.Scene) == false) {
                return false;
            }
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

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName = newContainerMask.getMainAccessory().getName();
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(newContainer.getFxomDocument(),
                newContainerPropertyName);

        assert children.size() == 1;

        // Add the children to the new container
        jobs.addAll(addChildrenJobs(newContainerProperty, children));

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final AbstractJob addPropertyJob = addPropertyJobFactory.getJob(newContainerProperty, newContainer, -1);
        jobs.add(addPropertyJob);

        return jobs;
    }
}