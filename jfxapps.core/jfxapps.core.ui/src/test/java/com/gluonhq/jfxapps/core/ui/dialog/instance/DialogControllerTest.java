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
package com.gluonhq.jfxapps.core.ui.dialog.instance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow.ButtonID;
import com.gluonhq.jfxapps.core.ui.dialog.ModalWindowImpl;
import com.gluonhq.jfxapps.test.JfxAppsTest;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

@JfxAppsTest
@ContextConfiguration(classes = {
        DialogControllerTest.Config.class,
        DialogController.class,
        AlertDialog.class,
        ErrorDialog.class,
        ModalWindowImpl.class
        })
//FIXME: even when closed the dialog is not removed from the stage, this completely defeat the lookup as we don't have any root to search in
class DialogControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        IconSetting iconSetting() {
            return Mockito.mock(IconSetting.class);
        }
        @Bean
        JfxAppsPlatform jfxAppsPlatform() {
            var mock = Mockito.mock(JfxAppsPlatform.class);
            Mockito.when(mock.isWindows()).thenReturn(true);
            return mock;
        }
    }

    @Test
    void must_show_the_alert_dialog_and_close_it_on_cancel_button_click(Stage stage, FxRobot robot, JfxAppContext context) {

        DialogController controller = context.getBean(DialogController.class);

        Runnable showAlert = () -> controller.showAlertAndWait("title", "message", "detail");
        Runnable interaction = () -> robot.interactNoWait(showAlert);

        Thread.startVirtualThread(interaction);

        var optButton = lookupWithAttempts(robot, 10, 100, Button.class, "cancelButton");

        assertThat(optButton.isPresent());

        robot.interact(() -> robot.clickOn(optButton.get()));

        assertFalse(optButton.get().getScene().getWindow().isShowing());

        robot.interact(() -> stage.close());
    }


    @Test
    void must_show_the_error_dialog_and_close_it_on_cancel_button_click(Stage stage, FxRobot robot, JfxAppContext context) {

        DialogController controller = context.getBean(DialogController.class);

        Runnable runnable = () -> robot.interactNoWait(() -> controller.showErrorAndWait("title", "message", "detail"));

        Thread.startVirtualThread(runnable);

        var optButton = lookupWithAttempts(robot, 10, 100, Button.class, "cancelButton");

        assertThat(optButton.isPresent());

        robot.interact(() -> robot.clickOn(optButton.get()));

        assertFalse(optButton.get().getScene().getWindow().isShowing());

        robot.interact(() -> stage.close());

    }


    @Test
    void must_show_a_custom_alert_dialog_and_close_it_on_cancel_button_click(Stage stage, FxRobot robot, JfxAppContext context) {

        DialogController controller = context.getBean(DialogController.class);

        Runnable runnable = () -> robot.interactNoWait(() -> {
            var alert = controller.customAlert();

            alert.setMessage("message");
            alert.setDetails("detail");

            var modalWindow = alert.getModalWindow();
            modalWindow.setTitle("title");
            modalWindow.setActionButtonTitle("action");
            modalWindow.setActionButtonVisible(true);
            modalWindow.setActionButtonDisable(true);

            modalWindow.setOKButtonTitle("ok");
            modalWindow.setOKButtonVisible(true);
            modalWindow.setOKButtonDisable(false);

            modalWindow.setCancelButtonTitle("cancel");

            modalWindow.setImageViewVisible(true);
            //alert.setImageViewImage(null);

            modalWindow.setShowDefaultButton(true);
            modalWindow.setDefaultButtonID(ButtonID.CANCEL);

            modalWindow.setButtonsFocusTraversable();

            alert.showAndWait();
        });

        Thread.startVirtualThread(runnable);

        var optButton = lookupWithAttempts(robot, 10, 100, Button.class, "cancelButton");

        assertThat(optButton.isPresent());

        robot.interact(() -> robot.clickOn(optButton.get()));

        assertFalse(optButton.get().getScene().getWindow().isShowing());

        robot.interact(() -> stage.close());
    }

    private <T extends Node> Optional<T> lookupWithAttempts(FxRobot robot, int maxAttempts,
            int waitBeetweenAttempts, Class<T> clazz, String buttonId) {

        Optional<T> optButton = Optional.ofNullable(null);
        while(!optButton.isPresent() && maxAttempts > 0) {
            try {
                Thread.sleep(waitBeetweenAttempts);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            optButton = robot.lookup("#"+buttonId).tryQueryAs(clazz);
            System.out.println("optButton not present yet, waiting...");
            maxAttempts--;
        }
        return optButton;
    }

}
