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
package com.oracle.javafx.scenebuilder.imagelibrary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.ComponentScan;

import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryStoreFactory;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ImageLibraryPanelActions;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ImageJarAnalysisReportController;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ImageLibraryController;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ThumbnailServiceController;
import com.oracle.javafx.scenebuilder.imagelibrary.editor.panel.explorer.ImageFileExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.editor.panel.explorer.ImageFolderExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.editor.panel.explorer.ImageMavenArtifactExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.editor.panel.library.ImageImportWindowController;
import com.oracle.javafx.scenebuilder.imagelibrary.editor.panel.library.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.imagelibrary.menu.ImageLibraryMenuProvider;
import com.oracle.javafx.scenebuilder.imagelibrary.preferences.global.ImageDisplayModePreference;
import com.oracle.javafx.scenebuilder.imagelibrary.tmp.ImageLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.tmp.ImageLibraryDialogConfiguration;
import com.oracle.javafx.scenebuilder.imagelibrary.tobeclassed.ImageBuiltinLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.tobeclassed.ImageDefaultLibraryFilter;

@ComponentScan(
        basePackages = {
                "com.oracle.javafx.scenebuilder.imagelibrary.action"
        })
public class ImageLibraryExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("2ba72473-4323-4bd6-ae3e-74a44f1a07b0");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                ImageLibraryPanelActions.class,
                ImageLibraryController.class,
                ImageLibraryMenuProvider.class,
                ImageImportWindowController.class,
                ImageLibraryPanelController.class,
                ImageDisplayModePreference.class,
                ImageLibrary.class,
                ImageBuiltinLibrary.class,
                ImageDefaultLibraryFilter.class,
                ImageLibraryDialogConfiguration.class,
                LibraryStoreFactory.class,
                ImageJarAnalysisReportController.class,
                ImageFileExplorer.class,
                ImageFolderExplorer.class,
                ImageMavenArtifactExplorer.class,
                ThumbnailServiceController.class
            );
     // @formatter:on
    }
    
    // TODO need to mark the dependency to fontbox
    // TODO how to do it? 
    // TODO explicit declaration (=duplicating pom data) ? 
    // TODO or exploring the pom inside the jar (=filtering already provided dependencies and external ones) ?
    // TODO something else ?
//    <dependencies>
//        <dependency>
//            <groupId>org.apache.pdfbox</groupId>
//            <artifactId>fontbox</artifactId>
//            <version>2.0.23</version>
//        </dependency>
//    </dependencies>
}
