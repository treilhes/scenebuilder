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
package com.gluonhq.jfxapps.app.devtools.projects.controller;

import java.io.File;
import java.util.List;

import com.gluonhq.jfxapps.app.devtools.api.project.Project;
import com.gluonhq.jfxapps.app.devtools.api.project.ProjectEvents;
import com.gluonhq.jfxapps.app.devtools.api.project.fs.FolderDefinitions;
import com.gluonhq.jfxapps.app.devtools.api.project.fs.WatcherInitializer;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fs.watcher.Watcher;

@ApplicationInstanceSingleton
public class ProjectController {

    private final ProjectEvents projectEvents;
    private final List<WatcherInitializer> watcherInitializers;

    public ProjectController(ProjectEvents projectEvents, List<WatcherInitializer> watcherInitializers) {
        super();
        this.projectEvents = projectEvents;
        this.watcherInitializers = watcherInitializers;
    }

    public void loadProject(File projectFolder) {

        var previous = projectEvents.project().get();
        var rootPath = projectFolder.getParentFile().toPath();
        var watcher = new Watcher();

        var rootType = FolderDefinitions.MAVEN_PROJECT.copy()
                .withId("ROOT")
                .withExclusionPatterns("docs")
                .build();

        watcherInitializers.forEach(WatcherInitializer::initialize);

        var folder = rootType.createFolder(watcher, null, rootPath);
        folder.requestRefresh();

        if (previous != null) {
            previous.getWatcher().stopWatch();
        }

        var project = new Project(watcher, rootPath.getFileName().toString(), folder);

        watcher.startWatch();
        projectEvents.project().set(project);
    }

}
