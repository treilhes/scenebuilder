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
package com.oracle.javafx.scenebuilder.gluon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.WelcomeDialog;
import com.oracle.javafx.scenebuilder.api.application.lifecycle.InitWithApplication;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstanceWindow;
import com.oracle.javafx.scenebuilder.gluon.controller.GluonJarImportController;
import com.oracle.javafx.scenebuilder.gluon.controller.RegistrationController;
import com.oracle.javafx.scenebuilder.gluon.controller.TrackingController;
import com.oracle.javafx.scenebuilder.gluon.controller.UpdateController;

import javafx.application.Platform;

@Component
public class GluonInitializer implements InitWithApplication {

    private final GluonJarImportController gluonJarImportController;
    private final RegistrationController registrationController;
    private final TrackingController trackingController;
    private final UpdateController updateController;
    private SceneBuilderBeanFactory context;

    public GluonInitializer(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired GluonJarImportController gluonJarImportController,
            @Autowired RegistrationController registrationController,
            @Autowired TrackingController trackingController,
            @Autowired UpdateController updateController
        ) {
        super();
        this.context = context;
        this.gluonJarImportController = gluonJarImportController;
        this.registrationController = registrationController;
        this.trackingController = trackingController;
        this.updateController = updateController;
    }

    @Override
    public void init() {
        gluonJarImportController.startListeningLibrary();
        trackingController.sendTrackingStartupInfo();

        SbPlatform.runOnFxThread(() -> {
            context.getBean(WelcomeDialog.class).getStage().setOnHidden(event -> {
                updateController.showUpdateDialogIfRequired(context.getBean(ApplicationInstanceWindow.class), () -> {
                    if (!Platform.isFxApplicationThread()) {
                        SbPlatform.runOnFxThread(() -> registrationController.showRegistrationDialogIfRequired(context.getBean(ApplicationInstanceWindow.class)));
                    } else {
                        registrationController.showRegistrationDialogIfRequired(context.getBean(ApplicationInstanceWindow.class));
                    }
                });
            });
        });
    }
}