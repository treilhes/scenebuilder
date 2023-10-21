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
package com.oracle.javafx.scenebuilder.boot.main.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.boot.main.util.MessageBoxNotificationHandler;
import com.oracle.javafx.scenebuilder.boot.main.Main;
import com.oracle.javafx.scenebuilder.boot.main.util.MessageBox;
import com.oracle.javafx.scenebuilder.boot.main.util.MessageBoxMessage;
import com.oracle.javafx.scenebuilder.boot.platform.DefaultFolders;
import com.oracle.javafx.scenebuilder.core.loader.ApplicationManager;
import com.oracle.javafx.scenebuilder.core.loader.OpenCommandEvent;
import com.oracle.javafx.scenebuilder.core.loader.dev.DevelopmentMode;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(subcommands = {RunFxmlCommand.class})
public class StartCommand implements Runnable, MessageBox.Delegate<MessageBoxMessage> {

    private final static Logger logger = LoggerFactory.getLogger(StartCommand.class);

    private static MessageBox<MessageBoxMessage> messageBox;

    private final static String BOOT_APPLICATION = "/boot.json";

    @Option(names = {"--dev", "-d"}, description = "Enable development mode, project extensions sources are resolved localy using classes folder")
    private boolean devMode;

    @Option(names = {"--root", "-r"}, defaultValue = "./target", description = "Extensions download folder")
    private Path root;

    @Option(names = {"--boot", "-b"}, description = "Custom json boot file")
    private File bootFile;

    @Option(names = {"--app", "-a"}, description = "target application uuid")
    private UUID targetApplication;

    @Option(names = {"--files", "-f"}, description = "list of files to open")
    private List<File> files;

    private ApplicationManager appManager;

    @Override
    public void run() {

        try {
            if (!lockMessageBox(this, targetApplication, files)) {
                logger.warn("An instance is already running forwarding execution to existing instance");
                return;
            }
        } catch (IOException e) {
            logger.error("Unable to initialize the message box", e);
        }

        if (devMode) {
            DevelopmentMode.setActive(true);
            DevelopmentMode.addMavenProjectDirectory(Path.of(".").resolve("../../scenebuilder.core"));
            DevelopmentMode.addMavenProjectDirectory(Path.of(".").resolve("../../scenebuilder.app"));
            DevelopmentMode.addMavenProjectDirectory(Path.of(".").resolve("../../scenebuilder.app/scenebuilder.app.manager"));
        }

        appManager =  ApplicationManager.get(root);

        if (appManager.hasSavedApplication()) {
            appManager.loadApplication();
        } else {
            try (InputStream bootStream = bootFile == null ? Main.class.getResourceAsStream(BOOT_APPLICATION) : new FileInputStream(bootFile)){
                appManager.loadApplication(bootStream);
            } catch (IOException e) {
                logger.error("Unable to load the default bootable application definition", e);
            }
        }

        try {
            appManager.load();
            appManager.start();

            if (targetApplication != null) {
                appManager.startEditor(targetApplication);
            }

            if (files != null && !files.isEmpty()) {
                for (File file:files) {
                    appManager.send(new OpenCommandEvent(targetApplication, file));
                }
            } else {
                appManager.send(new OpenCommandEvent(targetApplication, null));
            }


        } catch (Exception e) {
            logger.error("Unable to start the application", e);
        }
    }



    /*
     * Private (requestStartGeneric)
     */

    private static synchronized boolean lockMessageBox(MessageBox.Delegate<MessageBoxMessage> delegate, UUID targetApp, List<File> files) throws IOException {
        assert messageBox == null;

        try {
            Files.createDirectories(DefaultFolders.getMessageBoxFolder().toPath());
        } catch (FileAlreadyExistsException x) {
            // Fine
        }

        final boolean result;
        messageBox = new MessageBox<>(DefaultFolders.getMessageBoxFolder(), MessageBoxMessage.class, 1000 /* ms */);

        // Fix End
        if (messageBox.grab(delegate)) {
            result = true;
        } else {
            result = false;

            List<String> parameters = new ArrayList<>();
            if (files != null) {
                parameters.addAll(files.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
            }
            final MessageBoxMessage unamedParameters = new MessageBoxMessage(targetApp, parameters);
            try {
                messageBox.sendMessage(unamedParameters);
            } catch (InterruptedException x) {
                throw new IOException(x);
            }
        }

        return result;
    }

    @Override
    public void messageBoxDidGetMessage(MessageBoxMessage message) {
        try {
            if (message.getTargetApplication() != null) {
                appManager.startEditor(message.getTargetApplication());
            }

            if (message.getFiles() != null && !message.getFiles().isEmpty()) {
                for (String file:message.getFiles()) {
                    appManager.send(new OpenCommandEvent(targetApplication, new File(file)));
                }
            } else {
                appManager.send(new OpenCommandEvent(targetApplication, null));
            }
        } catch (Exception e) {
            logger.error("Unable to execute the message {} the application", message, e);
        }
    }



    @Override
    public void messageBoxDidCatchException(Exception ex) {
        logger.error("Received message but something failed", ex);
    }



}