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

package com.gluonhq.jfxapps.core.job.editor.misc;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.metadata.AbstractMetadata;

/**
 * Job used to remove properties from an {@link FXOMObject} if the property is either:<br/>
 * 1) static like GridPane.columnIndex and the new parent class is different than the previous parent</br>
 * 2) without any meaning in another parent (like position/rotation/scaling). This list is provided by {@link SbMetadata#isPropertyTrimmingNeeded(com.gluonhq.jfxapps.core.fxom.util.PropertyName)}
 */
@Prototype
// FIXME test me as  metadata.isPropertyTrimmingNeeded has changed
public final class PrunePropertiesJob extends BatchDocumentJob {

    private FXOMObject fxomObject;
    private FXOMObject targetParent;

    private final AbstractMetadata metadata;
    private final FxomJobsFactory fxomJobsFactory;

    protected PrunePropertiesJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            AbstractMetadata metadata,
            FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory, documentManager);
        this.metadata = metadata;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(FXOMObject fxomObject, FXOMObject targetParent) {
        assert fxomObject != null;

        this.fxomObject = fxomObject;
        this.targetParent = targetParent;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;

            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (metadata.isPropertyTrimmingNeeded(fxomObject.getSceneGraphObject().getObjectClass() ,p.getName())) {
                    final Class<?> residentClass = p.getName().getResidenceClass();
                    final boolean prune;
                    if (residentClass == null) {
                        prune = true;
                    } else if (targetParent instanceof FXOMInstance) {
                        final FXOMInstance parentInstance = (FXOMInstance) targetParent;
                        prune = residentClass != parentInstance.getDeclaredClass();
                    } else {
                        assert (targetParent == null) || (targetParent instanceof FXOMCollection);
                        prune = true;
                    }
                    if (prune) {
                        result.add(fxomJobsFactory.removeProperty(p));
                    }
                }
            }
        }

        if ((fxomObject.getFxController() != null) && (targetParent != null)) {
            result.add(fxomJobsFactory.removeFxController(fxomObject));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Should not reach user
    }

}
