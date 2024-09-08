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
package com.gluonhq.jfxapps.app.devtools.ext.strchk.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    private final File root;
    private final String name;
    private final List<Project> subProjects = new ArrayList<>();
    private final Map<String, List<ClassFile>> classes = new HashMap<>();
    private final Map<String, ProjectFile> resources = new HashMap<>();
    private ModuleFile moduleDescriptor;

    public Project(File root, String name) {
        super();
        this.root = root;
        this.name = name;
    }

    public ModuleFile getModuleDescriptor() {
        return moduleDescriptor;
    }

    public void setModuleDescriptor(ModuleFile moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;
    }

    public File getRoot() {
        return root;
    }

    public String getName() {
        return name;
    }

    public List<Project> getSubProjects() {
        return subProjects;
    }

    public Map<String, List<ClassFile>> getClasses() {
        return classes;
    }

    public Map<String, ProjectFile> getResources() {
        return resources;
    }

    @Override
    public String toString() {
        return "Project [name=" + name + ", subProjects=" + subProjects.size() + ", classes=" + classes.size()
                + ", resources=" + resources.size() + "]";
    }

}
