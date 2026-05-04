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
package com.oracle.javafx.scenebuilder.dummy.controller;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.subjects.ApplicationEvents;
import com.treilhes.jfxplace.core.api.subjects.ApplicationInstanceEvents;
import com.treilhes.jfxplace.core.api.ui.controller.AbstractFxmlViewController;
import com.treilhes.jfxplace.core.api.ui.controller.dock.ViewSearch;
import com.treilhes.jfxplace.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.ViewMenu;

/**
 *
 */
@ApplicationInstanceSingleton
@ViewAttachment(name = DummyWindowController.VIEW_NAME, id = DummyWindowController.VIEW_ID)
public class DummyWindowController extends AbstractFxmlViewController {

    public static final String VIEW_ID = "07a57164-de78-40f0-bb26-7c6b95afc35a";
    public static final String VIEW_NAME = "menu.title.dummy";

    //@formatter:off
    public DummyWindowController(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents applicationInstanceEvents,
            ViewMenu viewMenuController) {
        //@formatter:on
        super(i18n, applicationEvents, applicationInstanceEvents, viewMenuController,
                DummyWindowController.class.getResource("DummyWindow.fxml"));
    }

    @Override
    public void controllerDidLoadFxml() {
    }

    @Override
    public ViewSearch getSearchController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

}
