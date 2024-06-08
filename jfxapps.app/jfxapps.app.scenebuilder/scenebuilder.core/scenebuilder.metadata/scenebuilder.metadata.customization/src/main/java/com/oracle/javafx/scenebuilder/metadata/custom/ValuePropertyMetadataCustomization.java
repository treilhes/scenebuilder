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
package com.oracle.javafx.scenebuilder.metadata.custom;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 */
public class ValuePropertyMetadataCustomization {

    /** The inspector path. */
    private final InspectorPath inspectorPath;

    protected ValuePropertyMetadataCustomization(Builder builder) {
        this.inspectorPath = builder.inspectorPath;
    }

    /**
     * Gets the inspector path.
     *
     * @return the inspector path
     */
    public InspectorPath getInspectorPath() {
        return inspectorPath;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /** The inspector path. */
        private InspectorPath inspectorPath;

        public Builder inspectorPath(InspectorPath inspectorPath) {
            this.inspectorPath = inspectorPath;
            return this;
        }

        public ValuePropertyMetadataCustomization build() {
            return new ValuePropertyMetadataCustomization(this);
        }
    }

    /**
    *
    */
    public static class InspectorPath implements Comparable<InspectorPath> {
        protected static int indexCount = 0;
        protected int index = indexCount++;

        private final String sectionTag;
        private final String subSectionTag;
        private final int subSectionIndex;

        public static final String CUSTOM_SECTION = "Properties";
        public static final String CUSTOM_SUB_SECTION = "Custom";

        public static final InspectorPath UNUSED = new InspectorPath("", "", 0);

        public InspectorPath(String sectionTag, String subSectionTag, int subSectionIndex) {
            assert sectionTag != null;
            assert subSectionTag != null;
            assert subSectionIndex >= 0;

            this.sectionTag = sectionTag;
            this.subSectionTag = subSectionTag;
            this.subSectionIndex = subSectionIndex;
        }

        public String getSectionTag() {
            return sectionTag;
        }

        public String getSubSectionTag() {
            return subSectionTag;
        }

        public int getSubSectionIndex() {
            return subSectionIndex;
        }

        /*
         * Object
         */

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.sectionTag);
            hash = 97 * hash + Objects.hashCode(this.subSectionTag);
            hash = 97 * hash + this.subSectionIndex;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final InspectorPath other = (InspectorPath) obj;
            if (!Objects.equals(this.sectionTag, other.sectionTag)) {
                return false;
            }
            if (!Objects.equals(this.subSectionTag, other.subSectionTag)) {
                return false;
            }
            if (this.subSectionIndex != other.subSectionIndex) {
                return false;
            }
            return true;
        }

        /*
         * Comparable
         */
        @Override
        public int compareTo(InspectorPath o) {
            int result;

            result = this.sectionTag.compareTo(o.sectionTag);

            if (result == 0) {
                result = this.subSectionTag.compareTo(o.subSectionTag);
            }

            if (result == 0) {
                if (this.subSectionIndex < o.subSectionIndex) {
                    result = -1;
                } else if (this.subSectionIndex > o.subSectionIndex) {
                    result = +1;
                } else {
                    assert this.subSectionIndex == o.subSectionIndex;
                    assert result == 0;
                }
            }

            return result;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String sectionTag;
            private String subSectionTag;
            private int subSectionIndex;

            public Builder sectionTag(String sectionTag) {
                this.sectionTag = sectionTag;
                return this;
            }

            public Builder subSectionTag(String subSectionTag) {
                this.subSectionTag = subSectionTag;
                return this;
            }

            public Builder subSectionIndex(int subSectionIndex) {
                this.subSectionIndex = subSectionIndex;
                return this;
            }

            public InspectorPath build() {
                return new InspectorPath(sectionTag, subSectionTag, subSectionIndex);
            }
        }
    }

    /**
    *
    */
    public static class InspectorPathComparator implements Comparator<InspectorPath> {

        private final List<String> sectionNames;
        private final Map<String, List<String>> subSectionMap;

        /*
         * Public
         */

        public InspectorPathComparator(List<String> sectionNames, Map<String, List<String>> subSectionMap) {
            this.sectionNames = sectionNames;
            this.subSectionMap = subSectionMap;
        }

        /*
         * Comparator
         */

        @Override
        public int compare(InspectorPath p1, InspectorPath p2) {
            assert p1 != null;
            assert p2 != null;

            final int result;

            if (p1 == p2) {
                result = 0;
            } else {
                final int sectionIndex1 = sectionNames.indexOf(p1.getSectionTag());
                final int sectionIndex2 = sectionNames.indexOf(p2.getSectionTag());

                assert sectionIndex1 != -1 : "sectionTag=" + p1.getSectionTag(); // NOI18N
                assert sectionIndex2 != -1 : "sectionTag=" + p2.getSectionTag(); // NOI18N

                if (sectionIndex1 < sectionIndex2) {
                    result = -1;
                } else if (sectionIndex1 > sectionIndex2) {
                    result = +1;
                } else {
                    assert sectionIndex1 == sectionIndex2;
                    assert p1.getSectionTag().equals(p2.getSectionTag());
                    final List<String> subSections = subSectionMap.get(p1.getSectionTag());

                    assert subSections != null : "sectionTag=" + p1.getSectionTag(); // NOI18N

                    final int subSectionIndex1 = subSections.indexOf(p1.getSubSectionTag());
                    final int subSectionIndex2 = subSections.indexOf(p2.getSubSectionTag());

                    // assert subSectionIndex1 != -1 : "subSectionTag=" + p1.getSubSectionTag(); //
                    // NOI18N
                    // assert subSectionIndex2 != -1 : "subSectionTag=" + p2.getSubSectionTag(); //
                    // NOI18N

                    if (subSectionIndex1 < subSectionIndex2) {
                        result = -1;
                    } else if (subSectionIndex1 > subSectionIndex2) {
                        result = +1;
                    } else {
                        assert subSectionIndex1 == subSectionIndex2;
                        final int propertyIndex1 = p1.getSubSectionIndex();
                        final int propertyIndex2 = p2.getSubSectionIndex();
                        if (propertyIndex1 < propertyIndex2) {
                            result = -1;
                        } else if (propertyIndex1 > propertyIndex2) {
                            result = +1;
                        } else {
                            // result = 0;
                            result = Integer.compare(p1.index, p2.index);
                        }
                    }
                }
            }

            return result;
        }
    }
}
