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
package com.gluonhq.jfxapps.core.api.editor.selection;

import com.gluonhq.jfxapps.core.api.clipboard.Clipboard;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.mask.Accessory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

public interface SelectionJobsFactory {

    /**
     * create a backup of the current selection by cloning the content of
     * {@link Selection#getGroup()} Undoing the job will restore the selection
     *
     * @return the job to execute
     */
    Job backupSelection();

    /**
     * bring the selected objects one step forward in the parent collection (index +
     * 1)
     *
     * @return the job to execute
     */
    Job bringForward();

    /**
     * push the selected {@link FXOMObject} objects at the bottom in the parent
     * collection (maxindex)
     *
     * @return the job to execute
     */
    Job bringToFront();

    /**
     * Clear the currently scoped {@link FXOMDocument} selection.
     *
     * @return the job to execute
     */
    Job clearSelection();

    /**
     * Store the selected {@link FXOMObject} objects into {@link Clipboard} and
     * remove them from the current {@link FXOMDocument}
     *
     * @return the job to execute
     */
    Job cutSelection();

    /**
     * Create an {@link DeleteObjectJob} job
     *
     * @param fxomObject the object to delete
     * @return the job to execute
     */
    Job deleteObject(FXOMObject fxomObject);

    /**
     * Delete all object in the current selection for ObjectSelectionGroup. If one
     * object can't be deleted then none are Delegate to {@link DeleteObjectJob}
     *
     * @return the job to execute
     */
    Job deleteObjectSelection();

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
    Job deleteSelection();

    /**
     * Duplicate all object in the current selection
     *
     * @return the job to execute
     */
    Job duplicateSelection();

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
    Job insertAsAccessory(FXOMObject newObject, FXOMObject targetObject, Accessory accessory, int targetIndex);

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
    Job insertAsAccessory(FXOMObject newObject, FXOMObject targetObject, Accessory accessory);

    /**
     * set the property defined by the provided {@link ValuePropertyMetadata}<br/>
     * with the provided "value" on each object selected if the property is
     * available for the object
     *
     * @param propertyMetadata the definition of property to set
     * @param newValue         the new value of the property to set
     * @return the job to execute
     */
    Job modifySelection(ValuePropertyMetadata propertyMetadata, Object newValue);

    /**
     * try to paste the current clipboard content (if valid {@link FXOMObject}) into
     * the main accessory of the selected object (only one item selected accepted)
     * or into the first accepting accesory of the selected object
     *
     * @return the job to execute
     */
    Job pasteInto();

    /**
     * bring the selected objects one step backward in the parent collection (index
     * - 1)
     *
     * @return the job to execute
     */
    Job sendBackward();

    /**
     * pull the selected {@link FXOMObject} objects at the top in the parent
     * collection (index==0)
     *
     * @return the job to execute
     */
    Job sendToBack();

    /**
     * updates the FXOM document at execution time. It set the root of a document
     * {@link FXOMDocument} with the provided {@link FXOMObject}<br/>
     * The provided {@link FXOMObject} is cleaned from obsolete properties
     * {@link FXOMProperty}<br/>
     * and resized according user preferences.<br/>
     *
     * @param newRoot           the {@link FXOMObject} menat to be the new root of
     *                          the current document
     * @param usePredefinedSize if true, newRoot will be resized according user
     *                          predefined size
     * @param description       the job description
     * @return the job to execute
     */
    Job setDocumentRoot(FXOMObject newRoot, boolean usePredefinedSize, String description);

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
    Job setDocumentRoot(FXOMObject newRoot);

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
    Job trimSelection();

    /**
     * Update the currently scoped document selection {@link Selection} with the
     * provided list of {@link FXOMObject}
     *
     * @param group the selection group to select
     * @return the job to execute
     */
    Job updateSelection(SelectionGroup group);


}
