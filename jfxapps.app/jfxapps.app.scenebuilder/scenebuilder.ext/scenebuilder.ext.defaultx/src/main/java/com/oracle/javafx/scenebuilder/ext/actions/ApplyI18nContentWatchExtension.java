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
package com.oracle.javafx.scenebuilder.ext.actions;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.AbstractActionExtension;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.FileSystem.WatchingCallback;
import com.gluonhq.jfxapps.core.api.lifecycle.DisposeWithDocument;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.oracle.javafx.scenebuilder.ext.controller.I18nResourceMenuController;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;

@ApplicationInstanceSingleton
public class ApplyI18nContentWatchExtension extends AbstractActionExtension<ApplyI18nContentAction>
        implements DisposeWithDocument, WatchingCallback {

    private final I18NResourcePreference i18NResourcePreference;
    private final FileSystem fileSystem;
    private final I18nResourceMenuController i18nResourceMenuController;
    private final JfxAppContext context;

    public ApplyI18nContentWatchExtension(
            @Autowired FileSystem fileSystem,
            @Autowired JfxAppContext context,
            @Autowired I18nResourceMenuController i18nResourceMenuController,
            @Autowired @Lazy I18NResourcePreference i18NResourcePreference) {
        super();
        this.fileSystem = fileSystem;
        this.context = context;
        this.i18nResourceMenuController = i18nResourceMenuController;
        this.i18NResourcePreference = i18NResourcePreference;
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
        if (i18NResourcePreference.getValue() != null) {
            List<File> toWatch = i18NResourcePreference.getValue().stream().map(s -> new File(URI.create(s)))
                    .collect(Collectors.toList());
            fileSystem.watch(context.getBean(MainInstanceWindow.class), toWatch, this);
        }
    }

    @Override
    public void deleted(Path path) {
        i18nResourceMenuController.performRemoveResource(path.toFile());
    }

    @Override
    public void modified(Path path) {
        i18nResourceMenuController.performReloadResource();
    }

    @Override
    public void created(Path path) {
        // Unused here
    }

    @Override
    public void disposeWithDocument() {
        fileSystem.unwatch(this);
    }

    @Override
    public Object getOwnerKey() {
        return this;
    }

}
