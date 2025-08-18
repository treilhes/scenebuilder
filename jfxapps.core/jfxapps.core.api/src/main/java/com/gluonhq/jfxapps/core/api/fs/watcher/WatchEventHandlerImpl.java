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
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.FXCollections;

public class WatchEventHandlerImpl implements WatchEventHandler {

    private final Path path;
    private final List<Consumer<Path>> onFileCreated = FXCollections.observableArrayList();
    private final List<Consumer<Path>> onFileModified = FXCollections.observableArrayList();

    private final List<Consumer<Path>> onDirectoryCreated = FXCollections.observableArrayList();
    private final List<Consumer<Path>> onDirectoryModified = FXCollections.observableArrayList();

    private final List<Consumer<Path>> onDeleted = FXCollections.observableArrayList();

    private final List<Consumer<Object>> onOverflow = FXCollections.observableArrayList();

    public WatchEventHandlerImpl(Path path) {
        super();
        this.path = path;
    }

    @Override
    public WatchEventHandler addOnFileCreated(Consumer<Path> consumer) {
        onFileCreated.add(consumer);
        return this;
    }

    @Override
    public WatchEventHandler addOnFileModified(Consumer<Path> consumer) {
        onFileModified.add(consumer);
        return this;
    }

    @Override
    public WatchEventHandler addOnDirectoryCreated(Consumer<Path> consumer) {
        onDirectoryCreated.add(consumer);
        return this;
    }

    @Override
    public WatchEventHandler addOnDirectoryModified(Consumer<Path> consumer) {
        onDirectoryModified.add(consumer);
        return this;
    }

    @Override
    public WatchEventHandler addOnDeleted(Consumer<Path> consumer) {
        onDeleted.add(consumer);
        return this;
    }

    @Override
    public WatchEventHandler addOnOverflow(Consumer<Object> consumer) {
        onOverflow.add(consumer);
        return this;
    }

    @Override
    public void handleEvent(WatchEvent<?> event) {
        Kind<?> kind = event.kind();
        switch (kind.name()) {
        case "ENTRY_CREATE" -> handleCreateEvent(path.resolve((Path) event.context()));
        case "ENTRY_DELETE" -> handleDeleteEvent(path.resolve((Path) event.context()));
        case "ENTRY_MODIFY" -> handleModifyEvent(path.resolve((Path) event.context()));
        default -> handleOverflowEvent(event.context());
        }

    }

    private void handleCreateEvent(Path context) {
        var isDirectory = Files.isDirectory(context);
        if (isDirectory) {
            handleCreateDirectoryEvent(context);
        } else {
            handleCreateFileEvent(context);
        }
    }

    private void handleCreateFileEvent(Path context) {
        onFileCreated.forEach(c -> c.accept(context));
    }

    private void handleCreateDirectoryEvent(Path context) {
        onDirectoryCreated.forEach(c -> c.accept(context));
    }

    private void handleModifyEvent(Path context) {
        var isDirectory = Files.isDirectory(context);
        if (isDirectory) {
            handleModifyDirectoryEvent(context);
        } else {
            handleModifyFileEvent(context);
        }
    }

    private void handleModifyFileEvent(Path context) {
        onFileModified.forEach(c -> c.accept(context));
    }

    private void handleModifyDirectoryEvent(Path context) {
        onDirectoryModified.forEach(c -> c.accept(context));
    }

    private void handleDeleteEvent(Path context) {
        // Determine if the deleted path was a directory or a file is not possible
        onDeleted.forEach(c -> c.accept(context));
    }


    private void handleOverflowEvent(Object context) {
        onOverflow.forEach(c -> c.accept(context));
    }

}
