/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.list;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.fxml.FXMLLoader;

/**
 *
 */
public class StringListPropertyMetadata extends ListValuePropertyMetadata<String> {

    private final static I18nStringPropertyMetadata itemMetadata = new I18nStringPropertyMetadata.Builder()
            .withName(null)
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED)
            .withFileUrlDetection(true).build();

//    public StringListPropertyMetadata(PropertyName name, boolean readWrite, List<String> defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, String.class, itemMetadata, readWrite, defaultValue, inspectorPath);
//    }

    public StringListPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    public static List<String> splitValue(String listValue) {
        final List<String> result = new ArrayList<>();

        final String[] values = listValue.split(FXMLLoader.ARRAY_COMPONENT_DELIMITER);
        for (int i = 0, count = values.length; i < count; i++) {
            result.add(values[i]);
        }

        return result;
    }

    public static String assembleValue(List<String> valueItems) {
        assert FXMLLoader.ARRAY_COMPONENT_DELIMITER.length() == 1;

        final StringBuilder result = new StringBuilder();
        for (String s : valueItems) {
            assert s.indexOf(FXMLLoader.ARRAY_COMPONENT_DELIMITER.charAt(0)) == -1;
            if (result.length() >= 1) {
                result.append(FXMLLoader.ARRAY_COMPONENT_DELIMITER);
            }
            result.append(s);
        }

        return result.toString();
    }

    /*
     * ListValuePropertyMetadata
     */

    @Override
    protected boolean canMakeStringFromValue(List<String> value) {
        return value.size() == 1;
    }

    @Override
    protected String makeStringFromValue(List<String> value) {
        assert canMakeStringFromValue(value);
        return value.get(0);
    }

    @Override
    protected List<String> makeValueFromString(String string) {
        return splitValue(string);
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD>
            extends ListValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD, String> {

        public AbstractBuilder() {
            super();
            withItemClass(String.class);
            withItemMetadata(StringListPropertyMetadata.itemMetadata);
        }

        @Override
        public SELF withDefaultValue(List<String> defaultValue) {
            return super.withDefaultValue(defaultValue);
        }

    }

    public static final class Builder extends AbstractBuilder<Builder, StringListPropertyMetadata> {
        @Override
        public StringListPropertyMetadata build() {
            return new StringListPropertyMetadata(this);
        }
    }

    public static class StyleClassStringListPropertyMetadata extends StringListPropertyMetadata {
//        protected StyleClassStringListPropertyMetadata(PropertyName name, boolean readWrite, List<String> defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }
//
        protected StyleClassStringListPropertyMetadata(AbstractBuilder<?,?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, StyleClassStringListPropertyMetadata> {
            @Override
            public StyleClassStringListPropertyMetadata build() {
                return new StyleClassStringListPropertyMetadata(this);
            }
        }
    }

    public static class StylesheetsStringListPropertyMetadata extends StringListPropertyMetadata {
//        public StylesheetsStringListPropertyMetadata(PropertyName name, boolean readWrite, List<String> defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected StylesheetsStringListPropertyMetadata(AbstractBuilder<?,?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, StylesheetsStringListPropertyMetadata> {
            @Override
            public StylesheetsStringListPropertyMetadata build() {
                return new StylesheetsStringListPropertyMetadata(this);
            }
        }
    }

    public static class SourceStringListPropertyMetadata extends StringListPropertyMetadata {
//        public SourceStringListPropertyMetadata(PropertyName name, boolean readWrite, List<String> defaultValue,
//                InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }
//
        protected SourceStringListPropertyMetadata(AbstractBuilder<?,?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, SourceStringListPropertyMetadata> {
            @Override
            public SourceStringListPropertyMetadata build() {
                return new SourceStringListPropertyMetadata(this);
            }
        }
    }

}
