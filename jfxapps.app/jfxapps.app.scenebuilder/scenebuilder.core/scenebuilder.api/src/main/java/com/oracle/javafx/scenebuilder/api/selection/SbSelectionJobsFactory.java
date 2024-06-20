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
package com.oracle.javafx.scenebuilder.api.selection;

import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.layout.AnchorPane;

public interface SbSelectionJobsFactory {

    /**
     * Add a default context menu into the currently selected objects if those
     * objects have a contextMenu property
     *
     * @return the job to execute
     * @deprecated the new ui allow to insert a context menu using standard drag and
     *             drop jobs
     */
    @Deprecated
    Job addContextMenuToSelection();

    /**
     * Add a default tooltip into the currently selected objects if those objects
     * have a tooltip property
     *
     * @return the job to execute
     * @deprecated the new ui allow to insert a tooltip using standard drag and drop
     *             jobs
     */
    @Deprecated
    Job addTooltipToSelection();

    /**
     * Only if parent object is {@link AnchorPane} Force the selected objects
     * {@link FXOMObject} to the same size of the parent {@link AnchorPane}
     *
     * @return the job to execute
     */
    Job fitToParentSelection();

    /**
     * This job set the property defined by the provided
     * {@link ValuePropertyMetadata}<br/>
     * but it handles only the cacheHint property or generate an assertion
     * error<br/>
     * This job links the modification of the cacheHint property to the cache
     * property<br/>
     * If the new value is not DEFAULT it sets cache to true<br/>
     * FLAW: currently the modification of the cache property is not reflected in
     * the inspector until you deselect adn reselect the object
     *
     * @param propertyMetadata the definition of property to set (expected to be
     *                         cacheHint)
     * @param newValue         the new value of the property to set
     * @return the job to execute
     */
    Job modifyCacheHint(ValuePropertyMetadata propertyMetadata, Object newValue);

    /**
     * apply the constant USE_COMPUTED_SIZE on width and height on the currently
     * selected objects
     *
     * @return the job to execute
     */
    Job useComputedSizesSelection();

}
