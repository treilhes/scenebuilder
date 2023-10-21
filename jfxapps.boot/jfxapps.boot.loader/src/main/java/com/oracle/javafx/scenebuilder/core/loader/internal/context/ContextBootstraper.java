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
package com.oracle.javafx.scenebuilder.core.loader.internal.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.core.context.MultipleProgressListener;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.EditorSingleton;
import com.oracle.javafx.scenebuilder.core.context.annotation.Window;
import com.oracle.javafx.scenebuilder.core.context.impl.ContextManager;
import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.core.loader.extension.ExtensionValidator;
import com.oracle.javafx.scenebuilder.core.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.core.loader.extension.SealedExtension;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.LayerNotFoundException;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.ModuleLayerManager;
import com.oracle.javafx.scenebuilder.core.loader.model.AbstractExtension;

public class ContextBootstraper {

    private static final Logger logger = LoggerFactory.getLogger(ContextBootstraper.class);

    private final ContextManager contextManager;
    private final ModuleLayerManager layerManager;

    public ContextBootstraper(ModuleLayerManager layerManager) {
        super();
        this.contextManager = new ContextManager();
        this.layerManager = layerManager;
    }

    public SbContext get(AbstractExtension<?> extension) {
        return contextManager.get(extension.getId());
    }

    public SbContext get(UUID extensionId) {
        return contextManager.get(extensionId);
    }

    public boolean exists(AbstractExtension<?> extension) {
        return contextManager.exists(extension.getId());
    }

    public SbContext create(SbContext parent, AbstractExtension<?> extension, List<Object> singletonInstances, MultipleProgressListener progressListener) throws InvalidExtensionException, LayerNotFoundException {
        UUID layerId = extension.getId();
        UUID parentContextId = parent == null ? null : parent.getId();

        Set<UUID> extensionIds = extension.getExtensions().stream().map(e -> e.getId()).collect(Collectors.toSet());

        Layer currentLayer = layerManager.get(layerId);

        if (currentLayer == null) {
            throw new LayerNotFoundException(layerId, "Unable to find layer for id %s");
        }

        Set<Class<?>> classes = new HashSet<>();
        Set<Class<?>> extensionLocalClasses = new HashSet<>();
        Set<Class<?>> childrenExportedClasses = new HashSet<>();

        for (UUID id:extensionIds) {
            try {
                childrenExportedClasses.addAll(findExportedClasses(layerId, id));
            } catch (LayerNotFoundException e) {
                logger.error("Unable to find layer for child extension {}", id, e);
            } catch (InvalidExtensionException e) {
                logger.error("Child extension is not valid {}", id, e);
            }
        }

        extensionLocalClasses.addAll(findLocalClasses(parentContextId, currentLayer));

        classes.addAll(childrenExportedClasses);
        classes.addAll(extensionLocalClasses);



        if (parent != null) { // get classes from parent with @EditorSingleton annotation
            boolean isSealed = currentLayer.extensions().stream().anyMatch(SealedExtension.class::isInstance);
            if (isSealed) {
                Set<Class<?>> deportedClasses = Arrays.stream(parent.getRegisteredClasses())
                    .filter(this::acceptClassInSealedExtension)
                    .collect(Collectors.toSet());
                classes.addAll(deportedClasses);
            }

        }

        Class<?>[] classesToRegister = classes.toArray(new Class[0]);

        SbContext context = contextManager.create(parentContextId, layerId, classesToRegister, singletonInstances, progressListener);

        return context;
    }

    private boolean acceptClassInSealedExtension(Class<?> cls) {
        if (cls.getDeclaredAnnotationsByType(EditorSingleton.class).length > 0) {
            return true;
        }
        if (cls.getDeclaredAnnotationsByType(Window.class).length > 0) {
            return true;
        }
        return false;
    }

    private Set<Class<?>> findExportedClasses(UUID parentId, UUID extensionId) throws LayerNotFoundException, InvalidExtensionException {

        Layer layer = layerManager.get(extensionId);

        if (layer == null) {
            throw new LayerNotFoundException(extensionId, "Unable to find child layer for id %s");
        }

        try {
            return layer.extensions().stream().filter(OpenExtension.class::isInstance)
                .map(OpenExtension.class::cast)
                .peek(e -> validateExtension(e, extensionId, parentId))
                .flatMap(e -> e.exportedContextClasses().stream())
                .collect(Collectors.toSet());

        } catch (InvalidExtensionException.Unchecked e) {
            throw new InvalidExtensionException(e);
        }
    }

    private Set<Class<?>> findLocalClasses(UUID parentId, Layer layer) throws LayerNotFoundException, InvalidExtensionException {
        try {
            return layer.extensions().stream().peek(e -> validateExtension(e, layer.getId(), parentId))
                    .flatMap(e -> e.localContextClasses().stream())
                    .collect(Collectors.toSet());
        } catch (InvalidExtensionException.Unchecked e) {
            throw new InvalidExtensionException(e);
        }
    }

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

    public void clear() {
        contextManager.clear();
    }

    public void close(AbstractExtension<?> extension) {
        contextManager.close(extension.getId());
    }
}
