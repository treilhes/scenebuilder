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
package com.gluonhq.jfxapps.metadata.java.model;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private String targetPackage;
    private String uuid;
    private String moduleName;
    private List<String> requiredModules = new ArrayList<>();
    private String extensionClassSimpleName;
    private String propertyNamesClassSimpleName;
    String componentCustomizationClassName;
    String componentPropertyCustomizationClassName;
    String valuePropertyCustomizationClassName;
    private String componentSuperClass;

    public Context() {
        super();
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<String> getRequiredModules() {
        return requiredModules;
    }

    public void setRequiredModules(List<String> requiredModules) {
        this.requiredModules = requiredModules;
    }

    public String getExtensionClassSimpleName() {
        return extensionClassSimpleName;
    }

    public void setExtensionClassSimpleName(String extensionClassSimpleName) {
        this.extensionClassSimpleName = extensionClassSimpleName;
    }

    public String getPropertyNamesClassSimpleName() {
        return propertyNamesClassSimpleName;
    }

    public void setPropertyNamesClassSimpleName(String propertyNamesClassSimpleName) {
        this.propertyNamesClassSimpleName = propertyNamesClassSimpleName;
    }

    public String getComponentCustomizationClassName() {
        return componentCustomizationClassName;
    }

    public void setComponentCustomizationClassName(String componentCustomizationClassName) {
        this.componentCustomizationClassName = componentCustomizationClassName;
    }

    public String getComponentPropertyCustomizationClassName() {
        return componentPropertyCustomizationClassName;
    }

    public void setComponentPropertyCustomizationClassName(String componentPropertyCustomizationClassName) {
        this.componentPropertyCustomizationClassName = componentPropertyCustomizationClassName;
    }

    public String getValuePropertyCustomizationClassName() {
        return valuePropertyCustomizationClassName;
    }

    public void setValuePropertyCustomizationClassName(String valuePropertyCustomizationClassName) {
        this.valuePropertyCustomizationClassName = valuePropertyCustomizationClassName;
    }

    public void setComponentSuperClassName(String componentSuperClass) {
        this.componentSuperClass = componentSuperClass;
    }

    public String getComponentSuperClassName() {
        return componentSuperClass;
    }

}