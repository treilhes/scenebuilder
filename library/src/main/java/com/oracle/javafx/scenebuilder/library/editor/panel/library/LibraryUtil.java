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
package com.oracle.javafx.scenebuilder.library.editor.panel.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LibraryUtil {

    public static final String FOLDERS_FOR_FILES = "Files"; //NOI18N
    
    public static final String FOLDERS_LIBRARY_FILENAME = "library.folders"; //NOI18N

    public static boolean isJarPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".jar"); //NOI18N
    }

    public static boolean hasExtension(Path path, List<String> extensions) {
        return hasExtension(path.toString(), extensions);
    }
    
    public static boolean hasExtension(String path, List<String> extensions) {
        final String pathString = path.toLowerCase(Locale.ROOT);
        String upperPath = pathString.toUpperCase();
        return extensions.stream().anyMatch(e -> upperPath.endsWith("." + e.toUpperCase())); //NOI18N
    }
    
    public static boolean isFxmlPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".fxml"); //NOI18N
    }

    public static boolean isFolderMarkerPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".folders"); //NOI18N
    }

    public static List<Path> getFolderPaths(Path libraryFile) throws IOException {
        if (!Files.exists(libraryFile)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(libraryFile).stream()
                .map(line -> {
                    File f = new File(line);
                    if (f.exists() && f.isDirectory())
                        return f.toPath();
                    else
                        return null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }
    
    public static String makeFxmlText(Class<?> componentClass) {
        final StringBuilder sb = new StringBuilder();

        /*
         * <?xml version="1.0" encoding="UTF-8"?> //NOI18N
         *
         * <?import a.b.C?>
         *
         * <C/>
         */

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N

        sb.append("<?import "); // NOI18N
        sb.append(componentClass.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<"); // NOI18N
        sb.append(componentClass.getSimpleName());
        sb.append("/>\n"); // NOI18N

        return sb.toString();
    }
}
