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

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMObject} from his parent collection property provided by {@link FXOMObject#getParentProperty()}
 * Use the dedicated {@link Factory} to create an instance
 */
@Prototype
public final class RemoveCollectionItemJob extends AbstractJob {

    private FXOMObject targetValue;

    private FXOMCollection parentCollection;
    private int indexInParentCollection;

    private FXOMDocument fxomDocument;

    protected RemoveCollectionItemJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager
            ) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters(FXOMObject value) {
        this.targetValue = value;

        setDescription(String.format("%s[%s]", getClass().getSimpleName(), targetValue.getGlueElement().getTagName())); // NOCHECK
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return targetValue.getParentCollection() != null;
    }

    @Override
    public void doExecute() {
        assert parentCollection == null;
        assert isExecutable();

        parentCollection = targetValue.getParentCollection();
        indexInParentCollection = targetValue.getIndexInParentCollection();

        // Now same as redo()
        doRedo();
    }

    @Override
    public void doUndo() {
        assert targetValue.getParentCollection() == null;

        fxomDocument.beginUpdate();
        targetValue.addToParentCollection(indexInParentCollection, parentCollection);
        fxomDocument.endUpdate();

        assert targetValue.getParentCollection() == parentCollection;
        assert targetValue.getIndexInParentCollection() == indexInParentCollection;
    }

    @Override
    public void doRedo() {
        assert targetValue.getParentCollection() == parentCollection;
        assert targetValue.getIndexInParentCollection() == indexInParentCollection;

        fxomDocument.beginUpdate();
        targetValue.removeFromParentCollection();
        fxomDocument.endUpdate();

        assert targetValue.getParentCollection() == null;
    }
}
