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
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.job.editor.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardDecoder;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.Node;
import javafx.scene.input.Clipboard;

/**
 * This job try to paste the current clipboard content (if valid {@link FXOMObject})
 * into the main accessory of the selected objects common ancestor
 * or into the first accepting accesory of the selected object
 *
 * @deprecated in favor of {@link PasteIntoJob}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Deprecated(forRemoval = true)
public final class PasteJob extends BatchSelectionJob {

    private FXOMObject targetObject;
    private List<FXOMObject> newObjects;
    private final FXOMDocument fxomDocument;
    private final JobManager jobManager;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;
    private final RelocateNodeJob.Factory relocateNodeJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

 // @formatter:off
    protected PasteJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            Selection selection,
            JobManager jobManager,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory,
            RelocateNodeJob.Factory relocateNodeJobFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.jobManager = jobManager;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
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

            if (newObjects.isEmpty() == false) {

                // Retrieve the target FXOMObject :
                // If the document is empty (root object is null), then the target
                // object is null.
                // If the selection is root or is empty, the target object is
                // the root object.
                // Otherwise, the target object is the selection common ancestor.
                if (fxomDocument.getFxomRoot() == null) {
                    targetObject = null;
                } else {
                    final Selection selection = getSelection();
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
                    }
                } else {
                    // Build InsertAsSubComponent jobs
                    final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);
                    if (targetMask.isAcceptingSubComponent(newObjects)) {

                        final double relocateDelta;
                        if (targetMask.getMainAccessory() != null && targetMask.getMainAccessory().isFreeChildPositioning()) {
                            final int pasteJobCount = countPasteJobs();
                            relocateDelta = 10.0 * (pasteJobCount + 1);
                        } else {
                            relocateDelta = 0.0;
                        }
                        for (FXOMObject newObject : newObjects) {
                            final AbstractJob subJob = insertAsSubComponentJobFactory.getJob(newObject,targetObject,targetMask.getSubComponentCount(true));
                            result.add(0, subJob);
                            if ((relocateDelta != 0.0) && newObject.isNode()) {
                                final Node sceneGraphNode = (Node) newObject.getSceneGraphObject();
                                final AbstractJob relocateJob = relocateNodeJobFactory.getJob(
                                        (FXOMInstance) newObject,
                                        sceneGraphNode.getLayoutX() + relocateDelta,
                                        sceneGraphNode.getLayoutY() + relocateDelta);
                                result.add(relocateJob);
                            }
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
                result = I18N.getString("label.action.edit.paste.1", sceneGraphObject.getClass().getSimpleName());
            } else {
                result = I18N.getString("label.action.edit.paste.unresolved");
            }
        } else if (newObject instanceof FXOMCollection) {
            result = I18N.getString("label.action.edit.paste.collection");
        } else {
            assert false;
            result = I18N.getString("label.action.edit.paste.1", newObject.getClass().getSimpleName());
        }

        return result;
    }

    private String makeMultipleSelectionDescription() {
        final int objectCount = newObjects.size();
        return I18N.getString("label.action.edit.paste.n", objectCount);
    }

    private int countPasteJobs() {
        int result = 0;

        final List<AbstractJob> undoStack = jobManager.getUndoStack();
        for (AbstractJob job : undoStack) {
            if (job instanceof PasteJob) {
                final PasteJob pasteJob = (PasteJob) job;
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
    public final static class Factory extends JobFactory<PasteJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link PasteJob} job
         * @return the job to execute
         */
        public PasteJob getJob() {
            return create(PasteJob.class, j -> j.setJobParameters());
        }
    }
}
