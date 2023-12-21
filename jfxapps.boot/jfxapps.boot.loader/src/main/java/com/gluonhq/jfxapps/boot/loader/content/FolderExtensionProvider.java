/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.loader.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gluonhq.jfxapps.boot.loader.util.FolderSync;

/**
 * Folder must contains only jar files
 */
public class FolderExtensionProvider implements ExtensionContentProvider {

    private static final Logger logger = LoggerFactory.getLogger(FolderExtensionProvider.class);

    @JsonProperty("folder")
    private File sourceFolder;

    public FolderExtensionProvider(Path sourceFolder) {
        this(sourceFolder.toFile());
    }

    public FolderExtensionProvider(File sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    @Override
    public boolean isUpToDate(Path targetFolder) {
        return Arrays.stream(sourceFolder.listFiles()).allMatch(f -> {
            Path target = targetFolder.resolve(f.getName());
            return Utils.isFileUpToDate(f, target.toFile());
        });
    }

    @Override
    public boolean update(Path targetFolder) throws IOException {
//        for (File f:sourceFolder.listFiles()) {
//            Path target = targetFolder.resolve(f.getName());
//            if (!Utils.isFileUpToDate(f, target.toFile())) {
//                Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
//            }
//        }
        targetFolder.toFile().mkdirs();
        FolderSync.syncDirectories(sourceFolder.toPath(), targetFolder);
        return true;
    }

    @Override
    public boolean isValid() {

        if (!sourceFolder.exists()) {
            logger.error("source folder does not exists : {}", sourceFolder);
        }
        return Arrays.stream(sourceFolder.listFiles()).allMatch(Utils::isFileValid);
    }

    public File getSourceFolder() {
        return sourceFolder;
    }

    protected void setSourceFolder(File sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    @Override
    public String toString() {
        return "FolderExtensionProvider [sourceFolder=" + sourceFolder + "]";
    }


}
