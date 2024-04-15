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
package com.oracle.javafx.scenebuilder.core.fxom;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SceneGraphObject {

    private Optional<Object> object;

    public SceneGraphObject() {
        this(null);
    }

    public SceneGraphObject(Object scenGraphObject) {
        super();
        object = Optional.ofNullable(scenGraphObject);
    }

    protected void update(Object sceneGraphObject) {
        this.object = Optional.ofNullable(sceneGraphObject);
    }

    public Optional<Object> getOptional() {
        return object;
    }

    public Object get() {
        return object.orElse(null);
    }

    public Class<?> getObjectClass() {
        return object.map(Object::getClass).orElse(null);
    }

    public boolean isEmpty() {
        return object.isEmpty();
    }

    public boolean isPresent() {
        return object.isPresent();
    }

    /**
     * Gets the scene graph object casted as the provided type
     *
     * @param <T> the generic type
     * @param cls the cls
     * @return the as
     * @throws ClassCastException
     */
    public <T> T getAs(Class<T> cls) {
        return cls.cast(object.get());
    }

    public <T> Optional<T> getOptionalAs(Class<T> cls) {
        return object.map(cls::cast);
    }

    public boolean isInstanceOf(Class<?> type) {
        return object.map(type::isInstance).orElse(false);
    }
    /**
     * Checks if the scenegraphobject is a {@link Node}.
     *
     * @return true, if is node
     */
    public boolean isNode() {
        return isInstanceOf(Node.class);
    }

    public boolean isParent() {
        return isInstanceOf(Parent.class);
    }

    public boolean hasParent() {
        return isNode() && getAs(Node.class).getParent() != null;
    }

    public Scene getScene() {
        return object
                .filter(Node.class::isInstance)
                .map(Node.class::cast)
                .map(Node::getScene)
                .orElse(null);
    }

    public boolean hasScene() {
        return getScene() != null;
    }


}
