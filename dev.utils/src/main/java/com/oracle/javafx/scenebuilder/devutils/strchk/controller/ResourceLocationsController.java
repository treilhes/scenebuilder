package com.oracle.javafx.scenebuilder.devutils.strchk.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.devutils.strchk.Config;
import com.oracle.javafx.scenebuilder.devutils.strchk.loader.ProjectLoader;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.utils.MatchFinder;

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

public class ResourceLocationsController {
    
    @FXML
    private Label numberOfValues;

    @FXML
    private Label numberOfOk;

    @FXML
    private Label numberOfErrors;

    @FXML
    private Label numberOfUndefined;
    
    @FXML
    private TableView<ResourceLocationItem> valuesTable;
    
    @FXML
    private TableColumn<ResourceLocationItem, String> projectColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> packageColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> fileColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> valueColumn;
    
    @FXML
    private TableColumn<ResourceLocationItem, String> matchProjectColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> matchPackageColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> matchFileColumn;

    @FXML
    private TableColumn<ResourceLocationItem, String> matchErrorColumn;
    
    @FXML
    private TableColumn<ResourceLocationItem, String> matchSolutionColumn;
    
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
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        
        matchProjectColumn.setCellValueFactory(new PropertyValueFactory<>("matchProjectName"));
        matchPackageColumn.setCellValueFactory(new PropertyValueFactory<>("matchPackageName"));
        matchFileColumn.setCellValueFactory(new PropertyValueFactory<>("matchFileName"));
        matchErrorColumn.setCellValueFactory(new PropertyValueFactory<>("matchError"));
        matchSolutionColumn.setCellValueFactory(new PropertyValueFactory<>("matchSolution"));
        
        addTooltipToColumnCells(projectColumn);
        addTooltipToColumnCells(packageColumn);
        addTooltipToColumnCells(fileColumn);
        addTooltipToColumnCells(valueColumn);
        addTooltipToColumnCells(matchProjectColumn);
        addTooltipToColumnCells(matchPackageColumn);
        addTooltipToColumnCells(matchFileColumn);
        addTooltipToColumnCells(matchErrorColumn);
        addTooltipToColumnCells(matchSolutionColumn);
        
        numberOfMatch.setText("");
        
        applyFilterCheckbox.setSelected(!Config.DISABLE_ALL_FILTERS);
        applyFilterCheckbox.selectedProperty().addListener((ob, o , n) -> Config.DISABLE_ALL_FILTERS = !n);
    }
    
    private <T> void addTooltipToColumnCells(TableColumn<ResourceLocationItem,T> column) {

        Callback<TableColumn<ResourceLocationItem, T>, TableCell<ResourceLocationItem,T>> existingCellFactory 
            = column.getCellFactory();

        column.setCellFactory(c -> {
            TableCell<ResourceLocationItem, T> cell = existingCellFactory.call(c);

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
        
        ObservableList<ResourceLocationItem> items = accumulateData(project);
        
        long accumul = System.currentTimeMillis();
        
        AtomicInteger idx = new AtomicInteger();
        items.forEach(it -> {
            long mstart = System.currentTimeMillis();
            MatchFinder.findMatch(project, it);
            long mend = System.currentTimeMillis();
            System.out.println(String.format("Match %s time %s ms : %s", idx.incrementAndGet() ,(mend - mstart), it.getValue()));
        });
            
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

    private ObservableList<ResourceLocationItem> accumulateData(Project project) {
        final ObservableList<ResourceLocationItem> datas = FXCollections.observableArrayList();
        
        project.getClasses().values().forEach(cls -> cls.getStringOccurences().forEach(str -> {
            datas.add(new ResourceLocationItem(project, cls, str));
        }));
        project.getResources().values().forEach(cls -> cls.getStringOccurences().forEach(str -> {
            datas.add(new ResourceLocationItem(project, cls, str));
        }));
        
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
            initialize(ProjectLoader.load(new File(".", Config.ROOT_PROJECT).getCanonicalFile()));
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
