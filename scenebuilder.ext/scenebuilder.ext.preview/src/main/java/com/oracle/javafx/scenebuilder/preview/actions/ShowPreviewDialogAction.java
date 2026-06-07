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
package com.oracle.javafx.scenebuilder.preview.actions;

import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;

import javafx.scene.control.DialogPane;

//@formatter:off
@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.preview", descriptionKey = "action.description.show.preview")
@MenuItemAttachment(
        id = ShowPreviewDialogAction.SHOW_PREVIEW_IN_DIALOG_ID,
        targetMenuId = ShowPreviewAction.SHOW_PREVIEW_IN_WINDOW_ID,
        label = "menu.title.show.preview.in.dialog",
        positionRequest = PositionRequest.AsNextSibling)
//@formatter:on
public class ShowPreviewDialogAction extends AbstractAction {

    public static final String SHOW_PREVIEW_IN_DIALOG_ID = "showPreviewInDialog";

    private final FxomEvents fxomEvents;
    private final PreviewWindowController previewWindowController;

    //@formatter:off
    public ShowPreviewDialogAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            PreviewWindowController previewWindowController,
            FxomEvents fxomEvents) {
        //@formatter:on
        super(i18n, extensionFactory);
        this.fxomEvents = fxomEvents;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public boolean canPerform() {
        FXOMDocument fd = fxomEvents.fxomDocument().get();
        return fd != null && fd.getSceneGraphRoot() instanceof DialogPane;
    }

    @Override
    public ActionStatus doPerform() {
        previewWindowController.openDialog();
        return ActionStatus.DONE;
    }
}