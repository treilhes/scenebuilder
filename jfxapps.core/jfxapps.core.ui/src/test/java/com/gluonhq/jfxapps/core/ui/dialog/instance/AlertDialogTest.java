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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.ui.dialog.ModalWindowImpl;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

@JfxAppsTest
@ContextConfiguration(classes = { AlertDialogTest.Config.class, AlertDialog.class, ModalWindowImpl.class })
class AlertDialogTest {


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
    void must_show_the_alert_dialog(StageBuilder builder, FxRobot robot, JfxAppContext context) {
        try (var testStage = builder.controller().setup(StageType.Center).size(800, 600).show()) {

            AlertDialog alert = context.getBean(AlertDialog.class);

            robot.interact(() -> {
                alert.show();
                assertEquals(true, alert.getModalWindow().getStage().isFocused());
            });

            robot.interact(alert::close);
        }
    }

    @Test
    void must_show_the_alert_dialog_message(StageBuilder builder, FxRobot robot, JfxAppContext context) {
        final String message = "This is an alert message";
        try (var testStage = builder.controller().setup(StageType.Center).size(800, 600).show()) {

            AlertDialog alert = context.getBean(AlertDialog.class);

            robot.interact(() -> {
                alert.setMessage(message);
                alert.show();

                var messageLabel = robot.from(alert.getRoot()).lookup("#messageLabel").queryLabeled();
                assertEquals(message, messageLabel.getText());
            });

            robot.interact(alert::close);
        }
    }

    @Test
    void must_show_the_alert_dialog_details(StageBuilder builder, FxRobot robot, JfxAppContext context) {
        final String details = "These are alert details";
        try (var testStage = builder.controller().setup(StageType.Center).size(800, 600).show()) {

            AlertDialog alert = context.getBean(AlertDialog.class);

            robot.interact(() -> {
                alert.setDetails(details);
                alert.show();

                var detailsLabel = robot.from(alert.getRoot()).lookup("#detailsLabel").queryLabeled();
                assertEquals(details, detailsLabel.getText());
            });

            robot.interact(alert::close);
        }
    }


}
