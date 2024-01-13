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
package com.gluonhq.jfxapps.boot.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Layer must be successfully created from different type of Path
 *
 * Path objects can lead to:
 * - jar file
 * - folder containing classes (ex: maven target folder) if the META-INF folder exists
 * - folder containing jars and/or module folders
 */
public interface ModuleLayerManager {

    /**
     * Creates a new layer
     *
     * @param parent the parent
     * @param layerId the layer id
     * @param directory the directory
     * @return the layer
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InvalidLayerException the invalid layer exception
     */
    Layer create(Layer parent, UUID layerId, List<Path> paths, Path tempDirectory)
            throws IOException, InvalidLayerException;

    /**
     * Generated uuid
     * @param parent
     * @param paths
     * @param tempDirectory
     * @return
     * @throws IOException
     * @throws InvalidLayerException
     */
    Layer create(Layer parent, List<Path> paths, Path tempDirectory)
            throws IOException, InvalidLayerException;

    /**
     *
     * @param paths
     * @param tempDirectory
     * @return
     * @throws IOException
     * @throws InvalidLayerException
     */
    Layer create(List<Path> paths, Path tempDirectory) throws IOException, InvalidLayerException;

    /**
     * Gets the layer with the provided id
     *
     * @param layerId the layer id
     * @return the layer
     */
    Layer get(UUID id);

    /**
     * Removes the provided layer and its children.
     *
     * @param layer the layer
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    boolean remove(Layer layer) throws IOException;

    /**
     * Removes the layer identified by the provided id and its children
     * @param layerId
     * @return
     * @throws IOException
     */
    boolean remove(UUID layerId) throws IOException;

    /**
     * Removes all layers from the last created to the first
     * Any unremovable layer will stop the process leaving remaining layers and
     * the one failing in an unknown state
     * @return true, if successful
     */
    boolean removeAllLayers() throws IOException;

    void logLayers();



}
