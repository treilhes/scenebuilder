/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.extstore.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;

public class ExtensionFileSystemImpl implements ExtensionFileSystem {
    
    private final static Logger logger = LoggerFactory.getLogger(ExtensionFileSystemImpl.class);
    
    private final String TEMP_FILE_EXTENSION = ".tmp"; //NOCHECK
    private final Path root;

    private final Dialog dialog;

    protected ExtensionFileSystemImpl(Path root, Dialog dialog) {
        super();
        this.root = root.toAbsolutePath();
        this.dialog = dialog;
    }

    @Override
    public List<Path> list() {
        try {
            return Files.list(root).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Unable to list the content of {}", root);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Path> list(String path) {
        Path target = root.resolve(path);
        
        if (isSecurePath(target)) {
            try {
                return Files.list(target).collect(Collectors.toList());
            } catch (IOException e) {
                logger.error("Unable to list the content of {}", target);
            }
        }
        
        return Collections.emptyList();
    }

    @Override
    public Path get(String path) {
        Path target = root.resolve(path);
        return get(target);
    }
    
    @Override
    public Path get(Path target) {
        if (isSecurePath(target)) {
            return target;
        } else {
            return null;
        }
    }
    
    @Override
    public boolean copy(List<Path> files, String destination) throws IOException {
        Path target = get(destination);
       
        if (target == null) {
            return false;
        }
        
        return copy(files, target);
    }
    
    @Override
    public boolean copy(List<Path> files, Path destination) throws IOException {
        if (destination == null || !isSecurePath(destination)) {
            return false;
        }
        
        if (!enoughFreeSpaceOnDisk(files)) {
            return false;
        }
        
        return copyFilesToDir(files, destination);
    }

    @Override
    public boolean delete(String destination) {
        Path target = get(destination);
        
        if (target == null) {
            return false;
        }
        
        try {
            return Files.deleteIfExists(target);
        } catch (IOException e) {
            logger.error("Unable to delete file {}", target, e);
            return false;
        }
    }

    @Override
    public boolean isCreated() {
        return Files.exists(root) && Files.isWritable(root);
    }
    
    @Override
    public boolean create() {
        try {
            Files.createDirectories(root, new FileAttribute<?>[]{});
            return true;
        } catch (IOException ioe) {
            dialog.showErrorAndWait(
                    I18N.getString("error.dir.create.title"),
                    I18N.getString("error.dir.create.message", root.normalize().toString()),
                    I18N.getString("error.write.details"),
                    ioe);
            return false;
        }
    }
    
    // Each copy is done via an intermediate temporary file that is renamed if
    // the copy goes well (for atomicity). If a copy fails we try to erase the
    // temporary file to stick to an as clean as possible disk content.
    // TODO fix DTL-5879 [When copying FXML files in lib dir we have to copy files which are external references as well]
    private boolean copyFilesToDir(List<Path> files, Path destination) throws IOException {
        Map<Path, Exception> results = new HashMap<>();
        Path tempTargetPath = null;
        Exception ex = null;
        Path exFile = null;
        for (Path file : files) {
            try {
                tempTargetPath = Paths.get(root.toString(), file.getFileName() + TEMP_FILE_EXTENSION);
                Path ultimateTargetPath = destination.resolve(file.getFileName());
                Files.deleteIfExists(tempTargetPath);
                Files.copy(file, tempTargetPath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(tempTargetPath, ultimateTargetPath, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception e) {
                logger.error("Unable to copy file {}", file, e);
                results.put(file, e);
                ex = e;
                exFile = file;
            } finally {
                if (tempTargetPath != null) {
                    Files.deleteIfExists(tempTargetPath);
                }
            }
        } 

        int errorCount = results.size();
        if (errorCount > 0) {
            dialog.showErrorAndWait(
                    I18N.getString("error.copy.title"),
                    errorCount == 1 ?
                            I18N.getString("error.copy.message.single", exFile, destination):
                            I18N.getString("error.copy.message.multiple", errorCount, destination),
                    I18N.getString("error.write.details"),
                    errorCount == 1 ? ex : null);
        }
        return results.isEmpty();
    }

    @Override
    public void createDirectoryIfNotExists(Path path) {
        if (isSecurePath(path)) {
            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException ioe) {
                    logger.error("Unable to create directory {}", path);
                    dialog.showErrorAndWait(
                            I18N.getString("error.dir.create.title"),
                            I18N.getString("error.dir.create.message", path.normalize().toString()),
                            I18N.getString("error.write.details"),
                            ioe);
                }
            }
        }
        
    }

    @Override
    public boolean existsDirectory(Path path) {
        if (isSecurePath(path)) {
            return Files.exists(path) && Files.isDirectory(path);
        } else {
            return false;
        }
    }

    private boolean isSecurePath(Path target) {
        if (!target.toAbsolutePath().startsWith(root)) {
            logger.error("Path is out of reach! {}", target);
            return false;
        } else {
            return true;
        }
    }
    
    protected boolean enoughFreeSpaceOnDisk(List<Path> files) {
        try {
            return FileSystem.enoughFreeSpaceOnDisk(files, root);
        } catch (IOException ioe) {
            dialog.showErrorAndWait(
                    I18N.getString("error.disk.space.title"),
                    I18N.getString("error.disk.space.message"),
                    I18N.getString("error.write.details"),
                    ioe);
        }
        return false;
    }
}
