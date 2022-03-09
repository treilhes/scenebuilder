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
package com.oracle.javafx.scenebuilder.cssanalyser.actions;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.core.dock.DockViewController;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = ToggleCssAnalyserVisibilityAction.MENU_ID,
        targetMenuId = ToggleCssAnalyserVisibilityAction.TOGGLE_DOCUMENT_MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling)
@Accelerator(accelerator = "CTRL+6")
@Accelerator(accelerator = "CTRL+Numpad 6")
public class ToggleCssAnalyserVisibilityAction extends AbstractAction {

    /**
     * This is the menu id where the insertion will take place
     * It is a copy to prevent adding a direct dependency to FocusCodeTabAction in  scenebuilder.ext.inspector module
     * but is it the right choice, i'm wondering ?
     */
    // TODO reevaluate adding a direct dependency
    public final static String TOGGLE_DOCUMENT_MENU_ID = "toggleDocumentVisibilityMenuItem"; //NOCHECK

    public final static String MENU_ID = "toggleCssAnalyserVisibilityMenuItem"; //NOCHECK

    private final CssPanelController cssPanelController;

    private final DockViewController dockViewController;

    private DocumentWindow w;

    public ToggleCssAnalyserVisibilityAction(
            ActionExtensionFactory extensionFactory,
            DockViewController dockViewController,
            CssPanelController documentPanel,
            DocumentWindow w) {
        super(extensionFactory);
        this.cssPanelController = documentPanel;
        this.dockViewController = dockViewController;
        this.w = w;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        if (!cssPanelController.isVisible()) {
            dockViewController.performOpenView(cssPanelController);
            Dock dock = cssPanelController.parentDockProperty().get();
            dock.focusedProperty().set(cssPanelController);
        } else {
            dockViewController.performCloseView(cssPanelController);
        }

        //w.leftDockVisibleProperty().set(false);

        return ActionStatus.DONE;
    }

    public String getTitle() {
        final String title;
        if (cssPanelController.isVisible()) {
            title = "menu.title.hide.bottom.panel";
        } else {
            title = "menu.title.show.bottom.panel";
        }
        return title;
    }

}