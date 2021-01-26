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

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;

/**
 *
 */
public class RemovePropertyJob extends Job {

    private final FXOMProperty targetProperty;

    private FXOMInstance parentInstance;
    private int indexInParentInstance;

    private FXOMDocument fxomDocument;

    public RemovePropertyJob(ApplicationContext context, FXOMProperty targetProperty, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert targetProperty != null;
        this.targetProperty = targetProperty;
    }

    public FXOMProperty getTargetProperty() {
        return targetProperty;
    }

    public Job makeMirrorJob(FXOMProperty anotherProperty) {
        return new AddPropertyJob(getContext(), anotherProperty, parentInstance,
                indexInParentInstance, getEditorController()).extend();
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return targetProperty.getParentInstance() != null;
    }

    @Override
    public void execute() {
        assert parentInstance == null;
        assert isExecutable();

        parentInstance = targetProperty.getParentInstance();
        indexInParentInstance = targetProperty.getIndexInParentInstance();
        redo();
    }

    @Override
    public void undo() {
        assert targetProperty.getParentInstance() == null;

        fxomDocument.beginUpdate();
        targetProperty.addToParentInstance(indexInParentInstance, parentInstance);
        fxomDocument.endUpdate();

        assert targetProperty.getParentInstance() == parentInstance;
        assert targetProperty.getIndexInParentInstance() == indexInParentInstance;
    }

    @Override
    public void redo() {
        assert targetProperty.getParentInstance() == parentInstance;
        assert targetProperty.getIndexInParentInstance() == indexInParentInstance;

        fxomDocument.beginUpdate();
        targetProperty.removeFromParentInstance();
        fxomDocument.endUpdate();

        assert targetProperty.getParentInstance() == null;
    }

    @Override
    public String getDescription() {
        // Should normally not reach the user
        return getClass().getSimpleName()
                + "["
                + targetProperty.getName()
                + "]";
    }

}
