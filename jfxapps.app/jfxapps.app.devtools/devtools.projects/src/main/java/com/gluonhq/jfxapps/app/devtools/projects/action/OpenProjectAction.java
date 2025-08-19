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
package com.gluonhq.jfxapps.app.devtools.projects.action;

import com.gluonhq.jfxapps.app.devtools.api.menu.DefaultMenu;
import com.gluonhq.jfxapps.app.devtools.api.project.ProjectActionFactory;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;

import javafx.stage.FileChooser;

@ApplicationInstancePrototype("com.gluonhq.jfxapps.app.devtools.projects.action.OpenProjectAction")
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")
@MenuItemAttachment(
        id = OpenProjectAction.MENU_ID,
        targetMenuId = DefaultMenu.FILE_MENU_ID,
        label = "menu.title.cut",
        positionRequest = PositionRequest.AsFirstChild)
@Accelerator(accelerator = "CTRL+P")
public class OpenProjectAction extends AbstractAction {

    public static final String MENU_ID = "openProjectMenu";

    private final ProjectActionFactory projectActionFactory;
    private FileSystem fileSystem;

    public OpenProjectAction(
    // @formatter:off
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            ProjectActionFactory projectActionFactory) {
    // @formatter:on
        super(i18n, extensionFactory);
        this.fileSystem = fileSystem;
        this.projectActionFactory = projectActionFactory;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        final var fileChooser = new FileChooser();
        //final var filter = getI18n().getString("file.filter.label.fxml");
        final var filter = "Pom Files";
        final var extension = "pom.xml";
        final var extensionFilter = new FileChooser.ExtensionFilter(filter, extension);

        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

        final var fxmlFiles = fileChooser.showOpenMultipleDialog(null);

        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            var target = fxmlFiles.get(0);
            fileSystem.updateNextInitialDirectory(target);
            projectActionFactory.load(target).checkAndPerform();
        }

        return ActionStatus.DONE;
    }

}