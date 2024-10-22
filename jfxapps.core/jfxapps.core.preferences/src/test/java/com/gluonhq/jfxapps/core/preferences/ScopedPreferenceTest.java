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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanDefinitionRegistryPostProcessor;
import com.gluonhq.jfxapps.core.preferences.internal.scan.PreferenceScanBeanDefinitionRegistryPostProcessor;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity.PreferenceEntityId;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;
import com.gluonhq.jfxapps.test.JfxAppsTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

@JfxAppsTest(properties = {
        "spring.jpa.show-sql=true",
})
@ContextConfiguration(classes = { //
        ScopedPreferenceTest.Config.class, //
        PreferenceBeanDefinitionRegistryPostProcessor.class,
        PreferenceScanBeanDefinitionRegistryPostProcessor.class,
        ScopedPreferenceTest.TestMapPreference.class,
        ScopedPreferenceTest.TestAppInstancePreference.class,
        ScopedPreferenceTest.TestAppPreference.class,
        ScopedPreferenceTest.TestGlobalPreference.class,
        ScopedPreferenceTest.TestListPreference.class
        }) //
public class ScopedPreferenceTest {

    private static final String DEFAULT_VALUE = "default";

    @SpringBootConfiguration
    @EnableJpaRepositories(basePackageClasses = { PreferenceRepository.class })
    @EntityScan(basePackageClasses = { PreferenceEntity.class })
    @DataJpaTest
    static class Config {

    }

    @Autowired
    PreferenceRepository preferenceRepository;

    @Test
    @DirtiesContext
    void global_pref_should_be_loaded_with_default_value(TestInfo testInfo, JfxAppContext context) throws Exception {
        var global = context.getBean(TestGlobalPreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(global.getValue()));
    }

    @Test
    @DirtiesContext
    void global_pref_should_save_and_reload_custom_value(TestInfo testInfo, JfxAppContext context) throws Exception {
        var global = context.getBean(TestGlobalPreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(global.getValue()));

        var notDefault = "Not Default";
        global.setValue(notDefault);
        global.save();

        var globalPref = preferenceRepository.findById(new PreferenceEntityId(global.getId(), "", ""));
        var value = globalPref.get().getJsonValue();
        assertTrue("should have not default value", !DEFAULT_VALUE.equals(value));

        global.load();
        assertTrue("should have not default value", !DEFAULT_VALUE.equals(global.getValue()));

    }

    @Test
    @DirtiesContext
    void global_pref_should_be_the_same_for_all_applications(TestInfo testInfo, JfxAppContext context)
            throws Exception {
        var global = context.getBean(TestGlobalPreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(global.getValue()));

        context.getApplicationExecutor().unbindScope();
        context.getBean(JfxAppsTest.Application2Bean.class);

        var global2 = context.getBean(TestGlobalPreference.class);

        assertEquals(global, global2);

    }

    @Test
    @DirtiesContext
    void application_pref_should_be_unique_per_applications(TestInfo testInfo, JfxAppContext context) throws Exception {

        final String APP1_VALUE = "app1";
        final String APP2_VALUE = "app2";

        var app = context.getBean(TestAppPreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(app.getValue()));

        context.getApplicationExecutor().unbindScope();
        context.getBean(JfxAppsTest.Application2Bean.class);

        var app2 = context.getBean(TestAppPreference.class);

        // ensure we have two different instance
        assertNotEquals(app, app2);

        app.setValue(APP1_VALUE);
        app2.setValue(APP2_VALUE);

        app.save();
        app2.save();

        var all = preferenceRepository.findAll();

        // ensure we have two entries in db with different values with different
        // applications
        assertTrue(all.size() == 2);
        assertTrue(all.stream().filter(pe -> JfxAppsTest.Application1Bean.class.getName().equals(pe.getApplication()))
                .count() == 1);
        assertTrue(all.stream().filter(pe -> JfxAppsTest.Application2Bean.class.getName().equals(pe.getApplication()))
                .count() == 1);
    }

    @Test
    @DirtiesContext
    void instance_pref_should_be_unique_per_instance_and_per_applications(TestInfo testInfo, JfxAppContext context) throws Exception {
        final URL INST1_LOC = new URI("file:///val1").toURL();
        final URL INST2_LOC = new URI("file:///val2").toURL();
        final URL INST3_LOC = new URI("file:///val3").toURL();
        final URL INST4_LOC = new URI("file:///val4").toURL();

        final String INST1_VALUE = "val1";
        final String INST2_VALUE = "val2";
        final String INST3_VALUE = "val3";
        final String INST4_VALUE = "val4";

        var instance1Events = context.getBean(ApplicationInstanceEvents.class);
        var prefInst1 = context.getBean(TestAppInstancePreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(prefInst1.getValue()));

        context.getApplicationInstanceExecutor().unbindScope();
        context.getBean(JfxAppsTest.Application1InstanceBean.class);

        var instance2Events = context.getBean(ApplicationInstanceEvents.class);
        var prefInst2 = context.getBean(TestAppInstancePreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(prefInst2.getValue()));

        context.getApplicationInstanceExecutor().unbindScope();
        context.getApplicationExecutor().unbindScope();
        context.getBean(JfxAppsTest.Application2Bean.class);
        context.getBean(JfxAppsTest.Application2InstanceBean.class);

        var instance3Events = context.getBean(ApplicationInstanceEvents.class);
        var prefInst3 = context.getBean(TestAppInstancePreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(prefInst3.getValue()));

        context.getApplicationInstanceExecutor().unbindScope();
        context.getBean(JfxAppsTest.Application2InstanceBean.class);

        var instance4Events = context.getBean(ApplicationInstanceEvents.class);
        var prefInst4 = context.getBean(TestAppInstancePreference.class);
        assertTrue("should have default value", DEFAULT_VALUE.equals(prefInst4.getValue()));

        // ensure all instances are different
        assertNotEquals(prefInst1, prefInst2);
        assertNotEquals(prefInst1, prefInst3);
        assertNotEquals(prefInst1, prefInst4);

        assertNotEquals(prefInst2, prefInst3);
        assertNotEquals(prefInst2, prefInst4);

        assertNotEquals(prefInst3, prefInst4);

        prefInst1.setValue(INST1_VALUE);
        prefInst2.setValue(INST2_VALUE);
        prefInst3.setValue(INST3_VALUE);
        prefInst4.setValue(INST4_VALUE);

        prefInst1.save();
        prefInst2.save();
        prefInst3.save();
        prefInst4.save();

        // here no fxomDocuments for any instance, so nothing should be saved in db
        var all = preferenceRepository.findAll();
        assertTrue(all.size() == 0);

        instance1Events.fxomDocument().set(new FXOMDocument(""));
        instance2Events.fxomDocument().set(new FXOMDocument(""));
        instance3Events.fxomDocument().set(new FXOMDocument(""));
        instance4Events.fxomDocument().set(new FXOMDocument(""));

        // here we have fxomDocuments for all instance, but no locations set so nothing should be saved in db
        all = preferenceRepository.findAll();
        assertTrue(all.size() == 0);

        instance1Events.fxomDocument().get().setLocation(INST1_LOC);
        instance2Events.fxomDocument().get().setLocation(INST2_LOC);
        instance3Events.fxomDocument().get().setLocation(INST3_LOC);
        instance4Events.fxomDocument().get().setLocation(INST4_LOC);

        prefInst1.save();
        prefInst2.save();
        prefInst3.save();
        prefInst4.save();

        // here values should be saved in db
        all = preferenceRepository.findAll();

        // ensure we have 4 entries in db with different values with different
        // applications/llocation
        assertTrue(all.size() == 4);
        assertTrue(all.stream()
                .filter(pe -> JfxAppsTest.Application1Bean.class.getName().equals(pe.getApplication()))
                .filter(pe -> INST1_LOC.toString().equals(pe.getInstance()))
                .count() == 1);
        assertTrue(all.stream()
                .filter(pe -> JfxAppsTest.Application1Bean.class.getName().equals(pe.getApplication()))
                .filter(pe -> INST2_LOC.toString().equals(pe.getInstance()))
                .count() == 1);
        assertTrue(all.stream()
                .filter(pe -> JfxAppsTest.Application2Bean.class.getName().equals(pe.getApplication()))
                .filter(pe -> INST3_LOC.toString().equals(pe.getInstance()))
                .count() == 1);
        assertTrue(all.stream()
                .filter(pe -> JfxAppsTest.Application2Bean.class.getName().equals(pe.getApplication()))
                .filter(pe -> INST4_LOC.toString().equals(pe.getInstance()))
                .count() == 1);
    }

    @Test
    @DirtiesContext
    void list_pref_should_be_the_saved_loaded_and_keep_the_right_type(TestInfo testInfo, JfxAppContext context)
            throws Exception {

        final Value value = new Value("value1");
        var pref = context.getBean(TestListPreference.class);
        pref.getValue().add(value);
        pref.save();

        var all = preferenceRepository.findAll();
        assertTrue(all.size() == 1);

        pref.setValue(null); // ensure it is null before loading
        pref.load();

        assertTrue(pref.getValue() instanceof ObservableList);
        assertTrue(pref.getValue().size() == 1);
        assertTrue(pref.getValue().get(0).equals(value));
    }

    @Test
    @DirtiesContext
    void map_pref_should_be_the_saved_loaded_and_keep_the_right_type(TestInfo testInfo, JfxAppContext context)
            throws Exception {

        final Value key = new Value("key");
        final Value value = new Value("value");

        final Value key2 = new Value("key2");
        final Value value2 = new Value("value2");

        var pref = context.getBean(TestMapPreference.class);
        pref.getValue().put(key, value);
        pref.getValue().put(key2, value2);
        pref.save();

        var all = preferenceRepository.findAll();
        assertTrue(all.size() == 1);

        pref.setValue(null); // ensure it is null before loading
        pref.load();

        assertTrue("must be an ObservableMap", pref.getValue() instanceof ObservableMap);
        assertTrue("2 preference must exist in database", pref.getValue().size() == 2);
        assertTrue("preference key must exist", pref.getValue().get(key) != null);
        assertTrue("preference value must be the value set", pref.getValue().get(key).equals(value));
        assertTrue("preference key must exist", pref.getValue().get(key2) != null);
        assertTrue("preference value must be the value set", pref.getValue().get(key2).equals(value2));
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf0", name = "some.name.global", defaultValueProvider = TestDefaultValueProvider.class, validator = TestValueValidator.class)
    public static interface TestGlobalPreference extends Preference<String> {
    }

    @ApplicationSingleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf1", name = "some.name.app", defaultValueProvider = TestDefaultValueProvider.class, validator = TestValueValidator.class)
    public static interface TestAppPreference extends Preference<String> {
    }

    @ApplicationInstanceSingleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf2", name = "some.name.app.inst", defaultValueProvider = TestDefaultValueProvider.class, validator = TestValueValidator.class)
    public static interface TestAppInstancePreference extends Preference<String> {
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf3", name = "some.name.global", defaultValueProvider = TestListPreference.ListValueProvider.class)
    public static interface TestListPreference extends Preference<ObservableList<Value>> {
        public static class ListValueProvider implements DefaultValueProvider<ObservableList<Value>> {
            @Override
            public ObservableList<Value> get() {
                return FXCollections.observableArrayList();
            }
        }
    }

    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf4", name = "some.name.global", defaultValueProvider = TestMapPreference.MapValueProvider.class)
    public static interface TestMapPreference extends Preference<ObservableMap<Value, Value>> {
        public static class MapValueProvider implements DefaultValueProvider<ObservableMap<Value, Value>> {
            @Override
            public ObservableMap<Value, Value> get() {
                return FXCollections.observableHashMap();
            }
        }

    }

    public static class TestDefaultValueProvider implements DefaultValueProvider<String> {
        @Override
        public String get() {
            return "default";
        }
    }

    public static class TestValueValidator implements ValueValidator<String> {
        @Override
        public boolean test(String t) {
            return true;
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
