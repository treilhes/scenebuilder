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
package com.gluonhq.emc4j.plugin.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import com.gluonhq.emc4j.plugin.bootconfig.BootConfig;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

public class BootConfigMask {

    protected static final String DEFAULT_BOOT_CONFIG = "boot-config.xml";

    private final PluginArtifact pluginArtifact;
    private BootConfig config;

    public BootConfigMask(PluginArtifact pluginArtifact) {
        this.pluginArtifact = pluginArtifact;
    }

    public BootConfig getConfig() {
        if (config == null) {
            try {
                ClassLoader classLoader = pluginArtifact.classloader();
                config = loadBootConfig(classLoader);
            } catch (Exception e) {
                throw new RuntimeException("Error loading boot configuration", e);
            }
        }
        return config;
    }

    public boolean isForcedAsModule(File f) {
        return getConfig().getForceAsModules().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    public boolean isForcedAsClasspath(File f) {
        return getConfig().getForceAsClasspaths().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    public boolean isExcludedDependency(File f) {
        return getConfig().getExcludedDependencies().stream()
                .anyMatch(d -> f.getName().startsWith(d.getArtifactId() + "-")
                        && f.getName().endsWith(".jar"));
    }

    private BootConfig loadBootConfig(ClassLoader classLoader) throws Exception {

        try (InputStream input = classLoader.getResourceAsStream(DEFAULT_BOOT_CONFIG)) {
            JAXBContext ctx = JAXBContext.newInstance(BootConfig.class);

            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            StreamSource source = new StreamSource(input);
            JAXBElement<BootConfig> element = unmarshaller.unmarshal(source, BootConfig.class);

            return element.getValue();
        }
    }
    
    
}
