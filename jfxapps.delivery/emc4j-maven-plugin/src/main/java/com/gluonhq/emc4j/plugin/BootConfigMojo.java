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
import java.util.Comparator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.aether.artifact.Artifact;

import com.gluonhq.emc4j.plugin.bootconfig.BootConfig;
import com.gluonhq.emc4j.plugin.bootconfig.Dependency;
import com.gluonhq.emc4j.plugin.util.FsUtil;
import com.gluonhq.emc4j.plugin.util.JpmsHelper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

@Mojo(name = "boot-config", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.NONE, threadSafe = true)
public class BootConfigMojo extends Emc4jAbstractMojo {

    private static final String RUN_DIRECTORY = "boot-config";

    private static final String BOOT_CONFIG_FILENAME = "boot-config.xml";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            cleanRunDirectory();
            getRunDirectory().mkdirs();
            BootConfig bootConfig = loadFromArtifact();
            File target = new File(getRunDirectory(), BOOT_CONFIG_FILENAME);
            JAXBContext ctx = JAXBContext.newInstance(BootConfig.class);
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.marshal(bootConfig, target);

        } catch (Exception e) {
            throw new MojoExecutionException("Error during run", e);
        }

    }

    public BootConfig loadFromArtifact() throws Exception {
        var jpmsHelper = new JpmsHelper(getLog());
        var factory = getPluginArtifactFactory();
        var appBinPluginArtifact = factory.createArtifact(BOOT_JAR_GROUP_ID, BOOT_JAR_ARTIFACT_ID, getEmc4jVersion());


        List<Artifact> appArtifacts = appBinPluginArtifact.resolveDependencies();

        BootConfig config = new BootConfig();
        Comparator<Artifact> comparator = Comparator.comparing((Artifact a) -> a.getGroupId())
                .thenComparing((Artifact a) -> a.getGroupId());
        appArtifacts.stream()
            .sorted(comparator)
            .forEach(a -> {
                    File f = a.getFile();
                    Dependency d = new Dependency();
                    d.setGroupId(a.getGroupId());
                    d.setArtifactId(a.getArtifactId());
                    if (jpmsHelper.isModular(f)) {
                        config.getForceAsModules().add(d);
                    } else if (jpmsHelper.isAutomaticModule(f)) {
                        config.getForceAsModules().add(d);
                    } else {
                        config.getForceAsClasspaths().add(d);
                    }
                });

        return config;
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


}