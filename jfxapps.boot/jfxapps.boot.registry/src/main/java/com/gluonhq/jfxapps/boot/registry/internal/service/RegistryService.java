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
package com.gluonhq.jfxapps.boot.registry.internal.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.registry.internal.model.ApplicationEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.PluginEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistrySourceEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.view.InstalledItem;

import jakarta.validation.Valid;

public interface RegistryService {

    void saveSource(RegistrySourceEntity artifact);

    void deleteSource(@Valid RegistrySourceEntity source);

    List<RegistrySourceEntity> findAllSources();

    Optional<RegistrySourceEntity> findSource(String groupId, String artifactId);

    Optional<RegistryEntity> findRegistry(String groupId, String artifactId);

    Optional<RegistryEntity> findRegistry(RegistrySourceEntity source);

    void save(@Valid RegistryEntity registry);

    void updateRegistryFromSource(String groupId, String artifactId);

    void updateRegistryFromSource(@Valid RegistrySourceEntity source);

    Optional<ApplicationEntity> findApplication(UUID applicationId);

    Optional<PluginEntity> pluginInfo(UUID pluginId);

    List<ApplicationEntity> listApplicationsInfo();

    Set<PluginEntity> listApplicationPluginsInfo(UUID applicationId);

    void installPlugin(UUID pluginId);

    void uninstallPlugin(UUID pluginId);

    void updatePluginToLatestVersion(UUID pluginId);

    void installApplication(UUID applicationId);

    void uninstallApplication(UUID applicationId);

    void updateApplicationToLatestVersion(UUID applicationId);

    InstalledItem computeInstallTree(UUID applicationId);

    void initializeFromConfig();



}
