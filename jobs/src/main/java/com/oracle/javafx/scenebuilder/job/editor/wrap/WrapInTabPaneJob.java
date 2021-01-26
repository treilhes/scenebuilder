/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.job.editor.wrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Job used to wrap selection in a TabPane.
 */
public class WrapInTabPaneJob extends AbstractWrapInJob {

    public WrapInTabPaneJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        newContainerClass = TabPane.class;
    }

    @Override
    protected boolean canWrapIn() {
        final boolean result;
        if (super.canWrapIn()) { // (1)
            // Can wrap in CONTENT property single selection only
            final Selection selection = getEditorController().getSelection();
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

        final DesignHierarchyMask newContainerMask
                = new DesignHierarchyMask(newContainer);
        assert newContainerMask.isAcceptingSubComponent();

        // Retrieve the new container property name to be used
        final PropertyName newContainerPropertyName
                = newContainerMask.getSubComponentPropertyName();
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newContainer.getFxomDocument(), newContainerPropertyName);

        // Create the Tab sub container
        final FXOMInstance tabContainer = makeNewContainerInstance(Tab.class);
        final DesignHierarchyMask tabContainerMask
                = new DesignHierarchyMask(tabContainer);
        assert tabContainerMask.isAcceptingAccessory(Accessory.CONTENT);

        // Retrieve the Tab sub container property name to be used
        final PropertyName tabContainerPropertyName
                = new PropertyName("content"); //NOI18N
        // Create the Tab sub container property
        final FXOMPropertyC tabContainerProperty = new FXOMPropertyC(
                tabContainer.getFxomDocument(), tabContainerPropertyName);

        // Add the Tab sub container to the new container
        final Job addTabValueJob = new AddPropertyValueJob(getContext(), 
                tabContainer,
                newContainerProperty,
                -1,
                getEditorController()).extend();
        jobs.add(addTabValueJob);

        assert children.size() == 1;
        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Add the children to the Tab sub container
        final List<Job> addChildrenJobs
                = addChildrenJobs(tabContainerProperty, children);
        jobs.addAll(addChildrenJobs);

        // Add the Tab sub container property to the tab container instance
        assert tabContainerProperty.getParentInstance() == null;
        final Job addTabContainerPropertyJob = new AddPropertyJob(getContext(), 
                tabContainerProperty,
                tabContainer,
                -1, getEditorController()).extend();
        jobs.add(addTabContainerPropertyJob);

        // Add the new container property to the new container instance
        assert newContainerProperty.getParentInstance() == null;
        final Job addNewContainerPropertyJob = new AddPropertyJob(getContext(), 
                newContainerProperty,
                newContainer,
                -1, getEditorController()).extend();
        jobs.add(addNewContainerPropertyJob);

        return jobs;
    }
}
