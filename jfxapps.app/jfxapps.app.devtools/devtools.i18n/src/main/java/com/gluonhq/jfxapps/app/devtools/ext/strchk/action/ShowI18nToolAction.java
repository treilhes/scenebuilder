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
package com.gluonhq.jfxapps.app.devtools.ext.strchk.action;

import com.gluonhq.jfxapps.app.devtools.api.menu.DefaultMenu;
import com.gluonhq.jfxapps.app.devtools.api.ui.MainContent;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.controller.ResourceLocationsController;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxThread;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

@ApplicationInstancePrototype
@MenuItemAttachment(
        id = ShowI18nToolAction.I18N_MENU_ID,
        targetMenuId = DefaultMenu.TOOLS_MENU_ID,
        label = "menu.title.i18n",
        positionRequest = PositionRequest.AsLastChild)
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ShowI18nToolAction extends AbstractAction {

    public static final String I18N_MENU_ID = "i18nMenu"; //NOCHECK

    private final ResourceLocationsController resourceLocationsController;
    private final MainContent mainContent;

    public ShowI18nToolAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ResourceLocationsController resourceLocationsController,
            MainContent mainContent) {
        super(i18n, extensionFactory);
        this.resourceLocationsController = resourceLocationsController;
        this.mainContent = mainContent;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    //@FxThread
    public ActionStatus doPerform() {
        if (mainContent.getRoot() instanceof Pane p) {
            p.getChildren().clear();
            p.getChildren().add(resourceLocationsController.getRoot());
        }
        return ActionStatus.DONE;
    }

}
