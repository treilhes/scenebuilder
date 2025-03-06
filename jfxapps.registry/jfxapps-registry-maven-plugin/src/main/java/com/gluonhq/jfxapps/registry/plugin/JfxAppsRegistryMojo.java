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

	private void validate(Registry registry2) {

        try {
			if (registry == null) {
				throw new IllegalArgumentException("The registry is null!");
			}

			if (registry.getUuid() == null) {
				throw new IllegalArgumentException("The registry UUID is empty!");
			}

			validateApplications();
			validatePlugins();
		} catch (Exception e) {
			throw new IllegalArgumentException(REGISTRY_IS_NOT_VALID, e);
		}

	}

	private void validateApplications() {
		for (var app : registry.getApplications()) {
		    if (app.getUuid() == null) {
		        throw new IllegalArgumentException("The application UUID is null!");
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

			if (app.getDescription().getSplash() == null) {
				throw new IllegalArgumentException(String.format("The application splash of %s is null!", app.getUuid()));
			}

			if (!resourceExists(app.getDescription().getSplash())) {
				throw new IllegalArgumentException(String.format("The application splash file of %s does not exists!", app.getUuid()));
			}

			if (app.getDescription().getI18n() != null && !resourceExists(app.getDescription().getI18n())) {
				throw new IllegalArgumentException(String.format("The application i18n file of %s does not exists!", app.getUuid()));
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

			if (plugin.getDescription().getSplash() == null) {
				throw new IllegalArgumentException(String.format("The plugin splash of %s is null!", plugin.getUuid()));
			}

			if (!resourceExists(plugin.getDescription().getSplash())) {
				throw new IllegalArgumentException(String.format("The plugin splash file of %s does not exists!", plugin.getUuid()));
			}

			if (plugin.getDescription().getI18n() != null && !resourceExists(plugin.getDescription().getI18n())) {
				throw new IllegalArgumentException(String.format("The plugin i18n file of %s does not exists!", plugin.getUuid()));
			}

		}
	}

	private boolean resourceExists(String path) {
		for (Resource resource : project.getResources()) {
            File resourceDir = new File(resource.getDirectory());
            File resourceFile = new File(resourceDir, path);
            return resourceFile.exists();
        }
		return false;
	}

}
