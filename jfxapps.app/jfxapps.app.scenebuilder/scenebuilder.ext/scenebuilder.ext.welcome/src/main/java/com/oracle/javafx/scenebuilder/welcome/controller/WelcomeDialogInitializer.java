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
package com.oracle.javafx.scenebuilder.welcome.controller;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.application.InstancesManager;
import com.oracle.javafx.scenebuilder.api.application.lifecycle.InitWithApplication;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;

@Component
public class WelcomeDialogInitializer implements InitWithApplication {


    private final SceneBuilderBeanFactory context;
    private final InstancesManager main;

    public WelcomeDialogInitializer(
            SceneBuilderBeanFactory context,
            InstancesManager main) {
        super();
        this.context = context;
        this.main = main;
    }

    @Override
    public void init() {
        WelcomeDialogWindowController wdwc = context.getBean(WelcomeDialogWindowController.class);

        // Unless we're on a Mac we're starting SB directly (fresh start)
        // so we're not opening any file and as such we should show the Welcome Dialog
        if (main.lookupUnusedInstance() != null) {
            SbPlatform.runOnFxThread(() -> {
                wdwc.getStage().show();
            });
        }

    }
}