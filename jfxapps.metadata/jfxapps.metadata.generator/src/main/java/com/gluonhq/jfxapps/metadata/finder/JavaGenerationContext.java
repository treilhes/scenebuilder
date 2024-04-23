/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.metadata.finder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.metadata.finder.api.Executor;

public class JavaGenerationContext {
    private File sourceFolder;
    private String targetPackage;
    private Class<Executor> executorClass;
    private String extensionName;
    private String moduleName;
    private String metadataPrefix;
    private UUID uuid;
    private final List<String> moduleRequires = new ArrayList<>();


    public File getSourceFolder() {
        return sourceFolder;
    }
    public void setSourceFolder(File outputFolder) {
        this.sourceFolder = outputFolder;
    }

    public String getTargetPackage() {
        return targetPackage;
    }
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }
    public Class<Executor> getExecutorClass() {
        return executorClass;
    }
    public void setExecutorClass(Class<Executor> executorClass) {
        this.executorClass = executorClass;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    public String getModuleName() {
        return moduleName;
    }
    public void addModuleRequire(String s) {
        moduleRequires.add(s);
    }
    public List<String> getModuleRequires() {
        return moduleRequires;
    }
    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getExtensionName() {
        return extensionName;
    }
    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }
    public String getMetadataPrefix() {
        return metadataPrefix;
    }
    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }


}
