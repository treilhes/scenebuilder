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
package com.oracle.javafx.scenebuilder.api.job;

import java.util.Map;

import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;

import javafx.geometry.Point2D;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public interface SbJobsFactory {

    /**
     * {@link FXOMInstance} cannot be root Only if parent object is
     * {@link AnchorPane} Force the provided {@link FXOMInstance} to the same size
     * of the parent {@link AnchorPane}
     *
     * @param fxomInstance the fxom instance with an {@link AnchorPane} as parent
     * @return the job to execute
     */
    Job fitToParentObject(FXOMInstance fxomInstance);

    /**
     * Update the layout position of given {@link FXOMObject} objects to their
     * mapped position {@link Point2D}
     *
     * @param locationMap the location map
     * @return the job to execute
     */
    Job relocateSelection(Map<FXOMObject, Point2D> locationMap);

    /**
     * Apply the constant USE_COMPUTED_SIZE on width and height of the provided
     * {@link FXOMInstance}
     *
     * @param fxomInstance the target {@link FXOMInstance}
     * @return the job to execute
     */
    Job useComputedSizesObject(FXOMInstance fxomInstance);

    /**
     * use for setting the size of the given FXOMObject; when not provided deal with
     * the top level item of the layout. The job will set the preferred width and
     * height to the given value while min and max width and height are set to
     * Region.USE_PREF_SIZE. No action is taken unless the FXOMObject is an instance
     * of Region or WebView.
     *
     * @param size       the new size
     * @param fxomObject the target {@link FXOMObject}, if null the target is
     *                   {@link FXOMDocument#getFxomRoot()}
     * @return the job to execute
     */
    Job usePredefinedSize(Size size, FXOMObject fxomObject);

    /**
     * deal with the top level item of the layout. The job will set the preferred
     * width and height to the given value while min and max width and height are
     * set to Region.USE_PREF_SIZE. No action is taken unless the FXOMObject is an
     * instance of Region or WebView. job that target
     * {@link FXOMDocument#getFxomRoot()}
     *
     * @param size the new size
     * @return the job to execute
     */
    Job usePredefinedSize(Size size);

    /**
     * use for setting the size of the given FXOMObject; when not provided deal with
     * the top level item of the layout. The job will set the preferred width and
     * height to the given value while min and max width and height are set to
     * Region.USE_PREF_SIZE. No action is taken unless the FXOMObject is an instance
     * of Region or WebView.
     *
     * @param width      the new width
     * @param height     the new height
     * @param fxomObject the target {@link FXOMObject}, if null the target is
     *                   {@link FXOMDocument#getFxomRoot()}
     * @return the job to execute
     */
    Job useSize(double width, double height, FXOMObject fxomObject);

    /**
     * deal with the top level item of the layout. The job will set the preferred
     * width and height to the given value while min and max width and height are
     * set to Region.USE_PREF_SIZE. No action is taken unless the FXOMObject is an
     * instance of Region or WebView. job that target
     * {@link FXOMDocument#getFxomRoot()}
     *
     * @param width  the new width
     * @param height the new height
     * @return the job to execute
     */
    Job useSize(double width, double height);

    /**
     * Update the layout position of a given {@link FXOMObject}
     *
     * @param fxomInstance the fxom instance
     * @param newLayoutX   the new layout X
     * @param newLayoutY   the new layout Y
     * @return the job to execute
     */
    Job relocateNode(FXOMInstance fxomInstance, double newLayoutX, double newLayoutY);

    /**
     * creates a {@link ToggleGroup} in place of a toggleGroup reference<br/>
     * If the referee {@link ToggleGroup} exists, it is moved<br/>
     * If not, a new {@link ToggleGroup} is created<br/>
     *
     * @param reference the reference
     * @return the job to execute
     */
    Job fixToggleGroupExpressionReference(FXOMPropertyT reference);

    /**
     * creates a {@link ToggleGroup} in place of a toggleGroup referenced using an
     * {@link FXOMIntrinsic} (fx:reference)<br/>
     * If the referee {@link ToggleGroup} exists, it is switched with the
     * reference<br/>
     * If not, a new {@link ToggleGroup} is created<br/>
     *
     * @param reference the reference
     * @return the job to execute
     */
    Job fixToggleGroupIntrinsicReference(FXOMIntrinsic reference);

    /**
     * Find {@link ToggleGroup} reference in the provided {@link FXOMNode} and
     * replace it by an instance of {@link ToggleGroup} For {@link FXOMIntrinsic}
     * delegates to {@link #fixToggleGroupIntrinsicReference(FXOMIntrinsic)} For
     * {@link FXOMPropertyT} delegates to
     * {@link #fixToggleGroupExpressionReference(FXOMPropertyT)}
     *
     * @param reference reference the {@link FXOMNode} containing the reference
     * @return the job to execute
     */
    Job fixToggleGroupReference(FXOMNode reference);

}
