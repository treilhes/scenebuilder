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
package com.gluonhq.jfxapps.core.appmngr.impl;

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
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationInstanceScope;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.application.lifecycle.DisposeWithApplication;
import com.gluonhq.jfxapps.core.api.application.lifecycle.InitWithApplication;
import com.gluonhq.jfxapps.core.api.di.SbPlatform;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.dialog.Dialog;

import jakarta.inject.Provider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@ApplicationSingleton
@DependsOn("i18n") // NOCHECK
@Lazy
public class InstancesControllerImpl implements InstancesManager {

    private final static Logger logger = LoggerFactory.getLogger(InstancesControllerImpl.class);

    //private static MainController singleton;

    private JfxAppContext context;

    //private IconSetting windowIconSetting;

    //private RecentItemsPreference recentItemsPreference;

    private final ObservableList<ApplicationInstance> windowList = FXCollections.observableArrayList();

    // private UserLibrary userLibrary;

//    private ToolTheme toolTheme = ToolTheme.DEFAULT;
//
//	private final ToolThemePreference toolThemePreference;

    //private final FileSystem fileSystem;

    private final Provider<Dialog> dialog;
    private final Provider<Optional<List<InitWithApplication>>> initializations;
    private final Provider<Optional<List<DisposeWithApplication>>> finalizations;

    public InstancesControllerImpl(
            JfxAppContext context,
            //IconSetting windowIconSetting,
            //FileSystem fileSystem,
            Provider<Dialog> dialog,
            Provider<Optional<List<InitWithApplication>>> initializations,
            Provider<Optional<List<DisposeWithApplication>>> finalizations) {

        this.context = context;
        //this.windowIconSetting = windowIconSetting;
        //this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.initializations = initializations;
        this.finalizations = finalizations;



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
    public void notifyInstanceClosed(ApplicationInstance document) {
        assert windowList.contains(document);
        windowList.remove(document);
    }

    // TODO comment this
//    public Library getUserLibrary() {
//        return userLibrary;
//    }

    @Override
    public List<ApplicationInstance> getInstances() {
        return Collections.unmodifiableList(windowList);
    }

    @Override
    public int countInstances() {
        return windowList.size();
    }

    @Override
    public ApplicationInstance lookupInstance(URL fxmlLocation) {
        assert fxmlLocation != null;

        ApplicationInstance result = null;
        try {
            final URI fxmlURI = fxmlLocation.toURI();
            for (ApplicationInstance dwc : windowList) {
                final URL docLocation = dwc.getLocation();
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
    public ApplicationInstance lookupUnusedInstance() {
        return lookupUnusedInstance(Collections.emptyList());
    }

    @Override
    public ApplicationInstance lookupUnusedInstance(Collection<ApplicationInstance> ignored) {
        ApplicationInstance result = null;

        for (ApplicationInstance dwc : windowList) {
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
            final ApplicationInstance instance = newInstance();
            instance.openWindow();
            instance.loadBlank();
            return;
        }

        final Map<File, ApplicationInstance> documents = new HashMap<>();

        final Map<File, IOException> exceptions = new HashMap<>();

        //build dependency injections first
        for (File fxmlFile : fxmlFiles) {
                try {
                    final ApplicationInstance dwc = lookupInstance(fxmlFile.toURI().toURL());
                    if (dwc != null) {
                        // fxmlFile is already opened
                        dwc.getDocumentWindow().getStage().toFront();
                    } else {
                        // Open fxmlFile
                        final ApplicationInstance hostWindow;
                        final ApplicationInstance unusedWindow = lookupUnusedInstance(documents.values());
                        if (unusedWindow != null) {
                            logger.info("Assign {} to unused document", fxmlFile.getName());
                            hostWindow = unusedWindow;
                        } else {
                            logger.info("Assign {} to new document", fxmlFile.getName());
                            hostWindow = newInstance();
                        }
                        documents.put(fxmlFile, hostWindow);
                    }
                } catch (IOException e) {
                    exceptions.put(fxmlFile, e);
                }
        }

        // execute ui related loading now
        SbPlatform.runOnFxThread(() -> {


            for (Entry<File, ApplicationInstance> entry:documents.entrySet()) {
                File file = entry.getKey();
                ApplicationInstance hostWindow = entry.getValue();
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
                        //recentItemsPreference.addRecentItems(fxmlFiles);
                        break;
                    }
                    case 1: {
                        final File fxmlFile = exceptions.keySet().iterator().next();
                        final Exception x = exceptions.get(fxmlFile);
                        dialog.get().showErrorAndWait(
                                I18N.getString("alert.title.open"),
                                I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())),
                                I18N.getString("alert.open.failure1.details"),
                                x);
                        break;
                    }
                    default: {
                        if (exceptions.size() == fxmlFiles.size()) {
                            // Open operation has failed for all the files
                            dialog.get().showErrorAndWait(
                                    I18N.getString("alert.title.open"),
                                    I18N.getString("alert.open.failureN.message"),
                                    I18N.getString("alert.open.failureN.details")
                                    );
                        } else {
                            // Open operation has failed for some files
                            dialog.get().showErrorAndWait(
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
    public ApplicationInstance newInstance() {
        JfxAppContext.applicationInstanceScope.unbindScope();

        final ApplicationInstance result = context.getBean(ApplicationInstance.class);
        final SceneBuilderManager sceneBuilderManager = context.getBean(SceneBuilderManager.class);
        final DocumentManager documentManager = context.getBean(DocumentManager.class);

        sceneBuilderManager.documentScoped().onNext(result);
        documentManager.dependenciesLoaded().set(true);

        //TODO checkme: can be deleted, already handled by documentWidowController
        //SbPlatform.runOnFxThreadWithActiveScope(() -> windowIconSetting.setWindowIcon(result.getDocumentWindow().getStage()));

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
    public ApplicationInstance getFrontInstance() {
//        for (DocumentWindowController dwc : windowList) {
//            if (dwc.isFrontDocumentWindow()) {
//                return dwc;
//            }
//        }
        try {
            return (ApplicationInstance) JfxAppContext.applicationInstanceScope.getCurrentScope().getScopedObject();
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

    public void applyToAllDocuments(Consumer<ApplicationInstance> consumer) {
        windowList.stream().forEach(consumer::accept);
    }

    @Override
    public void close() {

        finalizations.get().ifPresent(f -> f.forEach(a -> a.dispose()));
    }

}
