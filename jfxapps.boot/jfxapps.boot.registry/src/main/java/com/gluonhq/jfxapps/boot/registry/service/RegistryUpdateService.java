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
package com.gluonhq.jfxapps.boot.registry.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gluonhq.jfxapps.boot.api.layer.InvalidLayerException;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient.VersionType;
import com.gluonhq.jfxapps.boot.registry.RegistryException;
import com.gluonhq.jfxapps.boot.registry.config.RegistryConfig;
import com.gluonhq.jfxapps.boot.registry.internal.RegistryEntityMappers;
import com.gluonhq.jfxapps.boot.registry.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.model.RegistrySourceEntity;
import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

import jakarta.validation.Valid;

@Service
public class RegistryUpdateService {

    private final static Logger logger = LoggerFactory.getLogger(RegistryUpdateService.class);

    /** The maven client. */
    private final RepositoryClient mavenClient;

    /** The module layer manager. */
    private final ModuleLayerManager moduleLayerManager;

    private final RegistryConfig config;

	private final RegistryEntityMappers mappers;


    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient        the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryUpdateService(
    		RepositoryClient mavenClient,
    		ModuleLayerManager moduleLayerManager,
            RegistryConfig config,
            RegistryEntityMappers mappers) {
        super();
        this.config = config;
        this.mavenClient = mavenClient;
        this.moduleLayerManager = moduleLayerManager;
        this.mappers = mappers;
    }

    public RegistryEntity loadLatest(@Valid RegistrySourceEntity src) {

        var artifact = Artifact.builder().groupId(src.getGroupId()).artifactId(src.getArtifactId()).build();

        logger.info("Loading artifact registry {}", artifact);

        var scope = config.isSnapshotsAllowed() ? VersionType.RELEASE_SNAPHOT : VersionType.RELEASE;

        var latest = mavenClient.getLatestVersion(artifact, scope).orElseThrow(
                () -> new RegistryException(String.format("Artifact not found %s scope: %s", artifact, scope)));

        var resolved = mavenClient.resolveWithDependencies(latest)
                .orElseThrow(() -> new RegistryException(String.format("Artifact not resolved %s", latest)));

        var layer = createLayer(resolved.toPaths());

        var registry = loadRegistryLayer(layer).orElseThrow(() -> new RegistryException(String.format("Layer not loaded %s", layer)));
        var registryEntity = mappers.map(registry);

        registry.getRegistries().forEach(r -> {

            var coordinates = r.getDependency();
            var nestedSource = mappers.map(coordinates);
            var subRegistry = loadLatest(nestedSource);

            if (subRegistry.getApplications() != null) {
				subRegistry.getApplications().forEach(registryEntity::addApplication);
            }
            if (subRegistry.getPlugins() != null) {
				subRegistry.getPlugins().forEach(registryEntity::addPlugin);
            }
        });

        return registryEntity;
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
                InputStream is = layer.getResourceAsStream(JfxApps.registryResourcePath(format));

                if (is == null) {
                    continue;
                }

                registry = Mapper.get(format).from(is);

                if (registry != null) {
                    break;
                }
            }

            moduleLayerManager.remove(layer);

            return Optional.ofNullable(registry);
        } catch (IOException e) {
            logger.error("Loading registry failed !", e);
        }
        return Optional.empty();
    }
}
