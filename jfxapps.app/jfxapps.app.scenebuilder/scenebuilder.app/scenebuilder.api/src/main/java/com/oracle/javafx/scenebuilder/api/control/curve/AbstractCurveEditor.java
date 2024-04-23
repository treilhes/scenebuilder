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

package com.oracle.javafx.scenebuilder.api.control.curve;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.api.control.CurveEditor;
import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide;
import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide.Tunable;

import javafx.scene.Node;

public abstract class AbstractCurveEditor<T extends Node> implements CurveEditor<T>{

    protected FXOMObject fxomObject;
    protected T sceneGraphObject;

    public AbstractCurveEditor() {
    }

    public void setFxomObject(FXOMObject fxomObject) {
        assert fxomObject != null;
        assert fxomObject.getSceneGraphObject() != null;
        assert fxomObject.getSceneGraphObject().isNode();

        this.fxomObject = fxomObject;
        this.sceneGraphObject = (T)fxomObject.getSceneGraphObject().get();
    }

    @Override
    public T getSceneGraphObject() {
        return sceneGraphObject;
    }

    @Override
    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    public abstract void initialize();

    @Override
    public abstract EditCurveGuide createController(EnumMap<Tunable, Integer> tunableMap);

    @Override
    public abstract void moveTunable(EnumMap<Tunable, Integer> tunableMap, double newX, double newY);
    @Override
    public abstract void revertToOriginalState();

    @Override
    public abstract List<PropertyName> getPropertyNames();

    @Override
    public abstract Object getValue(PropertyName propertyName);

    @Override
    public abstract Map<PropertyName, Object> getChangeMap();

    @Override
    public abstract List<Double> getPoints();
    @Override
    public abstract void addPoint(EnumMap<Tunable, Integer> tunableMap, double newX, double newY);
    @Override
    public abstract void removePoint(EnumMap<Tunable, Integer> tunableMap);
}
