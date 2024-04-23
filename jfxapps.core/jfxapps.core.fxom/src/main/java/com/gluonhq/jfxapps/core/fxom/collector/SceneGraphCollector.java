/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.fxom.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.SceneGraphObject;

public class SceneGraphCollector {

    public static SceneGraph allSceneGraphObjects() {
        return new SceneGraph(null);
    }

    public static SceneGraph sceneGraphObjectByClass(Class<?> cls) {
        return new SceneGraph(cls);
    }

    public static FindSceneGraph findSceneGraphObject(Object object) {
        return new FindSceneGraph(object);
    }

    public static class SceneGraph implements OMCollector<List<FXOMObject>>{

        private List<FXOMObject> result = new ArrayList<>();

        private final Class<?> cls;

        public SceneGraph(Class<?> cls) {
            super();
            this.cls = cls;
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            SceneGraphObject sgo = object.getSceneGraphObject();
            if (sgo != null && !sgo.isEmpty()) {
                if (cls == null || sgo.getObjectClass() == cls) {
                    result.add(object);
                }
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public List<FXOMObject> getCollected() {
            return result;
        }

    }

    public static class FindSceneGraph implements OMCollector<Optional<FXOMObject>>{

        private Optional<FXOMObject> result = Optional.empty();
        private final Object object;

        public FindSceneGraph(Object object) {
            super();
            assert object != null;
            this.object = object;
        }

        @Override
        public boolean accept(FXOMObject object) {
            return result.isEmpty();
        }

        @Override
        public boolean accept(FXOMProperty property) {
            return result.isEmpty();
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            final SceneGraphObject sceneGraphObject = object.getSceneGraphObject();
            if (result.isEmpty() && sceneGraphObject.get() == this.object) {
                result = Optional.of(object);
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public Optional<FXOMObject> getCollected() {
            return result;
        }
    }
}
