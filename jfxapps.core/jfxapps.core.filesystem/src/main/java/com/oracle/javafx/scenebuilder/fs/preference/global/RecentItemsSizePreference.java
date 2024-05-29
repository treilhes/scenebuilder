/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.fs.preference.global;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preferences.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preferences.Preference;
import com.gluonhq.jfxapps.core.api.preferences.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preferences.UserPreference;
import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preferences.type.IntegerPreference;

import javafx.scene.Parent;

@Singleton
public class RecentItemsSizePreference extends IntegerPreference implements ManagedGlobalPreference, UserPreference<Integer> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "RECENT_ITEMS_SIZE"; //NOCHECK
    public static final int PREFERENCE_DEFAULT_VALUE = 15;
    public static final Integer[] RECENT_ITEMS_SIZE = {5, 10, 15, 20};

	private final RecentItemsPreference recentItems;
	private final PreferenceEditorFactory preferenceEditorFactory;

	public RecentItemsSizePreference(
			PreferencesContext preferencesContext,
			RecentItemsPreference recentItems,
			PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
		this.recentItems = recentItems;
	}

	@Override
	public Preference<Integer> setValue(Integer newValue) {
		Preference<Integer> that = super.setValue(newValue);
		// Remove last items depending on the size

		if (recentItems != null) {
			while (recentItems.getValue().size() > getValue()) {
	            recentItems.getValue().remove(recentItems.getValue().size() - 1);
	        }
	        recentItems.writeToJavaPreferences();
		}

		return that;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.recent.items";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newChoiceFieldEditor(this, RECENT_ITEMS_SIZE);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_E;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}

}
