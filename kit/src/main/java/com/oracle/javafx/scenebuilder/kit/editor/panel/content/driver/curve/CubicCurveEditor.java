/*
 * Copyright (c) 2018, Gluon and/or its affiliates.
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
 * "AS IS" AND VERTEX EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR VERTEX DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON VERTEX
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN VERTEX WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.curve;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.api.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.util.MathUtils;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.EditCurveGuideController;

import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurve;

public class CubicCurveEditor extends AbstractCurveEditor<CubicCurve> {

    private final double originalStartX;
    private final double originalStartY;
    private final double originalControlX1;
    private final double originalControlY1;
    private final double originalControlX2;
    private final double originalControlY2;
    private final double originalEndX;
    private final double originalEndY;

    private final PropertyName startXName = new PropertyName("startX"); //NOI18N
    private final PropertyName startYName = new PropertyName("startY"); //NOI18N
    private final PropertyName controlX1Name = new PropertyName("controlX1"); //NOI18N
    private final PropertyName controlY1Name = new PropertyName("controlY1"); //NOI18N
    private final PropertyName controlX2Name = new PropertyName("controlX2"); //NOI18N
    private final PropertyName controlY2Name = new PropertyName("controlY2"); //NOI18N
    private final PropertyName endXName = new PropertyName("endX"); //NOI18N
    private final PropertyName endYName = new PropertyName("endY"); //NOI18N
    private final List<PropertyName> propertyNames = new ArrayList<>();

    public CubicCurveEditor(CubicCurve sceneGraphObject) {
        super(sceneGraphObject);

        originalStartX = sceneGraphObject.getStartX();
        originalStartY = sceneGraphObject.getStartY();
        originalControlX1 = sceneGraphObject.getControlX1();
        originalControlY1 = sceneGraphObject.getControlY1();
        originalControlX2 = sceneGraphObject.getControlX2();
        originalControlY2 = sceneGraphObject.getControlY2();
        originalEndX = sceneGraphObject.getEndX();
        originalEndY = sceneGraphObject.getEndY();

        propertyNames.add(startXName);
        propertyNames.add(startYName);
        propertyNames.add(controlX1Name);
        propertyNames.add(controlY1Name);
        propertyNames.add(controlX2Name);
        propertyNames.add(controlY2Name);
        propertyNames.add(endXName);
        propertyNames.add(endYName);
    }

    @Override
    public EditCurveGuideController createController(EnumMap<Tunable, Integer> tunableMap) {

        final EditCurveGuideController result;
        if (tunableMap.containsKey(Tunable.START)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getControlX1(), sceneGraphObject.getControlY1(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX2(), sceneGraphObject.getControlY2(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(Tunable.CONTROL1)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX2(), sceneGraphObject.getControlY2(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(Tunable.CONTROL2)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX1(), sceneGraphObject.getControlY1(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getEndX(), sceneGraphObject.getEndY(), true);
            result.addCurvePoint(point);
        } else if (tunableMap.containsKey(Tunable.END)) {
            result = new EditCurveGuideController();
            Point2D point = sceneGraphObject.localToScene(sceneGraphObject.getStartX(), sceneGraphObject.getStartY(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX1(), sceneGraphObject.getControlY1(), true);
            result.addCurvePoint(point);
            point = sceneGraphObject.localToScene(sceneGraphObject.getControlX2(), sceneGraphObject.getControlY2(), true);
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
        } else if (tunableMap.containsKey(Tunable.CONTROL1)) {
            sceneGraphObject.setControlX1(newX);
            sceneGraphObject.setControlY1(newY);
        } else if (tunableMap.containsKey(Tunable.CONTROL2)) {
            sceneGraphObject.setControlX2(newX);
            sceneGraphObject.setControlY2(newY);
        } else if (tunableMap.containsKey(Tunable.END)) {
            sceneGraphObject.setEndX(newX);
            sceneGraphObject.setEndY(newY);
        }
    }

    @Override
    public void revertToOriginalState() {
        sceneGraphObject.setStartX(originalStartX);
        sceneGraphObject.setStartY(originalStartY);
        sceneGraphObject.setControlX1(originalControlX1);
        sceneGraphObject.setControlY1(originalControlY1);
        sceneGraphObject.setControlX2(originalControlX2);
        sceneGraphObject.setControlY2(originalControlY2);
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
        } else if (propertyName.equals(controlX1Name)) {
            result = sceneGraphObject.getControlX1();
        } else if (propertyName.equals(controlY1Name)) {
            result = sceneGraphObject.getControlY1();
        } else if (propertyName.equals(controlX2Name)) {
            result = sceneGraphObject.getControlX2();
        } else if (propertyName.equals(controlY2Name)) {
            result = sceneGraphObject.getControlY2();
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
        if (!MathUtils.equals(sceneGraphObject.getControlX1(), originalControlX1)) {
            result.put(controlX1Name, sceneGraphObject.getControlX1());
        }
        if (!MathUtils.equals(sceneGraphObject.getControlY1(), originalControlY1)) {
            result.put(controlY1Name, sceneGraphObject.getControlY1());
        }
        if (!MathUtils.equals(sceneGraphObject.getControlX2(), originalControlX2)) {
            result.put(controlX2Name, sceneGraphObject.getControlX2());
        }
        if (!MathUtils.equals(sceneGraphObject.getControlY2(), originalControlY2)) {
            result.put(controlY2Name, sceneGraphObject.getControlY2());
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