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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.selection.job.SetDocumentRootJob;

import javafx.scene.text.TextFlow;

/**
 * Job used to wrap selection in a TextFlow.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class WrapInTextFlowJob extends AbstractWrapInSubComponentJob {

    protected WrapInTextFlowJob(JobExtensionFactory extensionFactory, ApplicationInstanceEvents documentManager,
            Selection selection, FXOMObjectMask.Factory designMaskFactory, IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            FxomJobsFactory fxomJobsFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, addPropertyJobFactory, objectSelectionGroupFactory);
        newContainerClass = TextFlow.class;
    }


    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<WrapInTextFlowJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInTextFlowJob} job
         * @return the job to execute
         */
        public WrapInTextFlowJob getJob() {
            return create(WrapInTextFlowJob.class, null);
        }
    }
}
