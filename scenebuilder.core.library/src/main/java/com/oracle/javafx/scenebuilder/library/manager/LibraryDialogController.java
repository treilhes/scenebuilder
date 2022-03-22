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

package com.oracle.javafx.scenebuilder.library.manager;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.library.api.AbstractLibrary;
import com.oracle.javafx.scenebuilder.library.api.LibraryStoreConfiguration;
import com.oracle.javafx.scenebuilder.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.library.maven.MavenDialogController;
import com.oracle.javafx.scenebuilder.library.maven.repository.RepositoryManagerController;
import com.oracle.javafx.scenebuilder.library.maven.search.SearchMavenDialogController;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the JAR/FXML Library dialog.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class LibraryDialogController extends AbstractFxmlWindowController{

    @FXML
    private ListView<DialogListItem> libraryListView;

    @FXML
    private Label listLabel;

    @FXML
    private Hyperlink searchRepositoryLink;

    @FXML
    private Hyperlink selectArtifactLink;

    @FXML
    private Hyperlink selectFileLink;

    @FXML
    private Hyperlink manageRepositoriesLink;

    @FXML
    private Hyperlink classesLink;

    private final Editor editorController;
    //private LibraryStoreConfiguration libraryConfiguration;
    //private LibraryStore libraryStore;
    private final Stage owner;

    //private ObservableList<DialogListItem> listItems;

//    private Runnable onAddJar;
//    private Runnable onAddFolder;
//    private Consumer<Path> onEditFXML;

    private final MavenArtifactsPreferences mavenPreferences;
    private final MavenRepositoriesPreferences repositoryPreferences;
    private final MavenSetting mavenSetting;
    // TODO may be replaced by editorcontroller after moving
    // libraryPanelController.copyFilesToUserLibraryDir(files)
    private final FileSystem fileSystem;
    private final SceneBuilderBeanFactory context;

    private final ListChangeListener<? super MavenArtifact> artifactListener = c -> loadLibraryList();
    private final ListChangeListener<? super Path> fileOrFolderListener = c -> loadLibraryList();

    private AbstractLibrary<?, ?> library;

    private final SceneBuilderManager sceneBuilderManager;

    private final IconSetting iconSetting;


    public LibraryDialogController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            SceneBuilderBeanFactory context,
            Editor editorController,
            MavenSetting mavenSetting,
            MavenArtifactsPreferences mavenPreferences,
            MavenRepositoriesPreferences repositoryPreferences,
            DocumentWindow document,
            FileSystem fileSystem) {
        super(sceneBuilderManager, iconSetting, LibraryDialogController.class.getResource("LibraryDialog.fxml"), I18N.getBundle(),
                document); // NOI18N
        this.owner = document.getStage();
        this.context = context;
        this.sceneBuilderManager = sceneBuilderManager;
        this.iconSetting = iconSetting;
        this.editorController = editorController;
        this.mavenPreferences = mavenPreferences;
        this.repositoryPreferences = repositoryPreferences;
        this.mavenSetting = mavenSetting;
        this.fileSystem = fileSystem;
    }

    public void initForLibrary(AbstractLibrary<?, ?> library) {
        this.library = library;

        final LibraryStoreConfiguration libraryConfiguration = library.getDialogConfiguration();

        String mainLabel = libraryConfiguration.getListLabel();
        String fileLabel = libraryConfiguration.getSelectFileLabel();
        String folderLabel = libraryConfiguration.getSelectFolderLabel();
        String artifactLabel = libraryConfiguration.getSelectArtifactLabel();

        if (mainLabel != null && !mainLabel.isEmpty()) {
            this.listLabel.setText(mainLabel);
        }
        if (fileLabel != null && !fileLabel.isEmpty()) {
            this.selectFileLink.setText(fileLabel);
        }
        if (folderLabel != null && !folderLabel.isEmpty()) {
            this.classesLink.setText(folderLabel);
        }
        if (artifactLabel != null && !artifactLabel.isEmpty()) {
            this.selectArtifactLink.setText(artifactLabel);
        }

        boolean handleFile = library.newFileExplorer() != null;
        boolean handleFolder = library.newFolderExplorer() != null;
        boolean handleArtifact = library.newArtifactExplorer() != null;

        this.selectFileLink.setManaged(handleFile);
        this.classesLink.setManaged(handleFolder);
        this.selectArtifactLink.setManaged(handleArtifact);
        this.searchRepositoryLink.setManaged(handleArtifact);
        this.manageRepositoriesLink.setManaged(handleArtifact);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        this.classesLink.setTooltip(new Tooltip(I18N.getString("library.dialog.hyperlink.tooltip")));
    }

    @Override
    protected void controllerDidCreateStage() {
        if (this.owner == null) {
            // Dialog will be appliation modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Dialog will be window modal
            getStage().initOwner(this.owner);
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }

    @Override
    public void onCloseRequest() {
        close();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void openWindow() {
        assert library != null;
        super.openWindow();
        super.getStage().setTitle(I18N.getString("library.dialog.title"));

        String title = library.getDialogConfiguration().getTitleLabel();
        if (title != null && !title.isEmpty()) {
            super.getStage().setTitle(title);
        }

        loadLibraryList();

        library.getStore().getArtifacts().addListener(artifactListener);
        library.getStore().getFilesOrFolders().addListener(fileOrFolderListener);
    }

    void loadLibraryList() {

        Stream<DialogListItem> artifactStream = library.getStore().getArtifacts().stream()
            .map(ma -> new ArtifactDialogListItem(this, ma));

        Stream<DialogListItem> filesStream = library.getStore().getFilesOrFolders().stream()
            .map(f -> new LibraryDialogListItem(this, f));

        SbPlatform.runLater(() -> {
            libraryListView.getItems().setAll(Stream.concat(artifactStream, filesStream)
                    .sorted(new DialogListItemComparator())
                    .collect(Collectors.toList()));
            libraryListView.setCellFactory(param -> new LibraryDialogListCell());
        });
    }

    @FXML
    private void close() {
        if (library.getStore() != null) {
            library.getStore().getArtifacts().removeListener(artifactListener);
            library.getStore().getFilesOrFolders().removeListener(fileOrFolderListener);
        }
        library = null;
        libraryListView.getItems().clear();
        closeWindow();
    }

    @FXML
    private void manage() {
        RepositoryManagerController repositoryDialogController = context.getBean(RepositoryManagerController.class,
                sceneBuilderManager, iconSetting, editorController, mavenSetting, repositoryPreferences, this);
        repositoryDialogController.openWindow();
    }

    @FXML
    private void addJar() {
        List<File> files = performSelectFiles();
        if (files != null && files.size() > 0) {
            library.performAddFilesOrFolders(files.stream().map(f -> f.toPath()).collect(Collectors.toList()));
        }
    }

    @FXML
    private void addFolder() {
        File folder = performSelectFolder();
        if (folder != null && folder.exists()) {
            library.performAddFilesOrFolders(List.of(folder.toPath()));
        }
    }

    @FXML
    private void addRelease() {
        SearchMavenDialogController mavenDialogController = context.getBean(SearchMavenDialogController.class,
                sceneBuilderManager, iconSetting,
                editorController, library, mavenSetting, mavenPreferences, repositoryPreferences,
                this);
        mavenDialogController.openWindow();
        mavenDialogController.getStage().showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!mavenDialogController.getStage().isShowing()) {
                    loadLibraryList();
                    mavenDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
    }

    @FXML
    private void addManually() {
        MavenDialogController mavenDialogController = context.getBean(MavenDialogController.class,
                sceneBuilderManager, iconSetting, editorController,
                library, mavenSetting, mavenPreferences, repositoryPreferences, this);
        mavenDialogController.openWindow();
        mavenDialogController.getStage().showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!mavenDialogController.getStage().isShowing()) {
                    loadLibraryList();
                    mavenDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
    }

    /*
     * If the file is an fxml, we don't need to stop the library watcher. Else we
     * have to stop it first: 1) We stop the library watcher, so that all related
     * class loaders will be closed and the jar can be deleted. 2) Then, if the file
     * exists, the jar or fxml file will be deleted from the library. 3) After the
     * jar or fxml is removed, the library watcher is started again.
     */
    public void processJarFXMLFolderDelete(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem) {
            library.performRemoveFilesOrFolders(List.of(((LibraryDialogListItem)dialogListItem).getFilePath()));
        } else if (dialogListItem instanceof ArtifactDialogListItem) {
            library.performRemoveArtifact(((ArtifactDialogListItem)dialogListItem).getMavenArtifact());
        }
    }

    public void processJarFXMLFolderEdit(DialogListItem dialogListItem) {
        if (dialogListItem instanceof LibraryDialogListItem) {
            library.performEditFilesOrFolders(List.of(((LibraryDialogListItem)dialogListItem).getFilePath()));
        } else if (dialogListItem instanceof ArtifactDialogListItem) {
            library.performEditArtifact(((ArtifactDialogListItem)dialogListItem).getMavenArtifact());
        }

        //TODO need to manage fxml edition
////    if (SceneBuilderApp.getSingleton().lookupUnusedDocumentWindowController() != null) {
////    closeWindow();
////}
////SceneBuilderApp.getSingleton().performOpenRecent(documentWindowController,
////        item.getFilePath().toFile());
//libraryConfiguration.processEditFile(item.getFilePath());
////if (onEditFXML != null) {
////    onEditFXML.accept(item.getFilePath());
////}
    }

    // TODO find usage in previous version reimplement then delete
    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }

    private void updatePreferences(MavenArtifact mavenArtifact) {
        if (mavenArtifact == null) {
            return;
        }

        //library.stopWatching();
        //libraryConfiguration.releaseLocks();

        // Update record artifact
        mavenPreferences.getRecordArtifact(mavenArtifact).writeToJavaPreferences();

        //library.startWatching();
        //libraryConfiguration.enableLocks();

    }

    /**
     * Open a file chooser that allows to select one folder
     * @return the selected folder or null
     */
    private File performSelectFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        File folder = dirChooser.showDialog(this.getStage());
        if (folder != null) {
            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(folder);
        }

        return folder;
    }

    /**
     * Open a file chooser that allows to select one or more FXML and JAR file.
     * @return the list of selected files
     */
    private List<File> performSelectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(library.getDialogConfiguration().getFileExtensionFilter()); //NOCHECK
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(this.getStage());
        if(selectedFiles != null && !selectedFiles.isEmpty()){
            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(selectedFiles.get(0));
        }
        return selectedFiles;
    }
}