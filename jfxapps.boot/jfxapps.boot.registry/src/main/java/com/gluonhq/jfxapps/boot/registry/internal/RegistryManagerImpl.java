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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifactManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryUpdateListener;
import com.gluonhq.jfxapps.boot.api.registry.model.ApplicationInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.LayerDefinition;
import com.gluonhq.jfxapps.boot.api.registry.model.PluginInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistryArtifact;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistryInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistrySourceInfo;
import com.gluonhq.jfxapps.boot.registry.internal.mapper.RegistryDtoMappers;
import com.gluonhq.jfxapps.boot.registry.internal.mapper.RegistryModelMappers;
import com.gluonhq.jfxapps.boot.registry.internal.model.ApplicationEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.internal.service.RegistryService;
import com.gluonhq.jfxapps.boot.registry.internal.util.BinaryCache;

import jakarta.annotation.PostConstruct;

/**
 * The Class RegistryManagerImpl.
 */
@Component
@Lazy
public class RegistryManagerImpl implements RegistryManager, RegistryArtifactManager {

    private final static Logger logger = LoggerFactory.getLogger(RegistryManagerImpl.class);

    private final RegistryService registryGlobalService;
    private final RegistryDtoMappers infoMappers;
    private final RegistryModelMappers mappers;
    private final BinaryCache binaryCache;

    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient        the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryManagerImpl(
            RegistryService registryGlobalService,
            RegistryDtoMappers infoMappers,
            RegistryModelMappers mappers,
            BinaryCache binaryCache
    		) {
        super();
        this.registryGlobalService = registryGlobalService;
        this.infoMappers = infoMappers;
        this.mappers = mappers;
        this.binaryCache = binaryCache;
    }


    @PostConstruct
    @Transactional
    // FIXME: this method only handles initialization but do not handle new installations
    protected void init() {
        registryGlobalService.initializeFromConfig();
    }

    @Override
    public ApplicationInfo applicationInfo(UUID applicationId) {
        return registryGlobalService.findApplication(applicationId)
                .map(this::mapAndfillAppBinariesFromCache)
                .orElse(null);
    }

    @Override
    public Set<ApplicationInfo> listApplicationsInfo() {
        return registryGlobalService.listApplicationsInfo().stream()
                .map(this::mapAndfillAppBinariesFromCache)
                .collect(Collectors.toSet());
    }

    @Override
    public PluginInfo pluginInfo(UUID pluginId) {
        return registryGlobalService.pluginInfo(pluginId)
                .map(infoMappers::map)
                .orElse(null);
    }

    @Override
    public Set<PluginInfo> listApplicationPluginsInfo(UUID applicationId) {
        return registryGlobalService.listApplicationPluginsInfo(applicationId).stream()
                .map(infoMappers::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PluginInfo> listApplicationPluginsInfo(ApplicationInfo application) {
        return registryGlobalService.listApplicationPluginsInfo(application.getUuid()).stream()
                .map(infoMappers::map)
                .collect(Collectors.toSet());
    }

    @Override
    public LayerDefinition computeLayerDefinition(UUID applicationId) {
        var installed = registryGlobalService.computeInstallTree(applicationId);
        var layerDef = infoMappers.map(installed);
        return layerDef;
    }


    @Override
    public void install(PluginInfo pluginInfo) {
        registryGlobalService.installPlugin(pluginInfo.getUuid());
    }

    @Override
    public void uninstall(PluginInfo pluginInfo) {
        registryGlobalService.uninstallPlugin(pluginInfo.getUuid());
    }

    @Override
    public void update(PluginInfo pluginInfo) {
        registryGlobalService.updatePluginToLatestVersion(pluginInfo.getUuid());
    }

    @Override
    public void install(ApplicationInfo applicationInfo) {
        registryGlobalService.installApplication(applicationInfo.getUuid());
    }

    @Override
    public void uninstall(ApplicationInfo applicationInfo) {
        registryGlobalService.uninstallApplication(applicationInfo.getUuid());
    }


    @Override
    public void update(ApplicationInfo applicationInfo) {
        registryGlobalService.updateApplicationToLatestVersion(applicationInfo.getUuid());
    }


    @Override
    public void add(RegistryArtifact source) {
        var artifact = mappers.map(source);
        //sourceService.save(artifact);
        registryGlobalService.saveSource(artifact);
    }

    @Override
    public void update(RegistryArtifact source) {
        var artifact = mappers.map(source);
        //sourceService.save(artifact);
        registryGlobalService.saveSource(artifact);
    }

    @Override
    public void remove(RegistryArtifact source) {
        var artifact = mappers.map(source);
        //sourceService.delete(artifact);
        registryGlobalService.deleteSource(artifact);
    }

    @Override
    public List<RegistryArtifact> list() {
        //var artifacts = sourceService.findAll();
        var artifacts = registryGlobalService.findAllSources();
        return artifacts.stream().map(mappers::map).toList();
    }

    @Override
    public Set<RegistrySourceInfo> listRegistrySourceInfo() {

        var sources = registryGlobalService.findAllSources();

        return sources.stream()
                .map(mappers::map)
                .map(this::getRegistrySourceInfo)
                .collect(Collectors.toSet());

    }

    @Override
    public RegistrySourceInfo getRegistrySourceInfo(String groupId, String artifactId) {
        var source = registryGlobalService.findSource(groupId, artifactId);
        var registryArtifact = source.map(mappers::map);
        return registryArtifact.map(this::getRegistrySourceInfo).orElse(null);
    }

    @Override
    public RegistrySourceInfo getRegistrySourceInfo(RegistryArtifact registryArtifact) {

        var registry = registryGlobalService.findRegistry(registryArtifact.groupId(), registryArtifact.artifactId());
        var registryInfo = registry.map(infoMappers::toRegistryInfo);

        registryInfo.ifPresent(r -> {
            r.setImage(binaryCache.get(registry.get().getId(), "image"));
            r.setI18n(binaryCache.get(registry.get().getId(), "i18n"));
        });

        var info = new RegistrySourceInfo();
        info.setArtifact(registryArtifact);
        info.setRegistryInfo(registryInfo.orElse(null));

        return info;

    }

    @Override
    public RegistrySourceInfo loadLatestRegistrySourceInfo(String groupId, String artifactId) {
        registryGlobalService.updateRegistryFromSource(groupId, artifactId);
        return getRegistrySourceInfo(groupId, artifactId);

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

    private ApplicationInfo mapAndfillAppBinariesFromCache(ApplicationEntity a) {
        var info = infoMappers.map(a);
        info.setImage(binaryCache.get(a.getId(), "image"));
        info.setI18n(binaryCache.get(a.getId(), "i18n"));
        info.setSplash(binaryCache.get(a.getId(), "splash"));
        return info;
    }

    private RegistryInfo mapAndfillRegistryBinariesFromCache(RegistryEntity registryEntity) {
        var info = infoMappers.toRegistryInfo(registryEntity);
        info.setImage(binaryCache.get(registryEntity.getId(), "image"));
        info.setI18n(binaryCache.get(registryEntity.getId(), "i18n"));
        return info;
    }
}
