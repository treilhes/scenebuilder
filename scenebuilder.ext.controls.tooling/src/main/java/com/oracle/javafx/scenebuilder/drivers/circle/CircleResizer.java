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
package com.oracle.javafx.scenebuilder.drivers.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractResizer;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.util.MathUtils;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

/**
 *
 * 
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class CircleResizer extends AbstractResizer<Circle> {

    private double originalRadius;
    private final PropertyName radiusName = new PropertyName("radius"); //NOCHECK
    private final List<PropertyName> propertyNames = new ArrayList<>();
    
    public CircleResizer() {
        super();
        propertyNames.add(radiusName);
    }

    @Override
    public void initialize() {
        originalRadius = sceneGraphObject.getRadius();
    }
    
    @Override
    public final Bounds computeBounds(double width, double height) {
        final double radius = Math.round(Math.min(width, height) / 2.0);
        final double minX = sceneGraphObject.getCenterX() - radius;
        final double minY = sceneGraphObject.getCenterY() - radius;
        return new BoundingBox(minX, minY, 2 * radius, 2 * radius);
    }
 
    @Override
    public Feature getFeature() {
        return Feature.SCALING;
    }

    @Override
    public void changeWidth(double width) {
        sceneGraphObject.setRadius(Math.round(width / 2));
    }

    @Override
    public void changeHeight(double height) {
        sceneGraphObject.setRadius(Math.round(height / 2));
    }

    @Override
    public void revertToOriginalSize() {
        sceneGraphObject.setRadius(originalRadius);
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
        if (propertyName.equals(radiusName)) {
            result = sceneGraphObject.getRadius();
        } else {
            // Emergency code
            result = null;
        }
        
        return result;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        final Map<PropertyName, Object> result = new HashMap<>();
        if (MathUtils.equals(sceneGraphObject.getRadius(), originalRadius) == false) {
            result.put(radiusName, sceneGraphObject.getRadius());
        }
        return result;
    }

}