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
package com.oracle.javafx.scenebuilder.ui.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.boot.layer.ApplicationManager;
import com.gluonhq.jfxapps.boot.loader.extension.EditorExtension;
import com.oracle.javafx.scenebuilder.api.editors.EditorDescriptor;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstancesManager;
import com.oracle.javafx.scenebuilder.api.editors.EditorsManager;

@Singleton
public class EditorsManagerImpl implements EditorsManager {

    private static final Logger logger = LoggerFactory.getLogger(EditorsManagerImpl.class);

    private final ApplicationManager applicationManager;

    private final Map<EditorExtension, EditorInstancesManager> loadedEditors;

    public EditorsManagerImpl(ApplicationManager applicationManager) {
        super();
        this.applicationManager = applicationManager;
        this.loadedEditors = new HashMap<>();
    }

    @Override
    public Set<EditorDescriptor> getAvailableEditors() {
        return applicationManager.getEditors().stream().map(EditorDescriptor::fromExtension).collect(Collectors.toSet());
    }

    @Override
    public Set<EditorInstancesManager> getLoadedEditors() {
        return new HashSet<>(loadedEditors.values());
    }

    @Override
    public EditorInstancesManager makeNewEditor(EditorDescriptor editorDescriptor) {

        if (loadedEditors.containsKey(editorDescriptor.getExtension())) {
            return loadedEditors.get(editorDescriptor.getExtension());
        }

        UUID extensionId = editorDescriptor.getExtension().getId();

        applicationManager.startApplication(extensionId);

        Optional<EditorInstancesManager> editor = applicationManager.getContext(extensionId)
                .map(c -> c.getBean(EditorInstancesManager.class));

        editor.ifPresent(ed -> loadedEditors.put(editorDescriptor.getExtension(), ed));

        return editor.orElse(null);
    }

    @Override
    public EditorInstancesManager lookupEditor(EditorDescriptor editorExtension) {
        return loadedEditors.get(editorExtension.getExtension());
    }

    @Override
    public void close(EditorDescriptor editorDescriptor) {
        close(editorDescriptor.getExtension());
    }
    public void close(EditorExtension editorExtension) {
        EditorInstancesManager editor = loadedEditors.get(editorExtension);

        boolean canStop = true;
        if (editor != null) {
            editor.close();
            canStop = editor.getOpenDocuments() == 0;
        }

        if (canStop) {
            loadedEditors.remove(editorExtension);
            applicationManager.stopApplication(editorExtension.getId());
            notifyEditorClosed(editor);
        }
    }

    @Override
    public void close() {
        Set<EditorExtension> editors = loadedEditors.keySet();
        editors.forEach(this::close);
    }

    @Override
    public void notifyEditorClosed(EditorInstancesManager editor) {
        // TODO Auto-generated method stub

    }


    @Override
    public Optional<EditorDescriptor> getAvailableEditor(UUID editorId) {
        return getAvailableEditors().stream().filter(e -> e.getExtension().getId().equals(editorId)).findFirst();
    }

}
