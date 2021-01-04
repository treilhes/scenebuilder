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

package com.oracle.javafx.scenebuilder.kit.editor.job.togglegroup;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ToggleGroupPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.RemovePropertyJob;

/**
 *
 */
public class ModifyToggleGroupJob extends BatchDocumentJob {

    private static final PropertyName toggleGroupName
            = new PropertyName("toggleGroup"); //NOI18N

    private final FXOMObject targetObject;
    private final String toggleGroupId;

    public ModifyToggleGroupJob(ApplicationContext context, FXOMObject fxomObject, String toggleGroupId,
            Editor editor) {
        super(context, editor);

        assert fxomObject != null;
        assert (toggleGroupId == null) || JavaLanguage.isIdentifier(toggleGroupId);

        this.targetObject = fxomObject;
        this.toggleGroupId = toggleGroupId;
    }

    /*
     * CompositeJob
     */

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (targetObject instanceof FXOMInstance) {
            final FXOMInstance targetInstance = (FXOMInstance) targetObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(targetInstance, toggleGroupName);
            if (vpm instanceof ToggleGroupPropertyMetadata) {
                /*
                 * Case #0 : toggleGroupId is null
                 *      => removes toggleGroup FXOMProperty if needed
                 *
                 * Case #1 : targetObject.toggleGroup is undefined
                 *      => adds FXOMPropertyT for toggleGroup="$toggleGroupId"      //NOI18N
                 *
                 * Case #2 : targetObject defines the ToggleGroup instance
                 *      => removes toggleGroup FXOMPropertyC
                 *      => adds FXOMPropertyT for toggleGroup="$toggleGroupId"      //NOI18N
                 *
                 * Case #3 : targetObject refers to a ToggleGroup instance
                 *      => removes toggleGroup FXOMPropertyT
                 *      => adds FXOMPropertyT for toggleGroup="$toggleGroupId"      //NOI18N
                 */

                final FXOMDocument fxomDocument
                        = targetInstance.getFxomDocument();
                final FXOMProperty fxomProperty
                        = targetInstance.getProperties().get(toggleGroupName);

                if (fxomProperty != null) { // Case #0 #2 or #3
                    final Job removePropertyJob
                            = new RemovePropertyJob(getContext(), fxomProperty, getEditorController()).extend();
                    result.add(removePropertyJob);
                }

                // Case #1, #2 and #3
                if (toggleGroupId != null) {
                    final PrefixedValue pv
                            = new PrefixedValue(PrefixedValue.Type.EXPRESSION, toggleGroupId);
                    final FXOMPropertyT newProperty
                            = new FXOMPropertyT(fxomDocument, toggleGroupName, pv.toString());
                    final Job addPropertyJob
                            = new AddPropertyJob(getContext(), newProperty, targetInstance, -1, getEditorController()).extend();
                    result.add(addPropertyJob);
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Should not reach the user
    }

}
