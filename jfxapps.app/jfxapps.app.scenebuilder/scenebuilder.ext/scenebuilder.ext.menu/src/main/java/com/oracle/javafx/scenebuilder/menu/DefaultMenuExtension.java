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
package com.oracle.javafx.scenebuilder.menu;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.oracle.javafx.scenebuilder.menu.action.LoadBlankAction;
import com.oracle.javafx.scenebuilder.menu.action.LoadFileAction;
import com.oracle.javafx.scenebuilder.menu.action.LoadUrlAction;
import com.oracle.javafx.scenebuilder.menu.action.OpenFilesAction;
import com.oracle.javafx.scenebuilder.menu.action.SaveAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.CopyAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.CutAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.DeleteAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.DuplicateAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.PasteAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.PasteIntoAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.RedoAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectAllAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectNextAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectNoneAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectParentAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.SelectPreviousAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.TrimAction;
import com.oracle.javafx.scenebuilder.menu.action.edit.UndoAction;
import com.oracle.javafx.scenebuilder.menu.action.file.ClearRecentItemsAction;
import com.oracle.javafx.scenebuilder.menu.action.file.CloseFileAction;
import com.oracle.javafx.scenebuilder.menu.action.file.EditIncludedFxmlAction;
import com.oracle.javafx.scenebuilder.menu.action.file.ImportFxmlAction;
import com.oracle.javafx.scenebuilder.menu.action.file.ImportMediaAction;
import com.oracle.javafx.scenebuilder.menu.action.file.ImportProvider;
import com.oracle.javafx.scenebuilder.menu.action.file.IncludeFxmlAction;
import com.oracle.javafx.scenebuilder.menu.action.file.IncludeProvider;
import com.oracle.javafx.scenebuilder.menu.action.file.NewAction;
import com.oracle.javafx.scenebuilder.menu.action.file.OpenAction;
import com.oracle.javafx.scenebuilder.menu.action.file.OpenRecentProvider;
import com.oracle.javafx.scenebuilder.menu.action.file.QuitScenebuilderAction;
import com.oracle.javafx.scenebuilder.menu.action.file.ReloadFileAction;
import com.oracle.javafx.scenebuilder.menu.action.file.RevealFxmlFileAction;
import com.oracle.javafx.scenebuilder.menu.action.file.RevealIncludedFxmlAction;
import com.oracle.javafx.scenebuilder.menu.action.file.RevertAction;
import com.oracle.javafx.scenebuilder.menu.action.file.SaveAsAction;
import com.oracle.javafx.scenebuilder.menu.action.file.SaveOrSaveAsAction;
import com.oracle.javafx.scenebuilder.menu.action.modify.AddContextMenuAction;
import com.oracle.javafx.scenebuilder.menu.action.modify.AddPopupControlMenuProvider;
import com.oracle.javafx.scenebuilder.menu.action.modify.AddTooltipAction;
import com.oracle.javafx.scenebuilder.menu.action.modify.FitToParentAction;
import com.oracle.javafx.scenebuilder.menu.action.modify.UseComputedSizeAction;
import com.oracle.javafx.scenebuilder.menu.action.view.CloseBottomDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.CloseLeftDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.CloseRightDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.ToggleMinimizeBottomDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.ToggleMinimizeLeftDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.ToggleMinimizeRightDockAction;
import com.oracle.javafx.scenebuilder.menu.action.view.ToggleViewVisibilityAction;
import com.oracle.javafx.scenebuilder.menu.i18n.I18NDefaultMenu;
import com.oracle.javafx.scenebuilder.menu.main.MainMenuProvider;
import com.oracle.javafx.scenebuilder.menu.viewmenu.ChangeDockTypeAction;
import com.oracle.javafx.scenebuilder.menu.viewmenu.CloseViewAction;
import com.oracle.javafx.scenebuilder.menu.viewmenu.MoveToDockAction;
import com.oracle.javafx.scenebuilder.menu.viewmenu.UndockViewAction;

public class DefaultMenuExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("1efa32d5-0673-4f6e-bb0b-7a57514e9cba");

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                AddPopupControlMenuProvider.class,
                AddContextMenuAction.class,
                AddTooltipAction.class,
                ChangeDockTypeAction.class,
                ChangeDockTypeAction.MenuProvider.class,
                ClearRecentItemsAction.class,
                CloseBottomDockAction.class,
                CloseFileAction.class,
                CloseLeftDockAction.class,
                CloseRightDockAction.class,
                CloseViewAction.class,
                CopyAction.class,
                CutAction.class,
                DeleteAction.class,
                DuplicateAction.class,
                EditIncludedFxmlAction.class,
                FitToParentAction.class,
                I18NDefaultMenu.class,
                ImportProvider.class,
                ImportFxmlAction.class,
                ImportMediaAction.class,
                IncludeProvider.class,
                IncludeFxmlAction.class,
                LoadBlankAction.class,
                NewAction.class,
                LoadFileAction.class,
                LoadUrlAction.class,
                MainMenuProvider.class,
                MoveToDockAction.class,
                MoveToDockAction.MenuProvider.class,
                OpenFilesAction.class,
                PasteAction.class,
                PasteIntoAction.class,
                QuitScenebuilderAction.class,
                RedoAction.class,
                ReloadFileAction.class,
                RevealFxmlFileAction.class,
                RevealIncludedFxmlAction.class,
                RevertAction.class,
                SaveAction.class,
                SaveAsAction.class,
                SaveOrSaveAsAction.class,
                SelectAllAction.class,
                SelectNextAction.class,
                SelectParentAction.class,
                SelectNoneAction.class,
                SelectPreviousAction.class,
                ToggleMinimizeBottomDockAction.class,
                ToggleMinimizeLeftDockAction.class,
                ToggleMinimizeRightDockAction.class,
                ToggleViewVisibilityAction.class,
                ToggleViewVisibilityAction.ViewMenuProvider.class,
                TrimAction.class,
                OpenAction.class,
                OpenRecentProvider.class,
                UndoAction.class,
                UndockViewAction.class,
                UseComputedSizeAction.class
            );
     // @formatter:on
    }

}
