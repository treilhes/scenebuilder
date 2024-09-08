/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.exporter.controller;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.graalvm.compiler.lir.CompositeValue.Component;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.exporter.format.ExportFormat;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ExporterMenuController {

    private final EditorInstanceWindow document;
    private final List<ExportFormat> formats;
    private final FxmlDocumentManager documentManager;

    public ExporterMenuController(
            @Autowired @Lazy EditorInstanceWindow document,
            @Autowired @Lazy FxmlDocumentManager documentManager,
            @Autowired List<ExportFormat> formats) {
        this.document = document;
        this.documentManager = documentManager;
        this.formats = formats;
    }

    public boolean hasSelectionExportFormat() {
        return formats != null && formats.stream().anyMatch(f -> f.canHandleSelection());
    }

    public boolean hasSceneExportFormat() {
        return formats != null && formats.stream().anyMatch(f -> f.canHandleScene());
    }

    public void performExportSelection() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18N.getString("menu.title.export") + " " + I18N.getString("menu.title.export.selection"));

        List<ExportFormat> sceneFormats = formats.stream().filter(f -> f.canHandleSelection()).collect(Collectors.toList());

        sceneFormats.forEach(f -> fileChooser.getExtensionFilters()
                .add(new ExtensionFilter(f.getDescription(), "*." + f.getExtension())));

        File result = fileChooser.showSaveDialog(document.getStage());
        if (result != null) {
            if (!result.getParentFile().exists()) {
                result.getParentFile().mkdirs();
            }
            ExtensionFilter selectedFilter = fileChooser.getSelectedExtensionFilter();
            String extension = selectedFilter.getExtensions().get(0).substring(2);

            Optional<ExportFormat> format = sceneFormats.stream()
                    .filter(f -> f.getExtension().equalsIgnoreCase(extension)).findFirst();

            if (format.isPresent()) {
                Selection selection = documentManager.selectionDidChange().get().getSelection();
                format.get().exportSelection(selection, result);
            }
        }
    }

    public void performExportScene() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18N.getString("menu.title.export") + " " + I18N.getString("menu.title.export.scene"));

        List<ExportFormat> sceneFormats = formats.stream().filter(f -> f.canHandleScene()).collect(Collectors.toList());

        sceneFormats.forEach(f -> fileChooser.getExtensionFilters()
                .add(new ExtensionFilter(f.getDescription(), "*." + f.getExtension())));

        File result = fileChooser.showSaveDialog(document.getStage());
        if (result != null) {
            if (!result.getParentFile().exists()) {
                result.getParentFile().mkdirs();
            }
            ExtensionFilter selectedFilter = fileChooser.getSelectedExtensionFilter();
            String extension = selectedFilter.getExtensions().get(0).substring(2);

            Optional<ExportFormat> format = sceneFormats.stream()
                    .filter(f -> f.getExtension().equalsIgnoreCase(extension)).findFirst();

            if (format.isPresent()) {
                FXOMDocument fd = documentManager.fxomDocument().get();
                Node rootNode = fd.getFxomRoot().getSceneGraphObject().getAs(Node.class);
                format.get().exportScene(rootNode, result);
            }
        }
    }
}
