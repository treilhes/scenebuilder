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
package com.oracle.javafx.scenebuilder.menu.action.file;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.action.Action.ActionStatus;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstanceWindow;
import com.oracle.javafx.scenebuilder.menu.action.AbstractFxmlAction;

/**
 * Performs the 'import' FXML edit action. Open a file chooser dialog to select the FXML file to be imported.
 * This action creates an object matching the root node of the selected FXML file and insert it in the
 * document (either as root if the document is empty or under the selection
 * common ancestor node otherwise).
 *
 */
@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = ImportFxmlAction.MENU_ID,
        targetMenuId = ImportProvider.MENU_ID,
        label = "menu.title.import.fxml",
        positionRequest = PositionRequest.AsFirstChild)
public class ImportFxmlAction extends AbstractFxmlAction {

    public final static String MENU_ID = "importFxmlMenu";

    public ImportFxmlAction(
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            ApplicationInstanceWindow documentWindow,
            JobManager jobManager,
            MessageLogger messageLogger,
            ImportFileJob.Factory importFileJobFactory,
            IncludeFileJob.Factory includeFileJobFactory
            ) {
        super(extensionFactory, fileSystem, documentWindow, jobManager, messageLogger, importFileJobFactory, includeFileJobFactory);
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        fetchFXMLFile().ifPresent(fxmlFile -> performImport(fxmlFile));
        return ActionStatus.DONE;
    }

}