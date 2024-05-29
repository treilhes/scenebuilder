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
package com.oracle.javafx.scenebuilder.editor.fxml.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.scenebuilder.fxml.api.SbEditor;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.application.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.error.ErrorReport;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.CombinedResourceBundle;
import com.gluonhq.jfxapps.core.api.i18n.I18nResourceProvider;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.misc.InlineEdit;
import com.gluonhq.jfxapps.core.api.ui.misc.MessageLogger;
import com.gluonhq.jfxapps.core.fxom.FXOMAssetIndex;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.selection.ObjectSelectionGroup;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

/**
 * An editor controller is the central object which coordinates the editing of
 * an FXML document across the different panels (hierarchy, content,
 * inspector...).
 * <p>
 * An editor controller is associated to an FXML document. It can perform
 * editing and control actions on this document. It also maintains the list of
 * objects selected by the user.
 * <p>
 * Some panel controllers can be attached to an editor controller. They listen
 * to the editor and update their content accordingly.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class EditorController implements SbEditor, InitWithDocument {

    private final Selection selection;
    private final JobManager jobManager;
    private final MessageLogger messageLogger;
    private final ErrorReport errorReport;
    private final InlineEdit inlineEdit;

    private final ObjectProperty<URL> fxmlLocationProperty;
    private final BooleanProperty pickModeEnabledProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty sampleDataEnabledProperty = new SimpleBooleanProperty(false);

    // private Callback<Void, Boolean> requestTextEditingSessionEnd;

    // private Stage ownerWindow;

    private final FileSystem fileSystem;
    private I18nResourceProvider resourceConfig;
    private final FxmlDocumentManager documentManager;

    private FXOMDocument fxomDocument;
    private final SceneBuilderManager sceneBuilderManager;

    /**
     * Creates an empty editor controller (ie it has no associated fxom document).
     */
    // @formatter:off
    public EditorController(
            SceneBuilderManager sceneBuilderManager,
            JobManager jobManager,
            FileSystem fileSystem,
            MessageLogger messageLogger,
            Selection selection,
            FxmlDocumentManager documentManager,
            ErrorReport errorReport,
            InlineEdit inlineEditController
        	) {
        // @formatter:on
        // this.api = api;
        this.sceneBuilderManager = sceneBuilderManager;
        this.jobManager = jobManager;
        this.fileSystem = fileSystem;
//    	this.dragController = dragController;
        this.messageLogger = messageLogger;
        this.selection = selection;
        this.documentManager = documentManager;
        this.errorReport = errorReport;
        this.inlineEdit = inlineEditController;

        fxmlLocationProperty = new SimpleObjectProperty<>();

    }


    @Override
    public void initWithDocument() {
        documentManager.i18nResourceConfig().subscribe(s -> {
            resourceConfig = s;
            resourcesDidChange();
        });
        jobManager.revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());
        documentManager.fxomDocument().subscribe(cl -> fxomDocumentDidChange(cl));
        sceneBuilderManager.classloader().subscribe(cl -> libraryClassLoaderDidChange(cl));
    }


//    /**
//     * Sets the fxml content to be edited by this editor. A null value makes this
//     * editor empty.
//     *
//     * @param fxmlText null or the fxml text to be edited
//     * @throws IOException if fxml text cannot be parsed and loaded correctly.
//     */
//    private void setFxmlText(String fxmlText, boolean checkGluonControls) throws IOException {
//        setFxmlTextAndLocation(fxmlText, getFxmlLocation(), checkGluonControls);
//    }

    /**
     * Returns null or the fxml content being edited by this editor.
     *
     * @return null or the fxml content being edited by this editor.
     * @param wildcardImports If the FXML should have wildcards in its imports.
     */
    @Override
    public String getFxmlText(boolean wildcardImports) {
        final String result;

        final FXOMDocument fxomDocument = getFxomDocument();
        if (fxomDocument == null) {
            result = null;
        } else {
            final boolean sampleDataEnabled = fxomDocument.isSampleDataEnabled();
            if (sampleDataEnabled) {
                fxomDocument.setSampleDataEnabled(false);
            }
            result = fxomDocument.getFxmlText(wildcardImports);
            if (sampleDataEnabled) {
                fxomDocument.setSampleDataEnabled(true);
            }
        }

        return result;
    }

//    /**
//     * Tells this editor that a text editing session has started. The editor
//     * controller may invoke the requestSessionEnd() callback if it needs the text
//     * editing session to stop. The callback should; - either stop the text editing
//     * session, invoke textEditingSessionDidEnd() and return true - either keep the
//     * text editing session on-going and return false
//     *
//     * @param requestSessionEnd Callback that should end the text editing session or
//     *                          return false
//     */
//    @Override
//    public void textEditingSessionDidBegin(Callback<Void, Boolean> requestSessionEnd) {
////        assert requestTextEditingSessionEnd == null;
////        requestTextEditingSessionEnd = requestSessionEnd;
//        inlineEdit.textEditingSessionDidBegin(requestSessionEnd);
//    }
//
//    /**
//     * Tells this editor that the text editing session has ended.
//     */
//    @Override
//    public void textEditingSessionDidEnd() {
//        // assert requestTextEditingSessionEnd != null;
//        // requestTextEditingSessionEnd = null;
//        inlineEdit.textEditingSessionDidEnd();
//    }

    /**
     * The property holding the fxml location associated to this editor.
     *
     * @return the property holding the fxml location associated to this editor.
     */
    @Override
    public ObservableValue<URL> fxmlLocationProperty() {
        return fxmlLocationProperty;
    }

    /**
     * Sets the location of the fxml being edited. If null value is passed, fxml
     * text is being interpreted with any location (ie some references may be
     * broken).
     *
     * @param fxmlLocation null or the location of the fxml being edited.
     */
    @Override
    public void setFxmlLocation(URL fxmlLocation) {
        fxmlLocationProperty.setValue(fxmlLocation);
        if (getFxomDocument() != null) {
            getFxomDocument().setLocation(fxmlLocation);
            jobManager.clear(); // Because FXOMDocument.setLocation() mutates the document
        }
        if (fxmlLocation != null) {
            final File newInitialDirectory = new File(fxmlLocation.getPath());
            fileSystem.updateNextInitialDirectory(newInitialDirectory);
        }
    }

    /**
     * Returns true if 'pick mode' is enabled for this editor.
     *
     * @return true if 'pick mode' is enabled for this editor.
     */
    @Override
    public boolean isPickModeEnabled() {
        return pickModeEnabledProperty.getValue();
    }

    /**
     * Enables or disables 'pick mode' on this editor.
     *
     * @param pickModeEnabled true if 'pick mode' should be enabled.
     */
    @Override
    public void setPickModeEnabled(boolean pickModeEnabled) {
        pickModeEnabledProperty.setValue(pickModeEnabled);
    }

    /**
     * The property indicating if 'pick mode' is enabled or not.
     *
     * @return the property indicating if 'pick mode' is enabled or not.
     */
    @Override
    public ObservableValue<Boolean> pickModeEnabledProperty() {
        return pickModeEnabledProperty;
    }

    /**
     * Returns true if content and preview panels attached to this editor should
     * display sample data.
     *
     * @return true if content and preview panels should display sample data.
     */
    @Override
    public boolean isSampleDataEnabled() {
        return sampleDataEnabledProperty.getValue();
    }

    /**
     * Enables or disables display of sample data in content and preview panels
     * attached to this editor.
     *
     * @param sampleDataEnabled true if sample data should be displayed
     */
    @Override
    public void setSampleDataEnabled(boolean sampleDataEnabled) {
        setPickModeEnabled(false);
        sampleDataEnabledProperty.setValue(sampleDataEnabled);
        if (getFxomDocument() != null) {
            getFxomDocument().setSampleDataEnabled(isSampleDataEnabled());
        }
    }

    /**
     * The property indicating if sample data should be displayed or not.
     *
     * @return the property indicating if sample data should be displayed or not.
     */
    @Override
    public ObservableValue<Boolean> sampleDataEnabledProperty() {
        return sampleDataEnabledProperty;
    }

    /**
     * Returns null or the location of the fxml being edited.
     *
     * @return null or the location of the fxml being edited.
     */
    @Override
    public URL getFxmlLocation() {
        return fxmlLocationProperty.getValue();
    }

//    /**
//     * Sets both fxml text and location to be edited by this editor. Performs
//     * setFxmlText() and setFxmlLocation() but in a optimized manner (it avoids an
//     * extra scene graph refresh).
//     *
//     * @param fxmlText     null or the fxml text to be edited
//     * @param fxmlLocation null or the location of the fxml text being edited
//     * @throws IOException if fxml text cannot be parsed and loaded correctly.
//     */
//    private void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation) throws IOException {
//        setFxmlTextAndLocation(fxmlText, fxmlLocation, false);
//    }

    /**
     * Sets both fxml text and location to be edited by this editor. Performs
     * setFxmlText() and setFxmlLocation() but in a optimized manner (it avoids an
     * extra scene graph refresh).
     *
     * @param fxmlText     null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @param checkTheme   if set to true a check will be made if the fxml contains
     *                     Gluon controls and if so, the correct theme is set
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    @Override
    public void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation, boolean checkTheme) throws IOException {
        updateFxomDocument(fxmlText, fxmlLocation,
                new CombinedResourceBundle(resourceConfig == null ? new ArrayList<>() : resourceConfig.getBundles()),
                checkTheme);
        this.fxmlLocationProperty.setValue(fxmlLocation);
    }

//    /**
//     * Sets fxml text, location and resources to be edited by this editor. Performs
//     * setFxmlText(), setFxmlLocation() and setResources() but in an optimized
//     * manner (it avoids extra scene graph refresh).
//     *
//     * @param fxmlText     null or the fxml text to be edited
//     * @param fxmlLocation null or the location of the fxml text being edited
//     * @param resources    null or the resource bundle used to load the fxml text
//     *
//     * @throws IOException if fxml text cannot be parsed and loaded correctly.
//     */
//    private void setFxmlTextLocationAndResources(String fxmlText, URL fxmlLocation, ResourceBundle resources)
//            throws IOException {
//        setFxmlTextLocationAndResources(fxmlText, fxmlLocation, resources, false);
//    }

//    /**
//     * Sets fxml text, location and resources to be edited by this editor. Performs
//     * setFxmlText(), setFxmlLocation() and setResources() but in an optimized
//     * manner (it avoids extra scene graph refresh).
//     *
//     * @param fxmlText     null or the fxml text to be edited
//     * @param fxmlLocation null or the location of the fxml text being edited
//     * @param resources    null or the resource bundle used to load the fxml text
//     * @param checkTheme   if set to true a check will be made if the fxml contains
//     *                     G Gluon controls and if so, the correct theme is set
//     * @throws IOException if fxml text cannot be parsed and loaded correctly.
//     */
//    private void setFxmlTextLocationAndResources(String fxmlText, URL fxmlLocation, ResourceBundle resources,
//            boolean checkTheme) throws IOException {
//        updateFxomDocument(fxmlText, fxmlLocation, resources, checkTheme);
//        this.fxmlLocationProperty.setValue(fxmlLocation);
//    }

    /**
     * If selection contains single FXOM object and this an fx:include instance,
     * then returns the included file. Else returns null.
     *
     * If the selection is single and is an included FXOM object : 1) if included
     * file source does not start with /, it's a path relative to the document
     * location. - if FXOM document location is null (document not saved yet),
     * return null - else return selection included file
     *
     * 2) if included file source starts with /, it's a path relative to the
     * document class loader. - if FXOM document class loader is null, return null -
     * else return selection included file
     *
     * @return the included file associated to the selected object or null.
     */
    @Override
    public File getIncludedFile() {
        final SelectionGroup asg = selection.getGroup();
        if (asg instanceof ObjectSelectionGroup == false) {
            return null;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        if (osg.getItems().size() != 1) {
            return null;
        }
        final FXOMObject fxomObject = osg.getItems().iterator().next();
        if (fxomObject instanceof FXOMIntrinsic == false) {
            return null;
        }
        final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
        if (fxomIntrinsic.getType() != FXOMIntrinsic.Type.FX_INCLUDE) {
            return null;
        }
        final String source = fxomIntrinsic.getSource();
        if (source == null) {
            return null; // Can this happen ?
        }
        if (source.startsWith("/")) { // NOCHECK
            // Source relative to FXOM document class loader
            final ClassLoader classLoader = getFxomDocument().getClassLoader();
            if (classLoader != null) {
                final PrefixedValue pv = new PrefixedValue(PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH, source);
                final URL url = pv.resolveClassLoaderRelativePath(classLoader);
                final File file;
                try {
                    file = new File(url.toURI());
                } catch (URISyntaxException ex) {
                    throw new IllegalArgumentException(ex);
                }
                return file;
            }
        } else {
            // Source relative to FXOM document location
            final URL location = getFxmlLocation();
            if (location != null) {
                final PrefixedValue pv = new PrefixedValue(PrefixedValue.Type.DOCUMENT_RELATIVE_PATH, source);
                final URL url = pv.resolveDocumentRelativePath(location);
                final File file;
                try {
                    file = new File(url.toURI());
                } catch (URISyntaxException ex) {
                    throw new IllegalArgumentException(ex);
                }
                return file;
            }
        }
        return null;
    }

    private void updateFxomDocument(String fxmlText, URL fxmlLocation, ResourceBundle resources, boolean checkTheme)
            throws IOException {
        final FXOMDocument newFxomDocument;

        if (fxmlText != null) {
            newFxomDocument = new FXOMDocument(fxmlText, fxmlLocation, sceneBuilderManager.classloader().get(),
                    resources);
        } else {
            newFxomDocument = null;
        }

        documentManager.fxomDocument().set(newFxomDocument);

        updateFileWatcher(newFxomDocument);

        // TODO remove comment
//        if (checkTheme) {
//            WarnThemeAlert.showAlertIfRequired(this, newFxomDocument, ownerWindow);
//        }
    }

    private void updateFileWatcher(FXOMDocument fxomDocument) {

        fileSystem.unwatch(this);

        if (fxomDocument != null && fxomDocument.getLocation() != null) {
            final FXOMAssetIndex assetIndex = new FXOMAssetIndex(fxomDocument);
            fileSystem.watch(null, assetIndex.getFileAssets().keySet(), new FileSystem.WatchingCallback() {

                @Override
                public void modified(Path path) {
                    assert Platform.isFxApplicationThread();
                    updateEditorController("file.watching.file.modified", path);
                }

                @Override
                public void deleted(Path path) {
                    assert Platform.isFxApplicationThread();
                    updateEditorController("file.watching.file.deleted", path);
                }

                @Override
                public void created(Path path) {
                    assert Platform.isFxApplicationThread();
                    updateEditorController("file.watching.file.created", path);
                }

                @Override
                public Object getOwnerKey() {
                    return EditorController.this;
                }
            });
        }
    }

    private void updateEditorController(String messageKey, Path target) {
        final String targetFileName = target.getFileName().toString();
        messageLogger.logInfoMessage(messageKey, targetFileName);
        errorReport.forget();
        if (targetFileName.toLowerCase(Locale.ROOT).endsWith(".css")) { // NOCHECK
            errorReport.fileDidChange(target);
            getFxomDocument().reapplyCSS(target);
        } else {
            getFxomDocument().refreshSceneGraph();
        }
    }

    private void fxomDocumentDidChange(FXOMDocument fxomDocument) {
        this.fxomDocument = fxomDocument;
    }

    private void libraryClassLoaderDidChange(ClassLoader classLoader) {
        if (fxomDocument != null) {
            errorReport.forget();
            fxomDocument.setClassLoader(classLoader);
        }
    }

    private void resourcesDidChange() {
        if (getFxomDocument() != null && resourceConfig != null) {
            errorReport.forget();
            fxomDocument.setResources(new CombinedResourceBundle(resourceConfig.getBundles()));
        }
    }

    private void jobManagerRevisionDidChange() {
        setPickModeEnabled(false);
        errorReport.forget();
        updateFileWatcher(getFxomDocument());
//        setPickModeEnabled(false);
    }

//    @Override
//    public void setOwnerWindow(Stage ownerWindow) {
//        this.ownerWindow = ownerWindow;
//    }
//
//    @Override
//    public Stage getOwnerWindow() {
//        return ownerWindow;
//    }

    public FXOMDocument getFxomDocument() {
        return fxomDocument;
    }

}
