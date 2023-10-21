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

package com.oracle.javafx.scenebuilder.fxml.job.editor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.metadata.IMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemoveFxControllerJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemovePropertyJob;

/**
 * Job used to remove properties from an {@link FXOMObject} if the property is either:<br/>
 * 1) static like GridPane.columnIndex and the new parent class is different than the previous parent</br>
 * 2) without any meaning in another parent (like position/rotation/scaling). This list is provided by {@link Metadata#isPropertyTrimmingNeeded(com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName)}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class PrunePropertiesJob extends BatchDocumentJob {

    private FXOMObject fxomObject;
    private FXOMObject targetParent;

    private final IMetadata metadata;
    private final RemovePropertyJob.Factory removePropertyJobFactory;
    private final RemoveFxControllerJob.Factory removeFxControllerJobFactory;

    protected PrunePropertiesJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            IMetadata metadata,
            RemovePropertyJob.Factory removePropertyJobFactory,
            RemoveFxControllerJob.Factory removeFxControllerJobFactory) {
        super(extensionFactory, documentManager);
        this.removePropertyJobFactory = removePropertyJobFactory;
        this.removeFxControllerJobFactory = removeFxControllerJobFactory;
        this.metadata = metadata;
    }

    protected void setJobParameters(FXOMObject fxomObject, FXOMObject targetParent) {
        assert fxomObject != null;

        this.fxomObject = fxomObject;
        this.targetParent = targetParent;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;

            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (metadata.isPropertyTrimmingNeeded(p.getName())) {
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
                        result.add(removePropertyJobFactory.getJob(p));
                    }
                }
            }
        }

        if ((fxomObject.getFxController() != null) && (targetParent != null)) {
            result.add(removeFxControllerJobFactory.getJob(fxomObject));
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Should not reach user
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<PrunePropertiesJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  PrunePropertiesJob} job
         * @param fxomObject the object whose properties will be pruned
         * @param targetParent the new parent object
         * @return the job to execute
         */
        public PrunePropertiesJob getJob(FXOMObject fxomObject, FXOMObject targetParent) {
            return create(PrunePropertiesJob.class, j -> j.setJobParameters(fxomObject, targetParent));
        }
    }
}
