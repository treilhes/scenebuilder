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
package com.oracle.javafx.scenebuilder.editor.fxml.actions;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.misc.Workspace;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(
        nameKey = "action.name.zoom.out",
        descriptionKey = "action.description.zoom.out")

@MenuItemAttachment(
        id = ZoomOutAction.ZOOM_OUT_MENU_ID,
        targetMenuId = ZoomInAction.ZOOM_IN_MENU_ID,
        label = "menu.title.zoom.out",
        positionRequest = PositionRequest.AsNextSibling,
        separatorAfter = true)
@Accelerator(accelerator = "CTRL+/")
public class ZoomOutAction extends AbstractAction {

    public final static String ZOOM_OUT_MENU_ID = "zoomOutMenu"; //NOCHECK

    private final Workspace workspace;

    public ZoomOutAction(ActionExtensionFactory extensionFactory, Workspace workspace) {
        super(extensionFactory);
        this.workspace = workspace;
    }

    @Override
    public boolean canPerform() {
        boolean result;
        final int currentScalingIndex = ZoomFeatureConfig.scalingTable.indexOf(workspace.getScaling());
        result = 0 <= currentScalingIndex - 1;
        return result;
    }

    @Override
    public ActionStatus doPerform() {
        assert canPerform();

        final int currentScalingIndex = ZoomFeatureConfig.scalingTable.indexOf(workspace.getScaling());
        final double newScaling = ZoomFeatureConfig.scalingTable.get(currentScalingIndex - 1);
        workspace.setScaling(newScaling);

        return ActionStatus.DONE;
    }
}