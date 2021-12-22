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
package com.oracle.javafx.scenebuilder.job.editor;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardDecoder;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RelocateNodeJob;

import javafx.scene.Node;
import javafx.scene.input.Clipboard;

/**
 *
 */
public class PasteJob extends BatchSelectionJob {

    private FXOMObject targetObject;
    private List<FXOMObject> newObjects;
    private final FXOMDocument fxomDocument;

    public PasteJob(SceneBuilderBeanFactory context, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (fxomDocument != null) {

            // Retrieve the FXOMObjects from the clipboard
            final ClipboardDecoder clipboardDecoder
                    = new ClipboardDecoder(Clipboard.getSystemClipboard());
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
                    final Selection selection = getEditorController().getSelection();
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
                        final Job subJob = new SetDocumentRootJob(getContext(),
                                newObject0,
                                getEditorController()).extend();
                        result.add(subJob);
                    }
                } else {
                    // Build InsertAsSubComponent jobs
                    final DesignHierarchyMask targetMask = new DesignHierarchyMask(targetObject);
                    if (targetMask.isAcceptingSubComponent(newObjects)) {

                        final double relocateDelta;
                        if (targetMask.getMainAccessory() != null && targetMask.getMainAccessory().isFreeChildPositioning()) {
                            final int pasteJobCount = countPasteJobs();
                            relocateDelta = 10.0 * (pasteJobCount + 1);
                        } else {
                            relocateDelta = 0.0;
                        }
                        for (FXOMObject newObject : newObjects) {
                            final Job subJob = new InsertAsSubComponentJob(getContext(),
                                    newObject,
                                    targetObject,
                                    targetMask.getSubComponentCount(),
                                    getEditorController()).extend();
                            result.add(0, subJob);
                            if ((relocateDelta != 0.0) && newObject.isNode()) {
                                final Node sceneGraphNode = (Node) newObject.getSceneGraphObject();
                                final Job relocateJob = new RelocateNodeJob(getContext(),
                                        (FXOMInstance) newObject,
                                        sceneGraphNode.getLayoutX() + relocateDelta,
                                        sceneGraphNode.getLayoutY() + relocateDelta,
                                        getEditorController()
                                ).extend();
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
            return new ObjectSelectionGroup(newObjects, newObjects.iterator().next(), null);
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

        final List<Job> undoStack = getEditorController().getJobManager().getUndoStack();
        for (Job job : undoStack) {
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
}
