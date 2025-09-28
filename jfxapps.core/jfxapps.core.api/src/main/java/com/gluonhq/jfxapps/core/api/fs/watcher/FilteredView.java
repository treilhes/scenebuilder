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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class FilteredView {

    private final Folder root;

    private final boolean recursive;

    private final Predicate<File> fileFilter;

    private final ObservableMap<Folder, ObservableList<File>> files = FXCollections.observableHashMap();

    private Consumer<File> onFileAdded;

    private Consumer<File> onFileRemoved;

    public FilteredView(Folder root, boolean recursive, Predicate<File> fileFilter) {
        Objects.requireNonNull(root, "Root folder cannot be null");
        this.root = root;
        this.recursive = recursive;
        this.fileFilter = fileFilter != null ? fileFilter : f -> true;
    }

    public void refresh() {
        files.clear();
        handleFolder(root);
    }

    /**
     * Process a folder: if recursive, process sub-folders, then filter files and
     * add them to the map if any. Finally, add listeners to folders and files
     * changes.
     *
     * @param folder the folder to process
     */
    private void handleFolder(Folder folder) {

        if (recursive) {
            folder.getFolders().values().forEach(this::handleFolder);
        }

        var folderFiles = folder.getFiles().values().stream()
                .filter(fileFilter)
                .collect(() -> {
                    ObservableList<File> newList = FXCollections.observableArrayList();
                    return newList;
                }, ObservableList::add, ObservableList::addAll);


        if (!folderFiles.isEmpty()) {
            files.put(folder, folderFiles);
        }
        folder.getFolders().addListener(this::handleFolderChange);
        folder.getFiles().addListener(this::handleFileChange);
    }

    /**
     * Handle changes in the folders of a folder: if a folder is added, process it
     * (handleFolder). If a folder is removed, remove listeners on it and its
     * sub-folders, and remove it from the map if it is present.
     *
     * @param change the change to handle
     */
    private void handleFolderChange(Change<? extends Path,? extends Folder> change) {
        if (change.wasAdded()) {
            Folder newFolder = change.getValueAdded();
            handleFolder(newFolder);
        } else if (change.wasRemoved()) {
            Folder removedFolder = change.getValueRemoved();

            if (recursive) {
                removeListenersOnFolder(removedFolder);
            }

            files.remove(removedFolder);
        }
    }

    private void removeListenersOnFolder(Folder removedFolder) {
        removedFolder.getFolders().removeListener(this::handleFolderChange);
        removedFolder.getFiles().removeListener(this::handleFileChange);

        if (recursive) {
            removedFolder.getFolders().values().forEach(this::removeListenersOnFolder);
        }
    }

    private void handleFileChange(Change<? extends Path,? extends File> change) {
        if (change.wasAdded()) {
            File newFile = change.getValueAdded();
            if (fileFilter.test(newFile)) {
                files.computeIfAbsent(newFile.getParent(), k -> FXCollections.observableArrayList()).add(newFile);
                if (onFileAdded != null) {
                    onFileAdded.accept(newFile);
                }
            }
        } else if (change.wasRemoved()) {
            File removedFile = change.getValueRemoved();
            var list = files.get(removedFile.getParent());
            if (list != null) {
                list.remove(removedFile);

                if (list.isEmpty()) {
                    files.remove(removedFile.getParent());
                }

                if (onFileRemoved != null) {
                    onFileRemoved.accept(removedFile);
                }
            }
        }
    }

    public Folder getRoot() {
        return root;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public Predicate<File> getFileFilter() {
        return fileFilter;
    }

    public ObservableMap<Folder, ObservableList<File>> getFiles() {
        return files;
    }

    public void onFileAdded(Consumer<File> consumer) {
        this.onFileAdded = consumer;
    }

    public void onFileRemoved(Consumer<File> consumer) {
        this.onFileRemoved = consumer;
    }

    public void close() {
        removeListenersOnFolder(root);
        files.clear();
    }
}
