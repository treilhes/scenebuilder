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
package com.oracle.javafx.scenebuilder.document.actions;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.menu.annotation.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.document.actions.AbstractShowAction.ShowActionToggle;
import com.oracle.javafx.scenebuilder.document.api.DocumentPanel;
import com.oracle.javafx.scenebuilder.document.api.Hierarchy;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.NodeIdDisplayOption;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;
import com.oracle.javafx.scenebuilder.document.view.DocumentPanelController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.show.node.id",
		descriptionKey = "action.description.show.node.id")
@ViewMenuItemAttachment(
        id = ShowNodeIdAction.MENU_ID,
        targetMenuId = ShowFxIdAction.MENU_ID,
        label = "hierarchy.show.nodeid",
        positionRequest = PositionRequest.AsNextSibling,
        viewClass = DocumentPanelController.class,
        toggleClass = ShowActionToggle.class)
@Accelerator(accelerator = "SHIFT+N", whenFocusing = DocumentPanelController.class)
public class ShowNodeIdAction extends AbstractShowAction {
    public final static String MENU_ID = "showNodeMenu";

	public ShowNodeIdAction(
	        ActionExtensionFactory extensionFactory,
	        DocumentPanel documentPanelController,
	        Hierarchy hierarchyPanel,
	        NodeIdDisplayOption nodeIdDisplayOption,
            DisplayOptionPreference displayOptionPreference) {
		super(extensionFactory, nodeIdDisplayOption, documentPanelController, hierarchyPanel, displayOptionPreference);
	}
}