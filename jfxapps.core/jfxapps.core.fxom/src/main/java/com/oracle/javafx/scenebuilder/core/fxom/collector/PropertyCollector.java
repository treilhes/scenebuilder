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
package com.oracle.javafx.scenebuilder.core.fxom.collector;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class PropertyCollector {

    public static PropertyByName byName(PropertyName name) {
        return new PropertyByName(name);
    }

    public static FxNullProperties fxNullProperties() {
        return new FxNullProperties();
    }

    public static SimpleProperties allSimpleProperties() {
        return new SimpleProperties();
    }

    public static class PropertyByName implements OMCollector<List<FXOMProperty>>{

        private List<FXOMProperty> result = new ArrayList<>();

        private final PropertyName search;


        public PropertyByName(PropertyName search) {
            super();
            assert search != null;
            this.search = search;
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.PROPERTY;
        }

        @Override
        public void collect(FXOMObject object) {

        }

        @Override
        public void collect(FXOMProperty property) {
            if (property.getName().equals(search)) {
                result.add(property);
            }
        }

        @Override
        public List<FXOMProperty> getCollected() {
            return result;
        }

    }

    public static class FxNullProperties implements OMCollector<List<FXOMPropertyT>>{

        private final static String JAVAFX_NULL = "$null";

        private List<FXOMPropertyT> result = new ArrayList<>();

        public FxNullProperties() {
            super();
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.PROPERTY;
        }

        @Override
        public void collect(FXOMObject object) {
        }

        @Override
        public void collect(FXOMProperty property) {
            if (property instanceof FXOMPropertyT tp) {
                if (tp.getValue().equals(JAVAFX_NULL)) {
                    result.add(tp);
                }
            }
        }

        @Override
        public List<FXOMPropertyT> getCollected() {
            return result;
        }

    }

    public static class SimpleProperties implements OMCollector<List<FXOMPropertyT>>{

        private List<FXOMPropertyT> result = new ArrayList<>();

        public SimpleProperties() {
            super();
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.PROPERTY;
        }

        @Override
        public void collect(FXOMObject object) {
        }

        @Override
        public void collect(FXOMProperty property) {
            if (property instanceof FXOMPropertyT tp) {
                result.add(tp);
            }
        }

        @Override
        public List<FXOMPropertyT> getCollected() {
            return result;
        }

    }
}
