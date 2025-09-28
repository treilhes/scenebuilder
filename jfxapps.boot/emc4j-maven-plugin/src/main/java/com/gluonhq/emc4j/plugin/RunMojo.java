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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.aether.artifact.Artifact;

import com.gluonhq.emc4j.plugin.model.AddExports;
import com.gluonhq.emc4j.plugin.model.AddOpens;
import com.gluonhq.emc4j.plugin.model.AddReads;
import com.gluonhq.emc4j.plugin.model.BootConfig;
import com.gluonhq.emc4j.plugin.model.Dependency;
import com.gluonhq.emc4j.plugin.model.PatchModule;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class RunMojo extends Emc4jAbstractMojo {

    private static final String BOOT_CONFIG_FILENAME = "boot.config";

    private static final String DEBUG_OPTION = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=%s,address=0.0.0.0:%s";

    @Parameter(property = "debug", defaultValue = "false")
    private boolean debug;

    @Parameter(property = "debugSuspend", defaultValue = "true")
    private boolean debugSuspend;

    @Parameter(property = "debugPort", defaultValue = "8000")
    private int debugPort;

    @Parameter(property = "javaOptions", required = false)
    private List<String> javaOptions;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String configCoordinates = toCoordinate(BOOT_CONFIG_GROUP_ID, BOOT_CONFIG_ARTIFACT_ID, getEmc4jVersion());

            String binCoordinates = toCoordinate(BOOT_JAR_GROUP_ID, BOOT_JAR_ARTIFACT_ID, getEmc4jVersion());

            Artifact configArtifact = resolveArtifact(configCoordinates);
            Artifact binArtifact = resolveArtifact(binCoordinates);
            List<Artifact> dependencies = resolveDependencies(binCoordinates);

            if (getOutputDirectory().exists()) {
                deleteDirectory(getOutputDirectory());
            }


            File binFile = binArtifact.getFile();
            File configFile = configArtifact.getFile();

            ClassLoader classLoader = createClassLoader(configFile);

            var bootConfig = loadBootConfig(classLoader);
            File targetConfig = new File(getOutputDirectory(), BOOT_CONFIG_FILENAME);
            generateConfigFile(bootConfig, targetConfig, dependencies);

            File targetBinary = new File(getOutputDirectory(), binFile.getName());

            Files.copy(binFile.toPath(), targetBinary.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (getProfile() != null && getProfileFile() != null) {
                String extension = getProfileFile().getName().substring(getProfileFile().getName().lastIndexOf('.'));
                File targetProfile = new File(getOutputDirectory(), "application-" + getProfile() + extension);
                Files.copy(getProfileFile().toPath(), targetProfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }


            run(targetBinary);

            System.out.println();
        } catch (Exception e) {
            throw new MojoExecutionException("Error during run", e);
        }

    }

    public void run(File targetBinary) throws MojoExecutionException {
        try {
            // Build the command
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

            List<String> command = new ArrayList<>();
            command.add(javaBin);

            if (debug) {
                command.add(String.format(DEBUG_OPTION, debugSuspend ? "y" : "n", String.valueOf(debugPort)));
            }

            command.add("-Djfxapps.registry.snapshotsAllowed=true");

            command.add("-Djfxapps.repository.directory=\""
                    + getSession().getLocalRepository().getBasedir().getAbsolutePath() + "\"");

            // command.add("@" + new File(getOutputDirectory(),
            // BOOT_CONFIG_FILENAME).getAbsolutePath());
            command.add("@" + BOOT_CONFIG_FILENAME);

            command.add("-m");
            command.add(BOOT_MODULE + "/" + BOOT_CLASS);

            command.add("-a");//-a 1b8bb5f3-efb3-41a6-b6f0-12c9d96fc6a2
            command.add(getApplicationId());

            getLog().info("Running command: " + String.join(" ", command));

            // Start process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.directory(targetBinary.getParentFile());

            Process process = pb.start();

            // Forward process output to Maven log
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    getLog().info("[java] " + line);
                }
            }

            // Wait for completion (blocking)
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new MojoExecutionException("Java process exited with code " + exitCode);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run Java command", e);
        }
    }

    public void generateConfigFile(BootConfig config, File targetFile, List<Artifact> dependencies) throws IOException {
        String patchFormat = "--patch-module %s=%s";
        String addReadFormat = "--add-reads %s=%s";
        String addOpensFormat = "--add-opens %s/%s=%s";
        String addExportsFormat = "--add-exports %s/%s=%s";

        StringBuilder sb = new StringBuilder();

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
                    String coordinate = toCoordinate(dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
                    artifact = resolveArtifact(coordinate);
                } else {
                    for (Artifact a : dependencies) {
                        if (a.getGroupId().equals(dep.getGroupId()) && a.getArtifactId().equals(dep.getArtifactId())) {
                            artifact = a;
                            break;
                        }
                    }

                    if (artifact != null) {
                        dependencies.remove(artifact);
                    } else {
                        String coordinate = toCoordinate(dep.getGroupId(), dep.getArtifactId(), getEmc4jVersion());
                        artifact = resolveArtifact(coordinate);
                    }
                }

                if (artifact == null) {
                    getLog().error("Could not resolve artifact for patch module " + moduleName + " with dependency "
                            + dep);
                    continue;
                } else {
                    sb.append(String.format(patchFormat, moduleName, artifact.getFile().getAbsolutePath())).append("\n");
                }

            } catch (Exception e) {
                getLog().error("Failed to resolve patch for module " + patch.getTargetModule(), e);
            }
        }

        for (AddReads addRead : config.getAddReads()) {
            sb.append(String.format(addReadFormat, addRead.getModule(), addRead.getToModule())).append("\n");
        }

        for (AddOpens addOpen : config.getAddOpens()) {
            sb.append(
                    String.format(addOpensFormat, addOpen.getModule(), addOpen.getPackageName(), addOpen.getToModule()))
                    .append("\n");
        }

        for (AddExports addExport : config.getAddExports()) {
            sb.append(String.format(addExportsFormat, addExport.getModule(), addExport.getPackageName(),
                    addExport.getToModule())).append("\n");
        }

        Map<ModuleType, List<File>> moduleMap = dependencies.stream()
                .map(Artifact::getFile)
                .collect(java.util.stream.Collectors.groupingBy(f -> {
                    if (isExcludedDependency(config, f)) {
                        return ModuleType.EXCLUDED;
                    } else if (isModular(f)) {
                        boolean force = isForcedAsClasspath(config, f);
                        return force ? ModuleType.UNNAMED : ModuleType.NAMED;
                    } else if (isAutomaticModule(f)) {
                        boolean force = isForcedAsClasspath(config, f);
                        return force ? ModuleType.UNNAMED : ModuleType.AUTOMATIC;
                    } else {
                        // here we check forced modules (=simple jars without automatic module name but
                        // still "required" in the code, this only works if the jar name use default
                        // maven naming convention)

                        boolean force = isForcedAsModule(config, f);
                        return force ? ModuleType.AUTOMATIC : ModuleType.UNNAMED;
                    }
                }));

        String joinedModulesPaths = String.join(File.pathSeparator,
                Stream.concat(moduleMap.get(ModuleType.NAMED).stream(),moduleMap.get(ModuleType.AUTOMATIC).stream())
                .map(File::getAbsolutePath).map(p -> p.replace("\\", "/"))
                .toArray(String[]::new));

        String joinedClasspathPaths = String.join(File.pathSeparator,
                moduleMap.get(ModuleType.UNNAMED).stream()
                .map(File::getAbsolutePath).map(p -> p.replace("\\", "/"))
                .toArray(String[]::new));


        sb.append("--module-path ").append("\"" + joinedModulesPaths + "\"").append("\n");
        sb.append("--class-path ").append("\"" + joinedClasspathPaths + "\"").append("\n");

        for (String option : config.getJavaOptions()) {
            sb.append(option).append("\n");
        }

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
            sb.append("-Dspring.profiles.active=").append(profilesArg).append("\n");
        }

        if (javaOptions != null) {
            for (String option : javaOptions) {
                sb.append(option).append("\n");
            }
        }

        Files.writeString(targetFile.toPath(), sb);
    }

    private boolean isForcedAsModule(BootConfig config, File f) {
        return config.getForceAsModules().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    private boolean isForcedAsClasspath(BootConfig config, File f) {
        return config.getForceAsClasspaths().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    private boolean isExcludedDependency(BootConfig config, File f) {
        return config.getExcludedDependencies().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    boolean isModular(File jarFile) {
        try (var jf = new java.util.jar.JarFile(jarFile)) {
            return jf.stream().anyMatch(e -> e.getName().endsWith("module-info.class"));
        } catch (IOException e) {
            getLog().error("Error reading jar file " + jarFile, e);
            return false;
        }
    }

    boolean isAutomaticModule(File jarFile) {

        try (var jf = new java.util.jar.JarFile(jarFile)) {
            return jf.getManifest().getMainAttributes().getValue("Automatic-Module-Name") != null;
        } catch (IOException e) {
            getLog().error("Error reading jar file " + jarFile, e);
            return false;
        } catch (NullPointerException e) {
            getLog().error("Error reading jar file manifest " + jarFile, e);
            return false;
        }
    }


    private boolean deleteDirectory(File directoryToBeDeleted) {
        try {
            Files.walk(directoryToBeDeleted.toPath())
            .sorted(Comparator.reverseOrder())   // delete children before parent
            .forEach(p -> {
                try { Files.delete(p); }
                catch (IOException e) { throw new RuntimeException(e); }
            });
            return true;
        } catch (IOException e) {
            getLog().error("Error deleting directory " + directoryToBeDeleted, e);
            return false;
        }
    }

    enum ModuleType {
        NAMED, AUTOMATIC, UNNAMED, EXCLUDED
    }
}