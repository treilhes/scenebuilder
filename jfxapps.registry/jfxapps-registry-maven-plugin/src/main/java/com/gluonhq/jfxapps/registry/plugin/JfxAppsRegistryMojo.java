/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.mapper.impl.JsonMapper;
import com.gluonhq.jfxapps.registry.mapper.impl.XmlMapper;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.Extension;
import com.gluonhq.jfxapps.registry.model.Feature;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

@Mojo(name = "jfxappsRegistry", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, configurator = "jfxapps-mojo-component-configurator")
public class JfxAppsRegistryMojo extends AbstractMojo {

    private static final String REGISTRY_IS_NOT_VALID = "The registry is not valid!";

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

            validate(registry);

            var resourceFolder = new File(outputDirectory, GENERATED_RESOURCES_FOLDER);
            var registryFolder = new File(resourceFolder, JfxApps.REGISTRY_FILE_FOLDER);
            var registryFile = new File(registryFolder, JfxApps.REGISTRY_FILE_NAME + "." + format);

            var projectPath = project.getBasedir().getAbsolutePath();
            var resourcePath = resourceFolder.getAbsolutePath();
            var relativePath = resourcePath.replace(projectPath + File.separator, "");

            final var resource = new Resource();
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

    private void validate(Registry registry2) {

        try {
            validateRegistry();
            validateApplications();
            validatePlugins();
            validateLinkedRegistries();
        } catch (Exception e) {
            throw new IllegalArgumentException(REGISTRY_IS_NOT_VALID, e);
        }

    }

    private void validateRegistry() {

        if (registry == null) {
            throw new IllegalArgumentException("The registry is null!");
        }

        if (registry.getUuid() == null) {
            throw new IllegalArgumentException("The registry UUID is empty!");
        }

        if (registry.getDependency() != null) {
            throw new IllegalArgumentException("The registry dependency must be set only for linked registries!");
        }

        if (registry.getDescription() != null) {
            if (registry.getDescription().getTitle() == null || registry.getDescription().getTitle().isBlank()) {
                throw new IllegalArgumentException("The registry title is null or blank!");
            }

            if (registry.getDescription().getImage() == null) {
                throw new IllegalArgumentException("The registry image is null!");
            }

            if (!resourceExists(registry.getDescription().getImage())) {
                throw new IllegalArgumentException("The registry image file does not exists!");
            }

            if (registry.getDescription().getI18n() != null) {
                for (var i18n : registry.getDescription().getI18n()) {
                    if (!resourceExists(i18n)) {
                        throw new IllegalArgumentException(String.format("The registry i18n file %s does not exists!", i18n));
                    }
                }
            }
        }

    }

    private void validateApplications() {
        for (var app : registry.getApplications()) {
            if (app.getUuid() == null) {
                throw new IllegalArgumentException("The application UUID is null!");
            }

            if (app.getDependency() == null) {
                throw new IllegalArgumentException(String.format("The application dependency of %s is null or blank!", app.getUuid()));
            }

            if (app.getDependency().getGroupId() == null || app.getDependency().getGroupId().isBlank()) {
                throw new IllegalArgumentException(String.format("The application groupId of %s is null or blank!", app.getUuid()));
            }

            if (app.getDependency().getArtifactId() == null || app.getDependency().getArtifactId().isBlank()) {
                throw new IllegalArgumentException(String.format("The application artifactId of %s is null or blank!", app.getUuid()));
            }

            if (app.getDependency().getVersion() == null || app.getDependency().getVersion().isBlank()) {
                throw new IllegalArgumentException(String.format("The application version of %s is null or blank!", app.getUuid()));
            }

            if (app.getDescription() == null) {
                throw new IllegalArgumentException(String.format("The application description of %s is null!", app.getUuid()));
            }

            if (app.getDescription().getTitle() == null || app.getDescription().getTitle().isBlank()) {
                throw new IllegalArgumentException(String.format("The application title of %s is null or blank!", app.getUuid()));
            }

            if (app.getDescription().getImage() == null) {
                throw new IllegalArgumentException(String.format("The application image of %s is null!", app.getUuid()));
            }

            if (!resourceExists(app.getDescription().getImage())) {
                throw new IllegalArgumentException(String.format("The application image file of %s does not exists!", app.getUuid()));
            }

            if (app.getSplash() == null) {
                throw new IllegalArgumentException(String.format("The application splash of %s is null!", app.getUuid()));
            }

            if (!resourceExists(app.getSplash())) {
                throw new IllegalArgumentException(String.format("The application splash file of %s does not exists!", app.getUuid()));
            }

            if (app.getDescription().getI18n() != null) {
                for (var i18n : app.getDescription().getI18n()) {
                    if (!resourceExists(i18n)) {
                        throw new IllegalArgumentException(String.format("The application i18n file %s of %s does not exists!", i18n, app.getUuid()));
                    }
                }
            }

            if (app.getExtensions() != null) {
                for (var extension : app.getExtensions()) {
                    validateExtension(extension);
                }
            }
        }
    }

    private void validatePlugins(){
        for (var plugin : registry.getPlugins()) {
            if (plugin.getUuid() == null) {
                throw new IllegalArgumentException("The plugin UUID is null!");
            }

            if (plugin.getDescription() == null) {
                throw new IllegalArgumentException(String.format("The plugin description of %s is null!", plugin.getUuid()));
            }

            if (plugin.getDescription().getTitle() == null || plugin.getDescription().getTitle().isBlank()) {
                throw new IllegalArgumentException(String.format("The plugin title of %s is null or blank!", plugin.getUuid()));
            }

            if (plugin.getDescription().getImage() == null) {
                throw new IllegalArgumentException(String.format("The plugin image of %s is null!", plugin.getUuid()));
            }

            if (!resourceExists(plugin.getDescription().getImage())) {
                throw new IllegalArgumentException(String.format("The plugin image file of %s does not exists!", plugin.getUuid()));
            }

            if (plugin.getDescription().getI18n() != null) {
                for (var i18n : plugin.getDescription().getI18n()) {
                    if (!resourceExists(i18n)) {
                        throw new IllegalArgumentException(String.format("The plugin i18n file %s of %s does not exists!", i18n, plugin.getUuid()));
                    }
                }
            }

            if (plugin.getFeatures() != null) {
                for (var feature : plugin.getFeatures()) {
                    validateFeature(feature);
                }
            }
        }
    }

    private void validateFeature(Feature feature) {
        if (feature.getUuid() == null) {
            throw new IllegalArgumentException("The feature UUID is null!");
        }

        if (feature.getExtensions() != null) {
            for (var extension : feature.getExtensions()) {
                validateExtension(extension);
            }
        }
    }

    private void validateLinkedRegistries() {
        for (var linked : registry.getRegistries()) {

            if (linked == null) {
                throw new IllegalArgumentException("The registry is null!");
            }

            if (linked.getUuid() != null) {
                throw new IllegalArgumentException("The linked registry UUID musn't be set!");
            }

            if (linked.getDependency() == null) {
                throw new IllegalArgumentException("The linked registry dependency must be set!");
            }

            if (linked.getDependency().getGroupId() == null || linked.getDependency().getGroupId().isBlank()) {
                throw new IllegalArgumentException("The linked registry groupId is null or blank!");
            }

            if (linked.getDependency().getArtifactId() == null || linked.getDependency().getArtifactId().isBlank()) {
                throw new IllegalArgumentException("The linked registry artifactId is null or blank!");
            }

            if (linked.getDependency().getVersion() == null || linked.getDependency().getVersion().isBlank()) {
                throw new IllegalArgumentException("The linked registry version is null or blank!");
            }

            if (linked.getDescription() != null) {
                throw new IllegalArgumentException("The linked registry description musn't be set!");
            }


        }
    }

    private void validateExtension(Extension extension) {

        if (extension == null) {
            throw new IllegalArgumentException("The extension is null!");
        }

        if (extension.getUuid() == null) {
            throw new IllegalArgumentException("The extension UUID is empty!");
        }

        if (extension.getDependency() == null) {
            throw new IllegalArgumentException(String.format("The extension dependency of %s is null or blank!", extension.getUuid()));
        }

        if (extension.getDependency().getGroupId() == null || extension.getDependency().getGroupId().isBlank()) {
            throw new IllegalArgumentException(String.format("The extension groupId of %s is null or blank!", extension.getUuid()));
        }

        if (extension.getDependency().getArtifactId() == null || extension.getDependency().getArtifactId().isBlank()) {
            throw new IllegalArgumentException(String.format("The extension artifactId of %s is null or blank!", extension.getUuid()));
        }

        if (extension.getDependency().getVersion() == null || extension.getDependency().getVersion().isBlank()) {
            throw new IllegalArgumentException(String.format("The extension version of %s is null or blank!", extension.getUuid()));
        }

    }

    private boolean resourceExists(String path) {
        for (Resource resource : project.getResources()) {
            var resourceDir = new File(resource.getDirectory());
            var resourceFile = new File(resourceDir, path);
            return resourceFile.exists();
        }
        return false;
    }

}
