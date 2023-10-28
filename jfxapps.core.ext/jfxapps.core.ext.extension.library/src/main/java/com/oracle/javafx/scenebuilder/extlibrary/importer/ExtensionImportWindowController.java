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
package com.oracle.javafx.scenebuilder.extlibrary.importer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionFilterTransform;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReport;
import com.oracle.javafx.scenebuilder.extlibrary.library.ExtensionReportEntry;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ExtensionImportWindowController extends AbstractModalDialog {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionImportWindowController.class);

    ClassLoader importClassLoader;
    Node zeNode = new Label(I18N.getString("import.preview.unable"));

    private int numOfImportedJar;

    private Stage owner;

    @FXML
    private VBox leftHandSidePart;

    @FXML
    ListView<ImportRow> importList = new ListView<>();

    @FXML
    private SplitPane topSplitPane;

    @FXML
    Group previewGroup;

    @FXML
    Label numOfItemsLabel;

    @FXML
    Label classNameLabel;

    @FXML
    Label previewHintLabel;

    @FXML
    ToggleButton checkAllUncheckAllToggle;

    private final Dialog dialog;
    private ExtensionFilterTransform filter;

    protected ExtensionImportWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            Dialog dialog) {
        super(sceneBuilderManager, iconSetting, ExtensionImportWindowController.class.getResource("ImportDialog.fxml"), I18N.getBundle(), null);
        // libPanelController = lpc;
        // importFiles = new ArrayList<>(files);
        // this.copyFilesToUserLibraryDir = copyFilesToUserLibraryDir;
        // this.artifactsFilter = artifactsFilter;
        this.owner = owner;
        // this.mavenPreferences = mavenPreferences;
        this.dialog = dialog;
    }

    public ExtensionFilterTransform editTransform(List<ExtensionReport> reports, ExtensionFilterTransform controlFilter, ClassLoader classLoader) {
        assert Platform.isFxApplicationThread();
        // This toFront() might not be necessary because import window is modal
        // and is chained to the document window. Anyway experience showed
        // we need it (FX 8 b106). This is suspicious, to be investigated ...
        // But more tricky is why toFront() is called here. Mind that when toFront()
        // is called while isShowing() returns false isn't effective: that's
        // why toFront called at the end of controllerDidCreateStage() or
        // controllerDidLoadContentFxml() wasn't an option. Below is the
        // earliest place it has been proven effective, at least on my machine.
        getStage().toFront();

        // We get the set of items which are already excluded prior to the current
        // import.
//        UserLibrary userLib = ((UserLibrary) libPanelController.getEditorController().libraryProperty().getValue());
        importList.getItems().clear();
        this.importClassLoader = classLoader;
        this.filter = controlFilter;

        List<ExtensionReport> jarReportList = reports; // blocking call
        final Callback<ImportRow, ObservableValue<Boolean>> importRequired = row -> row.importRequired();
        importList.setCellFactory(CheckBoxListCell.forListView(importRequired));

        for (ExtensionReport jarReport : jarReportList) {
            Path file = jarReport.getSource();
            String jarName = file.getName(file.getNameCount() - 1).toString();
            StringBuilder sb = new StringBuilder();

            if (Files.isDirectory(file)) {
                sb.append(I18N.getString("log.info.explore.folder.results", jarName));
            } else {
                sb.append(I18N.getString("log.info.explore.folder.jar", jarName));
            }
            sb.append("\n"); //NOCHECK
            for (ExtensionReportEntry e : jarReport.getEntries()) {
                sb.append("> ").append(e.toString()).append("\n"); //NOCHECK
                if ((e.getStatus() == ExtensionReportEntry.Status.OK)) {
                    boolean checked = true;

                    final ImportRow importRow = new ImportRow(checked, jarReport, e);
                    importList.getItems().add(importRow);
                    importRow.importRequired().addListener((ChangeListener<Boolean>) (ov, oldValue, newValue) -> {
                        final int numOfComponentToImport = getNumOfComponentToImport(importList);
                        updateOKButtonTitle(numOfComponentToImport);
                        updateSelectionToggleText(numOfComponentToImport);
                    });

                } else {
                    if (e.getException() != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.getException().printStackTrace(pw);
                        sb.append(">> " + sw.toString());
                    }
                }
            }
            logger.info(sb.toString());
        }
//        userLib.getExplorationJarReports().addAll(jarReportList);

        // Sort based on the simple class name.
        Collections.sort(importList.getItems(), new ImportRowComparator());

        final int numOfComponentToImport = getNumOfComponentToImport(importList);
        updateOKButtonTitle(numOfComponentToImport);
        updateOKCancelDefaultState(numOfComponentToImport);
        updateSelectionToggleText(numOfComponentToImport);
        updateNumOfItemsLabelAndSelectionToggleState();

        unsetProcessing();

        ButtonID userChoice = showAndWait();

        if (userChoice == ButtonID.OK) {
            return controlFilter;
        }

        return null;

    }

    /*
     * Event handlers
     */

    @Override
    protected void cancelButtonPressed(ActionEvent e) {
        getStage().close();
    }

    @Override
    protected void okButtonPressed(ActionEvent e) {
        getStage().close();
    }

    @Override
    protected void actionButtonPressed(ActionEvent e) {
        // NOTHING TO DO (no ACTION button)
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void onCloseRequest() {
        cancelButtonPressed(null);
    }

    @Override
    public void controllerDidLoadContentFxml() {
        assert topSplitPane != null;
        // The SplitPane should not be visible from the beginning: only the progressing
        // bar is initially visible.
        assert topSplitPane.isVisible() == true;
        assert previewGroup != null;
        assert importList != null;
        assert numOfItemsLabel != null;
        assert leftHandSidePart != null;
        assert classNameLabel != null;
        assert previewHintLabel != null;
        assert checkAllUncheckAllToggle != null;

        // Setup dialog buttons
        setOKButtonVisible(true);
        setDefaultButtonID(ButtonID.OK);
        setShowDefaultButton(true);

        // Setup Select All / Unselect All toggle
        // Initially all items are Selected.
        checkAllUncheckAllToggle.selectedProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> {
            if (t1) {
                for (ImportRow row1 : importList.getItems()) {
                    row1.setImportRequired(false);
                }
                checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.checkall"));
            } else {
                for (ImportRow row2 : importList.getItems()) {
                    row2.setImportRequired(true);
                }
                checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.uncheckall"));
            }
        });

        setProcessing();

        // We do not want the list becomes larger when the window is made larger.
        // The way to make the list larger is to use the splitter.
        SplitPane.setResizableWithParent(leftHandSidePart, false);

    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(I18N.getString("import.window.title"));
    }

    /*
     * Private
     */

    private void closeClassLoader() throws IOException {
//        if (importClassLoader != null) {
//            importClassLoader.close();
//        }
    }

    // This method returns a new list of File made of the union of the provided
    // one and jar files found in the user library dir.
    List<File> buildListOfAllFiles(List<File> importFiles) throws IOException {
//        final List<File> res = new ArrayList<>(importFiles);
//        File userLibraryDir = ((UserLibrary) libPanelController.getEditorController().libraryProperty().getValue()).getPath();
//        if (userLibraryDir.exists()) {
//            Path userLibraryPath = userLibraryDir.toPath();
//            try (DirectoryStream<Path> stream = Files.newDirectoryStream(userLibraryPath)) {
//                for (Path entry : stream) {
//                    if (entry.toString().endsWith(".jar")) { //NOCHECK
//    //                    System.out.println("ImportWindowController::buildListOfAllFiles: Adding " + element); //NOCHECK
//                        res.add(entry.toFile());
//                    }
//                }
//            }
//        }
//        // add artifacts jars (main and dependencies)
//        res.addAll(mavenPreferences.getArtifactsFilesWithDependencies());
//
//        return res;
        return null;
    }

    private void showErrorDialog(Exception exception) {
        dialog.showErrorAndWait(I18N.getString("import.error.title"), I18N.getString("import.error.message"),
                I18N.getString("import.error.details"), exception);
    }

    void updateImportClassLoader(URLClassLoader cl) {
        // this.importClassLoader = cl;
    }

    void unsetProcessing() {

        importList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ImportRow>) (ov, t, t1) -> {
            previewGroup.getChildren().clear();

//            try {
//                InputStream is = null;
//                //create zenode here
//                zeNode = flowbox;
//                is.close();
//            } catch (IOException ioe) {
//                showErrorDialog(ioe);
//            }


            previewGroup.getChildren().add(zeNode);


            String name = t1.getReport().getSource().getFileName().toString();
            if (t1.getReportEntry().getClassName() != null) {
                name += " > " + t1.getReportEntry().getClassName(); //NOCHECK
            } else {
                name += " > " + t1.getReportEntry().getName(); //NOCHECK
            }
            classNameLabel.setText(name);//t1.getReportEntry().getKlass().getName());

        });

        // We avoid to get an empty Preview area at first.
        if (importList.getItems().size() > 0) {
            importList.getSelectionModel().selectFirst();
        }
    }


    private void refreshItems(ImportRow row) {


        final ExtensionReport report = row.getReport();
        final ExtensionReportEntry entry = row.getReportEntry();
        ExtensionReport ir = new ExtensionReport(report.getSource());
        ir.getEntries().add(entry);

        List<ExtensionReport> list = filter.filter(List.of(ir));
    }


    private URLClassLoader getClassLoaderForFiles(List<File> files) {
        return new URLClassLoader(makeURLArrayFromFiles(files));
    }

    private URL[] makeURLArrayFromFiles(List<File> files) {
        final URL[] result = new URL[files.size()];
        try {
            int index = 0;
            for (File file : files) {
                URL url = file.toURI().toURL();
                if (url.toString().endsWith(".jar")) { //NOCHECK
                    result[index] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/ // NOCHECK
                } else {
                    result[index] = url; // <-- file:/path/to/folder/
                }

                index++;
            }
        } catch (MalformedURLException x) {
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOCHECK
        }

        return result;
    }

    private void setProcessing() {
        cancelButton.setDefaultButton(true);
    }

    private int getNumOfComponentToImport(final ListView<ImportRow> list) {
        int res = 0;

        for (final ImportRow row : list.getItems()) {
            if (row.isImportRequired()) {
                res++;
            }
        }

        return res;
    }

    private List<String> getExcludedItems() {
//        List<String> res = new ArrayList<>(alreadyExcludedItems);
//
//        for (ImportRow row : importList.getItems()) {
//            if (! row.isImportRequired()) {
//                res.add(row.getCanonicalClassName());
//            }
//        }
//        return res;
        return null;
    }

    public List<String> getNewExcludedItems() {
        return importList.getItems().stream().filter(r -> !r.isImportRequired()).map(ImportRow::getCanonicalClassName)
                .collect(Collectors.toList());
    }

    // The title of the button is important in the sense it says to the user
    // what action will be taken.
    // In the most common case one or more component are selected in the list,
    // but it is also possible to get an empty list, in which case the user may
    // want to import the jar file anyway; it makes sense in ooder to resolve
    // dependencies other jars have onto it.
    // See DTL-6531 for details.
    private void updateOKButtonTitle(int numOfComponentToImport) {
        if (numOfComponentToImport == 0) {
            if (numOfImportedJar == 1) {
                setOKButtonTitle(I18N.getString("import.button.import.jar"));
            } else {
                setOKButtonTitle(I18N.getString("import.button.import.jars"));
            }
        } else if (numOfComponentToImport == 1) {
            setOKButtonTitle(I18N.getString("import.button.import.component"));
        } else {
            setOKButtonTitle(I18N.getString("import.button.import.components"));
        }
    }

    private void updateOKCancelDefaultState(int numOfComponentsToImport) {
        if (numOfComponentsToImport == 0) {
            cancelButton.setDefaultButton(true);
            cancelButton.requestFocus();
        } else {
            okButton.setDefaultButton(true);
            okButton.requestFocus();
        }
    }

    void updateNumOfItemsLabelAndSelectionToggleState() {
        final int num = importList.getItems().size();
        if (num == 0 || num == 1) {
            numOfItemsLabel.setText(num + " " // NOI18N
                    + I18N.getString("import.num.item"));
        } else {
            numOfItemsLabel.setText(num + " " // NOI18N
                    + I18N.getString("import.num.items"));
        }

        if (num >= 1) {
            checkAllUncheckAllToggle.setDisable(false);
        }
    }

    private void updateSelectionToggleText(int numOfComponentToImport) {
        if (numOfComponentToImport == 0) {
            checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.checkall"));
        } else {
            checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.uncheckall"));
        }
    }

}
