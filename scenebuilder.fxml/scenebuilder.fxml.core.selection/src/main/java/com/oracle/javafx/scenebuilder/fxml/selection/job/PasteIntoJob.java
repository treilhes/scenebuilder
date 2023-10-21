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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardDecoder;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.Node;
import javafx.scene.input.Clipboard;

/**
 * This job try to paste the current clipboard content (if valid {@link FXOMObject})
 * into the main accessory of the selected object (only one item selected accepted)
 * or into the first accepting accesory of the selected object
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class PasteIntoJob extends BatchSelectionJob {

    private List<FXOMObject> newObjects;
    private final FXOMDocument fxomDocument;
    private final InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;
    private final JobManager jobManager;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final RelocateNodeJob.Factory relocateNodeJobFactory;

    private FXOMObject targetObject;
    
 // @formatter:off
    protected PasteIntoJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            Selection selection,
            JobManager jobManager,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory,
            RelocateNodeJob.Factory relocateNodeJobFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.jobManager = jobManager;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.insertAsAccessoryJobFactory = insertAsAccessoryJobFactory;
        this.relocateNodeJobFactory = relocateNodeJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

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
                    final AbstractJob subJob = setDocumentRootJobFactory.getJob(newObject0);
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
                    Accessory targetAccessory = selection.getTargetAccessory();

                    if (targetAccessory == null) {
                        // no explicit target, so use main in first place
                        targetAccessory = targetMask.getMainAccessory();

                        if (!targetMask.isAcceptingAccessory(targetAccessory, newObjects)) {
                            // either main accessory is null or objects not accepted
                            // find a new valid accessory
                            for (Accessory a : targetMask.getAccessories()) {
                                if (targetMask.isAcceptingAccessory(a, newObjects)) {
                                    targetAccessory = a;
                                    break;
                                }
                            }
                        }
                    }

                    if (targetAccessory != null) {
                        final double relocateDelta;
                        if (targetAccessory.isFreeChildPositioning()) {
                            final int pasteJobCount = countPasteJobs();
                            relocateDelta = 10.0 * (pasteJobCount + 1);
                        } else {
                            relocateDelta = 0.0;
                        }
                        
                        for (FXOMObject newObject : newObjects) {
                            final AbstractJob subJob = insertAsAccessoryJobFactory.getJob(newObject, targetObject,
                                    targetAccessory, targetMask.getSubComponentCount(targetAccessory, true));
                            
                            if ((relocateDelta != 0.0) && newObject.isNode()) {
                                final Node sceneGraphNode = (Node) newObject.getSceneGraphObject();
                                final AbstractJob relocateJob = relocateNodeJobFactory.getJob(
                                        (FXOMInstance) newObject,
                                        sceneGraphNode.getLayoutX() + relocateDelta,
                                        sceneGraphNode.getLayoutY() + relocateDelta);
                                result.add(relocateJob);
                            }
                            
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
    protected AbstractSelectionGroup getNewSelectionGroup() {
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
    
    private int countPasteJobs() {
        int result = 0;

        final List<AbstractJob> undoStack = jobManager.getUndoStack();
        for (AbstractJob job : undoStack) {
            if (job instanceof PasteIntoJob) {
                final PasteIntoJob pasteJob = (PasteIntoJob) job;
                if (this.targetObject == pasteJob.targetObject) {
                    result++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<PasteIntoJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link PasteIntoJob} job
         * @return the job to execute
         */
        public PasteIntoJob getJob() {
            return create(PasteIntoJob.class, j -> j.setJobParameters());
        }
    }


}
