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
import com.oracle.javafx.scenebuilder.menu.i18n.I18NDefaultMenu;
import com.oracle.javafx.scenebuilder.menu.main.MainMenuProvider;
import com.oracle.javafx.scenebuilder.menu.main.edit.CopyAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.CutAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.DeleteAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.DuplicateAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.PasteAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.PasteIntoAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.RedoAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.SelectAllAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.SelectNextAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.SelectNoneAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.SelectParentAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.SelectPreviousAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.TrimAction;
import com.oracle.javafx.scenebuilder.menu.main.edit.UndoAction;
import com.oracle.javafx.scenebuilder.menu.main.file.CloseFileAction;
import com.oracle.javafx.scenebuilder.menu.main.file.ImportFxmlAction;
import com.oracle.javafx.scenebuilder.menu.main.file.ImportMediaAction;
import com.oracle.javafx.scenebuilder.menu.main.file.ImportProvider;
import com.oracle.javafx.scenebuilder.menu.main.file.IncludeFxmlAction;
import com.oracle.javafx.scenebuilder.menu.main.file.IncludeProvider;
import com.oracle.javafx.scenebuilder.menu.main.file.IncludedEditFxmlAction;
import com.oracle.javafx.scenebuilder.menu.main.file.IncludedRevealFxmlAction;
import com.oracle.javafx.scenebuilder.menu.main.file.NewAction;
import com.oracle.javafx.scenebuilder.menu.main.file.OpenAction;
import com.oracle.javafx.scenebuilder.menu.main.file.OpenRecentProvider;
import com.oracle.javafx.scenebuilder.menu.main.file.QuitScenebuilderAction;
import com.oracle.javafx.scenebuilder.menu.main.file.RevealFxmlFileAction;
import com.oracle.javafx.scenebuilder.menu.main.file.RevertAction;
import com.oracle.javafx.scenebuilder.menu.main.file.SaveAsAction;
import com.oracle.javafx.scenebuilder.menu.main.file.SaveOrSaveAsAction;
import com.oracle.javafx.scenebuilder.menu.main.file.ShowPreferencesAction;
import com.oracle.javafx.scenebuilder.menu.main.help.ShowDocumentationAction;
import com.oracle.javafx.scenebuilder.menu.main.modify.FitToParentAction;
import com.oracle.javafx.scenebuilder.menu.main.modify.UseComputedSizeAction;
import com.oracle.javafx.scenebuilder.menu.main.view.ToggleMinimizeBottomDockAction;
import com.oracle.javafx.scenebuilder.menu.main.view.ToggleMinimizeLeftDockAction;
import com.oracle.javafx.scenebuilder.menu.main.view.ToggleMinimizeRightDockAction;
import com.oracle.javafx.scenebuilder.menu.main.view.ViewMenuProvider;
import com.oracle.javafx.scenebuilder.menu.unbound.CloseBottomDockAction;
import com.oracle.javafx.scenebuilder.menu.unbound.CloseLeftDockAction;
import com.oracle.javafx.scenebuilder.menu.unbound.CloseRightDockAction;
import com.oracle.javafx.scenebuilder.menu.view.CloseViewAction;
import com.oracle.javafx.scenebuilder.menu.view.DockTypeMenuProvider;
import com.oracle.javafx.scenebuilder.menu.view.MoveToDockMenuProvider;
import com.oracle.javafx.scenebuilder.menu.view.UndockViewAction;

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

                // TODO
                MainMenuProvider.class,
                CloseBottomDockAction.class,
                CloseLeftDockAction.class,
                CloseRightDockAction.class,

                ToggleMinimizeBottomDockAction.class,
                ToggleMinimizeLeftDockAction.class,
                ToggleMinimizeRightDockAction.class,
                ViewMenuProvider.class,

                // END TODO
                CloseFileAction.class,
                CloseViewAction.class,
                CopyAction.class,
                CutAction.class,
                DeleteAction.class,
                DockTypeMenuProvider.class,
                DuplicateAction.class,
                FitToParentAction.class,
                I18NDefaultMenu.class,
                ImportFxmlAction.class,
                ImportMediaAction.class,
                ImportProvider.class,
                IncludeFxmlAction.class,
                IncludeProvider.class,
                IncludedEditFxmlAction.class,
                IncludedRevealFxmlAction.class,
                MainMenuProvider.class,
                MoveToDockMenuProvider.class,
                NewAction.class,
                OpenAction.class,
                OpenRecentProvider.class,
                PasteAction.class,
                PasteIntoAction.class,
                QuitScenebuilderAction.class,
                RedoAction.class,
                RevealFxmlFileAction.class,
                RevertAction.class,
                SaveAsAction.class,
                SaveOrSaveAsAction.class,
                SelectAllAction.class,
                SelectNextAction.class,
                SelectNoneAction.class,
                SelectParentAction.class,
                SelectPreviousAction.class,
                ShowDocumentationAction.class,
                ShowPreferencesAction.class,
                TrimAction.class,
                UndoAction.class,
                UndockViewAction.class,
                UseComputedSizeAction.class,
                ViewMenuProvider.class
            );
     // @formatter:on
    }

}
