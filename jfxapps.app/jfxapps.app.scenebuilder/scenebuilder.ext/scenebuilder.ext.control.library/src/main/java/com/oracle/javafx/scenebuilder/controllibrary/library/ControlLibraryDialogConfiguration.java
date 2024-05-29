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
package com.oracle.javafx.scenebuilder.controllibrary.library;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.extstore.fs.ExtensionFileSystemFactory;
import com.oracle.javafx.scenebuilder.fs.controller.ClassLoaderController;
import com.oracle.javafx.scenebuilder.library.api.LibraryStore;
import com.oracle.javafx.scenebuilder.library.api.LibraryStoreConfiguration;
import com.oracle.javafx.scenebuilder.library.api.LibraryStoreFactory;

import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class ControlLibraryDialogConfiguration implements LibraryStoreConfiguration { //extends AbstractLibrary

    private final ClassLoaderController classLoaderController;
    private LibraryStore store;
    private SceneBuilderBeanFactory context;

    //private final Api api;

    // @formatter:off
    public ControlLibraryDialogConfiguration(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired LibraryStoreFactory libraryStoreFactory,
            @Autowired ClassLoaderController classLoaderController,
            @Autowired FileSystem fileSystem,
            @Autowired ExtensionFileSystemFactory extFactory,
            @Autowired Dialog dialog) {
     // @formatter:on
        this.context = context;
        this.classLoaderController = classLoaderController;

    }

    @Override
    public String getTitleLabel() {
        return "title";
    }

    @Override
    public String getListLabel() {
        return "list";
    }

    @Override
    public String getSelectFileLabel() {
        return "single file";
    }

    @Override
    public String getSelectFolderLabel() {
        return "folder";
    }

    @Override
    public String getSelectArtifactLabel() {
        return "artifact";
    }


    @Override
    public ExtensionFilter getFileExtensionFilter() {
        List<String> extensions = ControlLibrary.HANDLED_FILE_EXTENSIONS.stream()
                .map(e -> "*." + e).collect(Collectors.toList());
        return new ExtensionFilter(I18N.getString("lib.filechooser.filter.msg"), extensions);
    }


//    @Override
//    public LibraryResourceHandler<Path> getFolderHandler() {
//        return new LibraryResourceHandler<Path>() {
//
//            @Override
//            public boolean canEdit(Path resource) {
//                return true;
//            }
//
//            @Override
//            public boolean add(Path folder) {
//                if (folder != null && Files.exists(folder) && Files.isDirectory(folder)) {
//                    if (store.isReady()) {
//                        // From here we know we will initiate the import dialog.
//                        // This is why we put application window on the front.
//                        // From there the import dialog window, which is application modal,
//                        // should come on top of it.
//
////                        final Window window = store.getDialog().getStage().getScene().getWindow();
////                        if (window instanceof Stage) {
////                            final Stage stage = (Stage) window;
////                            stage.toFront();
////                        }
//
////                        final ImportWindowController iwc = context.getBean(ImportWindowController.class, api, this, Arrays.asList(folder), mavenPreferences, (Stage) window);
////                        //iwc.setToolStylesheet(getEditorController().getToolStylesheet());
////                        // See comment in OnDragDropped handle set in method startListeningToDrop.
////                        ButtonID userChoice = iwc.showAndWait();
////
////                        if (userChoice.equals(ButtonID.OK) && currentDisplayMode.equals(DISPLAY_MODE.SECTIONS)) {
////                            sectionNameToKeepOpened = DefaultSectionNames.TAG_USER_DEFINED;
////                        }
//                    }
//                }
//                return true;
//            }
//
//            @Override
//            public boolean edit(Path resource) {
////                final ImportWindowController iwc = context.getBean(ImportWindowController.class, getApi(), libraryPanelController,
////                        Arrays.asList(item.getFilePath().toFile()), mavenPreferences, getStage());
////                //iwc.setToolStylesheet(editorController.getToolStylesheet());
////                // See comment in OnDragDropped handle set in method startListeningToDrop.
////                AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
////                return userChoice == AbstractModalDialog.ButtonID.OK;
//                return true;
//            }
//
//            @Override
//            public boolean delete(Path resource) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//        };
//    }
//
//    @Override
//    public LibraryResourceHandler<Path> getFileHandler() {
//        return new LibraryResourceHandler<Path>() {
//
//            @Override
//            public boolean canEdit(Path resource) {
//                return true;
//            }
//
//            @Override
//            public boolean add(Path resource) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//            @Override
//            public boolean edit(Path resource) {
////                final ImportWindowController iwc = context.getBean(ImportWindowController.class, getApi(), libraryPanelController,
////                        Arrays.asList(item.getFilePath().toFile()), mavenPreferences, getStage());
////                //iwc.setToolStylesheet(editorController.getToolStylesheet());
////                // See comment in OnDragDropped handle set in method startListeningToDrop.
////                AbstractModalDialog.ButtonID userChoice = iwc.showAndWait();
////                return userChoice == AbstractModalDialog.ButtonID.OK;
//                return true;
//            }
//
//            @Override
//            public boolean delete(Path resource) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//        };
//    }
//
//    @Override
//    public LibraryResourceHandler<MavenArtifact> getArtifactHandler() {
//        return new LibraryResourceHandler<MavenArtifact>() {
//
//            @Override
//            public boolean canEdit(MavenArtifact resource) {
//                return true;
//            }
//
//            @Override
//            public boolean add(MavenArtifact mavenArtifact) {
////                final ImportWindowController iwc
////                        = context.getBean(ImportWindowController.class, api, libraryPanelController,files, mavenPreferences,
////                        (Stage)installButton.getScene().getWindow(), false,mavenPreferences.getArtifactsFilter());
////                //iwc.setToolStylesheet(editorController.getToolStylesheet());
////
////                ButtonID userChoice = iwc.showAndWait();
////                if (userChoice == ButtonID.OK) {
////                    mavenArtifact.setFilter(iwc.getNewExcludedItems());
////                }
////
////                return userChoice == ButtonID.OK;
//                return true;
//            }
//
//            @Override
//            public boolean edit(MavenArtifact resource) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//            @Override
//            public boolean delete(MavenArtifact resource) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//
//        };
//    }
}
