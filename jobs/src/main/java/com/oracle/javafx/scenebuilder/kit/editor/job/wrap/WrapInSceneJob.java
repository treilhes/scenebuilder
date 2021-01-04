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
package com.oracle.javafx.scenebuilder.kit.editor.job.wrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ReplaceObjectJob;

/**
 * Job used to wrap selection in a Scene using its ROOT property.
 */
public class WrapInSceneJob extends AbstractWrapInJob {
    public WrapInSceneJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        newContainerClass = javafx.scene.Scene.class;
    }

    @Override
    protected boolean canWrapIn() {
        if (!super.canWrapIn()) { // (1)
            return false;
        }

        // Can wrap in ROOT property single selection only
        final Selection selection = getEditorController().getSelection();
        assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
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
    protected List<Job> wrapChildrenJobs(final List<FXOMObject> children) {
        final List<Job> jobs = new ArrayList<>();

        final DesignHierarchyMask newContainerMask
                = new DesignHierarchyMask(newContainer);
        assert newContainerMask.isAcceptingAccessory(DesignHierarchyMask.Accessory.ROOT);

        final FXOMObject dummyPane = newContainerMask.getAccessory(DesignHierarchyMask.Accessory.ROOT);
        assert dummyPane != null;

        // Update children before adding them to the new container
        jobs.addAll(modifyChildrenJobs(children));

        // Replace the dummyPane with the new child
        assert children.size() == 1;
        final FXOMObject child = children.iterator().next();
        jobs.add(new ReplaceObjectJob(getContext(), dummyPane, child, getEditorController()).extend());

        return jobs;
    }

    @Override
    protected FXOMInstance makeNewContainerInstance(final Class<?> containerClass) {
        assert containerClass == javafx.scene.Scene.class;
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result = new FXOMInstance(newDocument, containerClass);
        // Scenes must have a root -- add a dummy one for now
        final FXOMInstance dummyPane = new FXOMInstance(newDocument, javafx.scene.layout.Pane.class);
        final PropertyName newContainerPropertyName = new PropertyName("root"); //NOI18N
        // Create the new container property
        final FXOMPropertyC newContainerProperty = new FXOMPropertyC(
                newDocument, newContainerPropertyName);
        dummyPane.addToParentProperty(0, newContainerProperty);
        newContainerProperty.addToParentInstance(0, result);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(getEditorController().getFxomDocument());

        return result;
    }
}
