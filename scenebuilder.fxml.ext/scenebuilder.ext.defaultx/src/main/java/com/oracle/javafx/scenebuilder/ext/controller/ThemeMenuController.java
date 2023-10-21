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

import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.UserStylesheetsPreference;

import javafx.stage.FileChooser;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ThemeMenuController {

    private final ActionFactory actionFactory;
    private final EditorInstanceWindow document;
    private final Dialog dialog;
    private final FileSystem fileSystem;
    private final UserStylesheetsPreference userStylesheetsPreference;

    public ThemeMenuController(@Autowired ActionFactory actionFactory, @Autowired EditorInstanceWindow document,
            @Autowired Dialog dialog, @Autowired FileSystem fileSystem,
            @Autowired UserStylesheetsPreference userStylesheetsPreference) {
        this.actionFactory = actionFactory;
        this.document = document;
        this.dialog = dialog;
        this.fileSystem = fileSystem;
        this.userStylesheetsPreference = userStylesheetsPreference;

    }

    public void performAddSceneStyleSheet() {

        boolean knownFilesModified = false;

        // Open a file chooser for *.css & *.bss
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                I18N.getString("scenestylesheet.filechooser.filter.msg"), "*.css", "*.bss")); // NOCHECK
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(document.getStage());

        if (selectedFiles != null) {
            assert selectedFiles.isEmpty() == false;
            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(selectedFiles.get(0));
            for (File f : selectedFiles) {
                if (!userStylesheetsPreference.getValue().contains(f.getAbsolutePath())) {
                    userStylesheetsPreference.getValue().add(f.getAbsolutePath());
                    knownFilesModified = true;
                }
            }
        }

        // Update stylesheet configuration
        if (knownFilesModified) {
            actionFactory.create(ApplyCssContentAction.class).checkAndPerform();
        }
    }

    public void performRemoveSceneStyleSheet(File toRemove) {
        if (userStylesheetsPreference.getValue().contains(toRemove.getAbsolutePath())) {
            userStylesheetsPreference.getValue().remove(toRemove.getAbsolutePath());
            actionFactory.create(ApplyCssContentAction.class).checkAndPerform();
        }
    }

    public void performOpenSceneStyleSheet(File toOpen) {
        try {
            fileSystem.open(toOpen.getPath());
        } catch (IOException ioe) {
            dialog.showErrorAndWait(I18N.getString("error.file.open.title"), I18N.getString("error.file.open.message"),
                    I18N.getString("error.filesystem.details"), ioe);
        }
    }

}
