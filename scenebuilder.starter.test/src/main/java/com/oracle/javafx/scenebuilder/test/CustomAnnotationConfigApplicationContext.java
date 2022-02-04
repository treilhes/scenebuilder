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
package com.oracle.javafx.scenebuilder.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Document.DocumentControlAction;
import com.oracle.javafx.scenebuilder.api.Document.DocumentEditAction;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.Action.ActionStatus;
import com.oracle.javafx.scenebuilder.api.di.DocumentScope;

/**
 * @author ptreilhes
 *
 */
public class CustomAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {

    public CustomAnnotationConfigApplicationContext() {
        super(new AutoMockBeanFactory());
    }

    public CustomAnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        this();
        this.register(annotatedClasses);
        this.refresh();
        DocumentScope.setCurrentScope(new Document() {

            @Override
            public boolean isInited() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isUnused() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isDocumentDirty() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean hasContent() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean hasName() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void loadFromFile(File fxmlFile) throws IOException {
                // TODO Auto-generated method stub

            }

            @Override
            public void loadFromURL(URL url, boolean keepTrackOfLocation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void openWindow() {
                // TODO Auto-generated method stub

            }

            @Override
            public void updatePreferences() {
                // TODO Auto-generated method stub

            }

            @Override
            public void updateWithDefaultContent() {
                // TODO Auto-generated method stub

            }

            @Override
            public void performImportFxml() {
                // TODO Auto-generated method stub

            }

            @Override
            public void performIncludeFxml() {
                // TODO Auto-generated method stub

            }

            @Override
            public void performImportMedia() {
                // TODO Auto-generated method stub

            }

            @Override
            public void performControlAction(DocumentControlAction toggleRightPanel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCloseRequest() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFocus() {
                // TODO Auto-generated method stub

            }

            @Override
            public DocumentWindow getDocumentWindow() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean canPerformEditAction(DocumentEditAction editAction) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void performEditAction(DocumentEditAction editAction) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean canPerformControlAction(DocumentControlAction controlAction) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public URL getFxmlLocation() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void closeWindow() {
                // TODO Auto-generated method stub

            }

            @Override
            public Editor getEditorController() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public FileTime getLoadFileTime() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void updateLoadFileTime() {
                // TODO Auto-generated method stub

            }

            @Override
            public ActionStatus performCloseAction() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }
}
