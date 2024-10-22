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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.core.preferences.controller.DocumentPreferencesController;
import com.gluonhq.jfxapps.core.preferences.controller.PreferencesController;
import com.gluonhq.jfxapps.core.preferences.edit.PreferenceEditorFactoryImpl;
import com.gluonhq.jfxapps.core.preferences.i18n.I18NPreferences;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanDefinitionRegistryPostProcessor;
import com.gluonhq.jfxapps.core.preferences.internal.scan.PreferenceScanBeanDefinitionRegistryPostProcessor;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;

@EnableJpaRepositories(basePackageClasses = { PreferenceRepository.class })
public class PreferencesExtension implements OpenExtension {

    public final static UUID ID = UUID.fromString("d82c47c7-e3a7-483c-bb49-f1d3b086c2bf");

    @Override
    public UUID getParentId() {
        return ROOT_ID;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                I18NPreferences.class,
                DocumentPreferencesController.class,
                PreferencesController.class,
                PreferenceEditorFactoryImpl.class,
                PreferenceBeanDefinitionRegistryPostProcessor.class,
                PreferenceScanBeanDefinitionRegistryPostProcessor.class
            );
     // @formatter:on
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

}
