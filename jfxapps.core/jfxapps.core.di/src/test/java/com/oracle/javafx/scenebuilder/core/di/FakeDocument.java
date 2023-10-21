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
package com.oracle.javafx.scenebuilder.core.di;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstance;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.core.context.SbContext;

@Component(DocumentScope.SCOPE_OBJECT_NAME)
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class FakeDocument implements EditorInstance {

    private final DocumentScopedObject documentScopedObject;

    public FakeDocument(DocumentScopedObject documentScopedObject) {
        super();
        this.documentScopedObject = documentScopedObject;
    }

    public DocumentScopedObject getDocumentScopedObject() {
        return documentScopedObject;
    }

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
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public EditorInstanceWindow getDocumentWindow() {
        // TODO Auto-generated method stub
        return null;
    }

//    @Override
//    public URL getFxmlLocation() {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public void closeWindow() {
        // TODO Auto-generated method stub

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
    public boolean isEditing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void logInfoMessage(String key) {
        // TODO Auto-generated method stub

    }

    @Override
    public void logInfoMessage(String key, Object... args) {
        // TODO Auto-generated method stub

    }

    @Override
    public URL getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

}
