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
package com.oracle.javafx.scenebuilder.ext.actions;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.action.AbstractActionExtension;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstanceWindow;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.FileSystem.WatchingCallback;
import com.oracle.javafx.scenebuilder.ext.controller.SceneStyleSheetMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ApplyCssContentWatchExtension extends AbstractActionExtension<ApplyCssContentAction>
        implements WatchingCallback {

    private final UserStylesheetsPreference userStylesheetsPreference;
    private final FileSystem fileSystem;
    private final SceneStyleSheetMenuController sceneStyleSheetMenuController;
    private final SceneBuilderBeanFactory context;

    public ApplyCssContentWatchExtension(@Autowired FileSystem fileSystem, @Autowired SceneBuilderBeanFactory context,
            @Autowired SceneStyleSheetMenuController sceneStyleSheetMenuController,
            @Autowired @Lazy UserStylesheetsPreference userStylesheetsPreference) {
        super();
        this.fileSystem = fileSystem;
        this.context = context;
        this.sceneStyleSheetMenuController = sceneStyleSheetMenuController;
        this.userStylesheetsPreference = userStylesheetsPreference;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public void prePerform() {
        fileSystem.unwatch(this);
    }

    @Override
    public void postPerform() {
        if (userStylesheetsPreference.getValue() != null) {
            List<File> toWatch = userStylesheetsPreference.getValue().stream().map(s -> new File(URI.create(s)))
                    .collect(Collectors.toList());
            fileSystem.watch(context.getBean(ApplicationInstanceWindow.class), toWatch, this);
        }
    }

    @Override
    public void deleted(Path path) {
        sceneStyleSheetMenuController.performRemoveSceneStyleSheet(path.toFile());
    }

    @Override
    public void modified(Path path) {
        sceneStyleSheetMenuController.performReloadSceneStyleSheet();
    }

    @Override
    public void created(Path path) {
        // Unused here
    }

    @Override
    public Object getOwnerKey() {
        return this;
    }

}
