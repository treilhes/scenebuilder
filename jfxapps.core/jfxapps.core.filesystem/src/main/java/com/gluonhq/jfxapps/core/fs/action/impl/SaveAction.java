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
package com.gluonhq.jfxapps.core.fs.action.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;

@ApplicationInstancePrototype("com.gluonhq.jfxapps.core.fs.action.impl.OpenAction")
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class SaveAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(SaveAction.class);

    private final FileSystem fileSystem;
    private final MainInstanceWindow mainInstanceWindow;
    private final Dialog dialog;

    private InputStream inputStream;

    private File target;

    // @formatter:off
    public SaveAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            MainInstanceWindow mainInstanceWindow,
            Dialog dialog) {
     // @formatter:on
        super(i18n, extensionFactory);
        this.fileSystem = fileSystem;
        this.mainInstanceWindow = mainInstanceWindow;
        this.dialog = dialog;
    }

    public void setParameters(InputStream inputStream, File target) {
        this.inputStream = inputStream;
        this.target = target;
    }

    @Override
    public boolean canPerform() {
        return inputStream != null && target != null && target.getParentFile().exists();
    }

    @Override
    public ActionStatus doPerform() {

        try {
            fileSystem.save(inputStream, target);
            return ActionStatus.DONE;
        } catch (IOException e) {
            dialog.showErrorAndWait(mainInstanceWindow.getStage(), null,
                    getI18n().getString("alert.save.failure.message", target.getAbsolutePath()),
                    getI18n().getString("alert.save.failure.details"), e);
            return ActionStatus.CANCELLED;
        }
    }
}