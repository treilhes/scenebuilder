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
package com.gluonhq.jfxapps.core.fs.controller;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fs.FileSystem.WatchingCallback;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.fs.util.FileWatcher;

@ApplicationInstanceSingleton
public class FileWatchController implements FileWatcher.Delegate {

    private final static Logger logger = LoggerFactory.getLogger(FileWatchController.class);

    private final Map<MainInstanceWindow, List<Object>> documentWatchKeys = new HashMap<>();
    private final Map<Object, List<Path>> watchedFiles = new HashMap<>();
    private final Map<Path, List<WatchingCallback>> watchCallbacks = new HashMap<>();

    private final FileWatcher fileWatcher = new FileWatcher(2000 /* ms */, this,
            FileSystemController.class.getSimpleName());

    private final JfxAppPlatform jfxAppPlatform;


    // @formatter:off
    public FileWatchController(
            JfxAppPlatform jfxAppPlatform) {
     // @formatter:on
        this.jfxAppPlatform = jfxAppPlatform;
    }

    public void watch(MainInstanceWindow document, Set<Path> files, WatchingCallback callback) {
        List<File> fileList = files.stream().map(p -> p.toFile()).collect(Collectors.toList());
        watch(document, fileList, callback);
    }

    public void watch(MainInstanceWindow document, List<File> files, WatchingCallback callback) {
        Object key = callback.getOwnerKey();

        List<Object> documentKeys = documentWatchKeys.get(document);

        if (documentKeys == null) {
            documentKeys = new ArrayList<>();
            documentWatchKeys.put(document, documentKeys);
        }

        if (!documentKeys.contains(key)) {
            documentKeys.add(key);
        }

        if (files != null && !files.isEmpty()) {
            List<Path> paths = files.stream().filter(f -> f != null && f.exists()).map(f -> f.toPath())
                    .collect(Collectors.toList());

            watchedFiles.put(key, paths);

            paths.forEach(p -> {
                if (!fileWatcher.hasTarget(p)) {
                    fileWatcher.addTarget(p);
                }

                List<WatchingCallback> callbacks = watchCallbacks.get(p);

                if (callbacks == null) {
                    callbacks = new ArrayList<>();
                    watchCallbacks.put(p, callbacks);
                }

                if (!callbacks.contains(callback)) {
                    callbacks.add(callback);
                }

                logger.info("Watching file : {}", p.toAbsolutePath());
            });
        }
    }

    public void unwatch(Object key) {
        if (watchedFiles.containsKey(key)) {
            watchedFiles.get(key).forEach(p -> {
                List<WatchingCallback> callbacks = watchCallbacks.get(p);
                List<WatchingCallback> ownedCallbacks = callbacks.stream().filter(c -> c.getOwnerKey() == key)
                        .collect(Collectors.toList());

                callbacks.removeAll(ownedCallbacks);

                if (callbacks.isEmpty()) {
                    watchCallbacks.remove(p);
                    fileWatcher.removeTarget(p);
                }
            });
            watchedFiles.remove(key);
        }
    }

    public void unwatchDocument(MainInstanceWindow document) {
        List<Object> keys = documentWatchKeys.get(document);
        if (keys != null) {
            keys.forEach(this::unwatch);
        }
        documentWatchKeys.remove(document);
    }

    @Override
    public void startWatcher() {
        logger.info("Starting filewatcher !");
        fileWatcher.start();
    }

    public void stopWatcher() {
        logger.info("Stoping filewatcher !");
        fileWatcher.stop();
    }

    /*
     * FileWatcher.Delegate
     */
    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetCreation(Path target) {
        logger.info("File Event : file created ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file created ({})", target.toFile().getName());
            jfxAppPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.created(target)));
        }
    }

    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetDeletion(Path target) {
        logger.info("File Event : file deleted ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file deleted ({})", target.toFile().getName());
            jfxAppPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.deleted(target)));
        }
    }

    // FIXME SbPlatform.runForDocumentLater is misused here, what about watcher from
    // other documents ?
    @Override
    public void fileWatcherDidWatchTargetModification(Path target) {
        logger.info("File Event : file modified ({})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            logger.info("File Event sent : file modified ({})", target.toFile().getName());
            jfxAppPlatform.runOnFxThreadWithActiveScope(() -> watchCallbacks.get(target).forEach(c -> c.modified(target)));
        }
    }
}
