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

import java.util.List;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.registry.RegistryArtifact;
import com.gluonhq.jfxapps.boot.registry.RegistrySourceManager;
import com.gluonhq.jfxapps.boot.registry.config.RegistryConfig;
import com.gluonhq.jfxapps.boot.registry.repository.RegistrySourceRepository;

import jakarta.annotation.PostConstruct;

@Component
public class RegistrySourceManagerImpl implements RegistrySourceManager {

    private final RegistryConfig config;
    private final RegistrySourceRepository repository;
    private final RegistryMappers mappers;

    public RegistrySourceManagerImpl(RegistryConfig config, RegistrySourceRepository repository, RegistryMappers mappers) {
        super();
        this.config = config;
        this.repository = repository;
        this.mappers = mappers;
    }

    @PostConstruct
    protected void init() {
        // init if empty
        if (repository.count() == 0) {
            config.getDefaults().values().forEach(v -> repository.save(mappers.map(v)));
        }
    }

    @Override
    public void add(RegistryArtifact source) {
        repository.save(mappers.map(source));
    }

    @Override
    public void remove(RegistryArtifact source) {
        repository.delete(mappers.map(source));
    }

    @Override
    public List<RegistryArtifact> list() {
        return mappers.map(repository.findAll());
    }

}
