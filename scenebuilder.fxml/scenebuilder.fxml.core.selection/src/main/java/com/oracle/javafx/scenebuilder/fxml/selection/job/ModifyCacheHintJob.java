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
package com.oracle.javafx.scenebuilder.fxml.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;


/**
 * This job set the property defined by the provided {@link ValuePropertyMetadata}<br/>
 * but it handles only the cacheHint property or generate an assertion error<br/>
 * This job links the modification of the cacheHint property to the cache property<br/>
 * If the new value is not DEFAULT it sets cache to true<br/>
 * FLAW: currently the modification of the cache property is not reflected in the inspector until you deselect adn reselect the object
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ModifyCacheHintJob extends BatchDocumentJob {

    private int subJobCount = 0;
    private final PropertyName cachePN = new PropertyName("cache"); //NOCHECK
    private final PropertyName cacheHintPN = new PropertyName("cacheHint"); //NOCHECK

    private final Selection selection;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final Metadata metadata;

    protected ValuePropertyMetadata propertyMetadata;
    protected Object newValue;

    protected ModifyCacheHintJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            Metadata metadata,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
        super(extensionFactory, documentManager);
        assert cacheHintPN.equals(propertyMetadata.getName());

        this.selection = selection;
        this.metadata = metadata;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

    protected void setJobParameters(ValuePropertyMetadata propertyMetadata, Object newValue) {
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();
        final Set<FXOMInstance> candidates = new HashSet<>();

        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
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
            final AbstractJob subJob1 = modifyObjectJobFactory.getJob(null, fxomInstance, propertyMetadata, newValue);

            if (subJob1.isExecutable()) {
                result.add(subJob1);
                subJobCount++;
            }
            // ModifyObject job for the cache property
            if ("DEFAULT".equals(newValue) == false) { //NOCHECK
                final ValuePropertyMetadata cacheVPM = metadata.queryValueProperty(fxomInstance, cachePN);
                final AbstractJob subJob2 = modifyObjectJobFactory.getJob(null, fxomInstance, cacheVPM, Boolean.TRUE);
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<ModifyCacheHintJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  ModifyCacheHintJob} job
         * @param propertyMetadata the definition of property to set (expected to be cacheHint)
         * @param newValue the new value of the property to set
         * @return the job to execute
         */
        public ModifyCacheHintJob getJob(ValuePropertyMetadata propertyMetadata, Object newValue) {
            return create(ModifyCacheHintJob.class, j -> j.setJobParameters(propertyMetadata, newValue));
        }
    }
}
