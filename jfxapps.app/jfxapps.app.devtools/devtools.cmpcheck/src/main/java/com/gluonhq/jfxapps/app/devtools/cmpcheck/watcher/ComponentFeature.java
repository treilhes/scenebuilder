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
package com.gluonhq.jfxapps.app.devtools.cmpcheck.watcher;

import java.util.List;

import com.gluonhq.jfxapps.app.devtools.api.project.fs.content.JavaAstContent;
import com.gluonhq.jfxapps.app.devtools.api.project.fs.feature.MavenProjectFeature;
import com.gluonhq.jfxapps.core.api.fs.watcher.File;
import com.gluonhq.jfxapps.core.api.fs.watcher.FileFeature;
import com.gluonhq.jfxapps.core.api.fs.watcher.FileFeatureHandler;
import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;

public class ComponentFeature implements FileFeature {

    @Override
    public void refresh(File item) {
        var astContent = item.getContent(JavaAstContent.class, JavaAstContent::supplier);
        var cu = astContent.getContent();

        MavenProjectFeature mavenProject = findParentMavenProject(item.getParent());
        System.out.println();
    }

    @Override
    public void onRemove() {
        // TODO Auto-generated method stub

    }


    public List<String> getAnnotations() {
        return List.of();
    }

    /**
     * Recursively search for a MavenProjectFeature in parent folders.
     *
     * @param folder the starting folder
     * @return the found MavenProjectFeature or null if not found
     */
    private MavenProjectFeature findParentMavenProject(Folder folder) {
        if (folder == null) {
            return null;
        }

        var feature = folder.getFeature(MavenProjectFeature.class);

        if (feature != null) {
            return feature;
        }

        if (folder.getParent() == null) {
            return null;
        }

        return findParentMavenProject(folder.getParent());
    }

    public static class Handler implements FileFeatureHandler {

        @Override
        public Class<ComponentFeature> getFeatureClass() {
            return ComponentFeature.class;
        }

        @Override
        public boolean isApplicable(File item) {
            return item.getPath().toString().endsWith(".java");
        }

        @Override
        public FileFeature createFeature(File item) {
            return new ComponentFeature();
        }
    }
}
