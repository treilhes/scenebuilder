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
package com.oracle.javafx.scenebuilder.api.preferences.global;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.tooltheme.CssPreference;
import com.oracle.javafx.scenebuilder.api.css.CssVariable;

import javafx.scene.Parent;
import javafx.scene.paint.Color;

@ApplicationSingleton
@PreferenceContext(id = "95778e73-cbce-4a50-b71a-c0c25658bd55", // NO CHECK
        name = ParentRingColorPreference.PREFERENCE_KEY, //
        defaultValueProvider = ParentRingColorPreference.DefaultProvider.class, //
        jsonMapper = ParentRingColorPreference.ColorJsonMapper.class)
public interface ParentRingColorPreference
        extends Preference<Color>, ManagedGlobalPreference, UserPreference<Color>, CssPreference<Color> {

    public static final String PREFERENCE_KEY = "prefs.drop.ring"; // NOCHECK
    public static final Color PREFERENCE_DEFAULT_VALUE = Color.rgb(238, 168, 47); // NOCHECK

    @Override
    default String getLabelI18NKey() {
        return PREFERENCE_KEY;
    }

    @Override
    default Parent getEditor() {
        return getPreferenceEditorFactory().newColorFieldEditor(this);
    }

    @Override
    default PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_B;
    }

    @Override
    default String getOrderKey() {
        return getGroup().getOrderKey() + "_C";
    }

    @Override
    default List<CssClass> getClasses() {
        CssClass allClass = new CssClass(CssVariable.ALL_CLASS);
        allClass.add(new CssProperty(CssVariable.PARENT_COLOR, toHexString(getValue())));
        allClass.add(new CssProperty(CssVariable.DROP_TARGET_COLOR, toHexString(getValue())));
        return List.of(allClass);
    }

    // TODO move below functions to some util package/function
    // Helper method
    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    private String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue())
                + format(value.getOpacity())).toUpperCase();
    }

    public static class DefaultProvider implements DefaultValueProvider<Color> {
        @Override
        public Color get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }

    static class ColorJsonMapper implements JsonMapper<Color> {
        @Override
        public String toJson(Color color) throws JsonProcessingException {
            var privColor = new PrivColor(color.getRed(), color.getGreen(), color.getBlue());
            return Preference.objectMapper.writeValueAsString(privColor);
        }

        @Override
        public Color fromJson(String json, JavaType type) throws JsonProcessingException {
            var privColor = Preference.objectMapper.readValue(json, PrivColor.class);
            var color = Color.color(privColor.red, privColor.green, privColor.blue);
            return color;
        }
    }

    record PrivColor(@JsonProperty double red, @JsonProperty double green, @JsonProperty double blue) {
    }
}
