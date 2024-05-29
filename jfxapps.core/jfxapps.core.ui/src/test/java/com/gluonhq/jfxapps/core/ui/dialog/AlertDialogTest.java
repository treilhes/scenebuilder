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
package com.gluonhq.jfxapps.core.ui.dialog;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.misc.IconSetting;
import com.gluonhq.jfxapps.core.ui.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.test.FxmlControllerLoader;
import com.oracle.javafx.scenebuilder.test.TestStages;

import javafx.scene.Parent;
import javafx.stage.Stage;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class AlertDialogTest {

    static {
        I18N.initForTest();
    }

    private SceneBuilderManager sbm = new SceneBuilderManager.SceneBuilderManagerImpl();

    @Mock
    private IconSetting is;

    @Mock
    private JfxAppContext context;

    private Stage stage;


    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        TestStages.emptyPane(stage);

        //stage.getScene().getStylesheets().add("file:///C:/SSDDrive/git/scenebuilder/scenebuilder.ext.sb/src/main/resources/com/oracle/javafx/scenebuilder/sb/css/ThemeDark.css");
        //stage.getScene().getStylesheets().add("file:///D:/Dev/eclipse/scenebuilderx/scenebuilder/scenebuilder.core.ext/scenebuilder.ext.sb/src/main/resources/com/oracle/javafx/scenebuilder/sb/css/ThemeDark.css");
    }

    @Test
    void should_load_alert_fxml() {
        Parent ui = FxmlControllerLoader.controller(new AlertDialog(sbm, is, stage)).loadFxml();
        assertNotNull(ui);
    }

    @Test
    void should_load_ui(FxRobot robot) {
        String message = "jfhgjkdh fgjkdhfgkh jfgkjdhf";
        String detailsLabel = "detailfgdfg df g   ggdfg";

        AlertDialog dialog = new AlertDialog(sbm, is, stage);

        robot.interact(() -> {
            FxmlControllerLoader.controller(dialog).darkTheme(sbm).load();

            dialog.setTitle("sometitle");
            dialog.setMessage(message);
            dialog.setDetails(detailsLabel);
            dialog.setActionButtonDisable(true);
            dialog.setActionButtonVisible(false);
            dialog.setOKButtonDisable(true);
            dialog.setOKButtonVisible(false);
            dialog.setCancelButtonTitle(I18N.getString("label.close"));

            dialog.openWindow();
        });

        robot.interact(() -> {
            FxAssert.verifyThat("#messageLabel" , LabeledMatchers.hasText(message));
            FxAssert.verifyThat("#detailsLabel" , LabeledMatchers.hasText(detailsLabel));
        });

    }

}
