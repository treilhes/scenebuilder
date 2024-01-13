/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.imagelibrary.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.imagelibrary.preferences.global.ImageDisplayModePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.view.as.list",
		descriptionKey = "action.description.view.as.list")
@ViewMenuItemAttachment(
        id = ViewImageAsListAction.MENU_ID,
        label = "library.panel.menu.view.list",
        positionRequest = PositionRequest.AsLastChild,
        viewClass = ImageLibraryPanelController.class,
        toggleClass = ViewImageAsToggle.class)
@Accelerator(accelerator = "CTRL+L", whenFocusing = ImageLibraryPanelController.class)
public class ViewImageAsListAction extends AbstractAction {

    public final static String MENU_ID = "viewAsListMenu";

    private final ImageLibraryPanelController libraryPanelController;
    private final ImageDisplayModePreference displayModePreference;

    public ViewImageAsListAction(ActionExtensionFactory extensionFactory,
            @Autowired @Lazy ImageLibraryPanelController libraryPanelController,
            @Autowired @Lazy ImageDisplayModePreference displayModePreference) {
        super(extensionFactory);
        this.libraryPanelController = libraryPanelController;
        this.displayModePreference = displayModePreference;
    }

    @Override
    public boolean canPerform() {
        return libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.LIST;
    }

    @Override
    public ActionStatus doPerform() {
        if (libraryPanelController.getDisplayMode() != ImageLibraryPanelController.DISPLAY_MODE.SEARCH) {
            libraryPanelController.setDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.LIST);
        } else {
            libraryPanelController.setPreviousDisplayMode(ImageLibraryPanelController.DISPLAY_MODE.LIST);
        }

        displayModePreference.setValue(libraryPanelController.getDisplayMode()).writeToJavaPreferences();
        return ActionStatus.DONE;
    }
}