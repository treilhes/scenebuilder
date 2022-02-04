/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.controllibrary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.controllibrary.action.LibraryPanelActions;
import com.oracle.javafx.scenebuilder.controllibrary.controller.JarAnalysisReportController;
import com.oracle.javafx.scenebuilder.controllibrary.controller.LibraryController;
import com.oracle.javafx.scenebuilder.controllibrary.drag.source.ControlLibraryDragSource;
import com.oracle.javafx.scenebuilder.controllibrary.importer.ImportWindowController;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibraryDefaultFilter;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibraryDialogConfiguration;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.explorer.ControlFileExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.library.explorer.ControlFolderExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.library.explorer.ControlMavenArtifactExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.menu.LibraryMenuProvider;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryPanelController;
import com.oracle.javafx.scenebuilder.controllibrary.preferences.global.DisplayModePreference;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

public class ControlLibraryExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("7b20f189-6c56-4814-a168-597341054616");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                BuiltinLibrary.class,
                ControlFileExplorer.class,
                ControlFolderExplorer.class,
                ControlLibrary.class,
                ControlLibraryDefaultFilter.class,
                ControlLibraryDialogConfiguration.class,
                ControlMavenArtifactExplorer.class,
                DisplayModePreference.class,
                ImportWindowController.class,
                JarAnalysisReportController.class,
                LibraryController.class,
                ControlLibraryDragSource.class,
                ControlLibraryDragSource.Factory.class,
                LibraryMenuProvider.class,
                LibraryPanelActions.ImportSelectionAction.class,
                LibraryPanelActions.ManageJarFxmlAction.class,
                LibraryPanelActions.RevealCustomFolderAction.class,
                LibraryPanelActions.ShowJarAnalysisReportAction.class,
                LibraryPanelActions.ViewAsListAction.class,
                LibraryPanelActions.ViewAsSectionsAction.class,
                LibraryPanelController.class
            );
     // @formatter:on
    }
}
