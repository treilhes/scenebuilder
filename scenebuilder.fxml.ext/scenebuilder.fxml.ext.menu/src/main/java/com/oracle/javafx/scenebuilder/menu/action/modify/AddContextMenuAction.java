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
package com.oracle.javafx.scenebuilder.menu.action.modify;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.selection.job.AddContextMenuToSelectionJob;

/**
 * Performs the 'add context menu' edit action. This method creates an instance
 * of ContextMenu and sets it in the contextMenu property of the selected
 * objects.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = AddContextMenuAction.MENU_ID,
        targetMenuId = AddPopupControlMenuProvider.MENU_ID,
        label = "menu.title.add.popup.context.menu",
        positionRequest = PositionRequest.AsFirstChild)
@Deprecated
public class AddContextMenuAction extends AbstractAction {

    public final static String MENU_ID = "addContextMenuMenuItem";

    private final AddContextMenuToSelectionJob.Factory addContextMenuToSelectionJobFactory;
    private final JobManager jobManager;
    private final Selection selection;

    public AddContextMenuAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            Selection selection,
            AddContextMenuToSelectionJob.Factory addContextMenuToSelectionJobFactory) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.addContextMenuToSelectionJobFactory = addContextMenuToSelectionJobFactory;
        this.selection = selection;
    }

    /**
     * Returns true if the 'add context menu' action is permitted with the current
     * selection. In other words, returns true if the selection contains only
     * Control objects.
     *
     * @return true if the 'add context menu' action is permitted.
     */
    @Override
    public boolean canPerform() {
        return selection.isSelectionControl();
    }

    /**
     * Performs the 'add context menu' edit action. This method creates an instance
     * of ContextMenu and sets it in the contextMenu property of the selected
     * objects.
     */
    @Override
    public ActionStatus doPerform() {
        assert canPerform();
        final AbstractJob addContextMenuJob = addContextMenuToSelectionJobFactory.getJob();
        jobManager.push(addContextMenuJob);
        return ActionStatus.DONE;
    }

}