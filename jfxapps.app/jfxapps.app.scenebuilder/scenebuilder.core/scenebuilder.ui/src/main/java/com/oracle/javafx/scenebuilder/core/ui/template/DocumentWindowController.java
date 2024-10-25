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
package com.oracle.javafx.scenebuilder.core.ui.template;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock.Orientation;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.SelectionBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.api.util.FXOMDocumentUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.ui.Docks;
import com.oracle.javafx.scenebuilder.core.ui.preference.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preference.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preference.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.template.InnerDockManager.DividerPosition;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Provider;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 *
 */
@ApplicationInstanceSingleton
public class DocumentWindowController extends AbstractFxmlWindowController implements MainInstanceWindow, InitializingBean {

    private enum InsertPosition {
        First, Last
    }

    private final Provider<LeftDividerHPosPreference> leftDividerHPos;
    private final Provider<RightDividerHPosPreference> rightDividerHPos;
    private final Provider<BottomDividerVPosPreference> bottomDividerVPos;

    @FXML
    private StackPane contentPanelHost;
    @FXML
    private StackPane messageBarHost;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private SplitPane leftRightSplitPane;

    @FXML
    private VBox leftHost;
    @FXML
    private VBox rightHost;
    @FXML
    private StackPane centerHost;
    @FXML
    private VBox bottomHost;

    private final Dock leftDockController;
    private final Dock rightDockController;
    private final Dock bottomDockController;

//    private SplitPositionController topBottonController;
//    private SplitPositionController leftRightController;


    //private PreferenceManager preferenceManager;
    //private MenuBarController menuBarController;
    //private Content contentPanelController;
    //private MessageBarController messageBarController;
    //private SelectionBarController selectionBarController;

    private InnerDockManager leftDockManager;
    private InnerDockManager rightDockManager;
    private InnerDockManager bottomDockManager;
    private final ApplicationInstanceEvents documentManager;

    private final MenuBar menuBar;
    private final Content content;
    private final MessageBar messageBar;
    private final SelectionBar selectionBar;
    private final Workspace workspace;
    private final JfxAppPlatform jfxAppPlatform;

    /*
     * DocumentWindowController
     */

    // @formatter:off
    public DocumentWindowController(
            I18N i18n,
            JfxAppPlatform jfxAppPlatform,
            ApplicationEvents sceneBuilderManager,
            IconSetting iconSetting,
            ApplicationInstanceEvents documentManager,


            Provider<LeftDividerHPosPreference> leftDividerHPos,
            Provider<RightDividerHPosPreference> rightDividerHPos,
            Provider<BottomDividerVPosPreference> bottomDividerVPos,

            Dock leftDockController,
            Dock rightDockController,
            Dock bottomDockController,
            DockViewController viewMenuController,

            MenuBar menuBar,
            @Autowired(required = false) Content content,
            MessageBar messageBar,
            SelectionBar selectionBar,
            Workspace workspace
            ) {
        super(i18n, sceneBuilderManager, iconSetting, DocumentWindowController.class.getResource("DocumentWindow.fxml"), false);
        // @formatter:on
        this.jfxAppPlatform = jfxAppPlatform;
        this.documentManager = documentManager;

        this.leftDockController = leftDockController;
        this.rightDockController = rightDockController;
        this.bottomDockController = bottomDockController;

        this.leftDockController.setId(Docks.LEFT_DOCK_UUID);
        this.rightDockController.setId(Docks.RIGHT_DOCK_UUID);
        this.bottomDockController.setId(Docks.BOTTOM_DOCK_UUID);

        this.leftDockController.setName(getI18n().getString("dock.name.left"));
        this.rightDockController.setName(getI18n().getString("dock.name.right"));
        this.bottomDockController.setName(getI18n().getString("dock.name.bottom"));

        this.bottomDockController.setMinimizedOrientation(Orientation.HORIZONTAL);

        this.leftDockController.notifyDockCreated();
        this.rightDockController.notifyDockCreated();
        this.bottomDockController.notifyDockCreated();

        // preferences
        this.leftDividerHPos = leftDividerHPos;
        this.rightDividerHPos = rightDividerHPos;
        this.bottomDividerVPos = bottomDividerVPos;

        this.menuBar = menuBar;
        this.content = content;
        this.messageBar = messageBar;
        this.selectionBar = selectionBar;
        this.workspace = workspace;

    }

    @FXML
    public void initialize() {

        // TODO ensure property listener is garbaged when fxom doc change
        documentManager.fxomDocument().subscribe(fxom -> {
            updateStageTitle();
            fxom.locationProperty().addListener((o, n, c) -> updateStageTitle());
        });

        leftDockManager = new InnerDockManager(jfxAppPlatform, leftDockController, leftHost, leftRightSplitPane, DividerPosition.AFTER, leftDividerHPos.get());
        rightDockManager = new InnerDockManager(jfxAppPlatform, rightDockController, rightHost, leftRightSplitPane, DividerPosition.BEFORE, rightDividerHPos.get());
        bottomDockManager = new InnerDockManager(jfxAppPlatform, bottomDockController, bottomHost, mainSplitPane, DividerPosition.BEFORE, bottomDividerVPos.get());

//        topBottonController = SplitPositionController.of(mainSplitPane, 2).content(leftRightSplitPane)
//                .divider(bottomDividerVPos).content(bottomHost)
//                .build(SplitPositionController.MAIN_TOP_BOTTOM);
//
//        leftRightController = SplitPositionController.of(leftRightSplitPane, 3).content(leftHost)
//                .divider(leftDividerHPos).content(centerHost).divider(rightDividerHPos)
//                .content(rightHost).build(SplitPositionController.MAIN_LEFT_RIGHT);

    }

    @PostConstruct
    public void initialize1() {
        System.out.println();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println();
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        assert contentPanelHost != null;
        assert messageBarHost != null;

        assert leftHost != null;
        assert rightHost != null;
        assert bottomHost != null;

        assert mainSplitPane != null;
        assert leftRightSplitPane != null;

        messageBar.setSelectionBar(selectionBar.getRoot());

        setMenuBar(menuBar);
        //setContentPane(content.getRoot());
        setMessageBar(messageBar);
        setContentPane(workspace);

        // Add a border to the Windows app, because of the specific window decoration on
        // Windows.
        if (JfxAppsPlatform.IS_WINDOWS) {
            getRoot().getStyleClass().add("windows-document-decoration");// NOI18N
        }

        setupDockContainer(leftDockController, leftRightSplitPane, leftHost, InsertPosition.First);
        setupDockContainer(rightDockController, leftRightSplitPane, rightHost, InsertPosition.Last);
        setupDockContainer(bottomDockController, mainSplitPane, bottomHost, InsertPosition.Last);
//
//        leftDockController.minimizedProperty().addListener((ob, o, n) -> {
//            leftRightController.setMinimized(leftHost, n);
//        });
//        rightDockController.minimizedProperty().addListener((ob, o, n) -> {
//            leftRightController.setMinimized(rightHost, n);
//        });
//        bottomDockController.minimizedProperty().addListener((ob, o, n) -> {
//            leftRightController.setMinimized(bottomHost, n);
//        });

        messageBarHost.heightProperty().addListener((InvalidationListener) o -> {
            final double h = messageBarHost.getHeight();
            contentPanelHost.setPadding(new Insets(h, 0.0, 0.0, 0.0));
        });

    }



//    private BooleanProperty leftDockVisibleProperty = new BooleanPropertyBase() {
//
//        @Override
//        public void set(boolean newValue) {
//            if (newValue) {
//                if (!leftHost.getChildren().isEmpty()) {
//                    leftRightSplitPane.getItems().add(0, leftHost);
//                }
//            } else {
//                leftRightSplitPane.getItems().remove(leftHost);
//                super.set(newValue);
//            }
//
//        }
//
//        @Override
//        public Object getBean() {
//            return DocumentWindowController.this;
//        }
//
//        @Override
//        public String getName() {
//            return "leftDockVisible";
//        }
//
//    };
//    @Override
//    public BooleanProperty leftDockVisibleProperty() {
//        return leftDockVisibleProperty;
//    }

    private BooleanProperty leftDockVisibleProperty = null;
    public ReadOnlyBooleanProperty leftDockVisibleProperty() {
        if (leftDockVisibleProperty == null) {
            leftDockVisibleProperty = new SimpleBooleanProperty();
            leftDockVisibleProperty.bind(leftDockController.minimizedProperty());
        }
        return leftDockVisibleProperty;
    }

    private void setupDockContainer(Dock dock, SplitPane placeHolder, Pane host,
            InsertPosition position) {
        // attach the dock container to the host
        host.getChildren().add(dock.getContent());
        // and set it for auto grow
        VBox.setVgrow(dock.getContent(), Priority.ALWAYS);
        // set initial state to removed
        placeHolder.getItems().remove(host);

        // if dock container has content
        // then attach the host to splitpane
        // else detach it
        dock.getContent().getChildren().addListener((Change<? extends Node> c) -> {
            int numChild = dock.getContent().getChildren().size();
            boolean isInserted = placeHolder.getItems().contains(host);
            if (numChild == 0 && isInserted) {
                placeHolder.getItems().remove(host);
            }
            if (numChild > 0 && !isInserted) {
                switch (position) {
                case First: {
                    placeHolder.getItems().add(0, host);
                    break;
                }
                default:
                    placeHolder.getItems().add(host);
                }
            }
        });
    }

    @Override
    protected void controllerDidCreateStage() {

        final Stage stage = getStage();
        assert stage != null;
        updateStageTitle();

    }

    @Override
    public void openWindow() {

        // if (!getStage().isShowing()) {
        // Starts watching document:
        // - editorController watches files referenced from the FXML text
        // - watchingController watches the document file, i18n resources,
        // preview stylesheets...

        // TODO remove after checking the new watching system is operational in
        // EditorController or in filesystem
        // assert !editorController.isFileWatchingStarted();
        // editorController.startFileWatching();
        // watchingController.start();
        // }

        if (!super.isOpen()) {
            super.openWindow();
        }

        if (!JfxAppsPlatform.IS_MAC) {
            // TODO uncomment or better add a Maximized preference to the document
            // getStage().setMaximized(true);
        }


    }


//    public boolean isFrontDocumentWindow() {
//        return getStage().isFocused()
//                || (previewWindowController != null && previewWindowController.getStage().isFocused())
//                || (skeletonWindowController != null && skeletonWindowController.getStage().isFocused())
//                || (jarAnalysisReportController != null && jarAnalysisReportController.getStage().isFocused());
//    }

//    public void performCloseFrontDocumentWindow() {
//        if (getStage().isFocused()) {
//            performCloseAction();
//        } else if (previewWindowController != null
//                && previewWindowController.getStage().isFocused()) {
//            previewWindowController.closeWindow();
//        } else if (skeletonWindowController != null
//                && skeletonWindowController.getStage().isFocused()) {
//            skeletonWindowController.closeWindow();
//        } else if (jarAnalysisReportController != null
//                && jarAnalysisReportController.getStage().isFocused()) {
//            jarAnalysisReportController.closeWindow();
//        }
//    }

//    public void initializeCssPanel() {
//        assert bottomHost != null;
//        if (!bottomHost.getChildren().contains(cssPanelController.getRoot())) {
//            bottomHost.getChildren().add(cssPanelController.getRoot());
//            VBox.setVgrow(cssPanelController.getRoot(), Priority.ALWAYS);
//        }
//    }

    @Override
    public void setCloseHandler(CloseHandler closeHandler) {
        super.setCloseHandler(() -> {
            closeHandler.onClose();
        });
    }

    @Override
    public void updateStageTitle() {
        if (contentPanelHost != null) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

            jfxAppPlatform.runOnFxThreadWithActiveScope(()->{
                getStage().setTitle(FXOMDocumentUtils.makeTitle(getI18n(), fxomDocument));
            });

        } // else controllerDidLoadFxml() will invoke me again

    }

    @Override
    public void setMainKeyPressedEvent(EventHandler<KeyEvent> mainKeyEventFilter) {
        mainSplitPane.addEventFilter(KeyEvent.KEY_PRESSED, mainKeyEventFilter);
    }

    private void setMenuBar(MenuBar menuBar) {
        assert getRoot() instanceof VBox;
        final VBox rootVBox = (VBox) getRoot();

        if (menuBar == null || menuBar.getMenuBar() == null) {
            logger.warn("MenuBar {} content is null", menuBar);
            return;
        }

        rootVBox.getChildren().add(0, menuBar.getMenuBar());
    }

    private void setContentPane(Workspace workspace) {
        Parent root = workspace != null ? workspace.getRoot() : null;
        if (root == null) {
            logger.warn("ContentPane can't be set to null");
            return;
        }
        contentPanelHost.getChildren().add(root);
    }

    private void setMessageBar(MessageBar messageBar) {
        Parent root = messageBar != null ? messageBar.getRoot() : null;
        if (root == null) {
            logger.warn("MessageBar can't be set to null");
            return;
        }
        messageBarHost.getChildren().add(root);
    }

//
//    @Override
//    public void toggleMinimizedLeft() {
//        leftDockController.setMinimized(!leftDockController.isMinimized());
//        leftRightController.toggleMinimized(leftHost);
//    }
//
//    @Override
//    public void toggleMinimizedRight() {
//        rightDockController.setMinimized(!rightDockController.isMinimized());
//        leftRightController.toggleMinimized(rightHost);
//    }
//
//    @Override
//    public void toggleMinimizedBottom() {
//        bottomDockController.setMinimized(!bottomDockController.isMinimized());
//        topBottonController.toggleMinimized(bottomHost);
//    }


    public Dock getLeftDock() {
        return leftDockController;
    }

    public Dock getRightDock() {
        return rightDockController;
    }

    public Dock getBottomDock() {
        return bottomDockController;
    }

}
