/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.job.JobManager;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.selection.SbSelectionJobsFactory;
import com.oracle.javafx.scenebuilder.api.util.FileHelper;

/**
 * Performs the 'include' FXML edit action. Open a file chooser dialog to select the FXML file to be included.
 * As opposed to the 'import' edit action, the 'include' action does not copy the FXML content but adds an
 * fx:include element to the FXML document.
 *
 */
@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = IncludeFxmlAction.MENU_ID,
        targetMenuId = IncludeProvider.MENU_ID,
        label = "menu.title.include.fxml",
        positionRequest = PositionRequest.AsFirstChild)
public class IncludeFxmlAction extends AbstractAction {

    public static final String MENU_ID = "includeFxmlMenu"; //NOCHECK

    private final FxomEvents documentManager;
    private final SbSelectionJobsFactory selectionJobsFactory;
    private final JobManager jobManager;
    private final FileHelper fileHelper;

    public IncludeFxmlAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            SbSelectionJobsFactory selectionJobsFactory,
            FxomEvents documentManager,
            FileHelper fileHelper) {
        super(i18n, extensionFactory);
        this.documentManager = documentManager;
        this.selectionJobsFactory = selectionJobsFactory;
        this.jobManager = jobManager;
        this.fileHelper = fileHelper;
    }

    @Override
    public boolean canPerform() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        return (fxomDocument != null)
              && (fxomDocument.getFxomRoot() != null)
              && (fxomDocument.getLocation() != null);
    }

    @Override
    public ActionStatus doPerform() {
        fileHelper.fetchFile(getI18n().getString("file.filter.label.fxml"), "*.fxml")
                .ifPresent(fxmlFile -> performIncludeFxml(fxmlFile));
        return ActionStatus.DONE;
    }

    /**
     * Performs the 'include' FXML edit action.
     * As opposed to the 'import' edit action, the 'include' action does not
     * copy the FXML content but adds an fx:include element to the FXML document.
     *
     * @param fxmlFile the FXML file to be included
     */
    protected void performIncludeFxml(File fxmlFile) {

        final var job = selectionJobsFactory.includeFile(fxmlFile);
        if (job.isExecutable()) {
            jobManager.push(job);
        }
    }
}