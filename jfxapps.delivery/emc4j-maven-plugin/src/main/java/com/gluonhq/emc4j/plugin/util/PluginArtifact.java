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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;

public class PluginArtifact {

    private final PluginArtifactFactory pluginArtifactFactory;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String coords;

    private Artifact artifact;
    private List<Artifact> dependencies;

    public PluginArtifact(PluginArtifactFactory pluginArtifactFactory, String groupId, String artifactId,
            String version) {
        this(pluginArtifactFactory, groupId, artifactId, version, "jar");
    }

    public PluginArtifact(PluginArtifactFactory pluginArtifactFactory, String groupId, String artifactId,
            String version, String classifier) {
        this.pluginArtifactFactory = pluginArtifactFactory;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.coords = groupId + ":" + artifactId + ":" + classifier + ":" + version;
    }

    public Artifact resolve() throws Exception {
        if (artifact == null) {
            artifact = pluginArtifactFactory.resolveArtifact(coords);
        }
        return artifact;
    }

    public URLClassLoader classloader() throws Exception {
        File file = resolve().getFile();
        return new URLClassLoader(new URL[] { file.toURI().toURL() },
                Thread.currentThread().getContextClassLoader());
    }

    public List<Artifact> resolveDependencies() throws Exception {

        if (dependencies == null) {
            dependencies = pluginArtifactFactory.resolveDependencies(coords);
        }

        return dependencies;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

}
