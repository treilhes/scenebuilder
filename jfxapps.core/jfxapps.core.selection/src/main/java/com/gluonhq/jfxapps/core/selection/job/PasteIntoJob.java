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
package com.gluonhq.jfxapps.core.selection.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.TargetSelection;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.input.Clipboard;

/**
 * This job try to paste the current clipboard content (if valid {@link FXOMObject})
 * into the main accessory of the selected object (only one item selected accepted)
 * or into the first accepting accesory of the selected object
 */
@Prototype
public final class PasteIntoJob extends BatchSelectionJob {

    private final SelectionJobsFactory selectionJobsFactory;
    private final FXOMDocument fxomDocument;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;
    private final TargetSelection<?> targetSelection;

    private List<FXOMObject> newObjects;
    private FXOMObject targetObject;

 // @formatter:off
    protected PasteIntoJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            TargetSelection<?> targetSelection,
            SelectionJobsFactory selectionJobsFactory,
            FXOMObjectMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selectionJobsFactory = selectionJobsFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.targetSelection = targetSelection;
    }

    public void setJobParameters() {
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (fxomDocument != null) {

            // Retrieve the FXOMObjects from the clipboard
            final ClipboardDecoder clipboardDecoder = new ClipboardDecoder(Clipboard.getSystemClipboard());
            newObjects = clipboardDecoder.decode(fxomDocument);
            assert newObjects != null; // But possible empty

            if (newObjects.isEmpty()) {
                //nothing to paste = nothing to do
                return result;
            }

            // Retrieve the target FXOMObject

            final Selection selection = getSelection();

            // Retrieve the target FXOMObject :
            // If the document is empty (root object is null), then the target
            // object is null.
            // If the selection is root or is empty, the target object is
            // the root object.
            // Otherwise, the target object is the selection common ancestor.
            if (fxomDocument.getFxomRoot() == null) {
                targetObject = null;
            } else {
                final FXOMObject rootObject = fxomDocument.getFxomRoot();
                if (selection.isEmpty() || selection.isSelected(rootObject)) {
                    targetObject = rootObject;
                } else {
                    targetObject = selection.getAncestor();
                }
            }
            assert (targetObject != null) || (fxomDocument.getFxomRoot() == null);

            if (targetObject == null) {
                // Document is empty : only one object can be inserted
                if (newObjects.size() == 1) {
                    final FXOMObject newObject0 = newObjects.get(0);
                    final Job subJob = selectionJobsFactory.setDocumentRoot(newObject0);
                    result.add(subJob);
                    return result;
                }
            }

            if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
                final Set<FXOMObject> selectedItems = osg.getItems();

                // Single target selection
                if (selectedItems.size() == 1) {
                    final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);

                    // get user selected target
                    Accessory targetAccessory = targetSelection.getTargetAccessory();

                    if (targetAccessory == null) {
                        // no explicit target, so use main in first place
                        targetAccessory = targetMask.getMainAccessory();

                        if (!targetMask.isAcceptingAccessory(targetAccessory, newObjects)) {
                            // either main accessory is null or objects not accepted
                            // find a new valid accessory
                            List<Accessory> accessories = targetMask.getAccessories();
                            for (Accessory a : accessories) {
                                if (targetMask.isAcceptingAccessory(a, newObjects)) {
                                    targetAccessory = a;
                                    break;
                                }
                            }
                        }
                    }

                    if (targetAccessory != null) {

                        for (FXOMObject newObject : newObjects) {
                            final Job subJob = selectionJobsFactory.insertAsAccessory(newObject, targetObject,
                                    targetAccessory, targetMask.getSubComponentCount(targetAccessory, true));
                            result.add(0, subJob);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;

        if (newObjects.size() == 1) {
            result = makeSingleSelectionDescription();
        } else {
            result = makeMultipleSelectionDescription();
        }

        return result;
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        assert newObjects != null; // But possibly empty
        if (newObjects.isEmpty()) {
            return null;
        } else {
            return objectSelectionGroupFactory.getGroup(newObjects, newObjects.iterator().next(), null);
        }
    }

    /*
     * Private
     */
    private String makeSingleSelectionDescription() {
        final String result;

        assert newObjects.size() == 1;
        final FXOMObject newObject = newObjects.get(0);
        if (newObject instanceof FXOMInstance) {
            final Object sceneGraphObject = newObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                result = I18N.getString("label.action.edit.paste.into.1", sceneGraphObject.getClass().getSimpleName());
            } else {
                result = I18N.getString("label.action.edit.paste.into.unresolved");
            }
        } else if (newObject instanceof FXOMCollection) {
            result = I18N.getString("label.action.edit.paste.into.collection");
        } else {
            assert false;
            result = I18N.getString("label.action.edit.paste.into.1", newObject.getClass().getSimpleName());
        }

        return result;
    }

    private String makeMultipleSelectionDescription() {
        final int objectCount = newObjects.size();
        return I18N.getString("label.action.edit.paste.into.n", objectCount);
    }

}
