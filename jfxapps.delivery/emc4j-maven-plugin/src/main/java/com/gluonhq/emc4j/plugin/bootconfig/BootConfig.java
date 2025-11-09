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
package com.gluonhq.emc4j.plugin.bootconfig;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "boot-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class BootConfig {
    @XmlElement(name = "debug")
    private boolean debug;
    @XmlElement(name = "profile")
    private List<String> profiles = new ArrayList<>();
    @XmlElement(name = "java-option")
    private List<String> javaOptions = new ArrayList<>();
    @XmlElement(name = "add-reads")
    private List<AddReads> addReads = new ArrayList<>();
    @XmlElement(name = "add-exports")
    private List<AddExports> addExports = new ArrayList<>();
    @XmlElement(name = "add-opens")
    private List<AddOpens> addOpens = new ArrayList<>();
    @XmlElement(name = "patch-module")
    private List<PatchModule> patchModules = new ArrayList<>();

    @XmlElementWrapper(name = "modules")
    @XmlElement(name = "dependency")
    private List<Dependency> forceAsModules = new ArrayList<>();

    @XmlElementWrapper(name = "classpath")
    @XmlElement(name = "dependency")
    private List<Dependency> forceAsClasspaths = new ArrayList<>();

    @XmlElementWrapper(name = "excluded")
    @XmlElement(name = "dependency")
    private List<Dependency> excludedDependencies = new ArrayList<>();

    public List<AddReads> getAddReads() {
        return addReads;
    }
    public void setAddReads(List<AddReads> addReads) {
        this.addReads = addReads;
    }
    public List<AddExports> getAddExports() {
        return addExports;
    }
    public void setAddExports(List<AddExports> addExports) {
        this.addExports = addExports;
    }
    public List<AddOpens> getAddOpens() {
        return addOpens;
    }
    public void setAddOpens(List<AddOpens> addOpens) {
        this.addOpens = addOpens;
    }
    public List<PatchModule> getPatchModules() {
        return patchModules;
    }
    public void setPatchModules(List<PatchModule> patchModules) {
        this.patchModules = patchModules;
    }
    public List<Dependency> getForceAsModules() {
        return forceAsModules;
    }
    public void setForceAsModules(List<Dependency> forceAsModules) {
        this.forceAsModules = forceAsModules;
    }
    public List<Dependency> getExcludedDependencies() {
        return excludedDependencies;
    }
    public void setExcludedDependencies(List<Dependency> excludedDependencies) {
        this.excludedDependencies = excludedDependencies;
    }
    public List<String> getJavaOptions() {
        return javaOptions;
    }
    public void setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
    }
    public List<String> getProfiles() {
        return profiles;
    }
    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }
    public boolean isDebug() {
        return debug;
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public List<Dependency> getForceAsClasspaths() {
        return forceAsClasspaths;
    }
    public void setForceAsClasspaths(List<Dependency> forceAsClasspaths) {
        this.forceAsClasspaths = forceAsClasspaths;
    }
}
