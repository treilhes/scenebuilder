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
package com.gluonhq.jfxapps.ext.container.preferences.global;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolTheme;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolThemeProvider;
import com.gluonhq.jfxapps.ext.container.tooltheme.DefaultToolThemesList;

import javafx.scene.Parent;

@ApplicationSingleton
@PreferenceContext(id = "f3f9b196-9e90-4aa8-acc4-ba5c7b0defcd", // NO CHECK
        name = ToolThemePreference.PREFERENCE_KEY,
        defaultValueProvider = ToolThemePreference.DefaultProvider.class)
public interface ToolThemePreference
        extends Preference<Class<? extends ToolTheme>>, ManagedGlobalPreference, UserPreference<Class<? extends ToolTheme>> {

    public static final String PREFERENCE_KEY = "TOOL_THEME"; // NOI18N
    public static final Class<? extends ToolTheme> PREFERENCE_DEFAULT_VALUE = DefaultToolThemesList.Default.class;

    @Override
    default String getLabelI18NKey() {
        return "prefs.tooltheme";
    }

    @SuppressWarnings("unchecked")
    @Override
    default Parent getEditor() {
        var toolThemeProviders = getContext().getBeansOfType(ToolThemeProvider.class);
        var toolThemeClasses = toolThemeProviders.values().stream().map(ToolThemeProvider::toolThemes).toList();
        var i18n = getContext().getBean(I18N.class);
        return getPreferenceEditorFactory().newChoiceFieldEditor(this,
                toolThemeClasses.toArray((Class<? extends ToolTheme>[]) new Class[0]), (c) -> ToolTheme.name(i18n, c));
    }

    @Override
    default PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_C;
    }

    @Override
    default String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }

    public static class DefaultProvider implements DefaultValueProvider<Class<? extends ToolTheme>> {
        @Override
        public Class<? extends ToolTheme> get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }
}
