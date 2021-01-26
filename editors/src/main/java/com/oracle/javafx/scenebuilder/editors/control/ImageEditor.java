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
package com.oracle.javafx.scenebuilder.editors.control;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignImage;
import com.oracle.javafx.scenebuilder.core.metadata.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

/**
 * Image property editor (handle the url path).
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImageEditor extends AbstractPropertyEditor {

    @FXML
    private Label prefixLb;
    @FXML
    private TextField imagePathTf;

    private Parent root;
    private DesignImage image = null;

    private final MenuItem documentRelativeMenuItem
            = new MenuItem(I18N.getString("inspector.resource.documentrelative"));
    private final MenuItem classPathRelativeMenuItem
            = new MenuItem(I18N.getString("inspector.resource.classpathrelative"));
    private final MenuItem absoluteMenuItem
            = new MenuItem(I18N.getString("inspector.resource.absolute"));

    private PrefixedValue.Type type = PrefixedValue.Type.PLAIN_STRING;
    private URL fxmlFileLocation;
	private final FileSystem fileSystem;
    private FXOMDocument fxomDocument;

    public ImageEditor(
            @Autowired Api api) {
        super(api);
        this.fileSystem = api.getFileSystem();
        api.getApiDoc().getDocumentManager().fxomDocument().subscribe(fxom -> this.fxomDocument = fxom);
        initialize(null);
    }

    // Separate method to please FindBugs
    private void initialize(URL fxmlFileLocation) {
        this.fxmlFileLocation = fxmlFileLocation;
        root = FXMLUtils.load(this, "ImageEditor.fxml"); //NOI18N

        EventHandler<ActionEvent> valueListener = event -> {
            Image imageObj;
            String prefixedValue = null;
            URL url;
            try {
                String suffix = imagePathTf.getText();
                if (suffix == null || suffix.isEmpty()) {
                    image = null;
                    switchType(PrefixedValue.Type.PLAIN_STRING);
                    userUpdateValueProperty(image);
                    return;
                }
                prefixedValue = new PrefixedValue(type, suffix).toString();
                url = EditorUtils.getUrl(suffix, type, ImageEditor.this.fxmlFileLocation);
                imageObj = new Image(url != null ? url.toExternalForm() : null);
            } catch (NullPointerException | IllegalArgumentException ex) {
                // Always happen for classpath relative, or if the url cannot be resolved.
                // In this case we cannot resolve the reference, so we use a dummy image.
                imageObj = new Image(DesignImage.getVoidImageUrl().toExternalForm());
            }
            image = new DesignImage(imageObj, prefixedValue);
            userUpdateValueProperty(image);
        };
        setTextEditorBehavior(this, imagePathTf, valueListener);

        documentRelativeMenuItem.setOnAction(e -> switchType(PrefixedValue.Type.DOCUMENT_RELATIVE_PATH));
        classPathRelativeMenuItem.setOnAction(e -> switchType(PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH));
        absoluteMenuItem.setOnAction(e -> switchType(PrefixedValue.Type.PLAIN_STRING));
        getMenu().getItems().addAll(documentRelativeMenuItem, classPathRelativeMenuItem, absoluteMenuItem);
        removeLabel();
        updateMenuItems();
    }

    private void switchType(PrefixedValue.Type newType) {
        String suffix = imagePathTf.getText();
        if (suffix == null || suffix.isEmpty()) {
            type = newType;
            updateMenuItems();
            handlePrefix();
            return;
        }
        // Get the current url
        URL url = EditorUtils.getUrl(suffix, type, fxmlFileLocation);
        // Switch to the new type now
        String newSuffix = null;
        if ((url == null) || (newType == PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH)) {
            // In this case we empty the text field (i.e. suffix) content
            newSuffix = ""; //NOI18N
        } else if (newType == PrefixedValue.Type.PLAIN_STRING) {
            newSuffix = url.toExternalForm();
        } else if (newType == PrefixedValue.Type.DOCUMENT_RELATIVE_PATH) {
            newSuffix = PrefixedValue.makePrefixedValue(url, fxmlFileLocation).getSuffix();
        }
        assert newSuffix != null;
        imagePathTf.setText(newSuffix);
        type = newType;
        // call the text field listener
        if (!newSuffix.isEmpty()) {
            getCommitListener().handle(null);
        }
        updateMenuItems();
        handlePrefix();
    }

    private void updateMenuItems() {
        documentRelativeMenuItem.setDisable(false);
        classPathRelativeMenuItem.setDisable(false);
        absoluteMenuItem.setDisable(false);
        if (fxmlFileLocation == null) {
            documentRelativeMenuItem.setDisable(true);
        }
        if (type == PrefixedValue.Type.DOCUMENT_RELATIVE_PATH) {
            documentRelativeMenuItem.setDisable(true);
        } else if (type == PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH) {
            classPathRelativeMenuItem.setDisable(true);
        } else if (type == PrefixedValue.Type.PLAIN_STRING) {
            absoluteMenuItem.setDisable(true);
        }
    }

    protected void handlePrefix() {
        if (type == PrefixedValue.Type.DOCUMENT_RELATIVE_PATH) {
            setPrefix(FXMLLoader.RELATIVE_PATH_PREFIX);
        } else if (type == PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH) {
            setPrefix(FXMLLoader.RELATIVE_PATH_PREFIX + "/");//NOI18N
        } else {
            // absolute
            removeLabel();
        }
    }

    private void setPrefix(String str) {
        if (!prefixLb.isVisible()) {
            prefixLb.setVisible(true);
            prefixLb.setManaged(true);
        }
        prefixLb.setText(str);
    }

    private void removeLabel() {
        prefixLb.setVisible(false);
        prefixLb.setManaged(false);
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
    }

    @Override
    public Object getValue() {
        return image;
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        if (value == null) {
            image = null;
            imagePathTf.setText(""); //NOI18N
        } else {
            assert value instanceof DesignImage;
            image = (DesignImage) value;
            PrefixedValue prefixedValue = new PrefixedValue(image.getLocation());
            imagePathTf.setText(prefixedValue.getSuffix());
            type = prefixedValue.getType();
            handlePrefix();
            updateMenuItems();
        }
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);
        this.fxmlFileLocation = fxomDocument == null ? null : fxomDocument.getLocation();
        imagePathTf.setPromptText(null);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(imagePathTf);
    }

    //
    // FXML methods
    //
    @FXML
    void chooseImage(ActionEvent event) {
        String[] extensions = {"*.jpg", "*.jpeg", "*.png", "*.gif"}; //NOI18N
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18N.getString("inspector.select.image"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        I18N.getString("inspector.select.image"),
                        Arrays.asList(extensions)));
        fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());
        File file = fileChooser.showOpenDialog(imagePathTf.getScene().getWindow());
        if ((file == null)) {
            return;
        }
        // Keep track of the user choice for next time
        fileSystem.updateNextInitialDirectory(file);
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid URL", ex); //NOI18N
        }

        // If the document exists, make the type as document relative by default.
        String urlStr;
        if (fxmlFileLocation != null) {
            urlStr = PrefixedValue.makePrefixedValue(url, fxmlFileLocation).toString();
            switchType(PrefixedValue.Type.DOCUMENT_RELATIVE_PATH);
        } else {
            urlStr = url.toExternalForm();
            switchType(PrefixedValue.Type.PLAIN_STRING);
        }
        PrefixedValue prefixedValue = new PrefixedValue(urlStr);
        type = prefixedValue.getType();
        String suffix = prefixedValue.getSuffix();
        imagePathTf.setText(suffix);
        image = new DesignImage(new Image(EditorUtils.getUrl(suffix, type, fxmlFileLocation).toExternalForm()), prefixedValue.toString());
        userUpdateValueProperty(getValue());
        updateMenuItems();
        handlePrefix();
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> imagePathTf.requestFocus());
    }
}
