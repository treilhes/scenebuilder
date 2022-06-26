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
package com.oracle.javafx.scenebuilder.controllibrary.panel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.dnd.DragSource;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.ViewSearch;
import com.oracle.javafx.scenebuilder.api.dock.annotation.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.api.ui.ViewMenuController;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.controllibrary.action.InsertControlAction;
import com.oracle.javafx.scenebuilder.controllibrary.controller.LibraryController;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.BuiltinSectionComparator;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.LibraryItemNameComparator;
import com.oracle.javafx.scenebuilder.controllibrary.preferences.global.DisplayModePreference;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMArchive;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.library.util.LibraryUtil;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * This class creates and controls the <b>Library Panel</b> of Scene Builder
 * Kit.
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ViewAttachment(name = LibraryPanelController.VIEW_NAME, id = LibraryPanelController.VIEW_ID, prefDockId = Dock.LEFT_DOCK_ID, openOnStart = true, selectOnStart = true,
        icon = "ViewIconControlLib.png", iconX2 = "ViewIconControlLib@2x.png")
public class LibraryPanelController extends AbstractFxmlViewController implements ControlLibraryPanel, InitWithDocument {

    public final static String VIEW_ID = "30acd1e6-a848-4ac6-95b2-0effa0b76932";
    public final static String VIEW_NAME = "view.name.control.library";

    private String searchPattern;
    ArrayList<LibraryItemImpl> searchData = new ArrayList<>();

    // The name of the library section to keep opened. This is used when e.g.
    // a user jar file is imported to the library directory.
    // If the user was doing a search then we let the library layout unchanged.
    // If the user wasn't doing a search and custom content is updated then
    // we want to get Custom section opened.
    String sectionNameToKeepOpened = null;
    boolean initiateImportDialog = false;
    final List<File> droppedFileList = new ArrayList<>();

    private boolean animateAccordion;

    @FXML
    private Accordion libAccordion;
    @FXML
    Label noSearchResults;
    @FXML
    ListView<LibraryListItem> libSearchList;

    @FXML
    ListView<LibraryListItem> libList = null;

    @FXML
    StackPane libPane;

    private final ControlLibrary userLibrary;
    private final DisplayModePreference displayModePreference;

    private final Dialog dialog;
    private final Drag drag;
    private final LibraryController libraryController;
    private FXOMDocument fxomDocument;
    private final Editor editorController;

    private final ViewSearch viewSearch;
    private final SceneBuilderBeanFactory sceneBuilderBeanFactory;
    private final ActionFactory actionFactory;

    /*
     * Public
     */

    /**
     * Creates a library panel controller for the specified editor controller.
     *
     * @param editor the editor controller (never null).
     */
    // TODO after verifying setLibrary is never reused in editorcontroller, must use
    // UserLibrary bean instead of libraryProperty
 // @formatter:off
    public LibraryPanelController(
            SceneBuilderManager scenebuilderManager,
            FxmlDocumentManager documentManager,
            Editor editor,
            Dialog dialog,
            Drag drag,
            DisplayModePreference displayModePreference,
            LibraryController libraryController,
            ViewSearch viewSearch,
            ControlLibrary controlLibrary,
            ViewMenuController viewMenuController,
            SceneBuilderBeanFactory sceneBuilderBeanFactory,
            ActionFactory actionFactory
            ) {
     // @formatter:on
        super(scenebuilderManager, documentManager, viewMenuController, LibraryPanelController.class.getResource("LibraryPanel.fxml"),
                I18N.getBundle());
        this.editorController = editor;
        this.dialog = dialog;
        this.drag = drag;
        this.userLibrary = controlLibrary;
        this.libraryController = libraryController;
        this.displayModePreference = displayModePreference;
        this.sceneBuilderBeanFactory = sceneBuilderBeanFactory;
        this.actionFactory = actionFactory;

        this.viewSearch = viewSearch;

        documentManager.fxomDocument().subscribe(fd -> fxomDocument = fd);

    }

    @FXML
    public void initialize() {
        setDisplayMode(displayModePreference.getValue());
        displayModePreference.getObservableValue().addListener((ob, o, n) -> setDisplayMode(n));
    }

    /**
     * Returns null or the search pattern applied to this library panel.
     *
     * @return null or the search pattern applied to this library panel.
     */
    public String getSearchPattern() {
        return searchPattern;
    }

    /**
     * Sets the search pattern to be applied to this library panel. When null value
     * is passed, the library panel displays all its items.
     *
     * @param searchPattern null or the search pattern to apply to this library
     *                      panel.
     */
    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern.toUpperCase(Locale.ENGLISH);
        searchPatternDidChange();
    }

    /**
     * @treatAsPrivate Perform the import of the selection
     * @param objects the FXOM objects to import to customize the Library content.
     */
    public void performImportSelection(List<FXOMObject> objects) {
        processInternalImport(objects);
    }

    public void animateAccordion(boolean animate) {
        this.animateAccordion = animate;
        libAccordion.getPanes().forEach(tp -> tp.setAnimated(animate));
    }

    /*
     * AbstractFxmlPanelController
     */

    /**
     * @treatAsPrivate Controller did load fxml.
     */
    @Override
    public void controllerDidLoadFxml() {
        assert libAccordion != null;
        assert libPane != null;
        assert libList != null;
        assert noSearchResults != null;
        assert libSearchList != null;

        getSearchController().textProperty()
                .addListener((ChangeListener<String>) (ov, oldStr, newStr) -> setSearchPattern(newStr));
        userLibrary.getItems().addListener(libraryItemListener);

        startListeningToDrop();
        populateLibraryPanel();

        nameProperty().bind(Bindings.createStringBinding(() -> {

            return userLibrary.exploringProperty().get() ? I18N.getString("library.exploring")
                    : I18N.getString("library");

        }, userLibrary.exploringProperty()));
    }

    private void displayModeDidChange(DISPLAY_MODE displayMode) {
        if (libAccordion != null) {
            switch (displayMode) {
            case SECTIONS:
                libAccordion.setVisible(true);
                libAccordion.setManaged(true);
                noSearchResults.setVisible(false);
                noSearchResults.setManaged(false);
                libSearchList.setVisible(false);
                libSearchList.setManaged(false);
                getLibList().setVisible(false);
                getLibList().setManaged(false);
                break;
            case SEARCH:
                libAccordion.setVisible(false);
                libAccordion.setManaged(false);
                if (libSearchList.getItems().isEmpty()) {
                    noSearchResults.setVisible(true);
                    noSearchResults.setManaged(true);
                    libSearchList.setVisible(false);
                    libSearchList.setManaged(false);
                } else {
                    noSearchResults.setVisible(false);
                    noSearchResults.setManaged(false);
                    libSearchList.setVisible(true);
                    libSearchList.setManaged(true);
                }
                getLibList().setVisible(false);
                getLibList().setManaged(false);
                break;
            case LIST:
                libAccordion.setVisible(false);
                libAccordion.setManaged(false);
                noSearchResults.setVisible(false);
                noSearchResults.setManaged(false);
                libSearchList.setVisible(false);
                libSearchList.setManaged(false);
                getLibList().setVisible(true);
                getLibList().setManaged(true);
                break;
            default:
                break;
            }
        }
    }

    /*
     * Private
     */

    /**
     * The display mode of the Library panel is used to choose how items are
     * rendered within the panel.
     */
    public enum DISPLAY_MODE {

        SECTIONS {

            @Override
            public String toString() {
                return I18N.getString("library.panel.menu.view.sections");
            }
        },
        SEARCH, LIST {

            @Override
            public String toString() {
                return I18N.getString("library.panel.menu.view.list");
            }
        }
    };

    private DISPLAY_MODE currentDisplayMode;
    private DISPLAY_MODE previousDisplayMode = DISPLAY_MODE.SECTIONS;

    public void setPreviousDisplayMode(DISPLAY_MODE displayMode) {
        this.previousDisplayMode = displayMode;
    }

    public void setDisplayMode(DISPLAY_MODE displayMode) {
        this.currentDisplayMode = displayMode;
        displayModeDidChange(displayMode);
    }

    public DISPLAY_MODE getDisplayMode() {
        return currentDisplayMode;
    }

    final ListChangeListener<LibraryItem> libraryItemListener = change -> libraryDidChange();

    void libraryDidChange() {
        if (libAccordion != null) {
            // Clear the content of the panel.
            libAccordion.getPanes().clear();

            // Reconstruct the panel content based on the new Library.
            populateLibraryPanel();
        }
    }

    private String getExpandedSectionName() {
        String sectionName = null;

        if (libAccordion != null && libAccordion.getExpandedPane() != null) {
            sectionName = libAccordion.getExpandedPane().getText();
        }

        return sectionName;
    }

    // We need to discover the sections and the content of each one before being
    // able to populate the panel.
    // Each section is a TitledPane that contains a ListView of LibraryItem.
    // The order section are listed is managed by the comparator that comes with
    // the Library. In each section the LibraryItem are sorted with alphabetical
    // ordering.
    // First TitledPane is expanded.
    private void populateLibraryPanel() {
        final Callback<ListView<LibraryListItem>, ListCell<LibraryListItem>> cb = param -> sceneBuilderBeanFactory.getBean(LibraryListCell.class);

        // libData is backend structure for all that we put in the Accordion.
        LinkedHashMap<String, ArrayList<LibraryItemImpl>> libData = new LinkedHashMap<>();
        TreeSet<String> sectionNames = new TreeSet<>(new BuiltinSectionComparator());
        List<TitledPane> panes = libAccordion.getPanes();

        searchData.clear();
        getLibList().getItems().clear();

        if (userLibrary.getItems().size() > 0) {
            // Construct a sorted set of all lib section names.
            for (LibraryItem item : userLibrary.getItems()) {
                sectionNames.add(item.getSection());
            }

            // Create a sorted set of lib elements for each section.
            for (String sectionName : sectionNames) {
                libData.put(sectionName, new ArrayList<>());
            }

            // Add each LibraryItem to the appropriate set.
            for (LibraryItemImpl item : userLibrary.getItems()) {
                libData.get(item.getSection()).add(item);
            }

            // Parse our lib data structure and populate the Accordion accordingly.
            for (String sectionName : sectionNames) {
                ListView<LibraryListItem> itemsList = new ListView<>();
                itemsList.setId(sectionName + "List"); // for QE //NOCHECK
                itemsList.setCellFactory(cb);
                itemsList.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
                Collections.sort(libData.get(sectionName), new LibraryItemNameComparator());
                for (LibraryItemImpl item : libData.get(sectionName)) {
                    itemsList.getItems().add(new LibraryListItem(item));
                }
                TitledPane sectionPane = new TitledPane(sectionName, itemsList);
                sectionPane.setId(sectionName); // for QE
                sectionPane.setAnimated(true);
                panes.add(sectionPane);

                searchData.addAll(libData.get(sectionName));

                getLibList().getItems().add(new LibraryListItem(sectionName));
                for (LibraryItemImpl item : libData.get(sectionName)) {
                    getLibList().getItems().add(new LibraryListItem(item));
                }
            }

            if (libAccordion.getPanes().size() >= 1) {
                expandPaneWithName(sectionNameToKeepOpened);
            }

            if (libSearchList.getCellFactory() == null) {
                libSearchList.setCellFactory(cb);
            }

            if (getLibList().getCellFactory() == null) {
                getLibList().setCellFactory(cb);
            }

            libSearchList.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
            getLibList().addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
        }

        // Update animation for all TitledPane
        libAccordion.getPanes().forEach(tp -> tp.setAnimated(animateAccordion));
    }

    private void expandPaneWithName(String paneName) {
        String sectionName = paneName;

        if (sectionName == null) {
            sectionName = DefaultSectionNames.TAG_CONTAINERS;
        }

        for (TitledPane tp : libAccordion.getPanes()) {
            if (tp.getText().equals(sectionName)) {
//                System.out.println("expandPaneWithName - Expand section " + sectionName);
                libAccordion.setExpandedPane(tp);
            }
        }
    }

    private void searchPatternDidChange() {
        if (searchPattern == null || searchPattern.isEmpty()) {
            currentDisplayMode = previousDisplayMode;
        } else {
            if (currentDisplayMode != DISPLAY_MODE.SEARCH) {
                previousDisplayMode = currentDisplayMode;
                currentDisplayMode = DISPLAY_MODE.SEARCH;
            }
        }

        // The filtering is done by ignoring case, and by retaining any item that
        // contains the given pattern. An opened question is to filter as soon as
        // the pattern is two or more characters long: for now we react from the
        // first character.
        //
        // It can occur the whole Library is changed under the foots of SceneBuilder
        // while filtering is on going. searchData is never null so if its
        // content is changing then the filtering result will be inacurrate:
        // that is acceptable.
        //
        if (currentDisplayMode.equals(DISPLAY_MODE.SEARCH)) {
            libSearchList.getItems().clear();

            SearchPredicate searchPredicate = new SearchPredicate(searchPattern);

            final ArrayList<LibraryItemImpl> rawFilteredItem = new ArrayList<>();
            for (LibraryItemImpl item : searchData) {
                if (searchPredicate.test(item.getName().toUpperCase(Locale.ROOT))) {
                    rawFilteredItem.add(item);
                }
            }
            Collections.sort(rawFilteredItem, new LibraryItemNameComparator());
            for (LibraryItemImpl item : rawFilteredItem) {
                libSearchList.getItems().add(new LibraryListItem(item));
            }
            rawFilteredItem.clear();
        }

        setDisplayMode(currentDisplayMode);
    }

    private class SearchPredicate implements Predicate<String>{
        private List<List<String>> orAndAnds = new ArrayList<>();

        SearchPredicate(String search){
            String[] ors = search.split("\\|");
            for (String or:ors) {
                String[] ands = or.split("&");
                orAndAnds.add(Arrays.asList(ands));
            }
        }

        @Override
        public boolean test(String t) {
            for (List<String> ands:orAndAnds) {
                boolean match = ands.stream()
                        .map(s -> {
                            if (s.startsWith("!")) {
                                return !t.contains(s.substring(1));
                            } else {
                                return t.contains(s);
                            }
                        })
                        .allMatch(Boolean.TRUE::equals);

                if (match) {
                    return true;
                }
            }
            return false;
        }

    }
    // Key events listened onto the ListView
    // For some reason the listener when set on the cell (see LibraryListCell)
    // is never called, probably because it is the ListView which has the focus.
    private final EventHandler<KeyEvent> keyEventHandler = e -> handleKeyEvent(e);

    private void handleKeyEvent(KeyEvent e) {
        // On ENTER we try to insert the item which is selected within the Library.
        if (e.getCode() == KeyCode.ENTER) {
            // This way of doing things requires the use of an
            // @SuppressWarnings("unchecked")
//            final LibraryItem item = ((ListView<LibraryItem>)e.getSource()).getSelectionModel().getSelectedItem();
            // hence this other way below ...
            Object source = e.getSource();
            assert source instanceof ListView;
            final ListView<?> list = (ListView<?>) source;
            Object rawItem = list.getSelectionModel().getSelectedItem();
            assert rawItem instanceof LibraryListItem;
            final LibraryListItem listitem = (LibraryListItem) rawItem;
            final LibraryItem item = listitem.getLibItem();

            InsertControlAction action = actionFactory.create(InsertControlAction.class);
            action.setLibraryItem(item);
            action.checkAndPerform();


            e.consume();
        }
    }

    private void startListeningToDrop() {

        libPane.setOnDragDropped(t -> {
//                System.out.println("libPane onDragDropped");
            DragSource dragSource = drag.getDragSource();
            // TODO DocumentDragSource became DragSource, what about LibraryDragSource and
            // ExternalDragSource
            if (dragSource instanceof DragSource) { // instanceof DocumentDragSource
                processInternalImport(dragSource.getDraggedObjects());
            } else {
                initiateImportDialog = false;
                droppedFileList.clear();
                t.setDropCompleted(true);
                // Drop gesture is only valid when the Library is an instance of UserLibrary
                if (userLibrary instanceof ControlLibrary) {
                    Dragboard db = t.getDragboard();
                    if (db.hasFiles()) {
                        final List<File> files = db.getFiles();
                        for (File file : files) {
                            // Keep only jar and fxml files
                            if (file.isFile() && LibraryUtil.hasExtension(file.toPath(),
                                    ControlLibrary.HANDLED_FILE_EXTENSIONS)) { // NOCHECK
//                                System.out.println("libPane onDragDropped - Retaining file " + file.getName());
                                droppedFileList.add(file);
                            }
                        }

                        // The import dialog might be kept opened by the user quite
                        // a long time.
                        // On Mac (not on Win), after around 20 seconds of opening
                        // time of the import dialog window the user sees a move from
                        // the lib panel to the Finder of the file icon, as if the drag
                        // is rejected.
                        // In order to silence (mask ?) this issue there's:
                        // - the delegation to setOnDragExited of the call of processImportJarFxml
                        // so that current handler returns fast.
                        // - the runLater in setOnDragExited, wrapped with a Timer set with a 1 second
                        // delay
                        // Is there a way to be notified when the import dialog
                        // can be run without interfering with the drag and drop sequence ?
                        initiateImportDialog = true;
                    }
                }
            }
        });

        libPane.setOnDragExited(t -> {
//                System.out.println("libPane onDragExited");
            if (initiateImportDialog) {
                initiateImportDialog = false;
                final Timer timer = new Timer(true);
                final TimerTask timerTask = new TimerTask() {

                    @Override
                    public void run() {
                        SbPlatform.runLater(() -> processImportJarFxml(droppedFileList));
                        // I don't need to use the timer later on so by
                        // cancelling it right here I'm sure free resources
                        // that otherwise would prevent the JVM from exiting.
                        timer.cancel();
                    }
                };
                timer.schedule(timerTask, 600); // milliseconds
            }
        });

        libPane.setOnDragOver(t -> {
//                System.out.println("libPane onDragOver");
            DragSource dragSource = drag.getDragSource();
            Dragboard db = t.getDragboard();
            // db has file when dragging a file from native file manager (Mac Finder,
            // Windows Explorer, ...).
            // dragSource is not null if the user drags something from Hierarchy or Content
            // panel.
            if (db.hasFiles() || dragSource != null) {
                t.acceptTransferModes(TransferMode.COPY);
            }
        });

        // This one is called only if lib is the source of the drop.
        libPane.setOnDragDone(t -> {
            assert drag.getDragSource() != null;
            drag.end();
            t.getDragboard().clear();
            t.consume();
        });

    }

    // An internal import is an import to the Library initiated from within
    // SceneBuilder (from Content or Hierarchy).
    // We stop the watching thread to avoid potential parsing of a file that
    // would not yet be properly finalized on disk.
    private void processInternalImport(List<FXOMObject> objects) {
        sectionNameToKeepOpened = getExpandedSectionName();
        boolean hasDependencies = false;
        // Selection can be multiple: as soon as one has dependencies
        // we won't import anything.
        // Copy of the dependencies remains something to do (DTL-5879).
        for (FXOMObject asset : objects) {
            if (FXMLUtils.hasDependencies(asset)) {
                hasDependencies = true;
                break;
            }
        }

        if (hasDependencies) {
            userLibraryUpdateRejected();
        } else {
            userLibrary.stopWatching();

            try {
                // The selection can be multiple, in which case each asset is
                // processed separately.
                for (FXOMObject asset : objects) {
                    // Create an FXML layout as a String
                    ArrayList<FXOMObject> selection = new ArrayList<>();
                    selection.add(asset);
                    final FXOMArchive fxomArchive = new FXOMArchive(selection);
                    final FXOMArchive.Entry entry0 = fxomArchive.getEntries().get(0);
                    String fxmlText = entry0.getFxmlText();

                    // Write the FXML layout into a dedicated file stored in the user Library dir.
                    // We use the tag name of the top element and append a number:
                    // if Library dir already contains SplitPane_1.fxml and top element has
                    // tag name SplitPane then we will create SplitPane_2.fxml and so on.
                    // Note the tag name is most of the time identical to the Java class name, see
                    // DTL-6643.
                    String prefix = asset.getGlueElement().getTagName();
                    File fxmlFile = getUniqueFxmlFileName(prefix, userLibrary.getStore().getFilesFolder().toFile());
                    writeFxmlFile(fxmlFile, fxmlText, userLibrary.getStore().getFilesFolder());
                }
            } finally {
                if (currentDisplayMode.equals(DISPLAY_MODE.SECTIONS)) {
                    sectionNameToKeepOpened = DefaultSectionNames.TAG_USER_DEFINED;
                }

                userLibrary.startWatching();
            }
        }
    }

    private void writeFxmlFile(File targetFile, String text, Path libPath) {
        Path targetFilePath = Paths.get(targetFile.getPath());

        try {
            // Create the new file
            Files.createFile(targetFilePath);

            // Write content of file
            try (PrintWriter writer = new PrintWriter(targetFile, "UTF-8")) { // NOCHECK
                writer.write(text);
            }
        } catch (IOException ioe) {
            dialog.showErrorAndWait(I18N.getString("error.file.create.title"),
                    I18N.getString("error.file.create.message", targetFilePath.normalize().toString()),
                    I18N.getString("error.write.details"), ioe);
        }
    }

    private File getUniqueFxmlFileName(String prefix, File libDir) {
        int suffix = 0;
        File file = null;
        while (file == null || file.exists()) {
            suffix++;
            file = new File(libDir, prefix + "_" + suffix + ".fxml"); // NOCHECK
        }

        return file;
    }

    private void processImportJarFxml(List<File> importedFiles) {
        if (importedFiles != null && !importedFiles.isEmpty()) {
            sectionNameToKeepOpened = getExpandedSectionName();

            if (hasDependencies(importedFiles)) {
                return;
            }

            List<Path> files = importedFiles.stream().map(f -> f.toPath()).collect(Collectors.toList());
            userLibrary.performAddFilesOrFolders(files);

            if (currentDisplayMode.equals(DISPLAY_MODE.SECTIONS)) {
                sectionNameToKeepOpened = DefaultSectionNames.TAG_USER_DEFINED;
            }
        }
    }

    private void userLibraryUpdateRejected() {
        dialog.showAlertAndWait(I18N.getString("alert.import.reject.dependencies.title"),
                I18N.getString("alert.import.reject.dependencies.message"),
                I18N.getString("alert.import.reject.dependencies.details"));
    }

    private boolean hasDependencies(List<File> fxmlFiles) {
        boolean hasDependencies = false;
        boolean scanWentWell = true;

        for (File fxmlFile : fxmlFiles) {
            if (!LibraryUtil.isFxmlPath(fxmlFile.toPath())) {
                continue;
            }

            try {
                if (FXMLUtils.fxmlHasDependencies(fxmlFile, fxomDocument.getClassLoader(),
                        fxomDocument.getResources())) {
                    hasDependencies = true;
                    break;
                }
            } catch (IOException ioe) {
                scanWentWell = false;
                hasDependencies = true; // not sure but better take no risk
                dialog.showErrorAndWait(I18N.getString("error.import.reject.dependencies.scan.title"),
                        I18N.getString("error.import.reject.dependencies.scan.message"),
                        I18N.getString("error.import.reject.dependencies.scan.details"), ioe);
            }
        }

        if (hasDependencies && scanWentWell) {
            userLibraryUpdateRejected();
        }

        return hasDependencies;
    }

    private ListView<LibraryListItem> getLibList() {
        if (libList == null) {
            libList = new ListView<>();
        }

        return libList;
    }

    public Editor getEditorController() {
        return editorController;
    }

    @Override
    public ViewSearch getSearchController() {
        return viewSearch;
    }

    @Override
    public void onShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initWithDocument() {
        // TODO this do not work, why?
        getSearchController().requestFocus();
    }

}
