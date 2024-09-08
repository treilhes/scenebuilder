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
package com.gluonhq.jfxapps.core.ui.controller;

import static org.junit.Assert.assertNotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.control.LabeledMatchers;

import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.TestStages;

import javafx.scene.control.Button;

@JfxAppsTest
@ContextConfiguration(classes = { HudWindowController.class })
class HudWindowControllerTest {

    @Test
    void should_load_the_hud_fxml(StageBuilder stageBuilder) {
        var controller = stageBuilder.controller(HudWindowController.class).show();
        assertNotNull(controller.getRoot());
    }

    @Test
    void should_create_3_rows_with_only_2_lines_with_values(StageBuilder stageBuilder, FxRobot robot) {
        AtomicReference<Button> b = new AtomicReference<>();

        HudWindowController hud = stageBuilder.controller(HudWindowController.class)
                .size(800, 600)
                .css("""
                    #gridPane {
                        -fx-border-color: red;
                        -fx-background-color: green;
                    }
                    """)
                .setup((s, w, h, c) -> {
                    b.set(TestStages.centeredButton(s, w, h));
                    s.show();
                    return b.get();
                })
                .show();

        final String name1 = "SomeName";
        final String value1 = "DLKJDK";

        final String name2 = "SomeName2";
        final String value2 = "DLKJDK3";

        robot.interact(() -> {
            hud.setRowCount(3);
            hud.setNameAtRowIndex(name1, 0);
            hud.setValueAtRowIndex(value1, 0);

            hud.setNameAtRowIndex(name2, 2);
            hud.setValueAtRowIndex(value2, 2);

            hud.openWindow(b.get());
        });

        String idName1 = String.format(HudWindowController.NAME_LABEL_ID_FORMAT, 0);
        String idValue1 = String.format(HudWindowController.VALUE_LABEL_ID_FORMAT, 0);

        String idName2 = String.format(HudWindowController.NAME_LABEL_ID_FORMAT, 2);
        String idValue2 = String.format(HudWindowController.VALUE_LABEL_ID_FORMAT, 2);

        FxAssert.verifyThat("#" + idName1, Objects::nonNull);
        FxAssert.verifyThat("#" + idValue1, Objects::nonNull);

        FxAssert.verifyThat("#" + idName1, LabeledMatchers.hasText(name1));
        FxAssert.verifyThat("#" + idValue1, LabeledMatchers.hasText(value1));

        FxAssert.verifyThat("#" + idName2, Objects::nonNull);
        FxAssert.verifyThat("#" + idValue2, Objects::nonNull);

        FxAssert.verifyThat("#" + idName2, LabeledMatchers.hasText(name2));
        FxAssert.verifyThat("#" + idValue2, LabeledMatchers.hasText(value2));

        robot.interact(hud::closeWindow);
    }

}
