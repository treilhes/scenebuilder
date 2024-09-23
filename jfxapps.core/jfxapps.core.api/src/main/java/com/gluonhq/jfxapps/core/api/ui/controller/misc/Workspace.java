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
package com.gluonhq.jfxapps.core.api.ui.controller.misc;

import java.util.Set;

import com.gluonhq.jfxapps.core.api.content.mode.ModeManager;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;

public interface Workspace {

    /**
     * The content Scaling property.
     *
     * @return the double property
     */
    DoubleProperty scalingProperty();

    /**
     * Return the scaling factor used by the content.
     *
     * @return the scaling factor used by the content.
     */
    default double getScaling() {
        return scalingProperty().get();
    }
    /**
     * Sets the scaling factor to be used by the content.
     *
     * @param scaling the scaling factor to be used by the content.
     */
    default void setScaling(double scaling) {
        scalingProperty().set(scaling);
    }

    Pane getGlassLayer();

    boolean isContentVisible();

    void hideContent();

    void showContent();

    //Throwable getLayoutException();

    void layoutContent(boolean applyCSS);

    void beginInteraction();

    void endInteraction();

    SubScene getContentSubScene();

    boolean isContentDisplayable();

    Parent getRoot();

    boolean isAutoResize3DContent();

    void setAutoResize3DContent(boolean autoResize3DContent);

    /**
     * Scrolls this content panel so that the selected objects are visible.
     */
    void scrollToSelection();

    void reveal(FXOMObject targetFxomObject);

    Node getWorkspacePane();

    //ModeManager getModeManager();

    FXOMObject searchWithNode(Node node, double x, double y);
    FXOMObject pick(double hitX, double hitY, Set<FXOMObject> pickExcludes);
    FXOMObject pick(double sceneX, double sceneY);
}
