/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.job.internal.reference;

import java.util.LinkedList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;

import javafx.scene.control.ToggleGroup;

/**
 * This job creates a {@link ToggleGroup} in place of a toggleGroup referenced using an {@link FXOMIntrinsic} (fx:reference)<br/>
 * If the referee {@link ToggleGroup} exists, it is switched with the reference<br/>
 * If not, a new {@link ToggleGroup} is created<br/>
 */
@Prototype
public final class FixToggleGroupIntrinsicReferenceJob extends InlineDocumentJob {

    private final FxomJobsFactory fxomJobsFactory;
    private final FXOMDocument fxomDocument;

    private FXOMIntrinsic reference;

    // @formatter:off
    protected FixToggleGroupIntrinsicReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomJobsFactory = fxomJobsFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters(FXOMIntrinsic reference) {
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
        final FXOMObject referee = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);

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

            // 2a.1) Backup locations

            var referenceParentInstance = referenceProperty.getParentInstance();
            var referenceIndexInParentInstance = referenceProperty.getIndexInParentInstance();

            var refereeParentInstance = refereeProperty.getParentInstance();
            var refereeIndexInParentInstance = refereeProperty.getIndexInParentInstance();

            // 2a.1) Removes referenceProperty

            final Job removeReferenceJob = fxomJobsFactory.removeProperty(referenceProperty);
            removeReferenceJob.execute();
            result.add(removeReferenceJob);

            // 2a.2) Removes refereeProperty
            final Job removeRefereeJob = fxomJobsFactory.removeProperty(refereeProperty);
            removeRefereeJob.execute();
            result.add(removeRefereeJob);

            // 2a.3) Adds referenceProperty where refereeProperty was
            final Job addReferenceJob = fxomJobsFactory.addProperty(referenceProperty, refereeParentInstance, refereeIndexInParentInstance);
            addReferenceJob.execute();
            result.add(addReferenceJob);

            // 2a.4) Adds refereeProperty where referenceProperty was
            final Job addRefereeJob = fxomJobsFactory.addProperty(refereeProperty, referenceParentInstance, referenceIndexInParentInstance);
            addRefereeJob.execute();
            result.add(addReferenceJob);

        } else {

            // 2a.1) Backup locations
            final FXOMProperty referenceProperty = reference.getParentProperty();
            var referenceParentInstance = referenceProperty.getParentInstance();
            var referenceIndexInParentInstance = referenceProperty.getIndexInParentInstance();

            // 2b.1) Removes reference

            final Job removeReferenceJob = fxomJobsFactory.removeProperty(referenceProperty);
            removeReferenceJob.execute();
            result.add(removeReferenceJob);

            // 2b.2) Creates and adds toggle group
            final FXOMPropertyC newToggleGroup = FXOMNodes.makeToggleGroup(fxomDocument, fxId);
            final Job addJob = fxomJobsFactory.addProperty(newToggleGroup, referenceParentInstance, referenceIndexInParentInstance);
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
}
