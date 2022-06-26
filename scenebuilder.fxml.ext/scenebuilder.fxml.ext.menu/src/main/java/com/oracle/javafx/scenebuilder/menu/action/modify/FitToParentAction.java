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
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.ContextMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectParentAction;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.FitToParentSelectionJob;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = FitToParentAction.MENU_ID,
        targetMenuId = DefaultMenu.MODIFY_MENU_ID,
        label = FitToParentAction.TITLE,
        positionRequest = PositionRequest.AsFirstChild)
@ContextMenuItemAttachment(
        selectionGroup = ObjectSelectionGroup.class,
        id = FitToParentAction.MENU_ID,
        targetMenuId = SelectParentAction.MENU_ID,
        label = FitToParentAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
@Accelerator(accelerator = "CTRL+K")
public class FitToParentAction extends AbstractAction {

    public final static String MENU_ID = "fitToParentMenuItem";
    public final static String TITLE = "menu.title.fit";

    private final FitToParentSelectionJob.Factory fitToParentSelectionJobFactory;
    private final JobManager jobManager;

    public FitToParentAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            FitToParentSelectionJob.Factory fitToParentSelectionJobFactory) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.fitToParentSelectionJobFactory = fitToParentSelectionJobFactory;
    }

    @Override
    public boolean canPerform() {
        final AbstractJob job = fitToParentSelectionJobFactory.getJob();
        return job.isExecutable();
    }

    @Override
    public ActionStatus doPerform() {
        final AbstractJob job = fitToParentSelectionJobFactory.getJob();
        jobManager.push(job);
        return ActionStatus.DONE;
    }

}