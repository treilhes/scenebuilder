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
package com.oracle.javafx.scenebuilder.app;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Document.ActionStatus;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.Template;
import com.oracle.javafx.scenebuilder.api.TemplateType;
import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.library.Library;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;
import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;
import com.oracle.javafx.scenebuilder.app.welcomedialog.WelcomeDialogWindowController;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.kit.template.TemplatesWindowController;
import com.oracle.javafx.scenebuilder.prefedit.controller.PreferencesWindowController;

import javafx.application.Application.Parameters;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@DependsOn("i18n")
public class MainController implements AppPlatform.AppNotificationHandler, ApplicationListener<JavafxApplication.StageReadyEvent>, UILogger, Main {

    private static MainController singleton;

    @Autowired
    ApplicationContext context;

    @Autowired
    HostServices hostServices;

    @Autowired
    Parameters parameters;

    @Autowired
    SceneBuilderBeanFactory sceneBuilderFactory;

    @Autowired
    private Library userLibrary;

    @Autowired
    private IconSetting windowIconSetting;

    @Autowired
    private RecentItemsPreference recentItemsPreference;

    private final ObservableList<Document> windowList = FXCollections.observableArrayList();

    //private UserLibrary userLibrary;

//    private ToolTheme toolTheme = ToolTheme.DEFAULT;
//
//	private final ToolThemePreference toolThemePreference;


	private final FileSystem fileSystem;

    private final Dialog dialog;
    private final List<InitWithSceneBuilder> initializations;
    private final List<DisposeWithSceneBuilder> finalizations;
    /*
     * Public
     * //TODO delete in favor of injection
     */
    public static MainController getSingleton() {
        return singleton;
    }

    public MainController(
    		@Autowired FileSystem fileSystem,
    		@Autowired @Lazy Dialog dialog,
    		@Lazy @Autowired(required = false) List<InitWithSceneBuilder> initializations,
            @Lazy @Autowired(required = false) List<DisposeWithSceneBuilder> finalizations
    		) {

    	this.fileSystem = fileSystem;
    	this.dialog = dialog;
    	this.initializations = initializations;
    	this.finalizations = finalizations;

    	fileSystem.startWatcher();

        if (singleton != null) {
        	return;
        }
        singleton = this;

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
    public void performControlAction(ApplicationControlAction a, Document source) {
        switch (a) {
            case ABOUT:
                AboutWindowController aboutWindowController = context.getBean(AboutWindowController.class);
                //aboutWindowController.setToolStylesheet(getToolStylesheet());
                aboutWindowController.openWindow();
                windowIconSetting.setWindowIcon(aboutWindowController.getStage());
                break;

//            case REGISTER:
//                final RegistrationWindowController registrationWindowController = context.getBean(RegistrationWindowController.class);
//                registrationWindowController.openWindow();
//                break;

//            case CHECK_UPDATES:
//                checkUpdates(source);
//                break;

            case NEW_FILE:
                final DocumentWindowController newWindow = makeNewWindow();
                newWindow.updateWithDefaultContent();
                newWindow.openWindow();
                break;

            case NEW_TEMPLATE:
                performNewFromTemplate();
                break;

            case OPEN_FILE:
                performOpenFile(source);
                break;

//            case CLOSE_FRONT_WINDOW:
//                performCloseFrontWindow();
//                break;

//            case USE_DEFAULT_THEME:
//                performUseToolTheme(ToolTheme.DEFAULT);
//                break;
//
//            case USE_DARK_THEME:
//                performUseToolTheme(ToolTheme.DARK);
//                break;

            case SHOW_PREFERENCES:
                PreferencesWindowController preferencesWindowController = context.getBean(PreferencesWindowController.class);
                //preferencesWindowController.setToolStylesheet(getToolStylesheet());
                preferencesWindowController.openWindow();
                break;

            case EXIT:
                performExit();
                break;
        }
    }

    @Override
    public void performNewFromTemplate() {
        final TemplatesWindowController templatesWindowController = context.getBean(TemplatesWindowController.class);
        templatesWindowController.setOnTemplateChosen(this::performNewTemplateInNewWindow);
        templatesWindowController.openWindow();
    }


    public boolean canPerformControlAction(ApplicationControlAction a, DocumentWindowController source) {
        final boolean result;
        switch (a) {
            case ABOUT:
            case REGISTER:
            case CHECK_UPDATES:
            case NEW_FILE:
            case NEW_TEMPLATE:
            case OPEN_FILE:
            case SHOW_PREFERENCES:
            case EXIT:
                result = true;
                break;

//            case CLOSE_FRONT_WINDOW:
//                result = windowList.isEmpty() == false;
//                break;

//            case USE_DEFAULT_THEME:
//                result = toolTheme != ToolTheme.DEFAULT;
//                break;
//
//            case USE_DARK_THEME:
//                result = toolTheme != ToolTheme.DARK;
//                break;

            default:
                result = false;
                assert false;
                break;
        }
        return result;
    }

    @Override
    public void performOpenRecent(Document source, final File fxmlFile) {
        assert fxmlFile != null && fxmlFile.exists();

        final List<File> fxmlFiles = new ArrayList<>();
        fxmlFiles.add(fxmlFile);
        performOpenFiles(fxmlFiles, source);
    }

    public void documentWindowRequestClose(Document fromWindow) {
        closeWindow(fromWindow);
    }

    //TODO comment this
    public Library getUserLibrary() {
        return userLibrary;
    }

    @Override
    public List<Document> getDocumentWindowControllers() {
        return Collections.unmodifiableList(windowList);
    }

    public Document lookupDocumentWindowControllers(URL fxmlLocation) {
        assert fxmlLocation != null;

        Document result = null;
        try {
            final URI fxmlURI = fxmlLocation.toURI();
            for (Document dwc : windowList) {
                final URL docLocation = dwc.getEditorController().getFxmlLocation();
                if ((docLocation != null) && fxmlURI.equals(docLocation.toURI())) {
                    result = dwc;
                    break;
                }
            }
        } catch (URISyntaxException x) {
            // Should not happen
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
        }

        return result;
    }

    @Override
    public Document lookupUnusedDocumentWindowController() {
        Document result = null;

        for (Document dwc : windowList) {
            if (dwc.isUnused()) {
                result = dwc;
                break;
            }
        }

        return result;
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

    @Override
    public void onApplicationEvent(JavafxApplication.StageReadyEvent stageReadyEvent) {
    	start(stageReadyEvent.getStage());
    }

    /*
     * Application
     */
    public void start(Stage stage) {
        try {
            if (AppPlatform.requestStart(this, parameters, fileSystem) == false) {
                // Start has been denied because another instance is running.
                Platform.exit();
            }
            // else {
            //      No other Scene Builder instance is already running.
            //      AppPlatform.requestStart() has/will invoke(d) handleLaunch().
            //      start() has now finished its job and should imply return.
            // }

        } catch (IOException x) {
            dialog.showErrorAndWait(
                    I18N.getString("alert.title.start"), 
                    I18N.getString("alert.start.failure.message"), 
                    I18N.getString("alert.start.failure.details"), 
                    x);
            Platform.exit();
        }
    }

    /*
     * AppPlatform.AppNotificationHandler
     */
    @Override
    //TODO there are some Gluon adherence here
    public void handleLaunch(List<String> files) {
        
        initializations.forEach(a -> a.init());
        
        boolean showWelcomeDialog = files.isEmpty();

        
//        userLibrary.explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> userLibraryExplorationCountDidChange());
//
//        userLibrary.startWatching();

        if (showWelcomeDialog) {
            // Creates an empty document
            final DocumentWindowController newWindow = makeNewWindow();
            newWindow.updateWithDefaultContent();
            newWindow.openWindow();

            WelcomeDialogWindowController wdwc = context.getBean(WelcomeDialogWindowController.class);

            // Unless we're on a Mac we're starting SB directly (fresh start)
            // so we're not opening any file and as such we should show the Welcome Dialog
            wdwc.getStage().show();

        } else {
            // Open files passed as arguments by the platform
            handleOpenFilesAction(files);
        }

      //TODO allow a more easy usage of scenic
        // Show ScenicView Tool when the JVM is started with option -Dscenic.
        // NetBeans: set it on [VM Options] line in [Run] category of project's Properties.
        if (System.getProperty("scenic") != null) { //NOI18N
            Platform.runLater(new ScenicViewStarter(context.getBean(Document.class).getScene()));
        }
    }

    

    @Override
    public void handleOpenFilesAction(List<String> files) {
        assert files != null;
        assert files.isEmpty() == false;

        final List<File> fileObjs = new ArrayList<>();
        for (String file : files) {
            fileObjs.add(new File(file));
        }

        fileSystem.updateNextInitialDirectory(fileObjs.get(0));

        // Fix for #45
        if (userLibrary.firstExplorationCompletedProperty().get()) {
            performOpenFiles(fileObjs, null);
        } else {
            // open files only after the first exploration has finished
            userLibrary.firstExplorationCompletedProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (userLibrary.firstExplorationCompletedProperty().get()) {
                        performOpenFiles(fileObjs, null);
                        userLibrary.firstExplorationCompletedProperty().removeListener(this);
                    }
                }
            });
        }
    }

    @Override
    public void handleMessageBoxFailure(Exception x) {
        dialog.showErrorAndWait(
                I18N.getString("alert.title.messagebox"), 
                I18N.getString("alert.messagebox.failure.message"), 
                I18N.getString("alert.messagebox.failure.details"), 
                x);
    }

    @Override
    public void handleQuitAction() {

        /*
         * Note : this callback is called on Mac OS X only when the user
         * selects the 'Quit App' command in the Application menu.
         *
         * Before calling this callback, FX automatically sends a close event
         * to each open window ie DocumentWindowController.performCloseAction()
         * is invoked for each open window.
         *
         * When we arrive here, windowList is empty if the user has confirmed
         * the close operation for each window : thus exit operation can
         * be performed. If windowList is not empty,  this means the user has
         * cancelled at least one close operation : in that case, exit operation
         * should be not be executed.
         */
        if (windowList.isEmpty()) {
            logTimestamp(ACTION.STOP);
            Platform.exit();
        }
    }

    /*
     * Private
     */
    public DocumentWindowController makeNewWindow() {
    	DocumentScope.setCurrentScope(null);

        final DocumentWindowController result = sceneBuilderFactory.get(DocumentWindowController.class);

        windowIconSetting.setWindowIcon(result.getStage());

        windowList.add(result);
        return result;
    }

    private void closeWindow(Document w) {
        assert windowList.contains(w);
        windowList.remove(w);
        w.closeWindow();
    }

    private static String displayName(String pathString) {
        return Paths.get(pathString).getFileName().toString();
    }

    /*
     * Private (control actions)
     */
    private void performOpenFile(Document fromWindow) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml")); //NOI18N
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
        final List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(null);
        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));
            performOpenFiles(fxmlFiles, fromWindow);
        }
    }

    @Override
    public void performNewTemplate(Template template) {
        Document documentWC = getDocumentWindowControllers().get(0);
        loadTemplateInWindow(template, documentWC);
    }

    public void performNewTemplateInNewWindow(Template template) {
        final DocumentWindowController newTemplateWindow = makeNewWindow();
        loadTemplateInWindow(template, newTemplateWindow);
    }

    private void loadTemplateInWindow(Template template, Document documentWindowController) {
        final URL url = template.getFXMLURL();
        if (url != null) {
        	// TODO How to pass this boolean into the new Pref API ?
        	// template.getType() != Type.PHONE ? reload theme : do not reload
            documentWindowController.loadFromURL(url, template.getType() != TemplateType.PHONE);
        }
        Template.prepareDocument(documentWindowController.getEditorController(), template);
        documentWindowController.openWindow();
    }

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

    private void performOpenFiles(List<File> fxmlFiles,
                                  Document fromWindow) {
        assert fxmlFiles != null;
        assert fxmlFiles.isEmpty() == false;

        final Map<File, IOException> exceptions = new HashMap<>();
        for (File fxmlFile : fxmlFiles) {
            try {
                final Document dwc
                        = lookupDocumentWindowControllers(fxmlFile.toURI().toURL());
                if (dwc != null) {
                    // fxmlFile is already opened
                    dwc.getStage().toFront();
                } else {
                    // Open fxmlFile
                    final Document hostWindow;
                    final Document unusedWindow
                            = lookupUnusedDocumentWindowController();
                    if (unusedWindow != null) {
                        hostWindow = unusedWindow;
                    } else {
                        hostWindow = makeNewWindow();
                    }
                    hostWindow.loadFromFile(fxmlFile);
                    hostWindow.openWindow();
                }
            } catch (IOException xx) {
                exceptions.put(fxmlFile, xx);
            }
        }

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

    private void performExit() {

        // Check if an editing session is on going
        for (Document dwc : windowList) {
            if (dwc.getEditorController().isTextEditingSessionOnGoing()) {
                // Check if we can commit the editing session
                if (dwc.getEditorController().canGetFxmlText() == false) {
                    // Commit failed
                    return;
                }
            }
        }

        // Collects the documents with pending changes
        final List<Document> pendingDocs = new ArrayList<>();
        for (Document dwc : windowList) {
            if (dwc.isDocumentDirty()) {
                pendingDocs.add(dwc);
            }
        }

        // Notifies the user if some documents are dirty
        final boolean exitConfirmed;
        switch (pendingDocs.size()) {
            case 0: {
                exitConfirmed = true;
                break;
            }

            case 1: {
                final Document dwc0 = pendingDocs.get(0);
                exitConfirmed = dwc0.performCloseAction() == ActionStatus.DONE;
                break;
            }

            default: {
                assert pendingDocs.size() >= 2;

                final Alert d = dialog.customAlert();
                d.setMessage(I18N.getString("alert.review.question.message", pendingDocs.size()));
                d.setDetails(I18N.getString("alert.review.question.details"));
                d.setOKButtonTitle(I18N.getString("label.review.changes"));
                d.setActionButtonTitle(I18N.getString("label.discard.changes"));
                d.setActionButtonVisible(true);

                switch (d.showAndWait()) {
                    default:
                    case OK: { // Review
                        int i = 0;
                        ActionStatus status;
                        do {
                            status = pendingDocs.get(i++).performCloseAction();
                        } while ((status == ActionStatus.DONE) && (i < pendingDocs.size()));
                        exitConfirmed = (status == ActionStatus.DONE);
                        break;
                    }
                    case CANCEL: {
                        exitConfirmed = false;
                        break;
                    }
                    case ACTION: { // Do not review
                        exitConfirmed = true;
                        break;
                    }
                }
                break;
            }
        }

        // Exit if confirmed
        if (exitConfirmed) {
            for (Document dwc : new ArrayList<>(windowList)) {
                // Write to java preferences before closing
                dwc.updatePreferences();
                documentWindowRequestClose(dwc);
            }
            fileSystem.stopWatcher();
            logTimestamp(ACTION.STOP);
            
            finalizations.forEach(a -> a.dispose());
            // TODO (elp): something else here ?
            Platform.exit();
        }
    }

    private enum ACTION {START, STOP}

    ;

    private void logTimestamp(ACTION type) {
        switch (type) {
            case START:
                Logger.getLogger(this.getClass().getName()).info(I18N.getString("log.start"));
                break;
            case STOP:
                Logger.getLogger(this.getClass().getName()).info(I18N.getString("log.stop"));
                break;
            default:
                assert false;
        }
    }

//    private String getToolStylesheet() {
//        return toolThemePreference.getValue().getStylesheetURL();
//    }






    

    @Override
	public void logInfoMessage(String key) {
        for (Document dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle());
        }
    }

    @Override
	public void logInfoMessage(String key, Object... args) {
        for (Document dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
        }
    }

    public static void applyToAllDocumentWindows(Consumer<Document> consumer) {
    	//TODO check if this is realy working, cause i've some doubts
        for (Document dwc : getSingleton().getDocumentWindowControllers()) {
            consumer.accept(dwc);
        }
    }

	public HostServices getHostServices() {
		return hostServices;
	}

}
