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
package com.oracle.javafx.scenebuilder.selection.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardDecoder;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

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
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;
    private final InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

 // @formatter:off
    protected PasteIntoJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory,
            InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
        this.insertAsAccessoryJobFactory = insertAsAccessoryJobFactory;
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
            final ClipboardDecoder clipboardDecoder
                    = new ClipboardDecoder(Clipboard.getSystemClipboard());
            newObjects = clipboardDecoder.decode(fxomDocument);
            assert newObjects != null; // But possible empty

            // Retrieve the target FXOMObject
            final Selection selection = getSelection();
            if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
                // Single target selection
                if (osg.getItems().size() == 1) {
                    final FXOMObject targetObject = osg.getItems().iterator().next();

                    // Build InsertAsSubComponent jobs
                    final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);
                    if (targetMask.isAcceptingSubComponent(newObjects)) {
                        for (FXOMObject newObject : newObjects) {
                            final AbstractJob subJob = insertAsSubComponentJobFactory.getJob(newObject, targetObject, targetMask.getSubComponentCount());
                            result.add(0, subJob);
                        }
                    } // Build InsertAsAccessory jobs for single source selection
                    else if (newObjects.size() == 1) {
                        final FXOMObject newObject = newObjects.get(0);
                        //TODO need to add ordering to accessories to keep the same order as below
//                        final Accessory[] accessories = {Accessory.CONTENT,
//                            Accessory.CONTEXT_MENU, Accessory.GRAPHIC,
//                            Accessory.TOOLTIP};

                        for (Accessory a : targetMask.getAccessories()) {
                            if (targetMask.isAcceptingAccessory(a, newObject)
                                    && targetMask.getAccessory(a) == null) {
                                final AbstractJob subJob = insertAsAccessoryJobFactory.getJob(newObject, targetObject, a);
                                result.add(subJob);
                                break;
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
