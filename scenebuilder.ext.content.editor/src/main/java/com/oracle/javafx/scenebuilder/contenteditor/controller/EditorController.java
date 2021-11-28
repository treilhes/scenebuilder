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
package com.oracle.javafx.scenebuilder.contenteditor.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.ControlAction;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.ErrorReport;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.CombinedResourceBundle;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.sample.SampleDataHandler;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardEncoder;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMAssetIndex;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.mask.BorderPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.job.editor.AddContextMenuToSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.AddTooltipToSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.BringForwardJob;
import com.oracle.javafx.scenebuilder.job.editor.BringToFrontJob;
import com.oracle.javafx.scenebuilder.job.editor.CutSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.DeleteSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.DuplicateSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.FitToParentSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.ImportFileJob;
import com.oracle.javafx.scenebuilder.job.editor.IncludeFileJob;
import com.oracle.javafx.scenebuilder.job.editor.PasteIntoJob;
import com.oracle.javafx.scenebuilder.job.editor.PasteJob;
import com.oracle.javafx.scenebuilder.job.editor.SendBackwardJob;
import com.oracle.javafx.scenebuilder.job.editor.SendToBackJob;
import com.oracle.javafx.scenebuilder.job.editor.TrimSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.UseComputedSizesSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.UsePredefinedSizeJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.AddColumnJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.AddRowJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.MoveColumnJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.MoveRowJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.v2.SpanJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.v2.SpanJob.SpanAction;
import com.oracle.javafx.scenebuilder.job.editor.wrap.AbstractWrapInJob;
import com.oracle.javafx.scenebuilder.job.editor.wrap.UnwrapJob;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * An editor controller is the central object which coordinates the editing
 * of an FXML document across the different panels (hierarchy, content,
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
public class EditorController implements Editor {

    private final Selection selection;
    private final JobManager jobManager;
    private final MessageLogger messageLogger;
    private final ErrorReport errorReport;
//    private final DragController dragController;
    private final InlineEdit inlineEditController;// = new InlineEditController(this);
    private final ContextMenuController contextMenuController;// = new ContextMenuController(this);
    //private final WatchingController watchingController;// = new WatchingController(this);

    //private final ObjectProperty<Library> libraryProperty;
    private final ObjectProperty<URL> fxmlLocationProperty;
    private final BooleanProperty pickModeEnabledProperty
            = new SimpleBooleanProperty(false);
    private final BooleanProperty sampleDataEnabledProperty
            = new SimpleBooleanProperty(false);

    //private Callback<Void, Boolean> requestTextEditingSessionEnd;

    private Stage ownerWindow;

	private final ApplicationContext context;
	private final FileSystem fileSystem;
	private final Dialog dialog;
    private I18nResourceProvider resourceConfig;
    private final DocumentManager documentManager;

    private FXOMDocument fxomDocument;
//    private Library builtinLibrary;
    //private final Api api;
    private final SceneBuilderManager sceneBuilderManager;
    private final SampleDataHandler sampleDataHandler;

    /**
     * Creates an empty editor controller (ie it has no associated fxom document).
     */
    public EditorController(
            @Autowired Api api,
    	    @Lazy @Autowired InlineEdit inlineEditController,
    		@Lazy @Autowired ContextMenuController contextMenuController,
    		@Autowired SampleDataHandler sampleDataHandler
    		) {
        //this.api = api;
    	this.context = api.getContext();
    	this.sceneBuilderManager = api.getSceneBuilderManager();
    	this.jobManager = api.getApiDoc().getJobManager();
    	this.fileSystem = api.getFileSystem();
    	this.dialog = api.getApiDoc().getDialog();
//    	this.dragController = dragController;
    	this.messageLogger = api.getApiDoc().getMessageLogger();
    	this.selection = api.getApiDoc().getSelection();
    	this.documentManager = api.getApiDoc().getDocumentManager();
    	this.errorReport = api.getApiDoc().getErrorReport();
    	this.inlineEditController = inlineEditController;
    	this.contextMenuController = contextMenuController;
    	this.sampleDataHandler = sampleDataHandler;


    	//libraryProperty = new SimpleObjectProperty<>(builtinLibrary);
    	fxmlLocationProperty = new SimpleObjectProperty<>();

    	//TODO remove below
    	//libraryProperty = new SimpleObjectProperty<Library>(builtinLibrary);

    	api.getApiDoc().getJobManager().revisionProperty().addListener((ob, o, n) -> setPickModeEnabled(false));
    }

//	@Override
//	public void afterPropertiesSet() throws Exception {
//		initialize();
//	}

	@Override
    public void initialize() {
	    jobManager.revisionProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> jobManagerRevisionDidChange());
        documentManager.i18nResourceConfig().subscribe(s -> {
            resourceConfig = s;
            resourcesDidChange();
        });

        documentManager.fxomDocument().subscribe(cl -> fxomDocumentDidChange(cl));
        sceneBuilderManager.classloader().subscribe(cl -> libraryClassLoaderDidChange(cl));
    }

    /**
     * Sets the fxml content to be edited by this editor.
     * A null value makes this editor empty.
     *
     * @param fxmlText null or the fxml text to be edited
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    private void setFxmlText(String fxmlText, boolean checkGluonControls) throws IOException {
        setFxmlTextAndLocation(fxmlText, getFxmlLocation(), checkGluonControls);
    }

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
            if (isSampleDataEnabled()) {
                sampleDataHandler.removeSampleData(getFxomDocument().getFxomRoot());
            }
            result = fxomDocument.getFxmlText(wildcardImports);
            if (isSampleDataEnabled()) {
                sampleDataHandler.assignSampleData(getFxomDocument().getFxomRoot());
            }
        }

        return result;
    }

    /**
     * Returns true if fxml content being edited can be returned safely.
     * This method will return false if there is a text editing session on-going.
     *
     * @return true if fxml content being edited can be returned safely.
     */
    @Override
    public boolean canGetFxmlText() {
//
//        final boolean result;
//
//        if (requestTextEditingSessionEnd == null) {
//            result = true;
//        } else {
//            result = requestTextEditingSessionEnd.call(null);
//            // If the callback returns true, then it should have call
//            // textEditingSessionDidEnd()
//            // => requestTextEditingSessionEnd should be null
//            assert (requestTextEditingSessionEnd == null) || (result == false);
//        }
//
//        return result;
        return inlineEditController.canGetFxmlText();
    }

    /**
     * Tells this editor that a text editing session has started.
     * The editor controller may invoke the requestSessionEnd() callback
     * if it needs the text editing session to stop. The callback should;
     *   - either stop the text editing session, invoke textEditingSessionDidEnd()
     *     and return true
     *   - either keep the text editing session on-going and return false
     *
     * @param requestSessionEnd Callback that should end the text editing session or return false
     */
    @Override
    public void textEditingSessionDidBegin(Callback<Void, Boolean> requestSessionEnd) {
//        assert requestTextEditingSessionEnd == null;
//        requestTextEditingSessionEnd = requestSessionEnd;
        inlineEditController.textEditingSessionDidBegin(requestSessionEnd);
    }


    /**
     * Tells this editor that the text editing session has ended.
     */
    @Override
    public void textEditingSessionDidEnd() {
        //assert requestTextEditingSessionEnd != null;
        //requestTextEditingSessionEnd = null;
        inlineEditController.textEditingSessionDidEnd();
    }

    /*
     * Returns true if a text editing session is currently on going.
     */
    @Override
    public boolean isTextEditingSessionOnGoing() {
        //return requestTextEditingSessionEnd != null;
        return inlineEditController.isTextEditingSessionOnGoing();
    }

    /**
     * The property holding the fxml location associated to this editor.
     * @return the property holding the fxml location associated to this editor.
     */
    @Override
    public ObservableValue<URL> fxmlLocationProperty() {
        return fxmlLocationProperty;
    }

    /**
     * Sets the location of the fxml being edited.
     * If null value is passed, fxml text is being interpreted with any location
     * (ie some references may be broken).
     *
     * @param fxmlLocation null or the location of the fxml being edited.
     */
    @Override
    public void setFxmlLocation(URL fxmlLocation) {
        fxmlLocationProperty.setValue(fxmlLocation);
        if (getFxomDocument() != null) {
            getFxomDocument().setLocation(fxmlLocation);
            clearUndoRedo(); // Because FXOMDocument.setLocation() mutates the document
        }
        if (fxmlLocation != null) {
            final File newInitialDirectory = new File(fxmlLocation.getPath());
            fileSystem.updateNextInitialDirectory(newInitialDirectory);
        }
    }
//
//    /**
//     * Returns the library used by this editor.
//     *
//     * @return the library used by this editor (never null).
//     */
//    @Override
//    public Library getLibrary() {
//        return libraryProperty.getValue();
//    }
//
//    /**
//     * Sets the library used by this editor.
//     * When this method is called, user scene graph is fully rebuilt using
//     * the new library and all panel refresh their contents.
//     *
//     * @param library the library to be used by this editor (never null).
//     */
//    public void setLibrary(Library library) {
//        assert library != null;
//        libraryProperty.getValue().classLoaderProperty().removeListener(libraryClassLoaderListener);
//        libraryProperty.setValue(library);
//        libraryProperty.getValue().classLoaderProperty().addListener(libraryClassLoaderListener);
//        libraryClassLoaderDidChange();
//    }

//    /**
//     * The property holding the library used by this editor.
//     *
//     * @return the property holding the library used by this editor (never null).
//     */
//    @Override
//    public ObservableValue<Library> libraryProperty() {
//        return libraryProperty;
//    }

//    /**
//     * Returns the glossary used by this editor.
//     *
//     * @return the glossary used by this editor (never null).
//     */
//    @Override
//    public AbstractGlossary getGlossary() {
//        return glossaryProperty.getValue();
//    }

//    /**
//     * Sets the glossary used by this editor.
//     * The Inspector panel(s) connected to this editor will update
//     * their suggested lists in Code section.
//     *
//     * @param glossary the glossary to be used by this editor (never null).
//     */
//    public void setLibrary(AbstractGlossary glossary) {
//        assert glossary != null;
//        glossaryProperty.setValue(glossary);
//    }
//
//    /**
//     * The property holding the glossary used by this editor.
//     *
//     * @return the property holding the glossary used by this editor (never null).
//     */
//    public ObservableValue<AbstractGlossary> glossaryProperty() {
//        return glossaryProperty;
//    }

//    /**
//     * Returns the resource bundle used by this editor.
//     *
//     * @return  the resource bundle used by this editor.
//     */
//    public ResourceBundle getResources() {
//        return resourcesProperty.getValue();
//    }
//
//    /**
//     * Sets the resource bundle used by this editor.
//     * Content and Preview panels sharing this editor will update
//     * their content to use this new theme.
//     *
//     * @param resources null of the resource bundle to be used by this editor.
//     */
//    public void setResources(ResourceBundle resources) {
//        resourcesProperty.setValue(resources);
//        resourcesDidChange();
//    }
//
//    /**
//     * The property holding the resource bundle used by this editor.
//     *
//     * @return the property holding the resource bundle used by this editor (never null).
//     */
//    public ObservableValue<ResourceBundle> resourcesProperty() {
//        return resourcesProperty;
//    }

    /**
     * Returns true if 'pick mode' is enabled for this editor.
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
     * Returns true if content and preview panels attached to this editor
     * should display sample data.
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
    public void setSampleDataEnabled(boolean sampleDataEnabled) {
        setPickModeEnabled(false);
        sampleDataEnabledProperty.setValue(sampleDataEnabled);

        if (getFxomDocument() == null) {
            return;
        }

        if (isSampleDataEnabled()) {
            sampleDataHandler.assignSampleData(getFxomDocument().getFxomRoot());
        } else {
            sampleDataHandler.removeSampleData(getFxomDocument().getFxomRoot());
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

    /**
     * Sets both fxml text and location to be edited by this editor.
     * Performs setFxmlText() and setFxmlLocation() but in a optimized manner
     * (it avoids an extra scene graph refresh).
     *
     * @param fxmlText null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    private void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation) throws IOException {
        setFxmlTextAndLocation(fxmlText, fxmlLocation, false);
    }

    /**
     * Sets both fxml text and location to be edited by this editor.
     * Performs setFxmlText() and setFxmlLocation() but in a optimized manner
     * (it avoids an extra scene graph refresh).
     *
     * @param fxmlText null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @param checkTheme if set to true a check will be made if the fxml contains
     *                           Gluon controls and if so, the correct theme is set
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    @Override
    public void setFxmlTextAndLocation(String fxmlText, URL fxmlLocation, boolean checkTheme) throws IOException {
        updateFxomDocument(fxmlText, fxmlLocation,
                new CombinedResourceBundle(resourceConfig == null ? new ArrayList<>(): resourceConfig.getBundles()),
                checkTheme);
        this.fxmlLocationProperty.setValue(fxmlLocation);
    }

    /**
     * Sets fxml text, location and resources to be edited by this editor.
     * Performs setFxmlText(), setFxmlLocation() and setResources() but in an
     * optimized manner (it avoids extra scene graph refresh).
     *
     * @param fxmlText null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @param resources null or the resource bundle used to load the fxml text
     *
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    private void setFxmlTextLocationAndResources(String fxmlText, URL fxmlLocation,
                                                ResourceBundle resources) throws IOException {
        setFxmlTextLocationAndResources(fxmlText, fxmlLocation, resources, false);
    }

    /**
     * Sets fxml text, location and resources to be edited by this editor.
     * Performs setFxmlText(), setFxmlLocation() and setResources() but in an
     * optimized manner (it avoids extra scene graph refresh).
     *
     * @param fxmlText null or the fxml text to be edited
     * @param fxmlLocation null or the location of the fxml text being edited
     * @param resources null or the resource bundle used to load the fxml text
     * @param checkTheme if set to true a check will be made if the fxml contains G
     *                           Gluon controls and if so, the correct theme is set
     * @throws IOException if fxml text cannot be parsed and loaded correctly.
     */
    private void setFxmlTextLocationAndResources(String fxmlText, URL fxmlLocation,
            ResourceBundle resources, boolean checkTheme) throws IOException {
        updateFxomDocument(fxmlText, fxmlLocation, resources, checkTheme);
        this.fxmlLocationProperty.setValue(fxmlLocation);
    }

//    /**
//     * The property holding the document associated to this editor.
//     * @return the property holding the document associated to this editor.
//     */
//    @Override
//    public ObservableValue<FXOMDocument> fxomDocumentProperty() {
//        return fxomDocumentProperty;
//    }

//    /**
//     * Returns the document associated to this editor.
//     *
//     * @return the document associated to this editor.
//     */
//    @Override
//    public FXOMDocument getFxomDocument() {
//        return fxomDocumentProperty.getValue();
//    }

//    /**
//     * Returns the tool stylesheet associated to this editor controller.
//     * Its default value equals to getBuiltinToolStylesheet().
//     *
//     * @return the tool stylesheet associated to this editor controller (never null)
//     */
//    public String getToolStylesheet() {
//        return toolStylesheetProperty.getValue();
//    }

//    /**
//     * Sets the tool stylesheet associated to this editor controller.
//     * Each panel connected to this editor controller will install this style
//     * sheet in its root object.
//     *
//     * @param stylesheet the tool stylesheet associated to this editor controller (never null)
//     */
//    public void setToolStylesheet(String stylesheet) {
//        assert stylesheet != null;
//        toolStylesheetProperty.setValue(stylesheet);
//    }

//    /**
//     * The property holding tool stylesheet associated to this editor controller.
//     * @return the property holding tool stylesheet associated to this editor controller.
//     */
//    public ObservableValue<String> toolStylesheetProperty() {
//        return toolStylesheetProperty;
//    }

//    /**
//     * Returns the builtin tool stylesheet.
//     * This is the default value for EditorController#toolStylesheet property.
//     *
//     * @return the builtin tool stylesheet.
//     */
//    public static synchronized String getBuiltinToolStylesheet() {
//        if (builtinToolStylesheet == null) {
//            builtinToolStylesheet = ToolTheme.DEFAULT.getStylesheetURL();
//        }
//        return builtinToolStylesheet;
//    }

//    /**
//     * Starts file watching on this editor.
//     * This editor will now monitor the files referenced by the FXML text
//     * (like images, medias, stylesheets, included fxmls...) and automatically
//     * request attached panels to update themselves.
//     */
//    public void startFileWatching() {
//        watchingController.start();
//    }
//
//    /**
//     * Stops file watching on this editor.
//     */
//    public void stopFileWatching() {
//        watchingController.stop();
//    }
//
//    /**
//     * Returns true if file watching is started on this editor.
//     *
//     * @return true if file watching is started on this editor.
//     */
//    public boolean isFileWatchingStarted() {
//        return watchingController.isStarted();
//    }

    /**
     * @treatAsPrivate Returns the selection associated to this editor.
     *
     * @return  the selection associated to this editor.
     */
    @Override
    public Selection getSelection() {
        return selection;
    }

    /**
     * Returns the list of selected objects
     *
     * @return the selected objects
     */
    @Override
    public List<FXOMObject> getSelectedObjects() {
        // Collects all the selected objects
        final List<FXOMObject> selectedObjects = new ArrayList<>();
        final Selection selection = getSelection();
        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            selectedObjects.addAll(osg.getItems());
        } else if (selection.getGroup() instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
            selectedObjects.addAll(gsg.collectSelectedObjects());
        } else {
            assert false;
        }
        return selectedObjects;
    }


    /**
     * @treatAsPrivate Returns the job manager associated to this editor.
     *
     * @return  the job manager associated to this editor.
     */
    @Override
    public JobManager getJobManager() {
        return jobManager;
    }

    /**
     * @treatAsPrivate Returns the message log associated to this editor.
     *
     * @return  the message log associated to this editor.
     */
    @Override
    public MessageLogger getMessageLog() {
        return messageLogger;
    }

    /**
     * @treatAsPrivate Returns the error report associated to this editor.
     *
     * @return  the error report associated to this editor.
     */
    @Override
    public ErrorReport getErrorReport() {
        return errorReport;
    }

//    /**
//     * @treatAsPrivate Returns the drag controller associated to this editor.
//     *
//     * @return the drag controller associated to this editor.
//     */
//    @Override
//    public Drag getDragController() {
//        return dragController;
//    }

    /**
     * @treatAsPrivate Returns the inline edit controller associated to this editor.
     *
     * @return the inline edit controller associated to this editor.
     */
    @Override
    public InlineEdit getInlineEditController() {
        return inlineEditController;
    }

    /**
     * @treatAsPrivate Returns the context menu controller associated to this editor.
     *
     * @return the context menu controller associated to this editor.
     */
    @Override
    public ContextMenuController getContextMenuController() {
        return contextMenuController;
    }

    /**
     * Returns true if the undo action is permitted (ie there is something
     * to be undone).
     *
     * @return true if the undo action is permitted.
     */
    @Override
    public boolean canUndo() {
        return jobManager.canUndo();
    }

    /**
     * Returns null or the description of the action to be undone.
     *
     * @return null or the description of the action to be undone.
     */
    @Override
    public String getUndoDescription() {
        return jobManager.getUndoDescription();
    }

    /**
     * Performs the undo action.
     */
    @Override
    public void undo() {
        jobManager.undo();
        assert getFxomDocument().isUpdateOnGoing() == false;
    }

    /**
     * Returns true if the redo action is permitted (ie there is something
     * to be redone).
     *
     * @return true if the redo action is permitted.
     */
    @Override
    public boolean canRedo() {
        return jobManager.canRedo();
    }

    /**
     * Returns null or the description of the action to be redone.
     *
     * @return null or the description of the action to be redone.
     */
    @Override
    public String getRedoDescription() {
        return jobManager.getRedoDescription();
    }

    /**
     * Performs the redo action.
     */
    @Override
    public void redo() {
        jobManager.redo();
        assert getFxomDocument().isUpdateOnGoing() == false;
    }

    /**
     * Clears the undo/redo stack of this editor controller.
     */
    public void clearUndoRedo() {
        jobManager.clear();
    }

    /**
     * Performs an edit action.
     *
     * @param editAction the edit action to be performed.
     */
    @Override
    public void performEditAction(EditAction editAction) {
        switch(editAction) {
            case ADD_CONTEXT_MENU: {
                performAddContextMenu();
                break;
            }
            case ADD_TOOLTIP: {
                performAddTooltip();
                break;
            }
            case ADD_COLUMN_BEFORE: {
                final Job job = new AddColumnJob(context, this, Position.BEFORE).extend();
                jobManager.push(job);
                break;
            }
            case ADD_COLUMN_AFTER: {
                final Job job = new AddColumnJob(context, this, Position.AFTER).extend();
                jobManager.push(job);
                break;
            }
            case ADD_ROW_ABOVE: {
                final Job job = new AddRowJob(context, this, Position.ABOVE).extend();
                jobManager.push(job);
                break;
            }
            case ADD_ROW_BELOW: {
                final Job job = new AddRowJob(context, this, Position.BELOW).extend();
                jobManager.push(job);
                break;
            }
            case BRING_FORWARD: {
                final Job job = new BringForwardJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case BRING_TO_FRONT: {
                final Job job = new BringToFrontJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case CUT: {
                final Job job = new CutSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case DECREASE_COLUMN_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.DECREASE_COLUMN_SPAN).extend();
                jobManager.push(job);
                break;
            }
            case DECREASE_ROW_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.DECREASE_ROW_SPAN).extend();
                jobManager.push(job);
                break;
            }
            case DELETE: {
                final Job job = new DeleteSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case DUPLICATE: {
                final Job job = new DuplicateSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case FIT_TO_PARENT: {
                final Job job = new FitToParentSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case INCREASE_COLUMN_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.INCREASE_COLUMN_SPAN).extend();
                jobManager.push(job);
                break;
            }
            case INCREASE_ROW_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.INCREASE_ROW_SPAN).extend();
                jobManager.push(job);
                break;
            }
            case MOVE_COLUMN_BEFORE: {
                final Job job = new MoveColumnJob(context, this, Position.BEFORE).extend();
                jobManager.push(job);
                break;
            }
            case MOVE_COLUMN_AFTER: {
                final Job job = new MoveColumnJob(context, this, Position.AFTER).extend();
                jobManager.push(job);
                break;
            }
            case MOVE_ROW_ABOVE: {
                final Job job = new MoveRowJob(context, this, Position.ABOVE).extend();
                jobManager.push(job);
                break;
            }
            case MOVE_ROW_BELOW: {
                final Job job = new MoveRowJob(context, this, Position.BELOW).extend();
                jobManager.push(job);
                break;
            }
            case PASTE: {
                final Job job = new PasteJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case PASTE_INTO: {
                final Job job = new PasteIntoJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case SEND_BACKWARD: {
                final Job job = new SendBackwardJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case SEND_TO_BACK: {
                final Job job = new SendToBackJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_335x600: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_335x600).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_900x600: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_900x600).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_320x240: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_320x240).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_640x480: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_640x480).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_1280x800: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_1280x800).extend();
                jobManager.push(job);
                break;
            }
            case SET_SIZE_1920x1080: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_1920x1080).extend();
                jobManager.push(job);
                break;
            }
            case TRIM: {
                final Job job = new TrimSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case UNWRAP: {
                final Job job = new UnwrapJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case USE_COMPUTED_SIZES: {
                final Job job = new UseComputedSizesSelectionJob(context, this).extend();
                jobManager.push(job);
                break;
            }
            case WRAP_IN_ANCHOR_PANE: {
                performWrap(javafx.scene.layout.AnchorPane.class);
                break;
            }
            case WRAP_IN_BORDER_PANE: {
                performWrap(javafx.scene.layout.BorderPane.class);
                break;
            }
            case WRAP_IN_BUTTON_BAR: {
                performWrap(javafx.scene.control.ButtonBar.class);
                break;
            }
            case WRAP_IN_DIALOG_PANE: {
                performWrap(javafx.scene.control.DialogPane.class);
                break;
            }
            case WRAP_IN_FLOW_PANE: {
                performWrap(javafx.scene.layout.FlowPane.class);
                break;
            }
            case WRAP_IN_GRID_PANE: {
                performWrap(javafx.scene.layout.GridPane.class);
                break;
            }
            case WRAP_IN_GROUP: {
                performWrap(javafx.scene.Group.class);
                break;
            }
            case WRAP_IN_HBOX: {
                performWrap(javafx.scene.layout.HBox.class);
                break;
            }
            case WRAP_IN_PANE: {
                performWrap(javafx.scene.layout.Pane.class);
                break;
            }
            case WRAP_IN_SCROLL_PANE: {
                performWrap(javafx.scene.control.ScrollPane.class);
                break;
            }
            case WRAP_IN_SPLIT_PANE: {
                performWrap(javafx.scene.control.SplitPane.class);
                break;
            }
            case WRAP_IN_STACK_PANE: {
                performWrap(javafx.scene.layout.StackPane.class);
                break;
            }
            case WRAP_IN_TAB_PANE: {
                performWrap(javafx.scene.control.TabPane.class);
                break;
            }
            case WRAP_IN_TEXT_FLOW: {
                performWrap(javafx.scene.text.TextFlow.class);
                break;
            }
            case WRAP_IN_TILE_PANE: {
                performWrap(javafx.scene.layout.TilePane.class);
                break;
            }
            case WRAP_IN_TITLED_PANE: {
                performWrap(javafx.scene.control.TitledPane.class);
                break;
            }
            case WRAP_IN_TOOL_BAR: {
                performWrap(javafx.scene.control.ToolBar.class);
                break;
            }
            case WRAP_IN_VBOX: {
                performWrap(javafx.scene.layout.VBox.class);
                break;
            }
            case WRAP_IN_SCENE: {
                performWrap(javafx.scene.Scene.class);
                break;
            }
            case WRAP_IN_STAGE: {
                performWrap(javafx.stage.Stage.class);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented"); //NOCHECK
        }
        assert getFxomDocument().isUpdateOnGoing() == false;
    }

    /**
     * Returns true if the specified edit action is permitted.
     *
     * @param editAction the edit action to be tested.
     * @return true if the specified edit action is permitted.
     */
    @Override
    public boolean canPerformEditAction(EditAction editAction) {
        final boolean result;
        switch(editAction) {
            case ADD_CONTEXT_MENU: {
                result = canPerformAddContextMenu();
                break;
            }
            case ADD_TOOLTIP: {
                result = canPerformAddTooltip();
                break;
            }
            case ADD_COLUMN_BEFORE: {
                final Job job = new AddColumnJob(context, this, Position.BEFORE).extend();
                result = job.isExecutable();
                break;
            }
            case ADD_COLUMN_AFTER: {
                final Job job = new AddColumnJob(context, this, Position.AFTER).extend();
                result = job.isExecutable();
                break;
            }
            case ADD_ROW_ABOVE: {
                final Job job = new AddRowJob(context, this, Position.ABOVE).extend();
                result = job.isExecutable();
                break;
            }
            case ADD_ROW_BELOW: {
                final Job job = new AddRowJob(context, this, Position.BELOW).extend();
                result = job.isExecutable();
                break;
            }
            case BRING_FORWARD: {
                final Job job = new BringForwardJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case BRING_TO_FRONT: {
                final Job job = new BringToFrontJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case CUT: {
                final Job job = new CutSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case DECREASE_COLUMN_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.DECREASE_COLUMN_SPAN).extend();
                result = job.isExecutable();
                break;
            }
            case DECREASE_ROW_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.DECREASE_ROW_SPAN).extend();
                result = job.isExecutable();
                break;
            }
            case DELETE: {
                final Job job = new DeleteSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case DUPLICATE: {
                final Job job = new DuplicateSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case FIT_TO_PARENT: {
                final Job job = new FitToParentSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case INCREASE_COLUMN_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.INCREASE_COLUMN_SPAN).extend();
                result = job.isExecutable();
                break;
            }
            case INCREASE_ROW_SPAN: {
                final Job job = new SpanJob(context, this, SpanAction.INCREASE_ROW_SPAN).extend();
                result = job.isExecutable();
                break;
            }
            case MOVE_COLUMN_BEFORE: {
                final Job job = new MoveColumnJob(context, this, Position.BEFORE).extend();
                result = job.isExecutable();
                break;
            }
            case MOVE_COLUMN_AFTER: {
                final Job job = new MoveColumnJob(context, this, Position.AFTER).extend();
                result = job.isExecutable();
                break;
            }
            case MOVE_ROW_ABOVE: {
                final Job job = new MoveRowJob(context, this, Position.ABOVE).extend();
                result = job.isExecutable();
                break;
            }
            case MOVE_ROW_BELOW: {
                final Job job = new MoveRowJob(context, this, Position.BELOW).extend();
                result = job.isExecutable();
                break;
            }
            case PASTE: {
                final Job job = new PasteJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case PASTE_INTO: {
                final Job job = new PasteIntoJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case SEND_BACKWARD: {
                final Job job = new SendBackwardJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case SEND_TO_BACK: {
                final Job job = new SendToBackJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_335x600: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_335x600).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_900x600: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_900x600).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_320x240: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_320x240).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_640x480: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_640x480).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_1280x800: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_1280x800).extend();
                result = job.isExecutable();
                break;
            }
            case SET_SIZE_1920x1080: {
                final Job job = new UsePredefinedSizeJob(context, this, Size.SIZE_1920x1080).extend();
                result = job.isExecutable();
                break;
            }
            case TRIM: {
                final Job job = new TrimSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case UNWRAP: {
                final Job job = new UnwrapJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case USE_COMPUTED_SIZES: {
                final Job job = new UseComputedSizesSelectionJob(context, this).extend();
                result = job.isExecutable();
                break;
            }
            case WRAP_IN_ANCHOR_PANE: {
                result = canPerformWrap(javafx.scene.layout.AnchorPane.class);
                break;
            }
            case WRAP_IN_BORDER_PANE: {
                result = canPerformWrap(javafx.scene.layout.BorderPane.class);
                break;
            }
            case WRAP_IN_BUTTON_BAR: {
                result = canPerformWrap(javafx.scene.control.ButtonBar.class);
                break;
            }
            case WRAP_IN_DIALOG_PANE: {
                result = canPerformWrap(javafx.scene.control.DialogPane.class);
                break;
            }
            case WRAP_IN_FLOW_PANE: {
                result = canPerformWrap(javafx.scene.layout.FlowPane.class);
                break;
            }
            case WRAP_IN_GRID_PANE: {
                result = canPerformWrap(javafx.scene.layout.GridPane.class);
                break;
            }
            case WRAP_IN_GROUP: {
                result = canPerformWrap(javafx.scene.Group.class);
                break;
            }
            case WRAP_IN_HBOX: {
                result = canPerformWrap(javafx.scene.layout.HBox.class);
                break;
            }
            case WRAP_IN_PANE: {
                result = canPerformWrap(javafx.scene.layout.Pane.class);
                break;
            }
            case WRAP_IN_SCROLL_PANE: {
                result = canPerformWrap(javafx.scene.control.ScrollPane.class);
                break;
            }
            case WRAP_IN_SPLIT_PANE: {
                result = canPerformWrap(javafx.scene.control.SplitPane.class);
                break;
            }
            case WRAP_IN_STACK_PANE: {
                result = canPerformWrap(javafx.scene.layout.StackPane.class);
                break;
            }
            case WRAP_IN_TAB_PANE: {
                result = canPerformWrap(javafx.scene.control.TabPane.class);
                break;
            }
            case WRAP_IN_TEXT_FLOW: {
                result = canPerformWrap(javafx.scene.text.TextFlow.class);
                break;
            }
            case WRAP_IN_TILE_PANE: {
                result = canPerformWrap(javafx.scene.layout.TilePane.class);
                break;
            }
            case WRAP_IN_TITLED_PANE: {
                result = canPerformWrap(javafx.scene.control.TitledPane.class);
                break;
            }
            case WRAP_IN_TOOL_BAR: {
                result = canPerformWrap(javafx.scene.control.ToolBar.class);
                break;
            }
            case WRAP_IN_VBOX: {
                result = canPerformWrap(javafx.scene.layout.VBox.class);
                break;
            }
            case WRAP_IN_SCENE: {
                result = canPerformWrap(javafx.scene.Scene.class);
                break;
            }
            case WRAP_IN_STAGE: {
                result = canPerformWrap(javafx.stage.Stage.class);
                break;
            }
            default:
                result = false;
                break;
        }

        return result;
    }

    /**
     * Performs the specified control action.
     *
     * @param controlAction the control action to be performed.
     */
    @Override
    public void performControlAction(ControlAction controlAction) {
        switch(controlAction) {
            case COPY: {
                performCopy();
                break;
            }

            case SELECT_ALL: {
                performSelectAll();
                break;
            }
            case SELECT_NONE: {
                performSelectNone();
                break;
            }
            case SELECT_PARENT: {
                performSelectParent();
                break;
            }
            case SELECT_NEXT: {
                performSelectNext();
                break;
            }
            case SELECT_PREVIOUS: {
                performSelectPrevious();
                break;
            }
            case TOGGLE_SAMPLE_DATA: {
                setSampleDataEnabled( ! isSampleDataEnabled());
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented"); //NOCHECK
        }
    }

    /**
     * Returns true if the specified control action is permitted.
     *
     * @param controlAction the control action to be tested.
     * @return true if the specified control action is permitted.
     */
    @Override
    public boolean canPerformControlAction(ControlAction controlAction) {
        final boolean result;

        // If there is no document loaded, we cannot perform control actions
        if (getFxomDocument() == null || getFxomDocument().getFxomRoot() == null) {
            return false;
        }
        switch(controlAction) {
            case COPY: {
                result = canPerformCopy();
                break;
            }
            case SELECT_ALL: {
                result = canPerformSelectAll();
                break;
            }
            case SELECT_NONE: {
                result = canPerformSelectNone();
                break;
            }
            case SELECT_PARENT: {
                result = canPerformSelectParent();
                break;
            }
            case SELECT_NEXT: {
                result = canPerformSelectNext();
                break;
            }
            case SELECT_PREVIOUS: {
                result = canPerformSelectPrevious();
                break;
            }
            case TOGGLE_SAMPLE_DATA: {
                result = true;
                break;
            }
            default:
                result = false;
                break;
        }

        return result;
    }

    /**
     * Performs the 'import' FXML edit action.
     * This action creates an object matching the root node of the selected
     * FXML file and insert it in the document (either as root if the document
     * is empty or under the selection common ancestor node otherwise).
     *
     * @param fxmlFile the FXML file to be imported
     */
    @Override
    public void performImportFxml(File fxmlFile) {
        performImport(fxmlFile);
    }

    /**
     * Performs the 'import' media edit action.
     * This action creates an object matching the type of the selected
     * media file (either ImageView or MediaView) and insert it in the document
     * (either as root if the document is empty or under the selection common
     * ancestor node otherwise).
     *
     * @param mediaFile the media file to be imported
     */
    @Override
    public void performImportMedia(File mediaFile) {
        performImport(mediaFile);
    }

    private void performImport(File file) {
        final ImportFileJob job = new ImportFileJob(context, file, this);
        if (job.extend().isExecutable()) {
            jobManager.push(job.extend());
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                getMessageLog().logWarningMessage(
                        "import.from.file.failed.target",
                        file.getName(), target);
            } else {
                getMessageLog().logWarningMessage(
                        "import.from.file.failed",
                        file.getName());
            }
        }
    }

    /**
     * Performs the 'include' FXML edit action.
     * As opposed to the 'import' edit action, the 'include' action does not
     * copy the FXML content but adds an fx:include element to the FXML document.
     *
     * @param fxmlFile the FXML file to be included
     */
    @Override
    public void performIncludeFxml(File fxmlFile) {
        final IncludeFileJob job = new IncludeFileJob(context, fxmlFile, this);
        if (job.extend().isExecutable()) {
            jobManager.push(job.extend());
        } else {
            final String target;
            if (job.getTargetObject() == null) {
                target = null;
            } else {
                final Object sceneGraphTarget
                        = job.getTargetObject().getSceneGraphObject();
                if (sceneGraphTarget == null) {
                    target = null;
                } else {
                    target = sceneGraphTarget.getClass().getSimpleName();
                }
            }
            if (target != null) {
                getMessageLog().logWarningMessage(
                        "include.file.failed.target",
                        fxmlFile.getName(), target);
            } else {
                getMessageLog().logWarningMessage(
                        "include.file.failed",
                        fxmlFile.getName());
            }
        }
    }

//    /**
//     * Performs the 'insert' edit action. This action creates an object
//     * matching the specified library item and insert it in the document
//     * (according the selection state).
//     *
//     * @param libraryItem the library item describing the object to be inserted.
//     */
//    @Override
//    public void performInsert(LibraryItem libraryItem) {
//        final Job job;
//        final FXOMObject target;
//
//        assert canPerformInsert(libraryItem); // (1)
//
//        final FXOMDocument newItemDocument = libraryItem.instantiate();
//        assert newItemDocument != null; // Because (1)
//        final FXOMObject newObject = newItemDocument.getFxomRoot();
//        assert newObject != null;
//        newObject.moveToFxomDocument(getFxomDocument());
//        final FXOMObject rootObject = getFxomDocument().getFxomRoot();
//        if (rootObject == null) { // Empty document
//            final String description
//                    = I18N.getString("drop.job.insert.library.item", libraryItem.getName());
//            job = new SetDocumentRootJob(context, newObject, true /* usePredefinedSize */, description, this);
//
//        } else {
//            if (selection.isEmpty() || selection.isSelected(rootObject)) {
//                // No selection or root is selected -> we insert below root
//                target = rootObject;
//            } else {
//                // Let's use the common parent of the selected objects.
//                // It might be null if selection holds some non FXOMObject entries
//                target = selection.getAncestor();
//            }
//            job = new InsertAsSubComponentJob(context, newObject, target, -1, this);
//        }
//
//        jobManager.push(job.extend());
//
//        //TODO remove comment
//        //WarnThemeAlert.showAlertIfRequired(this, newObject, ownerWindow);
//    }
//
//    /**
//     * Returns true if the 'insert' action is permitted with the specified
//     * library item.
//     *
//     * @param libraryItem the library item describing the object to be inserted.
//     * @return true if the 'insert' action is permitted.
//     */
//    @Override
//    public boolean canPerformInsert(LibraryItem libraryItem) {
//        final FXOMObject targetCandidate;
//        final boolean result;
//
//        if (getFxomDocument() == null) {
//            result = false;
//        } else {
//            assert (libraryItem.getLibrary().getClassLoader() == null)
//                    || (libraryItem.getLibrary().getClassLoader() == getFxomDocument().getClassLoader());
//            final FXOMDocument newItemDocument = libraryItem.instantiate();
//            if (newItemDocument == null) {
//                // For some reason, library is unable to instantiate this item
//                result = false;
//            } else {
//                final FXOMObject newItemRoot = newItemDocument.getFxomRoot();
//                newItemRoot.moveToFxomDocument(getFxomDocument());
//                assert newItemDocument.getFxomRoot() == null;
//                final FXOMObject rootObject = getFxomDocument().getFxomRoot();
//                if (rootObject == null) { // Empty document
//                    final Job job = new SetDocumentRootJob(context,
//                            newItemRoot, true /* usePredefinedSize */, "unused", this).extend(); //NOCHECK
//                    result = job.isExecutable();
//                } else {
//                    if (selection.isEmpty() || selection.isSelected(rootObject)) {
//                        // No selection or root is selected -> we insert below root
//                        targetCandidate = rootObject;
//                    } else {
//                        // Let's use the common parent of the selected objects.
//                        // It might be null if selection holds some non FXOMObject entries
//                        targetCandidate = selection.getAncestor();
//                    }
//                    final Job job = new InsertAsSubComponentJob(context,
//                            newItemRoot, targetCandidate, -1, this).extend();
//                    result = job.isExecutable();
//                }
//            }
//        }
//
//        return result;
//    }

    /**
     * Performs the 'wrap' edit action. This action creates an object
     * matching the specified class and reparent all the selected objects
     * below this new object.
     *
     * @param wrappingClass the wrapping class
     */
    public void performWrap(Class<?> wrappingClass) {
        assert canPerformWrap(wrappingClass);
        final AbstractWrapInJob job = AbstractWrapInJob.getWrapInJob(context, this, wrappingClass);
        jobManager.push(job.extend());
    }

    /**
     * Returns true if the 'wrap' action is permitted with the specified class.
     *
     * @param wrappingClass the wrapping class.
     * @return true if the 'wrap' action is permitted.
     */
    public boolean canPerformWrap(Class<?> wrappingClass) {
        if (AbstractWrapInJob.getClassesSupportingWrapping().contains(wrappingClass) == false) {
            return false;
        }
        final AbstractWrapInJob job = AbstractWrapInJob.getWrapInJob(context, this, wrappingClass);
        return job.extend().isExecutable();
    }


    /**
     * Performs the copy control action.
     */
    private void performCopy() {
        assert canPerformCopy(); // (1)
        assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();

        final ClipboardEncoder encoder = new ClipboardEncoder(osg.getSortedItems());
        assert encoder.isEncodable();
        Clipboard.getSystemClipboard().setContent(encoder.makeEncoding());
    }

    /**
     * Returns true if the selection is not empty.
     *
     * @return if the selection is not empty.
     */
    private boolean canPerformCopy() {
        return selection.getGroup() instanceof ObjectSelectionGroup;
    }

    /**
     * Performs the select all control action.
     * Select all sub components of the selection common ancestor.
     */
    private void performSelectAll() {
        assert canPerformSelectAll(); // (1)
        final FXOMObject rootObject = getFxomDocument().getFxomRoot();
        if (selection.isEmpty()) { // (1)
            // If the current selection is empty, we select the root object
            selection.select(rootObject);
        } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
            // Otherwise, select all sub components of the common ancestor ??
            final FXOMObject ancestor = selection.getAncestor();
            assert ancestor != null; // Because of (1)
            final BorderPaneHierarchyMask mask = new BorderPaneHierarchyMask(ancestor);
            final Set<FXOMObject> selectableObjects = new HashSet<>();
            // BorderPane special case : use accessories
            if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
                final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
                final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
                final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
                final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
                final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
                for (FXOMObject accessoryObject : new FXOMObject[]{
                    top, left, center, right, bottom}) {
                    if (accessoryObject != null) {
                        selectableObjects.add(accessoryObject);
                    }
                }
            } else {
                assert mask.isAcceptingSubComponent(); // Because of (1)
                selectableObjects.addAll(mask.getSubComponents());
            }
            selection.select(selectableObjects);
        } else if (selection.getGroup() instanceof GridSelectionGroup) {
            // Select ALL rows / columns
            final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
            final FXOMObject gridPane = gsg.getHitItem();
            assert gridPane instanceof FXOMInstance;
            final GridPaneHierarchyMask gridPaneMask = new GridPaneHierarchyMask(gridPane);
            int size = 0;
            switch (gsg.getType()) {
                case ROW:
                    size = gridPaneMask.getRowsSize();
                    break;
                case COLUMN:
                    size = gridPaneMask.getColumnsSize();
                    break;
                default:
                    assert false;
                    break;
            }
            // Select first index
            selection.select((FXOMInstance) gridPane, gsg.getType(), 0);
            for (int index = 1; index < size; index++) {
                selection.toggleSelection((FXOMInstance) gridPane, gsg.getType(), index);
            }
        } else {
            assert selection.getGroup() == null :
                    "Add implementation for " + selection.getGroup(); //NOCHECK

        }
    }

    /**
     * Returns true if the root object is not selected and if the sub components
     * of the selection common ancestor are not all already selected.
     *
     * @return if the root object is not selected and if the sub components of
     * the selection common ancestor are not all already selected.
     */
    private boolean canPerformSelectAll() {
        assert getFxomDocument() != null && getFxomDocument().getFxomRoot() != null;
        if (selection.isEmpty()) { // (1)
            return true;
        } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final FXOMObject rootObject = getFxomDocument().getFxomRoot();
            // Cannot select all if root is selected
            if (selection.isSelected(rootObject)) { // (1)
                return false;
            } else {
                // Cannot select all if all sub components are already selected
                final FXOMObject ancestor = selection.getAncestor();
                assert ancestor != null; // Because of (1)
                final BorderPaneHierarchyMask mask = new BorderPaneHierarchyMask(ancestor);
                // BorderPane special case : use accessories
                if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
                    final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
                    final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
                    final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
                    final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
                    final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
                    for (FXOMObject bpAccessoryObject : new FXOMObject[] {
                        top, left, center, right, bottom}) {
                        if (bpAccessoryObject != null
                                && selection.isSelected(bpAccessoryObject) == false) {
                            return true;
                        }
                    }
                } else if (mask.isAcceptingSubComponent()) {
                    for (FXOMObject subComponentObject : mask.getSubComponents()) {
                        if (selection.isSelected(subComponentObject) == false) {
                            return true;
                        }
                    }
                }
            }
        } else if (selection.getGroup() instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
            // GridSelectionGroup => at least 1 row/column is selected
            assert gsg.getIndexes().isEmpty() == false;
            return true;
        } else {
            assert selection.getGroup() == null :
                    "Add implementation for " + selection.getGroup(); //NOCHECK
        }
        return false;
    }

    /**
     * Performs the select parent control action.
     * If the selection is multiple, we select the common ancestor.
     */
    private void performSelectParent() {
        assert canPerformSelectParent(); // (1)
        final FXOMObject ancestor = selection.getAncestor();
        assert ancestor != null; // Because of (1)
        selection.select(ancestor);
    }

    /**
     * Returns true if the selection is not empty and the root object is not
     * selected.
     *
     * @return if the selection is not empty and the root object is not
     * selected.
     */
    private boolean canPerformSelectParent() {
        assert getFxomDocument() != null && getFxomDocument().getFxomRoot() != null;
        final FXOMObject rootObject = getFxomDocument().getFxomRoot();
        return !selection.isEmpty() && !selection.isSelected(rootObject);
    }

    /**
     * Performs the select next control action.
     */
    private void performSelectNext() {
        assert canPerformSelectNext(); // (1)

        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            final Set<FXOMObject> items = osg.getItems();
            assert items.size() == 1; // Because of (1)
            final FXOMObject selectedObject = items.iterator().next();
            final FXOMObject nextSibling = selectedObject.getNextSlibing();
            assert nextSibling != null; // Because of (1)
            selection.select(nextSibling);
        } else {
            assert asg instanceof GridSelectionGroup; // Because of (1)
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            final FXOMObject gridPane = gsg.getHitItem();
            final GridPaneHierarchyMask mask = new GridPaneHierarchyMask(gridPane);
            assert gridPane instanceof FXOMInstance;
            final Set<Integer> indexes = gsg.getIndexes();
            assert indexes.size() == 1; // Because of (1)
            int selectedIndex = indexes.iterator().next();
            int nextIndex = selectedIndex + 1;
            int size = 0;
            switch (gsg.getType()) {
                case ROW:
                    size = mask.getRowsSize();
                    break;
                case COLUMN:
                    size = mask.getColumnsSize();
                    break;
                default:
                    assert false;
                    break;
            }
            assert nextIndex < size; // Because of (1)
            selection.select((FXOMInstance) gridPane, gsg.getType(), nextIndex);
        }
    }

    /**
     * Returns true if the selection is single and the container of the selected
     * object container contains a child next to the selected one.
     *
     * @return if the selection is single and the container of the selected
     * object container contains a child next to the selected one.
     */
    private boolean canPerformSelectNext() {
        assert getFxomDocument() != null && getFxomDocument().getFxomRoot() != null;
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            final Set<FXOMObject> items = osg.getItems();
            if (items.size() != 1) {
                return false;
            }
            final FXOMObject selectedObject = items.iterator().next();
            return selectedObject.getNextSlibing() != null;
        } else if (asg instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            final Set<Integer> indexes = gsg.getIndexes();
            if (indexes.size() != 1) {
                return false;
            }
            final FXOMObject gridPane = gsg.getHitItem();
            final GridPaneHierarchyMask mask = new GridPaneHierarchyMask(gridPane);
            int size = 0;
            switch (gsg.getType()) {
                case ROW:
                    size = mask.getRowsSize();
                    break;
                case COLUMN:
                    size = mask.getColumnsSize();
                    break;
                default:
                    assert false;
                    break;
            }
            final int index = indexes.iterator().next();
            return index < size - 1;
        } else {
            assert selection.getGroup() == null :
                    "Add implementation for " + selection.getGroup(); //NOCHECK
        }
        return false;
    }

    /**
     * Performs the select previous control action.
     */
    private void performSelectPrevious() {
        assert canPerformSelectPrevious(); // (1)

        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            final Set<FXOMObject> items = osg.getItems();
            assert items.size() == 1; // Because of (1)
            final FXOMObject selectedObject = items.iterator().next();
            final FXOMObject previousSibling = selectedObject.getPreviousSlibing();
            assert previousSibling != null; // Because of (1)
            selection.select(previousSibling);
        } else {
            assert asg instanceof GridSelectionGroup; // Because of (1)
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            final FXOMObject gridPane = gsg.getHitItem();
            assert gridPane instanceof FXOMInstance;
            final Set<Integer> indexes = gsg.getIndexes();
            assert indexes.size() == 1; // Because of (1)
            int selectedIndex = indexes.iterator().next();
            int previousIndex = selectedIndex - 1;
            assert previousIndex >= 0; // Because of (1)
            selection.select((FXOMInstance) gridPane, gsg.getType(), previousIndex);
        }
    }

    /**
     * Returns true if the selection is single and the container of the selected
     * object container contains a child previous to the selected one.
     *
     * @return if the selection is single and the container of the selected
     * object container contains a child previous to the selected one.
     */
    private boolean canPerformSelectPrevious() {
        assert getFxomDocument() != null && getFxomDocument().getFxomRoot() != null;
        if (selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
            final Set<FXOMObject> items = osg.getItems();
            if (items.size() != 1) {
                return false;
            }
            final FXOMObject selectedObject = items.iterator().next();
            return selectedObject.getPreviousSlibing() != null;
        } else if (asg instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            final Set<Integer> indexes = gsg.getIndexes();
            if (indexes.size() != 1) {
                return false;
            }
            final int index = indexes.iterator().next();
            return index > 0;
        } else {
            assert selection.getGroup() == null :
                    "Add implementation for " + selection.getGroup(); //NOCHECK
        }
        return false;
    }

    /**
     * Performs the select none control action.
     */
    private void performSelectNone() {
        assert canPerformSelectNone();
        selection.clear();
    }

    /**
     * Returns true if the selection is not empty.
     *
     * @return if the selection is not empty.
     */
    private boolean canPerformSelectNone() {
        return getSelection().isEmpty() == false;
    }

    /**
     * If selection contains single FXOM object and this an fx:include instance, then
     * returns the included file. Else returns null.
     *
     * If the selection is single and is an included FXOM object :
     * 1) if included file source does not start with /,
     *    it's a path relative to the document location.
     *   - if FXOM document location is null (document not saved yet), return null
     *   - else return selection included file
     *
     * 2) if included file source starts with /,
     *    it's a path relative to the document class loader.
     *   - if FXOM document class loader is null, return null
     *   - else return selection included file
     *
     * @return the included file associated to the selected object or null.
     */
    @Override
    public File getIncludedFile() {
        final AbstractSelectionGroup asg = getSelection().getGroup();
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
        if (source.startsWith("/")) { //NOCHECK
            // Source relative to FXOM document class loader
            final ClassLoader classLoader = getFxomDocument().getClassLoader();
            if (classLoader != null) {
                final PrefixedValue pv = new PrefixedValue(
                        PrefixedValue.Type.CLASSLOADER_RELATIVE_PATH, source);
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
                final PrefixedValue pv = new PrefixedValue(
                        PrefixedValue.Type.DOCUMENT_RELATIVE_PATH, source);
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

    /**
     * Returns true if the selection is an included file that can be edited/revealed.
     *
     * @return true if the selection is an included file that can be edited/revealed.
     */
    private boolean canPerformIncludedFileAction() {
        return getIncludedFile() != null;
    }

    @Override
    public void performEditIncludedFxml() {
        assert canPerformIncludedFileAction(); // (1)
        final File includedFile = getIncludedFile();
        assert includedFile != null; // Because of (1)
        try {
            fileSystem.open(includedFile.getAbsolutePath());
        } catch (IOException ioe) {
        	dialog.showErrorAndWait(
        			I18N.getString("error.file.open.title"),
        			I18N.getString("error.file.open.message", includedFile.getAbsolutePath()),
        			"", ioe);
        }
    }

    @Override
    public void performRevealIncludeFxml() {
        assert canPerformIncludedFileAction(); // (1)
        final File includedFile = getIncludedFile();
        assert includedFile != null; // Because of (1)
        try {
        	fileSystem.revealInFileBrowser(includedFile);
        } catch (IOException ioe) {
        	dialog.showErrorAndWait(
        			I18N.getString("error.file.reveal.title"),
        			I18N.getString("error.file.reveal.message", includedFile.getAbsolutePath()),
        			I18N.getString("error.write.details"), ioe);
        }
    }

    /**
     * Returns true if the 'add context menu' action is permitted with the current
     * selection.
     * In other words, returns true if the selection contains only Control objects.
     *
     * @return true if the 'add context menu' action is permitted.
     */
    public boolean canPerformAddContextMenu() {
        return selection.isSelectionControl();
    }

    /**
     * Performs the 'add context menu' edit action. This method creates an instance of
     * ContextMenu and sets it in the contextMenu property of the
     * selected objects.
     */
    public void performAddContextMenu() {
        assert canPerformAddContextMenu();
        final Job addContextMenuJob = new AddContextMenuToSelectionJob(context, this).extend();
        getJobManager().push(addContextMenuJob);
    }

    /**
     * Returns true if the 'add tooltip' action is permitted with the current
     * selection.
     * In other words, returns true if the selection contains only Control objects.
     *
     * @return true if the 'add tooltip' action is permitted.
     */
    public boolean canPerformAddTooltip() {
        return selection.isSelectionControl();
    }

    /**
     * Performs the 'add tooltip' edit action. This method creates an instance of
     * Tooltip and sets it in the tooltip property of the
     * selected objects.
     */
    public void performAddTooltip() {
        assert canPerformAddTooltip(); // (1)
        final Job addTooltipJob = new AddTooltipToSelectionJob(context,this).extend();
        getJobManager().push(addTooltipJob);
   }


    private void updateFxomDocument(String fxmlText, URL fxmlLocation, ResourceBundle resources, boolean checkTheme) throws IOException {
        final FXOMDocument newFxomDocument;

        if (fxmlText != null) {
            newFxomDocument = new FXOMDocument(fxmlText, fxmlLocation, sceneBuilderManager.classloader().get(), resources);
        } else {
            newFxomDocument = null;
        }

        documentManager.fxomDocument().set(newFxomDocument);

        updateFileWatcher(newFxomDocument);


       //TODO remove comment
//        if (checkTheme) {
//            WarnThemeAlert.showAlertIfRequired(this, newFxomDocument, ownerWindow);
//        }
    }

//    private ResourceLoader getLibrary() {
//        // TODO Auto-generated method stub
//        return null;
//    }

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
        getErrorReport().forget();
        if (targetFileName.toLowerCase(Locale.ROOT).endsWith(".css")) { //NOCHECK
            getErrorReport().cssFileDidChange(target);
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
        errorReport.forget();
        updateFileWatcher(getFxomDocument());
//        setPickModeEnabled(false);
    }

    @Override
    public void setOwnerWindow(Stage ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    @Override
    public Stage getOwnerWindow() {
        return ownerWindow;
    }

    //TODO to remove
    @Override
    public FXOMDocument getFxomDocument() {
        return fxomDocument;
    }

//    //TODO to remove
//    @Override
//    public ObservableValue<Library> libraryProperty() {
//        return libraryProperty;
//    }

}
