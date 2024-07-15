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

public class FxCollector {

    public static FxReferenceBySource fxReferenceBySource(String source) {
        return new FxReferenceBySource(source, null);
    }

    public static FxReferenceBySource fxReferenceBySource(String source, FXOMObject excludedFromSearch) {
        return new FxReferenceBySource(source, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static FxReferenceBySource fxReferenceBySource(String source, Set<FXOMObject> excludedFromSearch) {
        return new FxReferenceBySource(source, excludedFromSearch);
    }

    public static FxCopyBySource allFxCopy() {
        return new FxCopyBySource(null, null);
    }

    public static FxCopyBySource fxCopyBySource(String source) {
        return new FxCopyBySource(source, null);
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

    /**
     * collect all fxom element with an fx:id attribute<br/>
     * @return a map indexed by fx id and containing the latest associated fxom object
     * @deprecated use {@link #fxIdMap()} instead
     */
    //FIXME remove this method in the future and replace by fxIdMap()
    @Deprecated
    public static FxIdUniqueMap fxIdsUniqueMap() {
        return new FxIdUniqueMap(false);
    }

    /**
     * collect all fxom element with an fx:id attribute<br/>
     * @param ensureUnicity if true, throw an exception if a duplicate fx:id is found
     * @return a map indexed by fx id and containing the associated fxom object
     * @throws FxIdUniqueMap.DuplicateIdException if a duplicate fx:id is found
     */
    @Deprecated
    public static FxIdUniqueMap fxIdsUniqueMap(boolean ensureUnicity) {
        return new FxIdUniqueMap(ensureUnicity);
    }

    /**
     * Collect all fxom element with an fx:id attribute<br/>
     *
     * @return a map indexed by fx id and containing a list of associated fxom object
     */
    public static FxIdsMap fxIdMap() {
        return new FxIdsMap();
    }

    /**
     * Collect all {@link FXOMNode} ({@link FXOMIntrinsic} or {@link FXOMPropertyT}
     * referencing the source id
     *
     * @param source
     * @param excludedFromSearch
     * @return
     */
    public static FxCopyBySource fxCopyBySource(String source, FXOMObject excludedFromSearch) {
        return new FxCopyBySource(source, excludedFromSearch == null ? null : Set.of(excludedFromSearch));
    }

    public static FxCopyBySource fxCopyBySource(String source, Set<FXOMObject> excludedFromSearch) {
        return new FxCopyBySource(source, excludedFromSearch);
    }

    public static FxReferenceBySource allFxReferences() {
        return new FxReferenceBySource(null, null);
    }

    /**
     * Collects all {@link FXOMIntrinsic} with type {@link Type#FX_REFERENCE}
     * The collected list retains the same order as the order the items were collected.
     */
    public static class FxReferenceBySource implements FXOMCollector<List<FXOMIntrinsic>> {

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


    /**
     * Collects all {@link FXOMIntrinsic} with type {@link Type#FX_COPY}
     * The collected list retains the same order as the order the items were collected.
     */
    public static class FxCopyBySource implements FXOMCollector<List<FXOMIntrinsic>> {

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

    /**
     * Collects all {@link FXOMScript}
     * The collected list retains the same order as the order the items were collected.
     */
    public static class FxScript implements FXOMCollector<List<FXOMScript>> {

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

    public static class FxInclude implements FXOMCollector<List<FXOMIntrinsic>> {

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
     * As a replacement for the method FXOMObject searchWithFxId(String fxId) Javafx
     * can load two components with the same fx id (at least for now) So this
     * collector mimic the same behaviour returning the first occurence
     *
     * WARN: the behaviour of this method is ok but everywhere it is used contains a
     * flaw
     */
    public static class FxIdFirst implements FXOMCollector<Optional<FXOMObject>> {

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
     * Collect all fxom element with an fx:id attribute<br/>
     * As a replacement for the method void collectFxIds(Map<String, FXOMObject>
     * result)<br/>
     * <br/>
     * WARN: the behaviour of this method has a flaw and everywhere it is used
     * contains the same flaw<br/>
     * <br/>
     * FLAW: Javafx can load two components with the same fx id (at least for
     * now)<br/>
     * so this collector mimic the same behaviour returing the last occurence<br/>
     *
     * @deprecated use {@link FxIdsMap} instead
     */
    @Deprecated
    public static class FxIdUniqueMap implements FXOMCollector<Map<String, FXOMObject>> {

        private final boolean ensureUnicity;
        private final Map<String, FXOMObject> result = new HashMap<>();

        public FxIdUniqueMap(boolean ensureUnicity) {
            this.ensureUnicity = ensureUnicity;
        }

        @Override
        public Strategy collectionStrategy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            final String fxId = object.getFxId();
            if (fxId != null) {
                if (ensureUnicity && result.containsKey(fxId)) {
                    throw new DuplicateIdException(fxId);
                }
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

        public static class DuplicateIdException extends RuntimeException{
            private static final long serialVersionUID = 1L;

            public DuplicateIdException(String fxId) {
                super("Duplicate fx:id : " + fxId);
            }
        }
    }

    /**
     * Collect all fxom element with an fx:id attribute<br/>
     */
    public static class FxIdsMap implements FXOMCollector<Map<String, List<FXOMObject>>> {

        private Map<String, List<FXOMObject>> result = new HashMap<>();

        @Override
        public Strategy collectionStrategy() {
            return Strategy.OBJECT;
        }

        @Override
        public void collect(FXOMObject object) {
            final String fxId = object.getFxId();
            if (fxId != null) {
                result.putIfAbsent(fxId, new ArrayList<>()).add(object);
            }
        }

        @Override
        public void collect(FXOMProperty property) {

        }

        @Override
        public Map<String, List<FXOMObject>> getCollected() {
            return result;
        }
    }
}
