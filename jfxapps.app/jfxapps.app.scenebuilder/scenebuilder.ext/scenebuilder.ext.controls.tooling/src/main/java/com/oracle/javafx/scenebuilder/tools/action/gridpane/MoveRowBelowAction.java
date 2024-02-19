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
package com.oracle.javafx.scenebuilder.tools.action.gridpane;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ContextMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveRowJob;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = MoveRowBelowAction.MENU_ID,
        targetMenuId = MoveRowAboveAction.MENU_ID,
        label = MoveRowBelowAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@ContextMenuItemAttachment(
        selectionGroup = GridSelectionGroup.class,
        id = MoveRowBelowAction.MENU_ID,
        targetMenuId = MoveRowAboveAction.MENU_ID,
        label = MoveRowBelowAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
public class MoveRowBelowAction extends AbstractAction {

    public final static String MENU_ID = "moveRowBelowMenuItem";
    public final static String TITLE = "menu.title.grid.move.row.below";

    private final MoveRowJob.Factory moveRowJobFactory;
    private final JobManager jobManager;

    public MoveRowBelowAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            MoveRowJob.Factory moveRowJobFactory) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.moveRowJobFactory = moveRowJobFactory;
    }

    @Override
    public boolean canPerform() {
        final AbstractJob job = moveRowJobFactory.getJob(Position.ABOVE);
        return job.isExecutable();
    }

    @Override
    public ActionStatus doPerform() {
        final AbstractJob job = moveRowJobFactory.getJob(Position.ABOVE);
        jobManager.push(job);
        return ActionStatus.DONE;
    }

}