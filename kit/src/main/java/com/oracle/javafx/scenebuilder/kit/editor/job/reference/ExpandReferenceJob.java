/*
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
 *  - Neither the name of Oracle Corporation nor the names of its
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

package com.oracle.javafx.scenebuilder.kit.editor.job.reference;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCloner;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;

/**
 *
 */
public class ExpandReferenceJob  extends Job {

    private final Job subJob;

    public ExpandReferenceJob(ApplicationContext context, FXOMNode reference, FXOMCloner cloner, Editor editor) {
        super(context, editor);
        if (reference instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) reference;
            subJob = new ExpandIntrinsicReferenceJob(getContext(), fxomIntrinsic, cloner, getEditorController()).extend();
        } else if (reference instanceof FXOMPropertyT) {
            final FXOMPropertyT fxomProperty = (FXOMPropertyT) reference;
            subJob = new ExpandExpressionReferenceJob(getContext(), fxomProperty, cloner, getEditorController()).extend();
        } else {
            throw new RuntimeException("Bug"); //NOI18N
        }
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return subJob.isExecutable();
    }

    @Override
    public void execute() {
        subJob.execute();
    }

    @Override
    public void undo() {
        subJob.undo();
    }

    @Override
    public void redo() {
        subJob.redo();
    }

    @Override
    public String getDescription() {
        return subJob.getDescription();
    }



}
