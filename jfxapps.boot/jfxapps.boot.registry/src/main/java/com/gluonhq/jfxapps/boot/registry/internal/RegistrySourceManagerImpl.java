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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifact;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifactManager;
import com.gluonhq.jfxapps.boot.api.registry.RegistryConfig;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistrySourceInfo;
import com.gluonhq.jfxapps.boot.registry.service.RegistryService;
import com.gluonhq.jfxapps.boot.registry.service.RegistrySourceService;

@Component
public class RegistrySourceManagerImpl implements RegistryArtifactManager {

    private final RegistryConfig config;
    private final RegistrySourceService sourceService;
    private final RegistryEntityMappers mappers;
    private final RegistryService registryService;

    public RegistrySourceManagerImpl(
            RegistryConfig config,
            RegistrySourceService sourceService,
            RegistryService registryService,
            RegistryEntityMappers mappers) {
        super();
        this.config = config;
        this.sourceService = sourceService;
        this.registryService = registryService;
        this.mappers = mappers;
    }

    @Override
    public void add(RegistryArtifact source) {
        var artifact = mappers.map(source);
        sourceService.save(artifact);
    }

    @Override
    public void update(RegistryArtifact source) {
        var artifact = mappers.map(source);
        sourceService.save(artifact);
    }

    @Override
    public void remove(RegistryArtifact source) {
        var artifact = mappers.map(source);
        sourceService.delete(artifact);
    }

    @Override
    public List<RegistryArtifact> list() {
        var artifacts = sourceService.findAll();
        return artifacts.stream().map(mappers::map).toList();
    }

    @Override
    public Set<RegistrySourceInfo> listRegistrySourceInfo() {
        var artifacts = list();
        var infos = new HashSet<RegistrySourceInfo>();

        for (var source: artifacts) {
            var info = new RegistrySourceInfo();
            info.setArtifact(source);

            var registryInfo = registryService.registryInfo(source.groupId(), source.artifactId());

            if (registryInfo != null) {
                info.setRegistryInfo(registryInfo);
            }

            infos.add(info);
        }

        return infos;
    }

    @Override
    public RegistrySourceInfo getRegistrySourceInfo(String groupId, String artifactId) {

        var entity = sourceService.find(groupId, artifactId);

        return entity.map(mappers::map).map(artifact -> {

            var info = new RegistrySourceInfo();
            info.setArtifact(artifact);

            var registryInfo = registryService.registryInfo(artifact.groupId(), artifact.artifactId());

            if (registryInfo != null) {
                info.setRegistryInfo(registryInfo);
            }

            return info;

        }).orElse(null);

    }

    @Override
    public RegistrySourceInfo loadLatestRegistrySourceInfo(String groupId, String artifactId) {
        var entity = sourceService.find(groupId, artifactId);

        var result = entity.map(mappers::map).map(artifact -> {

            var info = new RegistrySourceInfo();
            info.setArtifact(artifact);

            var registryInfo = registryService.updateRegistryInfo(artifact.groupId(), artifact.artifactId());

            if (registryInfo != null) {
                info.setRegistryInfo(registryInfo);
            }

            return info;

        }).orElse(null);

        return result;
    }

}
