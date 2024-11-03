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
package com.oracle.javafx.scenebuilder.ext.theme;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.oracle.javafx.scenebuilder.ext.theme.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.actions.ApplyCssContentThemeExtension;
import com.oracle.javafx.scenebuilder.ext.theme.actions.ApplyCssContentUserStylesheetsExtension;
import com.oracle.javafx.scenebuilder.ext.theme.actions.ApplyCssContentWatchExtension;
import com.oracle.javafx.scenebuilder.ext.theme.aop.ThemeBeanPostProcessor;
import com.oracle.javafx.scenebuilder.ext.theme.aop.ThemeGroupBeanPostProcessor;
import com.oracle.javafx.scenebuilder.ext.theme.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.controller.ThemeMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.menu.ThemeMenuProvider;
import com.oracle.javafx.scenebuilder.ext.theme.menu.UserStylesheetsMenuProvider;
import com.oracle.javafx.scenebuilder.ext.theme.preference.ThemeDocumentPreference;
import com.oracle.javafx.scenebuilder.ext.theme.preference.ThemePreference;
import com.oracle.javafx.scenebuilder.ext.theme.preference.UserStylesheetsPreference;

public class SbThemeExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("100855d2-4737-4efb-975e-e07feb840fa2");

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
                ApplyCssContentAction.class,
                ApplyCssContentThemeExtension.class,
                ApplyCssContentUserStylesheetsExtension.class,
                ApplyCssContentWatchExtension.class,
                DefaultThemesList.Caspian.class,
                DefaultThemesList.CaspianEmbedded.class,
                DefaultThemesList.CaspianEmbeddedHighContrast.class,
                DefaultThemesList.CaspianEmbeddedQvga.class,
                DefaultThemesList.CaspianEmbeddedQvgaHighContrast.class,
                DefaultThemesList.CaspianHighContrast.class,
                DefaultThemesList.Modena.class,
                DefaultThemesList.ModenaHighContrastBlackOnWhite.class,
                DefaultThemesList.ModenaHighContrastWhiteOnBlack.class,
                DefaultThemesList.ModenaHighContrastYellowOnBlack.class,
                DefaultThemesList.ModenaTouch.class,
                DefaultThemesList.ModenaTouchHighContrastBlackOnWhite.class,
                DefaultThemesList.ModenaTouchHighContrastWhiteOnBlack.class,
                DefaultThemesList.ModenaTouchHighContrastYellowOnBlack.class,
                SceneStyleSheetMenuController.class,
                ThemeBeanPostProcessor.class,
                ThemeGroupBeanPostProcessor.class,
                ThemeManagerImpl.class,
                ThemeMenuController.class,
                ThemeMenuProvider.class,
                ThemeDocumentPreference.class,
                ThemePreference.class,
                UserStylesheetsMenuProvider.class,
                UserStylesheetsPreference.class
            );
     // @formatter:on
    }
}
