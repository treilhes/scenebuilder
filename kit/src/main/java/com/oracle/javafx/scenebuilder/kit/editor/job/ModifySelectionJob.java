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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ModifyObjectJob;

/**
 *
 */
public class ModifySelectionJob extends BatchDocumentJob {

    protected final ValuePropertyMetadata propertyMetadata;
    protected final Object newValue;

    public ModifySelectionJob(ApplicationContext context, ValuePropertyMetadata propertyMetadata,
            Object newValue, Editor editor) {
        super(context, editor);
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();
        final Set<FXOMInstance> candidates = new HashSet<>();
        final Selection selection = getEditorController().getSelection();
        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            handleObjectSelectionGroup(selection.getGroup(), candidates);
        } else if (selection.getGroup() instanceof GridSelectionGroup) {
            handleGridSelectionGroup(selection.getGroup(), candidates);
        } else {
            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup();
        }
        // Add ModifyObject jobs
        for (FXOMInstance fxomInstance : candidates) {
            final Job subJob = new ModifyObjectJob(getContext(),
                    fxomInstance, propertyMetadata, newValue, getEditorController()).extend();
            if (subJob.isExecutable()) {
                result.add(subJob);
            }
        }
        return result;
    }

    private void handleObjectSelectionGroup(AbstractSelectionGroup group, Set<FXOMInstance> candidates) {
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) group;
        for (FXOMObject fxomObject : osg.getItems()) {
            handleFxomInstance(fxomObject, candidates);
            handleFxomIntrinsic(fxomObject, candidates);
        }
    }


    private void handleFxomInstance(FXOMObject fxomObject, Set<FXOMInstance> candidates) {
        if (fxomObject instanceof FXOMInstance) {
            candidates.add((FXOMInstance) fxomObject);
        }

    }

    private void handleFxomIntrinsic(FXOMObject fxomObject, Set<FXOMInstance> candidates) {
        if(fxomObject instanceof FXOMIntrinsic) {
            FXOMIntrinsic intrinsic = (FXOMIntrinsic) fxomObject;
            FXOMInstance fxomInstance = intrinsic.createFxomInstanceFromIntrinsic();
            candidates.add(fxomInstance);
        }
    }


    private void handleGridSelectionGroup(AbstractSelectionGroup group, Set<FXOMInstance> candidates) {
        final GridSelectionGroup gsg = (GridSelectionGroup) group;
        final DesignHierarchyMask mask = new DesignHierarchyMask(gsg.getAncestor());
        for (int index : gsg.getIndexes()) {
            FXOMObject constraints = null;
            switch (gsg.getType()) {
                case COLUMN:
                    constraints = mask.getColumnConstraintsAtIndex(index);
                    break;
                case ROW:
                    constraints = mask.getRowConstraintsAtIndex(index);
                    break;
                default:
                    assert false;
                    break;
            }
            assert constraints instanceof FXOMInstance;
            candidates.add((FXOMInstance) constraints);
        }
    }

    @Override
    protected String makeDescription() {
        final String result;
        final List<Job> subJobs = getSubJobs();
        final int subJobCount = subJobs.size();

        switch (subJobCount) {
            case 0:
                result = "Unexecutable Set"; //NOI18N
                break;
            case 1: // Single selection
                result = subJobs.get(0).getDescription();
                break;
            default:
                result = I18N.getString("label.action.edit.set.n",
                        propertyMetadata.getName().toString(),
                        subJobCount);
                break;
        }

        return result;
    }
}
