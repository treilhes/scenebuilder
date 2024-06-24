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

package com.oracle.javafx.scenebuilder.job.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.job.internal.atomic.RelocateNodeJob;

import javafx.geometry.Point2D;

/**
 * Update the layout position of given {@link FXOMObject} objects to their mapped position {@link Point2D}
 */
@Prototype
public final class RelocateSelectionJob extends BatchDocumentJob {

    private static final long MERGE_PERIOD = 1000; //  milliseconds

    private final SbJobsFactory sbJobsFactory;
    private final Map<FXOMObject, Point2D> locationMap = new HashMap<>();
    private long time = System.currentTimeMillis();



    // @formatter:off
    protected RelocateSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            SbJobsFactory sbJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.sbJobsFactory = sbJobsFactory;
    }

    public void setJobParameters(Map<FXOMObject, Point2D> locationMap) {
        this.locationMap.putAll(locationMap);
    }

    /**
     * This job is collapsible with other if:<br/>
     *      0) other is a RelocateSelectionJob instance<br/>
     *      1) other is younger than this of 1000 ms no more<br/>
     *      2) other and this have the same location map keys<br/>
     */
    @Override
    public boolean canBeMergedWith(Job other) {

        final boolean result;
        if (other instanceof RelocateSelectionJob) {
            final RelocateSelectionJob otherRelocate = (RelocateSelectionJob)other;
            final long timeDifference = otherRelocate.time - this.time;
            if ((0 <= timeDifference) && (timeDifference < MERGE_PERIOD)) {
                final Set<FXOMObject> thisKeys = this.locationMap.keySet();
                final Set<FXOMObject> otherKeys = otherRelocate.locationMap.keySet();
                result = thisKeys.equals(otherKeys);
            } else {
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public void mergeWith(Job younger) {
        assert canBeMergedWith(younger); // (1)
        assert younger instanceof RelocateSelectionJob; // Because (1)

        final RelocateSelectionJob youngerSelection = (RelocateSelectionJob) younger;
        for (Job subJob : getSubJobs()) {
            assert subJob instanceof RelocateNodeJob;
            final RelocateNodeJob thisRelocateJob
                    = (RelocateNodeJob) subJob;
            final RelocateNodeJob youngerRelocateJob
                    = youngerSelection.lookupSubJob(thisRelocateJob.getFxomInstance());
            thisRelocateJob.mergeWith(youngerRelocateJob);
        }

        this.time = youngerSelection.time;
    }


    public RelocateNodeJob lookupSubJob(FXOMObject fxomObject) {
        RelocateNodeJob result = null;

        for (Job subJob : getSubJobs()) {
            assert subJob instanceof RelocateNodeJob;
            final RelocateNodeJob relocateJob = (RelocateNodeJob) subJob;
            if (relocateJob.getFxomInstance() == fxomObject) {
                result = relocateJob;
                break;
            }
        }

        return result;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        for (Map.Entry<FXOMObject, Point2D> entry : locationMap.entrySet()) {
            assert entry.getKey() instanceof FXOMInstance;
            final FXOMInstance fxomInstance = (FXOMInstance) entry.getKey();
            final Point2D layoutXY = entry.getValue();
            final Job relocateJob = sbJobsFactory.relocateNode(fxomInstance,layoutXY.getX(), layoutXY.getY());
            result.add(relocateJob);
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;

        final Set<FXOMObject> movedObjects = locationMap.keySet();
        if (locationMap.size() == 1) {
            final FXOMObject movedObject = movedObjects.iterator().next();
            final Object sceneGraphObject = movedObject.getSceneGraphObject().get();
            if (sceneGraphObject == null) {
                result = I18N.getString("drop.job.move.single.unresolved");
            } else {
                result = I18N.getString("drop.job.move.single.resolved",
                        sceneGraphObject.getClass().getSimpleName());
            }
        } else {
            final Set<Class<?>> classes = new HashSet<>();
            int unresolvedCount = 0;
            for (FXOMObject o : movedObjects) {
                if (!o.getSceneGraphObject().isEmpty()) {
                    classes.add(o.getSceneGraphObject().getObjectClass());
                } else {
                    unresolvedCount++;
                }
            }
            final boolean homogeneous = (classes.size() == 1) && (unresolvedCount == 0);

            if (homogeneous) {
                final Class<?> singleClass = classes.iterator().next();
                result = I18N.getString("drop.job.move.multiple.homogeneous",
                        movedObjects.size(),
                        singleClass.getSimpleName());
            } else {
                result = I18N.getString("drop.job.move.multiple.heterogeneous",
                        movedObjects.size());
            }
        }

        return result;
    }
}
