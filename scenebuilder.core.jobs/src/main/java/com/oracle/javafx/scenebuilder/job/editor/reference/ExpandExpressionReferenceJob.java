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

package com.oracle.javafx.scenebuilder.job.editor.reference;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCloner;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;

/**
 *
 */
public class ExpandExpressionReferenceJob extends InlineDocumentJob {

    private final FXOMPropertyT reference;
    private final FXOMCloner cloner;
    private final FXOMDocument fxomDocument;

    public ExpandExpressionReferenceJob(ApplicationContext context, 
            FXOMPropertyT reference,
            FXOMCloner cloner,
            Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;
        assert (cloner == null) || (cloner.getTargetDocument() == fxomDocument);

        this.reference = reference;
        this.cloner = cloner;
    }

    /*
     * InlineDocumentJob
     */
    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> result = new LinkedList<>();

        // 1) remove the reference
        final FXOMInstance parentInstance = reference.getParentInstance();
        final Job removeReference = new RemovePropertyJob(getContext(), reference, getEditorController()).extend();
        removeReference.execute();
        result.add(removeReference);

        // 2.1) clone the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.searchWithFxId(fxId);
        final FXOMObject refereeClone = cloner.clone(referee);

        // 3) insert the clone in place of the reference
        final FXOMPropertyC cloneProperty
                = new FXOMPropertyC(fxomDocument, reference.getName(), refereeClone);
        final Job addCloneJob
                = new AddPropertyJob(getContext(), cloneProperty, parentInstance, -1, getEditorController()).extend();
        addCloneJob.execute();
        result.add(addCloneJob);

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Not expected to reach the user
    }

    @Override
    public boolean isExecutable() {
        final PrefixedValue pv = new PrefixedValue(reference.getValue());
        return pv.isExpression();
    }


}
