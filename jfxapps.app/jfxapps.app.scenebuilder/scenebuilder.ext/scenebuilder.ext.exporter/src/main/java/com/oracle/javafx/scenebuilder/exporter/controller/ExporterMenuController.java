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

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.exporter.format.ExportFormat;

import jakarta.inject.Provider;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 */
@ApplicationInstanceSingleton
public class ExporterMenuController {

    private final I18N i18n;
    private final Provider<MainInstanceWindow> document;
    private final List<ExportFormat> formats;
    private final ApplicationInstanceEvents documentManager;

    public ExporterMenuController(
            I18N i18n,
            Provider<MainInstanceWindow> document,
            ApplicationInstanceEvents documentManager,
            List<ExportFormat> formats) {
        this.i18n = i18n;
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
        fileChooser.setTitle(i18n.getString("menu.title.export") + " " + i18n.getString("menu.title.export.selection"));

        List<ExportFormat> sceneFormats = formats.stream().filter(f -> f.canHandleSelection()).collect(Collectors.toList());

        sceneFormats.forEach(f -> fileChooser.getExtensionFilters()
                .add(new ExtensionFilter(f.getDescription(), "*." + f.getExtension())));

        var stage = document.get().getStage();
        File result = fileChooser.showSaveDialog(stage);
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
        fileChooser.setTitle(i18n.getString("menu.title.export") + " " + i18n.getString("menu.title.export.scene"));

        List<ExportFormat> sceneFormats = formats.stream().filter(f -> f.canHandleScene()).collect(Collectors.toList());

        sceneFormats.forEach(f -> fileChooser.getExtensionFilters()
                .add(new ExtensionFilter(f.getDescription(), "*." + f.getExtension())));

        var stage = document.get().getStage();
        File result = fileChooser.showSaveDialog(stage);
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
