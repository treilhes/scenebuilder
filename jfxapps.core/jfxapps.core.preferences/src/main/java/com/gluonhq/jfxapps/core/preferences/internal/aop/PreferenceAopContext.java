/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.preferences.internal.aop;

import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.gluonhq.jfxapps.boot.api.aop.AopContext;
import com.gluonhq.jfxapps.boot.api.aop.AopFactoryBean;
import com.gluonhq.jfxapps.boot.api.aop.AopMetadata;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.preference.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.NoPreferenceBean;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.ApplicationPreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.GlobalPreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.InstancePreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.PreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;


public class PreferenceAopContext extends AopContext<Preference, PreferenceContext, PreferenceAopContext.PreferenceMetadata> {

    public PreferenceAopContext() {
        super(Preference.class, PreferenceContext.class);
    }

    @Override
    public PreferenceAopContext.PreferenceMetadata loadMetadata(Class<?> clazz) {
        return new PreferenceMetadata(getContexAnnotationClass(), getMarkerClass(), clazz);
    }

    @Override
    public Preference createTarget(JfxAppContext context, PreferenceMetadata metadata) {

        var preferenceInterface = metadata.getBeanClass();

        boolean isEditable = UserPreference.class.isAssignableFrom(preferenceInterface);

        var jfxAppContext = context;
        var i18n = jfxAppContext.getBean(I18N.class);

        var preferenceRepository = jfxAppContext.getBean(PreferenceRepository.class);

        var defaultEditorFactory = isEditable ? jfxAppContext.getBean(PreferenceEditorFactory.class) : null;

        var id = metadata.getId();
        var name = metadata.getName();
        var defaultValueProviderClass = metadata.getDefaultValueProviderClass();
        var valueValidatorClass = metadata.getValueValidatorClass();
        var jsonMapperClass = metadata.getJsonMapperClass();
        var preferenceType = metadata.getGenericTypeInformation();
        var scope = metadata.getScope();
        var dataClass = preferenceType.getType();

        DefaultValueProvider<?> defaultValueProvider = null;
        try {
            defaultValueProvider = defaultValueProviderClass != PreferenceContext.NoOpDefaultValueProvider.class //
                    ? instanciate(jfxAppContext, defaultValueProviderClass)
                    : () -> null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create DefaultValueProvider", e);
        }

        ValueValidator<?> valueValidator = null;
        try {
            valueValidator = valueValidatorClass != PreferenceContext.NoOpValueValidator.class //
                    ? valueValidatorClass.getDeclaredConstructor().newInstance()
                    : (v) -> true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create ValueValidator", e);
        }

        JsonMapper<?> jsonMapper = null;
        try {
            jsonMapper = jsonMapperClass != PreferenceContext.NoOpJsonMapper.class //
                    ? jsonMapperClass.getDeclaredConstructor().newInstance()
                    : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create ValueValidator", e);
        }

        var behaviourClass = switch (scope) {

        case DefaultListableBeanFactory.SCOPE_SINGLETON ->
            new GlobalPreferenceBehaviour(metadata, preferenceRepository);

        case ApplicationSingleton.SCOPE_NAME -> new ApplicationPreferenceBehaviour(metadata, preferenceRepository,
                context.getBean(ApplicationEvents.class));

        case ApplicationInstanceSingleton.SCOPE_NAME -> new InstancePreferenceBehaviour(metadata, preferenceRepository,
                context.getBean(ApplicationEvents.class), context.getBean(FxomEvents.class));

        default -> throw new IllegalArgumentException("Unexpected value: " + scope);
        };

        var preference = new BasePreference(jfxAppContext, i18n, id, name, dataClass, defaultValueProvider, valueValidator,
                behaviourClass, defaultEditorFactory, jsonMapper);

        return preference;
    }

    @Override
    public Class<? extends AopFactoryBean<Preference, PreferenceMetadata>> factoryBeanClass() {
        return PreferenceFactoryBean.class;
    }

    public static class PreferenceFactoryBean extends AopFactoryBean<Preference, PreferenceMetadata> {

        public PreferenceFactoryBean(Class<?> themeInterface) {
            super(themeInterface, new PreferenceAopContext());
        }

    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonPreferenceInterface = !Preference.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isPreference = Arrays.stream(beanDefinition.getMetadata().getInterfaceNames())
                .anyMatch(Preference.class.getName()::equals);
        boolean isInterface = beanDefinition.getMetadata().isInterface();
        boolean hasContextAnnotation = beanDefinition.getMetadata().isAnnotated(PreferenceContext.class.getName());

        return isPreference && isInterface && isNonPreferenceInterface && hasContextAnnotation;
    }

    @Override
    public Class<? extends NoPreferenceBean> getExclusionAnnotation() {
        return NoPreferenceBean.class;
    }

    public class BasePreference<T> implements Preference<T>, UserPreference<T> {

        private static final Logger logger = LoggerFactory.getLogger(BasePreference.class);

        private final JfxAppContext context;
        private final I18N i18n;
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
                I18N i18n,
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
            this.i18n = i18n;
            this.id = id;
            this.name = name;
            this.defaultValueProvider = defaultValueProvider != null ? defaultValueProvider : () -> null;
            this.valueValidator = valueValidator != null ? valueValidator : v -> getValue() != null;
            this.preferenceBehaviour = preferenceBehaviour;
            this.value = new SimpleObjectProperty<>(getDefault());
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

        @Override
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

        @Override
        public PreferenceGroup getGroup() {
            throw new UnsupportedOperationException("UserPreference.getGroup() must be implemented using default method in interface");
        }

        @Override
        public String getOrderKey() {
            throw new UnsupportedOperationException("UserPreference.getOrderKey() must be implemented using default method in interface");
        }

        @Override
        public String getLabelI18NKey() {
            throw new UnsupportedOperationException("UserPreference.getLabelI18NKey() must be implemented using default method in interface");
        }

        @Override
        public Parent getEditor() {
            throw new UnsupportedOperationException("UserPreference.getEditor() must be implemented using default method in interface");
        }

        @Override
        public I18N getI18n() {
            return i18n;
        }

    }

    public static class PreferenceMetadata extends AopMetadata<PreferenceContext, Preference> {

        private UUID id;
        private String name;
        private Class<? extends DefaultValueProvider<?>> defaultValueProviderClass;
        private Class<? extends ValueValidator<?>> valueValidatorClass;
        private Class<? extends JsonMapper<?>> jsonMapperClass;

        public PreferenceMetadata(Class<PreferenceContext> annotationClass, Class<Preference> markerClass, Class<?> themeInterface) {
            super(annotationClass, markerClass, themeInterface);
        }

        @Override
        protected void loadMetadata(PreferenceContext annotation) {
            if (hasAnnotation()) {
                this.id = UUID.fromString(annotation.id());
                this.name = annotation.name();
                this.defaultValueProviderClass = annotation.defaultValueProvider();
                this.valueValidatorClass = annotation.validator();
                this.jsonMapperClass = annotation.jsonMapper();
            } else {
                this.id = null;
                this.name = null;
                this.defaultValueProviderClass = null;
                this.valueValidatorClass = null;
                this.jsonMapperClass = null;
            }
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Class<? extends DefaultValueProvider<?>> getDefaultValueProviderClass() {
            return defaultValueProviderClass;
        }

        public Class<? extends ValueValidator<?>> getValueValidatorClass() {
            return valueValidatorClass;
        }

        public Class<? extends JsonMapper<?>> getJsonMapperClass() {
            return jsonMapperClass;
        }

    }
}
