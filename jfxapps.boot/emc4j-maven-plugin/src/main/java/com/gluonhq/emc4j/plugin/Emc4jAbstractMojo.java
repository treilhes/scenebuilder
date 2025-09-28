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
package com.gluonhq.emc4j.plugin;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamSource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.emc4j.plugin.model.BootConfig;
import com.gluonhq.emc4j.plugin.model.PatchModule;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

public abstract class Emc4jAbstractMojo extends AbstractMojo {

    private static final Logger logger = LoggerFactory.getLogger(Emc4jAbstractMojo.class);

    protected static final String BOOT_CONFIG_GROUP_ID = "com.gluonhq.jfxapps";
    protected static final String BOOT_CONFIG_ARTIFACT_ID = "emc4j.boot.config";

    protected static final String BOOT_JAR_GROUP_ID = "com.gluonhq.jfxapps";
    protected static final String BOOT_JAR_ARTIFACT_ID = "jfxapps.boot.main";

    protected static final String BOOT_MODULE = "jfxapps.boot.main";
    protected static final String BOOT_CLASS = "com.gluonhq.jfxapps.boot.main.Main";

    protected static final String DEFAULT_BOOT_CONFIG = "boot-config.xml";
    protected static final String DEFAULT_BOOT_OPTIONS = "options.config";

    @Component
    private MavenProject project;

    @Component
    private RepositorySystem repositorySystem;

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(defaultValue = "${repositorySystemSession}")
    private RepositorySystemSession repositorySession;

    /**
     * The project's remote repositories to use for the resolution of project
     * dependencies.
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}")
    private List<RemoteRepository> projectRepositories;

    @Parameter(property = "skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "emc4jVersion", required = true)
    private String emc4jVersion;

    @Parameter(property = "bootConfig", required = false, defaultValue = "boot-config.xml")
    private String bootConfiguration;

    @Parameter(property = "outputDirectory", required = false, defaultValue = "target/binaries")
    private String outputDirectory;

    @Parameter(property = "applicationId", required = true)
    private String applicationId;

    @Parameter(property = "profile", required = true)
    private String profile;

    @Parameter(property = "profileFile", required = true)
    private File profileFile;

    private List<PatchModule> patches = new ArrayList<>();

    public Emc4jAbstractMojo() {
        super();
    }

    public MavenProject getProject() {
        return project;
    }

    public boolean isSkip() {
        return skip;
    }

    public String getEmc4jVersion() {
        return emc4jVersion;
    }

    public File getProfileFile() {
        return profileFile;
    }

    public File getOutputDirectory() {
        // get maven project base directory
        File baseDir = project.getBasedir();
        File outputDirectory = new File(baseDir, this.outputDirectory);

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        return outputDirectory;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public RepositorySystemSession getSession() {
        return repositorySession;
    }

    public String getProfile() {
        return profile;
    }

    public BootConfig loadBootConfig(ClassLoader classLoader) throws Exception {

        try (InputStream input = classLoader.getResourceAsStream(DEFAULT_BOOT_CONFIG)) {
            JAXBContext ctx = JAXBContext.newInstance(BootConfig.class);

            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            StreamSource source = new StreamSource(input);
            JAXBElement<BootConfig> element = unmarshaller.unmarshal(source, BootConfig.class);

            return element.getValue();
        }
    }

    public Artifact resolveArtifact(String coords) throws Exception {
        ArtifactRequest request = new ArtifactRequest();
        DefaultArtifact artifact = new DefaultArtifact(coords);
        request.setArtifact(artifact);
        request.setRepositories(projectRepositories);

        ArtifactResult result = repositorySystem.resolveArtifact(repositorySession, request);
        return result.getArtifact();
    }

    public List<Artifact> resolveDependencies(String coords) throws Exception {

        Dependency dependency = new Dependency(new DefaultArtifact(coords), "compile");

        // Collect request: what do we want, from where?
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(projectRepositories);

        // DependencyRequest wraps it
        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setCollectRequest(collectRequest);

        // Resolve!
        DependencyResult result = repositorySystem.resolveDependencies(repositorySession, dependencyRequest);

        //CollectResult r = repositorySystem.collectDependencies(repositorySession, collectRequest);

        //tmpFindArtifact(r.getRoot(), "over");

        // To list of files
        List<Artifact> dependencies = result.getArtifactResults().stream()
            .map(artifactResult -> artifactResult.getArtifact())
            .collect(Collectors.toList());

        return dependencies;
    }

    private void tmpFindArtifact(DependencyNode dn, String search) {
        if (dn.getArtifact() != null) {
            if (dn.getArtifact().getArtifactId().contains(search)) {
                logger.info("Found: {}", dn.getAliases());
            }
        }
        for (DependencyNode child : dn.getChildren()) {
            tmpFindArtifact(child, search);
        }
    }
    public URLClassLoader createClassLoader(File file) {
        try {
            return new URLClassLoader(new URL[] { file.toURI().toURL() },
                    Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            logger.error("Error creating classloader from {]", file, e);
            return null;
        }
    }

    public static String toCoordinate(String groupId, String artifactId, String version) {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}
