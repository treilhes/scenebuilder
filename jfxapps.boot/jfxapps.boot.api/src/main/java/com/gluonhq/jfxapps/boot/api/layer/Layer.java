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
package com.gluonhq.jfxapps.boot.api.layer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The Interface Layer.
 */
public interface Layer {

    public final static UUID ROOT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

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
    Path getTempDirectory();

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
    boolean unlockLayer();

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

    /**
     * Clean the temporary directory
     * @return true if successfull
     * @throws IOException
     */
    boolean clean() throws IOException;

    ClassLoader getLoader();

    /**
     * Gets the class from a module using the following pattern
     * "my.module.name/my.class.Name"
     *
     * @param moduleSlashClassName the module slash class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    default Class<?> getClass(String moduleSlashClassName) throws ClassNotFoundException {
        String[] parts = moduleSlashClassName.split("/");
        return getClass(parts[0], parts[1]);
    }

    /**
     * Gets the class from a module using the following pattern
     * "my.module.name/my.class.Name"
     *
     * @param moduleSlashClassName the module slash class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    default Class<?> getClass(String moduleName, String className) throws ClassNotFoundException {
        try {
            ClassLoader loader = getModuleLayer().findLoader(moduleName);
            return loader.loadClass(className);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException(String.format("Module %s not found", moduleName));
    }

    default URL getRessource(String moduleName, String resource) {
        try {
            ClassLoader loader = getModuleLayer().findLoader(moduleName);
            return loader.getResource(resource);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException(String.format("Module %s not found", moduleName));
    }

    default URL getRessource(String resource) {
        return getModuleLayer().modules().stream().map(m -> getRessource(m.getName(), resource))
                .filter(Objects::nonNull).findFirst().orElse(null);
    }

    default InputStream getResourceAsStream(String moduleName, String resource) {
        try {
            ClassLoader loader = getModuleLayer().findLoader(moduleName);
            return loader.getResourceAsStream(resource);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException(String.format("Module %s not found", moduleName));

//        try {
//            URL url = getRessource(moduleName, resource);
//            URLConnection connection = url.openConnection();
//            connection.setUseCaches(true);
//            return connection.getInputStream();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
    }

    default InputStream getResourceAsStream(String resource) {
        return getModuleLayer().modules().stream().map(m -> getResourceAsStream(m.getName(), resource))
                .filter(Objects::nonNull).findFirst().orElse(null);
    }

    default Enumeration<URL> getRessources(String moduleName, String resource) throws IOException {
        try {
            ClassLoader loader = getModuleLayer().findLoader(moduleName);
            return loader.getResources(resource);
        }
        catch (IllegalArgumentException iae) {
            // Nothing
        }
        throw new IllegalArgumentException(String.format("Module %s not found", moduleName));
    }

    default Enumeration<URL> getRessources(String resource) throws IOException {
        List<URL> list = getModuleLayer().modules().stream()
                .map(m -> {
                    try {
                        return getRessources(m.getName(), resource);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(this::enumerationToList)
                .flatMap(e -> e.stream())
                .toList();
        return Collections.enumeration(list);
    }

    private <T> List<T> enumerationToList(Enumeration<T> enumeration) {
        List<T> l = new ArrayList<>();
        enumeration.asIterator().forEachRemaining(l::add);
        return l;
    }


}
