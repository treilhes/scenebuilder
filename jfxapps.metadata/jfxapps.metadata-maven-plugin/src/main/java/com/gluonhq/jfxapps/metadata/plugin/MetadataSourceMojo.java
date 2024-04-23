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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.finder.ClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.JarFinder;
import com.gluonhq.jfxapps.metadata.finder.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.MetadataConverter;
import com.gluonhq.jfxapps.metadata.finder.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.finder.SearchContext;
import com.gluonhq.jfxapps.metadata.finder.api.Executor;
import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Descriptor;
import com.gluonhq.jfxapps.metadata.model.MetadataFromJavafx;
import com.gluonhq.jfxapps.metadata.model.Property;
import com.gluonhq.jfxapps.metadata.plugin.JfxAppsAbstractMojo.FxThreadinitializer;
import com.gluonhq.jfxapps.metadata.util.Report;

import javafx.application.Platform;

@Mojo(name = "metadataSource", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataSourceMojo extends JfxAppsAbstractMojo {

    @Parameter(property = "sourceFolder", required = false, defaultValue = "${project.build.directory}/generated-sources/jfxapps")
    File sourceFolder;

    @Parameter(property = "targetPackage", required = true)
    String targetPackage;

    @Parameter(property = "moduleName", required = false, defaultValue = "${project.artifactId}")
    String moduleName;

    @Parameter(property = "uuid", required = true)
    String uuid;

    /** The input root classes. */
    @Parameter(property = "moduleRequires", required = false)
    List<String> moduleRequires;

    @Parameter(property = "executorClass", required = true)
    String executorClass;

    @Parameter(property = "extensionName", required = true)
    String extensionName;

    @Parameter(property = "metadataPrefix", required = false, defaultValue = "")
    String metadataPrefix = "";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Default constructor.
     */
    public MetadataSourceMojo() {
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
            final JavaGenerationContext javaContext = createJavaGenerationContext();

            Executor executor = javaContext.getExecutorClass().getConstructor().newInstance();
            List<Descriptor> descriptors = new ArrayList<>();
            Set<Path> jars = JarFinder.listJarsInClasspath(cp, searchContext.getJarFilterPatterns(), descriptors);
            ClassCrawler crawler = new ClassCrawler();
            MetadataConverter converter = new MetadataConverter();

            final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();
            final CompletableFuture<Map<Class<?>, Component>> descriptorReturnValue = new CompletableFuture<>();

            executor.preExecute(searchContext);

            Runnable runnable = () -> {
                try {
                    var classes = crawler.crawl(jars, searchContext);
                    var beanMap = converter.convert(classes, propertyContext);
                    descriptorReturnValue.complete(descriptorLoad(descriptors));
                    returnValue.complete(beanMap);
                } catch (Exception e) {
                    returnValue.completeExceptionally(e);
                }
            };

            Platform.runLater(runnable);

            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();
            Map<Class<?>, Component> othersFound = descriptorReturnValue.get();

            found.forEach((k,v) -> getLog().debug("Crawled:" + k.getName()));
            othersFound.forEach((k,v) -> getLog().debug("From descriptors:" + k.getName()));

            try {

                Map<Component, Set<Property>> components = MetadataFromJavafx.load(found, othersFound);

                executor.execute(propertyContext, javaContext, components, othersFound);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to execute Executor program", e);
            }

            // Resources.save(output);

            updateProject();

            if (Report.flush(getLog().isDebugEnabled()) && failOnError) {
                throw new MojoExecutionException(
                        "Some errors occured during the generation process, please see the logs!");
            }
        } catch (Exception e) {
            getLog().error("Failed to complete the generation process! " + e.getMessage(), e);
            Report.flush(getLog().isDebugEnabled());
            throw new MojoExecutionException("Failed to complete the generation process!", e);
        } finally {
            FxThreadinitializer.stop();
        }

    }

    private Map<Class<?>, Component> descriptorLoad(List<Descriptor> descriptors) {

        Map<Class<?>, Component> result = new HashMap<>();

        if (descriptors != null && !descriptors.isEmpty()) {
            for (Descriptor extDescriptor : descriptors) {
                for (Entry<Class<?>, String> e : extDescriptor.getClassToMetaClass().entrySet()) {
                    Class<?> componentClass = e.getKey();
                    String metaClassName = e.getValue();
                    try {
                        Component c = new Component(new BeanMetaData<>(componentClass, null));
                        c.getCustom().put("className", metaClassName);
                        result.put(componentClass, c);
                    } catch (Exception ex) {
                    }
                }
            }
        }

        return result;
    }

    private void updateProject() {
        project.addCompileSourceRoot(sourceFolder.getPath());
        project.addCompileSourceRoot(resourceFolder.getPath());

        Resource r = new Resource();
        r.setDirectory(resourceFolder.getAbsolutePath());
        r.setTargetPath("");
        project.addResource(r);
    }

    private JavaGenerationContext createJavaGenerationContext() throws MojoExecutionException {

        JavaGenerationContext javaContext = new JavaGenerationContext();

        try {
            javaContext.setExecutorClass((Class<Executor>) Class.forName(executorClass));
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to find executor class", e);
        }

        if (sourceFolder != null && !sourceFolder.exists()) {
            sourceFolder.mkdirs();
        }
        javaContext.setSourceFolder(sourceFolder);

        if (resourceFolder != null && !resourceFolder.exists()) {
            resourceFolder.mkdirs();
        }

        javaContext.setTargetPackage(targetPackage);

        javaContext.setModuleName(moduleName);

        for (String s : moduleRequires) {
            javaContext.addModuleRequire(s);
        }

        javaContext.setUuid(uuid);

        javaContext.setExtensionName(extensionName);

        javaContext.setMetadataPrefix(metadataPrefix);

        return javaContext;
    }
}