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

import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.tooltheme.CssPreference;

import javafx.scene.Parent;

@ApplicationSingleton
@PreferenceContext(id = "9cf53239-6948-44ce-b473-7424178a8ea9", // NO CHECK
        name = AccordionAnimationPreference.PREFERENCE_KEY,
        defaultValueProvider = AccordionAnimationPreference.DefaultProvider.class)
public interface AccordionAnimationPreference
        extends Preference<Boolean>, ManagedGlobalPreference, UserPreference<Boolean>, CssPreference<Boolean> {

    public static final String PREFERENCE_KEY = "prefs.animate.accordion"; //NOCHECK
    public static final boolean PREFERENCE_DEFAULT_VALUE = true;

	@Override
	default String getLabelI18NKey() {
		return PREFERENCE_KEY;
	}

	@Override
	default Parent getEditor() {
		return getPreferenceEditorFactory().newBooleanFieldEditor(this);
	}


	@Override
	default PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_F;
	}

	@Override
	default String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}

    @Override
    default List<CssClass> getClasses() {
        CssClass css = new CssClass(".titled-pane");
        css.add(new CssProperty("-fx-animated", Boolean.toString(getValue())));
        return List.of(css);
    }

    public static class DefaultProvider implements DefaultValueProvider<Boolean> {
        @Override
        public Boolean get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }
}
