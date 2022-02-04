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
package com.oracle.javafx.scenebuilder.extlibrary.library.explorer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionLibraryFilter;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReport;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReportEntry;
import com.oracle.javafx.scenebuilder.fs.controller.ClassLoaderController;
import com.oracle.javafx.scenebuilder.library.api.Explorer;
import com.oracle.javafx.scenebuilder.library.maven.MavenArtifact;

import javafx.concurrent.Task;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public class ExtensionMavenArtifactExplorer implements Explorer<MavenArtifact, ExtensionReport> {

    private final static Logger logger = LoggerFactory.getLogger(ExtensionMavenArtifactExplorer.class);

    private final ClassLoaderController classLoaderController;
    private final List<ExtensionLibraryFilter> filters;

    public ExtensionMavenArtifactExplorer(
            @Autowired ClassLoaderController classLoaderController,
            @Autowired(required = false) List<ExtensionLibraryFilter> filters) {
        super();
        this.classLoaderController = classLoaderController;
        this.filters = filters;
    }

    @Override
    public Task<List<ExtensionReport>> explore(MavenArtifact source) {

        return new Task<List<ExtensionReport>>() {

            @Override
            protected List<ExtensionReport> call() throws Exception {
                final List<ExtensionReport> res = new ArrayList<>();

                List<Path> files = source.toJarList();

                // The classloader takes in addition all already existing
                // jar files stored in the user lib dir.
                try (URLClassLoader classLoader = classLoaderController.copyClassLoader(files)) {

                    final AtomicInteger index = new AtomicInteger();
                    for (Path f : files) {
                        logger.info(I18N.getString("log.info.explore.jar", f));

                        List<ExtensionReportEntry> entries = new ArrayList<>();
                        try (JarFile jarFile = new JarFile(f.toFile())) {
                            JarEntry entry = jarFile.getJarEntry(ExtensionExplorerUtil.EXTENSION_SERVICE_FILE);
                            
                            if (entry != null) {
                                try (InputStreamReader isr = new InputStreamReader(jarFile.getInputStream(entry));
                                     BufferedReader br = new BufferedReader(isr)) {
                                    String className = null;
                                    while((className = br.readLine()) != null) {
                                        if (!className.isEmpty()) {
                                            ExtensionReportEntry reportEntry = ExtensionExplorerUtil.exploreEntry(classLoader, className, filters);
                                            if (reportEntry != null) {
                                                entries.add(reportEntry);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        StringBuilder sb = new StringBuilder(
                                I18N.getString("log.info.explore.jar.results", f.getFileName()));
                        sb.append("\n");
                        if (entries.isEmpty()) {
                            sb.append("> ").append(I18N.getString("log.info.explore.no.results"));
                        } else {
                            entries.forEach(entry -> sb.append("> ").append(entry.toString()).append("\n"));
                        }
                        logger.info(sb.toString());
                        logger.info(I18N.getString("log.info.explore.end", f));

                        ExtensionReport report = new ExtensionReport(f);
                        report.getEntries().addAll(entries);
                        res.add(report);
                        index.incrementAndGet();

                        if (isCancelled()) {
                            break;
                        }
                    }

                    return res;
                }
            }
        };
    }
}
