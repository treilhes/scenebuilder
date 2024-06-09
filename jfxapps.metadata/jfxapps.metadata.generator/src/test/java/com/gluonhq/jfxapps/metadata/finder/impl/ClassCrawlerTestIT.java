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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.finder.api.IClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.api.SearchContext;
import com.gluonhq.jfxapps.metadata.util.FxThreadinitializer;
import com.gluonhq.jfxapps.metadata.util.Report;

import javafx.application.Platform;
import javafx.scene.control.Cell;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

class ClassCrawlerTestIT {
    private static final Logger logger = LoggerFactory.getLogger(ClassCrawlerTestIT.class);

    @BeforeEach
    public void initJFX() {
        FxThreadinitializer.initJFX("");
    }

    @AfterEach
    public void stopJFX() {
        FxThreadinitializer.stop();
        System.out.println("STOP");
    }

    @Test
    void should_exit_and_restart_fx_thread() throws Exception {
        //Platform.runLater(() -> new WebView());


        FxThreadinitializer.stop();
        System.out.println();



        FxThreadinitializer.initJFX("");
        System.out.println("END");
    }

    @Test
    void should_crawl_classes_successfullys() {
        SearchContext searchContext = new SearchContext();

        searchContext.addRootClass(javafx.scene.Node.class);
        searchContext.addRootClass(javafx.scene.Scene.class);
        searchContext.addRootClass(javafx.scene.control.MenuItem.class);
        searchContext.addRootClass(javafx.scene.control.Tab.class);
        searchContext.addRootClass(javafx.scene.control.TableColumnBase.class);
        searchContext.addRootClass(javafx.scene.control.TextFormatter.class);
        searchContext.addRootClass(javafx.scene.layout.ConstraintsBase.class);
        searchContext.addRootClass(javafx.scene.shape.PathElement.class);
        searchContext.addRootClass(javafx.stage.Window.class);

        searchContext.addExcludeClass(Cell.class);

        searchContext.addJarFilterPattern(Pattern.compile(".*[/\\\\](javafx-(.*?))[/\\\\].*"));

        searchContext.addIncludedPackage("javafx");

        searchContext.addExcludedPackage("javafx.scene.control.skin");

        //List<Descriptor> descriptors = new ArrayList<>();
        MatchingJarCollector jarCollector = new MatchingJarCollector(searchContext.getJarFilterPatterns());
        JarFinder.listJarsInClasspath(List.of(jarCollector));

        Set<Path> jars = jarCollector.getCollected();

        IClassCrawler crawler = new ClassCrawler();
        var classes = crawler.crawl(jars, searchContext);

        for (Class<?> cls:classes) {
            logger.debug("Found class: {}", cls);
        }


        boolean hasError = Report.flush(true);

        assertTrue(classes.size() > 0, "Some classes must be found");
        assertTrue(!hasError, "No error must be encountered");
    }

}
