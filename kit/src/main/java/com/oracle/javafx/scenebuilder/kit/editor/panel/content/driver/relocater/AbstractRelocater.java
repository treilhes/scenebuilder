/*
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.relocater;

import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * 
 */
public abstract class AbstractRelocater<T extends Parent> {
    
    protected final Node sceneGraphObject;
    protected final Class<T> parentClass;

    public AbstractRelocater(Node sceneGraphObject, Class<T> parentClass) {
        assert sceneGraphObject != null;
        assert sceneGraphObject.getParent() != null;
        assert sceneGraphObject.getParent().getClass() == parentClass;
        this.sceneGraphObject = sceneGraphObject;
        this.parentClass = parentClass;
    }
    
    public abstract void moveToLayoutX(double newLayoutX, Bounds newLayoutBounds);
    public abstract void moveToLayoutY(double newLayoutY, Bounds newLayoutBounds);
    public abstract void revertToOriginalLocation();

    public abstract List<PropertyName> getPropertyNames();
    public abstract Object getValue(PropertyName propertyName);
    public abstract Map<PropertyName, Object> getChangeMap();
}
