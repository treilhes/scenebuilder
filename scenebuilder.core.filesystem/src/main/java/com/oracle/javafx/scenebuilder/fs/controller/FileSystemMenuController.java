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
package com.oracle.javafx.scenebuilder.fs.controller;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.fs.action.LoadBlankInNewWindowAction;
import com.oracle.javafx.scenebuilder.fs.action.OpenFilesAction;
import com.oracle.javafx.scenebuilder.fs.action.RevertAction;
import com.oracle.javafx.scenebuilder.fs.action.SaveAction;
import com.oracle.javafx.scenebuilder.fs.action.SaveAsAction;
import com.oracle.javafx.scenebuilder.fs.action.SelectAndOpenFilesAction;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class FileSystemMenuController {

    private final Editor editor;
    private final Main main;
    private final Document document;
    private final RecentItemsPreference recentItemsPreference;
    private final ActionFactory actionFactory;


    public FileSystemMenuController(
            @Autowired Main main,
            @Autowired Document document,
            @Autowired Editor editor,
            @Autowired ActionFactory actionFactory,
            @Autowired RecentItemsPreference recentItemsPreference) {
        this.main = main;
        this.document = document;
        this.editor = editor;
        this.recentItemsPreference = recentItemsPreference;
        this.actionFactory = actionFactory;

    }


    public void performImportFxml() {
        document.performImportFxml();
    }


    public void performIncludeFxml() {
        document.performIncludeFxml();
    }


    public void performNew() {
        actionFactory.create(LoadBlankInNewWindowAction.class).checkAndPerform();
    }


    public void performOpen() {
        actionFactory.create(SelectAndOpenFilesAction.class).checkAndPerform();
    }


    public void performRevert() {
        actionFactory.create(RevertAction.class).checkAndPerform();
    }

    public void performSave() {
        actionFactory.create(SaveAction.class).checkAndPerform();
    }


    public void performSaveAs() {
        actionFactory.create(SaveAsAction.class).checkAndPerform();
    }


    public void performClearOpenRecent() {
        recentItemsPreference.clearRecentItems();
    }


    public void performOpenRecent(File file) {
        //main.performOpenRecent(document, file);
        OpenFilesAction action = actionFactory.create(OpenFilesAction.class);
        action.setFxmlFile(Arrays.asList(file));
        action.checkAndPerform();
    }

    public void performImportMedia() {
        document.performImportMedia();
    }

    public void performEditIncludedFxml() {
        editor.performEditIncludedFxml();
    }

    public void performRevealIncludeFxml() {
        editor.performRevealIncludeFxml();
    }

}
