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
package com.oracle.javafx.scenebuilder.menu.action;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.preferences.Preferences;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class LoadUrlAction extends AbstractAction {

    private final Document document;
    private final DocumentWindow documentWindow;
    private final Editor editor;
    private final Preferences preferences;

    private URL fxmlURL;
    private boolean keepTrackOfLocation;

    protected LoadUrlAction(
            @Autowired ActionExtensionFactory extensionFactory,
            @Autowired Document document,
            @Autowired DocumentWindow documentWindow,
            @Autowired Preferences preferences,
            @Autowired Editor editor
            ) {
        super(extensionFactory);
        this.document = document;
        this.editor = editor;
        this.preferences = preferences;
        this.documentWindow = documentWindow;
    }

    public void setKeepTrackOfLocation(boolean keepTrackOfLocation) {
        this.keepTrackOfLocation = keepTrackOfLocation;
    }
    public void setFxmlURL(URL fxmlURL) {
        this.fxmlURL = fxmlURL;
    }

    public URL getFxmlURL() {
        return fxmlURL;
    }

    public boolean isKeepTrackOfLocation() {
        return keepTrackOfLocation;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        assert fxmlURL != null;
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editor.setFxmlTextAndLocation(fxmlText, keepTrackOfLocation ? fxmlURL : null, false);
            document.updateLoadFileTime();
            documentWindow.updateStageTitle(); // No-op if fxml has not been loaded yet

            documentWindow.untrack();
            preferences.readFromJavaPreferences();

            SbPlatform.runForDocumentLater(() -> {
                documentWindow.apply();
                documentWindow.track();
            });
            // TODO remove after checking the new watching system is operational in
            // EditorController or in filesystem
            // watchingController.update();
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
        return ActionStatus.DONE;
    }

}