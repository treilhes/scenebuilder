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
package com.oracle.javafx.scenebuilder.api.editor.job;

import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

/**
 * This Job updates the FXOM document at execution time. The selection is not
 * updated.
 *
 * The sub jobs are FIRST all created, THEN executed.
 */
public abstract class BatchDocumentJob extends CompositeJob {

    private List<Job> subJobs;
    private FXOMDocument fxomDocument;

    public BatchDocumentJob(ApplicationContext context, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        fxomDocument = documentManager.fxomDocument().get();
    }

    @Override
    public final List<Job> getSubJobs() {
        if (subJobs == null) {
            subJobs = Collections.unmodifiableList(makeSubJobs());
            assert subJobs != null;
        }
        return subJobs;
    }

    @Override
    public final boolean isExecutable() {
        return getSubJobs().isEmpty() == false;
    }

    @Override
    public void execute() {
        fxomDocument.beginUpdate();
        for (Job subJob : getSubJobs()) {
            subJob.execute();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void undo() {
        fxomDocument.beginUpdate();
        for (int i = getSubJobs().size() - 1; i >= 0; i--) {
            getSubJobs().get(i).undo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public void redo() {
        fxomDocument.beginUpdate();
        for (Job subJob : getSubJobs()) {
            subJob.redo();
        }
        fxomDocument.endUpdate();
    }

    protected abstract List<Job> makeSubJobs();
}
