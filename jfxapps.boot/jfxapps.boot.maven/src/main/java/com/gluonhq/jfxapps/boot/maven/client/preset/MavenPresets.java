/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.maven.client.preset;

import java.util.Arrays;
import java.util.List;

import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository.Content;
import com.gluonhq.jfxapps.boot.maven.client.type.Maven;
import com.gluonhq.jfxapps.boot.maven.client.type.Nexus;

public class MavenPresets {

    public static final String MAVEN = "Maven Central";
    public static final String SONATYPE = "Sonatype";
    public static final String GLUON_NEXUS = "Gluon Nexus";
    public static final String LOCAL = "Local";

    private static final List<Repository> presetRepositories = Arrays.asList(
            Repository.builder().id(MAVEN).type(Maven.class)
                    .url("https://repo1.maven.org/maven2/")
                    .build(),
            Repository.builder().id(SONATYPE).type(Nexus.class)
                    .url("https://oss.sonatype.org/content/repositories/snapshots")
                    .contentType(Content.SNAPSHOT).build(),
            Repository.builder().id(SONATYPE).type(Nexus.class)
                    .url("https://oss.sonatype.org/content/repositories/releases")
                    .contentType(Content.RELEASE)
                    .build(),
            Repository.builder().id(GLUON_NEXUS).type(Nexus.class)
                    .url("https://nexus.gluonhq.com/nexus/content/repositories/releases")
                    .contentType(Content.RELEASE).build());

    public static List<Repository> getPresetRepositories() {
        return presetRepositories;
    }

}
