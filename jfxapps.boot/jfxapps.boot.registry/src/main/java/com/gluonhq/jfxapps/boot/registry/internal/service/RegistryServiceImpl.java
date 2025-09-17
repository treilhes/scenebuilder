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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gluonhq.jfxapps.boot.api.maven.Repository;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryConfig;
import com.gluonhq.jfxapps.boot.registry.internal.mapper.RegistryModelMappers;
import com.gluonhq.jfxapps.boot.registry.internal.model.ApplicationEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.ExtensionEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.FeatureEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.LoadState;
import com.gluonhq.jfxapps.boot.registry.internal.model.PluginEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistrySourceEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.view.InstalledItem;
import com.gluonhq.jfxapps.boot.registry.internal.repository.ApplicationRepository;
import com.gluonhq.jfxapps.boot.registry.internal.repository.PluginRepository;
import com.gluonhq.jfxapps.boot.registry.internal.repository.RegistryRepository;
import com.gluonhq.jfxapps.boot.registry.internal.repository.RegistrySourceRepository;

import jakarta.validation.Valid;

@Service
@Transactional
public class RegistryServiceImpl implements RegistryService {

    private static final Logger logger = LoggerFactory.getLogger(RegistryServiceImpl.class);

    private final RegistryConfig config;
    private final RegistryRepository registryRepository;
    private final RegistrySourceRepository repository;
    private final ApplicationRepository applicationRepository;
    private final PluginRepository pluginRepository;
    private final RegistryUpdateService registryUpdateService;
    private final RepositoryManager repositoryManager;
    private final RegistryModelMappers mappers;

    // @formatter:off
    public RegistryServiceImpl(
            RegistryConfig config,
            RegistryRepository registryRepository,
            RegistrySourceRepository repository,
            ApplicationRepository applicationRepository,
            PluginRepository pluginRepository,
            RegistryUpdateService registryUpdateService,
            RepositoryManager repositoryManager,
            RegistryModelMappers mappers) {
        //
        this.config = config;
        this.registryRepository = registryRepository;
        this.repository = repository;
        this.applicationRepository = applicationRepository;
        this.pluginRepository = pluginRepository;
        this.registryUpdateService = registryUpdateService;
        this.repositoryManager = repositoryManager;
        this.mappers = mappers;
    }

    @Override
    public void saveSource(@Valid RegistrySourceEntity source) {
        repository.save(source);
    }

    @Override
    public void deleteSource(@Valid RegistrySourceEntity source) {
        repository.delete(source);
    }

    @Override
    public List<RegistrySourceEntity> findAllSources() {
        return repository.findAll();
    }

    @Override
    public Optional<RegistrySourceEntity> findSource(String groupId, String artifactId) {
        var id = new RegistrySourceEntity.RegistrySourceId();
        id.setGroupId(groupId);
        id.setArtifactId(artifactId);
        return repository.findById(id);
    }

    @Override
    public Optional<RegistryEntity> findRegistry(String groupId, String artifactId) {
        return registryRepository.findByGroupIdAndArtifactId(groupId, artifactId);
    }

    @Override
    public Optional<RegistryEntity> findRegistry(RegistrySourceEntity source) {
        return findRegistry(source.getGroupId(), source.getArtifactId());
    }

    @Override
    public void save(@Valid RegistryEntity registry) {
        registryRepository.save(registry);
    }

    @Override
    @Transactional
    public void updateRegistryFromSource(String groupId, String artifactId) {
        var entity = findSource(groupId, artifactId)
                .map(registryUpdateService::loadLatest)
                .orElse(null);

        if (entity != null) {

            var oldRegistry = registryRepository.findByGroupIdAndArtifactId(groupId, artifactId);

            entity = mergeOldAndNewRegistries(entity, oldRegistry);

            save(entity);

            if (entity.getLoadState() != LoadState.SUCCESS) {
                String messages = entity.getMessages().stream().collect(Collectors.joining(","));
                throw new RuntimeException("Registry update failed ! " + messages);
            }

            // Ensure the maven repositories required by the registry are added to the list of known repositories
            entity.getRepositories().forEach(r -> {
                Repository repository = Repository.builder()
                        .id(r.getId())
                        .url(r.getUrl())
                        .build();
                repositoryManager.add(repository);
            });

        } else {
            logger.warn("No registry source found for {}/{}", groupId, artifactId);
        }
    }

    @Override
    @Transactional
    public void updateRegistryFromSource(RegistrySourceEntity source) {
        updateRegistryFromSource(source.getGroupId(), source.getArtifactId());
    }


    /**
     * Merge the old registry with the new one.
     * We keep the internalId, the current version of the applications and plugins
     * We also keep the installed state of the applications and plugins
     * @param entity
     * @param oldRegistry
     * @return the merged registry
     */
    private static RegistryEntity mergeOldAndNewRegistries(RegistryEntity entity, Optional<RegistryEntity> oldRegistry) {

        oldRegistry.ifPresent(e -> {
            entity.setInternalId(e.getInternalId());

            var appVersionMap = e.getApplications().stream().collect(Collectors.toMap(ApplicationEntity::getId, a -> a));
            entity.getApplications().forEach(a -> Optional.ofNullable(appVersionMap.get(a.getId())).ifPresent(old -> {
                a.setVersion(old.getVersion());
                a.setInstalled(old.isInstalled());
            }));


            var pluginVersionMap = e.getPlugins().stream().collect(Collectors.toMap(PluginEntity::getId, p -> p));
            entity.getPlugins().forEach(p -> Optional.ofNullable(pluginVersionMap.get(p.getId())).ifPresent(old -> {
                p.setVersion(old.getVersion());
                p.setInstalled(old.isInstalled());
            }));
        });

        return entity;
    }

    @Override
    public Optional<ApplicationEntity> findApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId);
    }

    @Override
    public Optional<PluginEntity> pluginInfo(UUID pluginId) {
        return pluginRepository.findById(pluginId);
    }

    @Override
    public List<ApplicationEntity> listApplicationsInfo() {
        return applicationRepository.findAll();
    }

    @Override
    public Set<PluginEntity> listApplicationPluginsInfo(UUID applicationId) {
        return pluginRepository.findByTarget(applicationId);
    }

    @Override
    public void installPlugin(UUID pluginId) {
        pluginRepository.install(pluginId);
    }

    @Override
    public void uninstallPlugin(UUID pluginId) {
        pluginRepository.uninstall(pluginId);
    }

    @Override
    public void updatePluginToLatestVersion(UUID pluginId) {
        applicationRepository.update(pluginId);
    }

    @Override
    public void installApplication(UUID applicationId) {
        applicationRepository.install(applicationId);
    }

    @Override
    public void uninstallApplication(UUID applicationId) {
        applicationRepository.uninstall(applicationId);
    }

    @Override
    public void updateApplicationToLatestVersion(UUID applicationId) {
        applicationRepository.update(applicationId);
    }

    @Override
    public InstalledItem computeInstallTree(UUID applicationId) {
        var application = applicationRepository.findByInstalledTrueAndId(applicationId);
        var plugins = pluginRepository.findByInstalledTrueAndTarget(applicationId);
        return application.map(mappers::map).map(l -> this.populate(l, plugins)).orElse(null);
    }

    private InstalledItem populate(InstalledItem layer, Set<PluginEntity> plugins) {
        var uuidToExtensions = flatten(plugins);
        return recurse(layer, uuidToExtensions);
    }

    private Map<UUID, Set<ExtensionEntity>> flatten(Set<PluginEntity> plugins) {
        // Group by target UUID and accumulate extensions into a list
        var uuidToExtensions = plugins.stream().flatMap(p -> p.getFeatures().stream())
                .collect(Collectors.groupingBy(FeatureEntity::getTarget, // Group by the target UUID
                        Collectors.flatMapping( // Flatten the extensions into a single list
                                f -> f.getExtensions().stream(), Collectors.toSet())));

        return uuidToExtensions;
    }

    private InstalledItem recurse(InstalledItem layerDef, Map<UUID, Set<ExtensionEntity>> map) {
        var extensions = map.get(layerDef.getId());
        if (extensions != null) {
            extensions.forEach(e -> layerDef.getChildren().add(mappers.map(e)));
        }
        layerDef.getChildren().forEach(c -> recurse(c, map));
        return layerDef;
    }
}
