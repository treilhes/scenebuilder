/*
 * Copyright (c) 2016, 2017, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
import java.time.LocalDate;
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
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController.ActionStatus;
import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;
import com.oracle.javafx.scenebuilder.app.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesWindowController;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.app.registration.RegistrationWindowController;
import com.oracle.javafx.scenebuilder.app.settings.VersionSetting;
import com.oracle.javafx.scenebuilder.app.settings.WindowIconSetting;
import com.oracle.javafx.scenebuilder.app.tracking.Tracking;
import com.oracle.javafx.scenebuilder.app.welcomedialog.WelcomeDialogWindowController;
import com.oracle.javafx.scenebuilder.gluon.preferences.GluonPreferences;
import com.oracle.javafx.scenebuilder.kit.ResourceUtils;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.kit.alert.ImportingGluonControlsAlert;
import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReport;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ToolThemePreference;
import com.oracle.javafx.scenebuilder.kit.template.Template;
import com.oracle.javafx.scenebuilder.kit.template.TemplatesWindowController;
import com.oracle.javafx.scenebuilder.kit.template.Type;

import javafx.application.Application.Parameters;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
@DependsOn("i18n")
public class MainController implements AppPlatform.AppNotificationHandler, ApplicationListener<JavafxApplication.StageReadyEvent>, UILogger {

    public enum ApplicationControlAction {

        ABOUT,
        CHECK_UPDATES,
        REGISTER,
        NEW_FILE,
        NEW_TEMPLATE,
        OPEN_FILE,
        CLOSE_FRONT_WINDOW,
        USE_DEFAULT_THEME,
        USE_DARK_THEME,
        SHOW_PREFERENCES,
        EXIT
    }

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
    DocumentManager documentsManager;
    
    @Autowired
    private UserLibrary userLibrary;
    
    @Autowired
    private VersionSetting versionSetting;
    
    @Autowired
    private WindowIconSetting windowIconSetting;
    
    @Autowired
    private Tracking tracking;
    
    @Autowired
    private GlobalPreferences preferences;
    
    @Autowired
    private RecentItemsPreference recentItemsPreference;
    
    private final ObservableList<DocumentWindowController> windowList = FXCollections.observableArrayList();
    
    //private UserLibrary userLibrary;
    
    private ToolTheme toolTheme = ToolTheme.DEFAULT;


    //@Autowired
    private GluonPreferences gluPref;

	private final ToolThemePreference toolThemePreference;
    
    /*
     * Public
     * //TODO delete in favor of injection
     */
    public static MainController getSingleton() {
        return singleton;
    }

    public MainController(
    		@Autowired ToolThemePreference toolThemePreference, 
    		@Autowired GluonPreferences gluPref
    		) {
    	this.toolThemePreference = toolThemePreference;
        if (singleton != null) {
        	return;
        }
        singleton = this;
        
        // SB-270
        windowList.addListener((ListChangeListener.Change<? extends DocumentWindowController> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    final String toolStylesheet = getToolStylesheet();
                    for (DocumentWindowController dwc : c.getAddedSubList()) {
                        dwc.setToolStylesheet(toolStylesheet);
                    }
                }
            }
        });
        
        //PrefTests.doTest(gluPref);
        
    }
  
    public void performControlAction(ApplicationControlAction a, DocumentWindowController source) {
        switch (a) {
            case ABOUT:
                AboutWindowController aboutWindowController = context.getBean(AboutWindowController.class);
                aboutWindowController.setToolStylesheet(getToolStylesheet());
                aboutWindowController.openWindow();
                windowIconSetting.setWindowIcon(aboutWindowController.getStage());
                break;

            case REGISTER:
                final RegistrationWindowController registrationWindowController = context.getBean(RegistrationWindowController.class);
                registrationWindowController.openWindow();
                break;

            case CHECK_UPDATES:
                checkUpdates(source);
                break;

            case NEW_FILE:
                final DocumentWindowController newWindow = makeNewWindow();
                newWindow.updateWithDefaultContent();
                newWindow.openWindow();
                break;

            case NEW_TEMPLATE:
                final TemplatesWindowController templatesWindowController = new TemplatesWindowController(source.getStage());
                templatesWindowController.setOnTemplateChosen(this::performNewTemplateInNewWindow);
                templatesWindowController.openWindow();
                break;

            case OPEN_FILE:
                performOpenFile(source);
                break;

            case CLOSE_FRONT_WINDOW:
                performCloseFrontWindow();
                break;

//            case USE_DEFAULT_THEME:
//                performUseToolTheme(ToolTheme.DEFAULT);
//                break;
//
//            case USE_DARK_THEME:
//                performUseToolTheme(ToolTheme.DARK);
//                break;

            case SHOW_PREFERENCES:
                PreferencesWindowController preferencesWindowController = context.getBean(PreferencesWindowController.class);
                preferencesWindowController.setToolStylesheet(getToolStylesheet());
                preferencesWindowController.openWindow();
                break;

            case EXIT:
                performExit();
                break;
        }
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

            case CLOSE_FRONT_WINDOW:
                result = windowList.isEmpty() == false;
                break;

            case USE_DEFAULT_THEME:
                result = toolTheme != ToolTheme.DEFAULT;
                break;

            case USE_DARK_THEME:
                result = toolTheme != ToolTheme.DARK;
                break;

            default:
                result = false;
                assert false;
                break;
        }
        return result;
    }

    public void performOpenRecent(DocumentWindowController source, final File fxmlFile) {
        assert fxmlFile != null && fxmlFile.exists();

        final List<File> fxmlFiles = new ArrayList<>();
        fxmlFiles.add(fxmlFile);
        performOpenFiles(fxmlFiles, source);
    }

    public void documentWindowRequestClose(DocumentWindowController fromWindow) {
        closeWindow(fromWindow);
    }

    //TODO comment this
    public UserLibrary getUserLibrary() {
        return userLibrary;
    }

    public List<DocumentWindowController> getDocumentWindowControllers() {
        return Collections.unmodifiableList(windowList);
    }

    public DocumentWindowController lookupDocumentWindowControllers(URL fxmlLocation) {
        assert fxmlLocation != null;

        DocumentWindowController result = null;
        try {
            final URI fxmlURI = fxmlLocation.toURI();
            for (DocumentWindowController dwc : windowList) {
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

    public DocumentWindowController lookupUnusedDocumentWindowController() {
        DocumentWindowController result = null;

        for (DocumentWindowController dwc : windowList) {
            if (dwc.isUnused()) {
                result = dwc;
                break;
            }
        }

        return result;
    }

    public void toggleDebugMenu() {
        final boolean visible;

        if (windowList.isEmpty()) {
            visible = false;
        } else {
            final DocumentWindowController dwc = windowList.get(0);
            visible = dwc.getMenuBarController().isDebugMenuVisible();
        }

        for (DocumentWindowController dwc : windowList) {
            dwc.getMenuBarController().setDebugMenuVisible(!visible);
        }

        if (EditorPlatform.IS_MAC) {
            MenuBarController.getSystemMenuBarController().setDebugMenuVisible(!visible);
        }
    }

    @Override
    public void onApplicationEvent(JavafxApplication.StageReadyEvent stageReadyEvent) {
    	start(stageReadyEvent.getStage());
    }
    
    /*
     * Application
     */
    public void start(Stage stage) {
        try {
            if (AppPlatform.requestStart(this, parameters) == false) {
                // Start has been denied because another instance is running.
                Platform.exit();
            }
            // else {
            //      No other Scene Builder instance is already running.
            //      AppPlatform.requestStart() has/will invoke(d) handleLaunch().
            //      start() has now finished its job and should imply return.
            // }

        } catch (IOException x) {
            final ErrorDialog errorDialog = new ErrorDialog(null);
            errorDialog.setTitle(I18N.getString("alert.title.start"));
            errorDialog.setMessage(I18N.getString("alert.start.failure.message"));
            errorDialog.setDetails(I18N.getString("alert.start.failure.details"));
            errorDialog.setDebugInfoWithThrowable(x);
            errorDialog.showAndWait();
            Platform.exit();
        }
    }
    
    /*
     * AppPlatform.AppNotificationHandler
     */
    @Override
    public void handleLaunch(List<String> files) {
        boolean showWelcomeDialog = files.isEmpty();

        //MavenPreferences mavenPreferences = PreferencesController.getSingleton().getMavenPreferences();
        // Creates the user library
//        userLibrary = new UserLibrary(AppPlatform.getUserLibraryFolder(),
//                () -> mavenPreferences.getArtifactsPathsWithDependencies(),
//                () -> mavenPreferences.getArtifactsFilter());

        userLibrary = context.getBean(UserLibrary.class);
        userLibrary.setOnUpdatedJarReports(jarReports -> {
            boolean shouldShowImportGluonJarAlert = false;
            for (JarReport jarReport : jarReports) {
                if (jarReport.hasGluonControls()) {
                    // We check if the jar has already been imported to avoid showing the import gluon jar
                    // alert every time Scene Builder starts for jars that have already been imported
                    if (!hasGluonJarBeenImported(preferences, jarReport.getJar().getFileName().toString())) {
                        shouldShowImportGluonJarAlert = true;
                    }

                }
            }
            if (shouldShowImportGluonJarAlert) {
                Platform.runLater(() -> {
                    MainController sceneBuilderApp = MainController.getSingleton();
                    DocumentWindowController dwc = sceneBuilderApp.getFrontDocumentWindow();
                    if (dwc == null) {
                        dwc = sceneBuilderApp.getDocumentWindowControllers().get(0);
                    }
                    ImportingGluonControlsAlert alert = new ImportingGluonControlsAlert(dwc.getStage());
                    windowIconSetting.setWindowIcon(alert);
                    if (showWelcomeDialog) {
                        alert.initOwner(context.getBean(WelcomeDialogWindowController.class).getStage());
                    }
                    alert.showAndWait();
                });
            }
            updateImportedGluonJars(preferences, jarReports);
        });

//        userLibrary.explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> userLibraryExplorationCountDidChange());
//
//        userLibrary.startWatching();

        sendTrackingStartupInfo();

        if (showWelcomeDialog) {
            // Creates an empty document
            final DocumentWindowController newWindow = makeNewWindow();
            newWindow.updateWithDefaultContent();
            newWindow.openWindow();

            // Show ScenicView Tool when the JVM is started with option -Dscenic.
            // NetBeans: set it on [VM Options] line in [Run] category of project's Properties.
            if (System.getProperty("scenic") != null) { //NOI18N
                Platform.runLater(new ScenicViewStarter(newWindow.getScene()));
            }

            WelcomeDialogWindowController wdwc = context.getBean(WelcomeDialogWindowController.class);
            
            wdwc.getStage().setOnHidden(event -> {
                showUpdateDialogIfRequired(newWindow, () -> {
                    if (!Platform.isFxApplicationThread()) {
                        Platform.runLater(() -> showRegistrationDialogIfRequired(newWindow));
                    } else {
                        showRegistrationDialogIfRequired(newWindow);
                    }

                });
            });

            // Unless we're on a Mac we're starting SB directly (fresh start)
            // so we're not opening any file and as such we should show the Welcome Dialog
            wdwc.getStage().show();

        } else {
            // Open files passed as arguments by the platform
            handleOpenFilesAction(files);
        }

    }

    private void sendTrackingStartupInfo() {
        boolean sendTrackingInfo = shouldSendTrackingInfo(preferences);

        if (sendTrackingInfo) {
            boolean update = false;
            String hash = preferences.getRegistrationHash();
            String email = preferences.getRegistrationEmail();
            boolean optIn = preferences.isRegistrationOptIn();

            tracking.sendTrackingInfo(Tracking.SCENEBUILDER_USAGE_TYPE, hash, email, optIn, update);
        }
    }

    private boolean shouldSendTrackingInfo(GlobalPreferences recordGlobal) {
        LocalDate date = recordGlobal.getLastSentTrackingInfoDate();
        boolean sendTrackingInfo = true;
        LocalDate now = LocalDate.now();

        if (date != null) {
            sendTrackingInfo = date.plusWeeks(1).isBefore(now);
            if (sendTrackingInfo) {
                recordGlobal.setLastSentTrackingInfoDate(now);
            }
        } else {
            recordGlobal.setLastSentTrackingInfoDate(now);
        }
        return sendTrackingInfo;
    }

    @Override
    public void handleOpenFilesAction(List<String> files) {
        assert files != null;
        assert files.isEmpty() == false;

        final List<File> fileObjs = new ArrayList<>();
        for (String file : files) {
            fileObjs.add(new File(file));
        }

        EditorController.updateNextInitialDirectory(fileObjs.get(0));
        
        // Fix for #45
        if (userLibrary.isFirstExplorationCompleted()) {
            performOpenFiles(fileObjs, null);
        } else {
            // open files only after the first exploration has finished
            userLibrary.firstExplorationCompletedProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (userLibrary.isFirstExplorationCompleted()) {
                        performOpenFiles(fileObjs, null);
                        userLibrary.firstExplorationCompletedProperty().removeListener(this);
                    }
                }
            });
        }
    }

    @Override
    public void handleMessageBoxFailure(Exception x) {
        final ErrorDialog errorDialog = new ErrorDialog(null);
        errorDialog.setTitle(I18N.getString("alert.title.messagebox"));
        errorDialog.setMessage(I18N.getString("alert.messagebox.failure.message"));
        errorDialog.setDetails(I18N.getString("alert.messagebox.failure.details"));
        errorDialog.setDebugInfoWithThrowable(x);
        errorDialog.showAndWait();
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

    private void closeWindow(DocumentWindowController w) {
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
    private void performOpenFile(DocumentWindowController fromWindow) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml")); //NOI18N
        fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        final List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(null);
        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            EditorController.updateNextInitialDirectory(fxmlFiles.get(0));
            performOpenFiles(fxmlFiles, fromWindow);
        }
    }

    public void performNewTemplate(Template template) {
        DocumentWindowController documentWC = getDocumentWindowControllers().get(0);
        loadTemplateInWindow(template, documentWC);
    }

    public void performNewTemplateInNewWindow(Template template) {
        final DocumentWindowController newTemplateWindow = makeNewWindow();
        loadTemplateInWindow(template, newTemplateWindow);
    }

    private void loadTemplateInWindow(Template template, DocumentWindowController documentWindowController) {
        final URL url = template.getFXMLURL();
        if (url != null) {
        	// TODO How to pass this boolean into the new Pref API ?
        	// template.getType() != Type.PHONE ? reload theme : do not reload
            documentWindowController.loadFromURL(url, template.getType() != Type.PHONE);
        }
        Template.prepareDocument(documentWindowController.getEditorController(), template);
        documentWindowController.openWindow();
    }

    private void performCloseFrontWindow() {
        for (DocumentWindowController dwc : windowList) {
            if (dwc.isFrontDocumentWindow()) {
                dwc.performCloseFrontDocumentWindow();
                break;
            }
        }
    }

    public DocumentWindowController getFrontDocumentWindow() {
        for (DocumentWindowController dwc : windowList) {
            if (dwc.isFrontDocumentWindow()) {
                return dwc;
            }
        }
        return null;
    }

    private void performOpenFiles(List<File> fxmlFiles,
                                  DocumentWindowController fromWindow) {
        assert fxmlFiles != null;
        assert fxmlFiles.isEmpty() == false;

        final Map<File, IOException> exceptions = new HashMap<>();
        for (File fxmlFile : fxmlFiles) {
            try {
                final DocumentWindowController dwc
                        = lookupDocumentWindowControllers(fxmlFile.toURI().toURL());
                if (dwc != null) {
                    // fxmlFile is already opened
                    dwc.getStage().toFront();
                } else {
                    // Open fxmlFile
                    final DocumentWindowController hostWindow;
                    final DocumentWindowController unusedWindow
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
                final ErrorDialog errorDialog = new ErrorDialog(null);
                errorDialog.setMessage(I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())));
                errorDialog.setDetails(I18N.getString("alert.open.failure1.details"));
                errorDialog.setDebugInfoWithThrowable(x);
                errorDialog.setTitle(I18N.getString("alert.title.open"));
                errorDialog.showAndWait();
                break;
            }
            default: {
                final ErrorDialog errorDialog = new ErrorDialog(null);
                if (exceptions.size() == fxmlFiles.size()) {
                    // Open operation has failed for all the files
                    errorDialog.setMessage(I18N.getString("alert.open.failureN.message"));
                    errorDialog.setDetails(I18N.getString("alert.open.failureN.details"));
                } else {
                    // Open operation has failed for some files
                    errorDialog.setMessage(I18N.getString("alert.open.failureMofN.message",
                            exceptions.size(), fxmlFiles.size()));
                    errorDialog.setDetails(I18N.getString("alert.open.failureMofN.details"));
                }
                errorDialog.setTitle(I18N.getString("alert.title.open"));
                errorDialog.showAndWait();
                break;
            }
        }
    }

    private void performExit() {

        // Check if an editing session is on going
        for (DocumentWindowController dwc : windowList) {
            if (dwc.getEditorController().isTextEditingSessionOnGoing()) {
                // Check if we can commit the editing session
                if (dwc.getEditorController().canGetFxmlText() == false) {
                    // Commit failed
                    return;
                }
            }
        }

        // Collects the documents with pending changes
        final List<DocumentWindowController> pendingDocs = new ArrayList<>();
        for (DocumentWindowController dwc : windowList) {
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
                final DocumentWindowController dwc0 = pendingDocs.get(0);
                exitConfirmed = dwc0.performCloseAction() == ActionStatus.DONE;
                break;
            }

            default: {
                assert pendingDocs.size() >= 2;

                final AlertDialog d = new AlertDialog(null);
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
            for (DocumentWindowController dwc : new ArrayList<>(windowList)) {
                // Write to java preferences before closing
                dwc.updatePreferences();
                documentWindowRequestClose(dwc);
            }
            logTimestamp(ACTION.STOP);
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

    private String getToolStylesheet() {
        return toolThemePreference.getValue().getStylesheetURL();
    }
    
    
    

    

    private void showUpdateDialogIfRequired(DocumentWindowController dwc, Runnable runAfterUpdateDialog) {
    	versionSetting.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                // This can be because the url was not reachable so we don't show the update dialog.
                return;
            }
            try {
                boolean showUpdateDialog = true;
                if (versionSetting.isCurrentVersionLowerThan(latestVersion)) {
                    if (isVersionToBeIgnored(preferences, latestVersion)) {
                        showUpdateDialog = false;
                    }

                    if (!isUpdateDialogDateReached(preferences)) {
                        showUpdateDialog = false;
                    }
                } else {
                    showUpdateDialog = false;
                }

                if (showUpdateDialog) {
                    Platform.runLater(() -> {
                        UpdateSceneBuilderDialog dialog = context.getBean(UpdateSceneBuilderDialog.class);
                        dialog.setOnHidden(event -> runAfterUpdateDialog.run());
                        dialog.showAndWait();
                    });
                } else {
                    runAfterUpdateDialog.run();
                }
            } catch (NumberFormatException ex) {
                Platform.runLater(() -> showVersionNumberFormatError(dwc));
            }
        });
    }

    private void checkUpdates(DocumentWindowController source) {
    	versionSetting.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                Platform.runLater(() -> {
                    SBAlert alert = new SBAlert(Alert.AlertType.ERROR, getFrontDocumentWindow().getStage());
                    alert.setTitle(I18N.getString("check_for_updates.alert.error.title"));
                    alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                    alert.setContentText(I18N.getString("check_for_updates.alert.error.message"));
                    alert.showAndWait();
                });
            }
            try {
                if (versionSetting.isCurrentVersionLowerThan(latestVersion)) {
                    Platform.runLater(() -> {
                        UpdateSceneBuilderDialog dialog = context.getBean(UpdateSceneBuilderDialog.class);
                        dialog.showAndWait();
                    });
                } else {
                    SBAlert alert = new SBAlert(Alert.AlertType.INFORMATION, getFrontDocumentWindow().getStage());
                    alert.setTitle(I18N.getString("check_for_updates.alert.up_to_date.title"));
                    alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                    alert.setContentText(I18N.getString("check_for_updates.alert.up_to_date.message"));
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Platform.runLater(() -> showVersionNumberFormatError(source));
            }
        });
    }

    private void showVersionNumberFormatError(DocumentWindowController dwc) {
        SBAlert alert = new SBAlert(Alert.AlertType.ERROR, dwc.getStage());
        // The version number format is not supported and this is most probably only happening
        // in development so we don't localize the strings
        alert.setTitle("Error");
        alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
        alert.setContentText("Version number format not supported. Maybe using SNAPSHOT or RC versions.");
        alert.showAndWait();
    }

    private boolean isVersionToBeIgnored(GlobalPreferences recordGlobal, String latestVersion) {
        String ignoreVersion = recordGlobal.getIgnoreVersion();
        return latestVersion.equals(ignoreVersion);
    }

    private boolean isUpdateDialogDateReached(GlobalPreferences recordGlobal) {
        LocalDate dialogDate = recordGlobal.getShowUpdateDialogDate();
        if (dialogDate == null) {
            return true;
        } else if (dialogDate.isBefore(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }

    private void showRegistrationDialogIfRequired(DocumentWindowController dwc) {
        String registrationHash = preferences.getRegistrationHash();
        if (registrationHash == null) {
            performControlAction(ApplicationControlAction.REGISTER, dwc);
        } else {
            String registrationEmail = preferences.getRegistrationEmail();
            if (registrationEmail == null && Math.random() > 0.8) {
                performControlAction(ApplicationControlAction.REGISTER, dwc);
            }
        }
    }

    @Override
	public void logInfoMessage(String key) {
        for (DocumentWindowController dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle());
        }
    }

    @Override
	public void logInfoMessage(String key, Object... args) {
        for (DocumentWindowController dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
        }
    }

    private static void updateImportedGluonJars(GlobalPreferences preferences, List<? extends JarReport> jars) {
        List<String> jarReportCollection = new ArrayList<>();
        for (JarReport jarReport : jars) {
            if (jarReport.hasGluonControls()) {
                jarReportCollection.add(jarReport.getJar().getFileName().toString());
            }
        }
        if (jarReportCollection.isEmpty()) {
        	preferences.setImportedGluonJars(new String[0]);
        } else {
        	preferences.setImportedGluonJars(jarReportCollection.toArray(new String[0]));
        }
    }

    private static boolean hasGluonJarBeenImported(GlobalPreferences preferences, String jar) {
        String[] importedJars = preferences.getImportedGluonJars();
        if (importedJars == null) {
            return false;
        }

        for (String importedJar : importedJars) {
            if (jar.equals(importedJar)) {
                return true;
            }
        }
        return false;
    }

    public static void applyToAllDocumentWindows(Consumer<DocumentWindowController> consumer) {
    	//TODO check if this is realy working, cause i've some doubts
        for (DocumentWindowController dwc : getSingleton().getDocumentWindowControllers()) {
            consumer.accept(dwc);
        }
    }

	public HostServices getHostServices() {
		return hostServices;
	}

}
