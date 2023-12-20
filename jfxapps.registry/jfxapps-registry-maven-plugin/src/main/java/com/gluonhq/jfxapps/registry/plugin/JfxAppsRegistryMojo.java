/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.registry.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.mapper.impl.JsonMapper;
import com.gluonhq.jfxapps.registry.mapper.impl.XmlMapper;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

@Mojo(name = "jfxappsRegistry", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, configurator = "jfxapps-mojo-component-configurator")
public class JfxAppsRegistryMojo extends AbstractMojo {

    private static String GENERATED_RESOURCES_FOLDER = "registry-maven-plugin";

    /** The registry. */
    @Parameter(property = "registry", required = true, alias = "registry")
    Registry registry;

    @Parameter(property = "format", required = false, alias = "format", defaultValue = "xml")
    Format format;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-resources")
    private File outputDirectory;

    public enum Format {
        xml, json
    }

    /**
     * Default constructor.
     */
    public JfxAppsRegistryMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {

            Mapper mapper = switch (format) {
            case xml: {
                yield new XmlMapper();
            }
            case json: {
                yield new JsonMapper();
            }
            default:
                throw new IllegalArgumentException("Unexpected format: " + format);
            };

            File resourceFolder = new File(outputDirectory, GENERATED_RESOURCES_FOLDER);
            File registryFolder = new File(resourceFolder, JfxApps.REGISTRY_FILE_FOLDER);
            File registryFile = new File(registryFolder, JfxApps.REGISTRY_FILE_NAME + "." + format);

            String projectPath = project.getBasedir().getAbsolutePath();
            String resourcePath = resourceFolder.getAbsolutePath();
            String relativePath = resourcePath.replace(projectPath + File.separator, "");

            final Resource resource = new Resource();
            resource.setDirectory(relativePath);
            project.getBuild().getResources().add(resource);

            if (!registryFolder.exists()) {
                registryFolder.mkdirs();
            }

            getLog().info(relativePath);
            getLog().info(registryFile.getAbsolutePath());
            getLog().info(outputDirectory.getAbsolutePath());

            if (registry.getDependency() == null) {
                registry.setDependency(new Dependency());
            }
            if (registry.getDependency().getGroupId() == null) {
                registry.getDependency().setGroupId(project.getGroupId());
            }
            if (registry.getDependency().getArtifactId() == null) {
                registry.getDependency().setArtifactId(project.getArtifactId());
            }
            if (registry.getDependency().getVersion() == null) {
                registry.getDependency().setVersion(project.getVersion());
            }

            try (OutputStream output = new FileOutputStream(registryFile)) {
                mapper.to(registry, output);
            }

        } catch (Exception e) {
            getLog().error("Failed to complete the generation process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generation process!", e);
        }

    }

}
