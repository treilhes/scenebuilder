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
package com.oracle.javafx.scenebuilder.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;


/**
 * This job set the property defined by the provided {@link ValuePropertyMetadata}<br/>
 * but it handles only the cacheHint property or generate an assertion error<br/>
 * This job links the modification of the cacheHint property to the cache property<br/>
 * If the new value is not DEFAULT it sets cache to true<br/>
 * FLAW: currently the modification of the cache property is not reflected in the inspector until you deselect adn reselect the object
 */
@Prototype
public final class ModifyCacheHintJob extends BatchDocumentJob {

    private static final PropertyName cachePN = new PropertyName("cache"); //NOCHECK
    private static final PropertyName cacheHintPN = new PropertyName("cacheHint"); //NOCHECK

    private final Selection selection;
    private final FxomJobsFactory fxomJobsFactory;
    private final SbMetadata metadata;

    private int subJobCount = 0;


    protected ValuePropertyMetadata propertyMetadata;
    protected Object newValue;

    protected ModifyCacheHintJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            SbMetadata metadata,
            FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory, documentManager);
        assert cacheHintPN.equals(propertyMetadata.getName());

        this.selection = selection;
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(ValuePropertyMetadata propertyMetadata, Object newValue) {
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();
        final Set<FXOMInstance> candidates = new HashSet<>();

        if (selection.getGroup() != null) {
            final SelectionGroup osg = selection.getGroup();
            for (FXOMObject fxomObject : osg.getItems()) {
                if (fxomObject instanceof FXOMInstance) {
                    candidates.add((FXOMInstance) fxomObject);
                }
            }
        } else {
            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup();
        }

        // Add ModifyObject jobs
        for (FXOMInstance fxomInstance : candidates) {
            // ModifyObject job for the cacheHint property
            final Job subJob1 = fxomJobsFactory.modifyObject(fxomInstance, propertyMetadata, newValue);

            if (subJob1.isExecutable()) {
                result.add(subJob1);
                subJobCount++;
            }
            // ModifyObject job for the cache property
            if ("DEFAULT".equals(newValue) == false) { //NOCHECK
                final ValuePropertyMetadata cacheVPM = metadata.queryValueProperty(fxomInstance, cachePN);
                final Job subJob2 = fxomJobsFactory.modifyObject(fxomInstance, cacheVPM, Boolean.TRUE);
                if (subJob2.isExecutable()) {
                    result.add(subJob2);
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;

        switch (subJobCount) {
            case 0:
                result = "Unexecutable Set"; //NOCHECK
                break;
            case 1: // Single selection
                result = getSubJobs().get(0).getDescription();
                break;
            default:
                result = I18N.getString("label.action.edit.set.n",
                        propertyMetadata.getName().toString(),
                        subJobCount);
                break;
        }

        return result;
    }
}
