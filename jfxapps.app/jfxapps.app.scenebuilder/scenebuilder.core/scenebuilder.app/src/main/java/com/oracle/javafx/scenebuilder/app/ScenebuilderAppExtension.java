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
package com.oracle.javafx.scenebuilder.app;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.loader.extension.OpenExtension;
import com.oracle.javafx.scenebuilder.api.SbApiExtension;
import com.oracle.javafx.scenebuilder.app.action.CloseFileAction;
import com.oracle.javafx.scenebuilder.app.action.NewAction;
import com.oracle.javafx.scenebuilder.app.action.OpenAction;
import com.oracle.javafx.scenebuilder.app.action.OpenRecentProvider;
import com.oracle.javafx.scenebuilder.app.action.QuitScenebuilderAction;
import com.oracle.javafx.scenebuilder.app.action.RedoAction;
import com.oracle.javafx.scenebuilder.app.action.RevertAction;
import com.oracle.javafx.scenebuilder.app.action.SaveAsAction;
import com.oracle.javafx.scenebuilder.app.action.SaveOrSaveAsAction;
import com.oracle.javafx.scenebuilder.app.action.ShowDocumentationAction;
import com.oracle.javafx.scenebuilder.app.action.ShowPreferencesAction;
import com.oracle.javafx.scenebuilder.app.action.UndoAction;
import com.oracle.javafx.scenebuilder.app.doc.DocumentationImpl;
import com.oracle.javafx.scenebuilder.app.editors.ControllerClassEditor;
import com.oracle.javafx.scenebuilder.app.editors.CoreEditors;
import com.oracle.javafx.scenebuilder.app.editors.FxIdEditor;
import com.oracle.javafx.scenebuilder.app.editors.PropertyEditorFactory;
import com.oracle.javafx.scenebuilder.app.i18n.I18NFxmlCoreExtension;
import com.oracle.javafx.scenebuilder.app.job.fs.ImportFileJob;
import com.oracle.javafx.scenebuilder.app.job.fs.IncludeFileJob;

public class ScenebuilderAppExtension implements OpenExtension {

    public static final UUID ID = UUID.fromString("26cd6a45-7750-4b3e-bbd2-018187df0c49");

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public UUID getParentId() {
        return SbApiExtension.ID;
    }

    @Override
    public List<Class<?>> exportedContextClasses() {
     // @formatter:off
        return Arrays.asList(
                CloseFileAction.class,
                NewAction.class,
                OpenAction.class,
                OpenRecentProvider.class,
                QuitScenebuilderAction.class,
                RedoAction.class,
                RevertAction.class,
                SaveAsAction.class,
                SaveOrSaveAsAction.class,
                ShowDocumentationAction.class,
                ShowPreferencesAction.class,
                UndoAction.class,

                ImportFileJob.class,
                ImportFileJob.Factory.class,
                IncludeFileJob.class,
                IncludeFileJob.Factory.class,

                I18NFxmlCoreExtension.class,
                ControllerClassEditor.class,
                CoreEditors.class,

                DocumentationImpl.class,
                ShowDocumentationAction.class,

                FxIdEditor.class,

                PropertyEditorFactory.class
            );
     // @formatter:on
    }

    @Override
    public List<Class<?>> localContextClasses() {
        return List.of();
    }

}
