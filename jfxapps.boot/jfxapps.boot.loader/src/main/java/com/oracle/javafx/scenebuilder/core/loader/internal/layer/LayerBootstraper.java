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
package com.oracle.javafx.scenebuilder.core.loader.internal.layer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.core.context.MultipleProgressListener;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.impl.InvalidLayerException;
import com.oracle.javafx.scenebuilder.core.loader.model.AbstractExtension;
import com.oracle.javafx.scenebuilder.core.loader.model.LoadState;

public class LayerBootstraper {

    private static final Logger logger = LoggerFactory.getLogger(LayerBootstraper.class);

    private final ModuleLayerManager layerManager;
    private final Path root;

    public LayerBootstraper(Path root, ModuleLayerManager layerManager) {
        super();
        this.layerManager = layerManager;
        this.root = root;
    }

    public Layer get(UUID layerId) {
        return layerManager.get(layerId);
    }

    public boolean exists(UUID layerId) {
        return layerManager.get(layerId) != null;
    }

    public Layer load(Layer parent, AbstractExtension<?> extension, MultipleProgressListener progresslistener) throws InvalidLayerException {

        progresslistener.notifyStart(extension.getId());

        Layer layer = null;
        try {
            if (extension.getLoadState() != LoadState.Deleted && extension.getLoadState() != LoadState.Disabled) {
                Path path = root.resolve(extension.getId().toString());

                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                ExtensionContentProvider contentProvider = extension.getContentProvider();

                if (!contentProvider.isValid()) {
                    extension.setLoadState(LoadState.Error);
                    throw new InvalidLayerException("ExtensionContentProvider contains invalid content " + contentProvider);
                }

                logger.info("Checking layer files are up to date {}", extension.getId());
                if (!contentProvider.isUpToDate(path)) {
                    logger.info("Updating layer files {}", extension.getId());
                    contentProvider.update(path);
                }
                logger.info("Layer files are up to date {}", extension.getId());

                layer = layerManager.create(parent, extension.getId(), path);
                if (layer != null) {
                    extension.setLoadState(LoadState.Loaded);
                }

            }
        } catch (Exception e) {
            extension.setLoadState(LoadState.Error);
            throw new InvalidLayerException("Unable to load extension " + extension.getId(), e);
        }

        return layer;
    }

//    public void clear() {
//        contexts.values().forEach(ConfigurableApplicationContext::close);
//        contexts.clear();
//    }
//
    public void close(UUID id) {
        try {
            layerManager.remove(id);
        } catch (IOException e) {
            logger.error("Unable to remove layer {}", id, e);
        }
    }
}
