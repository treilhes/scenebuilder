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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;


/**
 *
 */
public class ModifyCacheHintJob extends ModifySelectionJob {

    private int subJobCount = 0;
    private final PropertyName cachePN = new PropertyName("cache"); //NOI18N
    private final PropertyName cacheHintPN = new PropertyName("cacheHint"); //NOI18N

    public ModifyCacheHintJob(ApplicationContext context, ValuePropertyMetadata propertyMetadata, Object newValue, Editor editor) {
        super(context, propertyMetadata, newValue, editor);
        assert cacheHintPN.equals(propertyMetadata.getName());
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();
        final Set<FXOMInstance> candidates = new HashSet<>();
        final Selection selection = getEditorController().getSelection();
        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            for (FXOMObject fxomObject : osg.getItems()) {
                if (fxomObject instanceof FXOMInstance) {
                    candidates.add((FXOMInstance) fxomObject);
                }
            }
        } else {
            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup();
        }

        // Add ModifyObject jobs
        for (FXOMInstance fxomInstance : candidates) {
            // ModifyObject job for the cacheHint property
            final Job subJob1 = new ModifyObjectJob(getContext(),
                    fxomInstance, propertyMetadata, newValue, getEditorController()).extend();
            if (subJob1.isExecutable()) {
                result.add(subJob1);
                subJobCount++;
            }
            // ModifyObject job for the cache property
            if ("DEFAULT".equals(newValue) == false) { //NOI18N
                final ValuePropertyMetadata cacheVPM
                        = Metadata.getMetadata().queryValueProperty(fxomInstance, cachePN);
                final Job subJob2 = new ModifyObjectJob(getContext(),
                        fxomInstance, cacheVPM, Boolean.TRUE, getEditorController()).extend();
                if (subJob2.isExecutable()) {
                    result.add(subJob2);
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;

        switch (subJobCount) {
            case 0:
                result = "Unexecutable Set"; //NOI18N
                break;
            case 1: // Single selection
                result = getSubJobs().get(0).getDescription();
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
