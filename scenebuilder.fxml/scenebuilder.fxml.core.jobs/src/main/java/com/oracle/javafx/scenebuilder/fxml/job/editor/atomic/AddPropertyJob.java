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

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;

/**
 * This Job updates the FXOM document at execution time.
 * It adds the provided {@link FXOMProperty} into the provided {@link FXOMInstance} at the specified index
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class AddPropertyJob extends AbstractJob {

    private final FXOMDocument fxomDocument;

    private FXOMProperty property;
    private FXOMElement targetInstance;
    private int targetIndex;

    protected AddPropertyJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();

    }

    protected void setJobParameters(FXOMProperty property, FXOMElement targetInstance, int targetIndex) {
        assert property != null;
        assert targetInstance != null;
        assert targetIndex >= -1;

        this.property = property;
        this.targetInstance = targetInstance;
        this.targetIndex = targetIndex;
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return property.getParentInstance() == null;
    }

    @Override
    public void doExecute() {
        doRedo();
    }

    @Override
    public void doUndo() {
        assert property.getParentInstance() == targetInstance;

        fxomDocument.beginUpdate();
        property.removeFromParentInstance();
        fxomDocument.endUpdate();

        assert property.getParentInstance() == null;
    }

    @Override
    public void doRedo() {
        assert property.getParentInstance() == null;

        fxomDocument.beginUpdate();
        property.addToParentInstance(targetIndex, targetInstance);
        fxomDocument.endUpdate();

        assert property.getParentInstance() == targetInstance;
    }

    @Override
    public String getDescription() {
        // Should normally not reach the user
        return getClass().getSimpleName();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<AddPropertyJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link AddPropertyJob} job
         * @param property the property to add
         * @param targetInstance the instance receiving the value
         * @param targetIndex the instance property collection index receiving the value
         * @return the job to execute
         */
        public AddPropertyJob getJob(FXOMProperty property, FXOMElement targetInstance, int targetIndex) {
            return create(AddPropertyJob.class, j -> j.setJobParameters(property, targetInstance, targetIndex));
        }
    }
}
