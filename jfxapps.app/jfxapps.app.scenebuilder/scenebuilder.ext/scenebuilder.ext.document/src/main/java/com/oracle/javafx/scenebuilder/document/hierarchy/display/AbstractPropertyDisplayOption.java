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
package com.oracle.javafx.scenebuilder.document.hierarchy.display;

import org.scenebuilder.fxml.api.HierarchyMask;

import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.document.api.AbstractDisplayOption;

/**
 *
 */
public abstract class AbstractPropertyDisplayOption extends AbstractDisplayOption {

    private final JobManager jobManager;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

    public AbstractPropertyDisplayOption(
            JobManager jobManager,
            ModifyObjectJob.Factory modifyObjectJobFactory) {
        super();
        this.jobManager = jobManager;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

    abstract PropertyName getTargetProperty(HierarchyMask mask);

    @Override
    public String getResolvedValue(HierarchyMask mask) {
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
    public String getValue(HierarchyMask mask) {
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
    public boolean isReadOnly(HierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return true;
        }

        return mask.isReadOnlyProperty(propName) && !mask.isResourceKey(propName);
    }

    @Override
    public boolean isMultiline(HierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return false;
        }

        return mask.isMultilineProperty(propName);
    }

    @Override
    public boolean hasValue(HierarchyMask mask) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return false;
        }

        return mask.hasProperty(propName);
    }

    @Override
    public void setValue(HierarchyMask mask, String newValue) {
        PropertyName propName = getTargetProperty(mask);

        if (propName == null) {
            return;
        }

        ValuePropertyMetadata vpm = mask.getPropertyMetadata(propName);

        if (vpm != null) {
            final AbstractJob job1 = modifyObjectJobFactory.getJob((FXOMInstance)mask.getFxomObject(), vpm, newValue);
            if (job1.isExecutable()) {
                jobManager.push(job1);
            }
        }
    }
}
