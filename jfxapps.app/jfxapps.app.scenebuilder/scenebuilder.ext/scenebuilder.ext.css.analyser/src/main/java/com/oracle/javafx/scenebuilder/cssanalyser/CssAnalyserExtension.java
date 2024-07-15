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
package com.oracle.javafx.scenebuilder.cssanalyser;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.scenebuilder.fxml.api.SbApiExtension;

import com.gluonhq.jfxapps.boot.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.CopyStyleablePathAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.CssViewAsMenuProvider;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.CssViewToggle;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.ShowStyledOnlyAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.SplitDefaultsAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.ToggleCssAnalyserVisibilityAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.ViewRulesAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.ViewTableAction;
import com.oracle.javafx.scenebuilder.cssanalyser.actions.ViewTextAction;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelDelegate;
import com.oracle.javafx.scenebuilder.cssanalyser.i18n.I18NCssAnalyser;
import com.oracle.javafx.scenebuilder.cssanalyser.mode.PickModeController;
import com.oracle.javafx.scenebuilder.cssanalyser.preferences.global.CssTableColumnsOrderingReversedPreference;

public class CssAnalyserExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("3155d7db-8df0-466c-b19a-8a8b9204fcb4");

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
            I18NCssAnalyser.class,
            CssPanelController.class,
            CssTableColumnsOrderingReversedPreference.class,
            CssAnalyserModeProvider.class,
            CssPanelDelegate.class,
            CssViewAsMenuProvider.class,
            CssViewToggle.class,
            PickModeController.class,
            CopyStyleablePathAction.class,
            ShowStyledOnlyAction.class,
            SplitDefaultsAction.class,
            ToggleCssAnalyserVisibilityAction.class,
            ViewRulesAction.class,
            ViewTableAction.class,
            ViewTextAction.class
            );
     // @formatter:on
    }
}
