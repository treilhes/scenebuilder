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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.gluonhq.jfxapps.core.api.fxom.editor.selection.FxomSelection;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.jobs.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.UnidimensionalComparator;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;

import javafx.geometry.Orientation;
import javafx.scene.control.ToolBar;

/**
 * Job used to wrap selection in a ToolBar.
 */
@ApplicationInstancePrototype
public final class WrapInToolBarJob extends AbstractWrapInSubComponentJob {

    //@formatter:off
    protected WrapInToolBarJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            FxomSelection selection,
            SbMetadata metadata,
            SbFXOMObjectMask.Factory designMaskFactory,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        //@formatter:on
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, fxomJobsFactory,
                selectionJobsFactory, objectSelectionGroupFactory);
        newContainerClass = ToolBar.class;
    }

    @Override
    protected Collection<FXOMObject> sortChildren(List<FXOMObject> children) {
        final List<FXOMObject> sorted = new ArrayList<>(children);
        Collections.sort(sorted, UnidimensionalComparator.of(Orientation.HORIZONTAL));
        return sorted;
    }

    @ApplicationInstanceSingleton
    public final static class Factory extends JobFactory<WrapInToolBarJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInToolBarJob} job
         * @return the job to execute
         */
        public WrapInToolBarJob getJob() {
            return create(WrapInToolBarJob.class, null);
        }
    }

}
