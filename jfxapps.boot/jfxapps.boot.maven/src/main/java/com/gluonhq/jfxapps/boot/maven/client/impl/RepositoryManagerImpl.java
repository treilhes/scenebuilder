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
package com.gluonhq.jfxapps.boot.maven.client.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.maven.MavenConfig;
import com.gluonhq.jfxapps.boot.api.maven.Repository;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.boot.maven.client.model.Repository.Content;
import com.gluonhq.jfxapps.boot.maven.client.preset.MavenPresets;
import com.gluonhq.jfxapps.boot.maven.client.prompt.CredentialPrompt;
import com.gluonhq.jfxapps.boot.maven.client.repository.RepositoryRepository;
import com.gluonhq.jfxapps.boot.maven.client.type.Maven;

import jakarta.annotation.PostConstruct;

@Component
@Lazy
public class RepositoryManagerImpl implements RepositoryManager {

    private final MavenConfig config;
    private final RepositoryRepository jpaRepository;
    private final RepositoryMapper mapper;
    private final Optional<ApplicationStartup> startup;

    // @formatter:off
    public RepositoryManagerImpl(
            MavenConfig config,
            RepositoryRepository jpaRepository,
            RepositoryMapper mapper,
            Optional<ApplicationStartup> startup) {
        // @formatter:on
        super();
        this.config = config;
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.startup = startup;
    }

    @PostConstruct
    protected void init() {
        var step = startup.map(s -> s.start("repository.manager.init"));

        var dbRepositories = mapper.map(jpaRepository.findAll());
        if (dbRepositories.size() == 0) { // Initialize with default repositories

            var repositories = new ArrayList<com.gluonhq.jfxapps.boot.maven.client.model.Repository>();
            var presets = mapper.mapApi(MavenPresets.getPresetRepositories());
            repositories.addAll(presets);

            var configRepositories = config.getRepository();
            if (configRepositories != null) {

                for (var configRepository : configRepositories) {
                    if (configRepository.requestCredentials()) {
                        // If requestCredentials is true, prompt the user for credentials
                        try {
                            var url = new URI(configRepository.url()).toURL();
                            var credentials = CredentialPrompt.requestCredentialsFor(url);
                            if (credentials != null) {

                                var authenticatedRepository = new com.gluonhq.jfxapps.boot.maven.client.model.Repository();

                                authenticatedRepository.setId(url.getHost());
                                authenticatedRepository.setType(Maven.class);
                                authenticatedRepository.setUrl(configRepository.url());
                                authenticatedRepository.setLogin(credentials.getUsername());
                                authenticatedRepository.setPassword(credentials.getPassword());
                                authenticatedRepository.setContentType(Content.SNAPSHOT_RELEASE);

                                repositories.add(authenticatedRepository);
                            }

                        } catch (MalformedURLException | URISyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            jpaRepository.saveAll(repositories);
        }
        step.ifPresent(StartupStep::end);
    }

    @Override
    public List<Repository> repositories() {
        return mapper.map(jpaRepository.findAll());
    }

    @Override
    public void add(Repository repository) {
        jpaRepository.save(mapper.map(repository));
    }

    @Override
    public void remove(Repository repository) {
        jpaRepository.deleteById(repository.getId());
    }

    @Override
    public Optional<Repository> get(String id) {
        var item = jpaRepository.findById(id);
        return item.map(mapper::map);
    }


}
