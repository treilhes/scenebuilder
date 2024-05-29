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
package com.oracle.javafx.scenebuilder.fxml.clipboard.internal;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardDataFormat;
import com.gluonhq.jfxapps.core.api.om.OMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMArchive;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ScenebuilderDataFormat implements ClipboardDataFormat {

    // Internal SB2 data format
    static final DataFormat SB_DATA_FORMAT = new DataFormat("com.oracle.javafx.scenebuilder2/internal"); // NOCHECK

    private final FxmlDocumentManager documentManager;

    public ScenebuilderDataFormat(
            FxmlDocumentManager documentManager) {
        super();
        this.documentManager = documentManager;
    }

    public DataFormat getDataFormat() {
        return SB_DATA_FORMAT;
    }

    @Override
    public boolean hasDecodableContent(Clipboard clipboard) {
        return clipboard.hasContent(SB_DATA_FORMAT);
    }

    @Override
    public List<? extends OMObject> decode(Clipboard clipboard, Consumer<Exception> errorHandler) throws Exception {

        FXOMDocument targetDocument = documentManager.fxomDocument().get();
        assert targetDocument != null;

        List<FXOMObject> result = null;

        // SB_DATA_FORMAT
        if (clipboard.hasContent(SB_DATA_FORMAT)) {
            final Object content = clipboard.getContent(SB_DATA_FORMAT);
            if (content instanceof FXOMArchive) {
                final FXOMArchive archive = (FXOMArchive) content;
                try {
                    result = archive.decode(targetDocument);
                } catch(IOException x) {
                    if (errorHandler != null) {
                        errorHandler.accept(x);
                    } else {
                        throw x;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean isEncodable(List<? extends OMObject> omObjects) {
        return omObjects != null && omObjects.isEmpty() == false && omObjects.stream().allMatch(FXOMObject.class::isInstance);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClipboardContent encode(List<? extends OMObject> omObjects) {
        assert isEncodable(omObjects);

        final List<FXOMObject> fxomObjects = (List<FXOMObject>)omObjects;
        final ClipboardContent result = new ClipboardContent();

        // SB_DATA_FORMAT
        result.put(SB_DATA_FORMAT, new FXOMArchive(fxomObjects));

        return result;
    }


}
