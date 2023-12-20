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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.layer.OpenCommandEvent;
import com.gluonhq.jfxapps.boot.loader.extension.EditorExtension;
import com.oracle.javafx.scenebuilder.api.appmngr.CommandDispatcher;
import com.oracle.javafx.scenebuilder.api.editors.EditorDescriptor;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstancesManager;
import com.oracle.javafx.scenebuilder.api.editors.EditorsManager;
import com.oracle.javafx.scenebuilder.api.javafx.JavafxThreadHolder;

@Singleton
public class CommandDispatcherImpl implements CommandDispatcher{

    private static final Logger logger = LoggerFactory.getLogger(CommandDispatcherImpl.class);

    private final EditorsManager editorsManager;
    private final JavafxThreadHolder fxThreadHolder;

    private final List<OpenCommandEvent> waitingCommands = new ArrayList<>();

    public CommandDispatcherImpl(EditorsManager editorsManager, JavafxThreadHolder fxThreadHolder) {
        super();
        this.editorsManager = editorsManager;
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

        if (targetApplication == null) {
            if (file == null) {
                // no target and no file so open the manager application
                targetApplication = EditorExtension.MANAGER_APP_ID;
                editorsManager.getAvailableEditor(targetApplication).ifPresent(e -> {
                    EditorInstancesManager editor = editorsManager.lookupEditor(e);

                    if (editor == null) {
                        editor = editorsManager.makeNewEditor(e);
                    }

                    EditorInstance instance = null;
                    if (editor.getDocuments().isEmpty()) {
                        instance = editor.makeNewDocument();
                    } else {
                        instance = editor.getDocuments().get(0);
                    }

                    instance.openWindow();
                });

            } else if (file != null) {
                // find which editors can handle the file
                // if only one then open it and load the file
                // if more than one then rely on user choice to choose
                final String fileName = file.getName();

                Predicate<String> extensionMatch = ext -> fileName.toLowerCase().endsWith("." + ext.toLowerCase());

                Predicate<EditorDescriptor> oneExtensionMatch = ed -> ed.handledFileExtensions().stream().anyMatch(extensionMatch);

                List<EditorDescriptor> possibleEditors = editorsManager.getAvailableEditors().stream().filter(oneExtensionMatch).collect(Collectors.toList());

                switch (possibleEditors.size()) {
                    case 0 -> logger.error("Unable to find a valid editor to open the file!");
                    case 1 -> logger.info("OK GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    default -> logger.error("Too much editors to open the file, must make a choice");
                }
            }
        } else {
            if (file != null) {
                // the target application is known so try opening the file
                editorsManager.getAvailableEditor(targetApplication).ifPresent(e -> {
                    EditorInstancesManager editor = editorsManager.lookupEditor(e);

                    if (editor == null) {
                        editor = editorsManager.makeNewEditor(e);
                    }

                    try {
                        EditorInstance instance = editor.lookupDocument(file.toURL());
                        if (instance == null) {
                            instance = editor.makeNewDocument();
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
                });
            } else {
                // the target application is known so load blank
                editorsManager.getAvailableEditor(targetApplication).ifPresent(e -> {
                    EditorInstancesManager editor = editorsManager.lookupEditor(e);

                    if (editor == null) {
                        editor = editorsManager.makeNewEditor(e);
                    }

                    EditorInstance instance = editor.lookupUnusedDocument();
                    if (instance == null) {
                        instance = editor.makeNewDocument();
                    }
                    instance.openWindow();
                });
            }

        }


    }
}
