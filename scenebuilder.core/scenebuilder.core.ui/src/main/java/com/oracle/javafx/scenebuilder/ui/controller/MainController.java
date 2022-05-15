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
package com.oracle.javafx.scenebuilder.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

import javafx.application.Application.Parameters;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@DependsOn("i18n") // NOCHECK
public class MainController implements UILogger, Main {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    //private static MainController singleton;

    @Autowired
    SceneBuilderBeanFactory context;

    @Autowired
    HostServices hostServices;

    @Autowired
    Parameters parameters;

    @Autowired
    SceneBuilderBeanFactory sceneBuilderFactory;

//    @Autowired
//    private ControlLibrary userLibrary;

    @Autowired
    private IconSetting windowIconSetting;

    @Autowired
    private RecentItemsPreference recentItemsPreference;

    private final ObservableList<Document> windowList = FXCollections.observableArrayList();

    // private UserLibrary userLibrary;

//    private ToolTheme toolTheme = ToolTheme.DEFAULT;
//
//	private final ToolThemePreference toolThemePreference;

    private final FileSystem fileSystem;

    private final Dialog dialog;
    private final List<InitWithSceneBuilder> initializations;
    private final List<DisposeWithSceneBuilder> finalizations;

    public MainController(
            @Autowired FileSystem fileSystem,
            @Autowired @Lazy Dialog dialog,
            @Lazy @Autowired(required = false) List<InitWithSceneBuilder> initializations,
            @Lazy @Autowired(required = false) List<DisposeWithSceneBuilder> finalizations) {

        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.initializations = initializations;
        this.finalizations = finalizations;

        fileSystem.startWatcher();

//        if (singleton != null) {
//            return;
//        }
//        singleton = this;

        // SB-270
//        windowList.addListener((ListChangeListener.Change<? extends DocumentWindowController> c) -> {
//            while (c.next()) {
//                if (c.wasAdded()) {
//                    final String toolStylesheet = getToolStylesheet();
//                    for (DocumentWindowController dwc : c.getAddedSubList()) {
//                        dwc.setToolStylesheet(toolStylesheet);
//                    }
//                }
//            }
//        });

    }


    @Override
    public void notifyDocumentClosed(Document document) {
        assert windowList.contains(document);
        windowList.remove(document);
    }

    // TODO comment this
//    public Library getUserLibrary() {
//        return userLibrary;
//    }

    @Override
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(windowList);
    }

    @Override
    public int getOpenDocuments() {
        return windowList.size();
    }

    @Override
    public Document lookupDocument(URL fxmlLocation) {
        assert fxmlLocation != null;

        Document result = null;
        try {
            final URI fxmlURI = fxmlLocation.toURI();
            for (Document dwc : windowList) {
                final URL docLocation = dwc.getFxmlLocation();
                if ((docLocation != null) && fxmlURI.equals(docLocation.toURI())) {
                    result = dwc;
                    break;
                }
            }
        } catch (URISyntaxException x) {
            // Should not happen
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOCHECK
        }

        return result;
    }

    @Override
    public Document lookupUnusedDocument() {
        return lookupUnusedDocument(Collections.emptyList());
    }

    @Override
    public Document lookupUnusedDocument(Collection<Document> ignored) {
        Document result = null;

        for (Document dwc : windowList) {
            if (dwc.isUnused() && !ignored.contains(dwc)) {
                result = dwc;
                break;
            }
        }

        return result;
    }

    @Override
    public void open(List<File> fxmlFiles) {

        if (fxmlFiles == null || fxmlFiles.isEmpty()) {
            final Document newWindow = makeNewDocument();
            newWindow.openWindow();
            try {
                newWindow.getEditorController().setFxmlTextAndLocation("", null, true); //NOCHECK
            } catch (IOException e) {
                logger.error("Not bound to happen", e);
            }
            newWindow.updateLoadFileTime();
            newWindow.getDocumentWindow().updateStageTitle();
            return;
        }

        final Map<File, Document> documents = new HashMap<>();

        final Map<File, IOException> exceptions = new HashMap<>();

        //build dependency injections first
        for (File fxmlFile : fxmlFiles) {
                try {
                    final Document dwc = lookupDocument(fxmlFile.toURI().toURL());
                    if (dwc != null) {
                        // fxmlFile is already opened
                        dwc.getDocumentWindow().getStage().toFront();
                    } else {
                        // Open fxmlFile
                        final Document hostWindow;
                        final Document unusedWindow = lookupUnusedDocument(documents.values());
                        if (unusedWindow != null) {
                            logger.info("Assign {} to unused document", fxmlFile.getName());
                            hostWindow = unusedWindow;
                        } else {
                            logger.info("Assign {} to new document", fxmlFile.getName());
                            hostWindow = makeNewDocument();
                        }
                        documents.put(fxmlFile, hostWindow);
                    }
                } catch (IOException e) {
                    exceptions.put(fxmlFile, e);
                }
        }

        // execute ui related loading now
        SbPlatform.runLater(() -> {


            for (Entry<File, Document> entry:documents.entrySet()) {
                File file = entry.getKey();
                Document hostWindow = entry.getValue();
                hostWindow.onFocus();
                //SbPlatform.runForDocument(hostWindow, () -> {
                    try {
                        hostWindow.loadFromFile(file);
                        hostWindow.openWindow();
                    } catch (IOException xx) {
                        hostWindow.closeWindow();
                        exceptions.put(file, xx);
                    }
                //});

                switch (exceptions.size()) {
                    case 0: { // Good
                        // Update recent items with opened files
                        recentItemsPreference.addRecentItems(fxmlFiles);
                        break;
                    }
                    case 1: {
                        final File fxmlFile = exceptions.keySet().iterator().next();
                        final Exception x = exceptions.get(fxmlFile);
                        dialog.showErrorAndWait(
                                I18N.getString("alert.title.open"),
                                I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())),
                                I18N.getString("alert.open.failure1.details"),
                                x);
                        break;
                    }
                    default: {
                        if (exceptions.size() == fxmlFiles.size()) {
                            // Open operation has failed for all the files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"),
                                    I18N.getString("alert.open.failureN.message"),
                                    I18N.getString("alert.open.failureN.details")
                                    );
                        } else {
                            // Open operation has failed for some files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"),
                                    I18N.getString("alert.open.failureMofN.message", exceptions.size(), fxmlFiles.size()),
                                    I18N.getString("alert.open.failureMofN.details")
                                    );
                        }
                        break;
                    }
                }
            }
        });
    }

//    public void toggleDebugMenu() {
//        final boolean visible;
//
//        if (windowList.isEmpty()) {
//            visible = false;
//        } else {
//            final Document dwc = windowList.get(0);
//            visible = dwc.getMenuBarController().isDebugMenuVisible();
//        }
//
//        for (Document dwc : windowList) {
//            dwc.getMenuBarController().setDebugMenuVisible(!visible);
//        }
//
//        if (EditorPlatform.IS_MAC) {
//            MenuBarController.getSystemMenuBarController().setDebugMenuVisible(!visible);
//        }
//    }
//
//    /*
//     * Application
//     */
//    public void start(Stage stage) {
//        try {
//            if (AppPlatform.requestStart(this, parameters, fileSystem) == false) {
//                // Start has been denied because another instance is running.
//                Platform.exit();
//            }
//            // else {
//            //      No other Scene Builder instance is already running.
//            //      AppPlatform.requestStart() has/will invoke(d) handleLaunch().
//            //      start() has now finished its job and should imply return.
//            // }
//
//        } catch (IOException x) {
//            dialog.showErrorAndWait(
//                    I18N.getString("alert.title.start"),
//                    I18N.getString("alert.start.failure.message"),
//                    I18N.getString("alert.start.failure.details"),
//                    x);
//            Platform.exit();
//        }
//    }
//
//    /*
//     * AppPlatform.AppNotificationHandler
//     */
//    @Override
//    //TODO there are some Gluon adherence here
//    public void handleLaunch(List<String> files) {
//
//        // defer dependency injection framework loading outside javafx thread
//        Task task = new Task(() -> {
//        initializations.forEach(a -> a.init());
//
//        boolean showWelcomeDialog = files.isEmpty();
//
//
////        userLibrary.explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> userLibraryExplorationCountDidChange());
////
////        userLibrary.startWatching();
//
//
//        if (showWelcomeDialog) {
//            // Creates an empty document
//            final Document newWindow = makeNewWindow();
//
//
//            WelcomeDialogWindowController wdwc = context.getBean(WelcomeDialogWindowController.class);
//
//            // Unless we're on a Mac we're starting SB directly (fresh start)
//            // so we're not opening any file and as such we should show the Welcome Dialog
//
//            SbPlatform.runLater(() -> {
//                newWindow.updateWithDefaultContent();
//                newWindow.openWindow();
//                wdwc.getStage().show();
//                SceneBuilderLoadingProgress.get().end();
//            });
//
//
//
//        } else {
//            // Open files passed as arguments by the platform
//            handleOpenFilesAction(files);
//        }
//
//
//        });
//
//        Thread th = new Thread(task.getRunnable());
//        th.setDaemon(true);
//        th.start();
//
//    }
//
//
//
//    @Override
//    public void handleOpenFilesAction(List<String> files) {
//        assert files != null;
//        assert files.isEmpty() == false;
//
//        final List<File> fileObjs = new ArrayList<>();
//        for (String file : files) {
//            fileObjs.add(new File(file));
//        }
//
//        fileSystem.updateNextInitialDirectory(fileObjs.get(0));
//
//        //TODO if there is more than one library this code must handle  all libraries loading instead of only one
//        // Fix for #45
//        if (userLibrary.firstExplorationCompletedProperty().get()) {
//            performOpenFiles(fileObjs, null);
//        } else {
//            // open files only after the first exploration has finished
//            userLibrary.firstExplorationCompletedProperty().addListener(new InvalidationListener() {
//                @Override
//                public void invalidated(Observable observable) {
//                    if (userLibrary.firstExplorationCompletedProperty().get()) {
//                        performOpenFiles(fileObjs, null);
//                        userLibrary.firstExplorationCompletedProperty().removeListener(this);
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public void handleMessageBoxFailure(Exception x) {
//        dialog.showErrorAndWait(
//                I18N.getString("alert.title.messagebox"),
//                I18N.getString("alert.messagebox.failure.message"),
//                I18N.getString("alert.messagebox.failure.details"),
//                x);
//    }
//
//    @Override
//    public void handleQuitAction() {
//
//        /*
//         * Note : this callback is called on Mac OS X only when the user
//         * selects the 'Quit App' command in the Application menu.
//         *
//         * Before calling this callback, FX automatically sends a close event
//         * to each open window ie DocumentWindowController.performCloseAction()
//         * is invoked for each open window.
//         *
//         * When we arrive here, windowList is empty if the user has confirmed
//         * the close operation for each window : thus exit operation can
//         * be performed. If windowList is not empty,  this means the user has
//         * cancelled at least one close operation : in that case, exit operation
//         * should be not be executed.
//         */
//        if (windowList.isEmpty()) {
//            logTimestamp(ACTION.STOP);
//            Platform.exit();
//        }
//    }

    /*
     * Private
     */
    @Override
    public Document makeNewDocument() {
        DocumentScope.setCurrentScope(null);

        final Document result = sceneBuilderFactory.getBean(Document.class);
        final SceneBuilderManager sceneBuilderManager = sceneBuilderFactory.getBean(SceneBuilderManager.class);
        final DocumentManager documentManager = sceneBuilderFactory.getBean(DocumentManager.class);

        sceneBuilderManager.documentScoped().onNext(result);
        documentManager.dependenciesLoaded().set(true);

        SbPlatform.runForDocumentLater(() -> windowIconSetting.setWindowIcon(result.getDocumentWindow().getStage()));

        windowList.add(result);
        return result;
    }

    private static String displayName(String pathString) {
        return Paths.get(pathString).getFileName().toString();
    }

//    @Override
//    public void performNewTemplate(Template template) {
//        Document documentWC = getDocumentWindowControllers().get(0);
//        loadTemplateInWindow(template, documentWC);
//    }
//
//    public void performNewTemplateInNewWindow(Template template) {
//        final DocumentWindowController newTemplateWindow = makeNewWindow();
//        loadTemplateInWindow(template, newTemplateWindow);
//    }
//
//    private void loadTemplateInWindow(Template template, Document documentWindowController) {
//        if (template != null && template.getFxmlUrl() != null) {
//        	// TODO How to pass this boolean into the new Pref API ?
//        	// template.getType() != Type.PHONE ? reload theme : do not reload
//            documentWindowController.loadFromURL(template.getFxmlUrl());
//        }
//        //Template.prepareDocument(documentWindowController.getEditorController(), template);
//        documentWindowController.openWindow();
//    }

//    private void performCloseFrontWindow() {
//        for (DocumentWindowController dwc : windowList) {
//            if (dwc.isFrontDocumentWindow()) {
//                dwc.performCloseFrontDocumentWindow();
//                break;
//            }
//        }
//    }

    @Override
    public Document getFrontDocumentWindow() {
//        for (DocumentWindowController dwc : windowList) {
//            if (dwc.isFrontDocumentWindow()) {
//                return dwc;
//            }
//        }
        try {
            return DocumentScope.getCurrentScope();
        } catch (Exception e) {
            return null;
        }

    }

//    /*
//     * Private (control actions)
//     */
//    @Override
//    public void performOpenFile(Document fromWindow) {
//        final FileChooser fileChooser = new FileChooser();
//
//        fileChooser.getExtensionFilters()
//                .add(new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml")); // NOCHECK
//        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
//        final List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(null);
//        if (fxmlFiles != null) {
//            assert fxmlFiles.isEmpty() == false;
//            fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));
//            performOpenFiles(fxmlFiles, fromWindow);
//        }
//    }

//    private void performOpenFiles(List<File> fxmlFiles, Document fromWindow) {
//        assert fxmlFiles != null;
//        assert fxmlFiles.isEmpty() == false;
//
//        final Map<File, Document> documents = new HashMap<>();
//
//        final Map<File, IOException> exceptions = new HashMap<>();
//
//        // build dependency injections first
//        for (File fxmlFile : fxmlFiles) {
//            try {
//                final Document dwc = lookupDocumentWindowControllers(fxmlFile.toURI().toURL());
//                if (dwc != null) {
//                    // fxmlFile is already opened
//                    dwc.getDocumentWindow().getStage().toFront();
//                } else {
//                    // Open fxmlFile
//                    final Document hostWindow;
//                    final Document unusedWindow = lookupUnusedDocumentWindowController();
//                    if (unusedWindow != null) {
//                        hostWindow = unusedWindow;
//                    } else {
//                        hostWindow = makeNewWindow();
//                    }
//                    documents.put(fxmlFile, hostWindow);
//                }
//            } catch (IOException e) {
//                exceptions.put(fxmlFile, e);
//            }
//        }
//
//        SceneBuilderLoadingProgress.get().end();
//
//        // execute ui related loading now
//        SbPlatform.runLater(() -> {
//
//            for (Entry<File, Document> entry : documents.entrySet()) {
//                File file = entry.getKey();
//                Document hostWindow = entry.getValue();
//
//                try {
//                    hostWindow.loadFromFile(file);
//                    hostWindow.openWindow();
//                } catch (IOException xx) {
//                    hostWindow.closeWindow();
//                    exceptions.put(file, xx);
//                }
//
//                switch (exceptions.size()) {
//                case 0: { // Good
//                    // Update recent items with opened files
//                    recentItemsPreference.addRecentItems(fxmlFiles);
//                    break;
//                }
//                case 1: {
//                    final File fxmlFile = exceptions.keySet().iterator().next();
//                    final Exception x = exceptions.get(fxmlFile);
//                    dialog.showErrorAndWait(I18N.getString("alert.title.open"),
//                            I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())),
//                            I18N.getString("alert.open.failure1.details"), x);
//                    break;
//                }
//                default: {
//                    if (exceptions.size() == fxmlFiles.size()) {
//                        // Open operation has failed for all the files
//                        dialog.showErrorAndWait(I18N.getString("alert.title.open"),
//                                I18N.getString("alert.open.failureN.message"),
//                                I18N.getString("alert.open.failureN.details"));
//                    } else {
//                        // Open operation has failed for some files
//                        dialog.showErrorAndWait(I18N.getString("alert.title.open"),
//                                I18N.getString("alert.open.failureMofN.message", exceptions.size(), fxmlFiles.size()),
//                                I18N.getString("alert.open.failureMofN.details"));
//                    }
//                    break;
//                }
//                }
//            }
//        });
//    }


    private enum ACTION {
        START, STOP
    }

    ;

    private void logTimestamp(ACTION type) {
        switch (type) {
        case START:
            logger.info(I18N.getString("log.start"));
            break;
        case STOP:
            logger.info(I18N.getString("log.stop"));
            break;
        default:
            assert false;
        }
    }

    @Override
    public void logInfoMessage(String key) {
        applyToAllDocuments(d -> d.logInfoMessage(key));
    }

    @Override
    public void logInfoMessage(String key, Object... args) {
        applyToAllDocuments(d -> d.logInfoMessage(key, args));
    }

    public void applyToAllDocuments(Consumer<Document> consumer) {
        windowList.stream().forEach(consumer::accept);
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    @Override
    public void close() {
        fileSystem.stopWatcher();
        finalizations.forEach(a -> a.dispose());
    }

}
