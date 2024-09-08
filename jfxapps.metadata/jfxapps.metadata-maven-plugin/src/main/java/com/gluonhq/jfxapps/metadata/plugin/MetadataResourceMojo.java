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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.finder.api.IClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.api.SearchContext;
import com.gluonhq.jfxapps.metadata.finder.impl.ClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.impl.DescriptorCollector;
import com.gluonhq.jfxapps.metadata.finder.impl.JarFinder;
import com.gluonhq.jfxapps.metadata.finder.impl.MatchingJarCollector;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerator;
import com.gluonhq.jfxapps.metadata.properties.impl.PropertyGeneratorImpl;
import com.gluonhq.jfxapps.metadata.util.FxThreadinitializer;
import com.gluonhq.jfxapps.metadata.util.Report;

import javafx.application.Platform;

@Mojo(name = "metadataResource", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataResourceMojo extends JfxAppsAbstractMojo {

    static {
        FxThreadinitializer.ENABLE_EXPERIMENTAL_FEATURES = false;
    }
    /**
     * Default constructor.
     */
    public MetadataResourceMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        Report.enableGlobalReport = enableGlobalReport;

        try {

         // Get the runtime classpath elements
            List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
            URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
            for (int i = 0; i < runtimeClasspathElements.size(); i++) {
                String element = runtimeClasspathElements.get(i).replace("\\", "/");
                Path path = Path.of(element);
                if (Files.exists(path) && Files.isDirectory(path) && !element.endsWith("/")) {
                    element += "/";
                }
                runtimeUrls[i] = new URL("file://" + element);
            }

            // Create a new URLClassLoader with the runtime classpath elements
            URLClassLoader urlClassLoader = new URLClassLoader(runtimeUrls,
                    Thread.currentThread().getContextClassLoader());

            // Set the context class loader to the new URLClassLoader
            Thread.currentThread().setContextClassLoader(urlClassLoader);

            if (!FxThreadinitializer.initJFX(javafxVersion)) {
                throw new MojoExecutionException("Failed to initialize JavaFX thread");
            }

            PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
            List<File> cp = pluginDescriptor.getArtifacts().stream().map(a -> a.getFile()).collect(Collectors.toList());

            final SearchContext searchContext = createSearchContext(getProjectClassloader());
            final PropertyGenerationContext propertyContext = createPropertyGenerationContext(getProjectClassloader());

            DescriptorCollector descriptorCollector = new DescriptorCollector();
            MatchingJarCollector jarCollector = new MatchingJarCollector(searchContext.getJarFilterPatterns());

            JarFinder.listJarsInClasspath(cp, List.of(jarCollector, descriptorCollector));
            Set<Path> jars = jarCollector.getCollected();
            Set<Descriptor> descriptors = descriptorCollector.getCollected();

            IClassCrawler crawler = new ClassCrawler();
            PropertyGenerator generator = new PropertyGeneratorImpl(propertyContext);

            final CompletableFuture<Boolean> returnValue = new CompletableFuture<>();

            Runnable runnable = () -> {
                try {
                    var classes = crawler.crawl(jars, searchContext);
                    generator.generateProperties(classes, descriptors);
                    returnValue.complete(true);
                } catch (Exception e) {
                    returnValue.completeExceptionally(e);
                }
            };

            Platform.runLater(runnable);

            if (returnValue.get()) {
                getLog().info("SUCCESS");
            }

            if (returnValue.isCompletedExceptionally()) {
                throw new MojoExecutionException("Failed to complete the generating process!", returnValue.exceptionNow());
            }

        } catch (Exception e) {
            getLog().error("Failed to complete the generating process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generating process!", e);
        } finally {
            FxThreadinitializer.stop();
        }

    }

}
