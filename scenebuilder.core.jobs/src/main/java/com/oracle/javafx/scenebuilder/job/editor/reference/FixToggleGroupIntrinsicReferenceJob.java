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
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;

/**
 *
 */
public class FixToggleGroupIntrinsicReferenceJob extends InlineDocumentJob {

    private final FXOMIntrinsic reference;
    private final FXOMDocument fxomDocument;

    public FixToggleGroupIntrinsicReferenceJob(ApplicationContext context,
            FXOMIntrinsic reference,
            Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;

        this.reference = reference;
    }

    /*
     * InlineDocumentJob
     */
    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> result = new LinkedList<>();

        // 1) Locates the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.searchWithFxId(fxId);

        /*
         *    <RadioButton>
         *       <toggleGroup>
         *           <fx:reference source="oxebo" />        // reference        //NOI18N
         *       </toggleGroup>
         *    </RadioButton>
         *    ...
         *    <RadioButton>
         *       <toggleGroup>
         *           <ToggleGroup fx:id="oxebo" />          // referee          //NOI18N
         *       </toggleGroup>
         *    </RadioButton>
         */

        if (referee != null) {
            assert referee.getParentProperty() != null;

            final FXOMPropertyC referenceProperty = reference.getParentProperty();
            final FXOMPropertyC refereeProperty = referee.getParentProperty();

            // 2a.1) Removes referenceProperty
            final RemovePropertyJob removeReferenceJob
                    = new RemovePropertyJob(getContext(), referenceProperty, getEditorController());
            removeReferenceJob.extend().execute();
            result.add(removeReferenceJob.extend());

            // 2a.2) Removes refereeProperty
            final RemovePropertyJob removeRefereeJob
                    = new RemovePropertyJob(getContext(), refereeProperty, getEditorController());
            removeRefereeJob.extend().execute();
            result.add(removeRefereeJob.extend());

            // 2a.3) Adds referenceProperty where refereeProperty was
            final Job addReferenceJob
                    = removeRefereeJob.makeMirrorJob(referenceProperty);
            addReferenceJob.extend().execute();
            result.add(addReferenceJob.extend());

            // 2a.4) Adds refereeProperty where referenceProperty was
            final Job addRefereeJob
                    = removeRefereeJob.makeMirrorJob(refereeProperty);
            addRefereeJob.extend().execute();
            result.add(addReferenceJob.extend());

        } else {

            // 2b.1) Removes reference
            final FXOMPropertyC referenceProperty = reference.getParentProperty();
            final RemovePropertyJob removeReferenceJob
                    = new RemovePropertyJob(getContext(), referenceProperty, getEditorController());
            removeReferenceJob.extend().execute();
            result.add(removeReferenceJob.extend());

            // 2b.2) Creates and adds toggle group
            final FXOMPropertyC newToggleGroup = FXOMNodes.makeToggleGroup(fxomDocument, fxId);
            final Job addJob = removeReferenceJob.makeMirrorJob(newToggleGroup);
            addJob.extend().execute();
            result.add(addJob.extend());
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Not expected to reach the user
    }

    @Override
    public boolean isExecutable() {
        return ((reference.getType() == FXOMIntrinsic.Type.FX_REFERENCE) &&
                (reference.getParentProperty() != null));
    }


}
