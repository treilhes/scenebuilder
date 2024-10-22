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
package com.oracle.javafx.scenebuilder.gluon.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.ManagedDocumentPreference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.type.EnumPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;

import javafx.scene.Parent;

@Component("gluonSwatchDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonSwatchDocumentPreference extends EnumPreference<GluonSwatch> implements ManagedDocumentPreference, UserPreference<GluonSwatch> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "gluonSwatch"; //NOCHECK

    private final PreferenceEditorFactory preferenceEditorFactory;

	public GluonSwatchDocumentPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory,
			@Autowired com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference defaultGluonSwatchPreference) {
		super(preferencesContext, PREFERENCE_KEY, GluonSwatch.class, defaultGluonSwatchPreference.getValue());
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.document.gluonswatch";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newEnumFieldEditor(this, (g) -> g.createGraphic());
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_D;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_B";
	}

}
