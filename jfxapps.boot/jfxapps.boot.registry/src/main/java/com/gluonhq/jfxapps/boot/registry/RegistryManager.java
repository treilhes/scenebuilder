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
package com.gluonhq.jfxapps.boot.registry;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.registry.model.Application;
import com.gluonhq.jfxapps.registry.model.Extension;
import com.gluonhq.jfxapps.registry.model.Registry;

/**
 * The Interface RegistryManager manage the registries. A {@link Registry} is a
 * maven artifact containing a list of available {@link Application} and also a
 * list of optional {@link Extension}.<br/>
 * <br/>
 * A registry source is provided as a {@link UniqueArtifact} which will be used
 * as a base version. If the registry source does not specify a version the
 * latest available one is used<br/>
 * <br/>
 * Three type of registry ({@link RegistryType}) exists:<br/>
 * <br/>
 * {@link RegistryType.Mandatory}<br/>
 * * All applications in those registries will be installed by default. Optional
 * extensions will still need user validation to install<br/>
 * {@link RegistryType.Fixed}<br/>
 * * When one of the component (application or extension) is installed/updated
 * the registry version will be fixed/frozen which means all components
 * (application or extension) from this registry will use the same version.<br/>
 * {@link RegistryType.Normal}<br/>
 * * Default type for registries<br/>
 * <br/>
 */
public interface RegistryManager {

    public enum RegistryType {
        /**
         * All applications in those registries will be installed by default. Optional
         * extensions will still need user validation to install
         */
        Mandatory,
        /**
         * When one of the component (application or extension) is installed/updated the
         * registry version will be fixed/frozen which means all components (application
         * or extension) from this registry will use the same version.
         */
        Fixed,
        /**
         * Default type for registries
         */
        Normal
    }

    /**
     * Load registries already retrieved from network.
     *
     * @throws IOException
     */
    void loadState() throws IOException;

    /**
     * Load the registry artifact from maven, store it and load it.
     *
     * @param artifact the artifact
     * @return the registry
     */
    Registry load(UniqueArtifact artifact);

    /**
     * get registry by id.
     *
     * @param registryId the registry id
     * @return true, if successful
     */
    Registry get(UUID registryId);

    /**
     * Delete a loaded registry.
     *
     * @param registry the registry
     * @return true, if successful
     */
    boolean delete(Registry registry);

    /**
     * Delete a loaded registry by id.
     *
     * @param registryId the registry id
     * @return true, if successful
     */
    default boolean delete(UUID registryId) {
        Registry registry = get(registryId);
        return delete(registry);
    }

    /**
     * List loaded registries
     *
     * @return the unmodifiable registry list
     */
    Collection<Registry> list();

    Registry installedRegistry();

    Registry installableRegistry();

    Registry bootRegistry();

}
