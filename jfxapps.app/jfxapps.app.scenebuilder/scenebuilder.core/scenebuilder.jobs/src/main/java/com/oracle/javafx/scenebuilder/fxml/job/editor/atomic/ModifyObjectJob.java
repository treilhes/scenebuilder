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
package com.oracle.javafx.scenebuilder.fxml.job.editor.atomic;

import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

/**
 * This Job updates the FXOM document at execution time.
 * It updates the property defined by the provided {@link ValuePropertyMetadata} owned by {@link FXOMElement} with the the provided value
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ModifyObjectJob extends AbstractJob {

    private final FXOMDocument fxomDocument;

    private FXOMElement fxomElement;
    private ValuePropertyMetadata propertyMetadata;
    private Object newValue;
    private Object oldValue;
    private String description;


    public ModifyObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager
            ) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    /**
     * Update the property defined by propertyMetadata and owned by fxomInstance with the new value newValue
     * @param fxomElement the {@link FXOMElement} owning the property
     * @param propertyMetadata the property definition
     * @param newValue the new value
     */
    protected void setJobParameters(String description, FXOMElement fxomElement, ValuePropertyMetadata propertyMetadata, Object newValue) {
        assert fxomElement != null;
        assert fxomElement.getSceneGraphObject() != null;
        assert propertyMetadata != null;

        this.fxomElement = fxomElement;
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
        this.oldValue = propertyMetadata.getValueObject(fxomElement);
        this.description = description != null ? description
                : I18N.getString("label.action.edit.set.1", propertyMetadata.getName().toString(),
                        fxomElement.getSceneGraphObject().getClass().getSimpleName());
    }
    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        final Object currentValue = propertyMetadata.getValueObject(fxomElement);
        return Objects.equals(newValue, currentValue) == false;
    }

    @Override
    public void doExecute() {
        doRedo();
    }

    @Override
    public void doUndo() {
        fxomDocument.beginUpdate();
        this.propertyMetadata.setValueObject(fxomElement, oldValue);
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        fxomDocument.beginUpdate();
        this.propertyMetadata.setValueObject(fxomElement, newValue);
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<ModifyObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link ModifyObjectJob} job
         * @param description the job description or null
         * @param fxomElement the {@link FXOMElement} owning the property
         * @param propertyMetadata the property definition
         * @param newValue the new value
         * @return the job to execute
         */
        public ModifyObjectJob getJob(String description, FXOMElement fxomElement, ValuePropertyMetadata propertyMetadata, Object newValue) {
            return create(ModifyObjectJob.class, j -> j.setJobParameters(description, fxomElement, propertyMetadata, newValue));
        }

        /**
         * Create an {@link ModifyObjectJob} job
         * @param fxomElement the {@link FXOMElement} owning the property
         * @param propertyMetadata the property definition
         * @param newValue the new value
         * @return the job to execute
         */
        public ModifyObjectJob getJob(FXOMElement fxomElement, ValuePropertyMetadata propertyMetadata, Object newValue) {
            return create(ModifyObjectJob.class, j -> j.setJobParameters(null, fxomElement, propertyMetadata, newValue));
        }
    }
}
