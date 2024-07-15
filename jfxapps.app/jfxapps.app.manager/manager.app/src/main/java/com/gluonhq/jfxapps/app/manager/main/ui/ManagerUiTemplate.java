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
package com.gluonhq.jfxapps.app.manager.main.ui;

import java.util.UUID;

import com.gluonhq.jfxapps.app.manager.api.ui.MainContent;
import com.gluonhq.jfxapps.app.manager.preferences.document.BottomDividerVPosPreference;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.WindowPreferenceTracker;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock.Orientation;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

import jakarta.inject.Provider;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class ManagerUiTemplate extends AbstractFxmlWindowController implements MainInstanceWindow {

    public static final String BOTTOM_DOCK_ID = "e8a0168d-f074-47e7-b107-aa7302a27cf8";
    public static final UUID BOTTOM_DOCK_UUID = UUID.fromString(BOTTOM_DOCK_ID);

    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private StackPane contentPanelHost;
    @FXML
    private VBox bottomHost;

    private final Dock bottomDockController;

    private final MenuBar menuBar;
    private final MainContent mainContent;
    private final WindowPreferenceTracker tracker;
    private final Provider<BottomDividerVPosPreference> bottomDividerVPos;

    // @formatter:off
    public ManagerUiTemplate(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            Provider<BottomDividerVPosPreference> bottomDividerVPos,
            Dock bottomDockController,
            MenuBar menuBar,
            MainContent mainContent,
            WindowPreferenceTracker tracker) {
        super(sceneBuilderManager, iconSetting, ManagerUiTemplate.class.getResource("ManagerUiTemplate.fxml"), I18N.getBundle(), false);
        // @formatter:on

        this.bottomDockController = bottomDockController;
        this.bottomDockController.setId(BOTTOM_DOCK_UUID);
        this.bottomDockController.setMinimizedOrientation(Orientation.HORIZONTAL);
        this.bottomDockController.setMinimized(true);
        this.bottomDockController.notifyDockCreated();

        // preferences
        this.bottomDividerVPos = bottomDividerVPos;

        this.menuBar = menuBar;
        this.mainContent = mainContent;
        this.tracker = tracker;
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void controllerDidLoadFxml() {
        assert getRoot() instanceof VBox;
        final VBox rootVBox = (VBox) getRoot();
        rootVBox.getChildren().add(0, menuBar.getMenuBar());
        contentPanelHost.getChildren().add(mainContent.getRoot());
        tracker.initialize(this);;
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
