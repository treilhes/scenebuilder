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
package com.oracle.javafx.scenebuilder.menu.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstancesManager;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

public abstract class AbstractOpenFilesAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(AbstractOpenFilesAction.class);

    private final Dialog dialog;
    private final RecentItemsPreference recentItemsPreference;
    private final EditorInstancesManager main;

    // @formatter:off
    public AbstractOpenFilesAction(
            ActionExtensionFactory extensionFactory,
            Dialog dialog,
            EditorInstancesManager main,
            RecentItemsPreference recentItemsPreference) {
     // @formatter:on
        super(extensionFactory);
        this.dialog = dialog;
        this.main = main;
        this.recentItemsPreference = recentItemsPreference;
    }


    protected void performOpenFiles(List<File> fxmlFiles) {
        assert fxmlFiles != null;
        assert fxmlFiles.isEmpty() == false;

        final Map<File, EditorInstance> documents = new HashMap<>();

        final Map<File, IOException> exceptions = new HashMap<>();

        //build dependency injections first
        for (File fxmlFile : fxmlFiles) {
                try {
                    final EditorInstance dwc = main.lookupDocument(fxmlFile.toURI().toURL());
                    if (dwc != null) {
                        // fxmlFile is already opened
                        dwc.getDocumentWindow().getStage().toFront();
                    } else {
                        // Open fxmlFile
                        final EditorInstance hostWindow;
                        final EditorInstance unusedWindow = main.lookupUnusedDocument(documents.values());
                        if (unusedWindow != null) {
                            logger.info("Assign {} to unused document", fxmlFile.getName());
                            hostWindow = unusedWindow;
                        } else {
                            logger.info("Assign {} to new document", fxmlFile.getName());
                            hostWindow = main.makeNewDocument();
                        }
                        documents.put(fxmlFile, hostWindow);
                    }
                } catch (IOException e) {
                    exceptions.put(fxmlFile, e);
                }
        }

        //SceneBuilderLoadingProgress.get().end();

        // execute ui related loading now
        SbPlatform.runOnFxThread(() -> {


            for (Entry<File, EditorInstance> entry:documents.entrySet()) {
                File file = entry.getKey();
                EditorInstance hostWindow = entry.getValue();
                hostWindow.onFocus();
                //SbPlatform.runForDocument(hostWindow, () -> {
                    try {
                        hostWindow.loadFromFile(file);
                        hostWindow.openWindow();
                    } catch (IOException xx) {
                        hostWindow.closeWindow();
                        exceptions.put(file, xx);
                    }
                //});

                switch (exceptions.size()) {
                    case 0: { // Good
                        // Update recent items with opened files
                        recentItemsPreference.addRecentItems(fxmlFiles);
                        break;
                    }
                    case 1: {
                        final File fxmlFile = exceptions.keySet().iterator().next();
                        final Exception x = exceptions.get(fxmlFile);
                        dialog.showErrorAndWait(
                                I18N.getString("alert.title.open"),
                                I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())),
                                I18N.getString("alert.open.failure1.details"),
                                x);
                        break;
                    }
                    default: {
                        if (exceptions.size() == fxmlFiles.size()) {
                            // Open operation has failed for all the files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"),
                                    I18N.getString("alert.open.failureN.message"),
                                    I18N.getString("alert.open.failureN.details")
                                    );
                        } else {
                            // Open operation has failed for some files
                            dialog.showErrorAndWait(
                                    I18N.getString("alert.title.open"),
                                    I18N.getString("alert.open.failureMofN.message", exceptions.size(), fxmlFiles.size()),
                                    I18N.getString("alert.open.failureMofN.details")
                                    );
                        }
                        break;
                    }
                }
            }
        });
    }

    private static String displayName(String pathString) {
        return Paths.get(pathString).getFileName().toString();
    }
}