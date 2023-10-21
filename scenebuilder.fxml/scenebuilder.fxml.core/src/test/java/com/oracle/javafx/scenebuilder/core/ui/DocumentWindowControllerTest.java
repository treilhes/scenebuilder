/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.ui;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager.DockRequest;
import com.oracle.javafx.scenebuilder.api.ui.dock.Dock;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockType;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.ui.dock.View;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewContent;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.core.context.DocumentScope;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.dock.DockPanelController;
import com.oracle.javafx.scenebuilder.core.dock.DockTypeSplitH;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.DockMinimizedPreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockDockTypePreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.test.FxmlControllerLoader;
import com.oracle.javafx.scenebuilder.ui.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.MaximizedPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageHeightPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageWidthPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.XPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.YPosPreference;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class DocumentWindowControllerTest {

    static {
        I18N.initForTest();
    }

    private SceneBuilderManager sbm = new SceneBuilderManager.SceneBuilderManagerImpl();
    private DocumentManager dm = new DocumentManager.DocumentManagerImpl();
    private ViewManager viewManager = new ViewManager.ViewManagerImpl();
    private DockManager dockManager = new DockManager.DockManagerImpl();

    @Mock
    private SbContext context;

    @Mock
    private EditorInstance scopedDocument;

    @Mock
    private IconSetting iconSetting;
    @Mock
    private XPosPreference xPos;
    @Mock
    private YPosPreference yPos;
    @Mock
    private StageHeightPreference stageHeight;
    @Mock
    private StageWidthPreference stageWidth;
    @Mock
    private MaximizedPreference maximizedWindow;
    @Mock
    private LeftDividerHPosPreference leftDividerHPos;
    @Mock
    private RightDividerHPosPreference rightDividerHPos;
    @Mock
    private BottomDividerVPosPreference bottomDividerVPos;
    @Mock
    private DockPanelController leftDockController;
    @Mock
    private DockPanelController rightDockController;
    @Mock
    private DockPanelController bottomDockController;
    @Mock
    private DockViewController viewMenuController;

    // DockPanelController
    @Mock
    private LastDockUuidPreference lastDockUuidPreference;
    @Mock
    private LastDockDockTypePreference lastDockDockTypePreference;
    @Mock
    private DockMinimizedPreference dockMinimizedPreference;

    // ViewAttachment
    @Mock
    private ViewAttachment viewAttachment;
    @Mock
    private View view1;
    @Mock
    private View view2;
    @Mock
    private View view3;
    @Mock
    private ViewContent viewContent;

    private ViewController viewController;

    private List<DockType<?>> dockTypes;

//    private Stage stage;
//    private Pane pane;
//    private Button btn;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
//        System.out.println("start");
//        this.stage = stage;
//
//        pane = new Pane();
//        btn = new Button();
//        pane.getChildren().add(btn);

//        stage.setScene(new Scene(pane, 800, 100, Color.BEIGE));
//        stage.show();
    }

    // @formatter:off
    private DocumentWindowController getInstance() {
        DocumentWindowController dwc = new DocumentWindowController(
                sbm,
                iconSetting,
                dm,
                xPos,
                yPos,
                stageHeight,
                stageWidth,
                maximizedWindow,
                leftDividerHPos,
                rightDividerHPos,
                bottomDividerVPos,
                leftDockController,
                rightDockController,
                bottomDockController,
                viewMenuController
                );
        return dwc;
    }
    // @formatter:on

    private DockPanelController newDockPanelController() {
        DockPanelController dpc = new DockPanelController(
            dockManager,
            viewManager,
            lastDockUuidPreference,
            lastDockDockTypePreference,
            dockMinimizedPreference,
            dockTypes);
        return dpc;
    }
    @Test
    void should_load_the_documentwindow_fxml() {
        Mockito.when(leftDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(rightDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(bottomDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));

        Parent ui = FxmlControllerLoader.loadFxml(getInstance());
        assertNotNull(ui);
    }

    @Test
    void should_load(FxRobot robot) {

        DocumentScope.setCurrentScope(scopedDocument);

        Mockito.when(leftDividerHPos.getValue()).thenReturn(0.2d);
        Mockito.when(rightDividerHPos.getValue()).thenReturn(0.8d);
        Mockito.when(bottomDividerVPos.getValue()).thenReturn(0.4d);

        Mockito.when(leftDividerHPos.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(0.2d));
        Mockito.when(rightDividerHPos.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(0.8d));
        Mockito.when(bottomDividerVPos.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(0.4d));

        Mockito.when(xPos.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(0d));
        Mockito.when(yPos.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(0d));
        Mockito.when(stageWidth.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(800d));
        Mockito.when(stageHeight.getObservableValue()).thenReturn(new SimpleObjectProperty<Double>(600d));

        Mockito.when(maximizedWindow.getObservableValue()).thenReturn(new SimpleBooleanProperty(true));

        Mockito.when(lastDockDockTypePreference.getValue()).thenReturn(FXCollections.observableHashMap());

        Mockito.when(view1.parentDockProperty()).thenReturn(new SimpleObjectProperty<>());
        Mockito.when(view1.nameProperty()).thenReturn(new SimpleStringProperty("view name 1"));
        Mockito.when(view1.getViewController()).thenReturn(viewContent);

        Mockito.when(view2.parentDockProperty()).thenReturn(new SimpleObjectProperty<>());
        Mockito.when(view2.nameProperty()).thenReturn(new SimpleStringProperty("view name 2"));
        Mockito.when(view2.getViewController()).thenReturn(viewContent);

        Mockito.when(view3.parentDockProperty()).thenReturn(new SimpleObjectProperty<>());
        Mockito.when(view3.nameProperty()).thenReturn(new SimpleStringProperty("view name 3"));
        Mockito.when(view3.getViewController()).thenReturn(viewContent);

        Mockito.when(viewContent.getRoot()).thenReturn(new Label("View Content"));


        viewController = new ViewController(dockManager);

        Mockito.when(context.getBean(ViewController.class)).thenReturn(new ViewController(dockManager));

        dockTypes = List.of(new DockTypeSplitH(context));

        leftDockController = newDockPanelController();
        rightDockController = newDockPanelController();
        bottomDockController = newDockPanelController();

        DocumentWindowController dw = FxmlControllerLoader.load(getInstance());

        robot.interact(() -> {
            Parent root = dw.getRoot();
            root.setStyle("-fx-background-color: #FF55BB");
            //pane.getChildren().add(root);
            dw.setMenuBar(new MenuBar(new Menu("Menu")));
            dw.setContentPane(new Label("Content"));
            dw.setMessageBar(new Label("MessageBar"));
            viewManager.dock().onNext(new DockRequest(viewAttachment, view1, Dock.LEFT_DOCK_UUID));
            viewManager.dock().onNext(new DockRequest(viewAttachment, view2, Dock.RIGHT_DOCK_UUID));
            viewManager.dock().onNext(new DockRequest(viewAttachment, view3, Dock.BOTTOM_DOCK_UUID));

            dw.openWindow();
        });

        System.out.println();
    }


}
