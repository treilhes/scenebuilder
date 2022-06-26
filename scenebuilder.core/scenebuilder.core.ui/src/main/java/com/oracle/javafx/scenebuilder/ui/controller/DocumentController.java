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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.MenuBar;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithDocument;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.preferences.Preferences;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;
import com.oracle.javafx.scenebuilder.ui.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.ui.message.MessageBarController;
import com.oracle.javafx.scenebuilder.ui.preferences.document.PathPreference;
import com.oracle.javafx.scenebuilder.ui.selectionbar.SelectionBarController;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentController implements Document, InitializingBean {

    private final DocumentWindow documentWindow;
    private final Editor editorController;
    private final MenuBar menuBarController;
    private final Content contentPanelController;
    private final RecentItemsPreference recentItemsPreference;
    private final Preferences documentPreferencesController;
    //private final LastDockUuidPreference lastDockUuidPreference;
    // PREFERENCES
    private FileTime loadFileTime;

    private EventHandler<KeyEvent> mainKeyEventFilter;

    private final PathPreference pathPreference;
    private final DocumentManager documentManager;
    private final List<InitWithDocument> initializations;
    private final List<DisposeWithDocument> finalizations;

    private final Main main;
    private final SceneBuilderManager sceneBuilderManager;
    private final MessageBarController messageBarController;
    private final SelectionBarController selectionBarController;
    private OMDocument<?> fxomDocument;
    private final InlineEdit inlineEdit;
    private final MessageLogger messageLogger;

    /*
     * DocumentWindowController
     */
    // @formatter:off
    public DocumentController(
            @Autowired RecentItemsPreference recentItemsPreference,
            //@Autowired WildcardImportsPreference wildcardImportsPreference,
            @Autowired Preferences documentPreferencesController,
            @Autowired(required = false) Content contentPanelController,
            @Autowired Editor editorController,
            InlineEdit inlineEdit,
            MessageLogger messageLogger,
            @Autowired DocumentWindow documentWindow,
            @Autowired MenuBarController menuBarController,
            @Lazy @Autowired MessageBarController messageBarController,
            @Autowired SelectionBarController selectionBarController,
            DocumentManager documentManager,
            //@Autowired DocumentPanelController documentPanelController,
            //@Autowired InspectorPanelController inspectorPanelController,
            //@Autowired LibraryPanel libraryPanelController,
            //@Autowired @Lazy CssPanelController cssPanelController,
            @Autowired DockManager dockManager,

            @Autowired Main main,
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Lazy @Autowired PathPreference pathPreference,
            @Lazy @Autowired LastDockUuidPreference lastDockUuidPreference,
            @Lazy @Autowired(required = false) List<InitWithDocument> initializations,
            @Lazy @Autowired(required = false) List<DisposeWithDocument> finalizations,
            @Autowired List<Class<? extends View>> classViews

    ) {
     // @formatter:on
        super();
        this.editorController = editorController;
        this.documentWindow = documentWindow;
        this.main = main;
        this.sceneBuilderManager = sceneBuilderManager;
        this.recentItemsPreference = recentItemsPreference;
        //this.wildcardImportsPreference = wildcardImportsPreference;
        this.menuBarController = menuBarController;
        this.messageBarController = messageBarController;
        this.selectionBarController = selectionBarController;
        this.inlineEdit = inlineEdit;
        this.messageLogger = messageLogger;

        this.contentPanelController = contentPanelController;
        this.documentManager = documentManager;
        this.documentPreferencesController = documentPreferencesController;
        //this.inspectorPanelController = inspectorPanelController;
        //this.libraryPanelController = libraryPanelController;

        this.pathPreference = pathPreference;
        this.initializations = initializations;
        this.finalizations = finalizations;

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
            if (isTextInputControlEditing(focusOwner) && accelerator != null) {

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

            }

            // ------------------------------------------------------------------
            // Hierarchy TreeView + select all
            // ------------------------------------------------------------------
            // Select all is handled natively by TreeView (= hierarchy panel control).
            boolean modifierDown = (EditorPlatform.IS_MAC ? event.isMetaDown() : event.isControlDown());
            boolean isSelectAll = KeyCode.A.equals(event.getCode()) && modifierDown;
            //TODO remove commented //if (documentPanelController.getHierarchyPanelController().getPanelControl().isFocused() && isSelectAll) {
            //if (documentPanelController.getHierarchyPanelController().getPanelControl().isFocused() && isSelectAll)
            if (false)
            {
                // Consume the event so the control action is not performed natively.
                event.consume();
                // When using system menu bar, the control action is performed by the app.
                if (!menuBarController.getMenuBar().isUseSystemMenuBar()) {
                    // TODO ensure select all is still working

//                    if (canPerformControlAction(DocumentControlAction.SELECT_ALL)) {
//                        performControlAction(DocumentControlAction.SELECT_ALL);
//                    }
                }
            }

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

    @Override
    public void afterPropertiesSet() throws Exception {
        initializations.forEach(a -> a.initWithDocument());
        editorController.initialize();

        documentManager.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        sceneBuilderManager.closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> close());

        SbPlatform.runForDocumentLater(() -> {
            initializeDocumentWindow();
        });

        documentManager.omDocument().subscribe(fd -> fxomDocument = fd);
    }

    @Override
    public void loadFromFile(File fxmlFile) throws IOException {
        final URL fxmlURL = fxmlFile.toURI().toURL();
        loadFromURL(fxmlURL, true);

        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // watchingController.update();

        // WarnThemeAlert.showAlertIfRequired(themePreference,
        // editorController.getFxomDocument(), documentWindow.getStage());
    }

    @Override
    public void loadFromURL(URL fxmlURL, boolean keepTrackOfLocation) {
        assert fxmlURL != null;
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
            updateLoadFileTime();
            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet


            documentWindow.untrack();
            documentPreferencesController.readFromJavaPreferences();

            SbPlatform.runForDocumentLater(() -> {
                documentWindow.apply();
                documentWindow.track();
            });
            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }

    @Override
    public void updateWithDefaultContent() {
        try {
            editorController.setFxmlTextAndLocation("", null, true); // NOI18N
            updateLoadFileTime();
            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet
            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }

    public void reload() throws IOException {
        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
        final URL fxmlURL = fxomDocument.getLocation();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, true);
        updateLoadFileTime();
        // Here we do not invoke updateStageTitleAndPreferences() neither
        // watchingController.update()
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
        return editorController != null;
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

        final URL fxmlLocation = editorController.getFxmlLocation();
        if (fxmlLocation == null) {
            // Document has not been saved => nothing to write
            // This is the case with initial empty document
            return;
        }
        try {
            pathPreference.setValue(new File(fxmlLocation.toURI()).getPath());
        } catch (URISyntaxException e) {
            // TODO log something here
            e.printStackTrace();
        }

        // recentItems may not contain the current document
        // if the Open Recent -> Clear menu has been invoked
        if (!recentItemsPreference.containsRecentItem(fxmlLocation)) {
            recentItemsPreference.addRecentItem(fxmlLocation);
        }

        documentPreferencesController.writeToJavaPreferences();
    }

    public void initializeDocumentWindow() {

        documentWindow.setMenuBar(menuBarController.getMenuBar());
        //setContentPane(contentPanelController.getRoot());
        documentWindow.setMessageBar(messageBarController.getRoot());

        messageBarController.getSelectionBarHost().getChildren().add(selectionBarController.getRoot());

        documentWindow.getStage().focusedProperty().addListener((ob, o, n) -> {
            if (n) {
                this.onFocus();
            }
        });

        documentWindow.setCloseHandler(this::close);
        documentWindow.setFocusHandler(this::onFocus);

        documentWindow.setMainKeyPressedEvent(mainKeyEventFilter);
        documentWindow.setContentPane(contentPanelController.getRoot());

        editorController.setOwnerWindow(documentWindow.getStage());

        documentWindow.openWindow();
    }

    @Override
    public void close() {
        onFocus();

        closeWindow();
     // Write java preferences at close time but before losing the current document
        // scope

        updatePreferences();

        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // finalizations list must handle the case below
        //// Stops watching
        // editorController.stopFileWatching();
        // watchingController.stop();

        finalizations.forEach(a -> a.disposeWithDocument());


        // Closes if confirmed
        main.notifyDocumentClosed(this);

        DocumentScope.removeScope(this);
    }

    @Override
    public void onFocus() {
        DocumentScope.setCurrentScope(this);
        sceneBuilderManager.documentScoped().onNext(this);
    }


    /**
     * Returns true if the specified node is part of the main scene and is either a
     * TextInputControl or a ComboBox.
     *
     * @param node the focused node of the main scene
     * @return
     */
    private boolean isTextInputControlEditing(Node node) {
        return (node instanceof TextInputControl || node instanceof ComboBox);
    }

    private TextInputControl getTextInputControl(Node node) {
        assert isTextInputControlEditing(node);
        final TextInputControl tic;
        if (node instanceof TextInputControl) {
            tic = (TextInputControl) node;
        } else {
            assert node instanceof ComboBox;
            final ComboBox<?> cb = (ComboBox<?>) node;
            tic = cb.getEditor();
        }
        return tic;
    }


    @Override
    public void updateLoadFileTime() {

        final URL fxmlURL = editorController.getFxmlLocation();
        if (fxmlURL == null) {
            loadFileTime = null;
        } else {
            try {
                final Path fxmlPath = Paths.get(fxmlURL.toURI());
                if (Files.exists(fxmlPath)) {
                    loadFileTime = Files.getLastModifiedTime(fxmlPath);
                } else {
                    loadFileTime = null;
                }
            } catch (URISyntaxException x) {
                throw new RuntimeException("Bug", x); // NOI18N
            } catch (IOException x) {
                loadFileTime = null;
            }
        }
    }

    private boolean checkLoadFileTime() throws IOException {
        assert editorController.getFxmlLocation() != null;

        /*
         * loadFileTime == null => fxml file does not exist => TRUE
         *
         * loadFileTime != null => fxml file does/did exist
         *
         * currentFileTime == null => fxml file no longer exists => TRUE
         *
         * currentFileTime != null => fxml file still exists =>
         * loadFileTime.compare(currentFileTime) == 0
         */

        boolean result;
        if (loadFileTime == null) {
            // editorController.getFxmlLocation() does not exist yet
            result = true;
        } else {
            try {
                // editorController.getFxmlLocation() still exists
                // Check if its file time matches loadFileTime
                Path fxmlPath = Paths.get(editorController.getFxmlLocation().toURI());
                FileTime currentFileTime = Files.getLastModifiedTime(fxmlPath);
                result = loadFileTime.compareTo(currentFileTime) == 0;
            } catch (NoSuchFileException x) {
                // editorController.getFxmlLocation() no longer exists
                result = true;
            } catch (URISyntaxException x) {
                throw new RuntimeException("Bug", x); // NOI18N
            }
        }

        return result;
    }

    @Override
    public boolean isDocumentDirty() {
        return documentManager.dirty().get();
    }

    @Override
    public DocumentWindow getDocumentWindow() {
        return documentWindow;
    }

    @Override
    public void openWindow() {
        documentWindow.openWindow();
    }

    @Override
    public void closeWindow() {
        documentWindow.closeWindow();
    }

    @Override
    public URL getFxmlLocation() {
        return editorController.getFxmlLocation();
    }

    @Override
    public Editor getEditorController() {
        return editorController;
    }

    @Override
    public FileTime getLoadFileTime() {
        return loadFileTime;
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
        messageLogger.logInfoMessage(key, I18N.getBundle());
    }

    @Override
    public void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, I18N.getBundle(), args);
    }

}

