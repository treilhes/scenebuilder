/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.util;

import java.io.File;
import java.util.Optional;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.fs.FileSystem;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@ApplicationInstanceSingleton
public class FileHelper {

    private final MainInstanceWindow instanceWindow;
    private FileSystem fileSystem;

    // @formatter:off
    public FileHelper(
            MainInstanceWindow instanceWindow,
            FileSystem fileSystem) {
     // @formatter:on
        this.instanceWindow = instanceWindow;
        this.fileSystem = fileSystem;

    }

    public Optional<File> fetchFile(String fileType, String extension) {
        var fileChooser = new FileChooser();
        var dottedExtension = "." + extension;
        var f = new ExtensionFilter(fileType, "*." + extension); // NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        var fxmlFile = fileChooser.showOpenDialog(instanceWindow.getStage());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(dottedExtension)) { // NOI18N
                fxmlFile = new File(path + dottedExtension); // NOI18N
            }

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(fxmlFile);
        }
        return Optional.ofNullable(fxmlFile);
    }

}
