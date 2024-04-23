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
package com.gluonhq.jfxapps.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ClassLoaderUtils {

    public static ClassLoader createClassLoader(Collection<Path> jarsOrFolders) {
        final ClassLoader classLoader;
        if (jarsOrFolders.isEmpty()) {
            classLoader = null;
        } else {
            classLoader = new URLClassLoader("customLLLOadeeer", makeURLArrayFromPaths(jarsOrFolders), ClassLoaderUtils.class.getClassLoader());

            classLoader.getUnnamedModule().addReads(ClassLoaderUtils.class.getClassLoader().getUnnamedModule());
            ClassLoaderUtils.class.getClassLoader().getUnnamedModule().addReads(classLoader.getUnnamedModule());
        }
        return classLoader;
    }

    public static ClassLoader createClassLoader(List<URL> jarsOrFolders) {
        final ClassLoader classLoader;
        if (jarsOrFolders.isEmpty()) {
            classLoader = null;
        } else {
            //ClassLoader o = ClassLoaderUtils.class.getClassLoader();
            classLoader = new URLClassLoader("customLLLOadr", makeURLArrayFromPaths(jarsOrFolders), ClassLoaderUtils.class.getClassLoader());

            //classLoader.getUnnamedModule().addReads(ClassLoaderUtils.class.getClassLoader().getUnnamedModule());
            //ClassLoaderUtils.class.getClassLoader().getUnnamedModule().addReads(classLoader.getUnnamedModule());
            //System.out.println();
        }
        return classLoader;
    }

    public static class URLClassLoaderBis extends URLClassLoader {

        public URLClassLoaderBis(String name, URL[] urls, ClassLoader parent) {
            super(name, urls, parent);
        }

    }

    private static URL[] makeURLArrayFromPaths(Collection<Path> paths) {
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
                throw new RuntimeException("Bug in " + ClassLoaderUtils.class.getSimpleName(), x); //NOCHECK
            }
        }

        return result;
    }

    private static URL[] makeURLArrayFromPaths(List<URL> urls) {
        final URL[] result = new URL[urls.size()];
        int i = 0;
        for (URL url : urls) {
            try {
                if (url.toString().endsWith(".jar")) {
                    result[i++] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/
                } else {
                    result[i++] = url; // <-- file:/path/to/folder/
                }
            } catch(MalformedURLException x) {
                throw new RuntimeException("Bug in " + ClassLoaderUtils.class.getSimpleName(), x); //NOCHECK
            }
        }

        return result;
    }
}
