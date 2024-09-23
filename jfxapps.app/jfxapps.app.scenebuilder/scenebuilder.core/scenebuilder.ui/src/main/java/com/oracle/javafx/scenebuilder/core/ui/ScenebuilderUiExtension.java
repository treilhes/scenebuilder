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
package com.oracle.javafx.scenebuilder.core.ui;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.scenebuilder.fxml.api.SbApiExtension;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.core.ui.action.CloseBottomDockAction;
import com.oracle.javafx.scenebuilder.core.ui.action.CloseLeftDockAction;
import com.oracle.javafx.scenebuilder.core.ui.action.CloseRightDockAction;
import com.oracle.javafx.scenebuilder.core.ui.action.ToggleMinimizeBottomDockAction;
import com.oracle.javafx.scenebuilder.core.ui.action.ToggleMinimizeLeftDockAction;
import com.oracle.javafx.scenebuilder.core.ui.action.ToggleMinimizeRightDockAction;
import com.oracle.javafx.scenebuilder.core.ui.i18n.I18NScenebuilderUiExtension;
import com.oracle.javafx.scenebuilder.core.ui.menu.MainMenuProvider;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.RightDividerHPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.settings.WindowIconSetting;
import com.oracle.javafx.scenebuilder.core.ui.template.DocumentWindowController;

public class ScenebuilderUiExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("86de1209-5c5e-41a5-941b-1484fadccdd6");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                BottomDividerVPosPreference.class,
                CloseBottomDockAction.class,
                CloseLeftDockAction.class,
                CloseRightDockAction.class,
                DocumentWindowController.class,
                I18NScenebuilderUiExtension.class,
                LeftDividerHPosPreference.class,
                MainMenuProvider.class,
                RightDividerHPosPreference.class,
                ToggleMinimizeBottomDockAction.class,
                ToggleMinimizeLeftDockAction.class,
                ToggleMinimizeRightDockAction.class,
                WindowIconSetting.class
            );
     // @formatter:on
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

}
