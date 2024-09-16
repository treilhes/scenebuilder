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
package com.gluonhq.jfxapps.core.accelerators.preferences.global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.preferences.MapPreferences;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCombination;

public class FocusedAcceleratorsMapPreference extends MapPreferences<Class<? extends Action>, ObservableList<KeyCombination>> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    private static final String PREFERENCE_KEY = "Accelerators-%s"; //NOCHECK
    private static final String SEPARATOR = "//"; //NOCHECK

    public FocusedAcceleratorsMapPreference(
            PreferencesContext preferencesContext,
            Class<?> focusedClass) {
        super(preferencesContext, PreferencesContext.generateKey(String.format(PREFERENCE_KEY, focusedClass.getName())));
    }

    @Override
    public String keyString(Class<? extends Action> key) {
        return key.getName();
    }

    @Override
    public String valueString(ObservableList<KeyCombination> value) {
        if (value == null) {
            return "";
        }
        return String.join(SEPARATOR, value.toString());
    }

    @Override
    public Class<? extends Action> fromKeyString(String key) {
        try {
            return (Class<? extends Action>)Class.forName(key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ObservableList<KeyCombination> fromValueString(String value) {
        if (value == null || value.isBlank()) {
            return FXCollections.observableArrayList();
        }
        return FXCollections.observableArrayList(Arrays.stream(value.split(SEPARATOR)).map(KeyCombination::valueOf).collect(Collectors.toList()));
    }

    @Override
    public void read() {
        var backup = new HashMap<>(getValue());

        super.read();

        if (getValue().isEmpty()) {
            getValue().putAll(backup);
        }
    }

    @ApplicationSingleton
    public static final class Factory {

        private final Map<Class<?>, FocusedAcceleratorsMapPreference> cache = new HashMap<>();
        private final PreferencesContext preferencesContext;

        public Factory(PreferencesContext preferencesContext) {
            this.preferencesContext = preferencesContext;
        }

        public FocusedAcceleratorsMapPreference get(Class<?> focusedClass) {
            if (cache.containsKey(focusedClass)) {
                return cache.get(focusedClass);
            }

            FocusedAcceleratorsMapPreference focusedPref = new FocusedAcceleratorsMapPreference(preferencesContext, focusedClass);
            focusedPref.readFromJavaPreferences();
            cache.put(focusedClass, focusedPref);

            return focusedPref;
        }
    }
}
