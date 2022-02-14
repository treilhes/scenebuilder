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
package com.oracle.javafx.scenebuilder.menu.action.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.menu.action.SaveAction;

import javafx.stage.FileChooser;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save"
        ,accelerator = "CTRL+SHIFT+S")

@MenuItemAttachment(
        id = SaveAsAction.MENU_ID,
        targetMenuId = SaveOrSaveAsAction.MENU_ID,
        label = "menu.title.save.as",
        positionRequest = PositionRequest.AsNextSibling)
public class SaveAsAction extends AbstractAction {

    public final static String MENU_ID = "saveAsMenu";

    private final Document document;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final DocumentWindow documentWindow;
    private final FileSystem fileSystem;
    private final RecentItemsPreference recentItemsPreference;
    private final Editor editor;
    private final Main main;
    private final ActionFactory actionFactory;

    public SaveAsAction(
            ActionExtensionFactory extensionFactory,
            @Autowired Document document,
            @Autowired DocumentWindow documentWindow,
            @Autowired Editor editor,
            @Autowired InlineEdit inlineEdit,
            @Autowired Dialog dialog,
            @Autowired FileSystem fileSystem,
            @Autowired ActionFactory actionFactory,
            @Autowired Main main,
            @Autowired RecentItemsPreference recentItemsPreference) {
        super(extensionFactory);
        this.document = document;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.actionFactory = actionFactory;
        this.main = main;
        this.recentItemsPreference = recentItemsPreference;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        final ActionStatus result;
        if (inlineEdit.canGetFxmlText()) {
            final FileChooser fileChooser = new FileChooser();
            final FileChooser.ExtensionFilter f = new FileChooser.ExtensionFilter(
                    I18N.getString("file.filter.label.fxml"),
                    "*.fxml"); // NOI18N
            fileChooser.getExtensionFilters().add(f);
            fileChooser.setInitialDirectory(fileSystem.getNextInitialDirectory());

            File fxmlFile = fileChooser.showSaveDialog(documentWindow.getStage());
            if (fxmlFile == null) {
                result = ActionStatus.CANCELLED;
            } else {
                boolean forgetSave = false;
                // It is only on Linux where you can get the case the path doesn't
                // end with the extension, thanks the behavior of the FX 8 FileChooser
                // on this specific OS (see RT-31956).
                // Below we ask the user if the extension shall be added or not.
                // See DTL-5948.
                final String path = fxmlFile.getPath();
                if (!path.endsWith(".fxml")) { // NOI18N
                    try {
                        URL alternateURL = new URL(fxmlFile.toURI().toURL().toExternalForm() + ".fxml"); // NOI18N
                        File alternateFxmlFile = new File(alternateURL.toURI());
                        final Alert d = dialog.customAlert(documentWindow.getStage());
                        d.setMessage(I18N.getString("alert.save.noextension.message", fxmlFile.getName()));
                        String details = I18N.getString("alert.save.noextension.details");

                        if (alternateFxmlFile.exists()) {
                            details += "\n" // NOI18N
                                    + I18N.getString("alert.save.noextension.details.overwrite",
                                            alternateFxmlFile.getName());
                        }

                        d.setDetails(details);
                        d.setOKButtonVisible(true);
                        d.setOKButtonTitle(I18N.getString("alert.save.noextension.savewith"));
                        d.setDefaultButtonID(ButtonID.OK);
                        d.setShowDefaultButton(true);
                        d.setActionButtonDisable(false);
                        d.setActionButtonVisible(true);
                        d.setActionButtonTitle(I18N.getString("alert.save.noextension.savewithout"));

                        switch (d.showAndWait()) {
                        case ACTION:
                            // Nothing to do, we save with the no extension name
                            break;
                        case CANCEL:
                            forgetSave = true;
                            break;
                        case OK:
                            fxmlFile = alternateFxmlFile;
                            break;
                        }
                    } catch (MalformedURLException | URISyntaxException ex) {
                        forgetSave = true;
                    }
                }

                // Transform File into URL
                final URL newLocation;
                try {
                    newLocation = fxmlFile.toURI().toURL();
                } catch (MalformedURLException x) {
                    // Should not happen
                    throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
                }

                // Checks if fxmlFile is the name of an already opened document
                final Document dwc = main.lookupDocumentWindowControllers(newLocation);
                if (dwc != null && dwc != this) {
                    final Path fxmlPath = Paths.get(fxmlFile.toString());
                    final String fileName = fxmlPath.getFileName().toString();
                    dialog.showErrorAndWait(documentWindow.getStage(), null,
                            I18N.getString("alert.save.conflict.message", fileName),
                            I18N.getString("alert.save.conflict.details"));
                    result = ActionStatus.CANCELLED;
                } else if (forgetSave) {
                    result = ActionStatus.CANCELLED;
                } else {
                    // Recalculates references if needed
                    // TODO(elp)

                    // First change the location of the fxom document
                    editor.setFxmlLocation(newLocation);
                    document.updateLoadFileTime();
                    documentWindow.updateStageTitle();

                    // TODO this case is not handled for using spring, need to take an extra look at
                    // this
                    // TODO this method do nothing for now
                    // TODO more generaly, what to do when using save as ? keep the same beans?
                    // something else
                    // We use same DocumentWindowController BUT we change its fxml :
                    // => reset document preferences
                    // resetDocumentPreferences();

                    // TODO remove after checking the new watching system is operational in
                    // EditorController or in filesystem
                    // watchingController.update();

                    // Now performs a regular save action
                    result = actionFactory.create(SaveAction.class).checkAndPerform();

                    // Keep track of the user choice for next time
                    fileSystem.updateNextInitialDirectory(fxmlFile);

                    // Update recent items with just saved file
                    recentItemsPreference.addRecentItem(fxmlFile);
                }
            }
        } else {
            result = ActionStatus.CANCELLED;
        }

        return result;
    }
}