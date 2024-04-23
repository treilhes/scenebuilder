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
package com.oracle.javafx.scenebuilder.core.doc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.scenebuilder.fxml.api.Documentation;
import org.scenebuilder.fxml.api.DocumentationUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;

@Component
@Lazy
public class DocumentationImpl implements Documentation {

    private final List<DocumentationUrlBuilder> urlBuilders;
    private final FileSystem fileSystem;
    private final IMetadata metadata;

    public DocumentationImpl(
            @Autowired IMetadata metadata,
            @Autowired FileSystem fileSystem,
            @Autowired List<DocumentationUrlBuilder> urlBuilders
            ) {
        this.metadata = metadata;
        this.fileSystem = fileSystem;
        this.urlBuilders = urlBuilders;
    }

    @Override
    public void openDocumentationUrl(Set<Class<?>> selectedClasses, ValuePropertyMetadata propMeta) {
        try {

            if (propMeta != null && selectedClasses != null) {
                if (selectedClasses.size() <= 1) {
                    openUrl(fileSystem, selectedClasses, propMeta);
                }
            } else {
                // Special case for non-properties (fx:id, ...)
                fileSystem.open(DEFAULT_JAVADOC_HOME
                        + "javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html"); //NOCHECK
            }
            // Selection of multiple different classes ==> no link
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void openUrl(FileSystem fileSystem, Set<Class<?>> selectedClasses, ValuePropertyMetadata propMeta) throws IOException {
        Class<?> clazz = null;
        // In case of static property, we don't care of the selectedClasses
        if (selectedClasses != null) {
            for (Class<?> cl : selectedClasses) {
                clazz = cl;
            }
        }
        PropertyName propertyName = propMeta.getName();
        if (propMeta.isStaticProperty()) {
            clazz = propertyName.getResidenceClass();
        } else {
            ComponentClassMetadata<?> cmp = metadata.queryComponentMetadata(clazz, propertyName);
            clazz = cmp == null ? null : cmp.getKlass();
        }

        for (DocumentationUrlBuilder urlBuilder:urlBuilders) {
            if (urlBuilder.canBuild(clazz)) {
                fileSystem.open(urlBuilder.buildUrl(clazz, propMeta));
            }
        }
    }

}
