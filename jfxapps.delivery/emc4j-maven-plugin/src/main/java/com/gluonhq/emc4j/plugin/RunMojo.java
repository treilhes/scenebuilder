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
import java.util.List;
import java.util.Map.Entry;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.gluonhq.emc4j.plugin.javaconfig.JavaProcessConfig;
import com.gluonhq.emc4j.plugin.util.FsUtil;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class RunMojo extends Emc4jAbstractMojo {

    private static final String RUN_DIRECTORY = "run";

    private static final String BOOT_CONFIG_FILENAME = "boot.config";

    private static final String DEBUG_OPTION = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=%s,address=0.0.0.0:%s";

    /**
     * The application identifier to run.
     * The application identifier must be contained in one of the provided registry dependencies.
     */
    @Parameter(property = "applicationId", required = true)
    private String applicationId;

    /**
     * If true, the JVM will be started in debug mode, allowing a debugger to attach.
     * The default value is false.
     */
    @Parameter(property = "debug", defaultValue = "false")
    private boolean debug;
    /**
     * If true, the JVM will wait for a debugger to attach before starting execution.
     * The default value is true.
     */
    @Parameter(property = "debugSuspend", defaultValue = "true")
    private boolean debugSuspend;

    /**
     * The debug port to use when starting the JVM in debug mode.
     * The default port is 8000.
     */
    @Parameter(property = "debugPort", defaultValue = "8000")
    private int debugPort;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            cleanRunDirectory();

            var javaProcessConfig = initializeJavaProcessConfig();

            copyDependencies(javaProcessConfig, getRunDirectory());

            if (debug) {
                javaProcessConfig.addJvmArg(String.format(DEBUG_OPTION, debugSuspend ? "y" : "n", String.valueOf(debugPort)));
            }

            javaProcessConfig.addJvmArg("-Djfxapps.registry.snapshotsAllowed=true");

            String localRepoPath = getRepositorySession().getLocalRepository().getBasedir().getAbsolutePath();
            javaProcessConfig.addJvmArg(String.format("-Djfxapps.repository.directory=\"%s\"", localRepoPath));

            javaProcessConfig.addAppArg("-a");
            javaProcessConfig.addAppArg(applicationId);

            generateConfigFile(javaProcessConfig, getRunDirectory());

            copyProfileToTarget();

            run(javaProcessConfig);

            System.out.println();
        } catch (Exception e) {
            throw new MojoExecutionException("Error during run", e);
        }

    }

    public void run(JavaProcessConfig jcfg) throws MojoExecutionException {
        try {

            List<String> command = new ArrayList<>();
            command.add(jcfg.getJavaBin().getAbsolutePath());
            command.add("@" + BOOT_CONFIG_FILENAME);

            String cmd = "Running command: %s, working directory: %s";
            getLog().info(String.format(cmd, String.join(" ", command), getRunDirectory().getAbsolutePath()));

            // Start process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.directory(getRunDirectory());

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

    private void copyProfileToTarget() throws IOException {
        if (getProfile() != null && getProfileFile() != null) {
            String extension = getProfileFile().getName().substring(getProfileFile().getName().lastIndexOf('.'));
            File targetProfile = new File(getRunDirectory(), "application-" + getProfile() + extension);
            Files.copy(getProfileFile().toPath(), targetProfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected void cleanRunDirectory() throws IOException {
        File runFolder = getRunDirectory();
        if (runFolder.exists()) {
            FsUtil.deleteDirectory(runFolder);
        }
    }

    public File getRunDirectory() {
        return new File(getOutputDirectory(), RUN_DIRECTORY);
    }



    public void generateConfigFile(JavaProcessConfig jcfg, File targetFolder) throws Exception {

        File configFile = new File(targetFolder, BOOT_CONFIG_FILENAME);

        String patchFormat = "--patch-module %s=%s";
        String addReadFormat = "--add-reads %s";
        String addOpensFormat = "--add-opens %s";
        String addExportsFormat = "--add-exports %s";

        StringBuilder sb = new StringBuilder();

        sb.append("--module-path ./mp").append("\n");

        sb.append("--class-path ./cp/*").append("\n");

        for (String addRead : jcfg.getAddReads()) {
            sb.append(String.format(addReadFormat, addRead)).append("\n");
        }
        for (String addOpen : jcfg.getAddOpens()) {
            sb.append(String.format(addOpensFormat, addOpen)).append("\n");
        }
        for (String addExport : jcfg.getAddExports()) {
            sb.append(String.format(addExportsFormat, addExport)).append("\n");
        }

        for (Entry<String, List<File>> patch : jcfg.getPatchModules().entrySet()) {
            for (File f : patch.getValue()) {
                String patchPath = targetFolder.getAbsolutePath() + File.separator + "patch" + File.separator + f.getName();
                sb.append(String.format(patchFormat, patch.getKey(), patchPath)).append("\n");
            }
        }

        for (String arg : jcfg.getJvmArgs()) {
            sb.append(arg).append("\n");
        }

        sb.append("-m").append(" ").append(jcfg.getMainModule()).append("/").append(jcfg.getMainClass()).append("\n");

        for (String arg : jcfg.getAppArgs()) {
            sb.append(arg).append("\n");
        }

        Files.writeString(configFile.toPath(), sb);
    }
}