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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.util.FXOMDocumentUtils;
import com.oracle.javafx.scenebuilder.core.dock.DockPanelController;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.ui.controller.InnerDockManager.DividerPosition;
import com.oracle.javafx.scenebuilder.ui.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.MaximizedPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageHeightPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageWidthPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.XPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.YPosPreference;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
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
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentWindowController extends AbstractFxmlWindowController implements DocumentWindow {

    private enum InsertPosition {
        First, Last
    }

    // PREFERENCES
    private final XPosPreference xPosPreference;
    private final YPosPreference yPosPreference;
    private final StageHeightPreference stageHeightPreference;
    private final StageWidthPreference stageWidthPreference;
    private final MaximizedPreference maximizedWindowPreference;
    private final LeftDividerHPosPreference leftDividerHPos;
    private final RightDividerHPosPreference rightDividerHPos;
    private final BottomDividerVPosPreference bottomDividerVPos;

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

    private final DockPanelController leftDockController;
    private final DockPanelController rightDockController;
    private final DockPanelController bottomDockController;
    private final DockViewController viewMenuController;
//    private SplitPositionController topBottonController;
//    private SplitPositionController leftRightController;


    private PreferenceManager preferenceManager;
    //private MenuBarController menuBarController;
    //private Content contentPanelController;
    //private MessageBarController messageBarController;
    //private SelectionBarController selectionBarController;

    private InnerDockManager leftDockManager;
    private InnerDockManager rightDockManager;
    private InnerDockManager bottomDockManager;
    private final DocumentManager documentManager;
    /*
     * DocumentWindowController
     */

    // @formatter:off
    public DocumentWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            DocumentManager documentManager,
            @Lazy @Autowired XPosPreference xPos,
            @Lazy @Autowired YPosPreference yPos,
            @Lazy @Autowired StageHeightPreference stageHeight,
            @Lazy @Autowired StageWidthPreference stageWidth,
            @Lazy @Autowired MaximizedPreference maximizedWindow,

            @Lazy @Autowired LeftDividerHPosPreference leftDividerHPos,
            @Lazy @Autowired RightDividerHPosPreference rightDividerHPos,
            @Lazy @Autowired BottomDividerVPosPreference bottomDividerVPos,

            @Autowired DockPanelController leftDockController,
            @Autowired DockPanelController rightDockController,
            @Autowired DockPanelController bottomDockController,
            @Autowired DockViewController viewMenuController) {
        super(sceneBuilderManager, iconSetting, DocumentWindowController.class.getResource("DocumentWindow.fxml"), I18N.getBundle(), false);
        // @formatter:on
        this.documentManager = documentManager;

        this.leftDockController = leftDockController;
        this.rightDockController = rightDockController;
        this.bottomDockController = bottomDockController;

        this.leftDockController.setId(Dock.LEFT_DOCK_UUID);
        this.rightDockController.setId(Dock.RIGHT_DOCK_UUID);
        this.bottomDockController.setId(Dock.BOTTOM_DOCK_UUID);

        this.leftDockController.notifyDockCreated();
        this.rightDockController.notifyDockCreated();
        this.bottomDockController.notifyDockCreated();

        // preferences
        this.xPosPreference = xPos;
        this.yPosPreference = yPos;
        this.stageHeightPreference = stageHeight;
        this.stageWidthPreference = stageWidth;
        this.maximizedWindowPreference = maximizedWindow;
        this.leftDividerHPos = leftDividerHPos;
        this.rightDividerHPos = rightDividerHPos;
        this.bottomDividerVPos = bottomDividerVPos;

        this.viewMenuController = viewMenuController;
        this.preferenceManager = new PreferenceManager();

//        this.menuBarController = menuBarController;
//        //this.contentPanelController = contentPanelController;
//        this.messageBarController = messageBarController;
//        this.selectionBarController = selectionBarController;
    }

    @FXML
    public void initialize() {

        leftDockManager = new InnerDockManager(leftDockController, leftHost, leftRightSplitPane, DividerPosition.AFTER, leftDividerHPos);
        rightDockManager = new InnerDockManager(rightDockController, rightHost, leftRightSplitPane, DividerPosition.BEFORE, rightDividerHPos);
        bottomDockManager = new InnerDockManager(bottomDockController, bottomHost, mainSplitPane, DividerPosition.BEFORE, bottomDividerVPos);

//        topBottonController = SplitPositionController.of(mainSplitPane, 2).withContent(leftRightSplitPane)
//                .withDivider(bottomDividerVPos).withContent(bottomHost)
//                .build(SplitPositionController.MAIN_TOP_BOTTOM);
//
//        leftRightController = SplitPositionController.of(leftRightSplitPane, 3).withContent(leftHost)
//                .withDivider(leftDividerHPos).withContent(centerHost).withDivider(rightDividerHPos)
//                .withContent(rightHost).build(SplitPositionController.MAIN_LEFT_RIGHT);

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



        // Add a border to the Windows app, because of the specific window decoration on
        // Windows.
        if (EditorPlatform.IS_WINDOWS) {
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

    private void setupDockContainer(DockPanelController dock, SplitPane placeHolder, Pane host,
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
        updateStageTitle();

        // initialize preference binding
        final Stage stage = getStage();
        assert stage != null;

        preferenceManager.apply();
        preferenceManager.track();

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

        if (!EditorPlatform.IS_MAC) {
            // TODO uncomment or better add a Maximized preference to the document
            // getStage().setMaximized(true);
        }

        preferenceManager.apply();
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
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
            preferenceManager.untrackMaximizedOnly();
            getStage().setMaximized(false);
            closeHandler.onClose();
        });
    }

    @Override
    public void updateStageTitle() {
        if (contentPanelHost != null) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            getStage().setTitle(FXOMDocumentUtils.makeTitle(fxomDocument));
        } // else controllerDidLoadFxml() will invoke me again

    }

    @Override
    public void setMainKeyPressedEvent(EventHandler<KeyEvent> mainKeyEventFilter) {
        mainSplitPane.addEventFilter(KeyEvent.KEY_PRESSED, mainKeyEventFilter);
    }

    @Override
    public void setMenuBar(MenuBar menuBar) {
        assert getRoot() instanceof VBox;
        final VBox rootVBox = (VBox) getRoot();
        rootVBox.getChildren().add(0, menuBar);
    }

    @Override
    public void setContentPane(Parent root) {
        contentPanelHost.getChildren().add(root);
    }

    @Override
    public void setMessageBar(Parent root) {
        messageBarHost.getChildren().add(root);
    }

    @Override
    public void apply() {
        viewMenuController.performLoadDockAndViewsPreferences();
        this.preferenceManager.apply();
    }

    @Override
    public void track() {
        this.preferenceManager.track();
    }

    @Override
    public void untrack() {
        this.preferenceManager.untrack();
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

    @Override
    public Dock getLeftDock() {
        return leftDockController;
    }

    @Override
    public Dock getRightDock() {
        return rightDockController;
    }

    @Override
    public Dock getBottomDock() {
        return bottomDockController;
    }

    private class PreferenceManager {

        private ChangeListener<? super Number> xPropertyListener = (ob, o, n) -> {
            if (!getStage().isMaximized()) {
                xPosPreference.setValue(n.doubleValue());
            }
        };
        private ChangeListener<? super Double> xPosPreferenceListener = (ob, o, n) -> getStage().setX(n);

        private ChangeListener<? super Number> yPropertyListener = (ob, o, n) -> {
            if (!getStage().isMaximized()) {
                yPosPreference.setValue(n.doubleValue());
            }
        };
        private ChangeListener<? super Double> yPosPreferenceListener = (ob, o, n) -> getStage().setY(n);

        private ChangeListener<? super Number> heightPropertyListener = (ob, o, n) -> {
            if (!getStage().isMaximized()) {
                stageHeightPreference.setValue(n.doubleValue());
            }
        };
        private ChangeListener<? super Double> stageHeightPreferenceListener = (ob, o, n) -> getStage().setHeight(n);

        private ChangeListener<? super Number> widthPropertyListener = (ob, o, n) -> {
            if (!getStage().isMaximized()) {
                stageWidthPreference.setValue(n.doubleValue());
            }
        };
        private ChangeListener<? super Double> stageWidthPreferenceListener = (ob, o, n) -> getStage().setWidth(n);

        private ChangeListener<? super Boolean> maximizedPropertyListener = (ob, o, n) -> {
            maximizedWindowPreference.setValue(n);
            if (!n) {
                getStage().setWidth(stageWidthPreference.getValue());
                getStage().setHeight(stageHeightPreference.getValue());
            }
        };
        private ChangeListener<? super Boolean> maximizedPreferenceListener = (ob, o, n) -> {
            //System.out.println("MAXIMIZING " + n);
            getStage().setMaximized(n);
        };

        public void apply() {
//            getStage().maximizedProperty().addListener((ob, o, n) -> System.out.println("MAX" + n));
//            getStage().maximizedProperty().addListener((n) -> System.out.println("MAXINV" + n));

            if (stageHeightPreference.isValid() && !maximizedWindowPreference.getValue()) {
                getStage().setHeight(stageHeightPreference.getValue());
            }
            if (stageWidthPreference.isValid() && !maximizedWindowPreference.getValue()) {
                getStage().setWidth(stageWidthPreference.getValue());
            }
            if (xPosPreference.isValid() && !maximizedWindowPreference.getValue()) {
                getStage().setX(xPosPreference.getValue());
            }
            if (yPosPreference.isValid() && !maximizedWindowPreference.getValue()) {
                getStage().setY(yPosPreference.getValue());
            }
            if (maximizedWindowPreference.isValid()) {
                getStage().setMaximized(maximizedWindowPreference.getValue());
            }
            leftDockManager.applyPreference();
            rightDockManager.applyPreference();
            bottomDockManager.applyPreference();
        }

        public void track() {
            // Add stage x and y listeners
            getStage().xProperty().addListener(xPropertyListener);
            xPosPreference.getObservableValue().addListener(xPosPreferenceListener);

            getStage().yProperty().addListener(yPropertyListener);
            yPosPreference.getObservableValue().addListener(yPosPreferenceListener);

            // Add stage height and width listeners
            getStage().heightProperty().addListener(heightPropertyListener);
            stageHeightPreference.getObservableValue().addListener(stageHeightPreferenceListener);

            getStage().widthProperty().addListener(widthPropertyListener);
            stageWidthPreference.getObservableValue().addListener(stageWidthPreferenceListener);

            getStage().maximizedProperty().addListener(maximizedPropertyListener);
            maximizedWindowPreference.getObservableValue().addListener(maximizedPreferenceListener);

            leftDockManager.trackPreference();
            rightDockManager.trackPreference();
            bottomDockManager.trackPreference();
        }

        public void untrack() {
            // Remove stage x and y listeners
            getStage().xProperty().removeListener(xPropertyListener);
            xPosPreference.getObservableValue().removeListener(xPosPreferenceListener);

            getStage().yProperty().removeListener(yPropertyListener);
            yPosPreference.getObservableValue().removeListener(yPosPreferenceListener);

            // Remove stage height and width listeners
            getStage().heightProperty().removeListener(heightPropertyListener);
            stageHeightPreference.getObservableValue().removeListener(stageHeightPreferenceListener);

            getStage().widthProperty().removeListener(widthPropertyListener);
            stageWidthPreference.getObservableValue().removeListener(stageWidthPreferenceListener);

            getStage().maximizedProperty().removeListener(maximizedPropertyListener);
            maximizedWindowPreference.getObservableValue().removeListener(maximizedPreferenceListener);

            leftDockManager.untrackPreference();
            rightDockManager.untrackPreference();
            bottomDockManager.untrackPreference();
        }

        /**
         * Used before closing the window to keep the real size and position of the window
         * as preferences. If the window is in the maximized state then x,y,width,height match the screen size
         * So opening the same document in another instance will size the window with the screen values and
         * with the maximized state. When unmaximizing the size of the window will stay the same
         */
        private void untrackMaximizedOnly() {
            getStage().maximizedProperty().removeListener(maximizedPropertyListener);
            maximizedWindowPreference.getObservableValue().removeListener(maximizedPreferenceListener);
        }
    }
}
