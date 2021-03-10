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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.api.control.relocater.AbstractRelocater;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.geometry.Bounds;
import javafx.scene.Group;

public class RootRelocater extends AbstractRelocater {

    private double originalLayoutX;
    private double originalLayoutY;
    private final PropertyName layoutXName = new PropertyName("layoutX"); //NOI18N
    private final PropertyName layoutYName = new PropertyName("layoutY"); //NOI18N
    private final List<PropertyName> propertyNames = new ArrayList<>();
    
    public RootRelocater() {
        super(Group.class);
        propertyNames.add(layoutXName);
        propertyNames.add(layoutYName);
    }
    
    @Override
    public void initialize() {
        this.originalLayoutX = sceneGraphObject.getLayoutX();
        this.originalLayoutY = sceneGraphObject.getLayoutY();
    }
    
    /*
     * AbstractRelocater
     */
    @Override
    public void moveToLayoutX(double newLayoutX, Bounds newLayoutBounds) {
        sceneGraphObject.setLayoutX(Math.round(newLayoutX));
        // newLayoutBounds is no use for this subclass
    }

    @Override
    public void moveToLayoutY(double newLayoutY, Bounds newLayoutBounds) {
        sceneGraphObject.setLayoutY(Math.round(newLayoutY));
        // newLayoutBounds is no use for this subclass
    }

    @Override
    public void revertToOriginalLocation() {
        sceneGraphObject.setLayoutX(originalLayoutX);
        sceneGraphObject.setLayoutY(originalLayoutY);
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
        if (propertyName.equals(layoutXName)) {
            result = originalLayoutX;
        } else if (propertyName.equals(layoutYName)) {
            result = originalLayoutY;
        } else {
            // Emergency code
            result = null;
        }
        
        return result;
    }

    @Override
    public Map<PropertyName, Object> getChangeMap() {
        final Map<PropertyName, Object> result = new HashMap<>();
        return result;
    }
}