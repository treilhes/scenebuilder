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
package com.gluonhq.jfxapps.metadata.java.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaGenerationContext {
    private File inputResourceFolder;
    private File sourceFolder;
    private String targetPackage;
    private int keepLastPackages = 3;
    private String extensionName;
    private String moduleName;
    private String metadataPrefix;
    private UUID uuid;
    private final List<String> moduleRequires = new ArrayList<>();

    private String componentCustomizationTemplate;
    private String componentPropertyCustomizationTemplate;
    private String valuePropertyCustomizationTemplate;
    private String staticValuePropertyCustomizationTemplate;
    private String componentConstructorCustomizationTemplate;

    private String targetComponentCustomizationClass;
    private String targetComponentPropertyCustomizationClass;
    private String targetValuePropertyCustomizationClass;
    private String targetComponentSuperClass;

    public File getSourceFolder() {
        return sourceFolder;
    }
    public void setSourceFolder(File outputFolder) {
        this.sourceFolder = outputFolder;
    }

    public File getInputResourceFolder() {
        return inputResourceFolder;
    }
    public void setInputResourceFolder(File inputResourceFolder) {
        this.inputResourceFolder = inputResourceFolder;
    }
    public String getTargetPackage() {
        return targetPackage;
    }
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
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
    /**
     * Number of the original package segment to keep in the generated package name.
     * (Ex: if 3, com.gluonhq.jfxapps.metadata.java.api -> metadata.java.api)
     * @return the number of package segments to keep
     */
    public int getKeepLastPackages() {
        return keepLastPackages;
    }
    public void setKeepLastPackages(int keepLastPackages) {
        this.keepLastPackages = keepLastPackages;
    }
    public String getComponentCustomizationTemplate() {
        return componentCustomizationTemplate;
    }
    public void setComponentCustomizationTemplate(String componentCustomizationTemplate) {
        this.componentCustomizationTemplate = componentCustomizationTemplate;
    }
    public String getComponentPropertyCustomizationTemplate() {
        return componentPropertyCustomizationTemplate;
    }
    public void setComponentPropertyCustomizationTemplate(String componentPropertyCustomizationTemplate) {
        this.componentPropertyCustomizationTemplate = componentPropertyCustomizationTemplate;
    }
    public String getValuePropertyCustomizationTemplate() {
        return valuePropertyCustomizationTemplate;
    }
    public void setValuePropertyCustomizationTemplate(String valuePropertyCustomizationTemplate) {
        this.valuePropertyCustomizationTemplate = valuePropertyCustomizationTemplate;
    }
    public String getStaticValuePropertyCustomizationTemplate() {
        return staticValuePropertyCustomizationTemplate;
    }
    public void setStaticValuePropertyCustomizationTemplate(String staticValuePropertyCustomizationTemplate) {
        this.staticValuePropertyCustomizationTemplate = staticValuePropertyCustomizationTemplate;
    }

    public String getComponentConstructorCustomizationTemplate() {
        return componentConstructorCustomizationTemplate;
    }
    public void setComponentConstructorCustomizationTemplate(String componentConstructorCustomizationTemplate) {
        this.componentConstructorCustomizationTemplate = componentConstructorCustomizationTemplate;
    }
    public String getTargetComponentCustomizationClass() {
        return targetComponentCustomizationClass;
    }
    public void setTargetComponentCustomizationClass(String targetComponentCustomizationClass) {
        this.targetComponentCustomizationClass = targetComponentCustomizationClass;
    }
    public String getTargetComponentPropertyCustomizationClass() {
        return targetComponentPropertyCustomizationClass;
    }
    public void setTargetComponentPropertyCustomizationClass(String targetComponentPropertyCustomizationClass) {
        this.targetComponentPropertyCustomizationClass = targetComponentPropertyCustomizationClass;
    }
    public String getTargetValuePropertyCustomizationClass() {
        return targetValuePropertyCustomizationClass;
    }
    public void setTargetValuePropertyCustomizationClass(String targetValuePropertyCustomizationClass) {
        this.targetValuePropertyCustomizationClass = targetValuePropertyCustomizationClass;
    }

    public String getTargetComponentSuperClass() {
        return targetComponentSuperClass;
    }

    public void setTargetComponentSuperClass(String targetComponentSuperClass) {
        this.targetComponentSuperClass = targetComponentSuperClass;
    }



}
