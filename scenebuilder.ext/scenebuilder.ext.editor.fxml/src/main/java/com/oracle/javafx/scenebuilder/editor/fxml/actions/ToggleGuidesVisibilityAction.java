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
package com.oracle.javafx.scenebuilder.editor.fxml.actions;

import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.Action;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.guide.GuideActionFactory;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.toggle.guides.visibility", descriptionKey = "action.description.toggle.guides.visibility")

@MenuItemAttachment(
        id = DefaultMenu.View.GUIDES_TOGGLE_ID,
        targetMenuId = DefaultMenu.View.OUTLINES_TOGGLE_ID,
        label = "#this.getToggleTitle()", // NOCHECK
        positionRequest = PositionRequest.AsNextSibling,
        separatorAfter = true)
public class ToggleGuidesVisibilityAction extends AbstractAction {

    private final Action disableAction;
    private final Action enableAction;

    //@formatter:off
    public ToggleGuidesVisibilityAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            GuideActionFactory guideActionFactory) {
        //@formatter:on
        super(i18n, extensionFactory);
        this.disableAction = guideActionFactory.disable();
        this.enableAction = guideActionFactory.enable();
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        if (disableAction.canPerform()) {
            return disableAction.perform();
        } else if (enableAction.canPerform()) {
            return enableAction.perform();
        }
        return ActionStatus.FAILED;
    }


    public String getToggleTitle() {
        if (disableAction.canPerform()) {
            return "menu.title.disable.guides";
        } else {
            return "menu.title.enable.guides";
        }
    }
}