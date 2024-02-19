/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.loader.internal;

import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gluonhq.jfxapps.boot.loader.content.MavenExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.internal.model.Extension;
import com.gluonhq.jfxapps.boot.loader.internal.model.State;
import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.ApplicationExtension;
import com.gluonhq.jfxapps.boot.loader.model.JfxApps;
import com.gluonhq.jfxapps.boot.loader.model.JfxAppsExtension;
import com.gluonhq.jfxapps.boot.loader.model.LoadState;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.registry.model.Dependency;

@Mapper(componentModel = "spring")
public interface LoaderMappers {

    @Mapping(target = "repositoryClient", source = "repository")
    MavenExtensionProvider map(Dependency dependency, RepositoryClient repository);

    @Mapping(target = "repositoryClient", source = "repository")
    MavenExtensionProvider map(com.gluonhq.jfxapps.boot.loader.internal.model.Application application,
            RepositoryClient repository);

    @Mapping(target = "repositoryClient", source = "repository")
    MavenExtensionProvider map(Extension root, RepositoryClient repository);

    LoadState map(State state);

    State map(LoadState state);

    default JfxApps mapToJfxApps(com.gluonhq.jfxapps.registry.model.Application app, RepositoryClient repository) {
        var r = new JfxApps(app.getUuid(), map(app.getDependency(), repository));
        if (Objects.nonNull(app.getExtensions())) {
            app.getExtensions().forEach(c -> r.addExtension(mapToJfxAppsExtension(c, repository)));
        }
        return r;
    }

    default JfxApps mapToJfxApps(com.gluonhq.jfxapps.boot.loader.internal.model.Application app,
            RepositoryClient repository) {
        var r = new JfxApps(app.getId(), map(app, repository));
        r.setLoadState(map(app.getState()));
        if (Objects.nonNull(app.getExtensions())) {
            app.getExtensions().forEach(c -> r.addExtension(mapToJfxAppsExtension(c, repository)));
        }
        return r;
    }

    default Application mapToApplication(com.gluonhq.jfxapps.registry.model.Application app,
            RepositoryClient repository) {
        var r = new Application(app.getUuid(), map(app.getDependency(), repository));
        if (Objects.nonNull(app.getExtensions())) {
            app.getExtensions().forEach(c -> r.addExtension(mapToApplicationExtension(c, repository)));
        }
        return r;
    }

    default Application mapToApplication(com.gluonhq.jfxapps.boot.loader.internal.model.Application app,
            RepositoryClient repository) {
        var r = new Application(app.getId(), map(app, repository));
        r.setLoadState(map(app.getState()));
        if (Objects.nonNull(app.getExtensions())) {
            app.getExtensions().forEach(c -> r.addExtension(mapToApplicationExtension(c, repository)));
        }
        return r;
    }

    default JfxAppsExtension mapToJfxAppsExtension(com.gluonhq.jfxapps.registry.model.Extension ext,
            RepositoryClient repository) {
        var r = new JfxAppsExtension(ext.getUuid(), map(ext.getDependency(), repository));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

    default JfxAppsExtension mapToJfxAppsExtension(Extension ext, RepositoryClient repository) {
        var r = new JfxAppsExtension(ext.getId(), map(ext, repository));
        r.setLoadState(map(ext.getState()));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

    default ApplicationExtension mapToApplicationExtension(com.gluonhq.jfxapps.registry.model.Extension ext,
            RepositoryClient repository) {
        var r = new ApplicationExtension(ext.getUuid(), map(ext.getDependency(), repository));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

    default ApplicationExtension mapToApplicationExtension(Extension ext, RepositoryClient repository) {
        var r = new ApplicationExtension(ext.getId(), map(ext, repository));
        r.setLoadState(map(ext.getState()));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

    default com.gluonhq.jfxapps.boot.loader.model.Extension mapToExtension(
            com.gluonhq.jfxapps.registry.model.Extension ext, RepositoryClient repository) {
        var r = new com.gluonhq.jfxapps.boot.loader.model.Extension(ext.getUuid(),
                map(ext.getDependency(), repository));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

    default com.gluonhq.jfxapps.boot.loader.model.Extension mapToExtension(
            com.gluonhq.jfxapps.boot.loader.internal.model.Extension ext, RepositoryClient repository) {
        var r = new com.gluonhq.jfxapps.boot.loader.model.Extension(ext.getId(), map(ext, repository));
        r.setLoadState(map(ext.getState()));
        if (Objects.nonNull(ext.getExtensions())) {
            ext.getExtensions().forEach(c -> r.addExtension(mapToExtension(c, repository)));
        }
        return r;
    }

}
