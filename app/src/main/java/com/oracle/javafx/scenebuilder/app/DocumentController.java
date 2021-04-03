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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.ControlAction;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Editor.EditAction;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.library.LibraryPanel;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithDocument;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;
import com.oracle.javafx.scenebuilder.app.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.app.message.MessageBarController;
import com.oracle.javafx.scenebuilder.app.preferences.DocumentPreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.document.PathPreference;
import com.oracle.javafx.scenebuilder.contenteditor.controller.ContentPanelController;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;
import com.oracle.javafx.scenebuilder.document.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController.SectionId;
import com.oracle.javafx.scenebuilder.kit.ResourceUtils;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.kit.selectionbar.SelectionBarController;
//import com.oracle.javafx.scenebuilder.library.controller.JarAnalysisReportController;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;
import com.oracle.javafx.scenebuilder.sb.preferences.global.WildcardImportsPreference;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentController implements Document, InitializingBean {

    private final DocumentWindow documentWindow;
    private final Editor editorController;
    private final FileSystem fileSystem;
    private final MenuBarController menuBarController;
    private final ContentPanelController contentPanelController;
    //private final DocumentPanelController documentPanelController;
    private final InspectorPanelController inspectorPanelController;
    private final CssPanelController cssPanelController;
    private final LibraryPanel libraryPanelController;
    private final SelectionBarController selectionBarController;
    private final MessageBarController messageBarController;
    private final WildcardImportsPreference wildcardImportsPreference;
    private final RecentItemsPreference recentItemsPreference;
    private final DocumentPreferencesController documentPreferencesController;
    //private final LastDockUuidPreference lastDockUuidPreference;
    // PREFERENCES
    private FileTime loadFileTime;

    private EventHandler<KeyEvent> mainKeyEventFilter;

    private final PathPreference pathPreference;
    private final DocumentManager documentManager;
    private final List<InitWithDocument> initializations;
    private final List<DisposeWithDocument> finalizations;
    private final Dialog dialog;

    private final Api api;
    

    /*
     * DocumentWindowController
     */
    // @formatter:off
    public DocumentController(
            @Autowired Api api, 
            @Autowired RecentItemsPreference recentItemsPreference,
            @Autowired WildcardImportsPreference wildcardImportsPreference,
            @Autowired PreviewWindowController previewWindowController,
            @Autowired DocumentPreferencesController documentPreferencesController,
            @Autowired ContentPanelController contentPanelController,
            @Autowired Editor editorController,
            @Autowired DocumentWindow documentWindow,
            @Autowired MenuBarController menuBarController,
            @Autowired DocumentPanelController documentPanelController,
            @Autowired InspectorPanelController inspectorPanelController,
            @Autowired LibraryPanel libraryPanelController,
            @Autowired @Lazy CssPanelController cssPanelController,
            @Autowired SelectionBarController selectionBarController,
            @Autowired MessageBarController messageBarController,
            @Autowired DockManager dockManager,
            @Lazy @Autowired ThemePreference themePreference,
            @Lazy @Autowired PathPreference pathPreference,
            @Lazy @Autowired LastDockUuidPreference lastDockUuidPreference,
            @Lazy @Autowired(required = false) List<InitWithDocument> initializations,
            @Lazy @Autowired(required = false) List<DisposeWithDocument> finalizations,
            @Autowired List<Class<? extends View>> classViews

    ) {
     // @formatter:on
        super();
        this.api = api;
        this.editorController = editorController;
        this.documentWindow = documentWindow;
        this.recentItemsPreference = recentItemsPreference;
        this.wildcardImportsPreference = wildcardImportsPreference;
        this.menuBarController = menuBarController;
        this.fileSystem = api.getFileSystem();
        this.dialog = api.getApiDoc().getDialog();
        this.contentPanelController = contentPanelController;
        this.documentManager = api.getApiDoc().getDocumentManager();
        this.documentPreferencesController = documentPreferencesController;
        this.inspectorPanelController = inspectorPanelController;
        this.libraryPanelController = libraryPanelController;
        this.cssPanelController = cssPanelController;
        this.selectionBarController = selectionBarController;
        this.messageBarController = messageBarController;
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
            final KeyCombination accelerator = getAccelerator(event);
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
            if (documentPanelController.getHierarchyPanelController().getPanelControl().isFocused() && isSelectAll) {
                // Consume the event so the control action is not performed natively.
                event.consume();
                // When using system menu bar, the control action is performed by the app.
                if (!menuBarController.getMenuBar().isUseSystemMenuBar()) {
                    if (canPerformControlAction(DocumentControlAction.SELECT_ALL)) {
                        performControlAction(DocumentControlAction.SELECT_ALL);
                    }
                }
            }

            // MenuItems define a single accelerator.
            // BACK_SPACE key must be handled same way as DELETE key.
            boolean isBackspace = KeyCode.BACK_SPACE.equals(event.getCode());
            if (!isTextInputControlEditing(focusOwner) && isBackspace) {
                if (canPerformEditAction(DocumentEditAction.DELETE)) {
                    performEditAction(DocumentEditAction.DELETE);
                }
                event.consume();
            }
        };
    }

    private Api getApi() {
        return api;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initializations.forEach(a -> a.init());
        editorController.initialize();

        api.getApiDoc().getDocumentManager().closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> {
            onCloseRequest();
            closeWindow();
        });

        api.getSceneBuilderManager().closed().subscribeOn(JavaFxScheduler.platform()).subscribe(c -> {
            onCloseRequest();
            closeWindow();
        });

        Platform.runLater(() -> {
            initializeDocumentWindow();
        });
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
            System.out.println("UNTRACKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
            documentPreferencesController.readFromJavaPreferences();
            
            Platform.runLater(() -> {
                System.out.println("222SETTINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                documentWindow.apply();
                documentWindow.track();
                System.out.println("222TRACK kkkkkkkkkkkkkkkkkkkkkkk");
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
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();

        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
        final URL fxmlURL = fxomDocument.getLocation();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, true);
        updateLoadFileTime();
        // Here we do not invoke updateStageTitleAndPreferences() neither
        // watchingController.update()
    }

    public String getFxmlText() {
        return editorController.getFxmlText(wildcardImportsPreference.getValue());
    }

    @Override
    public boolean canPerformControlAction(DocumentControlAction controlAction) {

        final boolean result;

        switch (controlAction) {
        case COPY:
            result = canPerformCopy();
            break;

        case SELECT_ALL:
            result = canPerformSelectAll();
            break;

        case SELECT_NONE:
            result = canPerformSelectNone();
            break;

        case TOGGLE_LIBRARY_PANEL:
        case TOGGLE_DOCUMENT_PANEL:
        case TOGGLE_CSS_PANEL:
        case TOGGLE_LEFT_PANEL:
        case TOGGLE_RIGHT_PANEL:
        case TOGGLE_OUTLINES_VISIBILITY:
        case TOGGLE_GUIDES_VISIBILITY:
            result = true;
            break;
//            case SHOW_PREVIEW_WINDOW:
//                result = true;
//                break;
//
//            case SHOW_PREVIEW_DIALOG:
//                final FXOMDocument fxomDocument = editorController.getFxomDocument();
//                if (fxomDocument != null) {
//                    Object sceneGraphRoot = fxomDocument.getSceneGraphRoot();
//                    return sceneGraphRoot instanceof DialogPane;
//                }
//                result = false;
//                break;

//            case SAVE_FILE:
//                result = isDocumentDirty() || fxomDocument.getLocation() == null; // Save new empty document
//                break;
//
//            case SAVE_AS_FILE:
        case CLOSE_FILE:
            result = true;
            break;

//            case REVERT_FILE:
//                result = isDocumentDirty()
//                        && fxomDocument.getLocation() != null;
//                break;
//
//            case REVEAL_FILE:
//                result = (fxomDocument != null) && (fxomDocument.getLocation() != null);
//                break;

        case GOTO_CONTENT:
        case GOTO_PROPERTIES:
        case GOTO_LAYOUT:
        case GOTO_CODE:
            result = true;
            break;

        case ADD_SCENE_STYLE_SHEET:
            result = true;
            break;

        case SET_RESOURCE:
            result = true;
            break;
//
//            case REMOVE_RESOURCE:
//            case REVEAL_RESOURCE:
//                result = resourceController.getResourceFile() != null;
//                break;

        case HELP:
            result = true;
            break;

        default:
            result = false;
            assert false;
            break;
        }

        return result;
    }

    @Override
    public void performControlAction(DocumentControlAction controlAction) {
        assert canPerformControlAction(controlAction);

        switch (controlAction) {
        case COPY:
            performCopy();
            break;

        case SELECT_ALL:
            performSelectAll();
            break;

        case SELECT_NONE:
            performSelectNone();
            break;

//            case SHOW_PREVIEW_WINDOW:
//                if (previewWindowController == null) {
//                    previewWindowController = new PreviewWindowController(sceneBuilderManager, editorController, documentManager, documentWindow.getStage());
//                    //previewWindowController.setToolStylesheet(getToolStylesheet());
//                }
//                previewWindowController.documentWindow.getStage().centerOnScreen();
//                previewWindowController.openWindow();
//                break;
//
//            case SHOW_PREVIEW_DIALOG:
//                if (previewWindowController == null) {
//                    previewWindowController = new PreviewWindowController(sceneBuilderManager, editorController, documentManager, documentWindow.getStage());
//                    //previewWindowController.setToolStylesheet(getToolStylesheet());
//                }
//                previewWindowController.openDialog();
//                break;

//            case SAVE_FILE:
//                save();
//                break;
//
//            case SAVE_AS_FILE:
//                saveAs();
//                break;
//
//            case REVERT_FILE:
//                revert();
//                break;

        case CLOSE_FILE:
            performCloseAction();
            break;

        case GOTO_CONTENT:
            contentPanelController.getGlassLayer().requestFocus();
            break;

        case GOTO_PROPERTIES:
            performGoToSection(SectionId.PROPERTIES);
            break;

        case GOTO_LAYOUT:
            performGoToSection(SectionId.LAYOUT);
            break;

        case GOTO_CODE:
            performGoToSection(SectionId.CODE);
            break;

//            case TOGGLE_LEFT_PANEL:
//                if (leftSplitController.isTargetVisible()) {
//                    assert librarySplitController.isTargetVisible()
//                            || documentSplitController.isTargetVisible();
//                    // Hide Left => hide both Library + Document
//                    librarySplitController.hideTarget();
//                    documentSplitController.hideTarget();
//                    leftSplitController.hideTarget();
//                } else {
//                    assert !librarySplitController.isTargetVisible()
//                            && !documentSplitController.isTargetVisible();
//                    // Show Left => show both Library + Document
//                    librarySplitController.showTarget();
//                    documentSplitController.showTarget();
//                    leftSplitController.showTarget();
//
//                    // This workarounds layout issues when showing Left
//                    libraryDocumentSplitPane.layout();
//                    libraryDocumentSplitPane.setDividerPositions(0.5);
//                }
//                // Update preferences
//                libraryVisiblePreference.setValue(librarySplitController.isTargetVisible());
//                documentVisiblePreference.setValue(documentSplitController.isTargetVisible());
//                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
//                break;
//
//            case TOGGLE_RIGHT_PANEL:
//                rightSplitController.toggleTarget();
//                // Update preferences
//                rightVisiblePreference.setValue(rightSplitController.isTargetVisible());
//                break;
//
//            case TOGGLE_CSS_PANEL:
//                // CSS panel is built lazely : initialize the CSS panel first
//                initializeCssPanel();
//                bottomSplitController.toggleTarget();
//                if (bottomSplitController.isTargetVisible()) {
//                    // CSS panel is built lazely
//                    // Need to update its table column ordering with preference value
//                    //refreshCssTableColumnsOrderingReversed(preferences.isCssTableColumnsOrderingReversed());
//                    // Enable pick mode
//                    editorController.setPickModeEnabled(true);
//                } else {
//                    // Disable pick mode
//                    editorController.setPickModeEnabled(false);
//                }
//                // Update preferences
//                bottomVisiblePreference.setValue(bottomSplitController.isTargetVisible());
//                break;
//
//            case TOGGLE_LIBRARY_PANEL:
//                if (librarySplitController.isTargetVisible()) {
//                    assert leftSplitController.isTargetVisible();
//                    librarySplitController.hideTarget();
//                    if (!documentSplitController.isTargetVisible()) {
//                        leftSplitController.hideTarget();
//                    }
//                } else {
//                    if (!leftSplitController.isTargetVisible()) {
//                        leftSplitController.showTarget();
//                    }
//                    librarySplitController.showTarget();
//                }
//                // Update preferences
//                libraryVisiblePreference.setValue(librarySplitController.isTargetVisible());
//                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
//                break;
//
//            case TOGGLE_DOCUMENT_PANEL:
//                if (documentSplitController.isTargetVisible()) {
//                    assert leftSplitController.isTargetVisible();
//                    documentSplitController.hideTarget();
//                    if (!librarySplitController.isTargetVisible()) {
//                        leftSplitController.hideTarget();
//                    }
//                } else {
//                    if (!leftSplitController.isTargetVisible()) {
//                        leftSplitController.showTarget();
//                    }
//                    documentSplitController.showTarget();
//                }
//                // Update preferences
//                documentVisiblePreference.setValue(documentSplitController.isTargetVisible());
//                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
//                break;

        case TOGGLE_OUTLINES_VISIBILITY:
            contentPanelController.setOutlinesVisible(!contentPanelController.isOutlinesVisible());
            break;

        case TOGGLE_GUIDES_VISIBILITY:
            contentPanelController.setGuidesVisible(!contentPanelController.isGuidesVisible());
            break;

//            case ADD_SCENE_STYLE_SHEET:
//                sceneStyleSheetMenuController.performAddSceneStyleSheet();
//                break;

//            case SET_RESOURCE:
//                resourceController.performSetResource();
//                // Update preferences
//                i18NResourcePreference.setValue(getResourceFile().getAbsolutePath());
//                break;
//
//            case REMOVE_RESOURCE:
//                resourceController.performRemoveResource();
//                // Update preferences
//                i18NResourcePreference.setValue(getResourceFile().getAbsolutePath());
//                break;
//
//            case REVEAL_RESOURCE:
//                resourceController.performRevealResource();
//                break;

        case HELP:
            performHelp();
            break;

        default:
            assert false;
            break;
        }
    }

    @Override
    public boolean canPerformEditAction(DocumentEditAction editAction) {

        final boolean result;

        switch (editAction) {
        case DELETE:
            result = canPerformDelete();
            break;

        case CUT:
            result = canPerformCut();
            break;

//            case IMPORT_FXML:
//            case IMPORT_MEDIA:
//                result = true;
//                break;
//
//            case INCLUDE_FXML:
//                // Cannot include as root or if the document is not saved yet
//                result = (fxomDocument != null)
//                        && (fxomDocument.getFxomRoot() != null)
//                        && (fxomDocument.getLocation() != null);
//                break;

        case PASTE:
            result = canPerformPaste();
            break;

        default:
            result = false;
            assert false;
            break;
        }

        return result;
    }

    @Override
    public void performEditAction(DocumentEditAction editAction) {
        assert canPerformEditAction(editAction);

        switch (editAction) {
        case DELETE:
            performDelete();
            break;

        case CUT:
            performCut();
            break;

//            case IMPORT_FXML:
//                performImportFxml();
//                break;
//
//            case IMPORT_MEDIA:
//                performImportMedia();
//                break;
//
//            case INCLUDE_FXML:
//                performIncludeFxml();
//                break;

        case PASTE:
            performPaste();
            break;

        default:
            assert false;
            break;
        }
    }

    @Override
    public boolean isUnused() {
        /*
         * A document window controller is considered as "unused" if: //NOI18N 1) it has
         * not fxml text 2) it is not dirty 3) it is unamed
         */
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
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
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        final boolean noFxmlText = (fxomDocument == null) || (fxomDocument.getFxomRoot() == null);
        return noFxmlText;
    }

    @Override
    public boolean hasName() {
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        final boolean hasName = (fxomDocument != null) && (fxomDocument.getLocation() != null);
        return hasName;
    }

    @Override
    public String getName() {
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
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

        documentWindow.getStage().focusedProperty().addListener((ob, o, n) -> {
            if (n) {
                this.onFocus();
            }
        });

        documentWindow.setCloseHandler(this::onCloseRequest);
        documentWindow.setFocusHandler(this::onFocus);

        documentWindow.setMainKeyPressedEvent(mainKeyEventFilter);
        documentWindow.setMenuBar(menuBarController.getMenuBar());
        documentWindow.setContentPane(contentPanelController.getRoot());
        documentWindow.setMessageBar(messageBarController.getRoot());

        messageBarController.getSelectionBarHost().getChildren().add(selectionBarController.getRoot());
        
        assert libraryPanelController != null;
        libraryPanelController.getSearchController().requestFocus();

        editorController.setOwnerWindow(documentWindow.getStage());

        documentWindow.openWindow();
    }

    @Override
    public void onCloseRequest() {
        onFocus();
        if (performCloseAction() == ActionStatus.DONE) {
            // Write java preferences at close time but before losing the current document
            // scope
            
            updatePreferences();

            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // finalizations list must handle the case below
            //// Stops watching
            // editorController.stopFileWatching();
            // watchingController.stop();

            finalizations.forEach(a -> a.dispose());


            // Closes if confirmed
            MainController.getSingleton().documentWindowRequestClose(this);
            
            DocumentScope.removeScope(this);
        }
    }

    @Override
    public void onFocus() {
        DocumentScope.setCurrentScope(this);
    }

    /*
     * Private
     */

    private boolean canPerformSelectAll() {
        final boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            final String text = tic.getText();
            final String selectedText = tic.getSelectedText();
            if (text == null || text.isEmpty()) {
                result = false;
            } else {
                // Check if the TextInputControl is not already ALL selected
                result = selectedText == null || selectedText.length() < tic.getText().length();
            }
        } else {
            result = editorController.canPerformControlAction(ControlAction.SELECT_ALL);
        }
        return result;
    }

    private void performSelectAll() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.selectAll();
        } else {
            editorController.performControlAction(ControlAction.SELECT_ALL);
        }
    }

    private boolean canPerformSelectNone() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else {
            result = editorController.canPerformControlAction(ControlAction.SELECT_NONE);
        }
        return result;
    }

    private void performSelectNone() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.deselect();
        } else {
            this.editorController.performControlAction(ControlAction.SELECT_NONE);
        }
    }

    private boolean canPerformCopy() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else if (isCssRulesEditing(focusOwner) || isCssTextEditing(focusOwner)) {
            result = true;
        } else {
            result = editorController.canPerformControlAction(ControlAction.COPY);
        }
        return result;
    }

    private void performCopy() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.copy();
        } else if (isCssRulesEditing(focusOwner)) {
            //cssPanelController.copyRules();
        } else if (isCssTextEditing(focusOwner)) {
            // CSS text pane is a WebView
            // Let the WebView handle the copy action natively
        } else {
            this.editorController.performControlAction(ControlAction.COPY);
        }
    }

    private boolean canPerformCut() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else {
            result = editorController.canPerformEditAction(EditAction.CUT);
        }
        return result;
    }

    private void performCut() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.cut();
        } else {
            this.editorController.performEditAction(EditAction.CUT);
        }
    }

    private boolean canPerformPaste() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (editorController.canPerformEditAction(EditAction.PASTE)) {
            result = true;
        } else if (isTextInputControlEditing(focusOwner)) {
            result = Clipboard.getSystemClipboard().hasString();
        } else {
            result = false;
        }
        return result;
    }

    private void performPaste() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (editorController.canPerformEditAction(EditAction.PASTE)) {
            this.editorController.performEditAction(EditAction.PASTE);
            // Give focus to content panel
            contentPanelController.getGlassLayer().requestFocus();
        } else {
            assert isTextInputControlEditing(focusOwner);
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.paste();
        }
    }

    private boolean canPerformDelete() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getCaretPosition() < tic.getLength();
        } else {
            result = editorController.canPerformEditAction(EditAction.DELETE);
        }
        return result;
    }

    private void performDelete() {

        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.deleteNextChar();
        } else {
            final List<FXOMObject> selectedObjects = editorController.getSelectedObjects();

            // Collects fx:ids in selected objects and their descendants.
            // We filter out toggle groups because their fx:ids are managed automatically.
            final Map<String, FXOMObject> fxIdMap = new HashMap<>();
            for (FXOMObject selectedObject : selectedObjects) {
                fxIdMap.putAll(selectedObject.collectFxIds());
            }
            FXOMNodes.removeToggleGroups(fxIdMap);

            // Checks if deleted objects have some fx:ids and ask for confirmation.
            final boolean deleteConfirmed;
            if (fxIdMap.isEmpty()) {
                deleteConfirmed = true;
            } else {
                final String message;

                if (fxIdMap.size() == 1) {
                    if (selectedObjects.size() == 1) {
                        message = I18N.getString("alert.delete.fxid1of1.message");
                    } else {
                        message = I18N.getString("alert.delete.fxid1ofN.message");
                    }
                } else {
                    if (selectedObjects.size() == fxIdMap.size()) {
                        message = I18N.getString("alert.delete.fxidNofN.message");
                    } else {
                        message = I18N.getString("alert.delete.fxidKofN.message");
                    }
                }

                final Alert d = dialog.customAlert(documentWindow.getStage());
                d.setMessage(message);
                d.setDetails(I18N.getString("alert.delete.fxid.details"));
                d.setOKButtonTitle(I18N.getString("label.delete"));

                deleteConfirmed = (d.showAndWait() == AbstractModalDialog.ButtonID.OK);
            }

            if (deleteConfirmed) {
                editorController.performEditAction(EditAction.DELETE);
            }
        }
    }

    @Override
    public void performImportFxml() {
        fetchFXMLFile().ifPresent(fxmlFile -> editorController.performImportFxml(fxmlFile));
    }

    @Override
    public void performIncludeFxml() {
        fetchFXMLFile().ifPresent(fxmlFile -> editorController.performIncludeFxml(fxmlFile));
    }

    private Optional<File> fetchFXMLFile() {
        var fileChooser = new FileChooser();
        var f = new ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml"); // NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        var fxmlFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(".fxml")) { // NOI18N
                fxmlFile = new File(path + ".fxml"); // NOI18N
            }

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(fxmlFile);
        }
        return Optional.ofNullable(fxmlFile);
    }

    @Override
    public void performImportMedia() {

        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter imageFilter = new ExtensionFilter(I18N.getString("file.filter.label.image"),
                ResourceUtils.getSupportedImageExtensions());
        final ExtensionFilter audioFilter = new ExtensionFilter(I18N.getString("file.filter.label.audio"),
                ResourceUtils.getSupportedAudioExtensions());
        final ExtensionFilter videoFilter = new ExtensionFilter(I18N.getString("file.filter.label.video"),
                ResourceUtils.getSupportedVideoExtensions());
        final ExtensionFilter mediaFilter = new ExtensionFilter(I18N.getString("file.filter.label.media"),
                ResourceUtils.getSupportedMediaExtensions());

        fileChooser.getExtensionFilters().add(mediaFilter);
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.getExtensionFilters().add(audioFilter);
        fileChooser.getExtensionFilters().add(videoFilter);

        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        File mediaFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (mediaFile != null) {

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(mediaFile);

            this.editorController.performImportMedia(mediaFile);
        }
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

    /**
     * Returns true if we are editing within a popup window : either the specified
     * node is showing a popup window or the inline editing popup is showing.
     *
     * @param node the focused node of the main scene
     * @return
     */
    private boolean isPopupEditing(Node node) {
        return (node instanceof MenuButton && ((MenuButton) node).isShowing())
                || editorController.getInlineEditController().isWindowOpened();
    }

    private boolean isCssRulesEditing(Node node) {
        final Node cssRules = cssPanelController.getRulesPane();
        if (cssRules != null) {
            return isDescendantOf(cssRules, node);
        }
        return false;
    }

    private boolean isCssTextEditing(Node node) {
        final Node cssText = cssPanelController.getTextPane();
        if (cssText != null) {
            return isDescendantOf(cssText, node);
        }
        return false;
    }

    private boolean isDescendantOf(Node container, Node node) {
        Node child = node;
        while (child != null) {
            if (child == container) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }

    private KeyCombination getAccelerator(final KeyEvent event) {
        KeyCombination result = null;
        for (KeyCombination kc : menuBarController.getAccelerators()) {
            if (kc.match(event)) {
                result = kc;
                break;
            }
        }
        return result;
    }

    @Override
    public ActionStatus save() {
        final ActionStatus result;
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        if (fxomDocument.getLocation() == null) {
            result = saveAs();
        } else {
            result = performSaveAction();
        }

        if (result.equals(ActionStatus.DONE)) {
            documentManager.dirty().set(false);
            documentManager.saved().set(true);
        }

        return result;
    }

    private void performGoToSection(SectionId sectionId) {
        // First make the right panel visible if not already the case
        // TODO uncomment and handle with the new view framework when ready
//        if (!isRightPanelVisible()) {
//            performControlAction(DocumentControlAction.TOGGLE_RIGHT_PANEL);
//        }
        inspectorPanelController.setExpandedSection(sectionId);
    }

    private ActionStatus performSaveAction() {
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        ActionStatus result;
        if (editorController.canGetFxmlText()) {
            final Path fxmlPath;
            try {
                fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
            } catch (URISyntaxException x) {
                // Should not happen
                throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
            }
            final String fileName = fxmlPath.getFileName().toString();

            try {
                final boolean saveConfirmed;
                if (checkLoadFileTime()) {
                    saveConfirmed = true;
                } else {
                    final Alert d = dialog.customAlert(documentWindow.getStage());
                    d.setMessage(I18N.getString("alert.overwrite.message", fileName));
                    d.setDetails(I18N.getString("alert.overwrite.details"));
                    d.setOKButtonVisible(true);
                    d.setOKButtonTitle(I18N.getString("label.overwrite"));
                    d.setDefaultButtonID(ButtonID.CANCEL);
                    d.setShowDefaultButton(true);
                    saveConfirmed = (d.showAndWait() == ButtonID.OK);
                }

                if (saveConfirmed) {
                    try {
                        // TODO remove after checking the new watching system is operational in
                        // EditorController or in filesystem
                        // watchingController.removeDocumentTarget();
                        final byte[] fxmlBytes = editorController.getFxmlText(wildcardImportsPreference.getValue())
                                .getBytes(StandardCharsets.UTF_8); // NOI18N
                        Files.write(fxmlPath, fxmlBytes);
                        updateLoadFileTime();
                        // TODO remove after checking the new watching system is operational in
                        // EditorController or in filesystem
                        // watchingController.update();

                        editorController.getMessageLog().logInfoMessage("log.info.save.confirmation", I18N.getBundle(),
                                fileName);
                        result = ActionStatus.DONE;
                    } catch (UnsupportedEncodingException x) {
                        // Should not happen
                        throw new RuntimeException("Bug", x); // NOI18N
                    }
                } else {
                    result = ActionStatus.CANCELLED;
                }
            } catch (IOException x) {
                dialog.showErrorAndWait(documentWindow.getStage(), null,
                        I18N.getString("alert.save.failure.message", fileName),
                        I18N.getString("alert.save.failure.details"), x);
                result = ActionStatus.CANCELLED;
            }
        } else {
            result = ActionStatus.CANCELLED;
        }

        return result;
    }

    @Override
    public ActionStatus saveAs() {

        final ActionStatus result;
        if (editorController.canGetFxmlText()) {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter f = new FileChooser.ExtensionFilter(
                    I18N.getString("file.filter.label.fxml"), "*.fxml"); // NOI18N
            fileChooser.getExtensionFilters().add(f);
            fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

            File fxmlFile = fileChooser.showSaveDialog(documentWindow.getStage());
            if (fxmlFile == null) {
                result = ActionStatus.CANCELLED;
            } else {
                boolean forgetSave = false;
                // It is only on Linux where you can get the case the path doesn't
                // end with the extension, thanks the behavior of the FX 8 FileChooser
                // on this specific OS (see RT-31956).
                // Below we ask the user if the extension shall be added or not.
                // See DTL-5948.
                final String path = fxmlFile.getPath();
                if (!path.endsWith(".fxml")) { // NOI18N
                    try {
                        URL alternateURL = new URL(fxmlFile.toURI().toURL().toExternalForm() + ".fxml"); // NOI18N
                        File alternateFxmlFile = new File(alternateURL.toURI());
                        final Alert d = dialog.customAlert(documentWindow.getStage());
                        d.setMessage(I18N.getString("alert.save.noextension.message", fxmlFile.getName()));
                        String details = I18N.getString("alert.save.noextension.details");

                        if (alternateFxmlFile.exists()) {
                            details += "\n" // NOI18N
                                    + I18N.getString("alert.save.noextension.details.overwrite",
                                            alternateFxmlFile.getName());
                        }

                        d.setDetails(details);
                        d.setOKButtonVisible(true);
                        d.setOKButtonTitle(I18N.getString("alert.save.noextension.savewith"));
                        d.setDefaultButtonID(ButtonID.OK);
                        d.setShowDefaultButton(true);
                        d.setActionButtonDisable(false);
                        d.setActionButtonVisible(true);
                        d.setActionButtonTitle(I18N.getString("alert.save.noextension.savewithout"));

                        switch (d.showAndWait()) {
                        case ACTION:
                            // Nothing to do, we save with the no extension name
                            break;
                        case CANCEL:
                            forgetSave = true;
                            break;
                        case OK:
                            fxmlFile = alternateFxmlFile;
                            break;
                        }
                    } catch (MalformedURLException | URISyntaxException ex) {
                        forgetSave = true;
                    }
                }

                // Transform File into URL
                final URL newLocation;
                try {
                    newLocation = fxmlFile.toURI().toURL();
                } catch (MalformedURLException x) {
                    // Should not happen
                    throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
                }

                // Checks if fxmlFile is the name of an already opened document
                final Document dwc = MainController.getSingleton().lookupDocumentWindowControllers(newLocation);
                if (dwc != null && dwc != this) {
                    final Path fxmlPath = Paths.get(fxmlFile.toString());
                    final String fileName = fxmlPath.getFileName().toString();
                    dialog.showErrorAndWait(documentWindow.getStage(), null,
                            I18N.getString("alert.save.conflict.message", fileName),
                            I18N.getString("alert.save.conflict.details"));
                    result = ActionStatus.CANCELLED;
                } else if (forgetSave) {
                    result = ActionStatus.CANCELLED;
                } else {
                    // Recalculates references if needed
                    // TODO(elp)

                    // First change the location of the fxom document
                    editorController.setFxmlLocation(newLocation);
                    updateLoadFileTime();
                    documentWindow.updateStageTitle();

                    // TODO this case is not handled for using spring, need to take an extra look at
                    // this
                    // TODO this method do nothing for now
                    // TODO more generaly, what to do when using save as ? keep the same beans?
                    // something else
                    // We use same DocumentWindowController BUT we change its fxml :
                    // => reset document preferences
                    // resetDocumentPreferences();

                    // TODO remove after checking the new watching system is operational in
                    // EditorController or in filesystem
                    // watchingController.update();

                    // Now performs a regular save action
                    result = performSaveAction();

                    // Keep track of the user choice for next time
                    fileSystem.updateNextInitialDirectory(fxmlFile);

                    // Update recent items with just saved file
                    recentItemsPreference.addRecentItem(fxmlFile);
                }
            }
        } else {
            result = ActionStatus.CANCELLED;
        }

        return result;
    }

    @Override
    public void revert() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final Alert d = dialog.customAlert(documentWindow.getStage());
        d.setMessage(I18N.getString("alert.revert.question.message", documentWindow.getStage().getTitle()));
        d.setDetails(I18N.getString("alert.revert.question.details"));
        d.setOKButtonTitle(I18N.getString("label.revert"));

        if (d.showAndWait() == AlertDialog.ButtonID.OK) {
            try {
                reload();
            } catch (IOException x) {
                dialog.showErrorAndWait(I18N.getString("alert.title.open"),
                        I18N.getString("alert.open.failure1.message", documentWindow.getStage().getTitle()),
                        I18N.getString("alert.open.failure1.details"), x);
                MainController.getSingleton().documentWindowRequestClose(this);
            }
        }
    }

    @Override
    public ActionStatus performCloseAction() {

        // Makes sure that our window is front
        documentWindow.getStage().toFront();

        // Check if an editing session is on going
        if (editorController.isTextEditingSessionOnGoing()) {
            // Check if we can commit the editing session
            if (!editorController.canGetFxmlText()) {
                // Commit failed
                return ActionStatus.CANCELLED;
            }
        }

        // Checks if there are some pending changes
        final boolean closeConfirmed;
        if (isDocumentDirty()) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            final Alert d = dialog.customAlert(documentWindow.getStage());
            d.setMessage(I18N.getString("alert.save.question.message", documentWindow.getStage().getTitle()));
            d.setDetails(I18N.getString("alert.save.question.details"));
            d.setOKButtonTitle(I18N.getString("label.save"));
            d.setActionButtonTitle(I18N.getString("label.do.not.save"));
            d.setActionButtonVisible(true);

            switch (d.showAndWait()) {
            default:
            case OK:
                if (fxomDocument.getLocation() == null) {
                    closeConfirmed = (saveAs() == ActionStatus.DONE);
                } else {
                    closeConfirmed = (performSaveAction() == ActionStatus.DONE);
                }
                break;
            case CANCEL:
                closeConfirmed = false;
                break;
            case ACTION: // Do not save
                closeConfirmed = true;
                break;
            }

        } else {
            // No pending changes
            closeConfirmed = true;
        }

        return closeConfirmed ? ActionStatus.DONE : ActionStatus.CANCELLED;
    }

    @Override
    public void performRevealAction() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final URL location = fxomDocument.getLocation();

        try {
            fileSystem.revealInFileBrowser(new File(location.toURI()));
        } catch (IOException | URISyntaxException x) {
            dialog.showErrorAndWait("",
                    I18N.getString("alert.reveal.failure.message", documentWindow.getStage().getTitle()),
                    I18N.getString("alert.reveal.failure.details"), x);
        }
    }

    private void updateLoadFileTime() {

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

    private void performHelp() {
        try {
            fileSystem.open(EditorPlatform.DOCUMENTATION_URL);
        } catch (IOException ioe) {
            dialog.showErrorAndWait("", I18N.getString("alert.help.failure.message", EditorPlatform.DOCUMENTATION_URL),
                    I18N.getString("alert.messagebox.failure.details"), ioe);
        }
    }

    @Override
    public boolean isDocumentDirty() {
        return getApi().getApiDoc().getDocumentManager().dirty().get();
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

}

///**
// * This class setup key bindings for the TextInputControl type classes and
// * provide a way to access the key binding list.
// */
//class SBTextInputControlBindings extends TextInputControlBindings {
//
//    private SBTextInputControlBindings() {
//        assert false;
//    }
//
//    public static List<KeyBinding> getBindings() {
//        return BINDINGS;
//    }
//}
