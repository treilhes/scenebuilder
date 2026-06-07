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
package com.oracle.javafx.scenebuilder.document.hierarchy.display;

import com.oracle.javafx.scenebuilder.api.mask.SbHierarchyMask;
import com.oracle.javafx.scenebuilder.document.api.AbstractDisplayOption;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobManager;
import com.treilhes.jfxplace.fxom.api.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.fxom.model.FXOMElement;
import com.treilhes.jfxplace.fxom.model.util.PropertyName;

/**
 *
 */
public abstract class AbstractPropertyDisplayOption extends AbstractDisplayOption {

    private final JobManager jobManager;
    private final FxomJobsFactory fxomJobsFactory;

    public AbstractPropertyDisplayOption(
            JobManager jobManager,
            FxomJobsFactory fxomJobsFactory) {
        super();
        this.jobManager = jobManager;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    abstract PropertyName getTargetProperty(SbHierarchyMask mask);

    @Override
    public String getResolvedValue(SbHierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return null;
        }

        Object value = mask.getPropertySceneGraphValue(propName);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    @Override
    public String getValue(SbHierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return null;
        }

        Object value = mask.getPropertyValue(propName);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    @Override
    public boolean isReadOnly(SbHierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return true;
        }

        return mask.isReadOnlyProperty(propName) && !mask.isResourceKey(propName);
    }

    @Override
    public boolean isMultiline(SbHierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return false;
        }

        return mask.isMultilineProperty(propName);
    }

    @Override
    public boolean hasValue(SbHierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return false;
        }

        return mask.hasProperty(propName);
    }

    @Override
    public void setValue(SbHierarchyMask mask, String newValue) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return;
        }

        var vpm = mask.getPropertyMetadata(propName);

        if (vpm != null) {
            final Job job1 = fxomJobsFactory.modifyObject((FXOMElement)mask.getFxomObject(), vpm, newValue);
            if (job1.isExecutable()) {
                jobManager.push(job1);
            }
        }
    }

}
