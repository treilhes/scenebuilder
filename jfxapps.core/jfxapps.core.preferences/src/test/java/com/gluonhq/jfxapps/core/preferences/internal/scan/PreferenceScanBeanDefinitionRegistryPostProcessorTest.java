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
package com.gluonhq.jfxapps.core.preferences.internal.scan;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.PreferenceScan;
import com.gluonhq.jfxapps.core.preferences.ScopedPreferenceTest.TestDefaultValueProvider;
import com.gluonhq.jfxapps.core.preferences.ScopedPreferenceTest.TestValueValidator;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;
import com.gluonhq.jfxapps.test.JfxAppsTest;

@JfxAppsTest
@ContextConfiguration(classes = { //
        PreferenceScanBeanDefinitionRegistryPostProcessor.class, //
        PreferenceScanBeanDefinitionRegistryPostProcessorTest.Config.class, //
        PreferenceScanBeanDefinitionRegistryPostProcessorTest.TestPreference.class, //
        ExternalTestPreference.class //
        }) //

class PreferenceScanBeanDefinitionRegistryPostProcessorTest {

    @SpringBootConfiguration
    @EnableJpaRepositories(basePackageClasses = { PreferenceRepository.class })
    @EntityScan(basePackageClasses = { PreferenceEntity.class })
    @DataJpaTest
    @PreferenceScan
    static class Config {

    }

    @Test
    void must_load_preference_found_with_preferencescan_annotation(JfxAppContext context) {
        var local = context.getBean(TestPreference.class);
        var external = context.getBean(ExternalTestPreference.class);
        assertNotNull(local);
        assertNotNull(external);
    }
    @Singleton
    @PreferenceContext(id = "96f69947-d70f-4f95-9b1b-317aa32bfdf0", name = "some.name.global", defaultValueProvider = TestDefaultValueProvider.class, validator = TestValueValidator.class)
    public static interface TestPreference extends Preference<String> {
    }
}