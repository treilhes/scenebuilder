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
package com.oracle.javafx.scenebuilder.api.content.mode;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Transform;

public interface Layer<T> {
    Class<T> getLayerId();
    void update();
    void enable();
    void disable();
    
    @FunctionalInterface
    public interface LayerItemCreator<T> {
        T create(FXOMObject fxomObject);
    }
    
    @FunctionalInterface
    public interface LayerItemSelector {
        Set<FXOMObject> select(Selection selection);
    }

    /**
     * Returns null or the layer associated to the specified fxom object.
     *
     * @param fxomObject an fxom object (never null)
     * @return null or the handles associated to the specified fxom object.
     */
    T lookup(FXOMObject fxomObject);
    List<T> getActiveItems();
    Group getLayerUI();
    void removeAll();
    
    void setOnMousePressed(EventHandler<? super MouseEvent> value);
    EventHandler<? super MouseEvent> getOnMousePressed();
    
    /**
     * Computes the transform that projects from local coordinates of a
     * scene graph object to the layer local coordinates.
     * @param fxomObject a fxml object model object
     * @return transform from sceneGraphObject local coordinates to local coordinates
     */
    Transform computeSceneGraphToLayerTransform(FXOMObject fxomObject);
    
    
    //TEMP
    void save(File out);
}
