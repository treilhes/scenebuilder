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
package com.gluonhq.jfxapps.metadata.finder.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.java.model.tbd.Descriptor;

public class JarFinder {

    private static Logger logger = LoggerFactory.getLogger(JarFinder.class);

    public static void listJarsInClasspath(List<Collector<?>> collectors) {
        List<File> list = getFiles(System.getProperty("java.class.path"));
        listJarsInClasspath(list, collectors);
    }

    public static void listJarsInClasspath(List<File> classpath, List<Collector<?>> collectors) {
        for (File file: classpath) {
            collectors.forEach(c -> c.collect(file));
        }
    }

    /**
     * list files in the given directory and subdirs (with recursion)
     * @param paths
     * @return
     */
    private static List<File> getFiles(String paths) {
      List<File> filesList = new ArrayList<File>();
      for (final String path : paths.split(File.pathSeparator)) {
        final File file = new File(path);
        if( file.isDirectory()) {
           recurse(filesList, file);
        }
        else {
            if (file.getName().toLowerCase().endsWith(".jar")) {
                filesList.add(file);
            }
        }
      }
      return filesList;
    }

    private static void recurse(List<File> filesList, File f) {
      File list[] = f.listFiles();
      for (File file : list) {
        if (file.isDirectory()) {
           recurse(filesList, file);
        }
        else {
            if (file.getName().toLowerCase().endsWith(".jar")) {
                filesList.add(file);
            }
        }
      }
    }
}
