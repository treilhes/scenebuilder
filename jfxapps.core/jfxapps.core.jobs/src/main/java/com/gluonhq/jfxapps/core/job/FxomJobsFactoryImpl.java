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
package com.gluonhq.jfxapps.core.job;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.AddPropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxIdJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReIndexObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveCollectionItemJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveNodeJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemovePropertyValueJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplaceObjectJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ReplacePropertyValueJobT;
import com.gluonhq.jfxapps.core.job.editor.atomic.SetFxomRootJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;
import com.gluonhq.jfxapps.core.job.editor.misc.PrunePropertiesJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineExpressionReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineIntrinsicReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.CombineReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.DeleteRefereeObjectJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandExpressionReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandIntrinsicReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ExpandReferenceJob;
import com.gluonhq.jfxapps.core.job.editor.reference.ReferencesUpdaterJob;
import com.gluonhq.jfxapps.core.job.editor.reference.UpdateReferencesJob;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

/**
 *
 */
public class FxomJobsFactoryImpl extends JobFactory<Job> implements FxomJobsFactory {
    public FxomJobsFactoryImpl(JfxAppContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job addProperty(FXOMProperty property, FXOMElement targetInstance, int targetIndex) {
        return create(AddPropertyJob.class, j -> j.setJobParameters(property, targetInstance, targetIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job addPropertyValue(FXOMObject value, FXOMPropertyC targetProperty, int targetIndex) {
        return create(AddPropertyValueJob.class, j -> j.setJobParameters(value, targetProperty, targetIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job modifyFxController(FXOMObject fxomObject, String newFxControllerValue) {
        return create(ModifyFxControllerJob.class, j -> j.setJobParameters(fxomObject, newFxControllerValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job modifyFxId(FXOMObject fxomObject, String newFxIdValue) {
        return create(ModifyFxIdJob.class, j -> j.setJobParameters(fxomObject, newFxIdValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job modifyObject(FXOMElement fxomElement, ValuePropertyMetadata propertyMetadata, Object newValue) {
        return create(ModifyObjectJob.class, j -> j.setJobParameters(fxomElement, propertyMetadata, newValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job reIndexObject(FXOMObject reindexedObject, FXOMObject beforeObject) {
        return create(ReIndexObjectJob.class, j -> j.setJobParameters(reindexedObject, beforeObject));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removeCollectionItem(FXOMObject value) {
        return create(RemoveCollectionItemJob.class, j -> j.setJobParameters(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removeFxController(FXOMObject fxomObject) {
        return create(RemoveFxControllerJob.class, j -> j.setJobParameters(fxomObject));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removeNode(FXOMNode targetNode) {
        return create(RemoveNodeJob.class, j -> j.setJobParameters(targetNode));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removeObject(FXOMObject targetObject) {
        return create(RemoveObjectJob.class, j -> j.setJobParameters(targetObject));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removeProperty(FXOMProperty targetProperty) {
        return create(RemovePropertyJob.class, j -> j.setJobParameters(targetProperty));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job removePropertyValue(FXOMObject value) {
        return create(RemovePropertyValueJob.class, j -> j.setJobParameters(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job replaceObject(FXOMObject original, FXOMObject replacement) {
        return create(ReplaceObjectJob.class, j -> j.setJobParameters(original, replacement));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job replacePropertyValue(FXOMPropertyT hostProperty, FXOMObject newValue) {
        return create(ReplacePropertyValueJobT.class, j -> j.setJobParameters(hostProperty, newValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job setFxomRoot(FXOMObject newRoot) {
        return create(SetFxomRootJob.class, j -> j.setJobParameters(newRoot));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job toggleFxRoot() {
        return create(ToggleFxRootJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job combineExpressionReference(FXOMPropertyT reference) {
        return create(CombineExpressionReferenceJob.class, j -> j.setJobParameters(reference));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job combineIntrinsicReference(FXOMIntrinsic reference) {
        return create(CombineIntrinsicReferenceJob.class, j -> j.setJobParameters(reference));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job combineReference(FXOMNode reference) {
        return create(CombineReferenceJob.class, j -> j.setJobParameters(reference));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job deleteRefereeObject(FXOMObject target) {
        return create(DeleteRefereeObjectJob.class, j -> j.setJobParameters(target));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job expandExpressionReference(FXOMPropertyT reference, FXOMCloner cloner) {
        return create(ExpandExpressionReferenceJob.class, j -> j.setJobParameters(reference, cloner));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job expandIntrinsicReference(FXOMIntrinsic reference, FXOMCloner cloner) {
        return create(ExpandIntrinsicReferenceJob.class, j -> j.setJobParameters(reference, cloner));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job expandReference(FXOMNode reference, FXOMCloner cloner) {
        return create(ExpandReferenceJob.class, j -> j.setJobParameters(reference, cloner));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job referencesUpdater() {
        return create(ReferencesUpdaterJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job updateReferences(Job subJob) {
        return create(UpdateReferencesJob.class, j -> j.setJobParameters(subJob));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job pruneProperties(FXOMObject fxomObject, FXOMObject targetParent) {
        return create(PrunePropertiesJob.class, j -> j.setJobParameters(fxomObject, targetParent));
    }
}
