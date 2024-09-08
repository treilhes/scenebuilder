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
package com.oracle.javafx.scenebuilder.menu.action;

import java.io.File;
import java.util.Optional;

import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstanceWindow;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractFxmlAction extends AbstractAction {

    private final FileSystem fileSystem;
    private final ApplicationInstanceWindow documentWindow;
    private final JobManager jobManager;
    private final MessageLogger messageLogger;
    private final ImportFileJob.Factory importFileJobFactory;
    private final IncludeFileJob.Factory includeFileJobFactory;


    public AbstractFxmlAction(
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            ApplicationInstanceWindow documentWindow,
            JobManager jobManager,
            MessageLogger messageLogger,
            ImportFileJob.Factory importFileJobFactory,
            IncludeFileJob.Factory includeFileJobFactory) {
        super(extensionFactory);
        this.fileSystem = fileSystem;
        this.documentWindow = documentWindow;
        this.jobManager = jobManager;
        this.messageLogger = messageLogger;
        this.importFileJobFactory = importFileJobFactory;
        this.includeFileJobFactory = includeFileJobFactory;
    }


    protected Optional<File> fetchFXMLFile() {
        var fileChooser = new FileChooser();
        var f = new ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml"); // NOI18N
        fileChooser.getExtensionFilters().add(f);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        var fxmlFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (fxmlFile != null) {
            // See DTL-5948: on Linux we anticipate an extension less path.
            final String path = fxmlFile.getPath();
            if (!path.endsWith(".fxml")) { // NOI18N
                fxmlFile = new File(path + ".fxml"); // NOI18N
            }

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(fxmlFile);
        }
        return Optional.ofNullable(fxmlFile);
    }

    protected void performImport(File file) {

        final ImportFileJob job = importFileJobFactory.getJob(file);
        if (job.isExecutable()) {
            jobManager.push(job);
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject().get();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                messageLogger.logWarningMessage(
                        "import.from.file.failed.target",
                        file.getName(), target);
            } else {
                messageLogger.logWarningMessage(
                        "import.from.file.failed",
                        file.getName());
            }
        }
    }

    /**
     * Performs the 'include' FXML edit action.
     * As opposed to the 'import' edit action, the 'include' action does not
     * copy the FXML content but adds an fx:include element to the FXML document.
     *
     * @param fxmlFile the FXML file to be included
     */
    protected void performIncludeFxml(File fxmlFile) {

        final IncludeFileJob job = includeFileJobFactory.getJob(fxmlFile);
        if (job.isExecutable()) {
            jobManager.push(job);
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject().get();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                messageLogger.logWarningMessage(
                        "include.file.failed.target",
                        fxmlFile.getName(), target);
            } else {
                messageLogger.logWarningMessage(
                        "include.file.failed",
                        fxmlFile.getName());
            }
        }
    }
}
