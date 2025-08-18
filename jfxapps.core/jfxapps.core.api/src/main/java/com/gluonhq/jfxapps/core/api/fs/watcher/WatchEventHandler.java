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

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.Consumer;

/**
 * Interface for handling file system watch events.
 * This interface allows for the handling of various types of file and directory events,
 * such as creation, modification, deletion, and overflow.
 */
public interface WatchEventHandler {

    /**
     * Handles a file system watch event.
     * @param event the watch event to handle
     */
    void handleEvent(WatchEvent<?> event);

    /**
     * Adds a consumer to handle file creation events.
     * @param consumer the consumer to handle the event
     * @return this WatchEventHandler instance for method chaining
     */
    WatchEventHandler addOnFileCreated(Consumer<Path> consumer);

    /**
     * Adds a consumer to handle file modification events.
     * @param consumer the consumer to handle the event
     * @return this WatchEventHandler instance for method chaining
     */
    WatchEventHandler addOnFileModified(Consumer<Path> consumer);

    /**
     * Adds a consumer to handle directory creation events.
     * @param consumer the consumer to handle the event
     * @return this WatchEventHandler instance for method chaining
     */
    WatchEventHandler addOnDirectoryCreated(Consumer<Path> consumer);

    /**
     * Adds a consumer to handle directory modification events.
     * @param consumer the consumer to handle the event
     * @return this WatchEventHandler instance for method chaining
     */
    WatchEventHandler addOnDirectoryModified(Consumer<Path> consumer);

    /**
     * Adds a consumer to handle file or directory deletion events.
     * @param consumer
     * @return
     */
    WatchEventHandler addOnDeleted(Consumer<Path> consumer);

    /**
     * Adds a consumer to handle overflow events, which occur when the watch service
     * cannot deliver all events due to system limitations.
     * @param consumer the consumer to handle the overflow event
     * @return this WatchEventHandler instance for method chaining
     */
    WatchEventHandler addOnOverflow(Consumer<Object> consumer);
}