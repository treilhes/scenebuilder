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
package com.oracle.javafx.scenebuilder.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;

public interface FileSystem {

    File getMessageBoxFolder();

    //File getUserLibraryFolder();

    File getApplicationDataFolder();

    /**
     * Returns the last directory selected from the file chooser.
     *
     * @return the last selected directory (never null).
     */
    File getNextInitialDirectory();

    /**
     * @treatAsPrivate
     *
     * Updates the initial directory used by the file chooser.
     *
     * @param chosenFile the selected file from which the initial directory is set.
     */
    void updateNextInitialDirectory(File chosenFile);

    void watch(DocumentWindow document, List<File> files, WatchingCallback callback);
    void watch(DocumentWindow document, Set<Path> files, WatchingCallback callback);

	void unwatch(Object key);
	void unwatchDocument(DocumentWindow document);

	void stopWatcher();

	void startWatcher();

	public interface WatchingCallback{
	    Object getOwnerKey();
		void created(Path path);
		void deleted(Path path);
		void modified(Path path);
	}

    /**
     * Requests the underlying platform to open a given file. On Linux, it runs
     * 'xdg-open'. On Mac, it runs 'open'. On Windows, it runs 'cmd /c start'.
     *
     * @param path path for the file to be opened
     * @throws IOException if an error occurs
     */
    default void open(String path) throws IOException {
        List<String> args = new ArrayList<>();
        if (EditorPlatform.IS_MAC) {
            args.add("open"); //NOCHECK
            args.add(path);
        } else if (EditorPlatform.IS_WINDOWS) {
            args.add("cmd"); //NOCHECK
            args.add("/c"); //NOCHECK
            args.add("start"); //NOCHECK

            if (path.contains(" ")) { //NOCHECK
                args.add("\"html\""); //NOCHECK
            }

            args.add(path);
        } else if (EditorPlatform.IS_LINUX) {
            // xdg-open does fine on Ubuntu, which is a Debian.
            // I've no idea how it does with other Linux flavors.
            args.add("xdg-open"); //NOCHECK
            args.add(path);
        }

        if (!args.isEmpty()) {
            executeDaemon(args, null);
        }
    }

    /**
     * Requests the underlying platform to "reveal" the specified folder. On
     * Linux, it runs 'nautilus'. On Mac, it runs 'open'. On Windows, it runs
     * 'explorer /select'.
     *
     * @param filePath path for the folder to be revealed
     * @throws IOException if an error occurs
     */
    default void revealInFileBrowser(File filePath) throws IOException {
        List<String> args = new ArrayList<>();
        String path = filePath.toURI().toURL().toExternalForm();
        if (EditorPlatform.IS_MAC) {
            args.add("open"); //NOCHECK
            args.add("-R"); //NOCHECK
            args.add(path);
        } else if (EditorPlatform.IS_WINDOWS) {
            args.add("explorer"); //NOCHECK
            args.add("/select," + path); //NOCHECK
        } else if (EditorPlatform.IS_LINUX) {
            // nautilus does fine on Ubuntu, which is a Debian.
            // I've no idea how it does with other Linux flavors.
            args.add("nautilus"); //NOCHECK
            // The nautilus that comes with Ubuntu up to 11.04 included doesn't
            // take a file path as parameter (you get an error popup), you must
            // provide a dir path.
            // Starting with Ubuntu 11.10 (the first based on kernel 3.x) a
            // file path is well managed.
            int osVersionNumerical = Integer.parseInt(System.getProperty("os.version").substring(0, 1)); //NOCHECK
            if (osVersionNumerical < 3) {
                // Case Ubuntu 10.04 to 11.04: What you provide to nautilus is
                // the name of the directory containing the file you want to see
                // listed. See DTL-5384.
                path = filePath.getAbsoluteFile().getParent();
                if (path == null) {
                    path = "."; //NOCHECK
                }
            }
            args.add(path);
        } else {
            // Not Supported
        }

        if (!args.isEmpty()) {
            executeDaemon(args, null);
        }
    }

    /*
     * Private
     */
    private static void executeDaemon(List<String> cmd, File wDir) throws IOException {
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder = builder.directory(wDir);
            builder.start();
        } catch (RuntimeException ex) {
            throw new IOException(ex);
        }
    }

    public static boolean enoughFreeSpaceOnDisk(List<Path> files, Path target) throws IOException {
        long totalSize = Long.MAX_VALUE; // bytes

        for (Path file : files) {
            totalSize += Files.size(file);
        }

        return totalSize < target.toFile().getFreeSpace();
    }
    public static boolean enoughFreeSpaceOnDisk(List<File> files, File target) throws IOException {
        long totalSize = Long.MAX_VALUE; // bytes

        for (File file : files) {
            Path targetPath = Paths.get(file.getAbsolutePath());
            totalSize += Files.size(targetPath);
        }

        return totalSize < target.getFreeSpace();
    }

    //File getUserExtensionsFolder();

    //File getUserExtensionFolder(UUID extensionId);

}
