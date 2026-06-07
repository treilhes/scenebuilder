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

import com.oracle.javafx.scenebuilder.document.api.DocumentPanel;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.Action;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.shortcut.annotation.Accelerator;
import com.treilhes.jfxplace.core.api.ui.DockActionFactory;
import com.treilhes.jfxplace.core.api.ui.controller.dock.View;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = ToggleDocumentVisibilityAction.MENU_ID,
        targetMenuId = ToggleDocumentVisibilityAction.TOGGLE_LIBRARY_MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling)
@Accelerator(accelerator = "CTRL+5")
@Accelerator(accelerator = "CTRL+Numpad 5")
public class ToggleDocumentVisibilityAction  extends AbstractAction {

    /**
     * This is the menu id where the insertion will take place
     * It is a copy to prevent adding a direct dependency to FocusCodeTabAction in  scenebuilder.ext.inspector module
     * but is it the right choice, i'm wondering ?
     */
    // TODO reevaluate adding a direct dependency
    public static final String TOGGLE_LIBRARY_MENU_ID = "toggleControlLibraryVisibilityMenuItem"; //NOCHECK

    public static final String MENU_ID = "toggleDocumentVisibilityMenuItem"; //NOCHECK

    private View view;
    private Action toggleAction;

    //@formatter:off
    public ToggleDocumentVisibilityAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            DockActionFactory dockActionFactory,
            DocumentPanel documentPanel) {
        //@formatter:on
        super(i18n, extensionFactory);
        this.view = documentPanel;
        this.toggleAction = dockActionFactory.toggleViewVisibility(view.getClass());
    }

    @Override
    public boolean canPerform() {
        return toggleAction.canPerform();
    }

    @Override
    public ActionStatus doPerform() {
        return toggleAction.perform();
    }

    public String getTitle() {
        final String title;
        if (view.isVisible() && view.getParentDock() != null && !view.getParentDock().isMinimized()) {
            title = "menu.title.hide.document.panel";
        } else {
            title = "menu.title.show.document.panel";
        }
        return title;
    }

}