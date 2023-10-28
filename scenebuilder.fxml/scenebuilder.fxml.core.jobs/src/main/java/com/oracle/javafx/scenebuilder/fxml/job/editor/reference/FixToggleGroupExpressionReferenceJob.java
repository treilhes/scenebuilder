/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.collector.FxIdCollector;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.fxml.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemoveObjectJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.ReplacePropertyValueJobT;

import javafx.scene.control.ToggleGroup;

/**
 * This job creates a {@link ToggleGroup} in place of a toggleGroup reference<br/>
 * If the referee {@link ToggleGroup} exists, it is moved<br/>
 * If not, a new {@link ToggleGroup} is created<br/>
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class FixToggleGroupExpressionReferenceJob extends InlineDocumentJob {

    private FXOMPropertyT reference;
    private final FXOMDocument fxomDocument;
    private RemoveObjectJob.Factory removeObjectJobFactory;
    private ReplacePropertyValueJobT.Factory replacePropertyValueJobTFactory;
    private AddPropertyJob.Factory addPropertyJobFactory;
    private RemovePropertyJob.Factory removePropertyJobFactory;

    // @formatter:off
    protected FixToggleGroupExpressionReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            Selection selection,
            RemoveObjectJob.Factory removeObjectJobFactory,
            ReplacePropertyValueJobT.Factory replacePropertyValueJobTFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.replacePropertyValueJobTFactory = replacePropertyValueJobTFactory;
        this.addPropertyJobFactory = addPropertyJobFactory;
        this.removePropertyJobFactory = removePropertyJobFactory;
    }

    protected void setJobParameters(FXOMPropertyT reference) {
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
        final FXOMObject referee = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);

        // @formatter:off
        /*
         *    <RadioButton toggleGroup="$oxebo" />          // reference    //NOCHECK
         *    ...
         *    <RadioButton>
         *       <toggleGroup>
         *           <ToggleGroup fx:id="oxebo" />          // referee      //NOCHECK
         *       </toggleGroup>
         *    </RadioButton>
         */
         // @formatter:on

        // 2) Finds or create the matching toggle group
        if (referee != null) {
            assert referee.getParentProperty() != null;
            assert referee.getParentProperty().getParentInstance() != null;

            // 2a.1) Toggle group is available : disconnect it and re-use it
            final FXOMElement parentInstance = referee.getParentProperty().getParentInstance();
            final AbstractJob removeJob = removeObjectJobFactory.getJob(referee);
            removeJob.execute();
            result.add(removeJob);

            // 2a.2) Replace the reference by the toggleGroup
            final AbstractJob replaceJob = replacePropertyValueJobTFactory.getJob(reference, referee);
            replaceJob.execute();
            result.add(replaceJob);

            // 2a.3) Put reference at referee previous place
            final AbstractJob addJob = addPropertyJobFactory.getJob(reference, parentInstance, -1);
            addJob.execute();
            result.add(addJob);

        } else {

            // 2b.1) Removes the reference
            final FXOMElement targetInstance = reference.getParentInstance();
            final AbstractJob removeJob = removePropertyJobFactory.getJob(reference);
            removeJob.execute();
            result.add(removeJob);

            // 2b.2) Creates and adds toggle group
            final FXOMPropertyC newToggleGroup = FXOMNodes.makeToggleGroup(fxomDocument, fxId);
            final AbstractJob addJob = addPropertyJobFactory.getJob(newToggleGroup, targetInstance, -1);
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
        final PrefixedValue pv = new PrefixedValue(reference.getValue());
        return pv.isExpression();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<FixToggleGroupExpressionReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link FixToggleGroupExpressionReferenceJob} job.
         *
         * @param reference the reference
         * @return the job to execute
         */
        public FixToggleGroupExpressionReferenceJob getJob(FXOMPropertyT reference) {
            return create(FixToggleGroupExpressionReferenceJob.class, j -> j.setJobParameters(reference));
        }
    }
}
