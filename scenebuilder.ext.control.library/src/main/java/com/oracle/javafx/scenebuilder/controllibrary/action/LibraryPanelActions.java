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
package com.oracle.javafx.scenebuilder.controllibrary.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.controllibrary.controller.JarAnalysisReportController;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryPanelController;
import com.oracle.javafx.scenebuilder.controllibrary.preferences.global.DisplayModePreference;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.library.api.LibraryDialogFactory;

public class LibraryPanelActions {

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.show.jar.analysis.report", descriptionKey = "action.description.show.jar.analysis.report", accelerator = "CTRL+J")
    public static class ShowJarAnalysisReportAction extends AbstractAction {

        private final DocumentWindow documentWindowController;
        private final JarAnalysisReportController jarAnalysisReportController;

        public ShowJarAnalysisReportAction(@Autowired Api api, @Autowired @Lazy DocumentWindow documentWindowController,
                @Autowired @Lazy JarAnalysisReportController jarAnalysisReportController) {
            super(api);
            this.documentWindowController = documentWindowController;
            this.jarAnalysisReportController = jarAnalysisReportController;
        }

        @Override
        public boolean canPerform() {
            return true;
        }

        @Override
        public ActionStatus perform() {
            // jarAnalysisReportController.setToolStylesheet(documentWindowController.getToolStylesheet());
            jarAnalysisReportController.openWindow();
            return ActionStatus.DONE;
        }

    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.reveal.custom.folder", descriptionKey = "action.description.reveal.custom.folder")
    public static class RevealCustomFolderAction extends AbstractAction {

        private final DocumentWindow documentWindowController;
        private final ControlLibrary userLibrary;
        private final FileSystem fileSystem;
        private final Dialog dialog;

        public RevealCustomFolderAction(@Autowired Api api, @Autowired ControlLibrary userLibrary,
                @Autowired @Lazy DocumentWindow documentWindowController) {
            super(api);
            this.documentWindowController = documentWindowController;
            this.userLibrary = userLibrary;
            this.fileSystem = api.getFileSystem();
            this.dialog = api.getApiDoc().getDialog();
        }

        @Override
        public boolean canPerform() {
            return true;
        }

        @Override
        public ActionStatus perform() {
            try {
                fileSystem.revealInFileBrowser(userLibrary.getPath());
            } catch (IOException x) {
                dialog.showErrorAndWait("",
                        I18N.getString("alert.reveal.failure.message", documentWindowController.getStage().getTitle()),
                        I18N.getString("alert.reveal.failure.details"), x);
                return ActionStatus.FAILED;
            }
            return ActionStatus.DONE;
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.import.selection", descriptionKey = "action.description.import.selection")
    public static class ImportSelectionAction extends AbstractAction {

        private final Editor editorController;
        private final LibraryPanelController libraryPanelController;

        public ImportSelectionAction(@Autowired Api api, @Autowired @Lazy Editor editorController,
                @Autowired @Lazy LibraryPanelController libraryPanelController) {
            super(api);
            this.editorController = editorController;
            this.libraryPanelController = libraryPanelController;
        }

        @Override
        public boolean canPerform() {
            // This method cannot be called if there is not a valid selection, a selection
            // eligible for being dropped onto Library panel.
            return editorController.getSelection().getGroup() instanceof ObjectSelectionGroup;
        }

        @Override
        public ActionStatus perform() {
            AbstractSelectionGroup asg = editorController.getSelection().getGroup();
            ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            assert !osg.getItems().isEmpty();
            List<FXOMObject> selection = new ArrayList<>(osg.getItems());
            libraryPanelController.performImportSelection(selection);
            return ActionStatus.DONE;
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.view.as.sections", descriptionKey = "action.description.view.as.sections")
    public static class ViewAsSectionsAction extends AbstractAction {

        private final LibraryPanelController libraryPanelController;
        private final DisplayModePreference displayModePreference;

        public ViewAsSectionsAction(@Autowired Api api, @Autowired @Lazy LibraryPanelController libraryPanelController,
                @Autowired @Lazy DisplayModePreference displayModePreference) {
            super(api);
            this.libraryPanelController = libraryPanelController;
            this.displayModePreference = displayModePreference;
        }

        @Override
        public boolean canPerform() {
            return libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SECTIONS;
        }

        @Override
        public ActionStatus perform() {
            if (libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SEARCH) {
                libraryPanelController.setDisplayMode(LibraryPanelController.DISPLAY_MODE.SECTIONS);
            } else {
                libraryPanelController.setPreviousDisplayMode(LibraryPanelController.DISPLAY_MODE.SECTIONS);
            }

            displayModePreference.setValue(libraryPanelController.getDisplayMode()).writeToJavaPreferences();
            return ActionStatus.DONE;
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.view.as.list", descriptionKey = "action.description.view.as.list")
    public static class ViewAsListAction extends AbstractAction {

        private final LibraryPanelController libraryPanelController;
        private final DisplayModePreference displayModePreference;

        public ViewAsListAction(@Autowired Api api, @Autowired @Lazy LibraryPanelController libraryPanelController,
                @Autowired @Lazy DisplayModePreference displayModePreference) {
            super(api);
            this.libraryPanelController = libraryPanelController;
            this.displayModePreference = displayModePreference;
        }

        @Override
        public boolean canPerform() {
            return libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.LIST;
        }

        @Override
        public ActionStatus perform() {
            if (libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SEARCH) {
                libraryPanelController.setDisplayMode(LibraryPanelController.DISPLAY_MODE.LIST);
            } else {
                libraryPanelController.setPreviousDisplayMode(LibraryPanelController.DISPLAY_MODE.LIST);
            }

            displayModePreference.setValue(libraryPanelController.getDisplayMode()).writeToJavaPreferences();
            return ActionStatus.DONE;
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    @ActionMeta(nameKey = "action.name.manage.jar.fxml", descriptionKey = "action.description.manage.jar.fxml")
    public static class ManageJarFxmlAction extends AbstractAction {

        private final Main mainController;
        private final Document documentWindowController;
        private final LibraryDialogFactory libraryDialogFactory;
        private final LibraryPanelController libraryPanelController;
        private final ControlLibrary controlLibrary;

        public ManageJarFxmlAction(@Autowired Api api, @Autowired @Lazy ControlLibrary controlLibrary,
                @Autowired @Lazy Document documentWindowController,
                @Autowired @Lazy LibraryDialogFactory libraryDialogFactory,
                @Autowired @Lazy LibraryPanelController libraryPanelController) {
            super(api);
            this.mainController = api.getMain();
            this.documentWindowController = documentWindowController;
            this.libraryDialogFactory = libraryDialogFactory;
            this.libraryPanelController = libraryPanelController;
            this.controlLibrary = controlLibrary;
        }

        @Override
        public boolean canPerform() {
            return true;
        }

        @Override
        public ActionStatus perform() {
//			libraryDialogController = new LibraryDialogController(editorController, libraryPanelController,
//            		mavenSetting, mavenPreferences, repositoryPreferences, getStage());

            /*
             * libraryDialogController.setOnAddJar(() ->
             * onImportJarFxml(libraryDialogController.getStage()));
             * libraryDialogController.setOnEditFXML(fxmlPath -> { if
             * (mainController.lookupUnusedDocumentWindowController() != null) {
             * libraryDialogController.closeWindow(); }
             * mainController.performOpenRecent(documentWindowController,
             * fxmlPath.toFile()); }); libraryDialogController.setOnAddFolder(() ->
             * onImportFromFolder(libraryDialogController.getStage()));
             */
            controlLibrary.openDialog();
            return ActionStatus.DONE;
        }

    }
}
