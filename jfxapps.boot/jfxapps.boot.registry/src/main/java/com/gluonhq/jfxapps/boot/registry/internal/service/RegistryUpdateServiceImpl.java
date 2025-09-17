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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gluonhq.jfxapps.boot.api.layer.InvalidLayerException;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient.VersionType;
import com.gluonhq.jfxapps.boot.api.maven.UniqueArtifact;
import com.gluonhq.jfxapps.boot.api.registry.RegistryConfig;
import com.gluonhq.jfxapps.boot.api.registry.RegistryException;
import com.gluonhq.jfxapps.boot.registry.internal.mapper.RegistryModelMappers;
import com.gluonhq.jfxapps.boot.registry.internal.model.LoadState;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistrySourceEntity;
import com.gluonhq.jfxapps.boot.registry.internal.util.BinaryCache;
import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

import jakarta.validation.Valid;

@Service
public class RegistryUpdateServiceImpl implements RegistryUpdateService {

    private final static Logger logger = LoggerFactory.getLogger(RegistryUpdateServiceImpl.class);

    /** The maven client. */
    private final RepositoryClient mavenClient;

    /** The module layer manager. */
    private final ModuleLayerManager moduleLayerManager;

    private final RegistryConfig config;

    private final RegistryModelMappers mappers;

    private final BinaryCache cache;


    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient        the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryUpdateServiceImpl(
            RepositoryClient mavenClient,
            ModuleLayerManager moduleLayerManager,
            RegistryConfig config,
            RegistryModelMappers mappers,
            BinaryCache cache) {
        super();
        this.config = config;
        this.mavenClient = mavenClient;
        this.moduleLayerManager = moduleLayerManager;
        this.mappers = mappers;
        this.cache = cache;
    }

    @Override
    public RegistryEntity loadLatest(@Valid RegistrySourceEntity src) {

        var artifact = Artifact.builder().groupId(src.getGroupId()).artifactId(src.getArtifactId()).build();

        logger.info("Loading artifact registry {}", artifact);

        var scope = config.isSnapshotsAllowed() ? VersionType.RELEASE_SNAPHOT : VersionType.RELEASE;

        final UniqueArtifact uniqueArtifact;
        if (src.getVersion() != null && !src.getVersion().isBlank()) {
            uniqueArtifact = UniqueArtifact.builder().artifact(artifact).version(src.getVersion()).build();
        } else {
            uniqueArtifact = mavenClient.getLatestVersion(artifact, scope).orElseThrow(
                    () -> new RegistryException(String.format("Artifact not found %s scope: %s", artifact, scope)));
        }

        var resolved = mavenClient.resolveWithDependencies(uniqueArtifact)
                .orElseThrow(() -> new RegistryException(String.format("Artifact not resolved %s", uniqueArtifact)));

        var layer = createLayer(resolved.toPaths());

        Registry registry = null;
        RegistryEntity registryEntity = null;

        try {
            registry = loadRegistryLayer(layer).orElseThrow(() -> new RegistryException(String.format("Layer not loaded %s", layer)));
            registryEntity = mappers.map(registry);
            registryEntity.setLoadState(LoadState.SUCCESS);
        } catch (Exception e) { // catch all exceptions
            logger.error("Loading registry from layer failed ({}) ! ", artifact, e);

            registry = new Registry();
            var dependency = new Dependency();
            dependency.setGroupId(src.getGroupId());
            dependency.setArtifactId(src.getArtifactId());
            dependency.setVersion(uniqueArtifact.getVersion());

            registry.setDependency(dependency);

            registryEntity = mappers.map(registry);
            registryEntity.addMessage(e.getMessage());
            registryEntity.setLoadState(LoadState.FAILURE);
        }

        final var finalRegistryEntity = registryEntity;
        registry.getRegistries().forEach(r -> {

            var coordinates = r.getDependency();
            var nestedSource = mappers.map(coordinates);
            var subRegistry = loadLatest(nestedSource);

            if (subRegistry.getApplications() != null) {
                subRegistry.getApplications().forEach(finalRegistryEntity::addApplication);
            }
            if (subRegistry.getPlugins() != null) {
                subRegistry.getPlugins().forEach(finalRegistryEntity::addPlugin);
            }
            if (subRegistry.getRepositories() != null) {
                subRegistry.getRepositories().forEach(finalRegistryEntity::addRepository);
            }
            if (subRegistry.getLoadState() == LoadState.FAILURE) {
                finalRegistryEntity.addMessage("Nested registry loading failed: " + subRegistry.getMessages());
                subRegistry.setLoadState(LoadState.PARTIAL);
            }
        });

        return registryEntity;
    }

    private void cacheBinaries(Registry registry, Layer layer) {
        cacheRegistryBinaries(registry, layer);
        cacheApplicationBinaries(registry, layer);
        cachePluginsBinaries(registry, layer);
    }

    private void cacheRegistryBinaries(Registry registry, Layer layer) {
        if (registry == null) {
            return;
        }
        if (registry.getDescription() == null) {
            return;
        }
        cacheResource(registry.getUuid(), "image", registry.getDescription().getImage(), layer);
        cacheI18nResource(registry.getUuid(), "i18n", registry.getDescription().getI18n(), layer);
    }

    private void cachePluginsBinaries(Registry registry, Layer layer) {
        for (var plugin:registry.getPlugins()) {
            cacheResource(plugin.getUuid(), "image", plugin.getDescription().getImage(), layer);
            cacheI18nResource(plugin.getUuid(), "i18n", plugin.getDescription().getI18n(), layer);
        }
    }

    private void cacheApplicationBinaries(Registry registry, Layer layer) {
        for (var application:registry.getApplications()) {
            cacheResource(application.getUuid(), "splash", application.getSplash(), layer);
            cacheResource(application.getUuid(), "image", application.getDescription().getImage(), layer);
            cacheI18nResource(application.getUuid(), "i18n", application.getDescription().getI18n(), layer);
        }
    }

    private void cacheI18nResource(UUID uuid, String key, List<String> i18n, Layer layer) {
        if (i18n == null) {
            return;
        }

        for (var resource:i18n) {
            var keySuffix = extractI18nSuffix(resource);
            cacheResource(uuid, key + keySuffix, resource, layer);
        }

    }

    private String extractI18nSuffix(String resource) {
        var keySuffix = "";
        var path = Path.of(resource);
        var filename = path.getFileName().toString();

        var dotIndex = filename.lastIndexOf('.') != -1 ? filename.lastIndexOf('.') : filename.length();

        if (filename.contains("_")) {
            keySuffix = filename.substring(filename.indexOf('_'), dotIndex);
        }

        return keySuffix;
    }

    private void cacheResource(UUID id, String key, String resource, Layer layer) {
        if (resource == null) {
            return;
        }

        try (var is = layer.getResourceAsStream(resource)) {
            layer.getResources(resource);
            cache.add(id, key, is);
        } catch (IOException e) {
            logger.error("Loading {} failed ! ", key, e);
        }
    }

    private Layer createLayer(List<Path> a) {
        try {
            return moduleLayerManager.create(a, null);
        } catch (IOException e) {
            logger.error("Layer creation failed ! ", e);
        } catch (InvalidLayerException e) {
            logger.error("Layer validation failed ! ", e);
        }
        return null;
    }

    private Optional<Registry> loadRegistryLayer(Layer layer) {
        Objects.requireNonNull(layer);

        try {
            Registry registry = null;

            for (String format:JfxApps.REGISTRY_FILE_FORMATS) {
                var is = layer.getResourceAsStream(JfxApps.registryResourcePath(format));

                if (is == null) {
                    continue;
                }

                registry = Mapper.get(format).from(is);

                if (registry != null) {
                    break;
                }
            }

            cacheBinaries(registry, layer);

            moduleLayerManager.remove(layer);

            return Optional.ofNullable(registry);
        } catch (IOException e) {
            logger.error("Loading registry failed !", e);
        }
        return Optional.empty();
    }
}
