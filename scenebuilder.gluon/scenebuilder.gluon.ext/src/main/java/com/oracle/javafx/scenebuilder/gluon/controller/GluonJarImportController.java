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
package com.oracle.javafx.scenebuilder.gluon.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.WelcomeDialog;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstancesManager;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlReportEntryImpl;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlReportImpl;
import com.oracle.javafx.scenebuilder.gluon.GluonConstants;
import com.oracle.javafx.scenebuilder.gluon.alert.ImportingGluonControlsAlert;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.ImportedGluonJarsPreference;

@Component
@Lazy
public class GluonJarImportController {

    private final EditorInstancesManager main;
    private final SceneBuilderBeanFactory context;
    private final ControlLibrary library;
    private final IconSetting iconSetting;
    private final ImportedGluonJarsPreference importedJarPreference;

    public GluonJarImportController(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired EditorInstancesManager main,
            @Autowired ControlLibrary library,
            @Autowired IconSetting iconSetting,
            @Autowired ImportedGluonJarsPreference importedJarPreference
            ) {
        super();
        this.context = context;
        this.main = main;
        this.library = library;
        this.iconSetting = iconSetting;
        this.importedJarPreference = importedJarPreference;
    }

    public void startListeningLibrary() {

        library.setOnUpdatedJarReports(jarReports -> {
            boolean shouldShowImportGluonJarAlert = false;
            List<String> gluonJarsCollection = new ArrayList<>();

            for (ControlReportImpl jarReport : jarReports) {
                if (hasGluonControls(jarReport)) {
                    gluonJarsCollection.add(jarReport.getSource().getFileName().toString());
                    // We check if the jar has already been imported to avoid showing the import gluon jar
                    // alert every time Scene Builder starts for jars that have already been imported
                    if (!hasGluonJarBeenImported(importedJarPreference, jarReport.getSource().getFileName().toString())) {
                        shouldShowImportGluonJarAlert = true;
                    }

                }
            }

            if (shouldShowImportGluonJarAlert) {
                SbPlatform.runOnFxThread(() -> {
                    EditorInstance dwc = main.getFrontDocumentWindow();
                    if (dwc == null) {
                        //TODO when started to fast will throw IndexOutOfBoundsException
                        dwc = main.getDocuments().get(0);
                    }
                    ImportingGluonControlsAlert alert = new ImportingGluonControlsAlert(dwc.getDocumentWindow().getStage());
                    iconSetting.setWindowIcon(alert);

                    WelcomeDialog welcome = context.getBean(WelcomeDialog.class);
                    if (welcome.getStage().isShowing()) {
                        alert.initOwner(welcome.getStage());
                    }
                    alert.showAndWait();
                });
            }
            updateImportedGluonJars(importedJarPreference, gluonJarsCollection);
        });

        //TODO test me, this case may be handled by the previous code.
//        library.setOnUpdatedExploringJarReports(jarReports -> {
//            boolean shouldShowImportGluonJarAlert = false;
//
//            for (ControlReport jarReport : jarReports) {
//                if (hasGluonControls(jarReport)) {
//                    shouldShowImportGluonJarAlert = true;
//                }
//            }
//
//            if (shouldShowImportGluonJarAlert) {
//                SbPlatform.runLater(() -> {
//                    Document dwc = main.getFrontDocumentWindow();
//                    if (dwc == null) {
//                        dwc = main.getDocumentWindowControllers().get(0);
//                    }
//                    ImportingGluonControlsAlert alert = new ImportingGluonControlsAlert(dwc.getDocumentWindow().getStage());
//                    iconSetting.setWindowIcon(alert);
//                    alert.showAndWait();
//                });
//            }
//        });

    }

    private static void updateImportedGluonJars(ImportedGluonJarsPreference preference, List<String> jarReportCollection) {
        if (jarReportCollection.isEmpty()) {
            preference.setValue(new String[0]);
        } else {
            preference.setValue(jarReportCollection.toArray(new String[0]));
        }
        preference.write();
    }

    private static boolean hasGluonControls(ControlReportImpl jarReport) {
        return jarReport.getEntries().stream().anyMatch(e -> isGluon(e));
    }

    private static boolean isGluon(ControlReportEntryImpl entry) {
        return entry.getClassName() != null && entry.getClassName().startsWith(GluonConstants.GLUON_PACKAGE);
    }


    private static boolean hasGluonJarBeenImported(ImportedGluonJarsPreference preference, String jar) {
        String[] importedJars = preference.getValue();
        if (importedJars == null) {
            return false;
        }

        for (String importedJar : importedJars) {
            if (jar.equals(importedJar)) {
                return true;
            }
        }
        return false;
    }

}
