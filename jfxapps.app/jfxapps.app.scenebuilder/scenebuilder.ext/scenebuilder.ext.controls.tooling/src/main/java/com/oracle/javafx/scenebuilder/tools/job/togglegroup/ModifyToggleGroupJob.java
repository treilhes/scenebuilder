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

package com.oracle.javafx.scenebuilder.tools.job.togglegroup;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.util.JavaLanguage;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.ToggleGroupPropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;

/**
 * This job allocate a toggle group id to an {@link FXOMObject}
 */
@ApplicationInstancePrototype
public final class ModifyToggleGroupJob extends BatchDocumentJob {

    private static final PropertyName toggleGroupName = new PropertyName("toggleGroup"); // NOCHECK

    private FXOMObject targetObject;
    private String toggleGroupId;

    private final SbMetadata metadata;

    private final FxomJobsFactory fxomJobsFactory;

    public ModifyToggleGroupJob(JobExtensionFactory extensionFactory, ApplicationInstanceEvents documentManager,
            SbMetadata metadata, FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory, documentManager);
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    protected void setJobParameters(FXOMObject fxomObject, String toggleGroupId) {
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

        if (targetObject instanceof FXOMInstance targetInstance) {
            final var vpm = metadata.queryValueProperty(targetInstance, toggleGroupName);
            if (vpm instanceof ToggleGroupPropertyMetadata) {
                /*
                 * Case #0 : toggleGroupId is null => removes toggleGroup FXOMProperty if needed
                 *
                 * Case #1 : targetObject.toggleGroup is undefined => adds FXOMPropertyT for
                 * toggleGroup="$toggleGroupId" //NOCHECK
                 *
                 * Case #2 : targetObject defines the ToggleGroup instance => removes
                 * toggleGroup FXOMPropertyC => adds FXOMPropertyT for
                 * toggleGroup="$toggleGroupId" //NOCHECK
                 *
                 * Case #3 : targetObject refers to a ToggleGroup instance => removes
                 * toggleGroup FXOMPropertyT => adds FXOMPropertyT for
                 * toggleGroup="$toggleGroupId" //NOCHECK
                 */

                final FXOMDocument fxomDocument = targetInstance.getFxomDocument();
                final FXOMProperty fxomProperty = targetInstance.getProperties().get(toggleGroupName);

                if (fxomProperty != null) { // Case #0 #2 or #3
                    final var removePropertyJob = fxomJobsFactory.removeProperty(fxomProperty);
                    result.add(removePropertyJob);
                }

                // Case #1, #2 and #3
                if (toggleGroupId != null) {
                    final var pv = new PrefixedValue(PrefixedValue.Type.EXPRESSION, toggleGroupId);
                    final var newProperty = new FXOMPropertyT(fxomDocument, toggleGroupName, pv.toString());
                    final var addPropertyJob = fxomJobsFactory.addProperty(newProperty, targetInstance, -1);
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

    @ApplicationInstanceSingleton
    public final static class Factory extends JobFactory<ModifyToggleGroupJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ModifyToggleGroupJob} job.
         *
         * @param fxomObject    the fxom object
         * @param toggleGroupId the toggle group id to allocate.
         * @return the job to execute
         */
        public ModifyToggleGroupJob getJob(FXOMObject fxomObject, String toggleGroupId) {
            return create(ModifyToggleGroupJob.class, j -> j.setJobParameters(fxomObject, toggleGroupId));
        }
    }
}
