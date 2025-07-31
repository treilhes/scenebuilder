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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Folder {

    private static final boolean ENABLE_VIRTUAL_THREADING = true;
    private final Folder parent;
    private final Path location;
    private final Watcher watcher;
    private final WatchEventHandlerImpl watchKeyEventHandler;

    private final FolderType folderType;

    private final List<Pattern> inclusionPattens = new ArrayList<>();
    private final List<Pattern> exclusionPattens = new ArrayList<>();

    private final ObservableMap<Path, File> files = FXCollections.observableHashMap();
    private final ObservableMap<Path, Folder> folders = FXCollections.observableHashMap();
    private boolean refreshing;

    /**
     * Creates a Folder instance that monitors the specified location for changes.
     *
     * @param watcher the Watcher instance to use for monitoring file system events
     * @param location the File location to monitor
     * @param contentRules the FolderType rules to apply for content management, can be null
     */
    public Folder(Watcher watcher, Folder parent, Path location, FolderType folderType) {
        super();
        if (!Files.exists(location) && !Files.isDirectory(location)) {
            throw new IllegalArgumentException("Workspace location does not exist or isn't a directory: " + location);
        }
        this.parent = parent;
        this.location = location;
        this.watcher = watcher;
        this.watchKeyEventHandler = new WatchEventHandlerImpl(location);
        this.folderType = folderType != null ? folderType : FolderType.Default.INSTANCE;

        watcher.registerEventHandler(location, watchKeyEventHandler);

        watchKeyEventHandler.addOnDirectoryCreated(System.out::println);
        watchKeyEventHandler.addOnFileCreated(System.out::println);
        watchKeyEventHandler.addOnDeleted(System.out::println);

        watchKeyEventHandler.addOnDirectoryCreated(this::addDirectory);
        watchKeyEventHandler.addOnFileCreated(this::addFile);
        watchKeyEventHandler.addOnFileModified(this::refreshFile);
        watchKeyEventHandler.addOnDeleted(this::remove);

    }

    public Path getLocation() {
        return location;
    }

    public void addInclusionPattern(String pattern) {
        inclusionPattens.add(Pattern.compile(pattern));
    }

    public void addExclusionPattern(String pattern) {
        exclusionPattens.add(Pattern.compile(pattern));
    }

    protected Watcher getWatcher() {
        return watcher;
    }

    protected WatchEventHandlerImpl getWatchKeyEventHandler() {
        return watchKeyEventHandler;
    }


    public final void requestRefresh() {
        if (refreshing) {
            return; // already refreshing
        }
        refreshing = true;
        Runnable refreshTask = () -> {
            try {
                refresh();
            } catch (Exception e) {
                // TODO Auto-generated catch block
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

    public void refresh() {

        try {
            Files.list(location).forEach(p -> {
                if (canProcess(p)) {
                    WatchEvent<Path> event = toWatchEvent(p.getFileName());
                    watchKeyEventHandler.handleEvent(event);
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onRemove() {

    }

    private void addDirectory(Path path) {
        if (!canProcess(path)) {
            return;
        }

        var type = folderType.getFactory().findFolderType(path);
        var folder = type.createFolder(getWatcher(), this, path);
        if (folder != null && !folders.containsKey(path)) {
            folders.put(path, folder);
            folder.refresh();
        }
    }

    private void addFile(Path path) {
        if (!canProcess(path)) {
            return;
        }

        var file = folderType.createFile(this, path);

        if (file != null && !files.containsKey(path)) {
            files.put(path, file);
            file.requestRefresh();
        }

    }

    private void refreshFile(Path path) {
        if (!canProcess(path)) {
            return;
        }

        var file = files.get(path);

        if (file != null) {
            file.requestRefresh();
        }

    }

    private void remove(Path path) {
        var folder = folders.remove(path);
        if (folder == null) {
            var file = files.remove(path);
            if (file != null) {
                file.onRemove();
            }
        } else {
            folder.onRemove();
        }
    }

    private boolean canProcess(Path path) {
        boolean canProcess = inclusionPattens.isEmpty();

        String lastSegment = path.getFileName().toString();

        for (Pattern pattern : inclusionPattens) {
            if (pattern.matcher(lastSegment).matches()) {
                canProcess = true;
                break;
            }
        }

        for (Pattern pattern : exclusionPattens) {
            if (pattern.matcher(lastSegment).matches()) {
                canProcess = false;
                break;
            }
        }

        return canProcess;
    }
    private WatchEvent<Path> toWatchEvent(Path p) {
        WatchEvent<Path> event = new WatchEvent<>() {
            @Override
            public Kind<Path> kind() {
                return StandardWatchEventKinds.ENTRY_CREATE;
            }

            @Override
            public int count() {
                return 1;
            }

            @Override
            public Path context() {
                return p;
            }
        };
        return event;
    }

    @Override
    public String toString() {
        return "Folder [location=" + location + "]";
    }


}
