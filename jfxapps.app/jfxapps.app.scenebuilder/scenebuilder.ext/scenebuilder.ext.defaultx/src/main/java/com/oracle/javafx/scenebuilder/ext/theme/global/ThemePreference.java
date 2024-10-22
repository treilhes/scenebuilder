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
package com.oracle.javafx.scenebuilder.ext.theme.global;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.type.ObjectPreference;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;
import com.oracle.javafx.scenebuilder.ext.theme.DefaultThemesList;

import javafx.scene.Parent;

@ApplicationSingleton
public class ThemePreference extends ObjectPreference<Class<? extends Theme>> implements ManagedGlobalPreference, UserPreference<Class<? extends Theme>> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "theme"; //NOCHECK
    public static final Class<? extends Theme> PREFERENCE_DEFAULT_VALUE = DefaultThemesList.Modena.class;

    private final I18N i18n;
    private final List<Class<? extends Theme>> themeClasses;

	private final PreferenceEditorFactory preferenceEditorFactory;

	public ThemePreference(
	        I18N i18n,
			PreferencesContext preferencesContext,
			PreferenceEditorFactory preferenceEditorFactory,
			List<ThemeProvider> themeProviders) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.i18n = i18n;
		this.preferenceEditorFactory = preferenceEditorFactory;
		themeClasses = new ArrayList<>();
		themeProviders.forEach(tp -> themeClasses.addAll(tp.themes()));
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
			setValue(clsName == null ? getDefault() : (Class<? extends Theme>) Class.forName(clsName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.global.theme";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newChoiceFieldEditor(this,
				themeClasses.toArray((Class<? extends Theme>[])new Class[0]), (c) -> Theme.name(i18n, c));
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_D;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}

}
