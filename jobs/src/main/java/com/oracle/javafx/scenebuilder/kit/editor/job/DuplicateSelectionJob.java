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
package com.oracle.javafx.scenebuilder.kit.editor.job;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RelocateNodeJob;

import javafx.scene.Node;

/**
 *
 */
public class DuplicateSelectionJob extends BatchSelectionJob {

    private final static double offset = 10;
    final Map<FXOMObject, FXOMObject> newFxomObjects = new LinkedHashMap<>();

    public DuplicateSelectionJob(ApplicationContext context, Editor editor) {
        super(context, editor);
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new LinkedList<>();

        if (canDuplicate()) { // (1)

            final Selection selection = getEditorController().getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            assert osg.hasSingleParent() == true; // Because of (1)
            final FXOMObject targetObject = osg.getAncestor();
            assert targetObject != null; // Because of (1)
            final FXOMDocument targetDocument = getEditorController().getFxomDocument();
            for (FXOMObject selectedObject : osg.getSortedItems()) {
                final FXOMDocument newDocument = FXOMNodes.newDocument(selectedObject);
                final FXOMObject newObject = newDocument.getFxomRoot();
                newObject.moveToFxomDocument(targetDocument);
                assert newDocument.getFxomRoot() == null;
                newFxomObjects.put(selectedObject, newObject);
            }
            assert newFxomObjects.isEmpty() == false; // Because of (1)

            // Build InsertAsSubComponent jobs
            final DesignHierarchyMask targetMask = new DesignHierarchyMask(targetObject);
            if (targetMask.isAcceptingSubComponent(newFxomObjects.keySet())) {
                int index = 0;
                for (Map.Entry<FXOMObject, FXOMObject> entry : newFxomObjects.entrySet()) {
                    final FXOMObject selectedFxomObject = entry.getKey();
                    final FXOMObject newFxomObject = entry.getValue();
                    final Job insertSubJob = new InsertAsSubComponentJob(getContext(),
                            newFxomObject,
                            targetObject,
                            targetMask.getSubComponentCount() + index++,
                            getEditorController()).extend();
                    result.add(insertSubJob);
                    final Object selectedSceneGraphObject = selectedFxomObject.getSceneGraphObject();
                    // Relocate duplicated objects if needed
                    if (selectedSceneGraphObject instanceof Node) {
                        final Node selectedNode = (Node) selectedSceneGraphObject;
                        final double newLayoutX = Math.round(selectedNode.getLayoutX() + offset);
                        final double newLayoutY = Math.round(selectedNode.getLayoutY() + offset);
                        assert newFxomObject instanceof FXOMInstance;
                        final Job relocateSubJob = new RelocateNodeJob(getContext(),
                                (FXOMInstance) newFxomObject,
                                newLayoutX,
                                newLayoutY,
                                getEditorController()).extend();
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
            return new ObjectSelectionGroup(newFxomObjects.values(), newFxomObjects.values().iterator().next(), null);
        }
    }

    private boolean canDuplicate() {
        final FXOMDocument fxomDocument = getEditorController().getFxomDocument();
        if (fxomDocument == null) {
            return false;
        }
        final Selection selection = getEditorController().getSelection();
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
}