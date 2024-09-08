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
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.gluonhq.jfxapps.metadata.finder.api.SearchContext;
import com.gluonhq.jfxapps.metadata.plugin.params.ConstructorOverride;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;

public abstract class JfxAppsAbstractMojo extends AbstractMojo {

    @Parameter(property = "enableGlobalReport", required = false, alias = "enableGlobalReport")
    boolean enableGlobalReport = true;

    /** The input root classes. */
    @Parameter(property = "rootClasses", required = true, alias = "rootClasses")
    List<String> rootClasses;

    /** The input file. */
    @Parameter(property = "excludeClasses", required = false, alias = "excludeClasses")
    List<String> excludeClasses;

    /** The input file. */
    @Parameter(property = "jarFilterPatterns", required = false, alias = "jarFilterPatterns")
    List<String> jarFilterPatterns;

    /** The input root classes. */
    @Parameter(property = "includePackages", required = false)
    List<String> includePackages;

    /** The input file. */
    @Parameter(property = "excludePackages", required = false)
    List<String> excludePackages;

    @Parameter(property = "outputResourceFolder", required = false, defaultValue = "${project.build.directory}/generated-resources/jfxapps")
    File outputResourceFolder;

    @Parameter(property = "failOnError", required = false, defaultValue = "true")
    boolean failOnError = true;

    @Parameter(property = "javafxVersion", required = true)
    String javafxVersion;



    @Parameter(property = "constructorOverrides", required = false)
    List<ConstructorOverride> constructorOverrides;

    @Parameter(property = "componentCustomizationClass", required = false)
    String componentCustomizationClass;

    @Parameter(property = "componentPropertyCustomizationClass", required = false)
    String componentPropertyCustomizationClass;

    @Parameter(property = "valuePropertyCustomizationClass", required = false)
    String valuePropertyCustomizationClass;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    protected ClassLoader getProjectClassloader() throws MojoExecutionException {
        try {
            // Get the classpath elements
            Set<Artifact> artifacts = project.getArtifacts();

            File buildDirectory = new File(project.getBuild().getOutputDirectory() );

            // Convert to URLs for class loading
            List<URL> list = artifacts.stream()
                  .map(artifact -> {
                      try {
                          return artifact.getFile().toURI().toURL();
                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                  }).collect(Collectors.toList());

            // add the build directory to the classpath for the classloader
            list.add( buildDirectory.toURI().toURL() );

            URL[] urls =list.toArray(URL[]::new);

            // Create a class loader with the project dependencies
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to load project dependencies", e);
        }
    }
    protected SearchContext createSearchContext(ClassLoader loader) throws MojoExecutionException {

        SearchContext searchContext = new SearchContext();

        for (String s : rootClasses) {
            try {
                Class<?> cls = loader.loadClass(s);
                searchContext.addRootClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load root class : " + s, e);
            }
        }

        for (String s : excludeClasses) {
            try {
                Class<?> cls = loader.loadClass(s);
                searchContext.addExcludeClass(cls);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to load excluded class : " + s, e);
            }
        }

        for (String s : jarFilterPatterns) {
            try {
                Pattern pattern = Pattern.compile(s);
                searchContext.addJarFilterPattern(pattern);
            } catch (Exception e) {
                throw new MojoExecutionException("Unable to compile jar filter pattern : " + s, e);
            }
        }

        for (String s:includePackages) {
            searchContext.addIncludedPackage(s);
        }

        for (String s:excludePackages) {
            searchContext.addExcludedPackage(s);
        }

        return searchContext;
    }

    protected PropertyGenerationContext createPropertyGenerationContext(ClassLoader loader) throws MojoExecutionException {

        PropertyGenerationContext propertyContext = new PropertyGenerationContext(loader);
        try {
            if (constructorOverrides != null) {
                for (ConstructorOverride cto : constructorOverrides) {
                    Class<?> cls = Class.forName(cto.getCls());
                    Class<?>[] originalParameters = cto.getParameterOverrides().stream().map(p -> {
                        try {
                            return loader.loadClass(p.getCls());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()).toArray(new Class<?>[0]);

                    Class<?>[] newParameters = cto.getParameterOverrides().stream().map(p -> {
                        try {
                            return loader.loadClass(p.getOverridedBy());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()).toArray(new Class<?>[0]);

                    Constructor<?> constructor = cls.getConstructor(originalParameters);

                    propertyContext.addAltConstructor(constructor, newParameters);
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to override constructors", e);
        }

        if (outputResourceFolder != null && !outputResourceFolder.exists()) {
            outputResourceFolder.mkdirs();
        }
        propertyContext.setOutputResourceFolder(outputResourceFolder);

        try {
            propertyContext.setComponentCustomizationClass(componentCustomizationClass);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e);
        }
        try {
            propertyContext.setComponentPropertyCustomizationClass(componentPropertyCustomizationClass);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e);
        }

        try {
            propertyContext.setValuePropertyCustomizationClass(valuePropertyCustomizationClass);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(e);
        }

        return propertyContext;
    }
}
