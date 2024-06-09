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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.finder.api.IClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.api.SearchContext;
import com.gluonhq.jfxapps.metadata.finder.impl.ClassCrawler;
import com.gluonhq.jfxapps.metadata.finder.impl.DescriptorCollector;
import com.gluonhq.jfxapps.metadata.finder.impl.JarFinder;
import com.gluonhq.jfxapps.metadata.finder.impl.MatchingJarCollector;
import com.gluonhq.jfxapps.metadata.java.api.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.java.impl.JavaGeneratorImpl;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.util.FxThreadinitializer;
import com.gluonhq.jfxapps.metadata.util.Report;

import javafx.application.Platform;

@Mojo(name = "metadataSource", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class MetadataSourceMojo extends JfxAppsAbstractMojo {

    /** The backup file. */
    @Parameter(property = "inputResourceFolder", required = false, defaultValue = "${project.directory}/src/main/resources")
    File inputResourceFolder;

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

    @Parameter(property = "extensionName", required = true)
    String extensionName;

    @Parameter(property = "metadataPrefix", required = false, defaultValue = "")
    String metadataPrefix = "";

    @Parameter(property = "templateForComponentCustomization", required = false, defaultValue = "")
    String templateForComponentCustomization = "";

    @Parameter(property = "templateForComponentConstructorCustomization", required = false, defaultValue = "")
    String templateForComponentConstructorCustomization = "";

    @Parameter(property = "templateForComponentPropertyCustomization", required = false, defaultValue = "")
    String templateForComponentPropertyCustomization = "";

    @Parameter(property = "templateForValuePropertyCustomization", required = false, defaultValue = "")
    String templateForValuePropertyCustomization = "";

    @Parameter(property = "templateForStaticValuePropertyCustomization", required = false, defaultValue = "")
    String templateForStaticValuePropertyCustomization = "";

    @Parameter(property = "targetComponentSuperClass", required = false)
    String targetComponentSuperClass;

    @Parameter(property = "targetComponentCustomizationClass", required = false)
    String targetComponentCustomizationClass;

    @Parameter(property = "targetComponentPropertyCustomizationClass", required = false)
    String targetComponentPropertyCustomizationClass;

    @Parameter(property = "targetValuePropertyCustomizationClass", required = false)
    String targetValuePropertyCustomizationClass;

    static {
        FxThreadinitializer.ENABLE_EXPERIMENTAL_FEATURES = false;
    }
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

            if (!FxThreadinitializer.initJFX(javafxVersion)) {
                throw new MojoExecutionException("Failed to initialize JavaFX thread");
            }

            PluginDescriptor pluginDescriptor = (PluginDescriptor)getPluginContext().get("pluginDescriptor");
            List<File> cp = pluginDescriptor.getArtifacts().stream().map(a -> a.getFile()).collect(Collectors.toList());

            final SearchContext searchContext = createSearchContext();
            final PropertyGenerationContext propertyContext = createPropertyGenerationContext();
            final JavaGenerationContext javaContext = createJavaGenerationContext();

            DescriptorCollector descriptorCollector = new DescriptorCollector();
            MatchingJarCollector jarCollector = new MatchingJarCollector(searchContext.getJarFilterPatterns());

            JarFinder.listJarsInClasspath(cp, List.of(jarCollector, descriptorCollector));
            Set<Path> jars = jarCollector.getCollected();
            Set<Descriptor> descriptors = descriptorCollector.getCollected();

            IClassCrawler crawler = new ClassCrawler();
            JavaGeneratorImpl generator = new JavaGeneratorImpl(propertyContext, javaContext);

            final CompletableFuture<Boolean> returnValue = new CompletableFuture<>();

            Runnable runnable = () -> {
                try {
                    var classes = crawler.crawl(jars, searchContext);
                    generator.generateJavaFiles(classes, descriptors);
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

    private void updateProject() {
        project.addCompileSourceRoot(sourceFolder.getPath());
    }

    private JavaGenerationContext createJavaGenerationContext() throws MojoExecutionException {

        JavaGenerationContext javaContext = new JavaGenerationContext();

        if (sourceFolder != null && !sourceFolder.exists()) {
            sourceFolder.mkdirs();
        }
        javaContext.setSourceFolder(sourceFolder);

        javaContext.setInputResourceFolder(inputResourceFolder);

        javaContext.setTargetPackage(targetPackage);

        javaContext.setModuleName(moduleName);

        for (String s : moduleRequires) {
            javaContext.addModuleRequire(s);
        }

        javaContext.setUuid(uuid);

        javaContext.setExtensionName(extensionName);

        javaContext.setMetadataPrefix(metadataPrefix);

        javaContext.setComponentCustomizationTemplate(templateForComponentCustomization);
        javaContext.setComponentPropertyCustomizationTemplate(templateForComponentPropertyCustomization);
        javaContext.setValuePropertyCustomizationTemplate(templateForValuePropertyCustomization);
        javaContext.setStaticValuePropertyCustomizationTemplate(templateForStaticValuePropertyCustomization);
        javaContext.setComponentConstructorCustomizationTemplate(templateForComponentConstructorCustomization);


        javaContext.setTargetComponentSuperClass(targetComponentSuperClass);
        javaContext.setTargetComponentCustomizationClass(targetComponentCustomizationClass);
        javaContext.setTargetComponentPropertyCustomizationClass(targetComponentPropertyCustomizationClass);
        javaContext.setTargetValuePropertyCustomizationClass(targetValuePropertyCustomizationClass);
        return javaContext;
    }
}
