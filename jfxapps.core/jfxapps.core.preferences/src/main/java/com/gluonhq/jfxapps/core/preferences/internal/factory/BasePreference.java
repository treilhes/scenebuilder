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
package com.gluonhq.jfxapps.core.preferences.internal.factory;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.PreferenceBehaviour;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class BasePreference<T> implements Preference<T> {

    private static final Logger logger = LoggerFactory.getLogger(BasePreference.class);

    private final JfxAppContext context;
    private final UUID id;
    private final String name;
    private final Class<T> dataClass;
    private final Property<T> value;
    private final DefaultValueProvider<T> defaultValueProvider;
    private final ValueValidator<T> valueValidator;
    private final PreferenceBehaviour preferenceBehaviour;
    private final PreferenceEditorFactory preferenceEditorFactory;
    private final JsonMapper jsonMapper;

    //@formatter:off
    public BasePreference(
            JfxAppContext context,
            UUID id,
            String name,
            Class<T> dataClass,
            DefaultValueProvider<T> defaultValueProvider,
            ValueValidator<T> valueValidator,
            PreferenceBehaviour preferenceBehaviour,
            PreferenceEditorFactory preferenceEditorFactory,
            JsonMapper<?> jsonMapper) {
        //@formatter:on
        this.context = context;
        this.id = id;
        this.name = name;
        this.defaultValueProvider = defaultValueProvider != null ? defaultValueProvider : () -> null;
        this.valueValidator = valueValidator != null ? valueValidator : v -> getValue() != null;
        this.preferenceBehaviour = preferenceBehaviour;
        this.value = new SimpleObjectProperty<T>(getDefault());
        this.dataClass = dataClass;
        this.preferenceEditorFactory = preferenceEditorFactory;
        this.jsonMapper = jsonMapper;
        this.load();
    }

    @Override
    public JfxAppContext getContext() {
        return context;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value.getValue();
    }

    @Override
    public Preference<T> setValue(T value) {
        this.value.setValue(value);
        return this;
    }

    @Override
    public ObservableValue<T> getObservableValue() {
        return this.value;
    }

    @Override
    public Class<T> getDataClass() {
        return dataClass;
    }

    @Override
    public T getDefault() {
        return defaultValueProvider.get();
    }

    @Override
    public Preference<T> reset() {
        setValue(getDefault());
        return this;
    }

    @Override
    public boolean isValid() {
        return valueValidator.test(getValue());
    }

    @Override
    public void load() {
        preferenceBehaviour.read(this);
    }

    @Override
    public void save() {
        preferenceBehaviour.write(this);
    }

    public PreferenceEditorFactory getPreferenceEditorFactory() {
        return preferenceEditorFactory;
    }

    public String toJson() throws JsonProcessingException {
        return jsonMapper != null ? jsonMapper.toJson(getValue()) : objectMapper.writeValueAsString(getValue());
    }

    public void fromJson(String json, JavaType type) throws JsonProcessingException {
        T value = jsonMapper != null ? (T) jsonMapper.fromJson(json, type) : (T) objectMapper.readValue(json, type);
        setValue(value);
    }
}
