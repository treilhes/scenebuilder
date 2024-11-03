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
package com.oracle.javafx.scenebuilder.ext.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.oracle.javafx.scenebuilder.ext.i18n.actions.ApplyI18nContentAction;
import com.oracle.javafx.scenebuilder.ext.i18n.actions.ApplyI18nContentResourceExtension;
import com.oracle.javafx.scenebuilder.ext.i18n.actions.ApplyI18nContentWatchExtension;
import com.oracle.javafx.scenebuilder.ext.i18n.controller.I18nResourceMenuController;
import com.oracle.javafx.scenebuilder.ext.i18n.menu.I18nMenuProvider;
import com.oracle.javafx.scenebuilder.ext.i18n.preference.I18NResourcePreference;

public class SbI18nExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("419512b6-4f35-4980-b926-ae0eb5070b19");

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
                ApplyI18nContentAction.class,
                ApplyI18nContentResourceExtension.class,
                ApplyI18nContentWatchExtension.class,
                I18NResourcePreference.class,
                I18nMenuProvider.class,
                I18nResourceMenuController.class
            );
     // @formatter:on
    }
}
