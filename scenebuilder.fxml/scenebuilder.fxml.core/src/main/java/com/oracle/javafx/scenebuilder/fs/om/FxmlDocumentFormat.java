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
package com.oracle.javafx.scenebuilder.fs.om;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.fs.ContentType;
import com.oracle.javafx.scenebuilder.api.fs.DocumentFormat;
import com.oracle.javafx.scenebuilder.api.fs.DocumentLoader;
import com.oracle.javafx.scenebuilder.api.fs.DocumentLoaderProvider;
import com.oracle.javafx.scenebuilder.api.fs.DocumentSaver;
import com.oracle.javafx.scenebuilder.api.fs.DocumentSaverProvider;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.CombinedResourceBundle;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;

@Component
public class FxmlDocumentFormat implements DocumentFormat {

    private final ContentType contentType;
    private final DocumentLoader loader;
    private final DocumentSaver saver;

    public FxmlDocumentFormat(SceneBuilderBeanFactory context) {
        super();
        this.contentType = new FXMLContentType();
        this.loader = new FXOMDocumentLoader(context);
        this.saver = new FXOMDocumentSaver();
    }

    @Override
    public ContentType contentType() {
        return contentType;
    }

    @Override
    public DocumentLoader loader() {
        return loader;
    }

    @Override
    public DocumentSaver saver() {
        return saver;
    }

    private static class FXMLContentType implements ContentType {

        @Override
        public String getMimeType() {
            return "application/fxml";
        }

        @Override
        public List<String> getExtensions() {
            return List.of("fxml");
        }

        @Override
        public String getLabel() {
            return I18N.getString("file.filter.label.fxml");
        }

    }

    private class FXOMDocumentLoader implements DocumentLoader {

        private final SceneBuilderBeanFactory context;

        public FXOMDocumentLoader(SceneBuilderBeanFactory context) {
            super();
            this.context = context;
        }

        @Override
        public List<ContentType> loadableContentTypes() {
            return List.of(contentType);
        }

        @Override
        public OMDocument<?> load(URL url, ClassLoader classLoader, boolean keepTrackOfLocation) throws IOException {
            FxmlDocumentManager documentManager = context.getBean(FxmlDocumentManager.class);

            String content = FileSystem.read(url);
            I18nResourceProvider resourceConfig = documentManager.i18nResourceConfig().get();
            CombinedResourceBundle bundle = new CombinedResourceBundle(
                    resourceConfig == null ? new ArrayList<>() : resourceConfig.getBundles());

            return new FXOMDocument(content, url, classLoader, bundle);
        }

    }

    private class FXOMDocumentSaver implements DocumentSaver {

        @Override
        public boolean canSave(OMDocument<?> omDocument) {
            return FXOMDocument.class.isInstance(omDocument);
        }

        @Override
        public ContentType targetContentType() {
            return contentType;
        }

        @Override
        public boolean save(OMDocument<?> omDocument, Path path) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    @Component
    public static class FXOMDocumentLoaderProvider implements DocumentLoaderProvider {
        private final FXOMDocumentLoader loader;

        public FXOMDocumentLoaderProvider(FXOMDocumentLoader loader) {
            super();
            this.loader = loader;
        }

        @Override
        public List<DocumentLoader> documentLoaders() {
            return List.of(loader);
        }
    }

    @Component
    public static class FXOMDocumentSaverProvider implements DocumentSaverProvider {
        private final FXOMDocumentSaver saver;

        public FXOMDocumentSaverProvider(FXOMDocumentSaver saver) {
            super();
            this.saver = saver;
        }

        @Override
        public List<DocumentSaver> documentSavers() {
            return List.of(saver);
        }

    }
}
