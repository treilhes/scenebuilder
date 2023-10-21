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
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic.Type;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;

public class FxReferenceCollector {

    public static FxReferenceBySource fxReferenceBySource(String source) {
        return new FxReferenceBySource(source, null);
    }

    /**
     * Collect all {@link FXOMNode} ({@link FXOMIntrinsic} or {@link FXOMPropertyT} referencing the source id
     *
     * @param source
     * @param excludedFromSearch
     * @return
     */
    public static FxReferenceBySource fxReferenceBySource(String source, FXOMObject excludedFromSearch) {
        return new FxReferenceBySource(source, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static FxReferenceBySource fxReferenceBySource(String source, Set<FXOMObject> excludedFromSearch) {
        return new FxReferenceBySource(source, excludedFromSearch);
    }

    public static FxReferenceBySource allFxReferences() {
        return new FxReferenceBySource(null, null);
    }

    public static ValueReference valueReferenceById(String referenceId) {
        return new ValueReference(referenceId, null);
    }

    public static ValueReference valueReferenceById(String referenceId, FXOMObject excludedFromSearch) {
        return new ValueReference(referenceId, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static ValueReference valueReferenceById(String referenceId, Set<FXOMObject> excludedFromSearch) {
        return new ValueReference(referenceId, excludedFromSearch);
    }

    public static ValueReference allValueReferences() {
        return new ValueReference(null, null);
    }

    public static Reference referenceById(String referenceId) {
        return new Reference(referenceId, null);
    }

    public static Reference referenceById(String referenceId, FXOMObject excludedFromSearch) {
        return new Reference(referenceId, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static Reference referenceById(String referenceId, Set<FXOMObject> excludedFromSearch) {
        return new Reference(referenceId, excludedFromSearch);
    }

    public static Reference allReferences() {
        return new Reference(null, null);
    }

    public static class FxReferenceBySource implements OMCollector<List<FXOMIntrinsic>>{

        private List<FXOMIntrinsic> result = new ArrayList<>();

        private final String source;
        private final Set<FXOMObject> excludedFromSearch;

        public FxReferenceBySource(String source, Set<FXOMObject> excludedFromSearch) {
            super();
            this.source = source;
            this.excludedFromSearch = excludedFromSearch;
        }

        @Override
        public boolean accept(FXOMObject object) {
            return excludedFromSearch == null || !excludedFromSearch.contains(object);
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object instanceof FXOMIntrinsic fi) {
                if ((fi.getType() == Type.FX_REFERENCE) && ((source == null) || source.equals(fi.getSource()))) {
                    result.add(fi);
                }
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public List<FXOMIntrinsic> getCollected() {
            return result;
        }

    }

    public static class ValueReference implements OMCollector<List<FXOMPropertyT>>{

        private List<FXOMPropertyT> result = new ArrayList<>();

        private final String source;
        private final Set<FXOMObject> excludedFromSearch;

        public ValueReference(String source, Set<FXOMObject> excludedFromSearch) {
            super();
            this.source = source;
            this.excludedFromSearch = excludedFromSearch;
        }

        @Override
        public boolean accept(FXOMObject object) {
            return excludedFromSearch == null || !excludedFromSearch.contains(object);
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
            if (property instanceof FXOMPropertyT pt) {
                final PrefixedValue pv = new PrefixedValue(pt.getValue());
                if (pv.isExpression()) {
                    final String suffix = pv.getSuffix();
                    if (JavaLanguage.isIdentifier(suffix)) {
                        if ((source == null) || source.equals(suffix)) {
                            result.add(pt);
                        }
                    }
                }
            }
        }

        @Override
        public List<FXOMPropertyT> getCollected() {
            return result;
        }

    }

    public static class Reference implements OMCollector<List<FXOMNode>>{

        private List<FXOMNode> result = new ArrayList<>();

        private final String source;
        private final Set<FXOMObject> excludedFromSearch;

        public Reference(String source, Set<FXOMObject> excludedFromSearch) {
            super();
            this.source = source;
            this.excludedFromSearch = excludedFromSearch;
        }

        @Override
        public boolean accept(FXOMObject object) {
            return excludedFromSearch == null || !excludedFromSearch.contains(object);
        }

        @Override
        public Strategy collectionStragtegy() {
            return Strategy.OBJECT_AND_PROPERTY;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object instanceof FXOMIntrinsic fi) {
                if ((fi.getType() == Type.FX_REFERENCE) && ((source == null) || source.equals(fi.getSource()))) {
                    result.add(fi);
                }
            }
        }

        @Override
        public void collect(FXOMProperty property) {
            if (property instanceof FXOMPropertyT pt) {
                final PrefixedValue pv = new PrefixedValue(pt.getValue());
                if (pv.isExpression()) {
                    final String suffix = pv.getSuffix();
                    if (JavaLanguage.isIdentifier(suffix)) {
                        if ((source == null) || source.equals(suffix)) {
                            result.add(pt);
                        }
                    }
                }
            }
        }

        @Override
        public List<FXOMNode> getCollected() {
            return result;
        }

    }
}
