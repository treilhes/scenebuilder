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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WatchEventHandlerImplTest {
    private static final String ENTRY_CREATE = "ENTRY_CREATE";
    private static final String ENTRY_MODIFY = "ENTRY_MODIFY";
    private static final String ENTRY_DELETE = "ENTRY_DELETE";
    private static final String OVERFLOW = "OVERFLOW";

    @TempDir
    private static Path watcherPath;
    private static Path dirPath;
    private static Path filePath;

    private WatchEventHandlerImpl handler;

    @BeforeAll
    public static void init() throws IOException {
        dirPath = Files.createDirectory(watcherPath.resolve("test"));
        filePath = Files.createFile(watcherPath.resolve("test.txt"));
    }

    @BeforeEach
    void setUp() {
        handler = new WatchEventHandlerImpl(watcherPath);
    }

    @Test
    void testAddOnFileCreated() {
        Consumer<Path> consumer = mock(Consumer.class);
        handler.addOnFileCreated(consumer);
        handler.handleEvent(createMockEvent(ENTRY_CREATE, filePath));
        verify(consumer, atLeastOnce()).accept(any(Path.class));
    }

    @Test
    void testAddOnFileModified() {
        Consumer<Path> consumer = mock(Consumer.class);
        handler.addOnFileModified(consumer);
        handler.handleEvent(createMockEvent(ENTRY_MODIFY, filePath));
        verify(consumer, atLeastOnce()).accept(any(Path.class));
    }

    @Test
    void testAddOnDirectoryCreated() {
        Consumer<Path> consumer = mock(Consumer.class);
        handler.addOnDirectoryCreated(consumer);
        handler.handleEvent(createMockEvent(ENTRY_CREATE, dirPath));
        verify(consumer, atLeastOnce()).accept(dirPath);
    }

    @Test
    void testAddOnDirectoryModified() {
        Consumer<Path> consumer = mock(Consumer.class);
        handler.addOnDirectoryModified(consumer);
        handler.handleEvent(createMockEvent(ENTRY_MODIFY, dirPath));
        verify(consumer, atLeastOnce()).accept(dirPath);
    }

    @Test
    void testAddOnDeleted() {
        Consumer<Path> consumer = mock(Consumer.class);
        handler.addOnDeleted(consumer);
        handler.handleEvent(createMockEvent(ENTRY_DELETE, "file.txt"));
        verify(consumer, atLeastOnce()).accept(any(Path.class));
    }

    @Test
    void testAddOnOverflow() {
        Consumer<Object> consumer = mock(Consumer.class);
        handler.addOnOverflow(consumer);
        handler.handleEvent(createMockEvent(OVERFLOW, "overflow"));
        verify(consumer, atLeastOnce()).accept(any());
    }

    // Helper to create a mock WatchEvent
    private WatchEvent<?> createMockEvent(String kindName, Object context) {
        Kind<Object> kind = mock(Kind.class);
        when(kind.name()).thenReturn(kindName);
        WatchEvent<Object> event = mock(WatchEvent.class);
        when(event.kind()).thenReturn(kind);
        when(event.context()).thenReturn(context instanceof Path ? context : Path.of(context.toString()));
        return event;
    }
}
