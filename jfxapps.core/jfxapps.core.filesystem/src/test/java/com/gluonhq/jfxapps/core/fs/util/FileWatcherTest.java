/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.fs.util;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.gluonhq.jfxapps.core.fs.util.FileWatcher;

class FileWatcherTest {

    private static long TEST_TIMEOUT = 1000L;

    private static long POLLING_TIME = 500L;

    private static String WATCHER_NAME = "test";

    private static class FileWatcherDelegate implements FileWatcher.Delegate {

        protected CountDownLatch lock;

        public FileWatcherDelegate(CountDownLatch lock) {
            super();
            this.lock = lock;
        }

        @Override
        public void startWatcher() {}

        @Override
        public void fileWatcherDidWatchTargetModification(Path target) {}

        @Override
        public void fileWatcherDidWatchTargetDeletion(Path target) {}

        @Override
        public void fileWatcherDidWatchTargetCreation(Path target) {}
    };

    @Test
    void should_detect_file_creation(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");

        CountDownLatch lock = new CountDownLatch(1);
        FileWatcherDelegate delegate = new FileWatcherDelegate(lock) {
            @Override
            public void fileWatcherDidWatchTargetCreation(Path target) {
                lock.countDown();
            }
        };
        delegate = Mockito.spy(delegate);

        FileWatcher fw = new FileWatcher(POLLING_TIME, delegate, WATCHER_NAME);
        fw.start();
        fw.addTarget(file);

        Files.writeString(file, "somedata");

        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);

        Mockito.verify(delegate).fileWatcherDidWatchTargetCreation(file);
        fw.stop();
    }

    @Test
    void should_detect_file_modification(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "somedata");
        CountDownLatch lock = new CountDownLatch(1);

        FileWatcherDelegate delegate = new FileWatcherDelegate(lock) {
            @Override
            public void fileWatcherDidWatchTargetModification(Path target) {
                lock.countDown();
            }
        };
        delegate = Mockito.spy(delegate);

        FileWatcher fw = new FileWatcher(POLLING_TIME, delegate, WATCHER_NAME);
        fw.start();
        fw.addTarget(file);

        Files.writeString(file, "somemodifieddata");

        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);

        Mockito.verify(delegate).fileWatcherDidWatchTargetModification(file);
        fw.stop();
    }

    @Test
    void should_detect_file_deletion(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "somedata");
        CountDownLatch lock = new CountDownLatch(1);

        FileWatcherDelegate delegate = new FileWatcherDelegate(lock) {
            @Override
            public void fileWatcherDidWatchTargetDeletion(Path target) {
                lock.countDown();
            }
        };
        delegate = Mockito.spy(delegate);

        FileWatcher fw = new FileWatcher(POLLING_TIME, delegate, WATCHER_NAME);
        fw.start();
        fw.addTarget(file);

        Files.delete(file);

        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);

        Mockito.verify(delegate).fileWatcherDidWatchTargetDeletion(file);
        fw.stop();
    }


    @Test
    void should_not_detect_any_action_when_stopped(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");

        CountDownLatch lock = new CountDownLatch(1);
        FileWatcherDelegate delegate = new FileWatcherDelegate(null);
        delegate = Mockito.spy(delegate);

        FileWatcher fw = new FileWatcher(POLLING_TIME, delegate, WATCHER_NAME);
        fw.start();
        fw.addTarget(file);

        fw.stop();

        Files.writeString(file, "somedata");
        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
        Files.writeString(file, "somemodifieddata");
        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
        Files.delete(file);
        lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS);

        Mockito.verify(delegate, never()).fileWatcherDidWatchTargetCreation(file);
        Mockito.verify(delegate, never()).fileWatcherDidWatchTargetModification(file);
        Mockito.verify(delegate, never()).fileWatcherDidWatchTargetDeletion(file);

    }

    @Test
    void should_correctly_list_targets(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Path file2 = tempDir.resolve("test2.txt");
        Path file3 = tempDir.resolve("test3.txt");

        FileWatcherDelegate delegate = new FileWatcherDelegate(null);

        FileWatcher fw = new FileWatcher(POLLING_TIME, delegate, WATCHER_NAME);
        fw.addTarget(file);

        assertTrue(fw.getTargets().contains(file));
        assertTrue(fw.hasTarget(file));

        fw.removeTarget(file);

        assertFalse(fw.getTargets().contains(file));
        assertFalse(fw.hasTarget(file));

        fw.addTarget(file);

        fw.setTargets(List.of(file2,file3));

        assertFalse(fw.hasTarget(file));
        assertTrue(fw.hasTarget(file2));
        assertTrue(fw.hasTarget(file3));
    }

}
