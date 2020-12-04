/*
 * Copyright (c) 2016, 2019 Gluon and/or its affiliates.
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.ControlAction;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithDocument;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.DocumentScope;
import com.oracle.javafx.scenebuilder.app.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.app.message.MessageBarController;
import com.oracle.javafx.scenebuilder.app.preferences.DocumentPreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences;
import com.oracle.javafx.scenebuilder.app.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.BottomVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.DocumentVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftDividerVPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LeftVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.LibraryVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.PathPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.RightVisiblePreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.StageHeightPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.StageWidthPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.XPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.document.YPosPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.WildcardImportsPreference;
import com.oracle.javafx.scenebuilder.app.report.JarAnalysisReportController;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.ext.controller.I18nResourceMenuController;
import com.oracle.javafx.scenebuilder.ext.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.alert.WarnThemeAlert;
import com.oracle.javafx.scenebuilder.kit.ResourceUtils;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController.EditAction;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.css.CssPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController.SectionId;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.preferences.global.CssTableColumnsOrderingReversedPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ToolThemePreference;
import com.oracle.javafx.scenebuilder.kit.preview.PreviewWindowController;
import com.oracle.javafx.scenebuilder.kit.selectionbar.SelectionBarController;
import com.oracle.javafx.scenebuilder.kit.skeleton.SkeletonWindowController;
import com.oracle.javafx.scenebuilder.kit.util.Utils;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentWindowController extends AbstractFxmlWindowController implements Document, InitializingBean {

    public enum DocumentControlAction {
        COPY,
        SELECT_ALL,
        SELECT_NONE,
        SAVE_FILE,
        SAVE_AS_FILE,
        REVERT_FILE,
        CLOSE_FILE,
        REVEAL_FILE,
        GOTO_CONTENT,
        GOTO_PROPERTIES,
        GOTO_LAYOUT,
        GOTO_CODE,
        TOGGLE_LIBRARY_PANEL,
        TOGGLE_DOCUMENT_PANEL,
        TOGGLE_CSS_PANEL,
        TOGGLE_LEFT_PANEL,
        TOGGLE_RIGHT_PANEL,
        TOGGLE_OUTLINES_VISIBILITY,
        TOGGLE_GUIDES_VISIBILITY,
        SHOW_PREVIEW_WINDOW,
        SHOW_PREVIEW_DIALOG,
        ADD_SCENE_STYLE_SHEET,
        SET_RESOURCE,
        REMOVE_RESOURCE,
        REVEAL_RESOURCE,
        HELP,
        SHOW_SAMPLE_CONTROLLER
    }

    public enum DocumentEditAction {
        DELETE,
        CUT,
        PASTE,
        IMPORT_FXML,
        IMPORT_MEDIA,
        INCLUDE_FXML
    }

    public enum ActionStatus {
        CANCELLED,
        DONE
    }

    private EditorController editorController;
    private final MenuBarController menuBarController;
    private final ContentPanelController contentPanelController;

    //private final AbstractHierarchyPanelController hierarchyPanelController;
    //private final InfoPanelController infoPanelController;
    private final DocumentPanelController documentPanelController;

    private final InspectorPanelController inspectorPanelController;
    private final CssPanelController cssPanelController;

    private final LibraryPanelController libraryPanelController;

    private final SelectionBarController selectionBarController;
    private final MessageBarController messageBarController;
    //private SearchController librarySearchController;
    //private SearchController inspectorSearchController;
    //private SearchController cssPanelSearchController;
    private final SceneStyleSheetMenuController sceneStyleSheetMenuController;
    private final CssPanelMenuController cssPanelMenuController;
    private final I18nResourceMenuController resourceController;
    //private final DocumentWatchingController watchingController;

    private final GlobalPreferences preferences;
    private final WildcardImportsPreference wildcardImportsPreference;
    private final RecentItemsPreference recentItemsPreference;
    private final ToolThemePreference toolThemePreference;

    private final DocumentPreferencesController documentPreferencesController;

    //PREFERENCES
    private final XPosPreference xPos;
	private final YPosPreference yPos;
	private final StageHeightPreference stageHeight;
	private final StageWidthPreference stageWidth;
	private final LeftDividerHPosPreference leftDividerHPos;
	private final RightDividerHPosPreference rightDividerHPos;
	private final BottomDividerVPosPreference bottomDividerVPos;
	private final LeftDividerVPosPreference leftDividerVPos;

	private final I18NResourcePreference i18NResourcePreference;
	private final ThemePreference themePreference;


	private final FileSystem fileSystem;

    //private final DocumentsManager documentManager;

    // The controller below are created lazily because they need an owner
    // and computing them here would be too costly (impact on start-up time):
    // - PreviewWindowController
    // - SkeletonWindowController
    // - JarAnalysisReportController
    private PreviewWindowController previewWindowController = null;
    private SkeletonWindowController skeletonWindowController = null;
    private JarAnalysisReportController jarAnalysisReportController = null;

    //@FXML private StackPane libraryPanelHost;
    //@FXML private StackPane librarySearchPanelHost;
    //@FXML private StackPane hierarchyPanelHost;
    //@FXML private StackPane infoPanelHost;
    @FXML private StackPane contentPanelHost;
    //@FXML private StackPane inspectorPanelHost;
    //@FXML private StackPane inspectorSearchPanelHost;
    //@FXML private StackPane cssPanelHost;
    //@FXML private StackPane cssPanelSearchPanelHost;
    @FXML private StackPane messageBarHost;
    //@FXML private Accordion documentAccordion;
    @FXML private SplitPane mainSplitPane;
    @FXML private SplitPane leftRightSplitPane;
    @FXML private SplitPane libraryDocumentSplitPane;

    @FXML private VBox leftTopHost;
    @FXML private VBox leftBottomHost;
    @FXML private VBox rightHost;
    @FXML private VBox bottomHost;

    //@FXML private Label libraryLabel;

    //@FXML private MenuButton libraryMenuButton;
    //@FXML private MenuItem libraryImportSelection;
    //@FXML private RadioMenuItem libraryViewAsList;
    //@FXML private RadioMenuItem libraryViewAsSections;
    //@FXML private MenuItem libraryReveal;
    //@FXML private Menu customLibraryMenu;

    //@FXML private MenuItem cssPanelShowStyledOnlyMi;
    //@FXML private MenuItem cssPanelSplitDefaultsMi;

//    @FXML private RadioMenuItem showInfoMenuItem;
//    @FXML private RadioMenuItem showFxIdMenuItem;
//    @FXML private RadioMenuItem showNodeIdMenuItem;

    private SplitController bottomSplitController;
    private SplitController leftSplitController;
    private SplitController rightSplitController;
    private SplitController librarySplitController;
    private SplitController documentSplitController;

    private FileTime loadFileTime;
    private Job saveJob;
    private EventHandler<KeyEvent> mainKeyEventFilter;

	private final LeftVisiblePreference leftVisiblePreference;
	private final RightVisiblePreference rightVisiblePreference;
	private final BottomVisiblePreference bottomVisiblePreference;
	private final DocumentVisiblePreference documentVisiblePreference;
	private final LibraryVisiblePreference libraryVisiblePreference;
	private final PathPreference pathPreference;
	private final DocumentManager documentManager;
	private final List<InitWithDocument> initializations;
	private final List<DisposeWithDocument> finalizations;
	private final Dialog dialog;


    /*
     * DocumentWindowController
     */

	public DocumentWindowController(
			@Autowired ApplicationContext context,
			@Autowired MainController mainController,
			@Autowired GlobalPreferences preferences,
			@Autowired RecentItemsPreference recentItemsPreference,
			@Autowired ToolThemePreference toolThemePreference,
			@Autowired WildcardImportsPreference wildcardImportsPreference,
			@Autowired FileSystem fileSystem,
			@Autowired Dialog dialog,
			@Lazy @Autowired I18NResourcePreference i18NResourcePreference,
			@Lazy @Autowired PathPreference pathPreference,

			@Lazy @Autowired DocumentPreferencesController documentPreferencesController,

			@Lazy @Autowired EditorController editorController,
			@Lazy @Autowired DocumentManager documentManager,
			//@Autowired DocumentsManager documentManager,
			@Lazy @Autowired MenuBarController menuBarController,

			@Lazy @Autowired ContentPanelController contentPanelController,

			//@Lazy @Autowired HierarchyPanelController hierarchyPanelController,
			//@Lazy @Autowired InfoPanelController infoPanelController,
			@Lazy @Autowired DocumentPanelController documentPanelController,

			@Lazy @Autowired InspectorPanelController inspectorPanelController,
			@Lazy @Autowired CssPanelController cssPanelController,
			@Lazy @Autowired LibraryPanelController libraryPanelController,
			@Lazy @Autowired SelectionBarController selectionBarController,
			@Lazy @Autowired MessageBarController messageBarController,
			@Lazy @Autowired SceneStyleSheetMenuController sceneStyleSheetMenuController,
			@Lazy @Autowired CssPanelMenuController cssPanelMenuController,
			@Lazy @Autowired I18nResourceMenuController resourceController,
			//@Lazy @Autowired DocumentWatchingController watchingController,

			@Lazy @Autowired XPosPreference xPos,
			@Lazy @Autowired YPosPreference yPos,
			@Lazy @Autowired StageHeightPreference stageHeight,
			@Lazy @Autowired StageWidthPreference stageWidth,

			@Lazy @Autowired LeftDividerHPosPreference leftDividerHPos,
			@Lazy @Autowired RightDividerHPosPreference rightDividerHPos,
			@Lazy @Autowired BottomDividerVPosPreference bottomDividerVPos,
			@Lazy @Autowired LeftDividerVPosPreference leftDividerVPos,

			@Lazy @Autowired LeftVisiblePreference leftVisiblePreference,
			@Lazy @Autowired RightVisiblePreference rightVisiblePreference,
			@Lazy @Autowired BottomVisiblePreference bottomVisiblePreference,

			@Lazy @Autowired DocumentVisiblePreference documentVisiblePreference,
			@Lazy @Autowired LibraryVisiblePreference libraryVisiblePreference,
			@Lazy @Autowired ThemePreference themePreference,
			@Lazy @Autowired CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversedPreference,

			//@Lazy @Autowired PreviewWindowController previewWindowController,
			//@Lazy @Autowired SkeletonWindowController skeletonWindowController,
			@Lazy @Autowired JarAnalysisReportController jarAnalysisReportController,
			@Lazy @Autowired List<InitWithDocument> initializations,
			@Lazy @Autowired List<DisposeWithDocument> finalizations
			) {
        super(DocumentWindowController.class.getResource("DocumentWindow.fxml"), //NOI18N
                I18N.getBundle(), false); // sizeToScene = false because sizing is defined in preferences
        //DocumentScope.setCurrentScope(this);

        this.editorController = editorController;
        this.recentItemsPreference = recentItemsPreference;
        this.toolThemePreference = toolThemePreference;
        this.wildcardImportsPreference = wildcardImportsPreference;
        this.menuBarController = menuBarController;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.contentPanelController = contentPanelController;
        this.documentManager = documentManager;
        this.documentPreferencesController = documentPreferencesController;
        //this.hierarchyPanelController = hierarchyPanelController;
        //this.infoPanelController = infoPanelController;
        this.documentPanelController = documentPanelController;

        this.inspectorPanelController = inspectorPanelController;
        this.cssPanelController = cssPanelController;
        this.libraryPanelController = libraryPanelController;
        this.selectionBarController = selectionBarController;
        this.messageBarController = messageBarController;
        //this.librarySearchController = librarySearchController;
        //this.inspectorSearchController = inspectorSearchController;
        //this.cssPanelSearchController = cssPanelSearchController;
        this.sceneStyleSheetMenuController = sceneStyleSheetMenuController;
        this.cssPanelMenuController = cssPanelMenuController;
        this.resourceController = resourceController;
        //this.watchingController = watchingController;

        this.preferences = preferences;
        this.i18NResourcePreference = i18NResourcePreference;
        this.pathPreference = pathPreference;
        // preferences
        this.xPos = xPos;
        this.yPos = yPos;
        this.stageHeight = stageHeight;
        this.stageWidth = stageWidth;
        this.leftDividerHPos = leftDividerHPos;
        this.rightDividerHPos = rightDividerHPos;
        this.bottomDividerVPos = bottomDividerVPos;
        this.leftDividerVPos = leftDividerVPos;
        this.themePreference = themePreference;

        this.leftVisiblePreference=leftVisiblePreference;
        this.rightVisiblePreference=rightVisiblePreference;
        this.bottomVisiblePreference=bottomVisiblePreference;
        this.documentVisiblePreference=documentVisiblePreference;
        this.libraryVisiblePreference=libraryVisiblePreference;

        this.jarAnalysisReportController = jarAnalysisReportController;

        this.initializations = initializations;
        this.finalizations = finalizations;

        documentPreferencesController.readFromJavaPreferences();
//        pathPreference.readFromJavaPreferences();

        //PrefTests.doDocTest(docPref);


        //this.documentManager = documentManager;
        this.editorController.setLibrary(mainController.getUserLibrary());

        mainKeyEventFilter = event -> {
            //------------------------------------------------------------------
            // TEXT INPUT CONTROL
            //------------------------------------------------------------------
            // Common editing actions handled natively and defined as application accelerators
            //
            // The platform support is not mature/stable enough to rely on.
            // Indeed, the behavior may differ :
            // - when using system menu bar vs not using it
            // - when using accelerators vs using menu items
            // - depending on the focused control (TextField vs ComboBox)
            //
            // On SB side, we decide for now to consume events that may be handled natively
            // so ALL actions are defined in our ApplicationMenu class.
            //
            // This may be revisit when platform implementation will be more reliable.
            //
            final Node focusOwner = getScene().getFocusOwner();
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

            //------------------------------------------------------------------
            // Hierarchy TreeView + select all
            //------------------------------------------------------------------
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
	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@FXML
	public void initialize() {
		initializations.forEach(a -> a.init());

		editorController.initialize();

		bottomSplitController = new SplitController(mainSplitPane, SplitController.Target.LAST);
        leftSplitController = new SplitController(leftRightSplitPane, SplitController.Target.FIRST);
        rightSplitController = new SplitController(leftRightSplitPane, SplitController.Target.LAST);
        librarySplitController = new SplitController(libraryDocumentSplitPane, SplitController.Target.FIRST);
        documentSplitController = new SplitController(libraryDocumentSplitPane, SplitController.Target.LAST);

		setToolStylesheet(toolThemePreference.getValue().getStylesheetURL());
		toolThemePreference.getObservableValue().addListener((ob, o, n) -> setToolStylesheet(n.getStylesheetURL()));

		// initialize preference binding
		final Stage stage = getStage();
        assert stage != null;

        // Add stage x and y listeners
        if (xPos.isValid()) {stage.setX(xPos.getValue());}
        stage.xProperty().addListener((ob, o, n) -> xPos.setValue(n.doubleValue()));
        xPos.getObservableValue().addListener((ob, o, n) -> stage.setX(n));

        if (yPos.isValid()) {stage.setY(yPos.getValue());}
        stage.yProperty().addListener((ov, t, t1) -> yPos.setValue(t1.doubleValue()));
        yPos.getObservableValue().addListener((ob, o, n) -> stage.setY(n));

        // Add stage height and width listeners
        if (stageHeight.isValid()) {stage.setHeight(stageHeight.getValue());}
        stage.heightProperty().addListener((ov, t, t1) -> stageHeight.setValue(t1.doubleValue()));
        stageHeight.getObservableValue().addListener((ob, o, n) -> stage.setHeight(n));

        if (stageWidth.isValid()) {stage.setWidth(stageWidth.getValue());}
        stage.widthProperty().addListener((ov, t, t1) -> stageWidth.setValue(t1.doubleValue()));
        stageWidth.getObservableValue().addListener((ob, o, n) -> stage.setWidth(n));

        //split containers
        // Add dividers position listeners
        if (leftDividerHPos.isValid()) {leftSplitController.setPosition(leftDividerHPos.getValue());}
        leftSplitController.position().addListener((ov, t, t1) -> leftDividerHPos.setValue(t1.doubleValue()));
        leftDividerHPos.getObservableValue().addListener((ob, o, n) -> leftSplitController.setPosition(n));

        if (rightDividerHPos.isValid()) {rightSplitController.setPosition(rightDividerHPos.getValue());}
        rightSplitController.position().addListener((ov, t, t1) -> rightDividerHPos.setValue(t1.doubleValue()));
        rightDividerHPos.getObservableValue().addListener((ob, o, n) -> rightSplitController.setPosition(n));

        if (bottomDividerVPos.isValid()) { bottomSplitController.setPosition(bottomDividerVPos.getValue());}
        bottomSplitController.position().addListener((ov, t, t1) -> bottomDividerVPos.setValue(t1.doubleValue()));
        bottomDividerVPos.getObservableValue().addListener((ob, o, n) -> bottomSplitController.setPosition(n));

        if (leftDividerVPos.isValid()) { librarySplitController.setPosition(leftDividerVPos.getValue());}
        librarySplitController.position().addListener((ov, t, t1) -> leftDividerVPos.setValue(t1.doubleValue()));
        leftDividerVPos.getObservableValue().addListener((ob, o, n) -> librarySplitController.setPosition(n));

        // TODO only restoring values but update from user still missing
        leftSplitController.setTargetVisible(leftVisiblePreference.getValue());
        rightSplitController.setTargetVisible(rightVisiblePreference.getValue());
        librarySplitController.setTargetVisible(libraryVisiblePreference.getValue() && leftVisiblePreference.getValue());
        documentSplitController.setTargetVisible(documentVisiblePreference.getValue() && leftVisiblePreference.getValue());

        if (bottomVisiblePreference.getValue()) { initializeCssPanel(); }
        bottomSplitController.setTargetVisible(bottomVisiblePreference.getValue());

	}

//	@Autowired
//	public void setLibrarySearchController(SearchController librarySearchController) {
//		this.librarySearchController = librarySearchController;
//	}


//	@Autowired
//	public void setInspectorSearchController(SearchController inspectorSearchController) {
//		this.inspectorSearchController = inspectorSearchController;
//	}


//	@Autowired
//	public void setCssPanelSearchController(SearchController cssPanelSearchController) {
//		this.cssPanelSearchController = cssPanelSearchController;
//	}



	public EditorController getEditorController() {
        return editorController;
    }

    public MenuBarController getMenuBarController() {
        return menuBarController;
    }

    public ContentPanelController getContentPanelController() {
        return contentPanelController;
    }

    public InspectorPanelController getInspectorPanelController() {
        return inspectorPanelController;
    }

    public DocumentPanelController getDocumentPanelController() {
		return documentPanelController;
	}

	public CssPanelController getCssPanelController() {
        return cssPanelController;
    }

//    public AbstractHierarchyPanelController getHierarchyPanelController() {
//        return hierarchyPanelController;
//    }
//
//    public InfoPanelController getInfoPanelController() {
//        return infoPanelController;
//    }

    public PreviewWindowController getPreviewWindowController() {
        return previewWindowController;
    }

    public SceneStyleSheetMenuController getSceneStyleSheetMenuController() {
        return sceneStyleSheetMenuController;
    }

    public I18nResourceMenuController getResourceController() {
        return resourceController;
    }

//    public DocumentWatchingController getWatchingController() {
//        return watchingController;
//    }

    public SplitController getBottomSplitController() {
        return bottomSplitController;
    }

    public SplitController getLeftSplitController() {
        return leftSplitController;
    }

    public SplitController getRightSplitController() {
        return rightSplitController;
    }

    public SplitController getLibrarySplitController() {
        return librarySplitController;
    }

    public SplitController getDocumentSplitController() {
        return documentSplitController;
    }

    public void loadFromFile(File fxmlFile) throws IOException {
        final URL fxmlURL = fxmlFile.toURI().toURL();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, false);
        System.out.println("OPEN" + editorController);
        updateLoadFileTime();
        updateStageTitle(); // No-op if fxml has not been loaded yet
        documentPreferencesController.readFromJavaPreferences();

        //TODO remove after checking the new watching system is operational in EditorController or in filesystem
        //watchingController.update();

        WarnThemeAlert.showAlertIfRequired(themePreference, editorController.getFxomDocument(), getStage());
    }

    public void loadFromURL(URL fxmlURL, boolean refreshThemeFromDocumentPreferences) {
        assert fxmlURL != null;
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editorController.setFxmlTextAndLocation(fxmlText, null, false);
            updateLoadFileTime();
            updateStageTitle(); // No-op if fxml has not been loaded yet
            documentPreferencesController.readFromJavaPreferences();
          //TODO remove after checking the new watching system is operational in EditorController or in filesystem
            //watchingController.update();
        } catch(IOException x) {
            throw new IllegalStateException(x);
        }
    }

    public void updateWithDefaultContent() {
        try {
            editorController.setFxmlTextAndLocation("", null, true); //NOI18N
            updateLoadFileTime();
            updateStageTitle(); // No-op if fxml has not been loaded yet
          //TODO remove after checking the new watching system is operational in EditorController or in filesystem
            //watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }

    public void reload() throws IOException {
        final FXOMDocument fxomDocument = editorController.getFxomDocument();
        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
        final URL fxmlURL = fxomDocument.getLocation();
        final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
        editorController.setFxmlTextAndLocation(fxmlText, fxmlURL, true);
        updateLoadFileTime();
        // Here we do not invoke updateStageTitleAndPreferences() neither watchingController.update()
    }

    public String getFxmlText() {
        return editorController.getFxmlText(wildcardImportsPreference.getValue());
    }

    public boolean canPerformControlAction(DocumentControlAction controlAction) {
        final boolean result;

        switch(controlAction) {
            case COPY:
                result = canPerformCopy();
                break;

            case SELECT_ALL:
                result = canPerformSelectAll();
                break;

            case SELECT_NONE:
                result = canPerformSelectNone();
                break;

            case SHOW_SAMPLE_CONTROLLER:
                result = editorController.getFxomDocument() != null;
                break;

            case TOGGLE_LIBRARY_PANEL:
            case TOGGLE_DOCUMENT_PANEL:
            case TOGGLE_CSS_PANEL:
            case TOGGLE_LEFT_PANEL:
            case TOGGLE_RIGHT_PANEL:
            case TOGGLE_OUTLINES_VISIBILITY:
            case TOGGLE_GUIDES_VISIBILITY:
            case SHOW_PREVIEW_WINDOW:
                result = true;
                break;

            case SHOW_PREVIEW_DIALOG:
                final FXOMDocument fxomDocument = editorController.getFxomDocument();
                if (fxomDocument != null) {
                    Object sceneGraphRoot = fxomDocument.getSceneGraphRoot();
                    return sceneGraphRoot instanceof DialogPane;
                }
                result = false;
                break;

            case SAVE_FILE:
                result = isDocumentDirty()
                        || editorController.getFxomDocument().getLocation() == null; // Save new empty document
                break;

            case SAVE_AS_FILE:
            case CLOSE_FILE:
                result = true;
                break;

            case REVERT_FILE:
                result = isDocumentDirty()
                        && editorController.getFxomDocument().getLocation() != null;
                break;

            case REVEAL_FILE:
                result = (editorController.getFxomDocument() != null)
                        && (editorController.getFxomDocument().getLocation() != null);
                break;

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

    public void performControlAction(DocumentControlAction controlAction) {
        assert canPerformControlAction(controlAction);

        switch(controlAction) {
            case COPY:
                performCopy();
                break;

            case SELECT_ALL:
                performSelectAll();
                break;

            case SELECT_NONE:
                performSelectNone();
                break;

            case SHOW_PREVIEW_WINDOW:
                if (previewWindowController == null) {
                    previewWindowController = new PreviewWindowController(editorController, documentManager, getStage());
                    previewWindowController.setToolStylesheet(getToolStylesheet());
                }
                previewWindowController.getStage().centerOnScreen();
                previewWindowController.openWindow();
                break;

            case SHOW_PREVIEW_DIALOG:
                if (previewWindowController == null) {
                    previewWindowController = new PreviewWindowController(editorController, documentManager, getStage());
                    previewWindowController.setToolStylesheet(getToolStylesheet());
                }
                previewWindowController.openDialog();
                break;

            case SAVE_FILE:
                performSaveOrSaveAsAction();
                break;

            case SAVE_AS_FILE:
                performSaveAsAction();
                break;

            case REVERT_FILE:
                performRevertAction();
                break;

            case CLOSE_FILE:
                performCloseAction();
                break;

            case REVEAL_FILE:
                performRevealAction();
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

            case TOGGLE_LEFT_PANEL:
                if (leftSplitController.isTargetVisible()) {
                    assert librarySplitController.isTargetVisible()
                            || documentSplitController.isTargetVisible();
                    // Hide Left => hide both Library + Document
                    librarySplitController.hideTarget();
                    documentSplitController.hideTarget();
                    leftSplitController.hideTarget();
                } else {
                    assert !librarySplitController.isTargetVisible()
                            && !documentSplitController.isTargetVisible();
                    // Show Left => show both Library + Document
                    librarySplitController.showTarget();
                    documentSplitController.showTarget();
                    leftSplitController.showTarget();

                    // This workarounds layout issues when showing Left
                    libraryDocumentSplitPane.layout();
                    libraryDocumentSplitPane.setDividerPositions(0.5);
                }
                // Update preferences
                libraryVisiblePreference.setValue(librarySplitController.isTargetVisible());
                documentVisiblePreference.setValue(documentSplitController.isTargetVisible());
                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
                break;

            case TOGGLE_RIGHT_PANEL:
                rightSplitController.toggleTarget();
                // Update preferences
                rightVisiblePreference.setValue(rightSplitController.isTargetVisible());
                break;

            case TOGGLE_CSS_PANEL:
                // CSS panel is built lazely : initialize the CSS panel first
                initializeCssPanel();
                bottomSplitController.toggleTarget();
                if (bottomSplitController.isTargetVisible()) {
                    // CSS panel is built lazely
                    // Need to update its table column ordering with preference value
                    //refreshCssTableColumnsOrderingReversed(preferences.isCssTableColumnsOrderingReversed());
                    // Enable pick mode
                    editorController.setPickModeEnabled(true);
                } else {
                    // Disable pick mode
                    editorController.setPickModeEnabled(false);
                }
                // Update preferences
                bottomVisiblePreference.setValue(bottomSplitController.isTargetVisible());
                break;

            case TOGGLE_LIBRARY_PANEL:
                if (librarySplitController.isTargetVisible()) {
                    assert leftSplitController.isTargetVisible();
                    librarySplitController.hideTarget();
                    if (!documentSplitController.isTargetVisible()) {
                        leftSplitController.hideTarget();
                    }
                } else {
                    if (!leftSplitController.isTargetVisible()) {
                        leftSplitController.showTarget();
                    }
                    librarySplitController.showTarget();
                }
                // Update preferences
                libraryVisiblePreference.setValue(librarySplitController.isTargetVisible());
                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
                break;

            case TOGGLE_DOCUMENT_PANEL:
                if (documentSplitController.isTargetVisible()) {
                    assert leftSplitController.isTargetVisible();
                    documentSplitController.hideTarget();
                    if (!librarySplitController.isTargetVisible()) {
                        leftSplitController.hideTarget();
                    }
                } else {
                    if (!leftSplitController.isTargetVisible()) {
                        leftSplitController.showTarget();
                    }
                    documentSplitController.showTarget();
                }
                // Update preferences
                documentVisiblePreference.setValue(documentSplitController.isTargetVisible());
                leftVisiblePreference.setValue(leftSplitController.isTargetVisible());
                break;

            case TOGGLE_OUTLINES_VISIBILITY:
                contentPanelController.setOutlinesVisible(
                        ! contentPanelController.isOutlinesVisible());
                break;

            case TOGGLE_GUIDES_VISIBILITY:
                contentPanelController.setGuidesVisible(
                        ! contentPanelController.isGuidesVisible());
                break;

            case ADD_SCENE_STYLE_SHEET:
                sceneStyleSheetMenuController.performAddSceneStyleSheet();
                break;

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

            case SHOW_SAMPLE_CONTROLLER:
                if (skeletonWindowController == null) {
                    skeletonWindowController = new SkeletonWindowController(editorController,
                            Utils.makeTitle(editorController.getFxomDocument()), getStage());
                    skeletonWindowController.setToolStylesheet(getToolStylesheet());
                }
                skeletonWindowController.openWindow();
                break;

            default:
                assert false;
                break;
        }
    }

    public boolean canPerformEditAction(DocumentEditAction editAction) {
        final boolean result;

        switch(editAction) {
            case DELETE:
                result = canPerformDelete();
                break;

            case CUT:
                result = canPerformCut();
                break;

            case IMPORT_FXML:
            case IMPORT_MEDIA:
                result = true;
                break;

            case INCLUDE_FXML:
                // Cannot include as root or if the document is not saved yet
                final FXOMDocument fxomDocument = editorController.getFxomDocument();
                result = (fxomDocument != null)
                        && (fxomDocument.getFxomRoot() != null)
                        && (fxomDocument.getLocation() != null);
                break;

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

    public void performEditAction(DocumentEditAction editAction) {
        assert canPerformEditAction(editAction);

        switch(editAction) {
            case DELETE:
                performDelete();
                break;

            case CUT:
                performCut();
                break;

            case IMPORT_FXML:
                performImportFxml();
                break;

            case IMPORT_MEDIA:
                performImportMedia();
                break;

            case INCLUDE_FXML:
                performIncludeFxml();
                break;

            case PASTE:
                performPaste();
                break;

            default:
                assert false;
                break;
        }
    }

    public boolean isLeftPanelVisible() {
        return leftSplitController.isTargetVisible();
    }


    public boolean isRightPanelVisible() {
        return rightSplitController.isTargetVisible();
    }


    public boolean isBottomPanelVisible() {
        return bottomSplitController.isTargetVisible();
    }


    public boolean isHierarchyPanelVisible() {
        return documentSplitController.isTargetVisible();
    }


    public boolean isLibraryPanelVisible() {
        return librarySplitController.isTargetVisible();
    }

    @Override
	public boolean isDocumentDirty() {
        return getEditorController().getJobManager().getCurrentJob() != saveJob;
    }

    @Override
	public boolean isUnused() {
        /*
         * A document window controller is considered as "unused" if: //NOI18N
         *  1) it has not fxml text
         *  2) it is not dirty
         *  3) it is unamed
         */

        final FXOMDocument fxomDocument = editorController.getFxomDocument();
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
    	final FXOMDocument fxomDocument = editorController.getFxomDocument();
        final boolean noFxmlText = (fxomDocument == null) || (fxomDocument.getFxomRoot() == null);
        return noFxmlText;
    }
    @Override
	public boolean hasName() {
    	final FXOMDocument fxomDocument = editorController.getFxomDocument();
    	final boolean hasName = (fxomDocument != null) && (fxomDocument.getLocation() != null);
        return hasName;
    }
    @Override
	public String getName() {
    	final FXOMDocument fxomDocument = editorController.getFxomDocument();
    	final String name = hasName() ? fxomDocument.getLocation().toExternalForm() : "";
        return name;
    }

    public static class TitleComparator implements Comparator<DocumentWindowController> {

        @Override
        public int compare(DocumentWindowController d1, DocumentWindowController d2) {
            final int result;

            assert d1 != null;
            assert d2 != null;

            if (d1 == d2) {
                result = 0;
            } else {
                final String t1 = d1.getStage().getTitle();
                final String t2 = d2.getStage().getTitle();
                assert t1 != null;
                assert t2 != null;
                result = t1.compareTo(t2);
            }

            return result;
        }

    }

    public void initializeCssPanel() {
        assert bottomHost != null;
        if (!bottomHost.getChildren().contains(cssPanelController.getPanelRoot())) {
        	bottomHost.getChildren().add(cssPanelController.getPanelRoot());
        	VBox.setVgrow(cssPanelController.getPanelRoot(), Priority.ALWAYS);
        }
    }

    public void updatePreferences() {

        final URL fxmlLocation = getEditorController().getFxmlLocation();
        if (fxmlLocation == null) {
            // Document has not been saved => nothing to write
            // This is the case with initial empty document
            return;
        }
        try {
			pathPreference.setValue(new File(fxmlLocation.toURI()).getPath());
		} catch (URISyntaxException e) {
			//TODO log something here
			e.printStackTrace();
		}

        // recentItems may not contain the current document
        // if the Open Recent -> Clear menu has been invoked
        if (!recentItemsPreference.containsRecentItem(fxmlLocation)) {
        	recentItemsPreference.addRecentItem(fxmlLocation);
        }

        documentPreferencesController.writeToJavaPreferences();
    }

    /*
     * AbstractFxmlWindowController
     */

    @Override
    public void controllerDidLoadFxml() {

        assert leftTopHost != null;
        assert leftBottomHost != null;
        assert rightHost != null;
        assert bottomHost != null;

        assert contentPanelHost != null;
        assert messageBarHost != null;
        assert mainSplitPane != null;
//        assert mainSplitPane.getItems().size() == 2;
        assert leftRightSplitPane != null;
//        assert leftRightSplitPane.getItems().size() == 2;
        assert libraryDocumentSplitPane != null;


        // Add a border to the Windows app, because of the specific window decoration on Windows.
        if (EditorPlatform.IS_WINDOWS) {
            getRoot().getStyleClass().add("windows-document-decoration");//NOI18N
        }

        mainSplitPane.addEventFilter(KeyEvent.KEY_PRESSED, mainKeyEventFilter);

        // Insert the menu bar
        assert getRoot() instanceof VBox;
        final VBox rootVBox = (VBox) getRoot();
        rootVBox.getChildren().add(0, menuBarController.getMenuBar());

        leftTopHost.getChildren().add(libraryPanelController.getPanelRoot());
        leftBottomHost.getChildren().add(documentPanelController.getPanelRoot());
        rightHost.getChildren().add(inspectorPanelController.getPanelRoot());

        VBox.setVgrow(libraryPanelController.getPanelRoot(), Priority.ALWAYS);
        VBox.setVgrow(documentPanelController.getPanelRoot(), Priority.ALWAYS);
        VBox.setVgrow(inspectorPanelController.getPanelRoot(), Priority.ALWAYS);

        //bottomHost.getChildren().add(cssPanelController.getPanelRoot());

        contentPanelHost.getChildren().add(contentPanelController.getPanelRoot());
        //inspectorPanelHost.getChildren().add(inspectorPanelController.getPanelRoot());
        //inspectorSearchPanelHost.getChildren().add(inspectorSearchController.getPanelRoot());
        messageBarHost.getChildren().add(messageBarController.getPanelRoot());

        messageBarController.getSelectionBarHost().getChildren().add(
                selectionBarController.getPanelRoot());

        //inspectorSearchController.textProperty().addListener((ChangeListener<String>) (ov, oldStr, newStr) -> inspectorPanelController.setSearchPattern(newStr));

        messageBarHost.heightProperty().addListener((InvalidationListener) o -> {
            final double h = messageBarHost.getHeight();
            contentPanelHost.setPadding(new Insets(h, 0.0, 0.0, 0.0));
        });

        //documentAccordion.setExpandedPane(documentAccordion.getPanes().get(0));

        // Monitor the status of the document to set status icon accordingly in message bar
        getEditorController().getJobManager().revisionProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> {
        	messageBarController.setDocumentDirty(isDocumentDirty());
        	documentManager.dirty().onNext(isDocumentDirty());
        });


        libraryPanelController.getLibraryLabel().bind(Bindings.createStringBinding(() -> {

            return MainController.getSingleton().getUserLibrary().isExploring() ? I18N.getString("library.exploring") : I18N.getString("library");

        }, MainController.getSingleton().getUserLibrary().exploringProperty()));
    }

    @Override
    protected void controllerDidCreateStage() {
        updateStageTitle();
        editorController.setOwnerWindow(getStage());
    }

    @Override
    public void openWindow() {

        if (!getStage().isShowing()) {
            // Starts watching document:
            //      - editorController watches files referenced from the FXML text
            //      - watchingController watches the document file, i18n resources,
            //        preview stylesheets...


        	//TODO remove after checking the new watching system is operational in EditorController or in filesystem
            //assert !editorController.isFileWatchingStarted();
            //editorController.startFileWatching();
        	//watchingController.start();
        }

        super.openWindow();
        if (!EditorPlatform.IS_MAC) {
        	//TODO uncomment or better add a Maximized preference to the document
            //getStage().setMaximized(true);
        }
        // Give focus to the library search TextField
        assert libraryPanelController != null;
        libraryPanelController.getSearchController().requestFocus();
    }

    @Override
    public void closeWindow() {
    	// Write java preferences at close time but before losing the current document scope
    	onFocus();
        updatePreferences();

        super.closeWindow();

        //TODO remove after checking the new watching system is operational in EditorController or in filesystem
        // finalizations list must handle the case below
        //// Stops watching
        //editorController.stopFileWatching();
        //watchingController.stop();

        finalizations.forEach(a -> a.dispose());
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        performCloseAction();
        DocumentScope.removeScope(this);
    }

    @Override
    public void onFocus() {
        DocumentScope.setCurrentScope(this);
    }

    public boolean isFrontDocumentWindow() {
        return getStage().isFocused()
                || (previewWindowController != null && previewWindowController.getStage().isFocused())
                || (skeletonWindowController != null && skeletonWindowController.getStage().isFocused())
                || (jarAnalysisReportController != null && jarAnalysisReportController.getStage().isFocused());
    }

    public void performCloseFrontDocumentWindow() {
        if (getStage().isFocused()) {
            performCloseAction();
        } else if (previewWindowController != null
                && previewWindowController.getStage().isFocused()) {
            previewWindowController.closeWindow();
        } else if (skeletonWindowController != null
                && skeletonWindowController.getStage().isFocused()) {
            skeletonWindowController.closeWindow();
        } else if (jarAnalysisReportController != null
                && jarAnalysisReportController.getStage().isFocused()) {
            jarAnalysisReportController.closeWindow();
        }
    }


    @Override
    protected void toolStylesheetDidChange(String oldStylesheet) {
        super.toolStylesheetDidChange(oldStylesheet);
        editorController.setToolStylesheet(getToolStylesheet());
        // previewWindowController should not be affected by tool style sheet
        if (skeletonWindowController != null) {
            skeletonWindowController.setToolStylesheet(getToolStylesheet());
        }
        if (jarAnalysisReportController != null) {
            jarAnalysisReportController.setToolStylesheet(getToolStylesheet());
        }
    }

    /*
     * Private
     */

    private boolean canPerformSelectAll() {
        final boolean result;
        final Node focusOwner = this.getScene().getFocusOwner();
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
                result = selectedText == null
                        || selectedText.length() < tic.getText().length();
            }
        } else {
            result = getEditorController().canPerformControlAction(ControlAction.SELECT_ALL);
        }
        return result;
    }

    private void performSelectAll() {
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.selectAll();
        } else {
            this.getEditorController().performControlAction(ControlAction.SELECT_ALL);
        }
    }

    private boolean canPerformSelectNone() {
        boolean result;
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else {
            result = getEditorController().canPerformControlAction(ControlAction.SELECT_NONE);
        }
        return result;
    }

    private void performSelectNone() {
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.deselect();
        } else {
            this.getEditorController().performControlAction(ControlAction.SELECT_NONE);
        }
    }

    private boolean canPerformCopy() {
        boolean result;
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else if (isCssRulesEditing(focusOwner) || isCssTextEditing(focusOwner)) {
            result = true;
        } else {
            result = getEditorController().canPerformControlAction(ControlAction.COPY);
        }
        return result;
    }

    private void performCopy() {
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.copy();
        } else if (isCssRulesEditing(focusOwner)) {
            cssPanelController.copyRules();
        } else if (isCssTextEditing(focusOwner)) {
            // CSS text pane is a WebView
            // Let the WebView handle the copy action natively
        } else {
            this.getEditorController().performControlAction(ControlAction.COPY);
        }
    }

    private boolean canPerformCut() {
        boolean result;
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isPopupEditing(focusOwner)) {
            return false;
        } else if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else {
            result = getEditorController().canPerformEditAction(EditAction.CUT);
        }
        return result;
    }

    private void performCut() {
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            tic.cut();
        } else {
            this.getEditorController().performEditAction(EditAction.CUT);
        }
    }

    private boolean canPerformPaste() {
        boolean result;
        final Node focusOwner = this.getScene().getFocusOwner();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner is
        if (getEditorController().canPerformEditAction(EditAction.PASTE)) {
            result = true;
        } else if (isTextInputControlEditing(focusOwner)) {
            result = Clipboard.getSystemClipboard().hasString();
        } else {
            result = false;
        }
        return result;
    }

    private void performPaste() {
        final Node focusOwner = this.getScene().getFocusOwner();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner is
        if (getEditorController().canPerformEditAction(EditAction.PASTE)) {
            this.getEditorController().performEditAction(EditAction.PASTE);
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
        final Node focusOwner = this.getScene().getFocusOwner();
        if (isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = getTextInputControl(focusOwner);
            result = tic.getCaretPosition() < tic.getLength();
        } else {
            result = getEditorController().canPerformEditAction(EditAction.DELETE);
        }
        return result;
    }

    private void performDelete() {

        final Node focusOwner = this.getScene().getFocusOwner();
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

                final AlertDialog d = new AlertDialog(getStage());
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

    private void performImportFxml() {
        fetchFXMLFile().ifPresent(fxmlFile -> getEditorController().performImportFxml(fxmlFile));
    }

    private void performIncludeFxml() {
        fetchFXMLFile().ifPresent(fxmlFile -> getEditorController().performIncludeFxml(fxmlFile));
    }

    private Optional<File> fetchFXMLFile() {
        var fileChooser = new FileChooser();
        var f = new ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml"); //NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        var fxmlFile = fileChooser.showOpenDialog(getStage());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(".fxml")) { //NOI18N
                fxmlFile = new File(path + ".fxml"); //NOI18N
            }

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(fxmlFile);
        }
        return Optional.ofNullable(fxmlFile);
    }

    private void performImportMedia() {

        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter imageFilter
                = new ExtensionFilter(I18N.getString("file.filter.label.image"),
                        ResourceUtils.getSupportedImageExtensions());
        final ExtensionFilter audioFilter
                = new ExtensionFilter(I18N.getString("file.filter.label.audio"),
                        ResourceUtils.getSupportedAudioExtensions());
        final ExtensionFilter videoFilter
                = new ExtensionFilter(I18N.getString("file.filter.label.video"),
                        ResourceUtils.getSupportedVideoExtensions());
        final ExtensionFilter mediaFilter
                = new ExtensionFilter(I18N.getString("file.filter.label.media"),
                        ResourceUtils.getSupportedMediaExtensions());

        fileChooser.getExtensionFilters().add(mediaFilter);
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.getExtensionFilters().add(audioFilter);
        fileChooser.getExtensionFilters().add(videoFilter);

        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        File mediaFile = fileChooser.showOpenDialog(getStage());
        if (mediaFile != null) {

            // Keep track of the user choice for next time
        	fileSystem.updateNextInitialDirectory(mediaFile);

            this.getEditorController().performImportMedia(mediaFile);
        }
    }

    /**
     * Returns true if the specified node is part of the main scene and is
     * either a TextInputControl or a ComboBox.
     *
     * @param node the focused node of the main scene
     * @return
     */
    private boolean isTextInputControlEditing(Node node) {
        return (node instanceof TextInputControl
                || node instanceof ComboBox);
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
     * Returns true if we are editing within a popup window :
     * either the specified node is showing a popup window
     * or the inline editing popup is showing.
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

    private void updateStageTitle() {
        if (contentPanelHost != null) {
            getStage().setTitle(Utils.makeTitle(editorController.getFxomDocument()));
        } // else controllerDidLoadFxml() will invoke me again
    }

    ActionStatus performSaveOrSaveAsAction() {
        final ActionStatus result;

        if (editorController.getFxomDocument().getLocation() == null) {
            result = performSaveAsAction();
        } else {
            result = performSaveAction();
        }

        if (result.equals(ActionStatus.DONE)) {
            messageBarController.setDocumentDirty(false);
            saveJob = getEditorController().getJobManager().getCurrentJob();
        }

        return result;
    }

    private void performGoToSection(SectionId sectionId) {
        // First make the right panel visible if not already the case
        if (!isRightPanelVisible()) {
            performControlAction(DocumentControlAction.TOGGLE_RIGHT_PANEL);
        }
        inspectorPanelController.setExpandedSection(sectionId);
    }

    private ActionStatus performSaveAction() {
        final FXOMDocument fxomDocument = editorController.getFxomDocument();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        ActionStatus result;
        if (editorController.canGetFxmlText()) {
            final Path fxmlPath;
            try {
                fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
            } catch(URISyntaxException x) {
                // Should not happen
                throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
            }
            final String fileName = fxmlPath.getFileName().toString();

            try {
                final boolean saveConfirmed;
                if (checkLoadFileTime()) {
                    saveConfirmed = true;
                } else {
                    final AlertDialog d = new AlertDialog(getStage());
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
                    	//TODO remove after checking the new watching system is operational in EditorController or in filesystem
                        //watchingController.removeDocumentTarget();
                        final byte[] fxmlBytes = editorController.getFxmlText(wildcardImportsPreference.getValue()).getBytes(StandardCharsets.UTF_8); //NOI18N
                        Files.write(fxmlPath, fxmlBytes);
                        updateLoadFileTime();
                        //TODO remove after checking the new watching system is operational in EditorController or in filesystem
                        //watchingController.update();

                        editorController.getMessageLog().logInfoMessage(
                                "log.info.save.confirmation", I18N.getBundle(), fileName);
                        result = ActionStatus.DONE;
                    } catch(UnsupportedEncodingException x) {
                        // Should not happen
                        throw new RuntimeException("Bug", x); //NOI18N
                    }
                } else {
                    result = ActionStatus.CANCELLED;
                }
            } catch(IOException x) {
                final ErrorDialog d = new ErrorDialog(getStage());
                d.setMessage(I18N.getString("alert.save.failure.message", fileName));
                d.setDetails(I18N.getString("alert.save.failure.details"));
                d.setDebugInfoWithThrowable(x);
                d.showAndWait();
                result = ActionStatus.CANCELLED;
            }
        } else {
            result = ActionStatus.CANCELLED;
        }

        return result;
    }


    private ActionStatus performSaveAsAction() {

        final ActionStatus result;
        if (editorController.canGetFxmlText()) {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter f
                    = new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                            "*.fxml"); //NOI18N
            fileChooser.getExtensionFilters().add(f);
            fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

            File fxmlFile = fileChooser.showSaveDialog(getStage());
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
                if (! path.endsWith(".fxml")) { //NOI18N
                    try {
                        URL alternateURL = new URL(fxmlFile.toURI().toURL().toExternalForm() + ".fxml"); //NOI18N
                        File alternateFxmlFile = new File(alternateURL.toURI());
                        final AlertDialog d = new AlertDialog(getStage());
                        d.setMessage(I18N.getString("alert.save.noextension.message", fxmlFile.getName()));
                        String details = I18N.getString("alert.save.noextension.details");

                        if (alternateFxmlFile.exists()) {
                            details += "\n" //NOI18N
                                    + I18N.getString("alert.save.noextension.details.overwrite", alternateFxmlFile.getName());
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
                } catch(MalformedURLException x) {
                    // Should not happen
                    throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
                }

                // Checks if fxmlFile is the name of an already opened document
                final DocumentWindowController dwc
                        = MainController.getSingleton().lookupDocumentWindowControllers(newLocation);
                if (dwc != null && dwc != this) {
                    final Path fxmlPath = Paths.get(fxmlFile.toString());
                    final String fileName = fxmlPath.getFileName().toString();
                    final ErrorDialog d = new ErrorDialog(getStage());
                    d.setMessage(I18N.getString("alert.save.conflict.message", fileName));
                    d.setDetails(I18N.getString("alert.save.conflict.details"));
                    d.showAndWait();
                    result = ActionStatus.CANCELLED;
                } else if (forgetSave) {
                    result = ActionStatus.CANCELLED;
                } else {
                    // Recalculates references if needed
                    // TODO(elp)

                    // First change the location of the fxom document
                    editorController.setFxmlLocation(newLocation);
                    updateLoadFileTime();
                    updateStageTitle();

                    //TODO this case is not handled for using spring, need to take an extra look at this
                    //TODO this method do nothing for now
                    //TODO more generaly, what to do when using save as ? keep the same beans? something else
                    // We use same DocumentWindowController BUT we change its fxml :
                    // => reset document preferences
                    //resetDocumentPreferences();

                  //TODO remove after checking the new watching system is operational in EditorController or in filesystem
                    //watchingController.update();

                    // Now performs a regular save action
                    result = performSaveAction();
                    if (result.equals(ActionStatus.DONE)) {
                        messageBarController.setDocumentDirty(false);
                        saveJob = getEditorController().getJobManager().getCurrentJob();
                    }

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


    private void performRevertAction() {
        assert editorController.getFxomDocument() != null;
        assert editorController.getFxomDocument().getLocation() != null;

        final AlertDialog d = new AlertDialog(getStage());
        d.setMessage(I18N.getString("alert.revert.question.message", getStage().getTitle()));
        d.setDetails(I18N.getString("alert.revert.question.details"));
        d.setOKButtonTitle(I18N.getString("label.revert"));

        if (d.showAndWait() == AlertDialog.ButtonID.OK) {
            try {
                reload();
            } catch(IOException x) {
                final ErrorDialog errorDialog = new ErrorDialog(null);
                errorDialog.setMessage(I18N.getString("alert.open.failure1.message", getStage().getTitle()));
                errorDialog.setDetails(I18N.getString("alert.open.failure1.details"));
                errorDialog.setDebugInfoWithThrowable(x);
                errorDialog.setTitle(I18N.getString("alert.title.open"));
                errorDialog.showAndWait();
                MainController.getSingleton().documentWindowRequestClose(this);
            }
        }
    }


    ActionStatus performCloseAction() {

        // Makes sure that our window is front
        getStage().toFront();

        // Check if an editing session is on going
        if (getEditorController().isTextEditingSessionOnGoing()) {
            // Check if we can commit the editing session
            if (!getEditorController().canGetFxmlText()) {
                // Commit failed
                return ActionStatus.CANCELLED;
            }
        }

        // Checks if there are some pending changes
        final boolean closeConfirmed;
        if (isDocumentDirty()) {

            final AlertDialog d = new AlertDialog(getStage());
            d.setMessage(I18N.getString("alert.save.question.message", getStage().getTitle()));
            d.setDetails(I18N.getString("alert.save.question.details"));
            d.setOKButtonTitle(I18N.getString("label.save"));
            d.setActionButtonTitle(I18N.getString("label.do.not.save"));
            d.setActionButtonVisible(true);

            switch(d.showAndWait()) {
                default:
                case OK:
                    if (editorController.getFxomDocument().getLocation() == null) {
                        closeConfirmed = (performSaveAsAction() == ActionStatus.DONE);
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

        // Closes if confirmed
        if (closeConfirmed) {
            MainController.getSingleton().documentWindowRequestClose(this);


        }

        return closeConfirmed ? ActionStatus.DONE : ActionStatus.CANCELLED;
    }


    private void performRevealAction() {
        assert editorController.getFxomDocument() != null;
        assert editorController.getFxomDocument().getLocation() != null;

        final URL location = editorController.getFxomDocument().getLocation();

        try {
            fileSystem.revealInFileBrowser(new File(location.toURI()));
        } catch(IOException | URISyntaxException x) {
        	dialog.showErrorAndWait("",
        			I18N.getString("alert.reveal.failure.message", getStage().getTitle()),
        			I18N.getString("alert.reveal.failure.details"),
        			x);
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
            } catch(URISyntaxException x) {
                throw new RuntimeException("Bug", x); //NOI18N
            } catch(IOException x) {
                loadFileTime = null;
            }
        }
    }


    private boolean checkLoadFileTime() throws IOException {
        assert editorController.getFxmlLocation() != null;

        /*
         *  loadFileTime == null
         *          => fxml file does not exist
         *          => TRUE
         *
         *  loadFileTime != null
         *          => fxml file does/did exist
         *
         *          currentFileTime == null
         *              => fxml file no longer exists
         *              => TRUE
         *
         *          currentFileTime != null
         *              => fxml file still exists
         *              => loadFileTime.compare(currentFileTime) == 0
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
            } catch(NoSuchFileException x) {
                // editorController.getFxmlLocation() no longer exists
                result = true;
            } catch(URISyntaxException x) {
                throw new RuntimeException("Bug", x); //NOI18N
            }
        }

        return result;
    }


    private void performHelp() {
        try {
            fileSystem.open(EditorPlatform.DOCUMENTATION_URL);
        } catch (IOException ioe) {
        	dialog.showErrorAndWait("",
        			I18N.getString("alert.help.failure.message", EditorPlatform.DOCUMENTATION_URL),
        			I18N.getString("alert.messagebox.failure.details"),
        			ioe);
        }
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
