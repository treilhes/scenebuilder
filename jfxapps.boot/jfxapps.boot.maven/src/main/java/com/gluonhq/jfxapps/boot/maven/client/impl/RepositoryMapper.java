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
package com.gluonhq.jfxapps.boot.maven.client.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig.Redirect;
import com.gluonhq.jfxapps.boot.maven.client.model.Repository;
import com.gluonhq.jfxapps.boot.mavenam.Dependency;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    List<com.gluonhq.jfxapps.boot.maven.client.api.Repository> map(List<Repository> repositories);
    List<Repository> mapApi(List<com.gluonhq.jfxapps.boot.maven.client.api.Repository> presetRepositories);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withType", source = "type")
    @Mapping(target = "withURL", source = "URL")
    @Mapping(target = "withUser", source = "login")
    @Mapping(target = "withPassword", source = "password")
    @Mapping(target = "withContentType", source = "contentType")
    com.gluonhq.jfxapps.boot.maven.client.api.Repository map(Repository repository);

    @Mapping(target = "login", source = "user")
    Repository map(com.gluonhq.jfxapps.boot.maven.client.api.Repository repository);

    @Mapping(target = "withArtifact.withArtifact.withGroupId", source = "groupId")
    @Mapping(target = "withArtifact.withArtifact.withArtifactId", source = "artifactId")
    @Mapping(target = "withPath", source = "path", qualifiedByName = "fileToPath")
    ResolvedArtifact map(Redirect redirect);

    @Mapping(target = "withArtifact.withGroupId", source = "groupId")
    @Mapping(target = "withArtifact.withArtifactId", source = "artifactId")
    @Mapping(target = "withVersion", source = "version")
    @Mapping(target = "withClassifier.withClassifier", source = "classifier")
    UniqueArtifact map(Dependency dependency);

    @Named("fileToPath")
    public static Path fileToPath(File file) {
        return file.toPath();
    }
}
