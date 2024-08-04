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
package com.gluonhq.jfxapps.ext.menu;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.gluonhq.jfxapps.ext.menu.action.LoadBlankAction;
import com.gluonhq.jfxapps.ext.menu.action.LoadFileAction;
import com.gluonhq.jfxapps.ext.menu.action.LoadUrlAction;
import com.gluonhq.jfxapps.ext.menu.action.OpenFilesAction;
import com.gluonhq.jfxapps.ext.menu.action.SaveAction;
import com.gluonhq.jfxapps.ext.menu.action.edit.RedoAction;
import com.gluonhq.jfxapps.ext.menu.action.edit.UndoAction;
import com.gluonhq.jfxapps.ext.menu.action.file.ClearRecentItemsAction;
import com.gluonhq.jfxapps.ext.menu.action.file.CloseFileAction;
import com.gluonhq.jfxapps.ext.menu.action.file.NewAction;
import com.gluonhq.jfxapps.ext.menu.action.file.OpenAction;
import com.gluonhq.jfxapps.ext.menu.action.file.OpenRecentProvider;
import com.gluonhq.jfxapps.ext.menu.action.file.QuitScenebuilderAction;
import com.gluonhq.jfxapps.ext.menu.action.file.ReloadFileAction;
import com.gluonhq.jfxapps.ext.menu.action.file.RevertAction;
import com.gluonhq.jfxapps.ext.menu.action.file.SaveAsAction;
import com.gluonhq.jfxapps.ext.menu.action.file.SaveOrSaveAsAction;
import com.gluonhq.jfxapps.ext.menu.action.view.ToggleViewVisibilityAction;
import com.gluonhq.jfxapps.ext.menu.i18n.I18NDefaultMenu;
import com.gluonhq.jfxapps.ext.menu.viewmenu.ChangeDockTypeAction;
import com.gluonhq.jfxapps.ext.menu.viewmenu.CloseViewAction;
import com.gluonhq.jfxapps.ext.menu.viewmenu.MoveToDockAction;
import com.gluonhq.jfxapps.ext.menu.viewmenu.UndockViewAction;

public class DefaultMenuExtension implements OpenExtension {
    @Override
    public UUID getId() {
        return UUID.fromString("1efa32d5-0673-4f6e-bb0b-7a57514e9cba");
    }

    @Override
    public UUID getParentId() {
        return OpenExtension.ROOT_ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                ChangeDockTypeAction.class,
                ChangeDockTypeAction.MenuProvider.class,
                ClearRecentItemsAction.class,

                CloseFileAction.class,

                CloseViewAction.class,
                I18NDefaultMenu.class,
                LoadBlankAction.class,
                NewAction.class,
                LoadFileAction.class,
                LoadUrlAction.class,
                MoveToDockAction.class,
                MoveToDockAction.MenuProvider.class,
                OpenFilesAction.class,
                QuitScenebuilderAction.class,
                RedoAction.class,
                ReloadFileAction.class,
                RevertAction.class,
                SaveAction.class,
                SaveAsAction.class,
                SaveOrSaveAsAction.class,
                ToggleViewVisibilityAction.class,
                ToggleViewVisibilityAction.ViewMenuProvider.class,
                OpenAction.class,
                OpenRecentProvider.class,
                UndoAction.class,
                UndockViewAction.class
            );
     // @formatter:on
    }
}
