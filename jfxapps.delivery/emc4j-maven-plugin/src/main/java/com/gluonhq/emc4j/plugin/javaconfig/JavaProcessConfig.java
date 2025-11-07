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
package com.gluonhq.emc4j.plugin.javaconfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gluonhq.emc4j.plugin.util.FsUtil;

public class JavaProcessConfig {

    private File javaBin;
    private String mainClass;
    private String mainModule;
    private File mainJar;
    private List<File> modules;
    private List<File> automaticModules;
    private List<File> classpath;
    private Map<String, List<File>> patchModules;
    private List<String> addReads;
    private List<String> addOpens;
    private List<String> addExports;

    private List<String> jvmArgs;
    private List<String> appArgs;

    public JavaProcessConfig() {
        this.modules = new ArrayList<>();
        this.automaticModules = new ArrayList<>();
        this.classpath = new ArrayList<>();
        this.patchModules = new HashMap<>();
        this.addReads = new ArrayList<>();
        this.addOpens = new ArrayList<>();
        this.addExports = new ArrayList<>();
        this.jvmArgs = new ArrayList<>();
        this.appArgs = new ArrayList<>();
    }
    public File getJavaBin() {
        return javaBin;
    }
    public void setJavaBin(File javaBin) {
        this.javaBin = javaBin;
    }
    public String getMainClass() {
        return mainClass;
    }
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    public String getMainModule() {
        return mainModule;
    }
    public void setMainModule(String mainModule) {
        this.mainModule = mainModule;
    }
    public File getMainJar() {
        return mainJar;
    }
    public void setMainJar(File mainJar) {
        this.mainJar = mainJar;
    }
    public List<File> getModules() {
        return modules;
    }
    public void addModule(File module) {
        this.modules.add(module);
    }
    public List<File> getAutomaticModules() {
        return automaticModules;
    }
    public void addAutomaticModule(File automaticModule) {
        this.automaticModules.add(automaticModule);
    }
    public List<File> getClasspath() {
        return classpath;
    }
    public void addClasspath(File classpathEntry) {
        this.classpath.add(classpathEntry);
    }

    public Map<String, List<File>> getPatchModules() {
        return patchModules;
    }
    public void addPatchModule(String moduleName, File patchModule) {
        this.patchModules.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(patchModule);
    }
    public List<String> getAddReads() {
        return addReads;
    }
    public void addAddReads(String addReads) {
        this.addReads.add(addReads);
    }
    public List<String> getAddOpens() {
        return addOpens;
    }
    public void addAddOpens(String addOpens) {
        this.addOpens.add(addOpens);
    }
    public List<String> getAddExports() {
        return addExports;
    }
    public void addAddExports(String addExports) {
        this.addExports.add(addExports);
    }
    public List<String> getJvmArgs() {
        return jvmArgs;
    }
    public void addJvmArg(String jvmArg) {
        this.jvmArgs.add(jvmArg);
    }
    public List<String> getAppArgs() {
        return appArgs;
    }
    public void addAppArg(String appArg) {
        this.appArgs.add(appArg);
    }

    public List<String> toCommand() {
        List<String> command = new ArrayList<>();
        command.add(javaBin.getAbsolutePath());
        command.addAll(jvmArgs);
        command.add("--module-path");
        command.add(FsUtil.toPathesString(modules, automaticModules));
        command.add("--class-path");
        command.add(FsUtil.toPathesString(classpath));
        command.add("-m");
        command.add(getMainModule() + "/" + getMainClass());
        command.addAll(appArgs);
        return command;
    }

    @Override
    public String toString() {
        return String.join(" ", toCommand());
    }
}
