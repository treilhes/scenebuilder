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
package com.oracle.javafx.scenebuilder.fxml.selection.job;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.job.editor.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.Node;

/**
 * Duplicate all object in the current selection
 * See {@link InsertAsSubComponentJob}
 * See {@link RelocateNodeJob}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DuplicateSelectionJob extends BatchSelectionJob {

    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;
    private final RelocateNodeJob.Factory relocateNodeJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    private final static double offset = 10;
    final Map<FXOMObject, FXOMObject> newFxomObjects = new LinkedHashMap<>();
    private final FXOMDocument fxomDocument;

    // @formatter:off
    protected DuplicateSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            Selection selection,
            DesignHierarchyMask.Factory designMaskFactory,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory,
            RelocateNodeJob.Factory relocateNodeJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory
            ) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
        this.relocateNodeJobFactory = relocateNodeJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new LinkedList<>();

        if (canDuplicate()) { // (1)

            final AbstractSelectionGroup asg = getSelection().getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            assert osg.hasSingleParent() == true; // Because of (1)
            final FXOMObject targetObject = osg.getAncestor();
            assert targetObject != null; // Because of (1)
            final FXOMDocument targetDocument = fxomDocument;
            for (FXOMObject selectedObject : osg.getSortedItems()) {
                final FXOMDocument newDocument = FXOMNodes.newDocument(selectedObject);
                final FXOMObject newObject = newDocument.getFxomRoot();
                newObject.moveToFxomDocument(targetDocument);
                assert newDocument.getFxomRoot() == null;
                newFxomObjects.put(selectedObject, newObject);
            }
            assert newFxomObjects.isEmpty() == false; // Because of (1)

            // Build InsertAsSubComponent jobs
            final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);
            if (targetMask.isAcceptingSubComponent(newFxomObjects.keySet())) {
                int index = 0;
                for (Map.Entry<FXOMObject, FXOMObject> entry : newFxomObjects.entrySet()) {
                    final FXOMObject selectedFxomObject = entry.getKey();
                    final FXOMObject newFxomObject = entry.getValue();
                    final AbstractJob insertSubJob = insertAsSubComponentJobFactory.getJob(
                            newFxomObject,
                            targetObject,
                            targetMask.getSubComponentCount(true) + index++);

                    result.add(insertSubJob);
                    final Object selectedSceneGraphObject = selectedFxomObject.getSceneGraphObject();
                    // Relocate duplicated objects if needed
                    if (selectedSceneGraphObject instanceof Node) {
                        final Node selectedNode = (Node) selectedSceneGraphObject;
                        final double newLayoutX = Math.round(selectedNode.getLayoutX() + offset);
                        final double newLayoutY = Math.round(selectedNode.getLayoutY() + offset);
                        assert newFxomObject instanceof FXOMInstance;
                        final AbstractJob relocateSubJob = relocateNodeJobFactory.getJob(
                                (FXOMInstance) newFxomObject,
                                newLayoutX,
                                newLayoutY
                                );
                        result.add(relocateSubJob);
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        assert newFxomObjects.values().isEmpty() == false;
        if (newFxomObjects.values().size() == 1) {
            result = makeSingleSelectionDescription();
        } else {
            result = makeMultipleSelectionDescription();
        }

        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        assert newFxomObjects != null; // But possibly empty
        if (newFxomObjects.isEmpty()) {
            return null;
        } else {
            return objectSelectionGroupFactory.getGroup(newFxomObjects.values(), newFxomObjects.values().iterator().next(), null);
        }
    }

    private boolean canDuplicate() {
        if (fxomDocument == null) {
            return false;
        }
        final Selection selection = getSelection();
        if (selection.isEmpty()) {
            return false;
        }
        final FXOMObject rootObject = fxomDocument.getFxomRoot();
        if (selection.isSelected(rootObject)) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if ((asg instanceof ObjectSelectionGroup) == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        for (FXOMObject fxomObject : osg.getItems()) {
            if (fxomObject.getSceneGraphObject() == null) { // Unresolved custom type
                return false;
            }
        }
        return osg.hasSingleParent() == true;
    }

    private String makeSingleSelectionDescription() {
        final String result;

        final FXOMObject newObject = newFxomObjects.values().iterator().next();
        if (newObject instanceof FXOMInstance) {
            final Object sceneGraphObject = newObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                result = I18N.getString("label.action.edit.duplicate.1", sceneGraphObject.getClass().getSimpleName());
            } else {
                result = I18N.getString("label.action.edit.duplicate.unresolved");
            }
        } else if (newObject instanceof FXOMCollection) {
            result = I18N.getString("label.action.edit.duplicate.collection");
        } else {
            assert false;
            result = I18N.getString("label.action.edit.duplicate.1", newObject.getClass().getSimpleName());
        }

        return result;
    }

    private String makeMultipleSelectionDescription() {
        return I18N.getString("label.action.edit.duplicate.n", newFxomObjects.values().size());
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<DuplicateSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  DuplicateSelectionJob} job
         * @return the job to execute
         */
        public DuplicateSelectionJob getJob() {
            return create(DuplicateSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
