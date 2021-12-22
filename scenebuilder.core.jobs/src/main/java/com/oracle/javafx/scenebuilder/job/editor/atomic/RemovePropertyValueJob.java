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
package com.oracle.javafx.scenebuilder.job.editor.atomic;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;

/**
 *
 */
public class RemovePropertyValueJob extends Job {

    private final FXOMObject targetValue;

    private FXOMPropertyC parentProperty;
    private int indexInParentProperty;
    private Job removePropertyJob;

    private FXOMDocument fxomDocument;

    public RemovePropertyValueJob(SceneBuilderBeanFactory context, FXOMObject value, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
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
    public void execute() {
        assert parentProperty == null;
        assert isExecutable();

        parentProperty = targetValue.getParentProperty();
        indexInParentProperty = targetValue.getIndexInParentProperty();
        if ((parentProperty.getValues().size() == 1) && (parentProperty.getParentInstance() != null)) {
            // targetValue is the last value of its parent property
            // => parent property must also be removed from its parent instance
            removePropertyJob = new RemovePropertyJob(getContext(), parentProperty, getEditorController()).extend();
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
    public void undo() {
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
    public void redo() {
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

}
