/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.metadata.property.value;

import java.net.URISyntaxException;
import java.net.URL;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.util.URLUtils;

/**
 *
 */
public abstract class StringPropertyMetadata extends TextEncodablePropertyMetadata<String> {

    private static final PropertyName valueName = new PropertyName("value"); //NOCHECK

    private final boolean detectFileURL;

//    protected StringPropertyMetadata(PropertyName name, boolean readWrite,
//            String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//        super(name, String.class, readWrite, defaultValue, inspectorPath);
//        this.detectFileURL = detectFileURL;
//    }

//    protected StringPropertyMetadata(PropertyName name, boolean readWrite,
//            String defaultValue, InspectorPath inspectorPath) {
//        this(name, readWrite, defaultValue, inspectorPath, false);
//    }

    protected StringPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.detectFileURL = builder.detectFileURL;
    }

    public boolean isMultiline() {
        return false;
    }
    /*
     * Values of a string property can be represented in multiple ways.
     *
     * Case 1 : as an XML attribute (ie an FXOMPropertyT)
     *      text='Button'
     *      url='@Desktop/Blah.css'
     *
     * Case 2 : as an XML element of type String (also an FXOMPropertyT)
     *      <text><String fx:value='Button'/><text>
     *
     * Case 3 : as an XML element of type URL/Boolean/Double... (ie an FXOMPropertyC)
     *      <text><URL value='@Desktop/Blah.css' /></text>
     *      <text><Double fx:value='12.0' /></text>
     */


    /*
     * TextEncodablePropertyMetadata
     */

    @Override
    public String makeValueFromFxomInstance(FXOMInstance valueFxomInstance) {
        final String result;

        final Class<?> valueClass = valueFxomInstance.getDeclaredClass();
        if (valueClass == URL.class) {
            final FXOMProperty p = valueFxomInstance.getProperties().get(valueName);
            if (p instanceof FXOMPropertyT) {
                result = ((FXOMPropertyT) p).getValue();
            } else {
                assert false;
                result = getDefaultValue();
            }
        } else {
            result = valueFxomInstance.getFxValue();
        }

        return result;
    }

    @Override
    public boolean canMakeStringFromValue(String value) {
        return true;
    }

    @Override
    public String makeValueFromString(String string) {
        return string;
    }

    @Override
    public FXOMInstance makeFxomInstanceFromValue(String value, FXOMDocument fxomDocument) {
        final FXOMInstance result;

        boolean shouldEncodeAsURL;
        final PrefixedValue pv = new PrefixedValue(value);
        if (pv.isClassLoaderRelativePath() || pv.isDocumentRelativePath()) {
            shouldEncodeAsURL = true;
        } else if (pv.isPlainString() && detectFileURL) {
            try {
                shouldEncodeAsURL = URLUtils.getFile(value) != null;
            } catch(URISyntaxException x) {
                shouldEncodeAsURL = false;
            }
        } else {
            shouldEncodeAsURL = false;
        }

        if (shouldEncodeAsURL) {
            // String value must be expressed using a URL element
            // <URL value='@Desktop/IssueTracking.css' />
            final FXOMPropertyT newProperty = new FXOMPropertyT(fxomDocument, valueName, value);
            result = new FXOMInstance(fxomDocument, URL.class);
            newProperty.addToParentInstance(-1, result);
        } else {
            result = new FXOMInstance(fxomDocument, String.class);
            result.setFxValue(value);
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends TextEncodablePropertyMetadata.AbstractBuilder<SELF, TOBUILD, String> {
        protected boolean detectFileURL;

        public AbstractBuilder() {
            super();
            withValueClass(String.class);
        }

        public SELF withFileUrlDetection(boolean fileUrlDetection) {
            this.detectFileURL = fileUrlDetection;
            return self();
        }
    }

    public static class StyleStringPropertyMetadata extends StringPropertyMetadata {
//        protected StyleStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//            super(name, readWrite, defaultValue, inspectorPath, detectFileURL);
//        }
//
//        protected StyleStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath, false);
//        }

        public StyleStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, StyleStringPropertyMetadata> {
            @Override
            public StyleStringPropertyMetadata build() {
                return new StyleStringPropertyMetadata(this);
            }
        }
    }

    public static class IdStringPropertyMetadata extends StringPropertyMetadata {
//        protected IdStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//            super(name, readWrite, defaultValue, inspectorPath, detectFileURL);
//        }
//
//        protected IdStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath, false);
//        }

        public IdStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, IdStringPropertyMetadata> {
            @Override
            public IdStringPropertyMetadata build() {
                return new IdStringPropertyMetadata(this);
            }
        }
    }

    public static class CharsetStringPropertyMetadata extends StringPropertyMetadata {
//        protected CharsetStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//            super(name, readWrite, defaultValue, inspectorPath, detectFileURL);
//        }
//
//        protected CharsetStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath, false);
//        }

        public CharsetStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, CharsetStringPropertyMetadata> {
            @Override
            public CharsetStringPropertyMetadata build() {
                return new CharsetStringPropertyMetadata(this);
            }
        }
    }

    public static class I18nStringPropertyMetadata extends StringPropertyMetadata {
//        protected I18nStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//            super(name, readWrite, defaultValue, inspectorPath, detectFileURL);
//        }
//
//        protected I18nStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath, false);
//        }

        public I18nStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, I18nStringPropertyMetadata> {
            @Override
            public I18nStringPropertyMetadata build() {
                return new I18nStringPropertyMetadata(this);
            }
        }
    }

    public static class MultilineI18nStringPropertyMetadata extends I18nStringPropertyMetadata {
//        protected MultilineI18nStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath, boolean detectFileURL) {
//            super(name, readWrite, defaultValue, inspectorPath, detectFileURL);
//        }
//
//        protected MultilineI18nStringPropertyMetadata(PropertyName name, boolean readWrite,
//                String defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath, false);
//        }

        public MultilineI18nStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isMultiline() {
            return true;
        }

        public static final class Builder extends AbstractBuilder<Builder, MultilineI18nStringPropertyMetadata> {
            @Override
            public MultilineI18nStringPropertyMetadata build() {
                return new MultilineI18nStringPropertyMetadata(this);
            }
        }
    }

    public static class SourceStringPropertyMetadata extends StringPropertyMetadata {

      public SourceStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
          super(builder);
      }

      public static final class Builder extends AbstractBuilder<Builder, SourceStringPropertyMetadata> {
          @Override
          public SourceStringPropertyMetadata build() {
              return new SourceStringPropertyMetadata(this);
          }
      }
  }

    public static class ResourceStringPropertyMetadata extends StringPropertyMetadata {

        public ResourceStringPropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        public static final class Builder extends AbstractBuilder<Builder, ResourceStringPropertyMetadata> {
            @Override
            public ResourceStringPropertyMetadata build() {
                return new ResourceStringPropertyMetadata(this);
            }
        }
    }
}
