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

package com.oracle.javafx.scenebuilder.drivers.line;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.api.control.curve.AbstractCurveEditor;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.util.MathUtils;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class LineEditor extends AbstractCurveEditor<Line> {

    private double originalStartX;
    private double originalStartY;
    private double originalEndX;
    private double originalEndY;

    private final PropertyName startXName = new PropertyName("startX"); //NOCHECK
    private final PropertyName startYName = new PropertyName("startY"); //NOCHECK
    private final PropertyName endXName = new PropertyName("endX"); //NOCHECK
    private final PropertyName endYName = new PropertyName("endY"); //NOCHECK
    private final List<PropertyName> propertyNames = new ArrayList<>();

    public LineEditor() {
        super();
        propertyNames.add(startXName);
        propertyNames.add(startYName);
        propertyNames.add(endXName);
        propertyNames.add(endYName);
    }
    
    @Override
    public void initialize() {
        originalStartX = sceneGraphObject.getStartX();
        originalStartY = sceneGraphObject.getStartY();
        originalEndX = sceneGraphObject.getEndX();
        originalEndY = sceneGraphObject.getEndY();
    }

    @Override
    public EditCurveGuideController createController(EnumMap<Tunable, Integer> tunableMap) {

        final EditCurveGuideController result;
        if (tunableMap.containsKey(Tunable.START)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(Tunable.END)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
        } else {
            // Emergency code
            result = null;
        }

        return result;
    }

    @Override
    public void moveTunable(EnumMap<Tunable, Integer> tunableMap, double newX, double newY) {
        if (tunableMap.containsKey(Tunable.START)) {
            sceneGraphObject.setStartX(newX);
            sceneGraphObject.setStartY(newY);
        } else if (tunableMap.containsKey(Tunable.END)) {
            sceneGraphObject.setEndX(newX);
            sceneGraphObject.setEndY(newY);
        }
    }

    @Override
    public void revertToOriginalState() {
        sceneGraphObject.setStartX(originalStartX);
        sceneGraphObject.setStartY(originalStartY);
        sceneGraphObject.setEndX(originalEndX);
        sceneGraphObject.setEndY(originalEndY);
    }

    @Override
    public List<PropertyName> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public Object getValue(PropertyName propertyName) {
        assert propertyName != null;
        assert propertyNames.contains(propertyName);

        final Object result;
        if (propertyName.equals(startXName)) {
            result = sceneGraphObject.getStartX();
        } else if (propertyName.equals(startYName)) {
            result = sceneGraphObject.getStartY();
        } else if (propertyName.equals(endXName)) {
            result = sceneGraphObject.getEndX();
        } else if (propertyName.equals(endYName)) {
            result = sceneGraphObject.getEndY();
        } else {
            // Emergency code
            result = null;
        }

        return result;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        final Map<PropertyName, Object> result = new HashMap<>();
        if (!MathUtils.equals(sceneGraphObject.getStartX(), originalStartX)) {
            result.put(startXName, sceneGraphObject.getStartX());
        }
        if (!MathUtils.equals(sceneGraphObject.getStartY(), originalStartY)) {
            result.put(startYName, sceneGraphObject.getStartY());
        }
        if (!MathUtils.equals(sceneGraphObject.getEndX(), originalEndX)) {
            result.put(endXName, sceneGraphObject.getEndX());
        }
        if (!MathUtils.equals(sceneGraphObject.getEndY(), originalEndY)) {
            result.put(endYName, sceneGraphObject.getEndY());
        }
        return result;
    }

    @Override
    public List<Double> getPoints() {
        return null;
    }

    @Override
    public void addPoint(EnumMap<Tunable, Integer> tunableMap, double newX, double newY) {
    }

    @Override
    public void removePoint(EnumMap<Tunable, Integer> tunableMap) {
    }

}
