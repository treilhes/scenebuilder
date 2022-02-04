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
package com.oracle.javafx.scenebuilder.launcher.actions;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.fs.action.LoadBlankInNewWindowAction;
import com.oracle.javafx.scenebuilder.fs.action.OpenFilesAction;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
public class OpenScenebuilderAction extends AbstractAction {

    private List<File> files;
    private final List<InitWithSceneBuilder> initializations;
    private final Main main;
    private final ActionFactory actionFactory;

    public OpenScenebuilderAction(
            ActionExtensionFactory extensionFactory,
            @Autowired Main main,
            @Autowired ActionFactory actionFactory,
            @Lazy @Autowired(required = false) List<InitWithSceneBuilder> initializations) {
        super(extensionFactory);
        this.main = main;
        this.actionFactory = actionFactory;
        this.initializations = initializations;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        if (files.isEmpty()) {
//            // Creates an empty document
//            final Document newWindow = main.makeNewWindow();
//
//            // Unless we're on a Mac we're starting SB directly (fresh start)
//            // so we're not opening any file and as such we should show the Welcome Dialog
//
//            SbPlatform.runLater(() -> {
//                newWindow.updateWithDefaultContent();
//                newWindow.openWindow();
//                SceneBuilderLoadingProgress.get().end();
//            });

            actionFactory.create(LoadBlankInNewWindowAction.class).checkAndPerform();
        } else {
            // Open files passed as arguments by the platform
            OpenFilesAction openFileAction = actionFactory.create(OpenFilesAction.class);
            openFileAction.setFxmlFile(files);
            openFileAction.checkAndPerform();
        }
        SceneBuilderLoadingProgress.get().end();

        initializations.forEach(a -> a.init());

        return ActionStatus.DONE;
    }
}