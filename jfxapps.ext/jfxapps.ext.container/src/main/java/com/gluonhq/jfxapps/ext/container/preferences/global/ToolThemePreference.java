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
package com.gluonhq.jfxapps.ext.container.preferences.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preferences.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preferences.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preferences.UserPreference;
import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preferences.type.ObjectPreference;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolTheme;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolThemeProvider;
import com.gluonhq.jfxapps.ext.container.tooltheme.DefaultToolThemesList;

import javafx.scene.Parent;

@Component
public class ToolThemePreference extends ObjectPreference<Class<? extends ToolTheme>>
        implements ManagedGlobalPreference, UserPreference<Class<? extends ToolTheme>> {
//	private static ToolThemePreference instance = null;
    /***************************************************************************
     * * Static fields * *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "TOOL_THEME"; // NOI18N
    public static final Class<? extends ToolTheme> PREFERENCE_DEFAULT_VALUE = DefaultToolThemesList.Default.class;

    private final List<Class<? extends ToolTheme>> toolThemeClasses;

    // TODO bad bad bad, but PropertyEditors need this instance and i don't want to
    // change editors constructors
    // TODO editors musn't use dialogs internaly, change this later
    // TODO same problem for FXOMLoadr
//    public static ToolThemePreference getInstance() {
//    	return instance;
//    }

    private final PreferenceEditorFactory preferenceEditorFactory;

    public ToolThemePreference(@Autowired PreferencesContext preferencesContext,
            @Autowired PreferenceEditorFactory preferenceEditorFactory,
            @Autowired List<ToolThemeProvider> toolThemeProviders) {
        super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
//		instance = this;
        this.preferenceEditorFactory = preferenceEditorFactory;
        toolThemeClasses = new ArrayList<>();
        toolThemeProviders.forEach(tp -> toolThemeClasses.addAll(tp.toolThemes()));
    }

    @Override
    protected void write() {
        getNode().put(getName(), getValue().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void read() {
        assert getName() != null;
        String clsName = getNode().get(getName(), null);
        try {
            setValue(clsName == null ? getDefault() : (Class<? extends ToolTheme>) Class.forName(clsName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLabelI18NKey() {
        return "prefs.tooltheme";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Parent getEditor() {
        return preferenceEditorFactory.newChoiceFieldEditor(this,
                toolThemeClasses.toArray((Class<? extends ToolTheme>[]) new Class[0]), (c) -> ToolTheme.name(c));
    }

    @Override
    public PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_C;
    }

    @Override
    public String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }
}
