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
package com.gluonhq.jfxapps.app.devtools.api.project.fs.feature;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;

import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;
import com.gluonhq.jfxapps.core.api.fs.watcher.FolderFeature;

public class MavenProjectFeature implements FolderFeature {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(MavenProjectFeature.class);

    Folder folder;

    Model model;

    URLClassLoader classLoader;

    @Override
    public void refresh(Folder folder) {
        if (this.classLoader != null) {
            try {
                this.classLoader.close();
            } catch (IOException e) {
                LOG.error("Error closing previous classloader", e);
            }
        }
        this.model = null;
        this.classLoader = null;
        this.folder = folder;
    }

    @Override
    public void onRemove() {
    }

    public Model getModel() {
        return loadModel(folder);
    }

    public URLClassLoader getClassLoader() {
        return loadClassLoader();
    }

    private Model loadModel(Folder folder) {

        Objects.requireNonNull(folder, "Folder cannot be null to load Maven model");

        if (model != null) {
            return model;
        }
        // load pom.xml descriptor
        Path pomFile = folder.getPath().resolve("pom.xml");
        if (pomFile.toFile().exists()) {
            // parse the pom.xml and update the folder/project info
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try (var freader = new FileReader(pomFile.toFile())) {
                model = reader.read(freader);
            } catch (Exception e) {
                LOG.error("Error reading/parsing pom.xml file: {}", pomFile, e);
            }
        }

        return model;
    }

    private URLClassLoader loadClassLoader() {
        if (classLoader != null) {
            return classLoader;
        }

        Model model = loadModel(folder);

        Objects.requireNonNull(model, "Maven model should not be null when loading classloader");

        // Get the build section
        Build build = model.getBuild();

        // If build is null, Maven will use defaults
        String buildDirectory = (build != null && build.getDirectory() != null) ? build.getDirectory() : "target"; // Maven
                                                                                                                   // default

        String outputDirectory = (build != null && build.getOutputDirectory() != null) ? build.getOutputDirectory()
                : buildDirectory + "/classes";

        // Resolve absolute path
        File baseDir = folder.getPath().toFile();
        File classesDir = new File(baseDir, outputDirectory);
        String absolutePath = classesDir.getAbsolutePath();

        // Check if the directory exists
        if (!classesDir.exists()) {
            throw new IllegalStateException("Classes directory does not exist: " + absolutePath);
        }

        // Create a ClassLoader pointing to compiled classes
        try {
            URL classesUrl = classesDir.toURI().toURL();
            classLoader = new URLClassLoader(new URL[] { classesUrl }, Thread.currentThread().getContextClassLoader());

        } catch (MalformedURLException e) {
            LOG.error("Error creating classloader for path: {}", absolutePath, e);
        }

        return classLoader;
    }

}
