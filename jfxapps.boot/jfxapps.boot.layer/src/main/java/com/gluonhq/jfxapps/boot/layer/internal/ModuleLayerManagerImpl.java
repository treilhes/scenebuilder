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

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.layer.InvalidLayerException;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ModuleLayerManagerImpl.
 */
@Component
public class ModuleLayerManagerImpl implements ModuleLayerManager {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ModuleLayerManagerImpl.class);

    /** The Constant DUPLICATE_LAYER. */
    private static final String DUPLICATE_LAYER = "Layer already exists : %s";

    /** The Constant INVALID_DIRECTORY. */
    private static final String INVALID_DIRECTORY = "invalid directory : %s";

    /** The Constant INVALID_LAYER. */
    private static final String INVALID_LAYER = "invalid layer : %s";

    /** The instance. */
    private static ModuleLayerManager INSTANCE;

    /** The layers. */
    private Map<UUID, Layer> layers = new HashMap<>();

    /**
     * Gets the.
     *
     * @return the module layer manager
     */
    public static ModuleLayerManager get() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ModuleLayerManagerImpl();
        }
        return INSTANCE;
    }

    /**
     * Instantiates a new module layer manager impl.
     */
    ModuleLayerManagerImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Layer get(UUID layerId) {
        return layers.get(layerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Layer create(Layer parent, UUID layerId, List<Path> paths, Path tempDirectory)
            throws IOException, InvalidLayerException {

        logger.info("Creating layer {} from  {}", layerId, tempDirectory);

        if (tempDirectory != null && !Files.isDirectory(tempDirectory)) {
            throw new InvalidLayerException(String.format(INVALID_DIRECTORY, tempDirectory));
        }

        if (layers.containsKey(layerId)) {
            throw new InvalidLayerException(String.format(DUPLICATE_LAYER, layerId));
        }
        // this.getClass().getSuperclass().getModule()
        try {
            List<ModuleLayer> parentLayers = parent == null ? List.of(ModuleLayer.boot())
                    : List.of(parent.getModuleLayer());
            // List<ModuleLayer> parentLayers = parent == null ?
            // List.of(this.getClass().getModule().getLayer()) :
            // List.of(parent.getModuleLayer());

            if (logger.isDebugEnabled()) {
                String strList = parentLayers.stream().flatMap(ml -> ml.modules().stream()).map(Module::getName)
                        .sorted().collect(Collectors.joining(","));
                logger.debug("Accessible modules : {}", strList);
            }

            ModuleLayerWithRef moduleLayerWithRef = Helper.createModuleLayer(parentLayers, paths, tempDirectory);
            ModuleLayer moduleLayer = moduleLayerWithRef.getModuleLayer();
            Map<String, ModuleReference> moduleReferences = moduleLayerWithRef.getModuleReferences();

            LayerImpl layer = new LayerImpl(layerId, paths, tempDirectory, moduleLayer, moduleReferences);

            if (parent != null) {
                layer.addParent(parent);
                // FIXME this cast is ugly as hell, remove it
                ((LayerImpl) parent).addChildren(layer);
            }

            layers.put(layer.getId(), layer);

            logger.info("Created layer {} from  {}", layerId, tempDirectory);

            return layer;
        } catch (IOException e) {
            throw new InvalidLayerException(String.format(INVALID_LAYER, tempDirectory), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidLayerException
     * @throws IOException
     */
    @Override
    public Layer create(Layer parent, List<Path> paths, Path directory) throws IOException, InvalidLayerException {
        return create(parent, UUID.randomUUID(), paths, directory);
    }

    @Override
    public Layer create(List<Path> paths, Path directory) throws IOException, InvalidLayerException {
        return create(null, UUID.randomUUID(), paths, directory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllLayers() {
        Set<Layer> parents = layers.values().stream().filter(l -> l.getParents().isEmpty()).collect(Collectors.toSet());

        for (Layer l : parents) {
            try {
                if (!remove(l)) {
                    logger.error("Unable to remove layer {}", l.getId());
                    return false;
                }
            } catch (IOException e) {
                logger.error("Unable to remove layer {}", l.getId(), e);
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Layer layer) throws IOException {
        Objects.requireNonNull(layer);

        if (!layer.getChildren().isEmpty()) {
            Set<Layer> children = new HashSet<>(layer.getChildren());
            for (Layer l : children) {
                if (!remove(l)) {
                    return false;
                }
            }
        }

        if (!layers.containsKey(layer.getId())) {
            logger.warn("Unknown layer {}", layer.getId());
        } else {
            layers.remove(layer.getId());
        }

        boolean unlocked = false;

        Future<Boolean> processing = layer.unlockLayer();
        try {
            unlocked = processing.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            processing.cancel(true);
        }

        if (!unlocked) {
            logger.warn("Layer is still in use {} (possible class leak), trying delete", layer.getId());
        }

        return layer.clean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(UUID layerId) throws IOException {
        Layer layer = get(layerId);
        return remove(layer);
    }

    /**
     * The Class Helper.
     */
    private static class Helper {

        private static final String MODULE_INFO_CLASS_FILE = "module-info.class";
        private static final String META_INF = "META-INF";

        /**
         * Creates a {@link ModuleLayer} with the provided parent layers and containing</br>
         * all the files provided and the content of a deletable directory.</br>
         *</br>
         * Layer must be successfully created from different type of Path</br>
         * </br>
         * Path objects can lead to:</br>
         * - jar file </br>
         * - folder containing classes (ex: maven target folder) if the META-INF folder exists</br>
         * - folder containing jars and/or module folders</br>
         * </br>
         * @param parents   the parent {@link ModuleLayer}
         * @param paths     a list of files to add as content for this
         *                  {@link ModuleLayer}. Won't be deleted
         * @param tempDirectory a deletable temporary directory which contains content for
         *                  this {@link ModuleLayer}
         * @return the {@link ModuleLayer} content description
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private static ModuleLayerWithRef createModuleLayer(final List<ModuleLayer> parents, List<Path> paths,
                Path tempDirectory) throws IOException {

            ClassLoader scl = ClassLoader.getSystemClassLoader();

            List<Path> contentList = new ArrayList<>();
            if (tempDirectory != null && Files.isDirectory(tempDirectory)) {
                try {
                    populateContentWith(contentList, tempDirectory);
                } catch (IOException e) {
                    logger.error("Exception processing tempDirectory folder {}", tempDirectory);
                }
            }

            if (paths != null) {
                paths.forEach(f -> {
                    try {
                        populateContentWith(contentList, f);
                    } catch (IOException e) {
                        logger.error("Exception processing folder {}", f);
                    }
                });
            }

            Path[] content = contentList.toArray(Path[]::new);

            logger.debug("Content found for module creation : {}", (Object) content);

            ModuleFinder finder = ModuleFinder.of(content);

            Map<String, ModuleReference> moduleReferences = new HashMap<>();

            Set<String> foundModules = finder.findAll().stream().filter(m -> {
                if (parents != null && !parents.isEmpty()) {
                    boolean alreadyPresent = parents.stream()
                            .anyMatch(ml -> ml.findModule(m.descriptor().name()).isPresent());
                    if (alreadyPresent) {
                        logger.warn("Filtering already loaded module : {}", m);
                    }
                    return !alreadyPresent;
                }
                return true;
            }).peek(m -> logger.debug("Loadable module : {}", m))
                    .peek(m -> moduleReferences.put(m.descriptor().name(), m)).map(m -> m.descriptor().name())
                    .collect(Collectors.toSet());

            List<Configuration> parentConfigurations = parents == null ? List.of()
                    : parents.stream().map(ModuleLayer::configuration).collect(Collectors.toList());

            Configuration layerConfig = Configuration.resolve(finder, parentConfigurations, ModuleFinder.of(),
                    foundModules);

            ModuleLayer moduleLayer = ModuleLayer.defineModulesWithOneLoader(layerConfig, parents, scl).layer();

            return new ModuleLayerWithRef(moduleLayer, moduleReferences);

        }

        /**
         * Detect the type of Path and handle it accordingly</br>
         * Path object can lead to:</br>
         * - a file : add it to content</br>
         * - a folder:
         * ----- the folder contains a module-info.class file : add it to content
         * ----- the folder contains a jar file or a folder containing a module-info.class : add all children as content
         * ----- else it must be a folder containing classes : add it to content
         * @param contentList
         * @param f
         * @throws IOException
         */
        private static void populateContentWith(List<Path> contentList, Path f) throws IOException {

            Predicate<Path> jarOrExpandedModule = p -> p.getFileName().toString().toLowerCase().endsWith(".jar")
                    || Files.exists(p.resolve(MODULE_INFO_CLASS_FILE));

            if (Files.isReadable(f)) {
                contentList.add(f);
            } else if (Files.isDirectory(f)) {
                if (Files.exists(f.resolve(MODULE_INFO_CLASS_FILE))) {
                    contentList.add(f);
                } else if (Files.list(f).anyMatch(jarOrExpandedModule)) {
                    try {
                        contentList.addAll(Files.list(f).toList());
                    } catch (IOException e) {
                        logger.error("Unable to list directory content of {}", f);
                    }
                } else {
                    contentList.add(f);
                }
            } else {
                logger.error("Discarded layer content {}", f);
            }
        }

    }

    /**
     * The Class ModuleLayerWithRef allows to map the {@link ModuleLayer} to its
     * content.
     */
    private static class ModuleLayerWithRef {

        /** The module layer. */
        private ModuleLayer moduleLayer;

        /** The module references. */
        private Map<String, ModuleReference> moduleReferences;

        /**
         * Instantiates a new module layer with ref.
         *
         * @param moduleLayer      the module layer
         * @param moduleReferences the module references
         */
        public ModuleLayerWithRef(ModuleLayer moduleLayer, Map<String, ModuleReference> moduleReferences) {
            super();
            this.moduleLayer = moduleLayer;
            this.moduleReferences = moduleReferences;
        }

        /**
         * Gets the module layer.
         *
         * @return the module layer
         */
        public ModuleLayer getModuleLayer() {
            return moduleLayer;
        }

        /**
         * Gets the module references.
         *
         * @return the module references
         */
        public Map<String, ModuleReference> getModuleReferences() {
            return moduleReferences;
        }

    }

}
