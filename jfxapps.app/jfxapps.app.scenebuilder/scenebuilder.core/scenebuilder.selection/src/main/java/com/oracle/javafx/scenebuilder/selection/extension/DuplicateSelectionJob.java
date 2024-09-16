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
package com.oracle.javafx.scenebuilder.selection.extension;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;

import javafx.scene.Node;

/**
 * Duplicate all object in the current selection
 */
@Prototype
public final class DuplicateSelectionJob extends BatchSelectionJob {

    private final static double offset = 10;

    private final I18N i18n;
    private final SbJobsFactory sbJobsFactory;
    private final SelectionJobsFactory selectionJobsFactory;
    private final FXOMObjectMask.Factory fxomObjectMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    private final Map<FXOMObject, FXOMObject> newFxomObjects = new LinkedHashMap<>();
    private final FXOMDocument fxomDocument;

    // @formatter:off
    protected DuplicateSelectionJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory,
            SbJobsFactory sbJobsFactory,
            FXOMObjectMask.Factory fxomObjectMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory
            ) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.i18n = i18n;
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selectionJobsFactory = selectionJobsFactory;
        this.sbJobsFactory = sbJobsFactory;
        this.fxomObjectMaskFactory = fxomObjectMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    public void setJobParameters() {
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new LinkedList<>();

        if (canDuplicate()) { // (1)

            final SelectionGroup asg = getSelection().getGroup();
            assert asg instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            assert osg.hasSingleParent() == true; // Because of (1)
            final FXOMObject targetObject = osg.getAncestor();
            assert targetObject != null; // Because of (1)
            final FXOMDocument targetDocument = fxomDocument;

            // Build jobs
            final var targetMask = fxomObjectMaskFactory.getMask(targetObject);
            Accessory<?> targetAccessory = null;

            for (FXOMObject selectedObject : osg.getSortedItems()) {
                if (targetAccessory == null) {
                    targetAccessory = targetMask.getAccessoryOf(selectedObject);
                }
                final FXOMDocument newDocument = FXOMNodes.newDocument(selectedObject);
                final FXOMObject newObject = newDocument.getFxomRoot();
                newObject.moveToFxomDocument(targetDocument);
                assert newDocument.getFxomRoot() == null;
                newFxomObjects.put(selectedObject, newObject);
            }
            assert newFxomObjects.isEmpty() == false; // Because of (1)



            if (targetMask.isAcceptingAccessory(targetAccessory, newFxomObjects.keySet())) {
                int index = 0;
                for (Map.Entry<FXOMObject, FXOMObject> entry : newFxomObjects.entrySet()) {
                    final FXOMObject selectedFxomObject = entry.getKey();
                    final FXOMObject newFxomObject = entry.getValue();
                    final Job insertSubJob = selectionJobsFactory.insertAsAccessory(
                            newFxomObject,
                            targetObject,
                            targetAccessory,
                            targetMask.getSubComponentCount(targetAccessory,true) + index++);

                    result.add(insertSubJob);
                    final Object selectedSceneGraphObject = selectedFxomObject.getSceneGraphObject().get();
                    // Relocate duplicated objects if needed
                    if (selectedSceneGraphObject instanceof Node) {
                        final Node selectedNode = (Node) selectedSceneGraphObject;
                        final double newLayoutX = Math.round(selectedNode.getLayoutX() + offset);
                        final double newLayoutY = Math.round(selectedNode.getLayoutY() + offset);
                        assert newFxomObject instanceof FXOMInstance;
                        final Job relocateSubJob = sbJobsFactory.relocateNode(
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
    protected SelectionGroup getNewSelectionGroup() {
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
        final SelectionGroup asg = selection.getGroup();
        if ((asg instanceof ObjectSelectionGroup) == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        for (FXOMObject fxomObject : osg.getItems()) {
            if (fxomObject.getSceneGraphObject().isEmpty()) { // Unresolved custom type
                return false;
            }
        }
        if (!osg.hasSingleParent()) {
            return false;
        }

        // parent property must be a collection
        final var firstFxomObject = osg.getItems().iterator().next();
        final var mask = fxomObjectMaskFactory.getMask(firstFxomObject.getParentObject());
        final var propertyMetadata = mask.getAccessoryOf(firstFxomObject);
        if (!propertyMetadata.isCollection()) {
            return false;
        }
        return true;
    }

    private String makeSingleSelectionDescription() {
        final String result;

        final FXOMObject newObject = newFxomObjects.values().iterator().next();
        if (newObject instanceof FXOMInstance) {
            final Object sceneGraphObject = newObject.getSceneGraphObject().get();
            if (sceneGraphObject != null) {
                result = i18n.getString("label.action.edit.duplicate.1", sceneGraphObject.getClass().getSimpleName());
            } else {
                result = i18n.getString("label.action.edit.duplicate.unresolved");
            }
        } else if (newObject instanceof FXOMCollection) {
            result = i18n.getString("label.action.edit.duplicate.collection");
        } else {
            assert false;
            result = i18n.getString("label.action.edit.duplicate.1", newObject.getClass().getSimpleName());
        }

        return result;
    }

    private String makeMultipleSelectionDescription() {
        return i18n.getString("label.action.edit.duplicate.n", newFxomObjects.values().size());
    }

}
