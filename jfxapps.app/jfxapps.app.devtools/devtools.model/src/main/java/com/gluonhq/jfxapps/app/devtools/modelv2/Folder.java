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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Represents a folder in the file system, which can contain files and subfolders.
 * This class provides functionality to monitor changes in the folder, add inclusion/exclusion patterns,
 * and manage files and subfolders.
 */
public class Folder {

    private static final Logger logger = LoggerFactory.getLogger(Folder.class);
    /**
     * Flag to enable or disable virtual threading for folder refresh operations.
     * If set to true, folder refresh operations will run in a virtual thread.
     * If set to false, they will run in the current thread.
     */
    private static final boolean ENABLE_VIRTUAL_THREADING = true;

    /**
     * The parent folder of this folder.
     */
    private final Folder parent;
    /**
     * The location of the folder in the file system.
     */
    private final Path location;
    /**
     * The Watcher instance used to monitor file system events in this folder.
     */
    private final Watcher watcher;

    /**
     * The WatchEventHandlerImpl instance that handles watch key events for this folder.
     */
    private final WatchEventHandlerImpl watchKeyEventHandler;

    /**
     * The type of this folder, defining its behavior and content rules.
     */
    private final FolderType folderType;

    /**
     * List of inclusion patterns for files and folders in this folder.
     * Only files and folders matching these patterns will be processed.
     */
    private final List<Pattern> inclusionPattens = new ArrayList<>();
    /**
     * List of exclusion patterns for files and folders in this folder.
     * Files and folders matching these patterns will not be processed.
     */
    private final List<Pattern> exclusionPattens = new ArrayList<>();

    /**
     * Observable map of files in this folder, mapping file paths to File instances.
     * This allows for dynamic updates and monitoring of files in the folder.
     */
    private final ObservableMap<Path, File> files = FXCollections.observableHashMap();

    /**
     * Observable map of subfolders in this folder, mapping folder paths to Folder instances.
     * This allows for dynamic updates and monitoring of subfolders in the folder.
     */
    private final ObservableMap<Path, Folder> folders = FXCollections.observableHashMap();

    /**
     * Indicates whether the folder is currently being refreshed.
     * This prevents multiple refresh requests from being processed simultaneously.
     */
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

    /**
     * Returns the parent folder of this folder.
     *
     * @return the parent folder
     */
    public Path getLocation() {
        return location;
    }

    /**
     * Adds an inclusion pattern for files and folders in this folder.
     * Files and folders matching this pattern will be processed.
     *
     * @param pattern the inclusion pattern to add
     */
    public void addInclusionPattern(String pattern) {
        inclusionPattens.add(Pattern.compile(pattern));
    }

    /**
     * Adds an exclusion pattern for files and folders in this folder.
     * Files and folders matching this pattern will not be processed.
     *
     * @param pattern the exclusion pattern to add
     */
    public void addExclusionPattern(String pattern) {
        exclusionPattens.add(Pattern.compile(pattern));
    }

    /**
     * Returns the Watcher instance used to monitor file system events in this folder.
     * @return the Watcher instance
     */
    protected Watcher getWatcher() {
        return watcher;
    }


    /**
     * returns the WatchEventHandlerImpl instance that handles watch key events for this folder.
     * @return  the WatchEventHandlerImpl instance
     */
    protected WatchEventHandlerImpl getWatchKeyEventHandler() {
        return watchKeyEventHandler;
    }


    /**
     * Requests a refresh of the folder's contents.
     */
    public final void requestRefresh() {
        if (refreshing) {
            return; // already refreshing
        }
        refreshing = true;
        Runnable refreshTask = () -> {
            try {
                refresh();
            } catch (Exception e) {
                logger.error("Error refreshing folder: " + location, e);
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
     * Refreshes the contents of the folder by listing all files and directories in the location.
     * It processes each file and directory according to the inclusion and exclusion patterns.
     */
    public void refresh() {

        try {
            Files.list(location).forEach(p -> {
                if (canProcess(p)) {
                    WatchEvent<Path> event = toWatchEvent(p.getFileName());
                    watchKeyEventHandler.handleEvent(event);
                }
            });
        } catch (IOException e) {
            logger.error("Error listing files in folder: " + location, e);
        }
    }

    /**
     * Called when the folder is removed or deleted.
     * This method can be overridden to perform cleanup actions when the folder is no longer needed.
     */
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
