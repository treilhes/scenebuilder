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
package com.gluonhq.jfxapps.app.devtools.db.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.devtools.api.ui.Docks;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.web.client.InternalRestClient;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

@ApplicationInstanceSingleton
@ViewAttachment(
        name = "H2 Database Console",
        id = "66d7dbd7-0eac-4e3d-a723-97507393b934",
        prefDockId = Docks.CENTER_DOCK_ID,
        openOnStart = false,
        selectOnStart = false,
        order = 5000,
        icon = "db_tool.png",
        iconX2 = "db_tool@2x.png"
        )
public class DatabaseConsoleController extends AbstractFxmlViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConsoleController.class);
    private static final String H2_CONSOLE_PATH = "h2-console";

    @FXML
    VBox rootVbox;

    @FXML
    TextField consoleUrl;

    @FXML
    WebView webView;

    private final InternalRestClient restClient;

    //@formatter:off
    protected DatabaseConsoleController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenu,
            InternalRestClient restClient,
            ModuleLayerManager moduleLayerManager,
            ContextManager contextManager) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenu, DatabaseConsoleController.class.getResource("DatabaseConsole.fxml"));

        this.restClient = restClient;

    }

    @FXML
    public void initialize() {

    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(DatabaseConsoleController.class.getSimpleName());
        getRoot().minWidth(400.0);
        getRoot().minHeight(400.0);
    }

    @Override
    public void onShow() {
        showApi();
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

    private void showApi() {
        String uri = getUri();
        consoleUrl.setText(uri.toString());
        webView.getEngine().load(uri.toString());
    }

    private String getUri() {
        return restClient.rootUri() + "/" + H2_CONSOLE_PATH;
    }

}
