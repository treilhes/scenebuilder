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
package com.gluonhq.jfxapps.boot.registry.internal;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryUpdateListener;
import com.gluonhq.jfxapps.boot.api.registry.model.ApplicationInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.LayerDefinition;
import com.gluonhq.jfxapps.boot.api.registry.model.PluginInfo;
import com.gluonhq.jfxapps.boot.registry.service.RegistryService;
import com.gluonhq.jfxapps.boot.registry.service.RegistrySourceService;
import com.gluonhq.jfxapps.boot.registry.service.RegistryUpdateService;

/**
 * The Class RegistryManagerImpl.
 */
@Component
@Lazy
public class RegistryManagerImpl implements RegistryManager {

    private final static Logger logger = LoggerFactory.getLogger(RegistryManagerImpl.class);

    private final RegistryService registryService;
    private final RegistryUpdateService updateService;
    private final RegistrySourceService registrySourceService;

    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient        the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryManagerImpl(
            RegistryService registryService,
            RegistrySourceService registrySourceService,
    		@Lazy RegistryUpdateService updateService
    		) {
        super();
        this.registryService = registryService;
        this.registrySourceService = registrySourceService;
        this.updateService = updateService;
    }

    @Override
    public ApplicationInfo applicationInfo(UUID applicationId) {
        return registryService.applicationInfo(applicationId);
    }

    @Override
    public Set<ApplicationInfo> listApplicationsInfo() {
        return registryService.listApplicationsInfo();
    }

    @Override
    public PluginInfo pluginInfo(UUID pluginId) {
        return registryService.pluginInfo(pluginId);
    }

    @Override
    public Set<PluginInfo> listApplicationPluginsInfo(UUID applicationId) {
        return registryService.listApplicationPluginsInfo(applicationId);
    }

    @Override
    public Set<PluginInfo> listApplicationPluginsInfo(ApplicationInfo application) {
        return registryService.listApplicationPluginsInfo(application.getUuid());
    }

    @Override
    public LayerDefinition computeLayerDefinition(UUID applicationId) {
        return registryService.computeLayerDefinition(applicationId);
    }

    @Override
    public void searchForUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addUpdateListener(RegistryUpdateListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUpdateListener(RegistryUpdateListener listener) {
        // TODO Auto-generated method stub

    }



}
