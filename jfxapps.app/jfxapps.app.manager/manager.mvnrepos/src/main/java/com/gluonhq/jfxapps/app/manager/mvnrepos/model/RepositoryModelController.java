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
package com.gluonhq.jfxapps.app.manager.mvnrepos.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@ApplicationInstanceSingleton
public class RepositoryModelController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryModelController.class);

    private final RepositoryManager repositoryManager;
    private final JfxAppPlatform jfxAppPlatform;
    private final ObservableList<Repository> repositories = FXCollections.observableArrayList();
    private final ApplicationInstance instance;

    private final RepositoryMapper mapper;

    public RepositoryModelController(
            RepositoryManager repositoryManager,
            ApplicationInstance instance,
            JfxAppPlatform jfxAppPlatform,
            RepositoryMapper mapper) {
        this.repositoryManager = repositoryManager;
        this.instance = instance;
        this.jfxAppPlatform = jfxAppPlatform;
        this.mapper = mapper;
    }

    public ObservableList<Repository> getSources() {
        return repositories;
    }

    public void load() {
        var registrySourceInfos = repositoryManager.repositories();
        var repositoryItems = registrySourceInfos.stream()
                .map(Repository::new)
                .toList();
        repositories.setAll(repositoryItems);
    }

    public boolean update(Repository source, Repository newSource) {
        try {
            var oldRepository = source.sourceProperty().get();

            var newName = newSource.nameProperty().get();
            var newUrl = newSource.urlProperty().get();
            var newLogin = newSource.loginProperty().get();
            var newPassword = newSource.passwordProperty().get();
            var newType = newSource.repositoryTypeProperty().get();
            var newContentType = newSource.contentTypeProperty().get();

            var repository = com.gluonhq.jfxapps.boot.api.maven.Repository.builder()
                    .id(newName)
                    .url(newUrl)
                    .user(newLogin)
                    .password(newPassword)
                    .type(newType.getClass())
                    .contentType(newContentType)
                    .build();
            repositoryManager.remove(oldRepository);
            repositoryManager.add(repository);
            source.sourceProperty().set(repository);

            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }

    public boolean create(Repository source) {

        try {

            var newName = source.nameProperty().get();
            var newUrl = source.urlProperty().get();
            var newLogin = source.loginProperty().get();
            var newPassword = source.passwordProperty().get();
            var newType = source.repositoryTypeProperty().get();
            var newContentType = source.contentTypeProperty().get();

            var repository = com.gluonhq.jfxapps.boot.api.maven.Repository.builder()
                    .id(newName)
                    .url(newUrl)
                    .user(newLogin)
                    .password(newPassword)
                    .type(newType.getClass())
                    .contentType(newContentType)
                    .build();

            repositoryManager.add(repository);

            source.sourceProperty().set(repository);

            repositories.add(0, source);
            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }

    public boolean delete(Repository source) {
        try {
            var repository = source.sourceProperty().get();
            repositoryManager.remove(repository);
            repositories.remove(source);
            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }


    private void handleException(Repository source, Exception e) {
        logger.error("Error updating source", e);
        source.errorProperty().set(true);
        source.errorMessageProperty().set(e.getMessage());
    }

}
