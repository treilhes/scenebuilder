/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.control.driver;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.CurveEditor;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.Pring;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.Tring;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 *
 */
public abstract class AbstractDriver implements Driver {

    protected final Content contentPanelController;

    public AbstractDriver(Content contentPanelController) {
        assert contentPanelController != null;
        this.contentPanelController = contentPanelController;
    }

    @Override
    public abstract Handles<?> makeHandles(FXOMObject fxomObject);
    @Override
    public abstract Pring<?> makePring(FXOMObject fxomObject);
    @Override
    public abstract Tring<?> makeTring(DropTarget dropTarget);
    @Override
    public abstract Resizer<?> makeResizer(FXOMObject fxomObject);
    @Override
    public abstract CurveEditor<?> makeCurveEditor(FXOMObject fxomObject);
    @Override
    public abstract FXOMObject refinePick(Node hitNode, double sceneX, double sceneY, FXOMObject fxomObject);
    @Override
    public abstract DropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY);
    @Override
    public abstract Node getInlineEditorBounds(FXOMObject fxomObject);
    @Override
    public abstract boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds);
}