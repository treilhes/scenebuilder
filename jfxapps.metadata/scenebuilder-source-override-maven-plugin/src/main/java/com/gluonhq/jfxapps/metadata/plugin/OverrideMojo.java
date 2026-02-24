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
package com.gluonhq.jfxapps.metadata.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;

@Mojo(name = "override", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class OverrideMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepos;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/scenebuilder-overrides", required = true)
    private File targetSources;

    @Parameter(defaultValue = "${project.build.directory}/generated-resources/scenebuilder-overrides", required = true)
    private File targetResources;

    /** The backup file. */
    @Parameter(required = true)
    Dependency sourceDependency;

    @Parameter(required = false)
    List<String> sourceExtensions = List.of("java");

    @Parameter(required = false, defaultValue = "deleted")
    String deletedExtension;

    /**
     * Default constructor.
     */
    public OverrideMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {

        try {

            targetSources.mkdirs();
            targetResources.mkdirs();

            project.addCompileSourceRoot(targetSources.getAbsolutePath());

            Resource targetResource = new Resource();
            targetResource.setDirectory(targetResources.getAbsolutePath());
            project.addResource(targetResource);

            cleanGeneratedSources(targetSources);
            cleanGeneratedResources(targetResources);

            if (sourceDependency == null) {
                throw new MojoExecutionException("sourceDependency is not set");
            }

            getLog().info("Resolving dependency: " +
                    sourceDependency.getGroupId() + ":" +
                    sourceDependency.getArtifactId() + ":" +
                    sourceDependency.getVersion());

            File jarFile;
            try {
                jarFile = resolveDependency(sourceDependency);
                getLog().info("Resolved JAR: " + jarFile.getAbsolutePath());
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to resolve dependency", e);
            }

            List<String> compileSourceRoots = project.getCompileSourceRoots();
            for (String sourceRoot : compileSourceRoots) {
                getLog().info("Compile source root: " + sourceRoot);
            }

            List<Resource> resources = project.getResources();
            for (Resource resource : resources) {
                getLog().info("Resource directory: " + resource.getDirectory());
            }

            List<File> sourceFolders = compileSourceRoots.stream().map(File::new).toList();
            List<File> resourceFolders = resources.stream().map(r -> new File(r.getDirectory())).toList();

            mergeJarContent(jarFile, targetSources, targetResources, sourceExtensions, sourceFolders, resourceFolders, deletedExtension);

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to complete the overriding process!", e);
        }
    }

    private File resolveDependency(Dependency dep) throws Exception {
        ArtifactRequest request = new ArtifactRequest();
        org.eclipse.aether.artifact.Artifact artifact = new org.eclipse.aether.artifact.DefaultArtifact(
                dep.getGroupId(),
                dep.getArtifactId(),
                "sources",
                "jar",
                dep.getVersion()
        );
        request.setArtifact(artifact);
        request.setRepositories(remoteRepos);

        org.eclipse.aether.resolution.ArtifactResult result = repositorySystem.resolveArtifact(repoSession, request);
        return result.getArtifact().getFile();
    }

    public static void mergeJarContent(
            File jarFile,
            File targetSources,
            File targetResources,
            List<String> sourceExtensions,
            List<File> sourceFolders,
            List<File> resourceFolders,
            String deletedExtension
    ) throws IOException {

        if (!jarFile.exists()) {
            throw new IllegalArgumentException("Jar file does not exist: " + jarFile);
        }

        // Normalize extensions for fast lookup
        Set<String> extensionSet = new HashSet<>();
        for (String ext : sourceExtensions) {
            extensionSet.add(normalizeExtension(ext));
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    continue;
                }

                String relativePath = entry.getName(); // always '/' separated

                // ---- Override checks ----
                if (existsInFolders(relativePath, sourceFolders)
                        || existsInFolders(relativePath + "." + deletedExtension, sourceFolders)
                        || existsInFolders(relativePath, resourceFolders)
                        || existsInFolders(relativePath + "." + deletedExtension, resourceFolders)) {
                    continue;
                }

                if (relativePath.endsWith(".deleted")) {
                    continue; // Skip .deleted files in the JAR itself
                }

                // ---- Routing decision ----
                String extension = getExtension(relativePath);
                File targetBase = extensionSet.contains(extension)
                        ? targetSources
                        : targetResources;

                File targetFile = new File(targetBase, relativePath);

                copyEntry(jar, entry, targetFile);
            }
        }
    }

    private static boolean existsInFolders(String relativePath, List<File> folders) {
        if (folders == null) {
            return false;
        }

        for (File folder : folders) {
            File candidate = new File(folder, relativePath);
            if (candidate.exists()) {
                return true;
            }
        }
        return false;
    }

    private static void copyEntry(JarFile jar, JarEntry entry, File targetFile) throws IOException {

        File parent = targetFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent);
        }

        try (InputStream in = jar.getInputStream(entry);
             OutputStream out = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    private static String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return normalizeExtension(path.substring(lastDot + 1));
    }

    private static String normalizeExtension(String ext) {
        if (ext.startsWith(".")) {
            ext = ext.substring(1);
        }
        return ext.toLowerCase();
    }


    private static void cleanGeneratedResources(File targetResources) {
        if (targetResources.exists()) {
            deleteRecursively(targetResources);
        }
    }

    private static void cleanGeneratedSources(File targetSources) {
        if (targetSources.exists()) {
            deleteRecursively(targetSources);
        }
    }

    private static void deleteRecursively(File targetFolder) {
        if (targetFolder.isDirectory()) {
            File[] children = targetFolder.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        if (!targetFolder.delete()) {
            System.err.println("Failed to delete: " + targetFolder);
        }
    }
}
