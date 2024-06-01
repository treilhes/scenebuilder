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
package com.gluonhq.jfxapps.ext.menu.action.file;

import java.io.IOException;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class ReloadFileAction extends AbstractAction {

    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final MainInstanceWindow documentWindow;
    private final FileSystem fileSystem;

    public ReloadFileAction(
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            DocumentManager documentManager,
            MainInstanceWindow documentWindow,
            Dialog dialog) {
        super(extensionFactory);
        this.documentManager = documentManager;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        final FXOMDocument omDocument = documentManager.fxomDocument().get();

        assert (omDocument != null) && (omDocument.getLocation() != null);

        try {
            fileSystem.reload();
            // Here we do not invoke updateStageTitleAndPreferences() neither
            // watchingController.update()
        } catch (IOException e) {
            dialog.showErrorAndWait(I18N.getString("alert.title.open"),
                    I18N.getString("alert.open.failure1.message", documentWindow.getStage().getTitle()),
                    I18N.getString("alert.open.failure1.details"), e);
            return ActionStatus.FAILED;
        }
        return ActionStatus.DONE;
    }

}