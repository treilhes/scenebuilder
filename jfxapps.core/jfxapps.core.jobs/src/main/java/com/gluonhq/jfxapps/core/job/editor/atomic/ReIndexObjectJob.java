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
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMVirtual;

/**
 * Add an {@link FXOMObject} before 'beforeObject' into 'beforeObject''s parent
 */
@Prototype
public final class ReIndexObjectJob extends AbstractJob {

    private FXOMObject reindexedObject;
    private FXOMObject beforeObject;
    private FXOMObject oldBeforeObject;
    private FXOMDocument fxomDocument;

    protected ReIndexObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters(FXOMObject reindexedObject, FXOMObject beforeObject) {
        assert reindexedObject != null;

        this.reindexedObject = reindexedObject;
        this.beforeObject = beforeObject;
        this.oldBeforeObject = reindexedObject.getNextSlibing();

        setDescription(createDescription());
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return (beforeObject != oldBeforeObject);
    }

    @Override
    public void doExecute() {
        doRedo();
    }

    @Override
    public void doUndo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        reindexedObject.moveBeforeSibling(oldBeforeObject);
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        reindexedObject.moveBeforeSibling(beforeObject);
        fxomDocument.endUpdate();
    }

    private String createDescription() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Move ");

        if (reindexedObject instanceof FXOMInstance) {
            final Object sceneGraphObject = reindexedObject.getSceneGraphObject().get();
            if (sceneGraphObject != null) {
                sb.append(sceneGraphObject.getClass().getSimpleName());
            } else {
                sb.append("Unresolved Object");
            }
        } else if (reindexedObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else if (reindexedObject instanceof FXOMVirtual) {
            sb.append(reindexedObject.getClass().getSimpleName());
        } else {
            assert false;
            sb.append(reindexedObject.getClass().getSimpleName());
        }
        return sb.toString();
    }
}
