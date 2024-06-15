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
package com.gluonhq.jfxapps.core.api.fxom;

import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

public interface FxomJobsFactory {
    /**
     * Create a job.
     * Add the provided {@link FXOMProperty} into the provided {@link FXOMElement} at the specified index
     * @param property the property to add
     * @param targetInstance the instance receiving the value
     * @param targetIndex the instance property collection index receiving the value
     * @return the job to execute
     */
    Job addProperty(FXOMProperty property, FXOMElement targetInstance, int targetIndex);

    /**
     * Add a value to a collection property of a FXOMObject instance at a specific index
     * @param value the value to add
     * @param targetProperty the collection property receiving the value
     * @param targetIndex the collection property index receiving the value
     * @return the job to execute
     */
    Job addPropertyValue(FXOMObject value, FXOMPropertyC targetProperty, int targetIndex);

    /**
     * Modify or set the fx:controller value of a FXOMObject
     *
     * @param fxomObject the fxom object
     * @param newFxControllerValue the new fx:controller value
     * @return the job to execute
     */
    Job modifyFxController(FXOMObject fxomObject, String newFxControllerValue);

    /**
     * Modify or set the fx:id of an fxom object
     *
     * @param fxomObject the fxom object
     * @param newFxIdValue the new fx:id value
     * @return the job to execute
     */
    Job modifyFxId(FXOMObject fxomObject, String newFxIdValue);

    /**
     * Update provided {@link FXOMElement} underlying scenegraph object property defined by the provided {@link ValuePropertyMetadata} with the new value.
     * @param fxomElement the {@link FXOMElement} owning the property
     * @param propertyMetadata the property definition
     * @param newValue the new value
     * @return the job to execute
     */
    Job modifyObject(FXOMElement fxomElement, ValuePropertyMetadata propertyMetadata, Object newValue);

    /**
     * Move or insert a reindexed object before a given object
     *
     * @param reindexedObject the reindexed object
     * @param beforeObject the object before which the reindexed object will be inserted
     * @return the job to execute
     */
    Job reIndexObject(FXOMObject reindexedObject, FXOMObject beforeObject);

    /**
     * Remove an item from his parent collection property of a FXOMObject instance
     * @param value the value to remove
     * @return the job to execute
     */
    Job removeCollectionItem(FXOMObject value);

    /**
     * Remove the fx:controller value of a FXOMObject
     *
     * @param fxomObject the object whose fx:controller property will be removed
     * @return the job to execute
     */
    Job removeFxController(FXOMObject fxomObject);

    /**
     * Remove the provided {@link FXOMNode} from its owner
     * @param targetNode the {@link FXOMNode} to delete
     * @return the job to execute
     */
    Job removeNode(FXOMNode targetNode);

    /**
     * Remove the provided {@link FXOMObject} from its owner property/collection
     * @param targetObject the object to remove
     * @return the job to execute
     */
    Job removeObject(FXOMObject targetObject);

    /**
     * Remove the provided {@link FXOMProperty} from its parent
     * @param targetProperty the property to remove
     * @return the job to execute
     */
    Job removeProperty(FXOMProperty targetProperty);

    /**
     * Remove the provided {@link FXOMObject} from its parent {@link FXOMProperty} value
     * @param value the value to remove
     * @return the job to execute
     */
    Job removePropertyValue(FXOMObject value);

    /**
     * It removes the provided FXOMObject from his parent property provided by FXOMObject.getParentProperty()
     * or FXOMObject.getParentCollection()regardless if the parent property is a collection or not.
     * and insert the replacement FXOMObject at the same index
     * @param original the object to replace
     * @param replacement the replacement object
     * @return the job to execute
     */
    Job replaceObject(FXOMObject original, FXOMObject replacement);

    /**
     * It removes the provided FXOMPropertyT from his parent FXOMElement provided by
     * FXOMPropertyT.getParentInstance()by an FXOMPropertyC containing the new
     * FXOMObject value.This peculiar case happens mainly when replacing a reference
     * by the referee scenegraph
     *
     * @param reference the property containing the reference expression
     * @return the job to execute
     */
    Job replacePropertyValue(FXOMPropertyT hostProperty, FXOMObject newValue);

    /**
     * Set the root of the current document {@link FXOMDocument} with the provided {@link FXOMObject}
     *
     * @param newRoot the new root of current {@link FXOMDocument}
     * @return the job to execute
     */
    Job setFxomRoot(FXOMObject newRoot);

    /**
     * enable/disable fx:root on the current {@link FXOMDocument}
     * @return the job to execute
     */
    Job toggleFxRoot();

    /**
     * updates the FXOM document at execution time. Replace the reference id from
     * the {@link FXOMPropertyT} by the original referee {@link FXOMObject}<br/>
     * The referee is moved from his original location
     *
     * @param reference the property containing the reference expression
     * @return the job to execute
     */
    Job combineExpressionReference(FXOMPropertyT reference);

    /**
     * updates the FXOM document at execution time. Replace the reference id from
     * the {@link FXOMIntrinsic} by the original referee {@link FXOMObject}<br/>
     * The referee is moved from his original location
     *
     * @param reference
     * @return the job to execute
     */
    Job combineIntrinsicReference(FXOMIntrinsic reference);

    /**
     * updates the FXOM document at execution time. It replace the reference id from
     * the {@link FXOMNode} by the original referee {@link FXOMObject}<br/>
     * The referee is moved from his original location<br/>
     * If {@link FXOMIntrinsic}, delegates to
     * {@link CombineIntrinsicReferenceJob}<br/>
     * If {@link FXOMPropertyT}, delegates to
     * {@link CombineExpressionReferenceJob}<br/>
     * else bug
     *
     * @param reference
     * @return the job to execute
     */
    Job combineReference(FXOMNode reference);

    /**
     * updates the FXOM document at execution time. Delete an {@link FXOMObject} if
     * there is no reference to it. If some reference exists the {@link FXOMObject}
     * is moved in place of the first found reference If weak reference found, they
     * are deleted
     *
     * @param target the object to delete
     * @return the job to execute
     */
    Job deleteRefereeObject(FXOMObject target);

    /**
     * find the reference id contained in the provided {@link FXOMPropertyT} then
     * replace it by cloning the referee using the provided {@link FXOMCloner}
     *
     * @param reference the property containing the reference expression
     * @param cloner    the cloner
     * @return the job to execute
     */
    Job expandExpressionReference(FXOMPropertyT reference, FXOMCloner cloner);

    /**
     * find the reference id contained in source attribute of an
     * {@link FXOMIntrinsic} then replace it by cloning the referee using the
     * provided {@link FXOMCloner}
     *
     * @param reference the {@link FXOMIntrinsic} reference
     * @param cloner    the cloner
     * @return the job to execute
     */
    Job expandIntrinsicReference(FXOMIntrinsic reference, FXOMCloner cloner);

    /**
     * Find a reference in the provided {@link FXOMNode} then replace it by cloning
     * the referee using the provided {@link FXOMCloner} For {@link FXOMIntrinsic}
     * delegates to {@link ExpandIntrinsicReferenceJob} For {@link FXOMPropertyT}
     * delegates to {@link ExpandExpressionReferenceJob}
     *
     * @param reference the {@link FXOMNode} containing the reference
     * @param cloner    the cloner
     * @return the job to execute
     */
    Job expandReference(FXOMNode reference, FXOMCloner cloner);

    /**
     * look for all reference in an {@link FXOMDocument} and for each reference r:
     * <br/>
     * r is a forward reference<br/>
     * 0) r is a toggleGroup reference<br/>
     * => if toggle group exists, we swap it with the reference<br/>
     * => if not, replace the reference by a new toggle group<br/>
     * 1) r is a weak reference (like labelFor)<br/>
     * => we remove the reference<br/>
     * 2) else r is a strong reference<br/>
     * => we expand the reference<br/>
     *
     * @return the job to execute
     */
    Job referencesUpdater();

    /**
     * look for all reference in an {@link FXOMDocument} and update them with the
     * referee content
     *
     * @param subJob the sub job
     * @return the job to execute
     */
    Job updateReferences(Job subJob);

    /**
     * used to remove properties from an {@link FXOMObject} if the property is
     * either:<br/>
     * 1) static like GridPane.columnIndex and the new parent class is different
     * than the previous parent</br>
     * 2) without any meaning in another parent (like position/rotation/scaling).
     * This list is provided by
     * {@link AbstractMetadata#isPropertyTrimmingNeeded(Class, com.gluonhq.jfxapps.core.fxom.util.PropertyName)<?>,com.gluonhq.jfxapps.core.fxom.util.PropertyName)}
     *
     * @param fxomObject   the object whose properties will be pruned
     * @param targetParent the new parent object
     * @return the job to execute
     */
    Job pruneProperties(FXOMObject fxomObject, FXOMObject targetParent);
}
