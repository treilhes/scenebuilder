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
package com.oracle.javafx.scenebuilder.document.actions;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.document.api.Hierarchy;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;
import com.oracle.javafx.scenebuilder.document.view.DocumentPanelController;

@ApplicationInstancePrototype
@ActionMeta(
        nameKey = "action.name.show.fx.id",
        descriptionKey = "action.description.show.fx.id")
@ViewMenuItemAttachment(
        id = ToggleExpertViewAction.MENU_ID,
        targetMenuId = HierarchyMenuProvider.MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling,
        viewClass = DocumentPanelController.class)
@Accelerator(accelerator = "SHIFT+E", whenFocusing = DocumentPanelController.class)
public class ToggleExpertViewAction extends AbstractAction {

    public final static String MENU_ID = "toggleExpertViewActionMenuItem"; //NOCHECK

    private final ShowExpertByDefaultPreference showExpertByDefaultPreference;

  //@formatter:off
    public ToggleExpertViewAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            Hierarchy hierarchy,
            ShowExpertByDefaultPreference showExpertByDefaultPreference) {
      //@formatter:on
        super(i18n, extensionFactory);
        this.showExpertByDefaultPreference = showExpertByDefaultPreference;

        showExpertByDefaultPreference.getObservableValue().addListener((ob, o, n) -> hierarchy.updatePanel());
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        showExpertByDefaultPreference.setValue(!showExpertByDefaultPreference.getValue());
        showExpertByDefaultPreference.writeToJavaPreferences();
        return ActionStatus.DONE;
    }

    public String getTitle() {
        final String title;
        if (showExpertByDefaultPreference.getValue()) {
            title = "hierarchy.hide.expert";
        } else {
            title = "hierarchy.show.expert";
        }
        return title;
    }

}