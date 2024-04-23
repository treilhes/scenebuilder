/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.layer.InvalidLayerException;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient.Scope;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.boot.registry.RegistryArtifact;
import com.gluonhq.jfxapps.boot.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.registry.RegistryException;
import com.gluonhq.jfxapps.boot.registry.RegistrySourceManager;
import com.gluonhq.jfxapps.boot.registry.config.RegistryConfig;
import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

import jakarta.annotation.PostConstruct;

/**
 * The Class RegistryManagerImpl.
 */
@Component
public class RegistryManagerImpl implements RegistryManager {

    private final static Logger logger = LoggerFactory.getLogger(RegistryManagerImpl.class);

    /** The maven client. */
    private final RepositoryClient mavenClient;

    /** The module layer manager. */
    private final ModuleLayerManager moduleLayerManager;

    private final RegistrySourceManager source;

    private final RegistryConfig config;

    private final Map<UUID, Registry> registries;

    private Registry bootRegistry;
    private List<Registry> mandatoryRegistries = new ArrayList<>();
    private List<Registry> guestRegistries = new ArrayList<>();

    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient        the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryManagerImpl(RepositoryClient mavenClient, ModuleLayerManager moduleLayerManager,
            RegistrySourceManager source, RegistryConfig config) {
        super();
        this.config = config;
        this.mavenClient = mavenClient;
        this.moduleLayerManager = moduleLayerManager;
        this.source = source;
        this.registries = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        bootRegistry = loadArtifact(config.getBoot());
        source.list().forEach(this::loadAndDispatch);
    }

    private void loadAndDispatch(RegistryArtifact src) {
        var a = loadArtifact(src);
        if (src.mandatory()) {
            mandatoryRegistries.add(a);
        } else {
            guestRegistries.add(a);
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

    private Registry loadArtifact(RegistryArtifact src) {
        var artifact = Artifact.builder().groupId(src.groupId()).artifactId(src.artifactId()).build();

        logger.info("Loading artifact registry {}", artifact);

        var scope = config.isSnapshotsAllowed() ? Scope.RELEASE_SNAPHOT : Scope.RELEASE;

        var latest = mavenClient.getLatestVersion(artifact, scope).orElseThrow(
                () -> new RegistryException(String.format("Artifact not found %s scope: %s", artifact, scope)));

        var resolved = mavenClient.resolveWithDependencies(latest)
                .orElseThrow(() -> new RegistryException(String.format("Artifact not resolved %s", latest)));

        var layer = createLayer(resolved.toPaths());

        var registry = loadRegistryLayer(layer).orElseThrow(() -> new RegistryException(String.format("Layer not loaded %s", layer)));

        return registry;
    }

    @Override
    public Registry bootRegistry() {
        return bootRegistry;
    }

    @Override
    public Registry installedRegistry() {
        Registry result = new Registry();

        mandatoryRegistries.stream()
            .forEach(r -> result.getApplications().addAll(r.getApplications()));

        return result;
    }

    @Override
    public Registry installableRegistry() {
        Registry result = new Registry();

        mandatoryRegistries.stream()
            .forEach(r -> result.getExtensions().addAll(r.getExtensions()));

        guestRegistries.stream()
            .peek(r -> result.getApplications().addAll(r.getApplications()))
            .forEach(r -> result.getExtensions().addAll(r.getExtensions()));

        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadState() throws IOException {
        Set<UniqueArtifact> artifactsToLoad = null;//store.load();
        artifactsToLoad.stream().map(a -> load(a)).filter(Objects::nonNull).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Registry load(UniqueArtifact artifact) {
        Objects.requireNonNull(artifact);

//        Registry registry = mavenClient
//            .resolve(artifact, Classifier.DEFAULT)
//            .filter(Files::exists)
//            .map(this::loadRegistryFromTempLayer)
//            .get().orElse(null);

        Registry registry = null;

        if (registry != null) {
            registries.put(registry.getUuid(), registry);
        }
        return registry;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Registry registry) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Registry get(UUID registryId) {
        return registries.get(registryId);
    }

    @Override
    public Collection<Registry> list() {
        return Collections.unmodifiableCollection(registries.values());
    }

    private void saveState() {
        //store.
    }
}
