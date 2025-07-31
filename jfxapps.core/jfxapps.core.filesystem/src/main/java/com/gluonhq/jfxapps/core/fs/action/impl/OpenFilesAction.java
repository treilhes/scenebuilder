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
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.ApplicationActionFactory;
import com.gluonhq.jfxapps.core.api.document.DocumentActionFactory;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.RecentItems;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.ApplicationDialog;

@ApplicationInstancePrototype("com.gluonhq.jfxapps.core.fs.action.impl.OpenFilesAction")
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class OpenFilesAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(OpenFilesAction.class);

    private final FileSystem fileSystem;

    private final ApplicationActionFactory applicationActionFactory;

    private final DocumentActionFactory documentActionFactory;

    private final ApplicationDialog applicationDialog;

    private final RecentItems recentItems;

    private List<File> fxmlFiles;

    // @formatter:off
    protected OpenFilesAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            RecentItems recentItems,
            FileSystem fileSystem,
            ApplicationActionFactory applicationActionFactory,
            DocumentActionFactory documentActionFactory,
            ApplicationDialog applicationDialog) {
     // @formatter:on
        super(i18n, extensionFactory);
        this.fileSystem = fileSystem;
        this.applicationActionFactory = applicationActionFactory;
        this.documentActionFactory = documentActionFactory;
        this.applicationDialog = applicationDialog;
        this.recentItems = recentItems;
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

        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            fileSystem.updateNextInitialDirectory(fxmlFiles.get(0));

            for (File file : fxmlFiles) {
                try {
                    var fileURL = file.toURI().toURL();
                    applicationActionFactory.lookupUnusedInstance(fileURL,
                            (instance) -> documentActionFactory.loadURL(fileURL, true));
                    recentItems.addRecentItem(fileURL);
                } catch (MalformedURLException e) {
                    logger.error("Error converting file to URL: {}", file, e);
                    applicationDialog.addError("Unable to open file", e.getMessage(), e);
                }
            }
        }
        return ActionStatus.DONE;
    }

}