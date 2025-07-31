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
package com.gluonhq.jfxapps.properties.plugin;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Same as
 * https://maven.apache.org/plugins/maven-dependency-plugin/properties-mojo.html
 * Goal that sets a property pointing to the artifact file for each project
 * dependency. For each dependency (direct and transitive) a project property
 * will be set which follows the groupId:artifactId:type:[classifier] form and
 * contains the path to the resolved artifact.
 *
 * What this plugin does on top of the maven-dependency-plugin is to set the
 * properties for the plugin dependencies as well.
 * It allows to add properties for artifacts outside of the project classpath
 * For modular projects, this is useful to set the properties for the patched artifacts
 */
@Mojo(name = "jpms.patch", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class JpmsPatchMojo extends AbstractMojo {
    @Component
    private MavenProject project;

    @Parameter(property = "skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "targetModule")
    private String targetModule;

    @Parameter(property = "dependencyPatches")
    private List<DependencyPatch> dependencyPatches;


    @Override
    public void execute() throws MojoExecutionException {
        if (isSkip()) {
            getLog().info("Skipping plugin execution");
            return;
        }

        Set<Artifact> artifacts = this.project.getArtifacts();
        for (Artifact artifact : artifacts) {
            this.project.getProperties().setProperty(artifact.getDependencyConflictId(),
                    artifact.getFile().getAbsolutePath());
        }

        PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");

        List<Artifact> pluginArtifacts = pluginDescriptor.getArtifacts();
        for (Artifact artifact : pluginArtifacts) {
            this.project.getProperties().setProperty(artifact.getDependencyConflictId(),
                    artifact.getFile().getAbsolutePath());
        }


        boolean hasTargetModule = targetModule != null && !targetModule.isEmpty();
        boolean hasPatches = dependencyPatches != null && !dependencyPatches.isEmpty();
        boolean hasSomethingToDo = hasTargetModule || hasPatches;

        if (!hasSomethingToDo) {
            getLog().info("No target module or dependency patches specified, skipping JPMS patching.");
            return;
        }

        File generatedResourcesDir = createOrRegisterGeneratedSourceDirectory();
        File jpmsPatchFile = new File(generatedResourcesDir, "patch.jpms");

        StringBuilder patchContent = new StringBuilder();

        if (hasTargetModule) {
            patchContent.append(targetModule).append("\n");
        }

        if (hasPatches) {
            for (DependencyPatch patch : dependencyPatches) {
                if (patch.getTargetModule() == null || patch.getDependency() == null) {
                    getLog().warn("Invalid DependencyPatch: " + patch);
                    continue;
                }

                Dependency dependency = patch.getDependency();
                String dependencyFileProperty = String.format("%s:%s:%s",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getClassifier() != null ? dependency.getClassifier() : "jar");

                String dependencyPath = this.project.getProperties().getProperty(dependencyFileProperty);

                if (dependencyPath == null) {
                    getLog().warn("Dependency not found for: " + dependencyFileProperty);
                    continue;
                }

                File dependencyFile = new File(dependencyPath);
                String dependencyFileName = dependencyFile.getName();

                if (!dependencyFile.exists()) {
                    getLog().warn("Dependency file does not exist: " + dependencyFile.getAbsolutePath());
                    continue;
                }

                String line = String.format("%s=%s\n",
                        patch.getTargetModule(),
                        dependencyFileName);

                patchContent.append(line);
            }
        }

        if (patchContent.length() > 0) {
            try {
                // Write the patch content to the file
                java.nio.file.Files.writeString(jpmsPatchFile.toPath(), patchContent.toString());
                getLog().info("JPMS patch file created: " + jpmsPatchFile.getAbsolutePath());
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to write JPMS patch file", e);
            }
        } else {
            getLog().info("No patches to write to JPMS patch file.");
        }
    }

    private File createOrRegisterGeneratedSourceDirectory() throws MojoExecutionException {
        File buildDir = new File(project.getBuild().getDirectory()); // usually "target"
        File generatedResourcesDir = new File(buildDir, "generated-resources");

        // Create folder if it doesn't exist
        if (!generatedResourcesDir.exists()) {
            if (generatedResourcesDir.mkdirs()) {
                getLog().info("Created: " + generatedResourcesDir.getAbsolutePath());
            } else {
                throw new MojoExecutionException("Could not create directory: " + generatedResourcesDir);
            }
        }

        // Check if it's already registered
        boolean alreadyRegistered = project.getResources().stream()
            .map(Resource::getDirectory)
            .filter(Objects::nonNull)
            .map(File::new)
            .anyMatch(f -> f.getAbsolutePath().equals(generatedResourcesDir.getAbsolutePath()));

        if (!alreadyRegistered) {
            Resource resource = new Resource();
            resource.setDirectory(generatedResourcesDir.getAbsolutePath());
            resource.setFiltering(false); // change to true if you need filtering
            project.addResource(resource);
            getLog().info("Registered generated-resources directory with project.");
        } else {
            getLog().info("Generated-resources directory is already registered.");
        }

        return generatedResourcesDir;
    }

    public boolean isSkip() {
        return this.skip;
    }

    public static class DependencyPatch {
        private String targetModule;
        private Dependency dependency;

        public DependencyPatch() {
        }

        protected String getTargetModule() {
            return targetModule;
        }

        protected void setTargetModule(String targetModule) {
            this.targetModule = targetModule;
        }

        protected Dependency getDependency() {
            return dependency;
        }

        protected void setDependency(Dependency dependency) {
            this.dependency = dependency;
        }


    }
    public static class Dependency {
        private String groupId;
        private String artifactId;
        private String classifier = "jar";

        public Dependency() {
        }

        protected String getGroupId() {
            return groupId;
        }

        protected void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        protected String getArtifactId() {
            return artifactId;
        }

        protected void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        protected String getClassifier() {
            return classifier;
        }

        protected void setClassifier(String classifier) {
            this.classifier = classifier;
        }


    }
}