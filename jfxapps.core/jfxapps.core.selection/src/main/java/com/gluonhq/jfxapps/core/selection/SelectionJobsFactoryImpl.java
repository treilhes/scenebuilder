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
package com.gluonhq.jfxapps.core.selection;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.selection.job.BackupSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.BringForwardJob;
import com.gluonhq.jfxapps.core.selection.job.BringToFrontJob;
import com.gluonhq.jfxapps.core.selection.job.ClearSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.CutSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteObjectJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteObjectSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DeleteSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.DuplicateSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.InsertAsAccessoryJob;
import com.gluonhq.jfxapps.core.selection.job.InsertAsSubComponentJob;
import com.gluonhq.jfxapps.core.selection.job.ModifySelectionJob;
import com.gluonhq.jfxapps.core.selection.job.PasteIntoJob;
import com.gluonhq.jfxapps.core.selection.job.SendBackwardJob;
import com.gluonhq.jfxapps.core.selection.job.SendToBackJob;
import com.gluonhq.jfxapps.core.selection.job.SetDocumentRootJob;
import com.gluonhq.jfxapps.core.selection.job.TrimSelectionJob;
import com.gluonhq.jfxapps.core.selection.job.UpdateSelectionJob;

@ApplicationInstanceSingleton
public class SelectionJobsFactoryImpl extends JobFactory<Job> implements SelectionJobsFactory {

    protected SelectionJobsFactoryImpl(JfxAppContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job backupSelection() {
        return create(BackupSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job bringForward() {
        return create(BringForwardJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job bringToFront() {
        return create(BringToFrontJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job clearSelection() {
        return create(ClearSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job cutSelection() {
        return create(CutSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job deleteObject(FXOMObject fxomObject) {
        return create(DeleteObjectJob.class, j -> j.setJobParameters(fxomObject));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Job deleteObjectSelection() {
        return create(DeleteObjectSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * job manages the deletion of the objects contained in either an
     * ObjectSelectionGroup or a GridSelectionGroup depending on the selection.<br/>
     * For {@link ObjectSelectionGroup} delegates to
     * {@link DeleteObjectSelectionJob}<br/>
     * For {@link GridSelectionGroup} delegates to
     * {@link DeleteGridSelectionJob}<br/>
     *
     * @return the job to execute
     */
    @Override
    public Job deleteSelection() {
        return create(DeleteSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * Duplicate all object in the current selection
     *
     * @return the job to execute
     */
    @Override
    public Job duplicateSelection() {
        return create(DuplicateSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * used to insert new FXOM objects into an accessory location. Insert newObject
     * into accessory of targetObject at index targetindex
     *
     * @param newObject    the object to insert
     * @param targetObject the target object
     * @param accessory    the target object accesory
     * @param targetIndex  the accesory target index
     * @return the job to execute
     */
    @Override
    public Job insertAsAccessory(FXOMObject newObject, FXOMObject targetObject, Accessory accessory, int targetIndex) {
        return create(InsertAsAccessoryJob.class,
                j -> j.setJobParameters(newObject, targetObject, accessory, targetIndex));
    }

    /**
     * used to insert new FXOM objects into an accessory location. Insert newObject
     * into accessory of targetObject at the last index Create an
     * {@link InsertAsAccessoryJob} job
     *
     * @param newObject    the object to insert
     * @param targetObject the target object
     * @param accessory    the target object accesory
     * @return the job to execute
     */
    @Override
    public Job insertAsAccessory(FXOMObject newObject, FXOMObject targetObject, Accessory accessory) {
        return create(InsertAsAccessoryJob.class, j -> j.setJobParameters(newObject, targetObject, accessory, -1));
    }

    /**
     * set the property defined by the provided {@link ValuePropertyMetadata}<br/>
     * with the provided "value" on each object selected if the property is
     * available for the object
     *
     * @param propertyMetadata the definition of property to set
     * @param newValue         the new value of the property to set
     * @return the job to execute
     */
    @Override
    public Job modifySelection(ValuePropertyMetadata propertyMetadata, Object newValue) {
        return create(ModifySelectionJob.class, j -> j.setJobParameters(propertyMetadata, newValue));
    }

    /**
     * try to paste the current clipboard content (if valid {@link FXOMObject}) into
     * the main accessory of the selected object (only one item selected accepted)
     * or into the first accepting accesory of the selected object
     *
     * @return the job to execute
     */
    @Override
    public Job pasteInto() {
        return create(PasteIntoJob.class, j -> j.setJobParameters());
    }

    /**
     * bring the selected objects one step backward in the parent collection (index
     * - 1)
     *
     * @return the job to execute
     */
    @Override
    public Job sendBackward() {
        return create(SendBackwardJob.class, j -> j.setJobParameters());
    }

    /**
     * pull the selected {@link FXOMObject} objects at the top in the parent
     * collection (index==0)
     *
     * @return the job to execute
     */
    @Override
    public Job sendToBack() {
        return create(SendToBackJob.class, j -> j.setJobParameters());
    }

    /**
     * updates the FXOM document at execution time. It set the root of a document
     * {@link FXOMDocument} with the provided {@link FXOMObject}<br/>
     * The provided {@link FXOMObject} is cleaned from obsolete properties
     * {@link FXOMProperty}<br/>
     *
     * With default description (class name) and usePredefinedSize = false
     *
     * @param newRoot the {@link FXOMObject} menat to be the new root of the current
     *                document
     * @return the job to execute
     */
    @Override
    public Job setDocumentRoot(FXOMObject newRoot) {
        Job job = create(SetDocumentRootJob.class, j -> j.setJobParameters(newRoot));
        job.setDescription("Set root");
        return job;
    }

    /**
     * replace the current document root by the selected {@link FXOMObject}
     * discarding all ancestors and siblings
     *
     * This job is composed of subjobs:<br/>
     * 0) Remove fx:controller/fx:root (if defined) from the old root object if
     * any<br/>
     * 1) Unselect the candidate<br/>
     * => {@link ClearSelectionJob}<br/>
     * 2) Disconnect the candidate from its existing parent<br/>
     * => {@link DeleteObjectJob}<br/>
     * 3) Set the candidate as the root of the document<br/>
     * => {@link SetDocumentRootJob}<br/>
     * 4) Add fx:controller/fx:root (if defined) to the new root object<br/>
     *
     * @return the job to execute
     */
    @Override
    public Job trimSelection() {
        return create(TrimSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * Update the currently scoped document selection {@link Selection} with the
     * provided list of {@link FXOMObject}
     *
     * @param group the selection group to select
     * @return the job to execute
     */
    @Override
    public Job updateSelection(SelectionGroup group) {
        return create(UpdateSelectionJob.class, j -> j.setJobParameters(group));
    }

    @Override
    public Job insertAsSubComponent(FXOMObject newObject, FXOMObject targetObject, int targetIndex) {
        return create(InsertAsSubComponentJob.class, j -> j.setJobParameters(newObject, targetObject, targetIndex));
    }
}
