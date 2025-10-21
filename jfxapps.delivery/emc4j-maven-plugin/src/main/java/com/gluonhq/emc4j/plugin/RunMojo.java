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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.gluonhq.emc4j.plugin.javaconfig.JavaProcessConfig;

@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class RunMojo extends Emc4jAbstractMojo {

    private static final String BOOT_CONFIG_FILENAME = "boot.config";

    private static final String DEBUG_OPTION = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=%s,address=0.0.0.0:%s";

    @Parameter(property = "applicationId", required = true)
    private String applicationId;

    @Parameter(property = "debug", defaultValue = "false")
    private boolean debug;

    @Parameter(property = "debugSuspend", defaultValue = "true")
    private boolean debugSuspend;

    @Parameter(property = "debugPort", defaultValue = "8000")
    private int debugPort;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File configFile = new File(getOutputDirectory(), BOOT_CONFIG_FILENAME);

            cleanOutputDirectory();

            var javaProcessConfig = initializeJavaProcessConfig();

            if (debug) {
                javaProcessConfig.addJvmArg(String.format(DEBUG_OPTION, debugSuspend ? "y" : "n", String.valueOf(debugPort)));
            }

            javaProcessConfig.addJvmArg("-Djfxapps.registry.snapshotsAllowed=true");

            String localRepoPath = getRepositorySession().getLocalRepository().getBasedir().getAbsolutePath();
            javaProcessConfig.addJvmArg(String.format("-Djfxapps.repository.directory=\"%s\"", localRepoPath));

            javaProcessConfig.addAppArg("-a");
            javaProcessConfig.addAppArg(applicationId);

            generateConfigFile(javaProcessConfig, configFile);

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
            getLog().info(String.format(cmd, String.join(" ", command), jcfg.getWorkingDir()));

            // Start process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.directory(jcfg.getWorkingDir());

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
            File targetProfile = new File(getOutputDirectory(), "application-" + getProfile() + extension);
            Files.copy(getProfileFile().toPath(), targetProfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }




}