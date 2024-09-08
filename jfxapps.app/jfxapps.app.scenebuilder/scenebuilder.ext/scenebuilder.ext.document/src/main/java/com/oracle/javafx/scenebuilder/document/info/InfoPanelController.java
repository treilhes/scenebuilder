/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.document.info;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scenebuilder.fxml.api.SbEditor;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyFxControllerJob;
import com.gluonhq.jfxapps.core.job.editor.atomic.ToggleFxRootJob;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class InfoPanelController extends AbstractFxmlController {

    @FXML private TableColumn<IndexEntry,String> leftTableColumn;
    @FXML private TableColumn<IndexEntry,FXOMObject> rightTableColumn;
    @FXML private Label bottomLabel;
    @FXML private VBox controllerClassVBox;
    @FXML CheckBox fxrootCheckBox;
    @FXML HBox controllerAndCogHBox;

    private IndexEntry.Type entryType = IndexEntry.Type.FX_ID;
    private ControllerClassEditor controllerClassEditor;
    private boolean controllerDidLoadFxmlOver = false;
    private final PropertyEditorFactorySession editorFactorysession;
    private final SbEditor editor;
    private final FxmlDocumentManager documentManager;
    private final Selection selection;
    private final JobManager jobManager;
    private final ToggleFxRootJob.Factory toggleFxRootJobFactory;
    private final ModifyFxControllerJob.Factory modifyFxControllerJobFactory;

    public InfoPanelController(
            SceneBuilderManager scenebuilderManager,
            FxmlDocumentManager documentManager,
    		SbEditor editor,
    		Selection selection,
    		JobManager jobManager,
    		PropertyEditorFactory propertyEditorFactory,
    		ToggleFxRootJob.Factory toggleFxRootJobFactory,
    		ModifyFxControllerJob.Factory modifyFxControllerJobFactory
    		) {
        super(scenebuilderManager, documentManager, InfoPanelController.class.getResource("InfoPanel.fxml"), I18N.getBundle());
        this.editor = editor;
        this.documentManager = documentManager;
        this.selection = selection;
        this.jobManager = jobManager;
        this.toggleFxRootJobFactory = toggleFxRootJobFactory;
        this.modifyFxControllerJobFactory = modifyFxControllerJobFactory;
        this.editorFactorysession = propertyEditorFactory.newSession();
    }

    public IndexEntry.Type getEntryType() {
        return entryType;
    }

    public void setEntryType(IndexEntry.Type entryType) {
        this.entryType = entryType;
        updateEntriesNow();
    }

    /*
     * AbstractPanelController
     */

    protected void fxomDocumentDidChange(FXOMDocument oldDocument) {
        requestEntriesUpdate();
        updateAsPerRootNodeStatus();
        updateControllerAndControllerClassEditor();

        if (fxrootCheckBox != null) {
            fxrootCheckBox.selectedProperty().removeListener(checkBoxListener);
            fxrootCheckBox.setSelected(isFxRoot());
            fxrootCheckBox.selectedProperty().addListener(checkBoxListener);
        }
    }

    protected void sceneGraphRevisionDidChange() {
        requestEntriesUpdate();
        updateAsPerRootNodeStatus();
    }

    protected void jobManagerRevisionDidChange() {
        requestEntriesUpdate();
        updateAsPerRootNodeStatus();
        updateControllerClassEditor();
        fxrootCheckBox.selectedProperty().removeListener(checkBoxListener);
        fxrootCheckBox.setSelected(isFxRoot());
        fxrootCheckBox.selectedProperty().addListener(checkBoxListener);
    }

    protected void editorSelectionDidChange() {
        final Set<IndexEntry> selectedEntries = new HashSet<>();

        if (!selection.isEmpty()){
            selectedEntries.addAll(searchIndexEntries(selection.getGroup().getItems()));
        }

        final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
        stopListeningToTableViewSelection();
        tableView.getSelectionModel().clearSelection();
        for (IndexEntry e : selectedEntries) {
            tableView.getSelectionModel().select(e);
        }
        startListeningToTableViewSelection();
    }

    /*
     * AbstractFxmlController
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert leftTableColumn != null;
        assert rightTableColumn != null;
        assert leftTableColumn.getTableView() == rightTableColumn.getTableView();
        assert bottomLabel != null;
        assert controllerClassVBox != null;
        assert fxrootCheckBox != null;
        assert controllerAndCogHBox != null;

        performInitialization();

        documentManager.fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));
        documentManager.sceneGraphRevisionDidChange().subscribe(c -> sceneGraphRevisionDidChange());
        documentManager.selectionDidChange().subscribe(c -> editorSelectionDidChange());
        jobManager.revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());
    }

    // This method is a step to a lazy initialization, to reduce startup time.
    // We didn't find a smart way to detect when the TitledPane containing
    // the InfoPanel is opened for the first time, except by putting a listener
    // on the expandedProperty: this approach means DocumentWindowController
    // has to call performInitialization (turned public), a path we do not want
    // to take.
    private void performInitialization() {
        SelectionState selectionState = new SelectionStateImpl(selection);

        if (controllerClassEditor == null) {
            controllerClassEditor = (ControllerClassEditor) editorFactorysession.getControllerClassEditor(selectionState);
        } else {
            controllerClassEditor.reset(null, selectionState);
        }

        HBox propNameNode = controllerClassEditor.getPropNameNode();

        // Make so that the property name appears left justified in the VBox
        propNameNode.setAlignment(Pos.CENTER_LEFT);
        controllerClassVBox.getChildren().add(0, propNameNode);

        // Initialize field with current value, if defined.
        controllerAndCogHBox.getChildren().add(controllerClassEditor.getValueEditor());
        controllerAndCogHBox.getChildren().add(controllerClassEditor.getMenu());

        // Need to react each time value of fx controller is changed (direct user input)
        controllerClassEditor.valueProperty().addListener((ChangeListener<Object>) (ov, t, t1) -> InfoPanelController.this.updateControllerAndControllerClassEditor((String)t1));

        // We e.g. an Untitled document is saved we need to trigger a scan for
        // potential controller classes.
        editor.fxmlLocationProperty().addListener((ChangeListener<URL>) (ov, t, t1) -> {
            if (t1 != null) {
                resetSuggestedControllerClasses(t1);
            }
        });

        // DTL-6626
        controllerClassEditor.getTextField().focusedProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> {
            if (!t1) {
                // Focus loss triggers an update. The text field can be empty.
                updateControllerAndControllerClassEditor(controllerClassEditor.getTextField().getText());
            }
        });

        leftTableColumn.setCellValueFactory(new PropertyValueFactory<>("key")); //NOCHECK
        rightTableColumn.setCellValueFactory(new PropertyValueFactory<>("fxomObject")); //NOCHECK
        leftTableColumn.setCellFactory(new LeftCell.Factory());
        rightTableColumn.setCellFactory(new RightCell.Factory());

        fxrootCheckBox.setSelected(isFxRoot());
        fxrootCheckBox.selectedProperty().addListener(checkBoxListener);

        controllerDidLoadFxmlOver = true; // Must be called before updateAsPerRootNodeStatus()
        requestEntriesUpdate();
        updateAsPerRootNodeStatus();
        updateControllerAndControllerClassEditor();
    }


    /*
     * Private
     */
    private final static String IGNORED = "ignored"; //NOCHECK

    private synchronized void updateControllerAndControllerClassEditor() {
        updateControllerAndControllerClassEditor(IGNORED);
    }

    private synchronized void updateControllerAndControllerClassEditor(String className) {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument != null) {
            FXOMObject root = fxomDocument.getFxomRoot();
            if (root != null) {
                String zeClassName = computeProperClassName(className, root);

                final AbstractJob job = modifyFxControllerJobFactory.getJob(root, zeClassName);

                if (job.isExecutable()) {
                    jobManager.push(job);
                }

                updateControllerClassEditor(zeClassName);
            }
        }
    }

    private void updateControllerClassEditor() {
        updateControllerClassEditor(IGNORED);
    }

    private void updateControllerClassEditor(String className) {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument != null) {
            FXOMObject root = fxomDocument.getFxomRoot();
            if (root != null) {
                String zeClassName = computeProperClassName(className, root);

                if (controllerClassEditor != null) {
                    controllerClassEditor.setUpdateFromModel(true);
                    controllerClassEditor.setValue(zeClassName);
                    controllerClassEditor.setUpdateFromModel(false);
                }
            }
        }
    }

    private String computeProperClassName(String className, FXOMObject root) {
        String res = className;

        if (className != null && className.equals(IGNORED)) {
            res = root.getFxController();
        }

        // When the user set an empty string we consider it means
        // no controller value needs to be set.
        if (className != null && className.isEmpty()) {
            res = null;
        }

        return res;
    }

    private void requestEntriesUpdate() {
        updateEntriesNow();
    }


    private void updateEntriesNow() {

        if (leftTableColumn != null) {
            final List<IndexEntry> newEntries = FXCollections.observableArrayList();

            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            if (fxomDocument != null) {
                switch(entryType) {
                    case FX_ID: {
                        final Map<String, FXOMObject> fxIds
                                = fxomDocument.collect(FxIdCollector.fxIdsMap());
                        for (Map.Entry<String, FXOMObject> e : fxIds.entrySet()) {
                            final String fxId = e.getKey();
                            final FXOMObject fxomObject = e.getValue();
                            newEntries.add(new IndexEntry(fxId, entryType, fxomObject));
                        }
                        break;
                    }

                    case HANDLER: {
                        break;
                    }

                    case RESOURCE_KEY: {
                        break;
                    }

                    case STYLECLASS:
                        break;

                    default:
                        break;
                }
            }


            // Update items in table view
            final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
            stopListeningToTableViewSelection();
            tableView.getItems().clear();
            tableView.getItems().addAll(newEntries);
            startListeningToTableViewSelection();

            // Update bottom label
            final int count = newEntries.size();
            final String labelText;
            switch(count) {
                case 0:
                    labelText = ""; //NOCHECK
                    break;
                case 1:
                    labelText = "1 " //NOCHECK
                            + I18N.getString("info.label.item");
                    break;
                default:
                    labelText = count + " " //NOCHECK
                            + I18N.getString("info.label.items");
                    break;
            }
            bottomLabel.setText(labelText);

            // Setup selection again
            editorSelectionDidChange();
        }
    }


    private void startListeningToTableViewSelection() {
        assert leftTableColumn != null;
        final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
        tableView.getSelectionModel().getSelectedItems().addListener(tableViewSelectionListener);
    }

    private void stopListeningToTableViewSelection() {
        assert leftTableColumn != null;
        final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
        tableView.getSelectionModel().getSelectedItems().removeListener(tableViewSelectionListener);
    }

    private final ListChangeListener<IndexEntry> tableViewSelectionListener
        = change -> tableSelectionDidChange();

    private void tableSelectionDidChange() {
        final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
        final List<IndexEntry> selectedItems =
                tableView.getSelectionModel().getSelectedItems();
        Set<FXOMObject> selectedFxomObjects = new HashSet<>();

        for (IndexEntry i : selectedItems) {
            selectedFxomObjects.add(i.getFxomObject());
        }

        //TODO check for infinite loop here
        //stopListeningToEditorSelection();
        selection.select(selectedFxomObjects);
        //startListeningToEditorSelection();
    }


    private Set<IndexEntry> searchIndexEntries(Set<FXOMObject> fxomObjects) {
        assert fxomObjects != null;

        final TableView<IndexEntry> tableView = leftTableColumn.getTableView();
        final Set<IndexEntry> result = new HashSet<>();
        for (IndexEntry e : tableView.getItems()) {
            if (fxomObjects.contains(e.getFxomObject())) {
                result.add(e);
            }
        }

        return result;
    }


//    private List<String> getSuggestedControllerClasses(URL location) {
//        Glossary glossary = getEditorController().getGlossary();
//
//        if (location == null && getEditorController().getFxomDocument() != null) {
//            location = getEditorController().getFxomDocument().getLocation();
//        }
//
//        return glossary.queryControllerClasses(location);
//    }

    // When there is no defined root node we reset and disable the class field
    // and the fx:root check box.
    private void updateAsPerRootNodeStatus() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (controllerDidLoadFxmlOver && fxomDocument != null) {

            if (fxomDocument.getFxomRoot() == null) {
                fxrootCheckBox.setDisable(true);
                controllerClassEditor.setDisable(true);
                controllerClassEditor.setUpdateFromModel(true);
                controllerClassEditor.setValue(null); //NOCHECK
                controllerClassEditor.setUpdateFromModel(false);
            } else {
                fxrootCheckBox.setDisable(false);
                String topClassName = fxomDocument.getGlue().getMainElement().getTagName();
                fxrootCheckBox.setTooltip(new Tooltip(I18N.getString("info.tooltip.controller", topClassName)));
                controllerClassEditor.setDisable(false);
            }
        }
    }

    private void toggleFxRoot() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument != null) {
            final FXOMObject root = fxomDocument.getFxomRoot();
            if (root instanceof FXOMInstance) {
                final AbstractJob job = toggleFxRootJobFactory.getJob();
                if (job.isExecutable()) {
                    // TODO check for infinite loop here
                    //stopListeningToJobManagerRevision();
                    jobManager.push(job);
                    //startListeningToJobManagerRevision();
                }
            }
        }
    }

    private boolean isFxRoot() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument != null) {
            final FXOMObject root = fxomDocument.getFxomRoot();
            if (root instanceof FXOMInstance) {
                return ((FXOMInstance)root).isFxRoot();
            }
        }

        return false;
    }

    private final ChangeListener<Boolean> checkBoxListener = (ov, t, t1) -> toggleFxRoot();

    private void resetSuggestedControllerClasses(URL location) {
        if (controllerClassEditor != null) {
            // The listener on fxmlLocationProperty is called before the file
            // denoted by the location is created on disk, hence the runLater.
            JfxAppPlatform.runOnFxThread(() -> {
                controllerClassEditor.setUpdateFromModel(true);
                controllerClassEditor.reset(null, null);
                controllerClassEditor.setUpdateFromModel(false);
            });
        }
    }
}
