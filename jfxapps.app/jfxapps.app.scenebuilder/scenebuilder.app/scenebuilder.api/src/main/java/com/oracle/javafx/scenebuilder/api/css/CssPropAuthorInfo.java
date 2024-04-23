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
package com.oracle.javafx.scenebuilder.api.css;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

import javafx.css.CssMetaData;
import javafx.css.Rule;
import javafx.css.Style;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;

/**
 * CSS information attached to a Bean Property when styled with Author or Inline
 * origin.
 *
 */
public class CssPropAuthorInfo {

    private final ValuePropertyMetadata prop;
    private final CssMetaData<?, ?> styleable;
    private final StyleableProperty<?> value;
    private final Object val;
    private final List<Style> styles = new ArrayList<>();

    public CssPropAuthorInfo(ValuePropertyMetadata prop, StyleableProperty<?> value, CssMetaData<?, ?> styleable) {
        this(prop, value, styleable, null);
    }

    private CssPropAuthorInfo(ValuePropertyMetadata prop, StyleableProperty<?> value, CssMetaData<?, ?> styleable,
            Object val) {
        this.prop = prop;
        this.styleable = styleable;
        this.value = value;
        this.val = val;
    }

    public CssPropAuthorInfo(StyleableProperty<?> val, CssMetaData<?, ?> styleable, Object value) {
        this(null, val, styleable, value);
    }

    public StyleOrigin getOrigin() {
        return value.getStyleOrigin();
    }

    public URL getMainUrl() {
        if (getStyles().isEmpty()) {
            return null;
        } else {
            Rule rule = getStyles().get(0).getDeclaration().getRule();
            if (rule == null) {
                return null;
            } else {
                try {
                    return new URL(rule.getStylesheet().getUrl());
                } catch (MalformedURLException ex) {
                    System.out.println(ex.getMessage() + " " + ex);
                    return null;
                }
            }
        }
    }

    public List<Style> getStyles() {
        return styles;
    }

    public Object getFxValue() {
        return val != null ? val : value.getValue();
    }

    public boolean isInline() {
        StyleOrigin o = getOrigin();
        return o != null && o.equals(StyleOrigin.INLINE);
    }

    /**
     * @return the prop
     */
    public ValuePropertyMetadata getProp() {
        return prop;
    }

    /**
     * @return the cssProp
     */
    public CssMetaData<?, ?> getCssProp() {
        return styleable;
    }

}