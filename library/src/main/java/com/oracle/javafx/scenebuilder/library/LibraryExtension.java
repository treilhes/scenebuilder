/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.library;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryDialogFactory;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryStoreFactory;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.manager.ImportProgressDialogController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.manager.LibraryDialogController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.maven.MavenDialogController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.maven.repository.RepositoryDialogController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.maven.repository.RepositoryManagerController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.maven.search.SearchMavenDialogController;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferencesFactory;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoryPreferences;


public class LibraryExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("3fddfa3c-0963-40da-8a59-895284c4b851");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                LibraryDialogController.class,
                SearchMavenDialogController.class,
                MavenDialogController.class,
                MavenArtifactsPreferences.class,
                MavenArtifactPreferences.class,
                MavenRepositoriesPreferences.class,
                MavenRepositoryPreferences.class,
                RepositoryManagerController.class,
                MavenArtifactsPreferencesFactory.class,
                LibraryDialogFactory.class,
                LibraryStoreFactory.class,
                ImportProgressDialogController.class,
                RepositoryDialogController.class
            );
     // @formatter:on
    }
}
