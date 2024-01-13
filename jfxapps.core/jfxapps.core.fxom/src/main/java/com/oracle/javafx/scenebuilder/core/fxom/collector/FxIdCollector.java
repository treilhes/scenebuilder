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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;

public class FxIdCollector {

    public static FxIdMap fxIdsMap() {
        return new FxIdMap();
    }

    public static FirstFxId findFirstById(String fxId) {
        return new FirstFxId(fxId);
    }

    /**
     * As a replacement for the method void collectFxIds(Map<String, FXOMObject> result)
     * Javafx can load two components with the same fx id (at least for now)
     * So this collector mimic the same behaviour returing the last occurence
     *
     * WARN: the behaviour of this method has a flaw and everywhere it is used contains the same flaw
     */
    public static class FxIdMap implements OMCollector<Map<String, FXOMObject>>{

        private Map<String, FXOMObject> result = new HashMap<>();

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            final String fxId = object.getFxId();
            if (fxId != null) {
                result.put(fxId, object);
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public Map<String, FXOMObject> getCollected() {
            return result;
        }
    }

    /**
     * As a replacement for the method FXOMObject searchWithFxId(String fxId)
     * Javafx can load two components with the same fx id (at least for now)
     * So this collector mimic the same behaviour returning the first occurence
     *
     * WARN: the behaviour of this method is ok but everywhere it is used contains a flaw
     */
    public static class FirstFxId implements OMCollector<Optional<FXOMObject>>{

        private Optional<FXOMObject> result = Optional.empty();
        private final String fxId;

        public FirstFxId(String fxId) {
            super();
            assert fxId != null;
            this.fxId = fxId;
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
            final String localFxId = object.getFxId();
            if (localFxId != null && result.isEmpty() && localFxId.equals(this.fxId)) {
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