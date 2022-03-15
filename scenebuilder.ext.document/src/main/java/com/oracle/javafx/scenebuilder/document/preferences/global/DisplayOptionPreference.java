/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.document.preferences.global;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.BeanPreference;
import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;

import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

@Component
public class DisplayOptionPreference extends BeanPreference<DisplayOption>
        implements ManagedGlobalPreference, UserPreference<Class<DisplayOption>> {

    /***************************************************************************
     * * Static fields * *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "HIERARCHY_DISPLAY_OPTION"; // NOCHECK

    @SuppressWarnings("unchecked")
    public static final Class<DisplayOption> PREFERENCE_DEFAULT_VALUE = (Class<DisplayOption>) MetadataInfoDisplayOption.class
            .asSubclass(DisplayOption.class);

    private final List<Class<DisplayOption>> displayOptions;

    public DisplayOptionPreference(PreferencesContext preferencesContext, SceneBuilderBeanFactory context) {
        super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE, context);
        this.displayOptions = context.getBeanClassesForType(DisplayOption.class);
    }

    @Override
    public String getLabelI18NKey() {
        return "prefs.hierarchy.displayoption";
    }

    @Override
    public Parent getEditor() {
        ComboBox<Class<DisplayOption>> field = new ComboBox<>();

        Supplier<ListCell<Class<DisplayOption>>> cell = () -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Class<DisplayOption> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        String name = DisplayOption.getName(item);
                        name = I18N.getStringOrDefault(name, name);
                        setText(name);
                    }
                }

            };
        };

        field.setCellFactory((a) -> cell.get());
        field.setButtonCell(cell.get());
        field.getItems().setAll(displayOptions);
        field.setValue(getValue());
        field.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
            setValue(n).writeToJavaPreferences();
        });
        getObservableValue().addListener((ob, o, n) -> {
            field.setValue(n);
        });
        return field;
    }

    @Override
    public PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_C;
    }

    @Override
    public String getOrderKey() {
        return getGroup().getOrderKey() + "_C";
    }
}
