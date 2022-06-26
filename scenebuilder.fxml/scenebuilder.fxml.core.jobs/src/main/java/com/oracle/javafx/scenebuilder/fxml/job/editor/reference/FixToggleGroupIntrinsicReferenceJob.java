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

package com.oracle.javafx.scenebuilder.fxml.job.editor.reference;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.fxml.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemovePropertyJob;

import javafx.scene.control.ToggleGroup;

/**
 * This job creates a {@link ToggleGroup} in place of a toggleGroup referenced using an {@link FXOMIntrinsic} (fx:reference)<br/>
 * If the referee {@link ToggleGroup} exists, it is moved<br/>
 * If not, a new {@link ToggleGroup} is created<br/>
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class FixToggleGroupIntrinsicReferenceJob extends InlineDocumentJob {

    private FXOMIntrinsic reference;
    private final FXOMDocument fxomDocument;
    private final RemovePropertyJob.Factory removePropertyJobFactory;

    // @formatter:off
    protected FixToggleGroupIntrinsicReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            RemovePropertyJob.Factory removePropertyJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removePropertyJobFactory = removePropertyJobFactory;
    }

    protected void setJobParameters(FXOMIntrinsic reference) {
        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;

        this.reference = reference;
    }


    /*
     * InlineDocumentJob
     */
    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> result = new LinkedList<>();

        // 1) Locates the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.searchWithFxId(fxId);

        // @formatter:off
        /*
         *    <RadioButton>
         *       <toggleGroup>
         *           <fx:reference source="oxebo" />        // reference        //NOCHECK
         *       </toggleGroup>
         *    </RadioButton>
         *    ...
         *    <RadioButton>
         *       <toggleGroup>
         *           <ToggleGroup fx:id="oxebo" />          // referee          //NOCHECK
         *       </toggleGroup>
         *    </RadioButton>
         */
        // @formatter:on

        if (referee != null) {
            assert referee.getParentProperty() != null;

            final FXOMProperty referenceProperty = reference.getParentProperty();
            final FXOMProperty refereeProperty = referee.getParentProperty();

            // 2a.1) Removes referenceProperty
            final RemovePropertyJob removeReferenceJob = removePropertyJobFactory.getJob(referenceProperty);
            removeReferenceJob.execute();
            result.add(removeReferenceJob);

            // 2a.2) Removes refereeProperty
            final RemovePropertyJob removeRefereeJob
                    = removePropertyJobFactory.getJob(refereeProperty);
            removeRefereeJob.execute();
            result.add(removeRefereeJob);

            // 2a.3) Adds referenceProperty where refereeProperty was
            final AbstractJob addReferenceJob
                    = removeRefereeJob.makeMirrorJob(referenceProperty);
            addReferenceJob.execute();
            result.add(addReferenceJob);

            // 2a.4) Adds refereeProperty where referenceProperty was
            final AbstractJob addRefereeJob
                    = removeRefereeJob.makeMirrorJob(refereeProperty);
            addRefereeJob.execute();
            result.add(addReferenceJob);

        } else {

            // 2b.1) Removes reference
            final FXOMProperty referenceProperty = reference.getParentProperty();
            final RemovePropertyJob removeReferenceJob
                    = removePropertyJobFactory.getJob(referenceProperty);
            removeReferenceJob.execute();
            result.add(removeReferenceJob);

            // 2b.2) Creates and adds toggle group
            final FXOMPropertyC newToggleGroup = FXOMNodes.makeToggleGroup(fxomDocument, fxId);
            final AbstractJob addJob = removeReferenceJob.makeMirrorJob(newToggleGroup);
            addJob.execute();
            result.add(addJob);
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


    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<FixToggleGroupIntrinsicReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link FixToggleGroupIntrinsicReferenceJob} job.
         *
         * @param reference the reference
         * @return the job to execute
         */
        public FixToggleGroupIntrinsicReferenceJob getJob(FXOMIntrinsic reference) {
            return create(FixToggleGroupIntrinsicReferenceJob.class, j -> j.setJobParameters(reference));
        }
    }
}
