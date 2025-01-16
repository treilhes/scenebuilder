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
package com.gluonhq.jfxapps.boot.main.config;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.api.loader.BootException;
import com.gluonhq.jfxapps.boot.api.loader.OpenCommandEvent;

@Component
public class BootHandler {

    private static final Logger logger = LoggerFactory.getLogger(BootHandler.class);

    private final ApplicationManager appManager;

    private final Optional<ApplicationStartup> startup;

    public BootHandler(ApplicationManager appManager, Optional<ApplicationStartup> startup) {
        super();
        this.appManager = appManager;
        this.startup = startup;
    }

    public void boot(UUID application, List<File> files, String[] args) {
        var bootStep = startup.map(s -> s.start("boot.handler"));

        try {
            var defaultStart = startup.map(s -> s.start("boot.start.default"));
            appManager.start();
            defaultStart.ifPresent(StartupStep::end);


            if (application != null) {
                var appStart = startup.map(s -> s.start("boot.start.application"));
                appManager.startApplication(application);
                appStart.ifPresent(StartupStep::end);
            }

            var commandStart = startup.map(s -> s.start("boot.start.command"));
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    appManager.send(new OpenCommandEvent(application, file));
                }
            } else {
                appManager.send(new OpenCommandEvent(application, null));
            }
            commandStart.ifPresent(StartupStep::end);
        } catch (BootException e) {
            logger.error("Unable to boot application", e);

        }

        bootStep.ifPresent(StartupStep::end);
    }
}
