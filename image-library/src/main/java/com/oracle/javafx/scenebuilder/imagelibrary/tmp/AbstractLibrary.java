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
package com.oracle.javafx.scenebuilder.imagelibrary.tmp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryStoreConfiguration;

public abstract class AbstractLibrary implements LibraryStoreConfiguration {
    
    private final String TEMP_FILE_EXTENSION = ".tmp"; //NOI18N
    
    private final String libraryName;
    private final FileSystem fileSystem;
    private final Dialog dialog;
    
    private String userLibraryPathString;
    
    
    
    
    public AbstractLibrary(String libraryName, FileSystem fileSystem, Dialog dialog) {
        super();
        this.libraryName = libraryName;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
        
        setLibraryPathString();
    }

    private void setLibraryPathString() {
        if (userLibraryPathString == null) {
            userLibraryPathString = fileSystem.getUserLibraryFolder().getAbsolutePath() + File.separator + libraryName;
            assert userLibraryPathString != null;
        }
    }

    protected List<File> getSubsetOfFiles(String pattern, List<File> files) {
        final List<File> res = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith(pattern)) {
                res.add(file);
            }
        }

        return res;
    }

    protected boolean createUserLibraryDir(Path libPath) {
        boolean dirCreated = false;
        try {
            // Files.createDirectories do nothing if provided Path already exists.
            Files.createDirectories(libPath, new FileAttribute<?>[]{});
            dirCreated = true;
        } catch (IOException ioe) {
            dialog.showErrorAndWait(
                    I18N.getString("error.dir.create.title"),
                    I18N.getString("error.dir.create.message", libPath.normalize().toString()),
                    I18N.getString("error.write.details"),
                    ioe);
        }

        return dirCreated;
    }

    protected boolean enoughFreeSpaceOnDisk(List<File> files) {
        try {
            return fileSystem.enoughFreeSpaceOnDisk(files);
        } catch (IOException ioe) {
            dialog.showErrorAndWait(
                    I18N.getString("error.disk.space.title"),
                    I18N.getString("error.disk.space.message"),
                    I18N.getString("error.write.details"),
                    ioe);
        }
        return false;
    }

    // Each copy is done via an intermediate temporary file that is renamed if
    // the copy goes well (for atomicity). If a copy fails we try to erase the
    // temporary file to stick to an as clean as possible disk content.
    // TODO fix DTL-5879 [When copying FXML files in lib dir we have to copy files which are external references as well]
    protected void copyFilesToUserLibraryDir(List<File> files) {
        int errorCount = 0;
        IOException savedIOE = null;
        String savedFileName = ""; //NOI18N
        Path tempTargetPath = null;
        setLibraryPathString();

        // Here we deactivate the UserLib so that it unlocks the files contained
        // in the lib dir in the file system meaning (especially on Windows).
        //((UserLibrary) userLibrary).stopWatching();
        //releaseLocks();

        try {
            for (File file : files) {
                savedFileName = file.getName();
                tempTargetPath = Paths.get(userLibraryPathString, file.getName() + TEMP_FILE_EXTENSION);
                Path ultimateTargetPath = Paths.get(userLibraryPathString, file.getName());
                Files.deleteIfExists(tempTargetPath);
                Files.copy(file.toPath(), tempTargetPath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(tempTargetPath, ultimateTargetPath, StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (IOException ioe) {
            errorCount++;
            savedIOE = ioe;
        } finally {
            if (tempTargetPath != null) {
                try {
                    Files.deleteIfExists(tempTargetPath);
                } catch (IOException ioe) {
                    errorCount++;
                    savedIOE = ioe;
                }
            }
        }

        //((UserLibrary) userLibrary).startWatching();
        //enableLocks();

        if (errorCount > 0) {
            dialog.showErrorAndWait(
                    I18N.getString("error.copy.title"),
                    errorCount == 1 ?
                            I18N.getString("error.copy.message.single", savedFileName, userLibraryPathString):
                            I18N.getString("error.copy.message.multiple", errorCount, userLibraryPathString),
                    I18N.getString("error.write.details"),
                    errorCount == 1 ? savedIOE : null);
        }
    }

    
    private void userLibraryUpdateRejected() {
        dialog.showAlertAndWait(
                I18N.getString("alert.import.reject.dependencies.title"), 
                I18N.getString("alert.import.reject.dependencies.message"), 
                I18N.getString("alert.import.reject.dependencies.details"));
    }
}
