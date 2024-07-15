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
package com.gluonhq.jfxapps.ext.menu.action;

import java.io.File;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.InstancesManager;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fs.preference.global.RecentItemsPreference;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class OpenFilesAction extends AbstractOpenFilesAction {

    //private static final Logger logger = LoggerFactory.getLogger(OpenFilesAction.class);

    private final FileSystem fileSystem;

    private List<File> fxmlFiles;

    // @formatter:off
    protected OpenFilesAction(
            ActionExtensionFactory extensionFactory,
            Dialog dialog,
            InstancesManager main,
            RecentItemsPreference recentItemsPreference,
            FileSystem fileSystem) {
     // @formatter:on
        super(extensionFactory, dialog, main, recentItemsPreference);
        this.fileSystem = fileSystem;
    }

    public void setFxmlFile(List<File> fxmlFiles) {
        this.fxmlFiles = fxmlFiles;
    }

    public List<File> getFxmlFiles() {
        return fxmlFiles;
    }

    @Override
    public boolean canPerform() {
        return fxmlFiles != null && fxmlFiles.size() > 0;
    }

    @Override
    public ActionStatus doPerform() {
        fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));
        performOpenFiles(fxmlFiles);
        return ActionStatus.DONE;
    }

}