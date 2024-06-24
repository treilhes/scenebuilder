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
import java.util.Optional;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic.Type;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.FXOMScript;
import com.gluonhq.jfxapps.core.fxom.util.JavaLanguage;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;

public class FxCollector {

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

    public static class FxReferenceBySource implements FXOMCollector<List<FXOMIntrinsic>>{

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
        public Strategy collectionStrategy() {
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

    public static class FxCopyBySource implements FXOMCollector<List<FXOMIntrinsic>>{

        private List<FXOMIntrinsic> result = new ArrayList<>();

        private final String source;
        private final Set<FXOMObject> excludedFromSearch;

        public FxCopyBySource(String source, Set<FXOMObject> excludedFromSearch) {
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
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object instanceof FXOMIntrinsic fi) {
                if ((fi.getType() == Type.FX_COPY) && ((source == null) || source.equals(fi.getSource()))) {
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

    public static class Reference implements FXOMCollector<List<FXOMNode>>{

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
        public Strategy collectionStrategy() {
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

    public static class FxScript implements FXOMCollector<List<FXOMScript>>{

        private List<FXOMScript> result = new ArrayList<>();

        private final String source;

        public FxScript(String source) {
            super();
            this.source = source;
        }

        @Override
        public Strategy collectionStrategy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object instanceof FXOMScript fs) {
                if ((source == null) || source.equals(fs.getSource())) {
                    result.add(fs);
                }
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public List<FXOMScript> getCollected() {
            return result;
        }

    }

    public static class FxInclude implements FXOMCollector<List<FXOMIntrinsic>>{

        private List<FXOMIntrinsic> result = new ArrayList<>();

        private final String source;

        public FxInclude(String source) {
            super();
            this.source = source;
        }

        @Override
        public Strategy collectionStrategy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            if (object instanceof FXOMIntrinsic fi) {
                if ((fi.getType() == Type.FX_INCLUDE) && ((source == null) || source.equals(fi.getSource()))) {
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

    /**
     * As a replacement for the method FXOMObject searchWithFxId(String fxId)
     * Javafx can load two components with the same fx id (at least for now)
     * So this collector mimic the same behaviour returning the first occurence
     *
     * WARN: the behaviour of this method is ok but everywhere it is used contains a flaw
     */
    public static class FxIdFirst implements FXOMCollector<Optional<FXOMObject>>{

        private Optional<FXOMObject> result = Optional.empty();
        private final String fxId;

        public FxIdFirst(String fxId) {
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
        public Strategy collectionStrategy() {
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

    /**
     * As a replacement for the method void collectFxIds(Map<String, FXOMObject> result)
     * Javafx can load two components with the same fx id (at least for now)
     * So this collector mimic the same behaviour returing the last occurence
     *
     * WARN: the behaviour of this method has a flaw and everywhere it is used contains the same flaw
     */
    public static class FxIdMap implements FXOMCollector<Map<String, FXOMObject>>{

        private Map<String, FXOMObject> result = new HashMap<>();

        @Override
        public Strategy collectionStrategy() {
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

    public static FxScript allFxScripts() {
        return new FxScript(null);
    }

    public static FxScript fxScriptBySource(String source) {
        return new FxScript(source);
    }

    public static FxInclude allFxIncludes() {
        return new FxInclude(null);
    }

    public static FxInclude fxIncludeBySource(String source) {
        return new FxInclude(source);
    }

    public static FxIdFirst fxIdFindFirst(String fxId) {
        return new FxIdFirst(fxId);
    }

    public static FxIdMap fxIdsMap() {
        return new FxIdMap();
    }
}
