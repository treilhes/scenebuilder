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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.emc4j.plugin.bootconfig.AddExports;
import com.gluonhq.emc4j.plugin.bootconfig.AddOpens;
import com.gluonhq.emc4j.plugin.bootconfig.AddReads;
import com.gluonhq.emc4j.plugin.bootconfig.Dependency;
import com.gluonhq.emc4j.plugin.bootconfig.PatchModule;
import com.gluonhq.emc4j.plugin.javaconfig.JavaProcessConfig;
import com.gluonhq.emc4j.plugin.util.BootConfigMask;
import com.gluonhq.emc4j.plugin.util.FsUtil;
import com.gluonhq.emc4j.plugin.util.JpmsHelper;
import com.gluonhq.emc4j.plugin.util.PluginArtifactFactory;

public abstract class Emc4jAbstractMojo extends AbstractMojo {
    enum ModuleType {
        NAMED, AUTOMATIC, UNNAMED, EXCLUDED
    }

    private static final Logger logger = LoggerFactory.getLogger(Emc4jAbstractMojo.class);

    protected static final String DELIVERY_CONFIG_GROUP_ID = "com.gluonhq.jfxapps";
    protected static final String DELIVERY_CONFIG_ARTIFACT_ID = "emc4j.delivery.config";

    protected static final String DELIVERY_RUNTIME_GROUP_ID = "com.gluonhq.jfxapps";
    protected static final String DELIVERY_RUNTIME_ARTIFACT_ID = "jfxapps.boot.runtime";
    protected static final String DELIVERY_RUNTIME_CLASSIFIER = "zip";

    protected static final String BOOT_JAR_GROUP_ID = "com.gluonhq.jfxapps";
    protected static final String BOOT_JAR_ARTIFACT_ID = "jfxapps.boot.main";

    protected static final String BOOT_MODULE = "jfxapps.boot.main";
    protected static final String BOOT_CLASS = "com.gluonhq.jfxapps.boot.main.Main";

    protected static final String DEFAULT_BOOT_CONFIG = "boot-config.xml";

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

    @Parameter(property = "outputDirectory", required = false, defaultValue = "target/emc4j-maven-plugin")
    private String outputDirectory;

    @Parameter(property = "profile", required = true)
    private String profile;

    @Parameter(property = "profileFile", required = true)
    private File profileFile;

    @Parameter(property = "javaOptions", required = false)
    private List<String> javaOptions;


    private PluginArtifactFactory pluginArtifactFactory;

    public Emc4jAbstractMojo() {
        super();
    }

    public MavenProject getProject() {
        return project;
    }

    protected PluginArtifactFactory getPluginArtifactFactory() {
        if (pluginArtifactFactory == null) {
            pluginArtifactFactory = new PluginArtifactFactory(repositorySystem, repositorySession,
                    projectRepositories);
        }
        return pluginArtifactFactory;
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

    public RepositorySystemSession getRepositorySession() {
        return repositorySession;
    }

    public String getProfile() {
        return profile;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public JavaProcessConfig initializeJavaProcessConfig() throws Exception {
        var factory = getPluginArtifactFactory();
        var appBinPluginArtifact = factory.createArtifact(BOOT_JAR_GROUP_ID, BOOT_JAR_ARTIFACT_ID, getEmc4jVersion());
        var appConfigPluginArtifact = factory.createArtifact(DELIVERY_CONFIG_GROUP_ID, DELIVERY_CONFIG_ARTIFACT_ID, getEmc4jVersion());
        var configMask = new BootConfigMask(appConfigPluginArtifact);

        Artifact appMainArtifact = appBinPluginArtifact.resolve();
        List<Artifact> appArtifacts = appBinPluginArtifact.resolveDependencies();

        File javaBin = findJavaBin();

        var javaProcessConfig = new JavaProcessConfig();

        javaProcessConfig.setJavaBin(javaBin);
        javaProcessConfig.setMainModule(BOOT_MODULE);
        javaProcessConfig.setMainClass(BOOT_CLASS);
        javaProcessConfig.setMainJar(appMainArtifact.getFile());

        populateDependencyPathes(javaProcessConfig, configMask, appArtifacts);
        populateOptions(javaProcessConfig, configMask);
        populateProfiles(javaProcessConfig, configMask);

        return javaProcessConfig;
    }

    public void populateDependencyPathes(JavaProcessConfig javaConfig, BootConfigMask configMask, List<Artifact> artifacts) throws Exception {
        var factory = getPluginArtifactFactory();
        var config = configMask.getConfig();
        var jpmsHelper = new JpmsHelper(getLog());


        String addReadFormat = "%s=%s";
        String addOpensFormat = "%s/%s=%s";
        String addExportsFormat = "%s/%s=%s";

        for (PatchModule patch : config.getPatchModules()) {
            try {
                String moduleName = patch.getTargetModule();
                Dependency dep = patch.getDependency();
                // 3 cases:
                // version specified in config
                // version not specified :
                //   if artifact is in dependencies
                //     use dependency version and remove from dependencies
                //   else
                //     use emc4j version

                Artifact artifact = null;
                if (dep.getVersion() != null) {
                    var pluginArtifact = factory.createArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
                    artifact = pluginArtifact.resolve();
                } else {
                    for (Artifact a : artifacts) {
                        if (a.getGroupId().equals(dep.getGroupId()) && a.getArtifactId().equals(dep.getArtifactId())) {
                            artifact = a;
                            break;
                        }
                    }

                    if (artifact != null) {
                        artifacts.remove(artifact);
                    } else {
                        var pluginArtifact = factory.createArtifact(dep.getGroupId(), dep.getArtifactId(), getEmc4jVersion());
                        artifact = pluginArtifact.resolve();
                    }
                }

                if (artifact == null) {
                    getLog().error("Could not resolve artifact for patch module " + moduleName + " with dependency "
                            + dep);
                    continue;
                } else {
                    javaConfig.addPatchModule(moduleName, artifact.getFile());
                }

            } catch (Exception e) {
                getLog().error("Failed to resolve patch for module " + patch.getTargetModule(), e);
            }
        }

        for (AddReads addRead : config.getAddReads()) {
            javaConfig.addAddReads(String.format(addReadFormat, addRead.getModule(), addRead.getToModule()));
        }

        for (AddOpens addOpen : config.getAddOpens()) {
            javaConfig.addAddOpens(String.format(addOpensFormat, addOpen.getModule(), addOpen.getPackageName(), addOpen.getToModule()));
        }

        for (AddExports addExport : config.getAddExports()) {
            javaConfig.addAddExports((String.format(addExportsFormat, addExport.getModule(), addExport.getPackageName(),
                    addExport.getToModule())));
        }

        Map<ModuleType, List<Artifact>> moduleMap = artifacts.stream()
                .collect(java.util.stream.Collectors.groupingBy(a -> {
                    File f = a.getFile();
                    if (configMask.isExcludedDependency(f)) {
                        return ModuleType.EXCLUDED;
                    } else if (jpmsHelper.isModular(f)) {
                        boolean force = configMask.isForcedAsClasspath(a);
                        return force ? ModuleType.UNNAMED : ModuleType.NAMED;
                    } else if (jpmsHelper.isAutomaticModule(f)) {
                        boolean force = configMask.isForcedAsClasspath(a);
                        return force ? ModuleType.UNNAMED : ModuleType.AUTOMATIC;
                    } else {
                        // here we check forced modules (=simple jars without automatic module name but
                        // still "required" in the code, this only works if the jar name use default
                        // maven naming convention)

                        boolean force = configMask.isForcedAsModule(a);
                        return force ? ModuleType.AUTOMATIC : ModuleType.UNNAMED;
                    }
                }));

        if (moduleMap.containsKey(ModuleType.NAMED)) {
            javaConfig.getModules().addAll(moduleMap.get(ModuleType.NAMED).stream().map(Artifact::getFile).toList());
        }

        if (moduleMap.containsKey(ModuleType.AUTOMATIC)) {
            javaConfig.getAutomaticModules().addAll(moduleMap.get(ModuleType.AUTOMATIC).stream().map(Artifact::getFile).toList());
        }

        if (moduleMap.containsKey(ModuleType.UNNAMED)) {
            javaConfig.getClasspath().addAll(moduleMap.get(ModuleType.UNNAMED).stream().map(Artifact::getFile).toList());
        }

    }

    public void populateOptions(JavaProcessConfig javaConfig, BootConfigMask configMask) throws Exception {
        var config = configMask.getConfig();

        for (String option : config.getJavaOptions()) {
            javaConfig.addJvmArg(option);
        }

        List<String> javaOptions = getJavaOptions();
        if (javaOptions != null) {
            for (String option : javaOptions) {
                javaConfig.addJvmArg(option);
            }
        }
    }

    public void populateProfiles(JavaProcessConfig javaConfig, BootConfigMask configMask) throws Exception {
        var config = configMask.getConfig();

        List<String> profiles = new ArrayList<>();

        if (config.getProfiles() != null && !config.getProfiles().isEmpty()) {
            profiles.addAll(config.getProfiles());
        }

        String profile = getProfile();
        if (profile != null && !profile.isEmpty()) {
            profiles.add(profile);
        }

        if (!profiles.isEmpty()) {
            String profilesArg = String.join(",", profiles);
            javaConfig.addJvmArg("-Dspring.profiles.active=" + profilesArg);
        }
    }

    private File findJavaBin() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        return new File(javaBin);
    }
    protected void cleanOutputDirectory() throws IOException {
        if (getOutputDirectory().exists()) {
            FsUtil.deleteDirectory(getOutputDirectory());
        }
    }


    protected void copyDependencies(JavaProcessConfig jcfg, File targetFolder) throws Exception {

        File modulesDir = new File(targetFolder, "mp");
        modulesDir.mkdirs();

        for (File module:jcfg.getModules()) {
            Files.copy(module.toPath(), modulesDir.toPath().resolve(module.getName()));
        }
        for (File automod:jcfg.getAutomaticModules()) {
            Files.copy(automod.toPath(), modulesDir.toPath().resolve(automod.getName()));
        }

        File cpDir = new File(targetFolder, "cp");
        cpDir.mkdirs();

        for (File cpjar:jcfg.getClasspath()) {
            Files.copy(cpjar.toPath(), cpDir.toPath().resolve(cpjar.getName()));
        }

        File patchDir = new File(targetFolder, "patch");
        patchDir.mkdirs();
        for (Entry<String, List<File>> patch : jcfg.getPatchModules().entrySet()) {
            for (File f : patch.getValue()) {
                Files.copy(f.toPath(), patchDir.toPath().resolve(f.getName()));
            }
        }
    }
}
