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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionLibraryFilter;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReport;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReportEntry;
import com.oracle.javafx.scenebuilder.fs.controller.ClassLoaderController;
import com.oracle.javafx.scenebuilder.library.api.Explorer;
import com.oracle.javafx.scenebuilder.library.util.LibraryUtil;

import javafx.concurrent.Task;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public class ExtensionFileExplorer implements Explorer<Path, ExtensionReport> {
    
    private final static Logger logger = LoggerFactory.getLogger(ExtensionFileExplorer.class);
    
    private final List<ExtensionLibraryFilter> filters;

    private final ClassLoaderController classLoaderController;

    public ExtensionFileExplorer(
            @Autowired ClassLoaderController classLoaderController,
            @Autowired(required = false) List<ExtensionLibraryFilter> filters
            ) {
        super();
        this.filters = filters;
        this.classLoaderController = classLoaderController;
    }

    /* TO BE SOLVED
    We have an issue with the exploration of SOME jar files.
    If e.g. you use sa-jdi.jar (take it in the JRE or JDK tree) then a NPE as
    the one below will be printed but cannot be caught in the code of this class.
    And from there we won't be able to exit from SB, whatever the action we take
    on the import window (Cancel or Import).
    Yes the window goes away but some thread refuse to give up.
    I noticed two non daemon threads:
    AWT-EventQueue-0
    AWT-Shutdown

    java.lang.NullPointerException
    at java.util.StringTokenizer.<init>(StringTokenizer.java:199)
    at java.util.StringTokenizer.<init>(StringTokenizer.java:221)
    at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:41)
    at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:36)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:414)
    at java.lang.Class.newInstance(Class.java:444)
    at sun.reflect.misc.ReflectUtil.newInstance(ReflectUtil.java:47)
    at javafx.fxml.FXMLLoader$InstanceDeclarationElement.constructValue(FXMLLoader.java:883)
    at javafx.fxml.FXMLLoader$ValueElement.processStartElement(FXMLLoader.java:614)
    at javafx.fxml.FXMLLoader.processStartElement(FXMLLoader.java:2491)
    at javafx.fxml.FXMLLoader.load(FXMLLoader.java:2300)
    at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.instantiateWithFXMLLoader(JarExplorer.java:83)
    at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.exploreEntry(JarExplorer.java:117)
    at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.explore(JarExplorer.java:43)
    at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:155)
    at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:138)
    at javafx.concurrent.Task$TaskCallable.call(Task.java:1376)
    at java.util.concurrent.FutureTask.run(FutureTask.java:262)
    at java.lang.Thread.run(Thread.java:724)
    */
    @Override
    public Task<List<ExtensionReport>> explore(Path source) {
        
        assert Files.isRegularFile(source);
        
        return new Task<List<ExtensionReport>>() {

            @Override
            protected List<ExtensionReport> call() throws Exception {
                final List<ExtensionReport> res = new ArrayList<>();
                
                try (URLClassLoader classLoader = classLoaderController.copyClassLoader(List.of(source))) {
                    if (LibraryUtil.isJarPath(source)) {
                        logger.info(I18N.getString("log.info.explore.jar", source));
                        
                        List<ExtensionReportEntry> entries = new ArrayList<>();
                        try (JarFile jarFile = new JarFile(source.toFile())) {
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
                        
                        
                        StringBuilder sb = new StringBuilder(I18N.getString("log.info.explore.jar.results", source.getFileName()));
                        sb.append("\n");
                        
                        if (entries.isEmpty()) {
                            sb.append("> ").append(I18N.getString("log.info.explore.no.results"));
                        } else {
                            entries.forEach(entry -> sb.append("> ").append(entry.toString()).append("\n"));
                        }
                        logger.info(sb.toString());
                        logger.info(I18N.getString("log.info.explore.end", source));
                        
                        
                        ExtensionReport report = new ExtensionReport(source);
                        report.getEntries().addAll(entries);
                        res.add(report);
                    }
                }
                
                
                
                return res;
            
            }
        };
    }
    
    
}
