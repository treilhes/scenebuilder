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
package com.oracle.javafx.scenebuilder.imagelibrary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.scenebuilder.fxml.api.SbApiExtension;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ImageLibraryFolderMenuProvider;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ImportSelectionAsImageAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ManageJarImageAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.RevealImageFolderAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ShowImageAnalysisReportAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ViewImageAsListAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ViewImageAsSectionsAction;
import com.oracle.javafx.scenebuilder.imagelibrary.action.ViewImageAsToggle;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ImageJarAnalysisReportController;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ImageLibraryController;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ThumbnailServiceController;
import com.oracle.javafx.scenebuilder.imagelibrary.drag.source.ImageLibraryDragSource;
import com.oracle.javafx.scenebuilder.imagelibrary.i18n.I18NImageLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.importer.ImageImportWindowController;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibraryDialogConfiguration;
import com.oracle.javafx.scenebuilder.imagelibrary.library.builtin.ImageBuiltinLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.library.builtin.ImageDefaultLibraryFilter;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageFileExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageFolderExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageMavenArtifactExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.imagelibrary.preferences.global.ImageDisplayModePreference;

public class ImageLibraryExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("7b20f189-6c56-4814-a168-597341054616");

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                I18NImageLibrary.class,
                ImageBuiltinLibrary.class,
                ImageDefaultLibraryFilter.class,
                ImageDisplayModePreference.class,
                ImageFileExplorer.class,
                ImageFolderExplorer.class,
                ImageImportWindowController.class,
                ImageJarAnalysisReportController.class,
                ImageLibrary.class,
                ImageLibraryController.class,
                ImageLibraryDialogConfiguration.class,
                ImageLibraryFolderMenuProvider.class,
                ImageLibraryPanelController.class,
                ImageMavenArtifactExplorer.class,
                ImportSelectionAsImageAction.class,
                ImageLibraryDragSource.class,
                ImageLibraryDragSource.Factory.class,
                ManageJarImageAction.class,
                RevealImageFolderAction.class,
                ShowImageAnalysisReportAction.class,
                ThumbnailServiceController.class,
                ViewImageAsListAction.class,
                ViewImageAsSectionsAction.class,
                ViewImageAsToggle.class
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
