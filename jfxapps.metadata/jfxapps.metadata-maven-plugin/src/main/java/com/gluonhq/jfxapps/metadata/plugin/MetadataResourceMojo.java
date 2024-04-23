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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.BundleValues;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.finder.ClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.JarFinder;
import com.gluonhq.jfxapps.metadata.finder.MetadataConverter;
import com.gluonhq.jfxapps.metadata.finder.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.model.Descriptor;
import com.gluonhq.jfxapps.metadata.util.Report;
import com.gluonhq.jfxapps.metadata.util.Resources;

import javafx.application.Platform;

@Mojo(name = "metadataResource", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataResourceMojo extends JfxAppsAbstractMojo {

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

            if (!FxThreadinitializer.initJFX(javafxVersion).get()) {
                throw new MojoExecutionException("Failed to initialize JavaFX thread");
            }

            PluginDescriptor pluginDescriptor = (PluginDescriptor)getPluginContext().get("pluginDescriptor");
            List<File> cp = pluginDescriptor.getArtifacts().stream().map(a -> a.getFile()).collect(Collectors.toList());

            final SearchContext searchContext = createSearchContext();
            final PropertyGenerationContext propertyContext = createPropertyGenerationContext();

            List<Descriptor> descriptors = new ArrayList<>();
            Set<Path> jars = JarFinder.listJarsInClasspath(cp, searchContext.getJarFilterPatterns(), descriptors);

            ClassCrawler crawler = new ClassCrawler();
            MetadataConverter converter = new MetadataConverter();

            final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();

            Runnable runnable = () -> {
                try {
                    var classes = crawler.crawl(jars, searchContext);
                    var beanMap = converter.convert(classes, propertyContext);
                    returnValue.complete(beanMap);
                } catch (Exception e) {
                    returnValue.completeExceptionally(e);
                }
            };

            Platform.runLater(runnable);

            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();

            for (BeanMetaData<?> bm:found.values()) {
                for (PropertyMetaData pm:bm.getProperties()) {
                    if (!pm.isHidden() && pm.isLocal()) {
                        defaultValueTo(bm, pm, BundleValues.METACLASS, "TOBEDEFINED");
                        defaultValueTo(bm, pm, BundleValues.ORDER, "TOBEDEFINED");
                    }
                }
            }
            Resources.save(found.keySet(), resourceFolder);

            if (Report.flush(getLog().isDebugEnabled()) && failOnError) {
                throw new MojoExecutionException(
                        "Some errors occured during the generation process, please see the logs!");
            }
        } catch (Exception e) {
            getLog().error("Failed to complete the generating process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generating process!", e);
        } finally {
            FxThreadinitializer.stop();
        }

    }

    private void defaultValueTo(BeanMetaData<?> bm, PropertyMetaData pm, String property, String defaultValue) {
        if (pm.getBundleValue(bm.getType(), property, null) == null) {
            pm.setBundleValue(bm.getType(), property, defaultValue);
        }
    }



}
