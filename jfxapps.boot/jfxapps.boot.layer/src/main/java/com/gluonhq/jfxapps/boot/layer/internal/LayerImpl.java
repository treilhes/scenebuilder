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
package com.gluonhq.jfxapps.boot.layer.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.ref.WeakReference;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.layer.Layer;

// TODO: Auto-generated Javadoc
/**
 * The Class LayerImpl.
 */
public class LayerImpl implements Layer {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(LayerImpl.class);

    /** The Constant META_INF_MANIFEST_MF. */
    private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";

    /** The Constant MODULE_NAME_ATTRIBUTE. */
    private static final Attributes.Name MODULE_NAME_ATTRIBUTE = new Attributes.Name("Automatic-Module-Name");

    /** The id. */
    private UUID id;

    /** The directory. */
    private Path tempDirectory;

    /** The module layer lock. */
    private ModuleLayer moduleLayerLock;

    /** The module layer. */
    private WeakReference<ModuleLayer> moduleLayer;

    /** The module references. */
    private Map<String, WeakReference<ModuleReference>> moduleReferences;

    /** The jars. */
    private Set<Path> jars;

    /** The modules. */
    private Set<WeakReference<Module>> modules;

    /** The automatic modules. */
    private Set<WeakReference<Module>> automaticModules;

    /** The unnamed modules. */
    private Set<WeakReference<Module>> unnamedModules;

    /** The parents. */
    private Set<Layer> parents;

    /** The children. */
    private Set<Layer> children;

    private List<Path> paths;

    /**
     * Instantiates a new layer impl.
     *
     * @param id               the id
     * @param tempDirectory        the directory
     * @param moduleLayer      the module layer
     * @param moduleReferences the module references
     */
    public LayerImpl(UUID id, List<Path> paths, Path tempDirectory, ModuleLayer moduleLayer, Map<String, ModuleReference> moduleReferences) {
        super();
        this.id = id;
        this.paths = paths != null ? new ArrayList<>(paths) : Collections.emptyList();
        this.tempDirectory = tempDirectory;
        this.moduleLayerLock = moduleLayer;
        this.moduleLayer = new WeakReference<ModuleLayer>(moduleLayer);

        this.parents = new HashSet<>();
        this.children = new HashSet<>();
        this.moduleReferences = new HashMap<>();
        moduleReferences.forEach((k, v) -> this.moduleReferences.put(k, new WeakReference<ModuleReference>(v)));
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Gets the directory.
     *
     * @return the directory
     */
    @Override
    public Path getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Gets the module layer.
     *
     * @return the module layer
     */
    @Override
    public ModuleLayer getModuleLayer() {
        return moduleLayer.get();
    }

    /**
     * Jars.
     *
     * @return the sets the
     */
    @Override
    public Set<Path> jars() {
        if (jars == null) {
            var tmpjars = new HashSet<Path>();
            try {
                if (tempDirectory != null) {
                    tmpjars.addAll(Files.list(tempDirectory).filter(p -> p.getFileName().toString().toLowerCase().endsWith(".jar"))
                            .collect(Collectors.toSet()));
                }

                if (paths != null) {
                    tmpjars.addAll(paths.stream().filter(p -> p.getFileName().toString().toLowerCase().endsWith(".jar"))
                            .collect(Collectors.toSet()));

                    tmpjars.addAll(paths.stream().filter(Files::isDirectory)
                            .flatMap(p -> {
                                try {
                                    return Files.list(p);
                                } catch (IOException e) {
                                    return Stream.of();
                                }
                            })
                            .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".jar"))
                            .collect(Collectors.toSet()));
                }
                jars = tmpjars;
            } catch (IOException e) {
                logger.error("Unable to list jars in directory {}", tempDirectory, e);
            }
        }
        return jars;
    }

    /**
     * Modules.
     *
     * @return the sets the
     */
    @Override
    public Set<WeakReference<Module>> modules() {
        if (modules == null) {
            populateCache();
        }
        return modules;
    }

    /**
     * Automatic modules.
     *
     * @return the sets the
     */
    @Override
    public Set<WeakReference<Module>> automaticModules() {
        if (automaticModules == null) {
            populateCache();
        }
        return automaticModules;
    }

    /**
     * Unnamed modules.
     *
     * @return the sets the
     */
    @Override
    public Set<WeakReference<Module>> unnamedModules() {
        if (unnamedModules == null) {
            populateCache();
        }
        return unnamedModules;
    }

    /**
     * Gets the location.
     *
     * @param module the module
     * @return the location
     */
    @Override
    public Optional<URI> getLocation(Module module) {
        WeakReference<ModuleReference> ref = moduleReferences.get(module.getName());

        if (ref == null || ref.get() == null) {
            return null;
        } else {
            return ref.get().location();
        }
    }

    /**
     * Unlock layer.
     *
     * @return the future
     */
    @Override
    public Future<Boolean> unlockLayer() {

        jars().forEach(this::trickToClearJarFileCache);

        this.moduleLayerLock = null;
        this.moduleLayer.clear();
        this.moduleReferences.clear();

        FutureTask<Boolean> task = new FutureTask<>(() -> {
            int maxAttempts = 5;
            while (maxAttempts > 0) {
                logger.debug("checking layer lock state");
                try {

                    if (Thread.interrupted()) {
                        logger.debug("layer unlock check interrupted");
                        return false;
                    }

                    System.gc();

                    if (jars().stream().filter(onlyJarsInTempDirectory()).anyMatch(LayerImpl::fileLockedCheck)) {
                        logger.debug("layer unlock check failed");
                        Thread.sleep(200);
                        maxAttempts--;
                    } else {
                        logger.debug("layer unlocked");
                        return true;
                    }

                } catch (InterruptedException e) {
                    logger.debug("layer unlock check interrupted");
                    return false;
                }
            }
            return false;
        });

        new Thread(task).start();

        return task;
    }

    /**
     * This method is a trick. When loading a jar in a {@link ModuleLayer} , opening
     * an {@link InputStream} on a resource inside the jar will create a cached
     * JarFile into the innaccessible class sun.net.www.protocol.jar.JarFileFactory.
     * While cached, you won't be able to delete this jar until you get rid of this
     * annoying lock. When accessing an EXISTING resource in the jar using the jar
     * protocol, the returned {@link URLConnection} can be casted to
     * {@link JarURLConnection} and give you access to the cached {@link JarFile}.
     * At this step, you only need to close the {@link JarFile} to release the lock
     *
     * @param jarPath
     */
    private void trickToClearJarFileCache(Path jarPath) {

        // we only need to clear the cache for jars in the temp directory
        // others won't be erased
        if (onlyJarsInTempDirectory().test(jarPath)) {
            try {

                URL jarResourceUrl = new URL("jar", "", jarPath.toUri() + "!/META-INF");
                URLConnection connection = jarResourceUrl.openConnection();

                if (connection instanceof JarURLConnection juc) {
                    JarFile jf = juc.getJarFile();
                    jf.close();
                }

            } catch (MalformedURLException e) {
                logger.error("Invalid jar path {}", jarPath, e);
            } catch (IOException e) {
                logger.error("Unable to access jar path {}", jarPath, e);
            }
        }
    }

    private Predicate<Path> onlyJarsInTempDirectory() {
        return (j) -> tempDirectory != null && j != null && j.startsWith(tempDirectory) && Files.exists(j);
    }

    /**
     * File locked check.
     *
     * @param path the path
     * @return true, if successful
     */
    private static boolean fileLockedCheck(Path path) {
        boolean locked = false;
        File file = path.toFile();

        try (RandomAccessFile fis = new RandomAccessFile(file, "rw")) {
            FileLock lck = fis.getChannel().lock();
            lck.release();
        } catch (Exception ex) {
            locked = true;
        }
        if (locked) {
            return locked;
        }

        // try further with rename
        String parent = file.getParent();
        File newName = new File(parent, file.getName() + "_lockcheck");
        if (file.renameTo(newName)) {
            newName.renameTo(file);
        } else {
            locked = true;
        }

        return locked;
    }

    /**
     * Populate cache.
     */
    private synchronized void populateCache() {
        try {
            modules = new HashSet<>();
            automaticModules = new HashSet<>();
            unnamedModules = new HashSet<>();

            for (Module m : allModules()) {
                ModuleDescriptor descriptor = m.getDescriptor();

                final Manifest manifest = new Manifest();
                try (ModuleReader reader = moduleReferences.get(m.getName()).get().open()) {
                    Optional<InputStream> iso = reader.open(META_INF_MANIFEST_MF);

                    iso.ifPresent(is -> {
                        try {
                            manifest.read(is);
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    });
                }

                boolean automatic = descriptor.isAutomatic();

                boolean hasAutomaticAttribute = manifest.getMainAttributes().get(MODULE_NAME_ATTRIBUTE) != null;

                if (!automatic) { // module
                    modules.add(new WeakReference<>(m));
                } else if (hasAutomaticAttribute) { // automatic module
                    automaticModules.add(new WeakReference<>(m));
                } else { // classpath module
                    unnamedModules.add(new WeakReference<>(m));
                }
            }

        } catch (Exception e) {
            logger.error("Unable to populate modules cache", e);
        }
    }

    /**
     * Gets the parents.
     *
     * @return the parents
     */
    @Override
    public Set<Layer> getParents() {
        return parents;
    }

    /**
     * Adds the parent.
     *
     * @param parent the parent
     */
    protected void addParent(Layer parent) {
        this.parents.add(parent);
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    @Override
    public Set<Layer> getChildren() {
        return children;
    }

    /**
     * Adds the children.
     *
     * @param child the child
     */
    protected void addChildren(Layer child) {
        this.children.add(child);
    }

    @Override
    public boolean clean() throws IOException {
        if (getTempDirectory() != null && Files.isDirectory(getTempDirectory())) {
            Files.walk(getTempDirectory()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            return !Files.exists(getTempDirectory());
        }
        return true;
    }

    @Override
    public String toString() {
        return "LayerImpl [id=" + id + ", directory=" + tempDirectory + ", children=" + children
                + "]";
    }



}
