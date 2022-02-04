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
package com.oracle.javafx.scenebuilder.devutils.cmpchk.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.devutils.CommonConfig;
import com.oracle.javafx.scenebuilder.devutils.cmpchk.loader.ProjectLoader;
import com.oracle.javafx.scenebuilder.devutils.model.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class ComponentDeclarationsController {

    @FXML
    private Label numberOfValues;

    @FXML
    private Label numberOfOk;

    @FXML
    private Label numberOfErrors;

    @FXML
    private Label numberOfUndefined;

    @FXML
    private TableView<ComponentItem> valuesTable;

    @FXML
    private TableColumn<ComponentItem, String> projectColumn;

    @FXML
    private TableColumn<ComponentItem, String> packageColumn;

    @FXML
    private TableColumn<ComponentItem, String> fileColumn;

    @FXML
    private TableColumn<ComponentItem, String> classColumn;

    @FXML
    private TableColumn<ComponentItem, String> matchProjectColumn;

    @FXML
    private TableColumn<ComponentItem, String> matchPackageColumn;

    @FXML
    private TableColumn<ComponentItem, String> matchFileColumn;

    @FXML
    private TableColumn<ComponentItem, String> matchErrorColumn;

    @FXML
    private TableColumn<ComponentItem, String> matchSolutionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Label numberOfMatch;

    @FXML
    private CheckBox applyFilterCheckbox;

    private String lastSearch;

    private int lastIndex;

    @FXML
    public void initialize() {
        valuesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE );

        projectColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        packageColumn.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        fileColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        matchProjectColumn.setCellValueFactory(new PropertyValueFactory<>("matchProjectName"));
        matchPackageColumn.setCellValueFactory(new PropertyValueFactory<>("matchPackageName"));
        matchFileColumn.setCellValueFactory(new PropertyValueFactory<>("matchFileName"));
        matchErrorColumn.setCellValueFactory(new PropertyValueFactory<>("matchError"));
        matchSolutionColumn.setCellValueFactory(new PropertyValueFactory<>("matchSolution"));

        addTooltipToColumnCells(projectColumn);
        addTooltipToColumnCells(packageColumn);
        addTooltipToColumnCells(fileColumn);
        addTooltipToColumnCells(classColumn);
        addTooltipToColumnCells(matchProjectColumn);
        addTooltipToColumnCells(matchPackageColumn);
        addTooltipToColumnCells(matchFileColumn);
        addTooltipToColumnCells(matchErrorColumn);
        addTooltipToColumnCells(matchSolutionColumn);

        numberOfMatch.setText("");

        applyFilterCheckbox.setSelected(!Config.DISABLE_ALL_FILTERS);
        applyFilterCheckbox.selectedProperty().addListener((ob, o , n) -> Config.DISABLE_ALL_FILTERS = !n);
    }

    private <T> void addTooltipToColumnCells(TableColumn<ComponentItem,T> column) {

        Callback<TableColumn<ComponentItem, T>, TableCell<ComponentItem,T>> existingCellFactory
            = column.getCellFactory();

        column.setCellFactory(c -> {
            TableCell<ComponentItem, T> cell = existingCellFactory.call(c);

            Tooltip tooltip = new Tooltip();
            // can use arbitrary binding here to make text depend on cell
            // in any way you need:
            tooltip.textProperty().bind(cell.itemProperty().asString());

            cell.setTooltip(tooltip);
            return cell ;
        });
    }

    public void initialize(Project project) {
        System.out.println(new Date());

        long start = System.currentTimeMillis();

        ObservableList<ComponentItem> items = accumulateData(project);

        long accumul = System.currentTimeMillis();

        AtomicInteger idx = new AtomicInteger();
        items.forEach(it -> {
            long mstart = System.currentTimeMillis();
            MatchFinder.findMatch(project, it);
            long mend = System.currentTimeMillis();
            System.out.println(String.format("Match %s time %s ms", idx.incrementAndGet() ,(mend - mstart)));
        });

        Comparator<ComponentItem> comparator = Comparator
                .comparing(ComponentItem::getProjectName)
                .thenComparing(ComponentItem::getClassName);

        Collections.sort(items, comparator);

        long match = System.currentTimeMillis();

        long numOk = items.stream().filter(i -> "OK".equals(i.getMatchError())).count();
        long numKo = items.stream().filter(i -> !"OK".equals(i.getMatchError()) && !i.getMatchError().isEmpty()).count();
        long numUndefined = items.stream().filter(i -> i.getMatchError().isEmpty()).count();

        long loaded = System.currentTimeMillis();

        System.out.println(String.format("accumul time %s ms", (accumul - start)));
        System.out.println(String.format("match time %s ms", (match - accumul)));
        System.out.println(String.format("Count time %s ms", (loaded - match)));
        System.out.println(String.format("Load time %s ms", (loaded - start)));

        numberOfValues.setText("" + items.size());
        numberOfOk.setText("" + numOk);
        numberOfErrors.setText("" + numKo);
        numberOfUndefined.setText("" + numUndefined);

        valuesTable.setItems(items);
    }

    private ObservableList<ComponentItem> accumulateData(Project project) {
        final ObservableList<ComponentItem> datas = FXCollections.observableArrayList();

        project.getClasses().values().stream()
        .filter(v -> v != null)
        .flatMap(v -> v.stream())
        .filter(cls -> cls.isComponent())
        .forEach(cls -> datas.add(new ComponentItem(project, cls)));


        project.getSubProjects().forEach(sp -> {
            datas.addAll(accumulateData(sp));
        });
        return datas;
    }

    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void update(ActionEvent event) {
        try {
            System.out.println(new Date());
            initialize(ProjectLoader.load(new File(".", CommonConfig.ROOT_PROJECT).getCanonicalFile()));
            System.out.println(new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copy(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (valuesTable.getSelectionModel().isEmpty()) {
            content.put(DataFormat.PLAIN_TEXT, valuesTable.getItems().stream()
                    .map(i -> i.toString()).collect(Collectors.joining("\n")));
        } else {
            content.put(DataFormat.PLAIN_TEXT,
                    valuesTable.getSelectionModel().getSelectedItems().stream()
                    .map(i -> i.toString()).collect(Collectors.joining("\n")));
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    private void copyToPaste(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (valuesTable.getSelectionModel().isEmpty()) {
            content.put(DataFormat.PLAIN_TEXT, valuesTable.getItems().stream()
                    .map(i -> i.getClassName() + ".class").collect(Collectors.joining(",\n")));
        } else {
            content.put(DataFormat.PLAIN_TEXT,
                    valuesTable.getSelectionModel().getSelectedItems().stream()
                    .map(i -> i.getClassName() + ".class").collect(Collectors.joining(",\n")));
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !searchField.getText().isEmpty()) {

            if (!searchField.getText().equals(lastSearch)) {
                lastIndex++;
            } else {
                lastSearch=searchField.getText();
                lastIndex=0;
            }

            valuesTable.getSelectionModel().clearSelection();
            valuesTable.getItems().stream()
            .forEach(i -> {
                String s = i.toString();
                if (s != null && s.contains(searchField.getText())) {
                    valuesTable.getSelectionModel().select(i);
                }
            });
            ObservableList<Integer> indices = valuesTable.getSelectionModel().getSelectedIndices();
            if (indices != null && !indices.isEmpty()) {
                if (lastIndex >= indices.size()) {
                    lastIndex = 0;
                }
                valuesTable.scrollTo(indices.get(lastIndex));

                numberOfMatch.setText("" + indices.size());
            }
        }
    }
}
