/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.appmngr.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.loader.OpenCommandEvent;
import com.oracle.javafx.scenebuilder.api.application.ApplicationInstance;
import com.oracle.javafx.scenebuilder.api.application.CommandHandler;
import com.oracle.javafx.scenebuilder.api.application.InstancesManager;
import com.oracle.javafx.scenebuilder.api.application.javafx.JavafxThreadHolder;

@ApplicationSingleton
public class CommandHandlerImpl implements CommandHandler{

    private static final Logger logger = LoggerFactory.getLogger(CommandHandlerImpl.class);

    private final InstancesManager instancesManager;
    private final JavafxThreadHolder fxThreadHolder;

    private final List<OpenCommandEvent> waitingCommands = new ArrayList<>();

    public CommandHandlerImpl(InstancesManager instancesManager, JavafxThreadHolder fxThreadHolder) {
        super();
        this.instancesManager = instancesManager;
        this.fxThreadHolder = fxThreadHolder;
        this.fxThreadHolder.whenStarted(this::executeStoredCommands);
    }

    @Override
    public void onApplicationEvent(OpenCommandEvent event) {
        logger.info("CMD received " + event.toString());

        if (!fxThreadHolder.hasStarted()) {
            waitingCommands.add(event);
        } else {
            execute(event);
        }
    }

    private void executeStoredCommands() {
        while (!waitingCommands.isEmpty()) {
            OpenCommandEvent currentArgs = waitingCommands.remove(0);
            execute(currentArgs);
        }
    }

    private void execute(OpenCommandEvent args) {
        logger.info("CMD executed " + args.toString());

        UUID targetApplication = args.getTarget();
        File file = args.getFile();


        if (file != null) {

            try {
                ApplicationInstance instance = instancesManager.lookupInstance(file.toURL());
                if (instance == null) {
                    instance = instancesManager.newInstance();
                    instance.loadFromFile(file);
                }
                instance.openWindow();
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {

            ApplicationInstance instance = instancesManager.lookupUnusedInstance();
            if (instance == null) {
                instance = instancesManager.newInstance();
            }
            instance.openWindow();
        }
    }
}
