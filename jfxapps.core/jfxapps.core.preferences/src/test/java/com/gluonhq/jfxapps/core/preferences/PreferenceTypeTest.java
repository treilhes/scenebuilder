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
package com.gluonhq.jfxapps.core.preferences;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanDefinitionRegistryPostProcessor;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;
import com.gluonhq.jfxapps.test.JfxAppsTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;

@JfxAppsTest(properties = {
        "spring.jpa.show-sql=true",
})
//@formatter:off
@ContextConfiguration(classes = {
        PreferenceTypeTest.Config.class,
        PreferenceBeanDefinitionRegistryPostProcessor.class,
        PreferenceTypeTest.ObjectMapPreference.class,
        PreferenceTypeTest.ClassListPreference.class,
        PreferenceTypeTest.NestedMapPreference.class,
        PreferenceTypeTest.ColorPreference.class,
        PreferenceTypeTest.SimpleTypePreference.class,
        PreferenceTypeTest.EnumTypePreference.class,
        PreferenceTypeTest.FilePreference.class,
        PreferenceTypeTest.UuidPreference.class

})
//@formatter:on
public class PreferenceTypeTest {
    @SpringBootConfiguration
    @EnableJpaRepositories(basePackageClasses = { PreferenceRepository.class })
    @EntityScan(basePackageClasses = { PreferenceEntity.class })
    @DataJpaTest
    static class Config {
    }

    @Test
    void simple_type_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, SimpleTypePreference.class, () -> 10);
    }

    @Test
    void enum_type_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, EnumTypePreference.class, () -> EnumType.B);
    }

    @Test
    void file_type_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, FilePreference.class, () -> new File("./src").getAbsoluteFile());
    }

    @Test
    void observablemap_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, ObjectMapPreference.class, () -> {
            ObservableMap<Value, Value> value = FXCollections.observableHashMap();
            value.put(new Value("key"), new Value("key"));
            return value;
        });
    }

    @Test
    void classlist_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, ClassListPreference.class, () -> {
            ObservableList<Class<?>> value = FXCollections.observableArrayList();
            value.add(TestInfo.class);
            value.add(JfxAppContext.class);
            return value;
        });
    }

    @Test
    void nested_observablemap_must_save_and_load(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, NestedMapPreference.class, () -> {
            ObservableMap<Value, ObservableMap<Value,Value>> value = FXCollections.observableHashMap();
            ObservableMap<Value, Value> subValue = FXCollections.observableHashMap();
            subValue.put(new Value("subkey"), new Value("value"));
            value.put(new Value("key"), subValue);
            return value;
        });
    }

    @Test
    void color_must_save_and_load_with_custom_json_mapper(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, ColorPreference.class, () -> {
            return Color.ALICEBLUE;
        });
    }

    @Test
    void uuid_must_save_and_load_with_custom_json_mapper(TestInfo testInfo, JfxAppContext context) throws Exception {
        getBeanSetSaveLoadAndTestEquals(context, UuidPreference.class, () -> {
            return UUID.randomUUID();
        });
    }

    private <T> void getBeanSetSaveLoadAndTestEquals(JfxAppContext context, Class<? extends Preference<T>> cls, Supplier<T> supplier) {
        var preference = context.getBean(cls);
        T value = supplier.get();
        preference.setValue(value);
        preference.save();
        preference.setValue(null);
        preference.load();
        var loadedValue = preference.getValue();
        assertEquals(value, loadedValue);
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface SimpleTypePreference extends Preference<Integer> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface EnumTypePreference extends Preference<EnumType> {
    }
    public enum EnumType {
        A, B, C
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface FilePreference extends Preference<File> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface ObjectMapPreference extends Preference<ObservableMap<Value, Value>> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface ClassListPreference extends Preference<ObservableList<Class<?>>> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface NestedMapPreference extends Preference<ObservableMap<Value, ObservableMap<Value,Value>>> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global")
    public static interface UuidPreference extends Preference<UUID> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global", jsonMapper = ColorPreference.ColorJsonMapper.class)
    public static interface ColorPreference extends Preference<Color> {

        static class ColorJsonMapper implements JsonMapper<Color> {

            @Override
            public String toJson(Color color) throws JsonProcessingException {
                var privColor = new PrivColor(color.getRed(), color.getGreen(), color.getBlue());
                return Preference.objectMapper.writeValueAsString(privColor);
            }

            @Override
            public Color fromJson(String json, JavaType type) throws JsonProcessingException {
                var privColor = Preference.objectMapper.readValue(json, PrivColor.class);
                var color = Color.color(privColor.red, privColor.green, privColor.blue);
                return color;
            }
        }

        record PrivColor(@JsonProperty double red, @JsonProperty double green, @JsonProperty double blue) {
        }
    }

    public static class Value {
        private String value;

        public Value() {
        }

        public Value(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Value other = (Value) obj;
            return Objects.equals(value, other.value);
        }

    }
}
