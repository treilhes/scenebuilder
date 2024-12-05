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
package com.gluonhq.jfxapps.app.devtools.app.ui;

import com.gluonhq.jfxapps.app.devtools.api.ui.Docks;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class DevtoolsUiTemplate extends AbstractFxmlWindowController implements MainInstanceWindow {

    private final MenuBar menuBar;
    private final Dock centerDock;
    private final ViewLinks viewLinks;

    @FXML
    private AnchorPane contentHost;
    @FXML
    private HBox hBox;



    // @formatter:off
    public DevtoolsUiTemplate(
            I18N i18n,
            ApplicationEvents sceneBuilderManager,
            IconSetting iconSetting,
            DockFactory dockFactory,
            MenuBar menuBar,
            ViewLinks viewLinks) {
        super(i18n, sceneBuilderManager, iconSetting, DevtoolsUiTemplate.class.getResource("DevtoolsUiTemplate.fxml"), false);
        // @formatter:on

        this.menuBar = menuBar;
        this.viewLinks = viewLinks;
        this.centerDock = dockFactory.create(Docks.CENTER_DOCK_UUID, "Center");
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert getRoot() instanceof VBox;
    }


    @Override
    public void composeWindow() {
        final VBox rootVBox = (VBox) getRoot();
        rootVBox.getChildren().add(0, menuBar.getMenuBar());

        hBox.getChildren().add(0, viewLinks.getRoot());

        var content = centerDock.getContent();
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        contentHost.getChildren().add(content);
    }

    @Override
    public void setMainKeyPressedEvent(EventHandler<KeyEvent> mainKeyEventFilter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateStageTitle() {
        // TODO Auto-generated method stub

    }

}
