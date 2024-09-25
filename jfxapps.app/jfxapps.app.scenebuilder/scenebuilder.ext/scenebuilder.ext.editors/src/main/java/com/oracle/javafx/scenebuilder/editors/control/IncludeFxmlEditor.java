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
package com.oracle.javafx.scenebuilder.editors.control;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.api.Documentation;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 * Editor for including FXML files into the main document (through fx:include).
 */
// FIXME strange editor : instead of editing the currently selected include, it adds include / FIXED, NEED TESTING
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class IncludeFxmlEditor extends InlineListEditor {

    private final StackPane root = new StackPane();

    @FXML
    private Button includeFxmlButton;
    @FXML
    private TextField includeFxmlField;

    private final FileSystem fileSystem;
    private final FxmlDocumentManager documentManager;

    public IncludeFxmlEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            FxmlDocumentManager documentManager) {
        super(dialog, documentation, fileSystem);
        this.fileSystem = fileSystem;
        this.documentManager = documentManager;
        initialize();
    }

    private void initialize() {
        Parent rootInitialBt = FXMLUtils.load(this, "IncludeFXMLButton.fxml");
        Tooltip tooltip = new Tooltip("Include FXML");
        includeFxmlButton.setTooltip(tooltip);
        root.getChildren().add(rootInitialBt);
        super.disableResetValueMenuItem();
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                includeFxmlField.setText(obj.toString());
            }
        }
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void requestFocus() {
        throw new UnsupportedOperationException();
    }

    @FXML
    public void addIncludeFile() {
        File fxmlFile = chooseFxml();
        if (fxmlFile != null) {
        	fileSystem.updateNextInitialDirectory(fxmlFile);
        	String relativePath = getRelativePath(fxmlFile);
            userUpdateValueProperty(relativePath);
            includeFxmlField.setText(relativePath);
        }
    }

    private File chooseFxml() {
        final FileChooser fileChooser = new FileChooser();
        final FileChooser.ExtensionFilter filter
                = new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"), "*.fxml");
        fileChooser.getExtensionFilters().add(filter);
        setInitialDirectory(fileChooser);
        return fileChooser.showOpenDialog(root.getScene().getWindow());
    }

    private void setInitialDirectory(FileChooser fileChooser) {
        //TODO set intial directory from the currently selected file if any
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
    }

    private String getRelativePath(File includedFile) {
        URL url = null;
        try {
            url = includedFile.toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(IncludeFxmlEditor.class.getName()).log(Level.SEVERE, "Path could not be determined.", ex);
        }
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        URL fxmlLocation = fxomDocument == null ? null : fxomDocument.getLocation();
        assert fxmlLocation != null;

        String prefixedValue = PrefixedValue.makePrefixedValue(url, fxmlLocation).toString();
        return removeAtSign(prefixedValue);
    }

    private static String removeAtSign(String prefixedValue) {
        String prefixedValueWithNoAt = "";
        if (prefixedValue.contains("@")) {
            prefixedValueWithNoAt = prefixedValue.replace("@", "");
        }
        return prefixedValueWithNoAt;
    }
}