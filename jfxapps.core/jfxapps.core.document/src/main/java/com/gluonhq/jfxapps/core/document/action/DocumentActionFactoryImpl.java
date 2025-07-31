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
package com.gluonhq.jfxapps.core.document.action;

import java.io.File;
import java.net.URL;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.document.DocumentActionFactory;
import com.gluonhq.jfxapps.core.document.action.impl.LoadBlankAction;
import com.gluonhq.jfxapps.core.document.action.impl.LoadFileAction;
import com.gluonhq.jfxapps.core.document.action.impl.LoadUrlAction;
import com.gluonhq.jfxapps.core.document.action.impl.ReloadFileAction;
import com.gluonhq.jfxapps.core.document.action.impl.SaveAction;
import com.gluonhq.jfxapps.core.document.action.impl.SaveAsAction;
import com.gluonhq.jfxapps.core.document.action.impl.SaveOrSaveAsAction;

@ApplicationSingleton
public class DocumentActionFactoryImpl implements DocumentActionFactory {

    private final ActionFactory actionFactory;

    public DocumentActionFactoryImpl(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public Action loadBlank() {
        return actionFactory.create(LoadBlankAction.class);
    }

    @Override
    public Action loadFile(File file) {
        return actionFactory.create(LoadFileAction.class, a -> a.setFxmlFile(file));
    }

    @Override
    public Action loadURL(URL url, boolean keepTrackOfLocation) {
        return actionFactory.create(LoadUrlAction.class, a -> {
            a.setFxmlURL(url);
            a.setKeepTrackOfLocation(keepTrackOfLocation);
        });
    }

    @Override
    public Action reload() {
        return actionFactory.create(ReloadFileAction.class);
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
