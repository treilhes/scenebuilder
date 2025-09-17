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
package com.gluonhq.jfxapps.boot.registry.internal.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gluonhq.jfxapps.boot.api.registry.model.RegistryArtifact;
import com.gluonhq.jfxapps.boot.registry.internal.model.ApplicationEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.ExtensionEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.FeatureEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.PluginEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.RegistrySourceEntity;
import com.gluonhq.jfxapps.boot.registry.internal.model.view.InstalledItem;
import com.gluonhq.jfxapps.registry.model.Application;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.Extension;
import com.gluonhq.jfxapps.registry.model.Feature;
import com.gluonhq.jfxapps.registry.model.Plugin;
import com.gluonhq.jfxapps.registry.model.Registry;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface RegistryModelMappers {

    RegistrySourceEntity map(RegistryArtifact source);
    RegistrySourceEntity map(Dependency source);

    RegistryArtifact map(RegistrySourceEntity source);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "groupId", source = "source.dependency.groupId")
    @Mapping(target = "artifactId", source = "source.dependency.artifactId")
    @Mapping(target = "version", source = "source.dependency.version")
    @Mapping(target = "description.title", source = "source.description.title")
    @Mapping(target = "description.text", source = "source.description.text")
    RegistryEntity map(Registry source);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "groupId", source = "source.dependency.groupId")
    @Mapping(target = "artifactId", source = "source.dependency.artifactId")
    @Mapping(target = "version", source = "source.dependency.version")
    @Mapping(target = "nextVersion", source = "source.dependency.version")
    @Mapping(target = "description.title", source = "source.description.title")
    @Mapping(target = "description.text", source = "source.description.text")
    ApplicationEntity map(Application source);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "groupId", source = "source.dependency.groupId")
    @Mapping(target = "artifactId", source = "source.dependency.artifactId")
    @Mapping(target = "version", source = "source.dependency.version")
    ExtensionEntity map(Extension source);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "description.title", source = "source.description.title")
    @Mapping(target = "description.text", source = "source.description.text")
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "nextVersion", source = "source.version")
    PluginEntity map(Plugin source);

    @Mapping(target = "id", source = "uuid")
    FeatureEntity map(Feature source);

    @Mapping(target = "children", source = "extensions")
    InstalledItem map(ApplicationEntity source);
    @Mapping(target = "children", source = "extensions")
    InstalledItem map(ExtensionEntity source);
}
