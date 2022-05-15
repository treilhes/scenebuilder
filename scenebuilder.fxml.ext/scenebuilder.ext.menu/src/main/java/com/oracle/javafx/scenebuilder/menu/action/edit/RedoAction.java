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
package com.oracle.javafx.scenebuilder.menu.action.edit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.redo",
        descriptionKey = "action.description.redo")

@MenuItemAttachment(
        id = RedoAction.MENU_ID,
        targetMenuId = UndoAction.MENU_ID,
        label = "#this.getRedoDescription()",
        positionRequest = PositionRequest.AsNextSibling)

@Accelerator(accelerator = "CTRL+Y", macosAccelerator = "META+SHIFT+Z")

public class RedoAction extends AbstractAction {

    public final static String MENU_ID = "redoMenuItem"; //NOCHECK

    private final JobManager jobManager;
    private final DocumentManager documentManager;

    public RedoAction(
        ActionExtensionFactory extensionFactory,
        Content content,
        JobManager jobManager,
        DocumentManager documentManager) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.documentManager = documentManager;
    }

    /**
     * Returns null or the description of the action to be redone.
     *
     * @return null or the description of the action to be redone.
     */
    public String getRedoDescription() {
        final StringBuilder result = new StringBuilder();
        result.append(I18N.getStringOrDefault("menu.title.redo", "menu.title.redo"));
        if (canPerform()) {
            result.append(" "); //NOI18N
            result.append(jobManager.getRedoDescription());
        }
        return result.toString();
    }
    /**
     * Returns true if the redo action is permitted (ie there is something to be
     * undone).
     *
     * @return true if the redo action is permitted.
     */
    @Override
    public boolean canPerform() {
        return jobManager.canRedo();
    }

    /**
     * Performs the redo action.
     */
    @Override
    public ActionStatus doPerform() {
        jobManager.redo();
        assert documentManager.fxomDocument().get().isUpdateOnGoing() == false;

        return ActionStatus.DONE;
    }

}