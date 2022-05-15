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
package com.oracle.javafx.scenebuilder.imagelibrary.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageFilterTransform;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageFilterTransform.FontImage;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageFilterTransform.FontImageItem;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageFilterTransform.StandardImage;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageReport;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageReportEntry;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageExplorerUtil;
import com.oracle.javafx.scenebuilder.javafx.controls.IntegerField;
import com.oracle.javafx.scenebuilder.library.util.LibraryUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImageImportWindowController extends AbstractModalDialog {

    private static final Logger logger = LoggerFactory.getLogger(ImageImportWindowController.class);

    public enum PrefSize {

        DEFAULT(-1, -1),
        TWO_HUNDRED_BY_ONE_HUNDRED(200, 100),
        TWO_HUNDRED_BY_TWO_HUNDRED(200, 200),
        THREE_HUNDRED_TWENTY_BY_TWO_HUNDRED_FIFTY_SIX(320, 256),
        SCREEN_640_480(640, 480),
        SCREEN_800_600(800, 600),
        SCREEN_1024_768(1024, 768),
        CUSTOM(-2, -2);

        private double width;
        private double height;

        PrefSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        @Override
        public String toString() {
            switch (this) {
            case CUSTOM:
                return "Custom";
            case DEFAULT:
                return I18N.getString("import.choice.builtin");
            default:
                return Math.round(width) + " x " + Math.round(height);
            }
        }
    };

//    final List<File> importFiles;
//    private final LibraryPanelController libPanelController;
    // Task<List<ControlReportImpl>> exploringTask = null;
    ClassLoader importClassLoader;
    Node zeNode = new Label(I18N.getString("import.preview.unable"));
    double builtinPrefWidth;
    double builtinPrefHeight;
    private int numOfImportedJar;
    // private boolean copyFilesToUserLibraryDir;
    private Stage owner;

    // At first we put in this collection the items which are already excluded,
    // basically all which are listed in the filter file.
    // When constructing the list of items discovered in new jar file being imported
    // we uncheck already excluded items and remove them from the collection.
    // When the user clicks the Import button the collection might contain the
    // items we retain from older import actions.


    @FXML
    private VBox leftHandSidePart;

//    @FXML
//    private Label processingLabel;

//    @FXML
//    ProgressIndicator processingProgressIndicator;

    @FXML
    ListView<ImportRow> importList = new ListView<>();

    @FXML
    ChoiceBox<PrefSize> defSizeChoice;

    @FXML
    private HBox customBox;

    @FXML
    private IntegerField widthField;

    @FXML
    private IntegerField heightField;

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
    private ImageFilterTransform filter;

//    protected ImportWindowController(Api api, LibraryPanelController lpc,  List<File> files, MavenArtifactsPreferences mavenPreferences, Stage owner) {
//        this(api, lpc, files, mavenPreferences, owner, true, new ArrayList<>());
//    }

    protected ImageImportWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            Dialog dialog) {
        super(sceneBuilderManager, iconSetting, ImageImportWindowController.class.getResource("ImportDialog.fxml"), I18N.getBundle(), null);
        // libPanelController = lpc;
        // importFiles = new ArrayList<>(files);
        // this.copyFilesToUserLibraryDir = copyFilesToUserLibraryDir;
        // this.artifactsFilter = artifactsFilter;
        this.owner = owner;
        // this.mavenPreferences = mavenPreferences;
        this.dialog = dialog;
    }

    public ImageFilterTransform editTransform(List<ImageReport> reports, ImageFilterTransform controlFilter, ClassLoader classLoader) {
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

        List<ImageReport> jarReportList = reports; // blocking call
        final Callback<ImportRow, ObservableValue<Boolean>> importRequired = row -> row.importRequired();
        importList.setCellFactory(CheckBoxListCell.forListView(importRequired));

        for (ImageReport jarReport : jarReportList) {
            Path file = jarReport.getSource();
            String jarName = file.getName(file.getNameCount() - 1).toString();
            StringBuilder sb = new StringBuilder(I18N.getString(
                    "log.info.explore." + (Files.isDirectory(file) ? "folder" : "jar") + ".results", jarName))
                            .append("\n");
            for (ImageReportEntry e : jarReport.getEntries()) {
                sb.append("> ").append(e.toString()).append("\n");
                if ((e.getStatus() == ImageReportEntry.Status.OK)) {
                    boolean checked = true;

                    switch (e.getType()) {
                    case FONT_ICONS:
                    {
                        FontImage font = controlFilter.getOrCreateFontImage(e.getFontName());
                        checked = font.isImported();

                        final ImportRow importRow = new ImportRow(checked, jarReport, e, null);
                        importList.getItems().add(importRow);
                        importRow.importRequired().addListener((ChangeListener<Boolean>) (ov, oldValue, newValue) -> {
                            font.setImported(newValue);
                            final int numOfComponentToImport = getNumOfComponentToImport(importList);
                            updateOKButtonTitle(numOfComponentToImport);
                            updateSelectionToggleText(numOfComponentToImport);
                        });
                        break;
                    }
                    case IMAGE:
                    {
                        StandardImage img = controlFilter.getOrCreateStandardImage(e.getName());
                        checked = img.isImported();

                        final ImportRow importRow = new ImportRow(checked, jarReport, e, null);
                        importList.getItems().add(importRow);
                        importRow.importRequired().addListener((ChangeListener<Boolean>) (ov, oldValue, newValue) -> {
                            img.setImported(newValue);
                            final int numOfComponentToImport = getNumOfComponentToImport(importList);
                            updateOKButtonTitle(numOfComponentToImport);
                            updateSelectionToggleText(numOfComponentToImport);
                        });
                        break;
                    }
                    default:
                        break;
                    }

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
//        exploringTask = null;
//

//        try {
//            closeClassLoader();
//
//            UserLibrary userLib = ((UserLibrary) libPanelController.getEditorController().libraryProperty().getValue());
//
//            if (copyFilesToUserLibraryDir) {
//                // collect directories from importFiles and add to library.folders file
//                // for other filex (jar, fxml) copy them directly
//                List<File> folders = new ArrayList<>(importFiles.size());
//                List<File> files = new ArrayList<>(importFiles.size());
//
//                for (File file : importFiles) {
//                    if (file.isDirectory())
//                        folders.add(file);
//                    else
//                        files.add(file);
//                }
//
//                if (!files.isEmpty())
//                    libPanelController.copyFilesToUserLibraryDir(files);
//
//                Path foldersMarkerPath = userLib.getPath().toPath().resolve(LibraryUtil.FOLDERS_LIBRARY_FILENAME);
//
//                if (!Files.exists(foldersMarkerPath))
//                    Files.createFile(foldersMarkerPath);
//
//                Set<String> lines = new TreeSet<>(Files.readAllLines(foldersMarkerPath));
//                lines.addAll(folders.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()));
//
//                Files.write(foldersMarkerPath, lines);
//            }
//
//            if (copyFilesToUserLibraryDir) {
//                userLib.setFilter(getExcludedItems());
//            }
//        } catch (IOException ex) {
//            showErrorDialog(ex);
//        } finally {
//            alreadyExcludedItems.clear();
//        }
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
//        assert processingLabel != null;
//        assert processingProgressIndicator != null;
        assert customBox != null;
        assert widthField != null;
        assert heightField != null;
        assert previewGroup != null;
        assert importList != null;
        assert defSizeChoice != null;
        assert numOfItemsLabel != null;
        assert leftHandSidePart != null;
        assert classNameLabel != null;
        assert previewHintLabel != null;
        assert checkAllUncheckAllToggle != null;

        // Setup dialog buttons
        setOKButtonVisible(true);
        setDefaultButtonID(ButtonID.OK);
        setShowDefaultButton(true);

        // Setup size choice box
        defSizeChoice.getItems().clear();
        // Care to have values in sync with definition of PrefSize
        defSizeChoice.getItems().addAll(PrefSize.values()); // NOI18N
        defSizeChoice.getSelectionModel().selectFirst();
        defSizeChoice.getSelectionModel().selectedItemProperty().addListener((ChangeListener<PrefSize>) (ov, t, t1) -> {
            assert t1 instanceof PrefSize;
            updateSize(t1);
        });

        ChangeListener<String> sizeListener = (ob, o, n) -> {
            updateZeNodeSize();
        };
        UnaryOperator<Change> integer4DigitFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*") && change.getControlNewText().length() <= 4) {
                return change;
            }
            return null;
        };

        widthField.setTextFormatter(new TextFormatter<>(integer4DigitFilter));
        heightField.setTextFormatter(new TextFormatter<>(integer4DigitFilter));
        widthField.textProperty().addListener(sizeListener);
        heightField.textProperty().addListener(sizeListener);

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
//        processingProgressIndicator.setVisible(false);
//        processingLabel.setVisible(false);
//        topSplitPane.setVisible(true);

        importList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ImportRow>) (ov, t, t1) -> {
            previewGroup.getChildren().clear();

//            String fxmlText;
//            if (LibraryUtil.isFxmlPath(t1.getReport().getSource())) {
//                try {
//                    fxmlText = Files.readString(t1.getReport().getSource());
//                } catch (IOException e) {
//                    fxmlText = "";
//                    logger.error("Unable to load the fxml file content", e);
//                }
//            } else {
//                fxmlText = ImageBuiltinLibrary.makeFxmlText(t1.getReportEntry().getKlass());
//            }


            try {

                InputStream is = null;
                if (LibraryUtil.isJarPath(t1.getReport().getSource()) || Files.isDirectory(t1.getReport().getSource())) {
                    is = importClassLoader.getResourceAsStream(t1.getReportEntry().getResourceName());
                } else {
                    is = new FileInputStream(t1.getReport().getSource().toFile());
                }

                switch (t1.getReportEntry().getType()) {
                case IMAGE:
                {
                    //temp until viewbox management
                    StandardImage img = filter.getOrCreateStandardImage(t1.getReportEntry().getName());

                    ImageView imv = new ImageView(new Image(is));
                    zeNode = imv;

                    if (builtinPrefWidth == 0 || builtinPrefHeight == 0) {
                        if (zeNode instanceof ImageView) {
                            updateSize(PrefSize.TWO_HUNDRED_BY_TWO_HUNDRED);
                        } else if (zeNode instanceof FlowPane) {
                            updateSize(PrefSize.THREE_HUNDRED_TWENTY_BY_TWO_HUNDRED_FIFTY_SIX);
                        }
                    } else {
                        double rule = 200.0;
                        // resize builtin in case too large or too tiny
                        // if (builtinPrefWidth > rule || builtinPrefHeight > rule ) {
                        if (builtinPrefWidth > builtinPrefHeight) {
                            double coef = builtinPrefWidth / rule;
                            builtinPrefWidth = rule;
                            builtinPrefHeight = builtinPrefHeight / coef;
                        } else {
                            double coef = builtinPrefHeight / rule;
                            builtinPrefHeight = rule;
                            builtinPrefWidth = builtinPrefWidth / coef;
                        }
                        // }

                        updateSize(PrefSize.DEFAULT);
                    }
                    break;
                }
                case FONT_ICONS:
                {
                    FontImage fontImage = filter.getOrCreateFontImage(t1.getReportEntry().getFontName());
                    Font bgFont = Font.loadFont(is, 62);

                    FlowPane flowbox = new FlowPane();
                    flowbox.setHgap(5);
                    flowbox.setVgap(5);

                    String selectedStyle =
                            "-fx-background-color: red, white;"
                            + "-fx-background-insets: 0, 1;"
                            + "-fx-background-radius: 5, 4;"
                            + "-fx-border-color: red, red;"
                            + "-fx-border-radius: 5, 4;";

                    String unselectedStyle = "-fx-background-color: grey, grey;"
                            + "-fx-background-insets: 0, 1;"
                            + "-fx-background-radius: 5, 4;"
                            + "-fx-border-color: grey, grey;"
                            + "-fx-border-radius: 5, 4;";

                    t1.getReportEntry().getUnicodePoints().forEach(unicodePoint -> {

                        FontImageItem fontImageItem = filter.getOrCreateFontImageItem(fontImage, unicodePoint);

                        System.out.println("\\u" + Integer.toHexString(unicodePoint));
                        String txt = Character.toString(unicodePoint);
                        VBox item = new VBox();
                        item.setStyle(unselectedStyle);
                        item.setAlignment(Pos.CENTER);

                        Text textCtrl = new Text(txt);
                        textCtrl.setFont(bgFont);

                        CheckBox selected = new CheckBox(ImageExplorerUtil.unicodePointToXmlEntity(unicodePoint));

                        boolean select = fontImageItem.isImported();
                        selected.setSelected(select);
                        item.setStyle(select ? selectedStyle : unselectedStyle);

                        selected.selectedProperty().addListener((ob, o , n) -> {
                            fontImageItem.setImported(n);
                            if (n) {
                                item.setStyle(selectedStyle);
                            } else {
                                item.setStyle(unselectedStyle);
                            }
                        });
                        item.getChildren().add(textCtrl);
                        item.getChildren().add(selected);
                        item.setPrefSize(80, 80);
                        flowbox.getChildren().add(item);
                    });
                    zeNode = flowbox;
                    break;
                }
                default:
                    break;
                }

                is.close();


            } catch (IOException ioe) {
                showErrorDialog(ioe);
            }


            updateZeNodeSize();
            previewGroup.getChildren().add(zeNode);
            defSizeChoice.setDisable(false);

            String name = t1.getReport().getSource().getFileName().toString();
            if (t1.getReportEntry().getResourceName() != null) {
                name += " > " + t1.getReportEntry().getResourceName();
            } else {
                name += " > " + t1.getReportEntry().getName();
            }
            classNameLabel.setText(name);//t1.getReportEntry().getKlass().getName());

        });

        // We avoid to get an empty Preview area at first.
        if (importList.getItems().size() > 0) {
            importList.getSelectionModel().selectFirst();
        }
    }


    private void refreshItems(ImportRow row) {


        final ImageReport report = row.getReport();
        final ImageReportEntry entry = row.getReportEntry();
        ImageReport ir = new ImageReport(report.getSource());
        ir.getEntries().add(entry);

        List<ImageReport> list = filter.filter(List.of(ir));
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
                if (url.toString().endsWith(".jar")) {
                    result[index] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/
                } else {
                    result[index] = url; // <-- file:/path/to/folder/
                }

                index++;
            }
        } catch (MalformedURLException x) {
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
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

    // NOTE At the end of the day some tooling in metadata will supersedes the
    // use of this method that is only able to deal with a Region, ignoring all
    // other cases.
    private void updateSize(PrefSize prefSize) {
        setFieldSize(prefSize);
        defSizeChoice.getSelectionModel().select(prefSize);
    }

    private void setFieldSize(PrefSize ps) {
        widthField.setEditable(false);
        heightField.setEditable(false);

        switch (ps) {
        case CUSTOM:
            widthField.setEditable(true);
            heightField.setEditable(true);
        case DEFAULT:
            widthField.setText(Integer.toString((int) builtinPrefWidth));
            heightField.setText(Integer.toString((int) builtinPrefHeight));
            customBox.setVisible(true);
            break;
        default:
            widthField.setText(Integer.toString((int) ps.getWidth()));
            heightField.setText(Integer.toString((int) ps.getHeight()));
            customBox.setVisible(false);
            break;
        }
    }

    private void updateZeNodeSize() {
        try {
            Double witdh = Double.valueOf(widthField.getText());
            Double height = Double.valueOf(heightField.getText());
            if (zeNode instanceof ImageView) {
                ((ImageView) zeNode).setFitWidth(witdh);
                ((ImageView) zeNode).setFitHeight(height);
            } else {
                ((Region) zeNode).setPrefSize(witdh, height);
                ((Region) zeNode).setMaxSize(witdh, height);
            }

        } catch (NumberFormatException e) {
            logger.warn("Unable to set the component size due to invalid format", e);
        }
    }

}
