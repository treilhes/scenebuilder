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
package com.gluonhq.jfxapps.core.ui.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.ui.editor.messagelog.MessageLog;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.scene.control.Button;

@JfxAppsTest
@ContextConfiguration(classes = { MessageTest.Config.class, MessageLog.class, MessageBarController.class, MessagePopupController.class,
        MessagePanelController.class })
class MessageTest {

    private static final String CSS_THEME_PRESET = """
            .theme-presets {
                -sb-base: rgb(147, 147, 147);
                -sb-base-dark: derive(-sb-base, -60%);

                -sb-warning-panel: rgb(230, 225, 140);
                -sb-warning-panel-text: derive(-sb-base, -50%);
                -sb-warning-panel-text-dimmed: derive(-sb-base, -20%);
                -sb-message-panel: rgb(185, 220, 240);

            }
            """;

    @TestConfiguration
    static class Config {
    }

    @Autowired
    MessageLogger messageLogger;

    @Test
    @DirtiesContext
    void should_load_the_fxml(StageBuilder builder, FxRobot robot) {

        MessageBarController controller = builder.controller(MessageBarController.class)
                .setup(StageType.Fill)
                .size(800, 30)
                .css(CSS_THEME_PRESET)
                .show();

        assertNotNull(controller.getRoot());
    }

    @Test
    @DirtiesContext
    void should_show_two_warnings_are_available(StageBuilder builder, FxRobot robot) {

        MessageBarController controller = builder.controller(MessageBarController.class)
                .setup(StageType.Fill)
                .size(800, 30)
                .css(CSS_THEME_PRESET)
                .show();

        robot.interact(() -> {
            messageLogger.logInfoMessage("sssssssssssssssssssssssss");
            messageLogger.logInfoMessage("sssssssssssssssssssssssss");
            messageLogger.logWarningMessage("wwwwwwwwwwwwwwwwwwwwwwwwwwwww");
            messageLogger.logWarningMessage("wwwwwwwwwwwwwwwwwwwwwwwwwwwww");
        });

        Button messageButton = robot.lookup("#messageButton").query();

        assertEquals(4, messageLogger.getEntryCount());
        assertEquals("2", messageButton.getText());

    }

}
