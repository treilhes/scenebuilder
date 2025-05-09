/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloaderDispatcher;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.lifecycle.DisposeWithDocument;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.preference.Preferences;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.DockManager;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.WindowPreferenceTracker;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Provider;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 * The ApplicationInstanceController class is responsible for managing the lifecycle of an application instance.
 * It handles the initialization and disposal of the application instance, manages the document window, and
 * interacts with various services such as the file system, internationalization (I18N), and the JavaFX thread classloader.
 *
 * This class is annotated with @ApplicationInstanceSingleton, indicating that only one instance of this class
 * should exist per application instance.
 *
 * The class uses the PostConstruct annotation to specify a method that should be run after the instance has been
 * constructed and dependency injection has been performed.
 *
 * It implements the com.gluonhq.jfxapps.core.api.application.ApplicationInstance interface, which defines the
 * contract for an application instance in the system.
 *
 * The class also manages various preferences related to the application instance and handles key events within the
 * application.
 *
 */
@ApplicationInstanceSingleton
public class ApplicationInstanceController implements ApplicationInstance {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInstanceController.class);

    private final I18N i18n;
    private final JfxAppContext context;
    private final JfxAppPlatform jfxAppPlatform;
    private final JavafxThreadClassloaderDispatcher dispatcher;
    private final JavafxThreadClassloader fxThreadClassloader;
    private final MainInstanceWindow documentWindow;
    private final FileSystem fileSystem;

    private final Preferences preferences;

    private EventHandler<KeyEvent> mainKeyEventFilter;

    private final FxomEvents applicationInstanceEvents;
    private final Provider<Optional<List<InitWithDocument>>> initializations;
    private final Provider<Optional<List<DisposeWithDocument>>> finalizations;

    private final InstancesManager main;
    private final ApplicationEvents sceneBuilderManager;

    private FXOMDocument fxomDocument;
    private final InlineEdit inlineEdit;
    private final MessageLogger messageLogger;

    private final PreferenceManager preferenceManager;
    private final DockViewController viewMenuController;
    private final WindowPreferenceTracker tracker;

    /*
     * DocumentWindowController
     */
    // @formatter:off
    public ApplicationInstanceController(
            I18N i18n,
            JfxAppContext context,
            JfxAppPlatform jfxAppPlatform,
            JavafxThreadClassloaderDispatcher dispatcher,
            JavafxThreadClassloader fxThreadClassloader,
            FileSystem fileSystem,
            Preferences preferences,
            InlineEdit inlineEdit,
            MessageLogger messageLogger,
            MainInstanceWindow documentWindow,
            FxomEvents documentManager,
            DockManager dockManager,
            DockViewController viewMenuController,
            InstancesManager main,
            ApplicationEvents sceneBuilderManager,
            WindowPreferenceTracker tracker,
            Provider<Optional<List<InitWithDocument>>> initializations,
            Provider<Optional<List<DisposeWithDocument>>> finalizations,
            List<Class<? extends View>> classViews

    ) {
     // @formatter:on
        super();
        this.i18n = i18n;
        this.context = context;
        this.jfxAppPlatform = jfxAppPlatform;
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
        this.applicationInstanceEvents = documentManager;
        this.preferences = preferences;
        //this.inspectorPanelController = inspectorPanelController;
        //this.libraryPanelController = libraryPanelController;

        //this.pathPreference = pathPreference;
        this.initializations = initializations;
        this.finalizations = finalizations;

        this.tracker = tracker;

        this.preferenceManager = new PreferenceManager();

        preferences.read();

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

        documentWindow.composeWindow();

        fileSystem.startWatcher();

        applicationInstanceEvents.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        sceneBuilderManager.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        jfxAppPlatform.runOnFxThreadWithActiveScope(() -> {
            initializeDocumentWindow();
        });

        applicationInstanceEvents.fxomDocument().subscribe(fd -> {
            boolean firstLoad = fxomDocument == null;
            fxomDocument = fd;

            if (firstLoad) { // load the last ui prefs of the document if any
                //preferenceManager.untrack();
                preferences.read();

                jfxAppPlatform.runOnFxThreadWithActiveScope(() -> {
                    preferenceManager.apply();
                    preferenceManager.track();
                });
            }
        });
    }

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

    private void updatePreferences() {
        if (fxomDocument == null) {
            return;
        }
        final URL fxmlLocation = fxomDocument.getLocation();
        if (fxmlLocation == null) {
            // Document has not been saved => nothing to write
            // This is the case with initial empty document
            return;
        }

        preferences.save();
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
        tracker.initialize(documentWindow);

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

        jfxAppPlatform.removeScope(this);
    }

    @Override
    public void onFocus() {
        jfxAppPlatform.setCurrentScope(this);
        sceneBuilderManager.documentScoped().set(this);
    }

    @Override
    public boolean isDocumentDirty() {
        return applicationInstanceEvents.dirty().get();
    }

    @Override
    public MainInstanceWindow getDocumentWindow() {
        return documentWindow;
    }

    @Override
    public void openWindow() {
        jfxAppPlatform.runOnFxThreadWithActiveScope(() ->{
            documentWindow.openWindow();

            // initialize preference binding
            preferenceManager.apply();
            preferenceManager.track();
        });

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
        FXOMDocument fxomDocument = applicationInstanceEvents.fxomDocument().get();
        return fxomDocument == null ? null : fxomDocument.getLocation();
    }

    @Override
    public void loadFromFile(File file, boolean keepTrackOfLocation) throws IOException {
        fileSystem.loadFromFile(file, keepTrackOfLocation);
    }

    @Override
    public void loadBlank() {
        fileSystem.loadDefaultContent();
    }

    @Override
    public JfxAppContext getContext() {
        return context;
    }

    private class PreferenceManager {

        public void apply() {
            viewMenuController.performLoadDockAndViewsPreferences();
            tracker.apply();
        }

        public void onClose() {
            tracker.onClose();
        }

        public void track() {
            tracker.track();
        }

        public void untrack() {
            tracker.untrack();
        }
    }
}

