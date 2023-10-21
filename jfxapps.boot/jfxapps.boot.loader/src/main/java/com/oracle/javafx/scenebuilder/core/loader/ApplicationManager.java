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
package com.oracle.javafx.scenebuilder.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionValidation;
import com.oracle.javafx.scenebuilder.core.loader.extension.EditorExtension;
import com.oracle.javafx.scenebuilder.core.loader.internal.ApplicationManagerImpl;
import com.oracle.javafx.scenebuilder.core.loader.model.Application;

public interface ApplicationManager {

    public static ApplicationManager get(Path root) {
        return new ApplicationManagerImpl(root);
    }
    /**
     * Get the current state of the application
     * @return clone of the current state
     */
    public Application getApplication();

    boolean hasSavedApplication();
    void saveApplication();
    void loadApplication(InputStream jsonStream) throws IOException;
    void setApplication(Application application);
    void loadApplication();


    void load();
    void load(ProgressListener progressListener);
    void start() throws BootException;
    void start(ProgressListener progressListener) throws BootException;
    void startEditor(UUID editorId);
    void startEditor(UUID editorId, ProgressListener progressListener);
    void send(OpenCommandEvent parameters);
    void stopEditor(UUID editorId);
    void stop();
    void unload();
    void unloadEditor(UUID editorId);

    void remove(UUID extensionId);
    void disable(UUID extensionId);
    void add(ExtensionContentProvider provider, ExtensionValidation validation);

    ExtensionReport getReport(UUID id);

    Set<EditorExtension> getEditors();
    Optional<SbContext> getContext(UUID extensionId);



}
