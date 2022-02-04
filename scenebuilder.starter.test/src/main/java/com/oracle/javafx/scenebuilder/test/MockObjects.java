/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.test;

import java.io.File;
import java.util.function.Consumer;

import org.mockito.Mockito;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.ApiDoc;
import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.ErrorReport;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Glossary;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;

public class MockObjects {

    public static Api buildApiMock() {
        return buildApiMock(null);
    }

    public static <T> Api buildApiMock(MockConfig config) {

        try {
          //other
            Editor editor = mock(Editor.class, config);
            Mockito.when(editor.getFxmlLocation()).thenReturn(new File(".").toURI().toURL());

            // singleton
            Api api = mock(Api.class, config);
            SceneBuilderBeanFactory sceneBuilderBeanFactory = mock(SceneBuilderBeanFactory.class, config);
            Main main = mock(Main.class, config);
            NetworkManager networkManager = mock(NetworkManager.class, config);
            SceneBuilderManager sceneBuilderManager = new SceneBuilderManager.SceneBuilderManagerImpl();
            FileSystem fileSystem = mock(FileSystem.class, config);
            Glossary glossary = mock(Glossary.class, config);
            IconSetting iconSetting = mock(IconSetting.class, config);
            Documentation documentation = mock(Documentation.class, config);
            Metadata metadata = mock(Metadata.class, config);

            Mockito.when(api.getFileSystem()).thenReturn(fileSystem);
            Mockito.when(api.getContext()).thenReturn(sceneBuilderBeanFactory);
            Mockito.when(api.getMain()).thenReturn(main);
            Mockito.when(api.getNetworkManager()).thenReturn(networkManager);
            Mockito.when(api.getSceneBuilderManager()).thenReturn(sceneBuilderManager);
            Mockito.when(api.getFileSystem()).thenReturn(fileSystem);
            Mockito.when(api.getGlossary()).thenReturn(glossary);
            Mockito.when(api.getIconSetting()).thenReturn(iconSetting);
            Mockito.when(api.getDocumentation()).thenReturn(documentation);
            Mockito.when(api.getMetadata()).thenReturn(metadata);

            // document
            ApiDoc apiDoc = mock(ApiDoc.class, config);
            Mockito.when(api.getApiDoc()).thenReturn(apiDoc);

            //DocumentManager documentManager = mock(DocumentManager.class);
            DocumentManager documentManager = new DocumentManager.DocumentManagerImpl();
            JobManager jobManager = mock(JobManager.class, config);
            //JobManager jm = new JobManagerImpl(sceneBuilderBeanFactory, documentManager, editor);
            Dialog dialog = mock(Dialog.class, config);
            MessageLogger messageLogger = mock(MessageLogger.class, config);
            Selection selection = mock(Selection.class, config);
            ErrorReport errorReport = mock(ErrorReport.class, config);
            Drag drag = mock(Drag.class, config);
            ContextMenu contextMenu = mock(ContextMenu.class, config);
            InlineEdit inlineEdit = mock(InlineEdit.class, config);


            Mockito.when(apiDoc.getDocumentManager()).thenReturn(documentManager);
            Mockito.when(apiDoc.getJobManager()).thenReturn(jobManager);
            Mockito.when(apiDoc.getDialog()).thenReturn(dialog);
            Mockito.when(apiDoc.getMessageLogger()).thenReturn(messageLogger);
            Mockito.when(apiDoc.getSelection()).thenReturn(selection);
            Mockito.when(apiDoc.getErrorReport()).thenReturn(errorReport);
            Mockito.when(apiDoc.getDrag()).thenReturn(drag);
            Mockito.when(apiDoc.getContextMenu()).thenReturn(contextMenu);
            Mockito.when(apiDoc.getInlineEdit()).thenReturn(inlineEdit);



            Mockito.when(sceneBuilderBeanFactory.getBean(Editor.class)).thenReturn(editor);

            api.afterPropertiesSet();

            return api;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T mock(Class<T> t, MockConfig config) {
        T mocked = Mockito.mock(t);
        if (config != null && config.getMap().containsKey(t)) {
            ((Consumer<T>)config.getMap().get(t)).accept(mocked);
        }
        return mocked;
    }
}
