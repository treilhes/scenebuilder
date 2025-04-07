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
package com.gluonhq.jfxapps.app.manager.store.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.api.ui.Docks;
import com.gluonhq.jfxapps.app.manager.store.ui.component.Switch;
import com.gluonhq.jfxapps.app.manager.store.ui.component.SwitchFactory;
import com.gluonhq.jfxapps.app.manager.store.ui.root.RootController;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

@ApplicationInstanceSingleton
@ViewAttachment(
        name = "Store",
        id = "5dd924ad-f410-4612-bb23-196c5b672441",
        prefDockId = Docks.CENTER_DOCK_ID,
        openOnStart = true,
        selectOnStart = false,
        order = 4000,
        icon = "openapi_tool.png",
        iconX2 = "openapi_tool@2x.png"
        )
public class StoreController extends AbstractFxmlViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);

    public static final String SWITCH_ID = "StoreSwitch";

    @FXML
    private StackPane rootPane;

    private final RootController rootController;

    private Switch switcher;

    //@formatter:off
    protected StoreController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenu,
            RootController rootController,
            JfxAppContext context,
            SwitchFactory switchFactory) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenu, StoreController.class.getResource("Store.fxml"));

        this.rootController = rootController;
        this.switcher = switchFactory.getSwitch(SWITCH_ID);
    }

    @FXML
    private void initialize() {
        switcher.attach(rootPane);
    }

    public void back() {
        switcher.back();
    }

    @Override
    public void onShow() {
        rootController.load();

        switcher.reset();
        switcher.next(rootController.getRoot());
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

}
