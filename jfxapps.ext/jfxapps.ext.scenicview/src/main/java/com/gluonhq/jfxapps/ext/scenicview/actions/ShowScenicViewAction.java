/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.ext.scenicview.actions;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.di.SbPlatform;
import com.gluonhq.jfxapps.core.api.ui.menu.DefaultMenu;
import com.gluonhq.jfxapps.core.api.ui.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.ext.scenicview.controller.ScenicViewStarter;

@Prototype
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = ShowScenicViewAction.SCENICVIEW_MENU_ID,
        targetMenuId = DefaultMenu.HELP_MENU_ID,
        label = "menu.title.scenicview",
        positionRequest = PositionRequest.AsLastChild)
public class ShowScenicViewAction extends AbstractAction {

    public final static String SCENICVIEW_MENU_ID = "scenicMenu"; //NOCHECK

    private final JfxAppContext context;

    public ShowScenicViewAction(
            ActionExtensionFactory extensionFactory,
            JfxAppContext context) {
        super(extensionFactory);
        this.context = context;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        //TODO allow a more easy usage of scenic
        // Show ScenicView Tool when the JVM is started with option -Dscenic.
        // NetBeans: set it on [VM Options] line in [Run] category of project's Properties.
        //if (System.getProperty("scenic") != null) //NOCHECK
        {
            SbPlatform.runOnFxThread(new ScenicViewStarter(context));
        }
        return ActionStatus.DONE;
    }
}