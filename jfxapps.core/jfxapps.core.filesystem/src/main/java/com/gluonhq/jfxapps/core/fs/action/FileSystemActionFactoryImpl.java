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
package com.gluonhq.jfxapps.core.fs.action;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.fs.FileSystemActionFactory;
import com.gluonhq.jfxapps.core.fs.action.impl.ClearRecentItemsAction;
import com.gluonhq.jfxapps.core.fs.action.impl.LoadBlankAction;
import com.gluonhq.jfxapps.core.fs.action.impl.LoadFileAction;
import com.gluonhq.jfxapps.core.fs.action.impl.LoadUrlAction;
import com.gluonhq.jfxapps.core.fs.action.impl.NewAction;
import com.gluonhq.jfxapps.core.fs.action.impl.OpenAction;
import com.gluonhq.jfxapps.core.fs.action.impl.OpenFilesAction;
import com.gluonhq.jfxapps.core.fs.action.impl.ReloadFileAction;
import com.gluonhq.jfxapps.core.fs.action.impl.RevertAction;
import com.gluonhq.jfxapps.core.fs.action.impl.SaveAction;
import com.gluonhq.jfxapps.core.fs.action.impl.SaveAsAction;
import com.gluonhq.jfxapps.core.fs.action.impl.SaveOrSaveAsAction;

@ApplicationInstanceSingleton
public class FileSystemActionFactoryImpl implements FileSystemActionFactory{

    private final ActionFactory actionFactory;

    public FileSystemActionFactoryImpl(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public Action clearRecentItems() {
        return actionFactory.create(ClearRecentItemsAction.class);
    }

    @Override
    public Action loadBlank() {
        return actionFactory.create(LoadBlankAction.class);
    }

    @Override
    public Action loadFile() {
        return actionFactory.create(LoadFileAction.class);
    }

    @Override
    public Action loadURL(URL url, boolean keepTrackOfLocation) {
        return actionFactory.create(LoadUrlAction.class, a -> {
            a.setFxmlURL(url);
            a.setKeepTrackOfLocation(keepTrackOfLocation);
        });
    }

    @Override
    //FIXME better to move it in a container related module
    public Action newInstance() {
        return actionFactory.create(NewAction.class);
    }

    @Override
    public Action open() {
        return actionFactory.create(OpenAction.class);
    }

    @Override
    public Action openFiles(List<File> list) {
        return actionFactory.create(OpenFilesAction.class, a -> a.setFxmlFile(list));
    }

    @Override
    public Action reload() {
        return actionFactory.create(ReloadFileAction.class);
    }

    @Override
    public Action revert() {
        return actionFactory.create(RevertAction.class);
    }

    @Override
    public Action save() {
        return actionFactory.create(SaveAction.class);
    }

    @Override
    public Action saveAs() {
        return actionFactory.create(SaveAsAction.class);
    }

    @Override
    public Action saveOrSaveAs() {
        return actionFactory.create(SaveOrSaveAsAction.class);
    }

}
