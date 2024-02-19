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
package com.oracle.javafx.scenebuilder.fs.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;

@Singleton
public class ClassLoaderController {

    private final static Logger logger = LoggerFactory.getLogger(ClassLoaderController.class);

    private final Set<Path> jarsOrFolders = ConcurrentHashMap.newKeySet();

    private URLClassLoader urlClassLoader;

    private SceneBuilderManager sbManager;

    public ClassLoaderController(
            SceneBuilderManager sbManager
            ) {
        this.sbManager = sbManager;
    }

    public Set<Path> getJarsOrFolders() {
        return jarsOrFolders;
    }

    public void releaseClassLoader() throws IOException {

        sbManager.classloader().set(this.getClass().getClassLoader());

        if (urlClassLoader != null) {
            urlClassLoader.close();
            urlClassLoader = null;
        }

        logger.info("Classloader has been released");
    }

    public void updateClassLoader() throws IOException {

        Set<Path> localJarsOrFolders = new HashSet<>(this.jarsOrFolders);

        if (urlClassLoader != null) {
            urlClassLoader.close();
            logger.info("Existing Classloader released before creation");
        }
        if (localJarsOrFolders.isEmpty()) {
            urlClassLoader = null;
            logger.info("Classloader created is not customized");
        } else {
            URL[] urls = makeURLArrayFromPaths(localJarsOrFolders);
            urlClassLoader = new URLClassLoader(urls);
            logger.info("Classloader created is customized with: {}", Arrays.toString(urls));
        }

        sbManager.classloader().set(urlClassLoader == null ? this.getClass().getClassLoader() : urlClassLoader);
    }

    public URLClassLoader copyClassLoader(List<Path> sources) throws IOException {

        Set<Path> localJarsOrFolders = new HashSet<>(this.jarsOrFolders);
        localJarsOrFolders.addAll(sources);

        URL[] urls = makeURLArrayFromPaths(localJarsOrFolders);

        logger.info("Classloader copy created customized with: {}", Arrays.toString(urls));

        if (localJarsOrFolders.isEmpty()) {
            return null;
        } else {
            return new URLClassLoader(urls);
        }

    }


    private URL[] makeURLArrayFromPaths(Collection<Path> paths) {
        final URL[] result = new URL[paths.size()];
        int i = 0;
        for (Path p : paths) {
            try {
                URL url = p.toUri().toURL();
                if (url.toString().endsWith(".jar")) {
                    result[i++] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/
                } else {
                    result[i++] = url; // <-- file:/path/to/folder/
                }
            } catch(MalformedURLException x) {
                throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOCHECK
            }
        }

        return result;
    }
}
