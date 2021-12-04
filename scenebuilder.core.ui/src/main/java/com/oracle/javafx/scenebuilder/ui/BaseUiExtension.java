/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.ui;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.ui.controller.DocumentController;
import com.oracle.javafx.scenebuilder.ui.controller.DocumentWindowController;
import com.oracle.javafx.scenebuilder.ui.controller.MainController;
import com.oracle.javafx.scenebuilder.ui.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.ui.dialog.DialogController;
import com.oracle.javafx.scenebuilder.ui.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.ui.dialog.TextViewDialog;
import com.oracle.javafx.scenebuilder.ui.editor.messagelog.MessageLog;
import com.oracle.javafx.scenebuilder.ui.i18n.I18NLayout;
import com.oracle.javafx.scenebuilder.ui.inlineedit.InlineEditController;
import com.oracle.javafx.scenebuilder.ui.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.ui.message.MessageBarController;
import com.oracle.javafx.scenebuilder.ui.message.MessagePanelController;
import com.oracle.javafx.scenebuilder.ui.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.MaximizedPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.PathPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageHeightPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.StageWidthPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.XPosPreference;
import com.oracle.javafx.scenebuilder.ui.preferences.document.YPosPreference;
import com.oracle.javafx.scenebuilder.ui.selectionbar.SelectionBarController;

public class BaseUiExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("cc8b28e8-b070-4cbd-8558-eff205a28cf1");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                I18NLayout.class,
                MainController.class,
                DocumentWindowController.class,
                MenuBarController.class,
                MessageBarController.class,
                MessagePanelController.class,
                SelectionBarController.class, 
                MessageLog.class,
                BottomDividerVPosPreference.class,
                LeftDividerHPosPreference.class,
                MaximizedPreference.class,
                RightDividerHPosPreference.class,
                StageHeightPreference.class,
                StageWidthPreference.class,
                XPosPreference.class,
                YPosPreference.class,
                AlertDialog.class,
                DialogController.class,
                ErrorDialog.class,
                TextViewDialog.class,
                InlineEditController.class,
                DocumentController.class,
                PathPreference.class
            );
     // @formatter:on
    }
}