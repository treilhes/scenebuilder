/*
 * Copyright (c) 2016, 2026, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2026, Pascal Treilhes and/or its affiliates.
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

import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.javafx.scene.control.SplitPaneMetadata;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.BidimensionalComparator;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.GridCourse;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.UnidimensionalComparator;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.fxom.api.editor.selection.FxomSelection;
import com.treilhes.jfxplace.fxom.api.editor.selection.ObjectSelectionGroup;
import com.treilhes.jfxplace.fxom.api.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.fxom.api.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMObject;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

/**
 * Job used to wrap selection in a SplitPane.
 */
@ApplicationInstancePrototype
public final class WrapInSplitPaneJob extends AbstractWrapInSubComponentJob {

    //@formatter:off
    protected WrapInSplitPaneJob(
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
        newContainerClass = SplitPane.class;
    }

    @Override
    protected void modifyNewContainer(final List<FXOMObject> children) {
        super.modifyNewContainer(children);

        // Update the SplitPane orientation depending on its children positionning
        final Orientation orientation = getOrientation(children);
        SplitPaneMetadata.orientationPropertyMetadata.setValue(newContainer, orientation.name());
    }

    @Override
    protected Collection<FXOMObject> sortChildren(List<FXOMObject> children) {
        final List<FXOMObject> sorted = new ArrayList<>(children);
        final Orientation orientation = getOrientation(children);
        Collections.sort(sorted, UnidimensionalComparator.of(orientation));
        return sorted;
    }

    private Orientation getOrientation(final List<FXOMObject> fxomObjects) {
        int cols = computeSizeByCourse(fxomObjects, GridCourse.COL_BY_COL);
        if (cols == fxomObjects.size()) {
            return Orientation.HORIZONTAL;
        }
        int rows = computeSizeByCourse(fxomObjects, GridCourse.ROW_BY_ROW);
        if (rows == fxomObjects.size()) {
            return Orientation.VERTICAL;
        }
        final Orientation orientation = cols >= rows
                ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        return orientation;
    }

    private int computeSizeByCourse(
            final List<FXOMObject> fxomObjects,
            final GridCourse course) {

        final BidimensionalComparator comparator = new BidimensionalComparator(course);
        FXOMObject lastObject = null;
        int rc = 0;
        int max = -1;
        for (FXOMObject currentObject : fxomObjects) {
            if (lastObject != null) {
                if (comparator.compare(lastObject, currentObject) != 0) {
                    final Node lastNode = lastObject.getSceneGraphObject().getAs(Node.class);
                    final Node currentNode = currentObject.getSceneGraphObject().getAs(Node.class);
                    final Bounds lastBounds = lastNode.getBoundsInParent();
                    final Bounds currentBounds = currentNode.getBoundsInParent();
                    if (course.getMinY(currentBounds) >= course.getMaxY(lastBounds)) {
                        rc++;
                    }
                }
            }
            max = Math.max(max, rc);
            lastObject = currentObject;
        }
        return max;
    }

    @ApplicationInstanceSingleton
    public static final class Factory extends JobFactory<WrapInSplitPaneJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInSplitPaneJob} job
         * @return the job to execute
         */
        public WrapInSplitPaneJob getJob() {
            return create(WrapInSplitPaneJob.class, null);
        }
    }
}
