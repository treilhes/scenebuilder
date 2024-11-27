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
package com.oracle.javafx.scenebuilder.ext.theme.preference;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeManager;
import com.oracle.javafx.scenebuilder.ext.theme.DefaultThemesList;

import javafx.scene.Parent;

//@formatter:off
@ApplicationSingleton
@PreferenceContext(
    id = "3971972d-6ad1-4b89-afc4-ea6d0bd30b18",
    name = ThemePreference.PREFERENCE_KEY,
    defaultValueProvider = ThemePreference.DefaultProvider.class)
//@formatter:on
public interface ThemePreference
        extends Preference<Class<? extends Theme>>, ManagedGlobalPreference, UserPreference<Class<? extends Theme>> {

    public static final String PREFERENCE_KEY = "prefs.global.theme";
    public static final Class<? extends Theme> PREFERENCE_DEFAULT_VALUE = DefaultThemesList.Modena.class;

    @Override
    default String getLabelI18NKey() {
        return PREFERENCE_KEY;
    }

    @SuppressWarnings("unchecked")
    @Override
    default Parent getEditor() {

        var manager = getContext().getBean(ThemeManager.class);
        var themeClasses = manager.getThemes().stream().map(tp -> tp.getClass()).toList();
        return getPreferenceEditorFactory().newChoiceFieldEditor(this,
                themeClasses.toArray((Class<? extends Theme>[]) new Class[0]), (c) -> getContext().getBean(c).getName());
    }

    @Override
    default PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_D;
    }

    @Override
    default String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }

    public static class DefaultProvider implements DefaultValueProvider<Class<? extends Theme>> {
        @Override
        public Class<? extends Theme> get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }
}
