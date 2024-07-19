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
package com.gluonhq.jfxapps.boot.loader.internal.context;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.ContextManager;
import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.DeportedSingleton;
import com.gluonhq.jfxapps.boot.internal.context.config.DefaultExtensionContextConfig;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.LayerNotFoundException;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.loader.extension.ExtensionValidator;
import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.boot.loader.extension.SealedExtension;
import com.gluonhq.jfxapps.boot.loader.model.AbstractExtension;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextBootstraper.
 */
@Component
public class ContextBootstraper {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ContextBootstraper.class);

    private static final Set<Class<? extends Annotation>> deportableAnnotations = Set.of(ApplicationSingleton.class,
            ApplicationInstanceSingleton.class, ApplicationInstancePrototype.class, DeportedSingleton.class);

    /** The context manager. */
    private final ContextManager contextManager;

    /** The layer manager. */
    private final ModuleLayerManager layerManager;

    private static final ServiceLoader DEFAULT_LOADER = new ServiceLoader() {
        @Override
        public <T> Set<T> loadService(Layer layer, Class<T> serviceClass) {
            return java.util.ServiceLoader.load(layer.getModuleLayer(), serviceClass).stream()
                    .map(java.util.ServiceLoader.Provider::get)
                    .filter(e -> e.getClass().getModule().getLayer().equals(layer.getModuleLayer()))
                    .collect(Collectors.toSet());
        }
    };

    /**
     * Instantiates a new context bootstraper.
     *
     * @param layerManager the layer manager
     */
    public ContextBootstraper(ModuleLayerManager layerManager, ContextManager contextManager) {
        super();
        this.contextManager = contextManager;
        this.layerManager = layerManager;
    }

    /**
     * Gets the.
     *
     * @param extension the extension
     * @return the sb context
     */
    public JfxAppContext get(AbstractExtension<?> extension) {
        return contextManager.get(extension.getId());
    }

    /**
     * Gets the.
     *
     * @param extensionId the extension id
     * @return the sb context
     */
    public JfxAppContext get(UUID extensionId) {
        return contextManager.get(extensionId);
    }

    /**
     * Exists.
     *
     * @param extension the extension
     * @return true, if successful
     */
    public boolean exists(AbstractExtension<?> extension) {
        return contextManager.exists(extension.getId());
    }

    public JfxAppContext create(JfxAppContext parent, AbstractExtension<?> extension, List<Object> singletonInstances,
            MultipleProgressListener progressListener) throws InvalidExtensionException, LayerNotFoundException {
        return create(parent, extension, singletonInstances, progressListener, DEFAULT_LOADER);
    }

    /**
     * Creates the.
     *
     * @param parent             the parent
     * @param extension          the extension
     * @param singletonInstances the singleton instances
     * @param progressListener   the progress listener
     * @return the sb context
     * @throws InvalidExtensionException the invalid extension exception
     * @throws LayerNotFoundException    the layer not found exception
     */
    public JfxAppContext create(JfxAppContext parent, AbstractExtension<?> extension, List<Object> singletonInstances,
            MultipleProgressListener progressListener, ServiceLoader loader)
            throws InvalidExtensionException, LayerNotFoundException {
        UUID layerId = extension.getId();
        UUID parentContextId = parent == null ? null : parent.getUuid();

        // get children extensions
        Set<UUID> extensionIds = extension.getExtensions().stream().map(e -> e.getId()).collect(Collectors.toSet());

        Layer currentLayer = layerManager.get(layerId);

        if (currentLayer == null) {
            throw new LayerNotFoundException(layerId, "Unable to find layer for id %s");
        }

        Set<Class<?>> classes = new HashSet<>();
        Set<Class<?>> extensionLocalClasses = new HashSet<>();
        Set<Class<?>> childrenExportedClasses = new HashSet<>();
        Set<Class<?>> childrenDeportedClasses = new HashSet<>();

        for (UUID id : extensionIds) {
            try {
                var map = findExportedClasses2(loader, layerId, id);

                childrenExportedClasses.addAll(map.getOrDefault(ExportType.EXPORTED, List.of()));
                childrenDeportedClasses.addAll(map.getOrDefault(ExportType.DEPORTED, List.of()));
            } catch (LayerNotFoundException e) {
                logger.error("Unable to find layer for child extension {}", id, e);
            } catch (InvalidExtensionException e) {
                logger.error("Child extension is not valid {}", id, e);
            }
        }

        extensionLocalClasses.addAll(findLocalClasses(loader, parentContextId, currentLayer));

        classes.addAll(childrenExportedClasses);
        classes.addAll(DefaultExtensionContextConfig.classesToRegister);

        boolean isSealed = loader.loadService(currentLayer, Extension.class).stream()
                .anyMatch(SealedExtension.class::isInstance);

        // handle classes from parent with @ApplicationSingleton annotation
        // handle classes from parent with @ApplicationInstanceSingleton annotation
        if (isSealed) {
            if (parent != null) {
                classes.addAll(parent.getDeportedClasses());
            }
            classes.addAll(childrenDeportedClasses);
            childrenDeportedClasses.clear();
        } else {
            if (parent != null) {
                childrenDeportedClasses.addAll(parent.getDeportedClasses());
            }
        }

        if (parent != null) {
            classes.addAll(extensionLocalClasses);
        } else {
            // root extension is the only one to deport local classes
            final var partionedByDeportable = extensionLocalClasses.stream()
                    .collect(Collectors.partitioningBy(this::isDeportableClass));

            classes.addAll(partionedByDeportable.get(Boolean.FALSE));
            childrenDeportedClasses.addAll(partionedByDeportable.get(Boolean.TRUE));
        }

        ClassLoader classloader = currentLayer.getLoader();
        JfxAppContext context = contextManager.create(parentContextId, layerId, classloader, classes, childrenDeportedClasses, singletonInstances,
                progressListener);

        return context;
    }

    /**
     * true if the class is annotated with one of the deportable annotations.
     * A deportable class is a class that is not registered in the context but is
     * deported to a child context for loading.
     * @param cls the cls
     * @return true, if successful
     */
    private boolean isDeportableClass(Class<?> cls) {
        return deportableAnnotations.stream().anyMatch(a -> cls.getDeclaredAnnotationsByType(a).length > 0);
    }

    /**
     * Find exported classes.
     *
     * @param parentId    the parent id
     * @param extensionId the extension id
     * @return the sets the
     * @throws LayerNotFoundException    the layer not found exception
     * @throws InvalidExtensionException the invalid extension exception
     */
    private Set<Class<?>> findExportedClasses(ServiceLoader loader, UUID parentId, UUID extensionId)
            throws LayerNotFoundException, InvalidExtensionException {

        Layer layer = layerManager.get(extensionId);

        if (layer == null) {
            throw new LayerNotFoundException(extensionId, "Unable to find child layer for id %s");
        }

        try {
            return loader.loadService(layer, Extension.class).stream()
                    .filter(OpenExtension.class::isInstance)
                    .map(OpenExtension.class::cast)
                    .peek(e -> validateExtension(e, extensionId, parentId))
                    .flatMap(e -> e.exportedContextClasses().stream()).collect(Collectors.toSet());

        } catch (InvalidExtensionException.Unchecked e) {
            throw new InvalidExtensionException(e);
        }
    }

    private enum ExportType {
        EXPORTED, DEPORTED
    }
    private Map<ExportType, List<Class<?>>> findExportedClasses2(ServiceLoader loader, UUID parentId, UUID extensionId)
            throws LayerNotFoundException, InvalidExtensionException {

        Layer layer = layerManager.get(extensionId);

        if (layer == null) {
            throw new LayerNotFoundException(extensionId, "Unable to find child layer for id %s");
        }

        try {
            return loader.loadService(layer, Extension.class).stream()
                    .filter(OpenExtension.class::isInstance)
                    .map(OpenExtension.class::cast)
                    .peek(e -> validateExtension(e, extensionId, parentId))
                    .flatMap(e -> e.exportedContextClasses().stream())
                    .collect(Collectors.groupingBy(c -> isDeportableClass(c) ? ExportType.DEPORTED : ExportType.EXPORTED));

        } catch (InvalidExtensionException.Unchecked e) {
            throw new InvalidExtensionException(e);
        }
    }

    /**
     * Find local classes.
     *
     * @param parentId the parent id
     * @param layer    the layer
     * @return the sets the
     * @throws LayerNotFoundException    the layer not found exception
     * @throws InvalidExtensionException the invalid extension exception
     */
    private Set<Class<?>> findLocalClasses(ServiceLoader loader, UUID parentId, Layer layer)
            throws LayerNotFoundException, InvalidExtensionException {
        try {
            return loader.loadService(layer, Extension.class).stream()
                    .peek(e -> validateExtension(e, layer.getId(), parentId))
                    .peek(e -> e.initializeModule(layer))
                    .flatMap(e -> Stream.concat(Stream.of(e.getClass()), e.localContextClasses().stream()))
                    .collect(Collectors.toSet());
        } catch (InvalidExtensionException.Unchecked e) {
            throw new InvalidExtensionException(e);
        }
    }

    /**
     * Validate extension.
     *
     * @param extension the extension
     * @param id        the id
     * @param parentId  the parent id
     * @return true, if successful
     */
    private boolean validateExtension(Extension extension, UUID id, UUID parentId) {
        if (!ExtensionValidator.isValid(extension)) {
            throw new InvalidExtensionException.Unchecked(extension.toString());
        }
        if (!extension.getId().equals(id)) {
            String msg = "Invalid extension id expected : %s but was %s";
            msg = String.format(msg, id, extension.getId());
            throw new InvalidExtensionException.Unchecked(msg);
        }
        if (parentId != null && !extension.getParentId().equals(parentId)) {
            String msg = "Invalid extension parent id expected : %s but was %s";
            msg = String.format(msg, parentId, extension.getParentId());
            throw new InvalidExtensionException.Unchecked(msg);
        }
        return true;
    }

    /**
     * Clear.
     */
    public void clear() {
        contextManager.clear();
    }

    /**
     * Close.
     *
     * @param extension the extension
     */
    public void close(AbstractExtension<?> extension) {
        contextManager.close(extension.getId());
    }

    @FunctionalInterface
    public interface ServiceLoader {
        <T> Set<T> loadService(Layer layer, Class<T> serviceClass);
    }
}
