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
package com.oracle.javafx.scenebuilder.cssanalyser.preferences.global;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preference.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.type.BooleanPreference;

import javafx.scene.Parent;

@ApplicationSingleton
public class CssTableColumnsOrderingReversedPreference extends BooleanPreference
        implements ManagedGlobalPreference, UserPreference<Boolean> {
    /***************************************************************************
     * * Support Classes * *
     **************************************************************************/

    public enum CSSAnalyzerColumnsOrder {

        DEFAULTS_FIRST {

            @Override
            public String toString() {
                //return I18N.getString("prefs.cssanalyzer.columns.defaults.first");
                return "prefs.cssanalyzer.columns.defaults.first";
            }
        },
        DEFAULTS_LAST {

            @Override
            public String toString() {
                //return I18N.getString("prefs.cssanalyzer.columns.defaults.last");
                return "prefs.cssanalyzer.columns.defaults.last";
            }
        }
    }

    /***************************************************************************
     * * Static fields * *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "CSS_TABLE_COLUMNS_ORDERING_REVERSED"; // NOCHECK
    public static final boolean PREFERENCE_DEFAULT_VALUE = false;

    private final PreferenceEditorFactory preferenceEditorFactory;

    public CssTableColumnsOrderingReversedPreference(@Autowired PreferencesContext preferencesContext,
            @Autowired PreferenceEditorFactory preferenceEditorFactory) {
        super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
        this.preferenceEditorFactory = preferenceEditorFactory;
    }

    @Override
    public PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_C;
    }

    @Override
    public String getOrderKey() {
        return getGroup() + "_D";
    }

    @Override
    public String getLabelI18NKey() {
        return "prefs.cssanalyzer.columns.order";
    }

    @Override
    public Parent getEditor() {
        Function<Boolean, CSSAnalyzerColumnsOrder> adapter = b -> b ? CSSAnalyzerColumnsOrder.DEFAULTS_LAST
                : CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;

        Function<CSSAnalyzerColumnsOrder, Boolean> reverseAdapter = e -> {
            switch (e) {
            case DEFAULTS_FIRST:
                return false;
            default:
                return true;
            }
        };
        return preferenceEditorFactory.newChoiceFieldEditor(this, CSSAnalyzerColumnsOrder.values(), adapter,
                reverseAdapter);
    }

}
