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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMObject} from his parent property provided by {@link FXOMObject#getParentProperty()}
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RemovePropertyValueJob extends AbstractJob {

    private FXOMObject targetValue;

    private FXOMProperty parentProperty;
    private int indexInParentProperty;
    private AbstractJob removePropertyJob;

    private final FXOMDocument fxomDocument;

    private final RemovePropertyJob.Factory removePropertyJobFactoty;

    protected RemovePropertyValueJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            RemovePropertyJob.Factory removePropertyJobFactoty) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removePropertyJobFactoty = removePropertyJobFactoty;
    }

    protected void setJobParameters(FXOMObject value) {
        this.targetValue = value;
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return targetValue.getParentProperty() != null;
    }

    @Override
    public void doExecute() {
        assert parentProperty == null;
        assert isExecutable();

        parentProperty = targetValue.getParentProperty();
        indexInParentProperty = targetValue.getIndexInParentProperty();
        if ((parentProperty.getChildren().size() == 1) && (parentProperty.getParentInstance() != null)) {
            // targetValue is the last value of its parent property
            // => parent property must also be removed from its parent instance
            removePropertyJob = removePropertyJobFactoty.getJob(parentProperty);
        }

        // Note : below we may have to run removePropertyJob.execute() so
        // we cannot re-use redo() here.
        fxomDocument.beginUpdate();
        if (removePropertyJob != null) {
            removePropertyJob.execute();
        }
        targetValue.removeFromParentProperty();
        fxomDocument.endUpdate();
    }

    @Override
    public void doUndo() {
        assert targetValue.getParentProperty() == null;

        fxomDocument.beginUpdate();
        targetValue.addToParentProperty(indexInParentProperty, parentProperty);
        if (removePropertyJob != null) {
            removePropertyJob.undo();
        }
        fxomDocument.endUpdate();

        assert targetValue.getParentProperty() == parentProperty;
        assert targetValue.getIndexInParentProperty() == indexInParentProperty;
    }

    @Override
    public void doRedo() {
        assert targetValue.getParentProperty() == parentProperty;
        assert targetValue.getIndexInParentProperty() == indexInParentProperty;

        fxomDocument.beginUpdate();
        if (removePropertyJob != null) {
            removePropertyJob.redo();
        }
        targetValue.removeFromParentProperty();
        fxomDocument.endUpdate();

        assert targetValue.getParentProperty() == null;
    }

    @Override
    public String getDescription() {
        // Should normally not reach the user
        return getClass().getSimpleName()
                + "["
                + targetValue.getGlueElement().getTagName()
                + "]";
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<RemovePropertyValueJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link RemovePropertyValueJob} job
         * @param value the value to remove
         * @return the job to execute
         */
        public RemovePropertyValueJob getJob(FXOMObject value) {
            return create(RemovePropertyValueJob.class, j -> j.setJobParameters(value));
        }
    }
}
