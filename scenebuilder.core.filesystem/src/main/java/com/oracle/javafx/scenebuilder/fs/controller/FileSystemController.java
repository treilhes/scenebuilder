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
package com.oracle.javafx.scenebuilder.fs.controller;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.extension.DefaultFolders;
import com.oracle.javafx.scenebuilder.fs.preference.global.InitialDirectoryPreference;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.fs.util.FileWatcher;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public class FileSystemController implements FileWatcher.Delegate, FileSystem {

    private final static Logger log = Logger.getLogger(FileSystemController.class.getName());

    private final InitialDirectoryPreference initialDirectoryPreference;
    private final RecentItemsPreference recentItemsPreference;

    private final Map<DocumentWindow, List<Object>> documentWatchKeys = new HashMap<>();
    private final Map<Object, List<Path>> watchedFiles = new HashMap<>();
    private final Map<Path, List<WatchingCallback>> watchCallbacks = new HashMap<>();

    private final FileWatcher fileWatcher = new FileWatcher(2000 /* ms */, this,
            FileSystemController.class.getSimpleName());

    public FileSystemController(
            InitialDirectoryPreference initialDirectoryPreference,
            RecentItemsPreference recentItemsPreference) {
        this.initialDirectoryPreference = initialDirectoryPreference;
        this.recentItemsPreference = recentItemsPreference;
    }

    @Override
    public File getNextInitialDirectory() {
        return initialDirectoryPreference.getValue();
    }

    @Override
    public void updateNextInitialDirectory(File chosenFile) {
        assert chosenFile != null;

        final Path chosenFolder = chosenFile.toPath().getParent();
        if (chosenFolder != null) {
            initialDirectoryPreference.setValue(chosenFolder.toFile()).writeToJavaPreferences();
        }
    }

    @Override
    public void watch(DocumentWindow document, Set<Path> files, WatchingCallback callback) {
        List<File> fileList = files.stream().map(p -> p.toFile()).collect(Collectors.toList());
        watch(document, fileList, callback);
    }

    @Override
    public void watch(DocumentWindow document, List<File> files, WatchingCallback callback) {
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

                log.log(Level.INFO, "Watching file : {0}", p.toAbsolutePath());
            });
        }
    }

    @Override
    public void unwatch(Object key) {
        if (watchedFiles.containsKey(key)) {
            watchedFiles.get(key).forEach(p -> {
                List<WatchingCallback> callbacks = watchCallbacks.get(p);
                List<WatchingCallback> ownedCallbacks = callbacks.stream().filter(c -> c.getOwnerKey() == key).collect(Collectors.toList());

                callbacks.removeAll(ownedCallbacks);

                if (callbacks.isEmpty()) {
                    watchCallbacks.remove(p);
                    fileWatcher.removeTarget(p);
                }
            });
            watchedFiles.remove(key);
        }
    }


    @Override
    public void unwatchDocument(DocumentWindow document) {
        List<Object> keys = documentWatchKeys.get(document);
        if (keys != null) {
            keys.forEach(this::unwatch);
        }
        documentWatchKeys.remove(document);
    }

    @Override
    public void startWatcher() {
        log.log(Level.INFO, "Starting filewatcher !");
        fileWatcher.start();
    }

    @Override
    public void stopWatcher() {
        log.log(Level.INFO, "Stoping filewatcher !");
        fileWatcher.stop();
    }

    /*
     * FileWatcher.Delegate
     */
    @Override
    public void fileWatcherDidWatchTargetCreation(Path target) {
        log.log(Level.INFO, "File Event : file created ({0})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            log.log(Level.INFO, "File Event sent : file created ({0})", target.toFile().getName());
            watchCallbacks.get(target).forEach(c -> c.created(target));
        }
    }

    @Override
    public void fileWatcherDidWatchTargetDeletion(Path target) {
        log.log(Level.INFO, "File Event : file deleted ({0})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            log.log(Level.INFO, "File Event sent : file deleted ({0})", target.toFile().getName());
            watchCallbacks.get(target).forEach(c -> c.deleted(target));
        }
    }

    @Override
    public void fileWatcherDidWatchTargetModification(Path target) {
        log.log(Level.INFO, "File Event : file modified ({0})", target.toFile().getName());
        if (watchCallbacks.containsKey(target)) {
            log.log(Level.INFO, "File Event sent : file modified ({0})", target.toFile().getName());
            watchCallbacks.get(target).forEach(c -> c.modified(target));
        }
    }

    @Override
    public File getMessageBoxFolder() {
        return DefaultFolders.getMessageBoxFolder();
    }

    @Override
    public File getApplicationDataFolder() {
        return DefaultFolders.getApplicationDataFolder();
    }

}
