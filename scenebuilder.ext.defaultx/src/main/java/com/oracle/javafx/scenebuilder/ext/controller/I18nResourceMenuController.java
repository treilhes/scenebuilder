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
package com.oracle.javafx.scenebuilder.ext.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyI18nContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;

import javafx.collections.ListChangeListener.Change;
import javafx.stage.FileChooser;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class I18nResourceMenuController {

    private final ActionFactory actionFactory;
    private final DocumentWindow document;
    private final FileSystem fileSystem;
    private final Dialog dialog;
    private final I18NResourcePreference i18nResourcePreference;

    public I18nResourceMenuController(@Autowired ActionFactory actionFactory, @Autowired @Lazy DocumentWindow document,
            @Autowired FileSystem fileSystem, @Autowired Dialog dialog,
            @Autowired @Lazy I18NResourcePreference i18nResourcePreference) {
        this.actionFactory = actionFactory;
        this.document = document;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.i18nResourcePreference = i18nResourcePreference;

        i18nResourcePreference.getValue().addListener((Change<? extends String> c) -> {
            performReloadResource();
        });
    }

    public void performAddResource() {

        // Open a file chooser for *.properties & *.bss
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(I18N.getString("resource.filechooser.filter.msg"), "*.properties")); // NOCHECK
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(document.getStage());

        if (selectedFiles != null) {
            assert selectedFiles.isEmpty() == false;
            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(selectedFiles.get(0));

            for (File f : selectedFiles) {
                if (!i18nResourcePreference.getValue().contains(f.toURI().toString())) {
                    i18nResourcePreference.getValue().add(f.toURI().toString());
                }
            }
        }
    }

    public void performRemoveResource(File toRemove) {
        if (i18nResourcePreference.getValue().contains(toRemove.toURI().toString())) {
            i18nResourcePreference.getValue().remove(toRemove.toURI().toString());
        }
    }

    public void performRevealResource(File resourceFile) {
        assert resourceFile != null;
        try {
            fileSystem.revealInFileBrowser(resourceFile);
        } catch (IOException ioe) {
            dialog.showErrorAndWait(I18N.getString("error.file.reveal.title"),
                    I18N.getString("error.file.reveal.message"), I18N.getString("error.filesystem.details"), ioe);
        }
    }

    public void performReloadResource() {
        actionFactory.create(ApplyI18nContentAction.class).checkAndPerform();
    }

}
