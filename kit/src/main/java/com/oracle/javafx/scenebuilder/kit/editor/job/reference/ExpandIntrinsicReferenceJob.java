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

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCloner;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.editor.job.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.ReplaceObjectJob;

/**
 *
 */
public class ExpandIntrinsicReferenceJob extends InlineDocumentJob {

    private final FXOMIntrinsic reference;
    private final FXOMCloner cloner;

    public ExpandIntrinsicReferenceJob(ApplicationContext context, 
            FXOMIntrinsic reference,
            FXOMCloner cloner,
            Editor editor) {
        super(context, editor);

        assert reference != null;
        assert cloner != null;
        assert reference.getFxomDocument() == editor.getFxomDocument();
        assert cloner.getTargetDocument() == editor.getFxomDocument();

        this.reference = reference;
        this.cloner = cloner;
    }

    /*
     * InlineDocumentJob
     */
    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> result = new LinkedList<>();

        // 1) clone the referee
        final FXOMDocument fxomDocument = getEditorController().getFxomDocument();
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.searchWithFxId(fxId);
        final FXOMObject refereeClone = cloner.clone(referee);

        // 2) replace the reference by the referee clone
        final Job replaceJob = new ReplaceObjectJob(getContext(), reference, refereeClone, getEditorController()).extend();
        replaceJob.execute();
        result.add(replaceJob);

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Not expected to reach the user
    }

    @Override
    public boolean isExecutable() {
        return ((reference.getType() == FXOMIntrinsic.Type.FX_COPY) ||
                (reference.getType() == FXOMIntrinsic.Type.FX_REFERENCE)) &&
               ((reference.getParentProperty() != null) ||
                (reference.getParentCollection() != null));
    }


}
