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
package com.gluonhq.jfxapps.core.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.context.annotation.FxThread;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloaderDispatcher;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.lifecycle.DisposeWithDocument;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.preferences.Preferences;
import com.gluonhq.jfxapps.core.api.subjects.DockManager;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.WindowPreferenceTracker;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Provider;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 *
 */
@ApplicationInstanceSingleton
public class ApplicationInstanceController implements com.gluonhq.jfxapps.core.api.application.ApplicationInstance {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInstanceController.class);

    private final I18N i18n;
    private final JavafxThreadClassloaderDispatcher dispatcher;
    private final JavafxThreadClassloader fxThreadClassloader;
    private final MainInstanceWindow documentWindow;
    private final FileSystem fileSystem;

    //private final MenuBar menuBarController;
    //private final Content contentPanelController;
    //private final Workspace workspace;
    //private final RecentItemsPreference recentItemsPreference;
    private final Preferences documentPreferencesController;
    //private final LastDockUuidPreference lastDockUuidPreference;
    // PREFERENCES
    //private FileTime loadFileTime;

    private EventHandler<KeyEvent> mainKeyEventFilter;

    //private final Provider<PathPreference> pathPreference;
    private final DocumentManager documentManager;
    private final Provider<Optional<List<InitWithDocument>>> initializations;
    private final Provider<Optional<List<DisposeWithDocument>>> finalizations;

    private final InstancesManager main;
    private final SceneBuilderManager sceneBuilderManager;

    //private final SelectionBarController selectionBarController;
    private FXOMDocument fxomDocument;
    private final InlineEdit inlineEdit;
    private final MessageLogger messageLogger;

    private final PreferenceManager preferenceManager;
    private final DockViewController viewMenuController;
    private final List<WindowPreferenceTracker> trackers;





    /*
     * DocumentWindowController
     */
    // @formatter:off
    public ApplicationInstanceController(
            I18N i18n,
            JavafxThreadClassloaderDispatcher dispatcher,
            JavafxThreadClassloader fxThreadClassloader,
            FileSystem fileSystem,
            //RecentItemsPreference recentItemsPreference,
            //WildcardImportsPreference wildcardImportsPreference,
            Preferences documentPreferencesController,
            //Workspace workspace,
//            @Autowired(required = false) Content contentPanelController,
//            @Autowired Editor editorController,
            InlineEdit inlineEdit,
            MessageLogger messageLogger,
            MainInstanceWindow documentWindow,
            //MenuBarController menuBarController,
//            Provider<MessageBarController> messageBarController,
//            SelectionBarController selectionBarController,
            DocumentManager documentManager,
            //@Autowired DocumentPanelController documentPanelController,
            //@Autowired InspectorPanelController inspectorPanelController,
            //@Autowired LibraryPanel libraryPanelController,
            //@Autowired @Lazy CssPanelController cssPanelController,
            DockManager dockManager,
            DockViewController viewMenuController,
            InstancesManager main,
            SceneBuilderManager sceneBuilderManager,
            List<WindowPreferenceTracker> trackers,
            //Provider<PathPreference> pathPreference,
            //Provider<LastDockUuidPreference> lastDockUuidPreference,
            Provider<Optional<List<InitWithDocument>>> initializations,
            Provider<Optional<List<DisposeWithDocument>>> finalizations,
            List<Class<? extends View>> classViews

    ) {
     // @formatter:on
        super();
        this.i18n = i18n;
        this.dispatcher = dispatcher;
        this.fxThreadClassloader = fxThreadClassloader;
        this.fileSystem = fileSystem;
        this.documentWindow = documentWindow;
        //this.workspace = workspace;
        this.main = main;
        this.sceneBuilderManager = sceneBuilderManager;
        //this.recentItemsPreference = recentItemsPreference;
        //this.wildcardImportsPreference = wildcardImportsPreference;
        //this.menuBarController = menuBarController;
        //this.messageBarController = messageBarController;
        //this.selectionBarController = selectionBarController;
        this.inlineEdit = inlineEdit;
        this.messageLogger = messageLogger;

        this.viewMenuController = viewMenuController;
        this.documentManager = documentManager;
        this.documentPreferencesController = documentPreferencesController;
        //this.inspectorPanelController = inspectorPanelController;
        //this.libraryPanelController = libraryPanelController;

        //this.pathPreference = pathPreference;
        this.initializations = initializations;
        this.finalizations = finalizations;

        this.trackers = trackers;

        this.preferenceManager = new PreferenceManager();

        documentPreferencesController.readFromJavaPreferences();

        mainKeyEventFilter = event -> {
            // ------------------------------------------------------------------
            // TEXT INPUT CONTROL
            // ------------------------------------------------------------------
            // Common editing actions handled natively and defined as application
            // accelerators
            //
            // The platform support is not mature/stable enough to rely on.
            // Indeed, the behavior may differ :
            // - when using system menu bar vs not using it
            // - when using accelerators vs using menu items
            // - depending on the focused control (TextField vs ComboBox)
            //
            // On SB side,5 we decide for now to consume events that may be handled natively
            // so ALL actions are defined in our ApplicationMenu class.
            //
            // This may be revisit when platform implementation will be more reliable.
            //
            final Node focusOwner = documentWindow.getScene().getFocusOwner();

            final KeyCombination accelerator = null;//getAccelerator(event);



            // TODO ensure menu event on Mac is not performed twice

//            if (isTextInputControlEditing(focusOwner) && accelerator != null) {

//                focusOwner.getInputMap()
//                          .lookupMapping(KeyBinding.toKeyBinding(event))
//                          .ifPresent(mapping -> {
//                              // The event is handled natively
//                              if (mapping.getSpecificity(event) > 0) {
//                                  // When using system menu bar, the event is handled natively
//                                  // before the application receives it : we just consume the event
//                                  // so the editing action is not performed a second time by the app.
//                                  if (menuBarController.getMenuBar().isUseSystemMenuBar()) {
//                                      event.consume();
//                                  }
//                              }
//                          });

//            }

            // ------------------------------------------------------------------
            // Hierarchy TreeView + select all
            // ------------------------------------------------------------------
            // Select all is handled natively by TreeView (= hierarchy panel control).
            boolean modifierDown = (JfxAppsPlatform.IS_MAC ? event.isMetaDown() : event.isControlDown());
            boolean isSelectAll = KeyCode.A.equals(event.getCode()) && modifierDown;
            // TODO ensure select all is still working on mac
            //TODO remove commented //if (documentPanelController.getHierarchyPanelController().getPanelControl().isFocused() && isSelectAll) {

            //if (documentPanelController.getHierarchyPanelController().getPanelControl().isFocused() && isSelectAll)
//            if (false)
//            {
//                // Consume the event so the control action is not performed natively.
//                event.consume();
//                // When using system menu bar, the control action is performed by the app.
//                if (!menuBarController.getMenuBar().isUseSystemMenuBar()) {
//
//
////                    if (canPerformControlAction(DocumentControlAction.SELECT_ALL)) {
////                        performControlAction(DocumentControlAction.SELECT_ALL);
////                    }
//                }
//            }

//            // MenuItems define a single accelerator.
//            // BACK_SPACE key must be handled same way as DELETE key.
//            boolean isBackspace = KeyCode.BACK_SPACE.equals(event.getCode());
//            if (!isTextInputControlEditing(focusOwner) && isBackspace) {
//                if (canPerformEditAction(DocumentEditAction.DELETE)) {
//                    performEditAction(DocumentEditAction.DELETE);
//                }
//                event.consume();
//            }
        };
    }

    @PostConstruct
    public void init() throws Exception {
        initializations.get().ifPresent(l -> l.forEach(a -> a.initWithDocument()));
        //editorController.initialize();

        fileSystem.startWatcher();

        documentManager.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        sceneBuilderManager.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        JfxAppPlatform.runOnFxThreadWithActiveScope(() -> {
            initializeDocumentWindow();
        });

        documentManager.fxomDocument().subscribe(fd -> {
            boolean firstLoad = fxomDocument == null;
            fxomDocument = fd;

            if (firstLoad) { // load the last ui prefs of the document if any
                preferenceManager.untrack();
                documentPreferencesController.readFromJavaPreferences();

                JfxAppPlatform.runOnFxThreadWithActiveScope(() -> {
                    preferenceManager.apply();
                    preferenceManager.track();
                });
            }
        });
    }



//    @Override
//    public void loadFromURL(URL fxmlURL, boolean keepTrackOfLocation) {
//        assert fxmlURL != null;
//        try {
//            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
//            editorController.setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
//            updateLoadFileTime();
//            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet
//
//
//            documentWindow.untrack();
//            documentPreferencesController.readFromJavaPreferences();
//
//            SbPlatform.runForDocumentLater(() -> {
//                documentWindow.apply();
//                documentWindow.track();
//            });
//            // TODO remove after checking the new watching system is operational in
//            // EditorController or in filesystem
//            // watchingController.update();
//        } catch (IOException x) {
//            throw new IllegalStateException(x);
//        }
//    }
//
//    @Override
//    public void updateWithDefaultContent() {
//        try {
//            editorController.setFxmlTextAndLocation("", null, true); // NOI18N
//            updateLoadFileTime();
//            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet
//            // TODO remove after checking the new watching system is operational in
//            // EditorController or in filesystem
//            // watchingController.update();
//        } catch (IOException x) {
//            throw new IllegalStateException(x);
//        }
//    }
//
//    public void reload() throws IOException {
//        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
//        final URL fxmlURL = fxomDocument.getLocation();
//        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
//        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, true);
//        updateLoadFileTime();
//        // Here we do not invoke updateStageTitleAndPreferences() neither
//        // watchingController.update()
//    }

    @Override
    public boolean isUnused() {
        /*
         * A document window controller is considered as "unused" if: //NOCHECK 1) it has
         * not fxml text 2) it is not dirty 3) it is unamed
         */
        final boolean noFxmlText = (fxomDocument == null) || (fxomDocument.getFxomRoot() == null);
        final boolean clean = !isDocumentDirty();
        final boolean noName = (fxomDocument != null) && (fxomDocument.getLocation() == null);

        return noFxmlText && clean && noName;
    }

    @Override
    public boolean isInited() {
        return documentWindow != null;
    }

    @Override
    public boolean hasContent() {
        final boolean noFxmlText = (fxomDocument == null) || (fxomDocument.getFxomRoot() == null);
        return noFxmlText;
    }

    @Override
    public boolean hasName() {
        final boolean hasName = (fxomDocument != null) && (fxomDocument.getLocation() != null);
        return hasName;
    }

    @Override
    public String getName() {
        final String name = hasName() ? fxomDocument.getLocation().toExternalForm() : "";
        return name;
    }

    @Override
    public void updatePreferences() {
        if (fxomDocument == null) {
            return;
        }
        final URL fxmlLocation = fxomDocument.getLocation();
        if (fxmlLocation == null) {
            // Document has not been saved => nothing to write
            // This is the case with initial empty document
            return;
        }

        documentPreferencesController.writeToJavaPreferences();
    }

    public void initializeDocumentWindow() {

        dispatcher.register(documentWindow.getStage(), fxThreadClassloader);

        documentWindow.getStage().focusedProperty().addListener((ob, o, n) -> {
            if (n) {
                this.onFocus();
            }
        });

        documentWindow.setCloseHandler(this::close);
        documentWindow.setFocusHandler(this::onFocus);

        documentWindow.setMainKeyPressedEvent(mainKeyEventFilter);

        //editorController.setOwnerWindow(documentWindow.getStage());

        logger.info("Opening window");
        documentWindow.openWindow();
    }

    @Override
    public void close() {
        onFocus();

        closeWindow();
     // Write java preferences at close time but before losing the current document
        // scope

        updatePreferences();

        fileSystem.stopWatcher();
        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // finalizations list must handle the case below
        //// Stops watching
        // editorController.stopFileWatching();
        // watchingController.stop();

        finalizations.get().ifPresent(f -> f.forEach(a -> a.disposeWithDocument()));


        // Closes if confirmed
        main.notifyInstanceClosed(this);

        JfxAppContext.applicationInstanceScope.removeScope(this);
    }

    @Override
    public void onFocus() {
        JfxAppContext.applicationInstanceScope.setCurrentScope(this);
        sceneBuilderManager.documentScoped().onNext(this);
    }


//    /**
//     * Returns true if the specified node is part of the main scene and is either a
//     * TextInputControl or a ComboBox.
//     *
//     * @param node the focused node of the main scene
//     * @return
//     */
//    private boolean isTextInputControlEditing(Node node) {
//        return (node instanceof TextInputControl || node instanceof ComboBox);
//    }
//
//    private TextInputControl getTextInputControl(Node node) {
//        assert isTextInputControlEditing(node);
//        final TextInputControl tic;
//        if (node instanceof TextInputControl) {
//            tic = (TextInputControl) node;
//        } else {
//            assert node instanceof ComboBox;
//            final ComboBox<?> cb = (ComboBox<?>) node;
//            tic = cb.getEditor();
//        }
//        return tic;
//    }


//    @Override
//    public void updateLoadFileTime() {
//
//        final URL fxmlURL = fxomDocument.getLocation();
//        if (fxmlURL == null) {
//            loadFileTime = null;
//        } else {
//            try {
//                final Path fxmlPath = Paths.get(fxmlURL.toURI());
//                if (Files.exists(fxmlPath)) {
//                    loadFileTime = Files.getLastModifiedTime(fxmlPath);
//                } else {
//                    loadFileTime = null;
//                }
//            } catch (URISyntaxException x) {
//                throw new RuntimeException("Bug", x); // NOI18N
//            } catch (IOException x) {
//                loadFileTime = null;
//            }
//        }
//    }
//
//    private boolean checkLoadFileTime() throws IOException {
//        assert fxomDocument.getLocation() != null;
//
//        /*
//         * loadFileTime == null => fxml file does not exist => TRUE
//         *
//         * loadFileTime != null => fxml file does/did exist
//         *
//         * currentFileTime == null => fxml file no longer exists => TRUE
//         *
//         * currentFileTime != null => fxml file still exists =>
//         * loadFileTime.compare(currentFileTime) == 0
//         */
//
//        boolean result;
//        if (loadFileTime == null) {
//            // editorController.getFxmlLocation() does not exist yet
//            result = true;
//        } else {
//            try {
//                // editorController.getFxmlLocation() still exists
//                // Check if its file time matches loadFileTime
//                Path fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
//                FileTime currentFileTime = Files.getLastModifiedTime(fxmlPath);
//                result = loadFileTime.compareTo(currentFileTime) == 0;
//            } catch (NoSuchFileException x) {
//                // editorController.getFxmlLocation() no longer exists
//                result = true;
//            } catch (URISyntaxException x) {
//                throw new RuntimeException("Bug", x); // NOI18N
//            }
//        }
//
//        return result;
//    }

    @Override
    public boolean isDocumentDirty() {
        return documentManager.dirty().get();
    }

    @Override
    public MainInstanceWindow getDocumentWindow() {
        return documentWindow;
    }

    @Override
    @FxThread
    public void openWindow() {
        documentWindow.openWindow();

        // initialize preference binding
        preferenceManager.apply();
        preferenceManager.track();
    }

    @Override
    public void closeWindow() {
        preferenceManager.onClose();
        documentWindow.closeWindow();
    }

    @Override
    public boolean isEditing() {
        if (inlineEdit.isTextEditingSessionOnGoing()) {
            // Check if we can commit the editing session
            if (inlineEdit.canGetFxmlText() == false) {
                // Commit failed
                return true;
            }
        }
        return false;
    }

    @Override
    public void logInfoMessage(String key) {
        messageLogger.logInfoMessage(key, i18n.getBundle());
    }

    @Override
    public void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, i18n.getBundle(), args);
    }

    @Override
    public URL getLocation() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        return fxomDocument == null ? null : fxomDocument.getLocation();
    }

    @Override
    public void loadFromFile(File file) throws IOException {
        fileSystem.loadFromFile(file);
    }

    @Override
    public void loadBlank() {
        fileSystem.loadDefaultContent();
    }

    private class PreferenceManager {

        public void apply() {
            viewMenuController.performLoadDockAndViewsPreferences();
            trackers.forEach(WindowPreferenceTracker::apply);
        }

        public void onClose() {
            trackers.forEach(WindowPreferenceTracker::onClose);
        }

        public void track() {
            trackers.forEach(WindowPreferenceTracker::track);
        }

        public void untrack() {
            trackers.forEach(WindowPreferenceTracker::untrack);
        }

    }
}

