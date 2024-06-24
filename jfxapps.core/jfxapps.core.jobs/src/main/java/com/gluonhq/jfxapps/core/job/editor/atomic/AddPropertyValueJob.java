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
package com.gluonhq.jfxapps.core.job.editor.atomic;

import java.util.Collection;
import java.util.Collections;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;


/**
 * This Job updates the FXOM document at execution time.
 * It adds the provided {@link FXOMObject} into the provided collection property {@link FXOMPropertyC} at the specified index
 * Use the dedicated {@link Factory} to create an instance
 */
@Prototype
public final class AddPropertyValueJob extends AbstractJob {

    private final FXOMDocument fxomDocument;

    private FXOMObject value;
    private FXOMPropertyC targetProperty;
    private int targetIndex;

    protected AddPropertyValueJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters(FXOMObject value, FXOMPropertyC targetProperty, int targetIndex) {
        assert value != null;
        assert targetProperty != null;
        assert targetIndex >= -1;

        this.value = value;
        this.targetProperty = targetProperty;
        this.targetIndex = targetIndex;
    }
    /*
     * AddPropertyValueJob
     */

    @Override
    public boolean isExecutable() {
        return (value.getParentProperty() == null)
                && (value.getParentCollection() == null);
    }

    @Override
    public void doExecute() {
        assert targetIndex <= targetProperty.getChildren().size();
        doRedo();
    }

    @Override
    public void doUndo() {
        assert value.getParentProperty() == targetProperty;
        assert value.getParentCollection() == null;

        fxomDocument.beginUpdate();
        value.removeFromParentProperty();
        fxomDocument.endUpdate();

        assert value.getParentProperty() == null;
        assert value.getParentCollection() == null;
    }

    @Override
    public void doRedo() {
        assert value.getParentProperty() == null;
        assert value.getParentCollection() == null;

        fxomDocument.beginUpdate();
        value.addToParentProperty(targetIndex, targetProperty);
        fxomDocument.endUpdate();

        //TODO ensure the new framework execute the following alert
        //WarnThemeAlert.showAlertIfRequired(getEditorController(), value, getEditorController().getOwnerWindow());

        assert value.getParentProperty() == targetProperty;
        assert value.getParentCollection() == null;
    }

	public FXOMObject getValue() {
		return value;
	}

	public FXOMPropertyC getTargetProperty() {
		return targetProperty;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

}
