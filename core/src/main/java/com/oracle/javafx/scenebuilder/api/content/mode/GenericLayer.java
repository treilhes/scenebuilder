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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.Decoration;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public class GenericLayer<T extends Decoration<?>> implements Layer<T> {
    private final List<T> activeItems = new ArrayList<>();
    
    private final Class<T> layerId;
    private final Group layerUI;
    private final Group detachableUI;
    private final Content content;
    private final Selection selection;
    private final LayerItemCreator<T> creator;
    private final LayerItemSelector selector;

    public GenericLayer(Class<T> layerId, Group layerUI, Selection selection, Content content, 
            LayerItemSelector selector, LayerItemCreator<T> creator) {
        super();
        this.layerId = layerId;
        this.layerUI = layerUI;
        this.detachableUI = new Group();
        this.content = content;
        this.selection = selection;
        this.selector = selector;
        this.creator = creator;
    }

    @Override
    public Class<T> getLayerId() {
        return layerId;
    }

//    @Override
//    public abstract void update();
    
    /**
     * Returns null or the layer associated to the specified fxom object.
     *
     * @param fxomObject an fxom object (never null)
     * @return null or the handles associated to the specified fxom object.
     */
    @Override
    public T lookup(FXOMObject fxomObject) {
        assert fxomObject != null;

        T result = null;
        for (T t : activeItems) {
            if (t.getFxomObject() == fxomObject) {
                result = t;
                break;
            }
        }

        return result; 
    }
    
    @Override
    public void removeAll() {
        for (T t : new ArrayList<>(activeItems)) {
            detachableUI.getChildren().remove(t.getRootNode());
            activeItems.remove(t);
        }
    }

    @Override
    public List<T> getActiveItems() {
        return activeItems;
    }

    @Override
    public Group getLayerUI() {
        return layerUI;
    }

    @Override
    public void update() {

        Set<FXOMObject> targets = selector.select(selection);
        if (targets != null && targets.size() > 0) {
            updateLayer(targets);
        } else {
            removeAll();
        }
    }

    private void updateLayer(Set<FXOMObject> targets) {
        final List<T> obsoleteItems = new ArrayList<>();
        final List<FXOMObject> incomingObjects = new ArrayList<>();

        // Collects fxom objects from selection
        if (content.isContentDisplayable()) {
            incomingObjects.addAll(targets);
            
            // Collects obsolete handles
            for (T h : getActiveItems()) {
                if (incomingObjects.contains(h.getFxomObject())) {
                    // FXOM object associated to these handles is still selected
                    switch(h.getState()) {
                        case CLEAN:
                            incomingObjects.remove(h.getFxomObject());
                            break;
                        case NEEDS_RECONCILE:
                            // scene graph associated to h has changed but h is still compatible
                            h.reconcile();
                            incomingObjects.remove(h.getFxomObject());
                            break;
                        case NEEDS_REPLACE:
                            // h is no longer compatible with the new scene graph object
                            obsoleteItems.add(h);
                            break;
                    }
                    
                    h.update();
                    //incomingObjects.remove(h.getFxomObject());
                } else {
                    // FXOM object associated to these handles is no longer selected
                    // => handles become obsolete
                    obsoleteItems.add(h);
                }
            }
        } else {
            // FXOM object associated to these handles is no longer selected
            // => handles become obsolete
            obsoleteItems.addAll(getActiveItems());
        }

        // Let's create new handles for the incoming objects
        for (FXOMObject incomingObject : incomingObjects) {
            
            final T newItem = creator.create(incomingObject);
            if (newItem != null) {
                detachableUI.getChildren().add(newItem.getRootNode());
                getActiveItems().add(newItem);
                newItem.update();
            }
        }

        // Let's disconnect the obsolete handles
        for (T h : obsoleteItems) {
            detachableUI.getChildren().remove(h.getRootNode());
            getActiveItems().remove(h);
        }
    }

    @Override
    public void enable() {
        if (!this.layerUI.getChildren().contains(this.detachableUI)) {
            this.layerUI.getChildren().add(this.detachableUI);
        }
    }

    @Override
    public void disable() {
        this.layerUI.getChildren().clear();
    }
    
    @Override
    public final void setOnMousePressed(EventHandler<? super MouseEvent> value) {
        detachableUI.onMousePressedProperty().set(value);
    }

    @Override
    public final EventHandler<? super MouseEvent> getOnMousePressed() {
        return detachableUI.getOnMousePressed();
    }
    
    /**
     * Computes the transform that projects from local coordinates of a
     * scene graph object to the rudder layer local coordinates.
     * @param sceneGraphObject a scene graph object
     * @return transform from sceneGraphObject local coordinates to rudder local coordinates
     */
    @Override
    public Transform computeSceneGraphToLayerTransform(Node sceneGraphObject) {
        assert sceneGraphObject != null;
        assert sceneGraphObject.getScene() == getLayerUI().getScene();

        final Transform t1 = sceneGraphObject.getLocalToSceneTransform();
        final Transform t2 = content.getContentSubScene().getLocalToSceneTransform();
        final Transform t3 = getLayerUI().getLocalToSceneTransform();
        final Transform result;

        try {
            final Transform i3 = t3.createInverse();
            result = i3.createConcatenation(t2).createConcatenation(t1);
        } catch(NonInvertibleTransformException x) {
            throw new RuntimeException(x);
        }

        return result;
    }
}