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
package com.gluonhq.jfxapps.boot.registry.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.gluonhq.jfxapps.boot.registry.RegistryArtifact;
import com.gluonhq.jfxapps.boot.registry.internal._Component;
import com.gluonhq.jfxapps.boot.registry.model._Model;
import com.gluonhq.jfxapps.boot.registry.repository._Repository;

@Configuration
@ConfigurationProperties(prefix = "jfxapps.registry")
@EntityScan(basePackageClasses = _Model.class)
@EnableJpaRepositories(basePackageClasses = _Repository.class)
@ComponentScan(basePackageClasses = _Component.class)
public class RegistryConfig {

    private RegistryArtifact boot;
    private boolean snapshotsAllowed;
    private Map<String, RegistryArtifact> defaults = new HashMap<>();

    public Map<String, RegistryArtifact> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, RegistryArtifact> defaults) {
        this.defaults = defaults;
    }

    public RegistryArtifact getBoot() {
        return boot;
    }

    public void setBoot(RegistryArtifact boot) {
        this.boot = boot;
    }

    public boolean isSnapshotsAllowed() {
        return snapshotsAllowed;
    }

    public void setSnapshotsAllowed(boolean snapshotsAllowed) {
        this.snapshotsAllowed = snapshotsAllowed;
    }

}
