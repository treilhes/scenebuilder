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
package com.gluonhq.jfxapps.metadata.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.finder.ClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.JarFinder;
import com.gluonhq.jfxapps.metadata.finder.MetadataConverter;
import com.gluonhq.jfxapps.metadata.finder.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.model.Descriptor;
import com.gluonhq.jfxapps.metadata.util.Report;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Cell;
import javafx.stage.Stage;

class CrawlClassTest {

    public static class DummyApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    @BeforeAll
    public static void initJFX() {
        Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(DummyApp.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    void test() {
        SearchContext searchContext = new SearchContext();

        searchContext.addRootClass(javafx.scene.Node.class);
        searchContext.addRootClass(javafx.scene.Scene.class);
        searchContext.addRootClass(javafx.scene.control.MenuItem.class);
        searchContext.addRootClass(javafx.scene.control.Tab.class);
        searchContext.addRootClass(javafx.scene.control.TableColumnBase.class);
        searchContext.addRootClass(javafx.scene.control.TextFormatter.class);
        //searchContext.addRootClass(javafx.scene.layout.ColumnConstraints.class);
        //searchContext.addRootClass(javafx.scene.layout.RowConstraints.class);
        searchContext.addRootClass(javafx.scene.layout.ConstraintsBase.class);
        searchContext.addRootClass(javafx.scene.shape.PathElement.class);
        searchContext.addRootClass(javafx.stage.Window.class);

        searchContext.addExcludeClass(Cell.class);

        searchContext.addJarFilterPattern(Pattern.compile(".*[/\\\\](javafx-(.*?))[/\\\\].*"));

        searchContext.addIncludedPackage("javafx");

        searchContext.addExcludedPackage("javafx.scene.control.skin");

        PropertyGenerationContext propertyContext = new PropertyGenerationContext();
        try {
            propertyContext.addAltConstructor(BubbleChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {NumberAxis.class, NumberAxis.class});
            propertyContext.addAltConstructor(StackedBarChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {CategoryAxis.class, NumberAxis.class});
            propertyContext.addAltConstructor(StackedAreaChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {Axis.class, ValueAxis.class});
            propertyContext.addAltConstructor(BarChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {CategoryAxis.class, NumberAxis.class});
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Descriptor> descriptors = new ArrayList<>();
        Set<Path> jars = JarFinder.listJarsInClasspath(searchContext.getJarFilterPatterns(), descriptors);
        ClassCrawler crawler = new ClassCrawler();

        final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();

        Platform.runLater(() -> {
            var classes = crawler.crawl(jars, searchContext);
            var beanMap = new MetadataConverter().convert(classes, propertyContext);
            returnValue.complete(beanMap);
        });

        try {
            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();
            for (Class<?> cls:found.keySet()) {
                System.out.println(cls.getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean hasError = Report.flush(true);

        assertTrue(!hasError, "No error must be encountered");
    }

}
