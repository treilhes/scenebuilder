/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.fs.watcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Represents a file in the filesystem watcher model.
 * This class encapsulates the properties and behaviors of a file,
 * including its location, type, and refresh capabilities.
 */
public class File extends FileSystemItem<File, FileFeature, FileFeatureHandler> {

    /**
     * The type of the file, defining its extensions and creation logic.
     */
    private final FileType fileType;

    private volatile Map<Class<?>, Object> contentCache = new ConcurrentHashMap<>();

    /**
     * Constructs a File instance with the specified parent folder, location, and file type.
     *
     * @param parent the parent folder of this file
     * @param location the path to the file in the filesystem
     * @param fileType the type of the file, defining its extensions and creation logic
     */
    public File(Folder parent, Path location, List<FileFeatureHandler> handlers, FileType fileType) {
        super(parent, location, handlers);
        Objects.requireNonNull(parent, "fileType can't be null");
        if (!Files.exists(location) || !Files.isRegularFile(location)) {
            throw new IllegalArgumentException("location does not exist or isn't a file: " + location);
        }
        this.fileType = fileType;
    }


    @Override
    public String toString() {
        return "File [location=" + getPath() + "]";
    }

    /**
     * Called when an update to the file has been done.
     * This method should be overridden to implement the actual internal state refresh logic.
     */
    @Override
    public void refresh() {

    }

    /**
     * Called when the file is removed from the filesystem.
     * This method should be overridden to implement the actual removal logic.
     */
    @Override
    public void onRemove() {

    }

    public FileType getFileType() {
        return fileType;
    }

    public <T> T getContent(Class<T> contentType, Function<File, T> contentSupplier) {
        var result = contentCache.computeIfAbsent(contentType, k -> contentSupplier.apply(this));
        return contentType.cast(result);
    }

    /**
     * Clears the content cache of this file.
     * This method can be used to reset the cached content, forcing a reload on the next request.
     */
    public void clearCache() {
        contentCache.clear();
    }
}
