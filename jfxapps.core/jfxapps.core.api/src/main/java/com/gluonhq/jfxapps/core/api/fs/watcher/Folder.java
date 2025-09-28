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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;

/**
 * Represents a folder in the file system, which can contain files and subfolders.
 * This class provides functionality to monitor changes in the folder, add inclusion/exclusion patterns,
 * and manage files and subfolders.
 */
public class Folder extends FileSystemItem<Folder, FolderFeature, FolderFeatureHandler> {

    private static final Logger logger = LoggerFactory.getLogger(Folder.class);

    public static boolean any(Path path) {
        return true;
    }

    public static boolean isNamed(Path path, String folderName) {
        return path != null && path.getFileName().toString().equals(folderName);
    }
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
     * Observable map of files in this folder, mapping file paths to File instances.
     * This allows for dynamic updates and monitoring of files in the folder.
     */
    private final ObservableMap<Path, File> files = FXCollections.observableHashMap();

    /**
     * Observable map of subfolders in this folder, mapping folder paths to Folder instances.
     * This allows for dynamic updates and monitoring of subfolders in the folder.
     */
    private final ObservableMap<Path, Folder> folders = FXCollections.observableHashMap();

    private final ListChangeListener<FileType> fileTypeListener = change -> {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(fileType -> {
                    logger.debug("Adding file type: {}", fileType);
                    this.onFileTypeAdded(fileType);
                });
            }
            if (change.wasRemoved()) {
                change.getRemoved().forEach(fileType -> {
                    logger.debug("Removing file type: {}", fileType);
                    this.onFileTypeRemoved(fileType);
                });
            }
        }
    };

    private final ListChangeListener<FolderType> folderTypeListener = change -> {

        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(folderType -> {
                    logger.debug("Adding folder type: {}", folderType);
                    this.onFolderTypeAdded(folderType);
                });
            }
            if (change.wasRemoved()) {
                change.getRemoved().forEach(folderType -> {
                    logger.debug("Removing folder type: {}", folderType);
                    this.onFolderTypeRemoved(folderType);
                });
            }
        }

    };
    /**
     * Creates a Folder instance that monitors the specified location for changes.
     *
     * @param watcher the Watcher instance to use for monitoring file system events
     * @param location the File location to monitor
     * @param contentRules the FolderType rules to apply for content management, can be null
     */
    public Folder(Watcher watcher, Folder parent, Path location, List<FolderFeatureHandler> handlers, FolderType folderType) {
        super(parent, location, handlers);

        Objects.requireNonNull(watcher, "watcher can't be null");
        Objects.requireNonNull(folderType, "folderType can't be null");

        if (!Files.exists(location) || !Files.isDirectory(location)) {
            throw new IllegalArgumentException("Workspace location does not exist or isn't a directory: " + location);
        }

        this.watcher = watcher;
        this.watchKeyEventHandler = new WatchEventHandlerImpl(location);
        this.folderType = folderType != null ? folderType : FolderType.generic();

        watchKeyEventHandler.addOnDirectoryCreated(this::addDirectory);
        watchKeyEventHandler.addOnFileCreated(this::addFile);
        watchKeyEventHandler.addOnFileModified(this::refreshFile);
        watchKeyEventHandler.addOnDeleted(this::remove);

        watcher.registerEventHandler(location, watchKeyEventHandler);
        folderType.addFileTypeListener(fileTypeListener);
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
     * Refreshes the contents of the folder by listing all files and directories in the location.
     * It processes each file and directory according to the inclusion and exclusion patterns.
     */
    @Override
    public void refresh() {

        try {
            Files.list(getPath()).forEach(p -> {
                if (canProcess(p)) {
                    WatchEvent<Path> event = toWatchEvent(p.getFileName());
                    watchKeyEventHandler.handleEvent(event);
                }
            });
        } catch (IOException e) {
            logger.error("Error listing files in folder: " + getPath(), e);
        }
    }

    /**
     * Called when the folder is removed or deleted.
     * This method can be overridden to perform cleanup actions when the folder is no longer needed.
     */
    @Override
    public void onRemove() {

    }

    private void addDirectory(Path path) {
        if (!canProcess(path)) {
            return;
        }

        var type = folderType.getFactory().findFolderType(path);

        if (type.isEmpty()) {
            logger.trace("No folder type found for path: {} by folder of : {}", path, getPath());
            return;
        }

        var folder = type.get().createFolder(getWatcher(), this, path);
        if (folder != null && !folders.containsKey(path)) {
            folders.put(path, folder);
            logger.debug("Adding folder: {}", path);
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
            logger.debug("Adding file: {}", path);
            file.requestRefresh();
        }

    }

    private void refreshFile(Path path) {
        if (!canProcess(path)) {
            return;
        }

        var file = files.get(path);

        if (file != null) {
            logger.debug("Refreshing file: {}", path);
            file.requestRefresh();
        }

    }

    private void remove(Path path) {
        var folder = folders.remove(path);
        if (folder == null) {
            var file = files.remove(path);
            if (file != null) {
                logger.debug("Removing file: {}", path);
                file.onRemove();
            }
        } else {
            logger.debug("Removing folder: {}", path);
            folder.onRemoveInternal();
        }
    }

    @Override
    protected void onRemoveInternal() {
        folderType.removeFileTypeListener(fileTypeListener);
        super.onRemoveInternal();
    }

    private boolean canProcess(Path path) {
        var inclusionPatterns = folderType.getInclusionPatterns();
        var exclusionPatterns = folderType.getExclusionPatterns();

        boolean canProcess = inclusionPatterns.isEmpty();

        String lastSegment = path.getFileName().toString();

        for (Pattern pattern : inclusionPatterns) {
            if (pattern.matcher(lastSegment).matches()) {
                logger.trace("Including path: {} due to pattern: {}", path, pattern);
                canProcess = true;
                break;
            }
        }

        for (Pattern pattern : exclusionPatterns) {
            if (pattern.matcher(lastSegment).matches()) {
                logger.trace("Excluding path: {} due to pattern: {}", path, pattern);
                canProcess = false;
                break;
            }
        }

        return canProcess;
    }


    private void onFileTypeRemoved(FileType fileType) {
        files.entrySet().removeIf(e -> e.getValue().getFileType() == fileType);
    }

    private void onFileTypeAdded(FileType fileType) {
        try {
            Files.list(getPath())
                .filter(Files::isRegularFile)
                .filter(p -> !files.containsKey(p))
                .filter(fileType::isApplicable)
                .map(p -> fileType.createFile(this, p))
                .forEach(f -> files.put(f.getPath(), f));
        } catch (IOException e) {
            logger.error("Error while creating files from new fileType {} in folder: {}", fileType.getId(), getPath(),
                    e);
        }
    }

    private void onFolderTypeRemoved(FolderType folderType) {
        folders.entrySet().removeIf(e -> e.getValue().getFolderType() == folderType);
    }

    private void onFolderTypeAdded(FolderType folderType) {
        try {
            Files.list(getPath())
                .filter(Files::isDirectory)
                .filter(p -> !folders.containsKey(p))
                .filter(folderType::isDefinitionApplicable)
                .map(p -> folderType.createFolder(watcher, this, p))
                .forEach(f -> folders.put(f.getPath(), f));
        } catch (IOException e) {
            logger.error("Error while creating folders from new folderType {} in folder: {}", folderType.getId(), getPath(),
                    e);
        }
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
        return "Folder [location=" + getPath() + "]";
    }

    protected ObservableMap<Path, File> getFiles() {
        return files;
    }

    protected ObservableMap<Path, Folder> getFolders() {
        return folders;
    }

    public FolderType getFolderType() {
        return folderType;
    }

}
