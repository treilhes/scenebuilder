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

import org.scenebuilder.fxml.api.SbEditor;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
@MenuItemAttachment(
        id = ToggleSampleDataAction.MENU_ID,
        targetMenuId = ZoomAction.ZOOM_MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
public class ToggleSampleDataAction extends AbstractAction {

    public final static String MENU_ID = "toggleSampleDataMenu";

    private final ApplicationInstanceEvents documentManager;
    private final SbEditor editor;

    //@formatter:off
    public ToggleSampleDataAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            SbEditor editor) {
        //@formatter:on
        super(i18n, extensionFactory);
        this.documentManager = documentManager;
        this.editor = editor;
    }

    @Override
    public boolean canPerform() {
        return documentManager.fxomDocument().get() != null;
    }

    @Override
    public ActionStatus doPerform() {
        boolean sampleDataEnabled = editor.isSampleDataEnabled();
        editor.setSampleDataEnabled(!sampleDataEnabled);
        return ActionStatus.DONE;
    }

    public String getTitle() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        final String titleKey;
        if (fxomDocument != null && fxomDocument.isSampleDataEnabled()) {
            titleKey = "menu.title.hide.sample.data";
        } else {
            titleKey = "menu.title.show.sample.data";
        }
        return getI18n().getString(titleKey);
    }
}