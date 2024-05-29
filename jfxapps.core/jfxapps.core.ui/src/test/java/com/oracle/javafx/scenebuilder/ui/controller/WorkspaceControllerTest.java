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
package com.oracle.javafx.scenebuilder.ui.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationInstanceScope;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.menu.ContextMenu;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.SceneGraphObject;
import com.oracle.javafx.scenebuilder.test.FxmlControllerLoader;
import com.oracle.javafx.scenebuilder.test.JfxAppsExtension;
import com.oracle.javafx.scenebuilder.test.TestStages;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class, JfxAppsExtension.class})
class WorkspaceControllerTest {

    static {
        I18N.initForTest();
    }

    private SceneBuilderManager sbm = new SceneBuilderManager.SceneBuilderManagerImpl();
    private DocumentManager dm = new DocumentManager.DocumentManagerImpl();

//    @Mock
//    private ApplicationInstance scopedDocument;

    @Mock
    private FXOMDocument omDocument;

    @Mock
    private FXOMObject omObject;

    @Mock
    private ContextMenu contextMenu;

    private SceneGraphObject sceneGraphObject;

    private Pane pane;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        pane = TestStages.paneWithEnlargedContentToFit(stage, 800, 600);
        stage.show();
    }

    @Test
    void should_load_the_hud_fxml() {
        Parent ui = FxmlControllerLoader.controller(new WorkspaceController(sbm, dm, contextMenu)).loadFxml();
        assertNotNull(ui);
    }

    @Test
    void show_ui(FxRobot robot) {
        //JfxAppContext.applicationInstanceScope.setCurrentScope(scopedDocument);

        WorkspaceController workspace = FxmlControllerLoader.controller(new WorkspaceController(sbm, dm, contextMenu))
                .darkTheme(sbm)
                .load();

        robot.interact(() -> {
            pane.getChildren().add(workspace.getRoot());
        });

        System.out.println();
    }

    @Test
    void should_scale_the_content(FxRobot robot) {
        sceneGraphObject = new SceneGraphObject(new Label("sceneGraphObject"));

        Mockito.when(omDocument.sceneGraphRevisionProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(omDocument.cssRevisionProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(omDocument.getSceneGraphRoot()).thenReturn(new Label("content"));
        Mockito.when(omDocument.getFxomRoot()).thenReturn(omObject);
        Mockito.when(omObject.getSceneGraphObject()).thenReturn(sceneGraphObject);

        Mockito.when(omDocument.getDisplayNodeOrSceneGraphRoot()).thenReturn(new Label("content2"));

        //JfxAppContext.applicationInstanceScope.setCurrentScope(scopedDocument);

        WorkspaceController workspace = FxmlControllerLoader.controller(new WorkspaceController(sbm, dm, contextMenu)).load();

        robot.interact(() -> {
            pane.getChildren().add(workspace.getRoot());
        });

        dm.fxomDocument().set(omDocument);

        robot.interact(() -> {
            workspace.setScaling(2.0d);
        });

        System.out.println();
    }

}
