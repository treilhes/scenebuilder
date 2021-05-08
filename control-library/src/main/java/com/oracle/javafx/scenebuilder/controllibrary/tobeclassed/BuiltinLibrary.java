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
package com.oracle.javafx.scenebuilder.controllibrary.tobeclassed;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.LibraryUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @treatAsPrivate
 */
@Component//("builtinLibrary")
@org.springframework.beans.factory.annotation.Qualifier("builtin")
public class BuiltinLibrary {

    protected final ObservableList<LibraryItemImpl> itemsProperty = FXCollections.observableArrayList();
    
    /*
     * Private
     */

    protected BuiltinLibrary(
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired List<ComponentClassMetadata<?>> componentClassMetadatas
    ) {
        for (ComponentClassMetadata<?> ccm : componentClassMetadatas) {
            for (Map.Entry<String, Qualifier> entry : ccm.getQualifiers().entrySet()) {
                if (entry.getKey() != Qualifier.HIDDEN) {
                    Qualifier qualifier = entry.getValue();
                    String fxmlText = qualifier.getFxmlUrl() == null ? LibraryUtil.makeFxmlText(ccm.getKlass())
                            : readQualifierFxmlText(qualifier, ccm);
                    addItem(ccm, qualifier, fxmlText);
                }
            }
        }
    }

    private void addItem(ComponentClassMetadata<?> ccm, Qualifier qualifier, String fxmlText) {
        final LibraryItemImpl item = new LibraryItemImpl(ccm.getKlass().getSimpleName(), qualifier, fxmlText);
        itemsProperty.add(item);
    }

    

    private static String readQualifierFxmlText(Qualifier componentQualifier,
            ComponentClassMetadata<?> componentMetadata) {
        final URL fxmlURL = componentQualifier.getFxmlUrl();
        assert fxmlURL != null;

        try {
            return FXOMDocument.readContentFromURL(fxmlURL);
        } catch (Exception x) {
            throw new RuntimeException("Unable to love component's fxml: " + fxmlURL + " provided by "
                    + componentMetadata.getClass().getName(), x); // NOI18N
        }
    }
    
    public ObservableList<LibraryItemImpl> getItems() {
        return itemsProperty;
    }
    
    
}
