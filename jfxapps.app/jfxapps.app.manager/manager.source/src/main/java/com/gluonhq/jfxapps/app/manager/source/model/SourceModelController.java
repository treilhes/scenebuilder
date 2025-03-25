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
package com.gluonhq.jfxapps.app.manager.source.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifact;
import com.gluonhq.jfxapps.boot.api.registry.RegistryArtifactManager;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@ApplicationInstanceSingleton
public class SourceModelController {

    private static final Logger logger = LoggerFactory.getLogger(SourceModelController.class);

    public static final String LATEST = "Latest";
    private final RegistryArtifactManager registryArtifactManager;
    private final JfxAppPlatform jfxAppPlatform;
    private final ObservableList<Source> sources = FXCollections.observableArrayList();
    private final ApplicationInstance instance;


    public SourceModelController(
            RegistryArtifactManager registryArtifactManager,
            ApplicationInstance instance,
            JfxAppPlatform jfxAppPlatform) {
        this.registryArtifactManager = registryArtifactManager;
        this.instance = instance;
        this.jfxAppPlatform = jfxAppPlatform;
    }

    public ObservableList<Source> getSources() {
        return sources;
    }

    public void load() {
        var registrySourceInfos = registryArtifactManager.listRegistrySourceInfo();
        var sourceItems = registrySourceInfos.stream()
                .map(Source::new)
                .toList();
        sources.setAll(sourceItems);
    }

    public boolean update(Source source, Source newSource) {
        try {
            var oldSourceInfo = source.infoProperty().get();
            var oldGroupId = source.groupIdProperty().get();
            var oldArtifactId = source.artifactIdProperty().get();
            var oldVersion = source.versionProperty().get();

            var groupId = newSource.groupIdProperty().get();
            var artifactId = newSource.artifactIdProperty().get();
            var version = newSource.versionProperty().get();

            var coordinateChanged = !oldGroupId.equals(groupId) || !oldArtifactId.equals(artifactId);
            var versionChanged = !oldVersion.equals(version);

            if (coordinateChanged) {

                var oldArtifact = oldSourceInfo.getArtifact();
                var artifact = new RegistryArtifact(groupId, artifactId, LATEST.equals(version) ? null : version, false);
                registryArtifactManager.remove(oldArtifact);
                registryArtifactManager.add(artifact);

                var newInfo = registryArtifactManager.getRegistrySourceInfo(groupId, artifactId);
                source.infoProperty().set(newInfo);

            } else if (versionChanged) {

                var artifact = new RegistryArtifact(groupId, artifactId, version, false);
                registryArtifactManager.update(artifact);
                source.versionProperty().set(version);

            }

            launchUpdate(source);
            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }

    public boolean create(Source source) {

        try {
            var groupId = source.groupIdProperty().get();
            var artifactId = source.artifactIdProperty().get();
            var version = source.versionProperty().get();
            var artifact = new RegistryArtifact(groupId, artifactId, version, false);

            registryArtifactManager.add(artifact);

            var sourceInfo = registryArtifactManager.getRegistrySourceInfo(groupId, artifactId);

            Source newSource = new Source(sourceInfo);
            sources.add(0, newSource);

            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }

    public boolean delete(Source source) {
        try {
            var info = source.infoProperty().get();
            var artifact = info.getArtifact();
            registryArtifactManager.remove(artifact);
            sources.remove(source);
            return true;
        } catch (Exception e) {
            handleException(source, e);
            return false;
        }
    }

    public void launchUpdate(Source source) {

        if (source.updatingProperty().get()) {
            return;
        }
        source.updatingProperty().set(true);

        var info = source.infoProperty().get();
        var artifact = info.getArtifact();


        try {
            Thread.startVirtualThread(() -> {
                try {
                    var newInfo = registryArtifactManager.loadLatestRegistrySourceInfo(artifact.groupId(), artifact.artifactId());

                    jfxAppPlatform.runOnFxThreadWithScope(instance, () -> {
                        source.infoProperty().set(newInfo);
                        source.updatingProperty().set(false);
                    });
                } catch (Exception e) {
                    jfxAppPlatform.runOnFxThreadWithScope(instance, () -> {
                        source.updatingProperty().set(false);
                        handleException(source, e);
                    });
                }
            }).join();

        } catch (Exception e) {
            source.updatingProperty().set(false);
            handleException(source, e);
        }
    }

    private void handleException(Source source, Exception e) {
        logger.error("Error updating source", e);
        source.errorProperty().set(true);
        source.errorMessageProperty().set(e.getMessage());
    }

}
