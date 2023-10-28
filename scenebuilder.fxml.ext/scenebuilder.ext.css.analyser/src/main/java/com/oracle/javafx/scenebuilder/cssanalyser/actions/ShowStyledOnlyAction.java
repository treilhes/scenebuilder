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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.css.show.styled.only",
		descriptionKey = "action.description.css.show.styled.only")
@ViewMenuItemAttachment(
        id = ShowStyledOnlyAction.MENU_ID,
        targetMenuId = CopyStyleablePathAction.MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling,
        viewClass = CssPanelController.class)
@Accelerator(accelerator = "CTRL+S", whenFocusing = CssPanelController.class)
public class ShowStyledOnlyAction extends AbstractAction {

    public final static String MENU_ID = "cssShowStyledOnlyMenu";

    private final static String LABEL_ON = "csspanel.show.default.values";
    private final static String LABEL_OFF = "csspanel.hide.default.values";

    private final CssPanelController cssPanelController;

    public ShowStyledOnlyAction(ActionExtensionFactory extensionFactory, @Lazy CssPanelController cssPanelController) {
        super(extensionFactory);
        this.cssPanelController = cssPanelController;
    }

    @Override
    public boolean canPerform() {
        return cssPanelController.getCurrentView() == CssPanelController.View.TABLE;
    }

    @Override
    public ActionStatus doPerform() {
        cssPanelController.showStyledOnly();
        return ActionStatus.DONE;
    }

    public String getTitle() {
        if (cssPanelController.isShowingStyledOnly()) {
            return I18N.getStringOrDefault(LABEL_ON, LABEL_ON);
        } else {
            return I18N.getStringOrDefault(LABEL_OFF, LABEL_OFF);
        }
    }
}