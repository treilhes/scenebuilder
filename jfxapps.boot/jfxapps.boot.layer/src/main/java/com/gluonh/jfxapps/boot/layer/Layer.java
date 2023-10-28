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
package com.gluonh.jfxapps.boot.layer;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Interface Layer.
 */
public interface Layer {

    /**
     * Gets the id.
     *
     * @return the id
     */
    UUID getId();

    /**
     * Gets the directory.
     *
     * @return the directory
     */
    Path getDirectory();

    /**
     * Gets the module layer.
     *
     * @return the module layer
     */
    ModuleLayer getModuleLayer();

    /**
     * Jars.
     *
     * @return the sets the
     */
    Set<Path> jars();

    /**
     * Modules.
     *
     * @return the sets the
     */
    Set<WeakReference<Module>> modules();

    /**
     * Automatic modules.
     *
     * @return the sets the
     */
    Set<WeakReference<Module>> automaticModules();

    /**
     * Unnamed modules.
     *
     * @return the sets the
     */
    Set<WeakReference<Module>> unnamedModules();

    /**
     * All modules.
     *
     * @return the sets the
     */
    default Set<Module> allModules() {
        return getModuleLayer().modules();
    }

    /**
     * Gets the location.
     *
     * @param module the module
     * @return the location
     */
    Optional<URI> getLocation(Module module);

    /**
     * Unlock layer.
     *
     * @return the future
     */
    Future<Boolean> unlockLayer();

    /**
     * Gets the parents.
     *
     * @return the parents
     */
    Set<Layer> getParents();

    /**
     * Gets the children.
     *
     * @return the children
     */
    Set<Layer> getChildren();

    //void bootLayer(BootProgressListener progressListener);

    /**
     * Load service.
     *
     * @param <T> the generic type
     * @param serviceClass the service class
     * @return the sets the
     */
    default <T> Set<T> loadService(Class<T> serviceClass) {
        return ServiceLoader.load(getModuleLayer(), serviceClass).stream()
                .map(ServiceLoader.Provider::get)
                .filter(e -> e.getClass().getModule().getLayer().equals(getModuleLayer()))
                .collect(Collectors.toSet());
    }

    /**
     * Gets the class.
     *
     * @param moduleSlashClassName the module slash class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    default Class<?> getClass(String moduleSlashClassName) throws ClassNotFoundException {
        String[] parts = moduleSlashClassName.split("/");
        try {
            ClassLoader loader = getModuleLayer().findLoader(parts[0]);
            return loader.loadClass(parts[1]);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException("Module " + parts[0] + " not found");
    }

}
