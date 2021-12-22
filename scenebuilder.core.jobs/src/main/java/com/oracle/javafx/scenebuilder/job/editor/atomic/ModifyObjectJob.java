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

import java.util.Objects;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

/**
 *
 */
public class ModifyObjectJob extends Job {

    private final FXOMInstance fxomInstance;
    private final ValuePropertyMetadata propertyMetadata;
    private final Object newValue;
    private final Object oldValue;
    private final String description;
    private FXOMDocument fxomDocument;

    public ModifyObjectJob(SceneBuilderBeanFactory context,
            FXOMInstance fxomInstance,
            ValuePropertyMetadata propertyMetadata,
            Object newValue,
            Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert fxomInstance != null;
        assert fxomInstance.getSceneGraphObject() != null;
        assert propertyMetadata != null;

        this.fxomInstance = fxomInstance;
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
        this.oldValue = propertyMetadata.getValueObject(fxomInstance);
        this.description = I18N.getString("label.action.edit.set.1",
                propertyMetadata.getName().toString(),
                fxomInstance.getSceneGraphObject().getClass().getSimpleName());
    }

    public ModifyObjectJob(SceneBuilderBeanFactory context,
            FXOMInstance fxomInstance,
            ValuePropertyMetadata propertyMetadata,
            Object newValue,
            Editor editor,
            String description) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

        assert fxomInstance != null;
        assert fxomInstance.getSceneGraphObject() != null;
        assert propertyMetadata != null;

        this.fxomInstance = fxomInstance;
        this.propertyMetadata = propertyMetadata;
        this.newValue = newValue;
        this.oldValue = propertyMetadata.getValueObject(fxomInstance);
        this.description = description;
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        final Object currentValue = propertyMetadata.getValueObject(fxomInstance);
        return Objects.equals(newValue, currentValue) == false;
    }

    @Override
    public void execute() {
        redo();
    }

    @Override
    public void undo() {
        fxomDocument.beginUpdate();
        this.propertyMetadata.setValueObject(fxomInstance, oldValue);
        fxomDocument.endUpdate();
    }

    @Override
    public void redo() {
        fxomDocument.beginUpdate();
        this.propertyMetadata.setValueObject(fxomInstance, newValue);
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return description;
    }

}
