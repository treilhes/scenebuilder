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
package com.oracle.javafx.scenebuilder.launcher.app;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.launcher.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithSceneBuilder;

import javafx.application.Application.Parameters;
import javafx.application.Platform;
import javafx.stage.Stage;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@DependsOn("i18n") // NOCHECK
public class LaunchController implements AppPlatform.AppNotificationHandler, ApplicationListener<StageReadyEvent> {

    private final static Logger logger = LoggerFactory.getLogger(LaunchController.class);

    @Autowired
    private Parameters parameters;

    private final FileSystem fileSystem;

    private final Dialog dialog;

    private final Main main;

    private final List<InitWithSceneBuilder> initializations;

    public LaunchController(
            @Autowired FileSystem fileSystem,
            @Autowired Main main,
            @Autowired @Lazy Dialog dialog,
            @Lazy @Autowired(required = false) List<InitWithSceneBuilder> initializations) {

        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.main = main;
        this.initializations = initializations;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        start(stageReadyEvent.getStage());
    }

    /*
     * Application
     */
    public void start(Stage stage) {
        try {
            if (AppPlatform.requestStart(this, parameters, fileSystem) == false) {
                // Start has been denied because another instance is running.
                Platform.exit();
            }
            // else {
            // No other Scene Builder instance is already running.
            // AppPlatform.requestStart() has/will invoke(d) handleLaunch().
            // start() has now finished its job and should imply return.
            // }

        } catch (IOException x) {
            dialog.showErrorAndWait(I18N.getString("alert.title.start"), I18N.getString("alert.start.failure.message"),
                    I18N.getString("alert.start.failure.details"), x);
            Platform.exit();
        }
    }

    /*
     * AppPlatform.AppNotificationHandler
     */
    @Override
    public void handleLaunch(List<String> files) {
        List<File> lFiles = files.stream().map(s -> new File(s)).filter(f -> f.exists()).collect(Collectors.toList());

        main.open(lFiles);

        SceneBuilderLoadingProgress.get().end();

        initializations.forEach(a -> a.init());

    }

    @Override
    public void handleOpenFilesAction(List<String> files) {
        assert files != null;
        assert files.isEmpty() == false;

        List<File> lFiles = files.stream().map(s -> new File(s)).filter(f -> f.exists()).collect(Collectors.toList());

        main.open(lFiles);
    }

    @Override
    public void handleMessageBoxFailure(Exception x) {
        dialog.showErrorAndWait(I18N.getString("alert.title.messagebox"),
                I18N.getString("alert.messagebox.failure.message"), I18N.getString("alert.messagebox.failure.details"),
                x);
    }

    @Override
    public void handleQuitAction() {

        /*
         * Note : this callback is called on Mac OS X only when the user selects the
         * 'Quit App' command in the Application menu.
         *
         * Before calling this callback, FX automatically sends a close event to each
         * open window ie DocumentWindowController.performCloseAction() is invoked for
         * each open window.
         *
         * When we arrive here, windowList is empty if the user has confirmed the close
         * operation for each window : thus exit operation can be performed. If
         * windowList is not empty, this means the user has cancelled at least one close
         * operation : in that case, exit operation should be not be executed.
         */
        if (main.getOpenDocuments() == 0) {
            logger.info(I18N.getString("log.stop"));
            Platform.exit();
        }
    }

}
