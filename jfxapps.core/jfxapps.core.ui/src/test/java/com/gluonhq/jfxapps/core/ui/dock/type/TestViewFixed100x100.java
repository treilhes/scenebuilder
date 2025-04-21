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
package com.gluonhq.jfxapps.core.ui.dock.type;

import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.TestView;

@ApplicationInstanceSingleton
@com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment(
        name = "testView2",
        id = TestViewFixed100x100.VIEW_ID,
        prefDockId = TestApp.DOCK_ID
)
public class TestViewFixed100x100 extends TestView {

    public static final String VIEW_ID = "d4e424f2-0b0f-4e51-8ff6-6a9ae42a27ae";
    public static final UUID VIEW_UUID = UUID.fromString(VIEW_ID);

    public TestViewFixed100x100(I18N i18n, ApplicationEvents scenebuilderManager, ApplicationInstanceEvents documentManager,
            ViewMenu viewMenuController) {
        super(i18n, scenebuilderManager, documentManager, viewMenuController, TestViewFixed100x100.class, """
                <?xml version="1.0" encoding="UTF-8"?>

                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.Pane?>


                <Pane maxHeight="100.0" maxWidth="100.0" minHeight="100.0" minWidth="100.0" prefHeight="100.0" prefWidth="100.0"
                style="-fx-background-color: blue;" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1">
                   <children>
                      <Button layoutX="30.0" layoutY="40.0" mnemonicParsing="false" text="Button" />
                   </children>
                </Pane>
                """);
    }
}