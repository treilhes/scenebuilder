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
package com.oracle.javafx.scenebuilder.ext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.ComponentScan;

import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentThemeExtension;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentUserStylesheetsExtension;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentWatchExtension;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyI18nContentAction;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyI18nContentResourceExtension;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyI18nContentWatchExtension;
import com.oracle.javafx.scenebuilder.ext.controller.I18nResourceMenuController;
import com.oracle.javafx.scenebuilder.ext.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.controller.ThemeMenuController;
import com.oracle.javafx.scenebuilder.ext.menu.I18nMenuProvider;
import com.oracle.javafx.scenebuilder.ext.menu.ThemeMenuProvider;
import com.oracle.javafx.scenebuilder.ext.menu.UserStylesheetsMenuProvider;
import com.oracle.javafx.scenebuilder.ext.theme.DefaultThemesList;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;
import com.oracle.javafx.scenebuilder.ext.theme.global.ThemePreference;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;

@ComponentScan(basePackages = { 
        "com.oracle.javafx.scenebuilder.ext.theme"
        })
public class BasicThemeAndResourceExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("98163b4e-12c5-4f59-bee6-bbbbf619bcd5");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
            ApplyCssContentAction.class,
            ApplyCssContentThemeExtension.class,
            ApplyCssContentUserStylesheetsExtension.class,
            ApplyCssContentWatchExtension.class,
            ApplyI18nContentAction.class,
            ApplyI18nContentResourceExtension.class,
            ApplyI18nContentWatchExtension.class,
            I18nResourceMenuController.class,
            SceneStyleSheetMenuController.class,
            ThemeMenuController.class,
            I18nMenuProvider.class,
            ThemeMenuProvider.class,
            UserStylesheetsMenuProvider.class,
            I18NResourcePreference.class,
            com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference.class,
            UserStylesheetsPreference.class,
            ThemePreference.class,
            DefaultThemesList.class
            );
     // @formatter:on
    }
}
