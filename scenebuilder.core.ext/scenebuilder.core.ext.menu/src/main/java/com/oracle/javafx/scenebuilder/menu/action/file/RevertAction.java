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
package com.oracle.javafx.scenebuilder.menu.action.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = RevertAction.MENU_ID,
        targetMenuId = SaveAsAction.MENU_ID,
        label = "menu.title.revert",
        positionRequest = PositionRequest.AsNextSibling)
public class RevertAction extends AbstractAction {

    public final static String MENU_ID = "revertMenu";

    private final Document document;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final DocumentWindow documentWindow;
    private final ActionFactory actionFactory;

    public RevertAction(
            ActionExtensionFactory extensionFactory,
            @Autowired Document document,
            @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow,
            @Autowired Dialog dialog,
            @Autowired ActionFactory actionFactory) {
        super(extensionFactory);
        this.document = document;
        this.documentManager = documentManager;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerform() {
        final OMDocument<?> omDocument = documentManager.omDocument().get();
        boolean locationSet = omDocument != null && omDocument.getLocation() != null;
        boolean dirty = documentManager.dirty().get();
        return locationSet && dirty;
    }

    @Override
    public ActionStatus doPerform() {
        final OMDocument<?> omDocument = documentManager.omDocument().get();
        assert omDocument != null;
        assert omDocument.getLocation() != null;

        final Alert d = dialog.customAlert(documentWindow.getStage());
        d.setMessage(I18N.getString("alert.revert.question.message", documentWindow.getStage().getTitle()));
        d.setDetails(I18N.getString("alert.revert.question.details"));
        d.setOKButtonTitle(I18N.getString("label.revert"));

        if (d.showAndWait() == ButtonID.OK) {
            ActionStatus result = actionFactory.create(ReloadFileAction.class).perform();
            if (result == ActionStatus.FAILED) {
                document.close();
            }
            return result;
        }

        return ActionStatus.DONE;
    }

}