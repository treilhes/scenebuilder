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
package com.gluonh.jfxapps.boot.layer.internal;

import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonh.jfxapps.boot.layer.InvalidLayerException;
import com.gluonh.jfxapps.boot.layer.Layer;
import com.gluonh.jfxapps.boot.layer.ModuleLayerManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ModuleLayerManagerImpl.
 */
public class ModuleLayerManagerImpl implements ModuleLayerManager {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ModuleLayerManagerImpl.class);
    
    /** The Constant DUPLICATE_LAYER. */
    private static final String DUPLICATE_LAYER = "Layer already exists with directory : %s";
    
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
    private ModuleLayerManagerImpl() {
        super();
    }

    /**
     * Gets the.
     *
     * @param layerId the layer id
     * @return the layer
     */
    @Override
    public Layer get(UUID layerId) {
        return layers.get(layerId);
    }

    /**
     * Creates the.
     *
     * @param parent the parent
     * @param layerId the layer id
     * @param directory the directory
     * @return the layer
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InvalidLayerException the invalid layer exception
     */
    @Override
    public Layer create(Layer parent, UUID layerId, Path directory) throws IOException, InvalidLayerException {

        logger.info("Creating layer {} from  {}", layerId, directory);

        if (!Files.isDirectory(directory)) {
            throw new InvalidLayerException(String.format(INVALID_DIRECTORY, directory));
        }

        if (layers.containsKey(layerId)) {
            return layers.get(layerId);
        }

        try {
            List<ModuleLayer> parentLayers = parent == null ? List.of(ModuleLayer.boot()) : List.of(parent.getModuleLayer());

            ModuleLayerWithRef moduleLayerWithRef = Helper.createModuleLayer(parentLayers, directory);
            ModuleLayer moduleLayer = moduleLayerWithRef.getModuleLayer();
            Map<String, ModuleReference> moduleReferences = moduleLayerWithRef.getModuleReferences();

            LayerImpl layer = new LayerImpl(layerId, directory, moduleLayer, moduleReferences);

            if (parent != null) {
                layer.addParent(parent);
                // FIXME this cast is ugly as hell, remove it
                ((LayerImpl)parent).addChildren(layer);
            }

            layers.put(layer.getId(), layer);

            logger.info("Created layer {} from  {}", layerId, directory);

            return layer;
        } catch (IOException e) {
            throw new InvalidLayerException(String.format(INVALID_LAYER, directory), e);
        }
    }

    /**
     * Removes the all layers.
     *
     * @return true, if successful
     */
    @Override
    public boolean removeAllLayers() {
        ArrayList<Layer> tmpList = new ArrayList<>(layers.values());
        boolean allDeleted = tmpList.stream().allMatch(l -> {
            try {
                if (!remove(l)) {
                    logger.error("Unable to remove layer");
                } else {
                    return true;
                }
            } catch (IOException e) {
                logger.error("Unable to remove layer", e);
            }
            return false;
        });
        tmpList = null;
        return allDeleted;
    }


    /**
     * Unload.
     *
     * @param layerId the layer id
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public boolean unload(UUID layerId) throws IOException {

        if (!layers.containsKey(layerId)) {
            logger.warn("Unknown layer {}", layerId);
            return true;
        }

        Layer layer = layers.get(layerId);

        boolean unlocked = false;

        Future<Boolean> processing = layer.unlockLayer();
        try {
            unlocked = processing.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            processing.cancel(true);
        }

        if (!unlocked) {
            logger.warn("Layer is still in use {} (possible class leak)", layer.getId());
        }

        return unlocked;

    }

    /**
     * Removes the.
     *
     * @param layer the layer
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public boolean remove(Layer layer) throws IOException {

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

        Files.walk(layer.getDirectory()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

        return !Files.exists(layer.getDirectory());
    }

    /**
     * Removes the.
     *
     * @param layerId the layer id
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public boolean remove(UUID layerId) throws IOException {

        if (!layers.containsKey(layerId)) {
            logger.warn("Unknown layer {}", layerId);
            return true;
        }

        Layer layer = layers.get(layerId);

        boolean unlocked = unload(layer.getId());

        if (!unlocked) {
            logger.warn("Layer is still in use {} (possible class leak), trying delete", layer.getId());
        }

        Files.walk(layer.getDirectory()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

        boolean deleted = !Files.exists(layer.getDirectory());

        if (deleted) {
            layers.remove(layerId);
        }

        return deleted;
    }

    /**
     * The Class Helper.
     */
    private static class Helper {

        /**
         * List content.
         *
         * @param directory the directory
         * @return the path[]
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private static Path[] listContent(Path directory) throws IOException {
            return Files.list(directory).toArray(Path[]::new);
        }

        /**
         * Creates the module layer.
         *
         * @param parents the parents
         * @param directory the directory
         * @return the module layer with ref
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private static ModuleLayerWithRef createModuleLayer(final List<ModuleLayer> parents, Path directory)
                throws IOException {
            ClassLoader scl = ClassLoader.getSystemClassLoader();

            Path[] content = listContent(directory);

            ModuleFinder finder = ModuleFinder.of(content);

            Map<String, ModuleReference> moduleReferences = new HashMap<>();

            Set<String> foundModules = finder.findAll().stream()
                    .filter(m -> {
                       if (parents != null && !parents.isEmpty()) {
                           boolean alreadyPresent = parents.stream().anyMatch(ml -> ml.findModule(m.descriptor().name()).isPresent());
                           if (alreadyPresent) {
                               logger.warn("Filtering already loaded module : {}", m);
                           }
                           return !alreadyPresent;
                       }
                       return true;
                    })
                    .peek(m -> logger.debug("Loadable module : {}", m))
                    .peek(m -> moduleReferences.put(m.descriptor().name(), m))
                    .map(m -> m.descriptor().name())
                    .collect(Collectors.toSet());

            List<Configuration> parentConfigurations = parents == null ? List.of() : parents.stream().map(ModuleLayer::configuration)
                    .collect(Collectors.toList());

            Configuration layerConfig = Configuration.resolve(finder, parentConfigurations, ModuleFinder.of(),
                    foundModules);

            ModuleLayer moduleLayer = ModuleLayer.defineModulesWithOneLoader(layerConfig, parents, scl).layer();

            return new ModuleLayerWithRef(moduleLayer, moduleReferences);
        }
    }

    /**
     * The Class ModuleLayerWithRef.
     */
    private static class ModuleLayerWithRef {
        
        /** The module layer. */
        private ModuleLayer moduleLayer;
        
        /** The module references. */
        private Map<String, ModuleReference> moduleReferences;

        /**
         * Instantiates a new module layer with ref.
         *
         * @param moduleLayer the module layer
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
