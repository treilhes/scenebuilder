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
package com.gluonhq.jfxapps.app.devtools.modelv2;

import java.nio.file.Path;

/**
 * Represents a file in the filesystem watcher model.
 * This class encapsulates the properties and behaviors of a file,
 * including its location, type, and refresh capabilities.
 */
public class File {

    /**
     * Flag to enable or disable virtual threading for refresh operations.
     * If true, refresh operations will run on virtual threads; otherwise, they will run on the current thread.
     */
    private static final boolean ENABLE_VIRTUAL_THREADING = true;

    /**
     * The parent folder of this file.
     */
    private final Folder parent;

    /**
     * The location of the file in the filesystem.
     */
    private final Path location;
    /**
     * The type of the file, defining its extensions and creation logic.
     */
    private final FileType fileType;
    /**
     * Indicates whether the file is currently being refreshed.
     * This prevents multiple refresh requests from being processed simultaneously.
     */
    private boolean refreshing;

    /**
     * Constructs a File instance with the specified parent folder, location, and file type.
     *
     * @param parent the parent folder of this file
     * @param location the path to the file in the filesystem
     * @param fileType the type of the file, defining its extensions and creation logic
     */
    public File(Folder parent, Path location, FileType fileType) {
        super();
        this.parent = parent;
        this.location = location;
        this.fileType = fileType;
    }

    /**
     * Returns the parent folder of this file.
     *
     * @return the parent folder
     */
    public Path getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "File [location=" + location + "]";
    }

    /**
     * Returns the type of this file.
     *
     * @return the file type
     */
    public final void requestRefresh() {
        if (refreshing) {
            return; // already refreshing
        }
        refreshing = true;

        Runnable refreshTask = () -> {
            try {
                this.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                refreshing = false;
            }
        };

        if (ENABLE_VIRTUAL_THREADING) {
            Thread.startVirtualThread(refreshTask);
        } else {
            refreshTask.run();
        }
    }

    /**
     * Called when an update to the file has been done.
     * This method should be overridden to implement the actual internal state refresh logic.
     */
    public void refresh() {

    }

    /**
     * Called when the file is removed from the filesystem.
     * This method should be overridden to implement the actual removal logic.
     */
    public void onRemove() {

    }
}
