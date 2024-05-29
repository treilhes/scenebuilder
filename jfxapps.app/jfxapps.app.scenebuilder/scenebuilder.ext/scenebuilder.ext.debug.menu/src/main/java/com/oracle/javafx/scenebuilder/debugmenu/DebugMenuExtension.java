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
package com.oracle.javafx.scenebuilder.debugmenu;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.core.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.debugmenu.action.ToggleDebugViewVisibilityAction;
import com.oracle.javafx.scenebuilder.debugmenu.controller.DebugMenuController;
import com.oracle.javafx.scenebuilder.debugmenu.controller.DebugMenuWindowController;
import com.oracle.javafx.scenebuilder.debugmenu.i18n.I18NDebugMenu;
import com.oracle.javafx.scenebuilder.debugmenu.menu.DebugMenuMenuProvider;
import com.oracle.javafx.scenebuilder.debugmenu.view.DebugPreferencesView;

public class DebugMenuExtension extends AbstractExtension {
    @Override
    public UUID getId() {
        return UUID.fromString("b97afbec-1861-4b39-9cee-4bd4f541afe3");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
            DebugMenuController.class,
            DebugMenuWindowController.class,
            DebugPreferencesView.class,
            I18NDebugMenu.class,
            DebugMenuMenuProvider.class,
            DebugPreferencesView.class,
            ToggleDebugViewVisibilityAction.class,
            ToggleDebugViewVisibilityAction.ViewMenuProvider.class
            );
     // @formatter:on
    }
}
