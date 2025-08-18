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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.collections.MapChangeListener.Change;

public class FilteredView {

    private final Folder root;

    private final boolean recursive;

    private final Predicate<File> fileFilter;

    private final Map<Folder, List<File>> files = new HashMap<>();

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

    private void handleFolder(Folder folder) {

        if (recursive) {
            folder.getFolders().values().forEach(this::handleFolder);
        }

        var folderFiles = folder.getFiles().values().stream()
                .filter(fileFilter)
                .toList();

        if (!folderFiles.isEmpty()) {
            files.put(folder, folderFiles);
        }
        folder.getFolders().addListener(this::handleFolderChange);
        folder.getFiles().addListener(this::handleFileChange);
    }

    private void handleFolderChange(Change<? extends Path,? extends Folder> change) {
        if (change.wasAdded()) {
            Folder newFolder = change.getValueAdded();
            handleFolder(newFolder);
        } else if (change.wasRemoved()) {
            Folder removedFolder = change.getValueRemoved();
            files.remove(removedFolder);
        }
    }

    private void handleFileChange(Change<? extends Path,? extends File> change) {
        if (change.wasAdded()) {
            File newFile = change.getValueAdded();
            if (fileFilter.test(newFile)) {
                files.computeIfAbsent(newFile.getParent(), k -> new ArrayList<>()).add(newFile);
            }
        } else if (change.wasRemoved()) {
            File removedFile = change.getValueRemoved();
            var list = files.get(removedFile.getParent());
            if (list != null) {
                list.remove(removedFile);
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

}
