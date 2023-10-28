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

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import com.gluonh.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClassifier;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClient;
import com.gluonhq.jfxapps.boot.registry.RegistryManager;
import com.gluonhq.jfxapps.registry.model.Registry;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryManagerImpl.
 */
public class RegistryManagerImpl implements RegistryManager {

    /** The maven client. */
    private final MavenClient mavenClient;

    /** The module layer manager. */
    private final ModuleLayerManager moduleLayerManager;


    /**
     * Instantiates a new registry manager impl.
     *
     * @param mavenClient the maven client
     * @param moduleLayerManager the module layer manager
     */
    public RegistryManagerImpl(MavenClient mavenClient, ModuleLayerManager moduleLayerManager) {
        super();
        this.mavenClient = mavenClient;
        this.moduleLayerManager = moduleLayerManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initRegistry() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Registry load(MavenArtifact artifact) {

        Optional<Path> resolved = mavenClient.resolve(artifact, MavenClassifier.DEFAULT);
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Delete.
     *
     * @param registry the registry
     * @return true, if successful
     */
    @Override
    public boolean delete(Registry registry) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Delete.
     *
     * @param registryId the registry id
     * @return true, if successful
     */
    @Override
    public boolean delete(UUID registryId) {
        // TODO Auto-generated method stub
        return false;
    }


}
