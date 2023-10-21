/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.om;

import java.net.URL;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.SubScene;

/**
 * The Interface OMObject.
 */
public interface OMObject {

    /**
     * Document location will change.
     *
     * @param location the location
     */
    public abstract void documentLocationWillChange(URL location);

    /**
     * Gets the fxom document.
     *
     * @return the fxom document
     */
    public FXOMDocument getFxomDocument();

    /**
     * Gets the parent object.
     *
     * @return the parent object
     */
    public OMObject getParentObject();


    /**
     * Gets the child objects (only direct descendant regardless of any grouping or separation).
     *
     * @return the child objects
     */
    public List<? extends OMObject> getChildObjects();

    /**
     * Gets the scene graph object.
     *
     * @return the scene graph object
     */
    public SceneGraphObject getSceneGraphObject();


    /**
     * An {@link OMObject} is virtual when the object does not have any graphic representation.
     *
     * @return true, if is virtual
     */
    public boolean isVirtual();

    default public boolean isDescendantOf(OMObject other) {
        final boolean result;

        if (other == null) {
            result = true;
        } else {
            OMObject ancestor = getParentObject();
            while ((ancestor != other) && (ancestor != null)) {
                ancestor = ancestor.getParentObject();
            }
            result = (ancestor != null);
        }

        return result;
    }

    default public OMObject getClosestNode() {
        OMObject result;

        result = this;
        while ((result.getSceneGraphObject().isNode() == false) && (result.getParentObject() != null)) {
            result = result.getParentObject();
        }

        return result.getSceneGraphObject().isNode() ? result : null;
    }

    default public OMObject getClosestMainGraphNode() {
        OMObject result;
        OMObject current;

        result = this;
        current = this;
        while (current.getParentObject() != null) {
            boolean isNode = current.getSceneGraphObject().isNode();

            if (isNode) {
                Node node = (Node)current.getSceneGraphObject().get();
                boolean hasParent = node.getParent() != null;
                boolean hasParentSubScene = current.getParentObject() != null
                        && current.getParentObject().getSceneGraphObject().get() instanceof SubScene;
                if (result == null && hasParent) {
                    result = current;
                } else if (result != null && !hasParent && !hasParentSubScene) {
                    result = null;
                }
            }

            current = current.getParentObject();
        }

        if (result != null) {
            return result;
        } else if (current.getSceneGraphObject().isNode()) {
            return current;
        } else {
            return null;
        }
    }

    default public OMObject getClosestParent() {
        OMObject result;

        result = this;
        while ((result.getSceneGraphObject().isParent() == false) && (result.getParentObject() != null)) {
            result = result.getParentObject();
        }

        return result.getSceneGraphObject().isParent() ? result : null;
    }

    default public OMObject getFirstAncestorWithNonNullScene() {
        OMObject result = this;

        while ((result != null) && (!result.getSceneGraphObject().hasScene())) {
            result = result.getParentObject();
        }

        return result;
    }

    /**
     * Check if the object is viewable.
     * A node is viewable if all the parents are instance of node
     *
     * @return true if viewable
     */
    default public boolean isViewable() {
        if (!getSceneGraphObject().isNode()) {
            return false;
        }
        OMObject parent = getParentObject();
        while (parent != null) {
            if (!parent.getSceneGraphObject().isNode()) {
                return false;
            }
            parent = parent.getParentObject();
        }
        return true;
    }
}
