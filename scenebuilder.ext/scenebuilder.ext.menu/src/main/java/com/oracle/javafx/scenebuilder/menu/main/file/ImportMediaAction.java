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
package com.oracle.javafx.scenebuilder.menu.main.file;

import java.io.File;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.util.ResourceUtils;
import com.oracle.javafx.scenebuilder.api.selection.SbSelectionJobsFactory;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Performs the 'import' media edit action. Open a file chooser dialog to select the media  file to be imported.
 * This action creates an object matching the type of the selected media file (either ImageView or MediaView)
 * and insert it in the document (either as root if the document is empty or
 * under the selection common ancestor node otherwise).
 *
 */
@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = ImportMediaAction.MENU_ID,
        targetMenuId = ImportFxmlAction.MENU_ID,
        label = "menu.title.import.media",
        positionRequest = PositionRequest.AsNextSibling)
public class ImportMediaAction extends AbstractAction {

    public final static String MENU_ID = "importMediaMenu";

    private final MainInstanceWindow documentWindow;
    private final FileSystem fileSystem;
    private final SbSelectionJobsFactory selectionJobsFactory;
    private final JobManager jobManager;

    public ImportMediaAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            @Lazy MainInstanceWindow documentWindow,
            JobManager jobManager,
            MessageLogger messageLogger,
            SbSelectionJobsFactory selectionJobsFactory) {
        super(i18n, extensionFactory);
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.jobManager = jobManager;
        this.selectionJobsFactory = selectionJobsFactory;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    /**
     * Performs the 'import' media edit action.
     * This action creates an object matching the type of the selected
     * media file (either ImageView or MediaView) and insert it in the document
     * (either as root if the document is empty or under the selection common
     * ancestor node otherwise).
     *
     * @param mediaFile the media file to be imported
     */
    @Override
    public ActionStatus doPerform() {

        final FileChooser fileChooser = new FileChooser();
        final ExtensionFilter imageFilter = new ExtensionFilter(getI18n().getString("file.filter.label.image"),
                ResourceUtils.getSupportedImageExtensions());
        final ExtensionFilter audioFilter = new ExtensionFilter(getI18n().getString("file.filter.label.audio"),
                ResourceUtils.getSupportedAudioExtensions());
        final ExtensionFilter videoFilter = new ExtensionFilter(getI18n().getString("file.filter.label.video"),
                ResourceUtils.getSupportedVideoExtensions());
        final ExtensionFilter mediaFilter = new ExtensionFilter(getI18n().getString("file.filter.label.media"),
                ResourceUtils.getSupportedMediaExtensions());

        fileChooser.getExtensionFilters().add(mediaFilter);
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.getExtensionFilters().add(audioFilter);
        fileChooser.getExtensionFilters().add(videoFilter);

        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        File mediaFile = fileChooser.showOpenDialog(documentWindow.getStage());
        if (mediaFile != null) {

            // Keep track of the user choice for next time
            fileSystem.updateNextInitialDirectory(mediaFile);

            performImport(mediaFile);
        }

        return ActionStatus.DONE;
    }

    protected void performImport(File file) {
        final var job = selectionJobsFactory.importFile(file);
        if (job.isExecutable()) {
            jobManager.push(job);
        }
    }

}