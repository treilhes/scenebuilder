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
package com.gluonhq.jfxapps.core.fxom.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.util.JavaLanguage;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;

public class ExpressionCollector {

    /**
     * Collects all {@link FXOMPropertyT} that are expressions and have a suffix
     * that is a valid Java identifier. The collected list retains the same order as
     * the order the items were collected.
     */
    public static class ExpressionReference implements FXOMCollector<List<FXOMPropertyT>> {

        private List<FXOMPropertyT> result = new ArrayList<>();

        private final String source;
        private final Set<FXOMObject> excludedFromSearch;

        public ExpressionReference(String source, Set<FXOMObject> excludedFromSearch) {
            super();
            this.source = source;
            this.excludedFromSearch = excludedFromSearch;
        }

        @Override
        public boolean accept(FXOMObject object) {
            return excludedFromSearch == null || !excludedFromSearch.contains(object);
        }

        @Override
        public Strategy collectionStrategy() {
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

    /**
     * Collects all {@link FXOMPropertyT} that are expressions and have a suffix
     * that is a valid Java identifier and for which the referee was not already
     * found/declared at the time of the collection. Also collect
     * {@link FXOMIntrinsic} for which the referee source was not already
     * found/declared at the time of the collection. return a map of
     * {@link FXOMNode} ({@link FXOMIntrinsic} or {@link FXOMPropertyT}) to the
     * undeclared reference.
     */
    public static class UndeclaredExpressionReference implements FXOMCollector<Map<FXOMNode, String>> {

        private Map<FXOMNode, String> result = new HashMap<>();

        private final List<String> declaredFxIds = new ArrayList<>();

        public UndeclaredExpressionReference() {
            super();
        }

        @Override
        public Strategy collectionStrategy() {
            return Strategy.OBJECT_AND_PROPERTY;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object.getFxId() != null) {
                declaredFxIds.add(object.getFxId());
            }

            if (object instanceof FXOMIntrinsic intrinsic) {
                if (intrinsic.getSource() != null) {
                    final String fxId = intrinsic.getSource();
                    if (!declaredFxIds.contains(fxId)) {
                        result.put(intrinsic, fxId);
                    }
                }
            }
        }

        @Override
        public void collect(FXOMProperty property) {
            if (property instanceof FXOMPropertyT pt) {
                final PrefixedValue pv = new PrefixedValue(pt.getValue());
                if (pv.isExpression()) {
                    final String fxId = pv.getSuffix();
                    if (JavaLanguage.isIdentifier(fxId)) {
                        if (!declaredFxIds.contains(fxId)) {
                            result.put(pt, fxId);
                        }
                    }
                }
            }
        }

        @Override
        public Map<FXOMNode, String> getCollected() {
            return result;
        }

    }

    public static ExpressionReference allExpressionReferences() {
        return new ExpressionReference(null, null);
    }

    public static ExpressionReference expressionReferenceById(String referenceId) {
        return new ExpressionReference(referenceId, null);
    }

    public static ExpressionReference expressionReferenceById(String referenceId, FXOMObject excludedFromSearch) {
        return new ExpressionReference(referenceId, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static ExpressionReference expressionReferenceById(String referenceId, Set<FXOMObject> excludedFromSearch) {
        return new ExpressionReference(referenceId, excludedFromSearch);
    }

    public static UndeclaredExpressionReference allUndeclaredExpressionReferences() {
        return new UndeclaredExpressionReference();
    }
}
