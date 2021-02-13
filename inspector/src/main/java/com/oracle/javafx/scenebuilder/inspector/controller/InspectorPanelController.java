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
package com.oracle.javafx.scenebuilder.inspector.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Inspector;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertiesEditor;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor.LayoutFormat;
import com.oracle.javafx.scenebuilder.core.editors.CssPropAuthorInfo;
import com.oracle.javafx.scenebuilder.core.editors.FxIdEditor;
import com.oracle.javafx.scenebuilder.core.editors.PropertyEditor;
import com.oracle.javafx.scenebuilder.core.editors.PropertyEditorFactory;
import com.oracle.javafx.scenebuilder.core.editors.PropertyEditorFactory.PropertyEditorFactorySession;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.util.ValuePropertyMetadataClassComparator;
import com.oracle.javafx.scenebuilder.core.metadata.util.ValuePropertyMetadataNameComparator;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.core.util.Deprecation;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.editors.control.GenericEditor;
import com.oracle.javafx.scenebuilder.editors.control.ToggleGroupEditor;
import com.oracle.javafx.scenebuilder.inspector.preferences.document.InspectorSectionIdPreference;
import com.oracle.javafx.scenebuilder.job.editor.ModifyCacheHintJob;
import com.oracle.javafx.scenebuilder.job.editor.ModifySelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxIdJob;
import com.oracle.javafx.scenebuilder.job.editor.togglegroup.ModifySelectionToggleGroupJob;
import com.oracle.javafx.scenebuilder.kit.util.CssInternal;
import com.oracle.javafx.scenebuilder.sb.preferences.global.AccordionAnimationPreference;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.Style;
import javafx.css.StyleableProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class InspectorPanelController extends AbstractFxmlViewController implements Inspector {

    @FXML
    private TitledPane propertiesTitledPane;
    @FXML
    private ScrollPane propertiesScroll;
    @FXML
    private GridPane propertiesSection;
    @FXML
    private TitledPane layoutTitledPane;
    @FXML
    private ScrollPane layoutScroll;
    @FXML
    private GridPane layoutSection;
    @FXML
    private TitledPane codeTitledPane;
    @FXML
    private ScrollPane codeScroll;
    @FXML
    private GridPane codeSection;
    @FXML
    private TitledPane allTitledPane;
    @FXML
    private ScrollPane allScroll;
    @FXML
    private GridPane allContent;
    @FXML
    private StackPane searchStackPane;
    @FXML
    private GridPane searchContent;
    @FXML
    private Accordion accordion;
    @FXML
    private SplitPane inspectorRoot;

    public enum SectionId {

        PROPERTIES,
        LAYOUT,
        CODE,
        NONE
    }

    public enum ViewMode {

        SECTION, // View properties by section (default)
        PROPERTY_NAME, // Flat view of all properties, ordered by name
        PROPERTY_TYPE  // Flat view of all properties, ordered by type
    }

    public enum ShowMode {

        ALL, // Show all the properties (default)
        EDITED // Show only the properties which have been set in the FXML
    }
    //
    private static final String fxmlFile = "Inspector.fxml"; //NOI18N
    private static final String FXID_SUBSECTION_NAME = "Identity";
    private String searchPattern;
    private SectionId previousExpandedSection;
    private PropertyEditor lastPropertyEditorValueChanged = null;
    private boolean dragOnGoing = false;

    // ...
    //
    // Subsection title pool
    private final Stack<SubSectionTitle> subSectionTitlePool = new Stack<>();
    //
    // SubSectionTitles currently in use
    private final List<SubSectionTitle> subSectionTitlesInUse = new ArrayList<>();
    //
    private final SectionId[] sections = {SectionId.PROPERTIES, SectionId.LAYOUT, SectionId.CODE};
    //
    // State variables
    private final ObjectProperty<ViewMode> viewModeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ShowMode> showModeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<SectionId> expandedSectionProperty = new SimpleObjectProperty<>();

    // Inspector state
    private SelectionState selectionState;
    private double searchResultDividerPosition;

    // Charsets for the properties of included elements
//    private Map<String, Charset> availableCharsets;



    private RadioMenuItem showAll;
    private RadioMenuItem showEdited;
    private SeparatorMenuItem separator;
    private RadioMenuItem viewAsSections;
    private RadioMenuItem viewByPropName;
    private RadioMenuItem viewByPropType;

    private Action showAllAction;
	private Action showEditedAction;
	private Action viewBySectionsAction;
	private Action viewByPropertyNameAction;
	private Action viewByPropertyTypeAction;

	private final Editor editorController;
    private final SceneBuilderBeanFactory sceneBuilderFactory;
    private final InspectorSectionIdPreference inspectorSectionIdPreference;
    private final AccordionAnimationPreference accordionAnimationPreference;
	private final ApplicationContext context;
	private final DocumentManager documentManager;
	private final FileSystem fileSystem;

    private PropertyEditorFactorySession session;
    /*
     * Public
     */
    public InspectorPanelController(
    		@Autowired Api api,
    		@Autowired Editor editorController,
    		@Autowired InspectorSectionIdPreference inspectorSectionIdPreference,
    		@Autowired SceneBuilderBeanFactory sceneBuilderFactory,
    		@Autowired PropertyEditorFactory propertyEditorFactory,
    		@Autowired AccordionAnimationPreference accordionAnimationPreference,
    		@Autowired @Qualifier("inspectorPanelActions.ShowAllAction") Action showAllAction,
    		@Autowired @Qualifier("inspectorPanelActions.ShowEditedAction") Action showEditedAction,
    		@Autowired @Qualifier("inspectorPanelActions.ViewBySectionsAction") Action viewBySectionsAction,
    		@Autowired @Qualifier("inspectorPanelActions.ViewByPropertyNameAction") Action viewByPropertyNameAction,
    		@Autowired @Qualifier("inspectorPanelActions.ViewByPropertyTypeAction") Action viewByPropertyTypeAction
    		) {
        super(api, InspectorPanelController.class.getResource(fxmlFile), I18N.getBundle());
        this.context = api.getContext();
        this.fileSystem = api.getFileSystem();
        this.editorController = editorController;
        this.documentManager = api.getApiDoc().getDocumentManager();
        this.session = propertyEditorFactory.newSession();
//        this.availableCharsets = CharsetEditor.getStandardCharsets();
        this.sceneBuilderFactory = sceneBuilderFactory;
        this.inspectorSectionIdPreference = inspectorSectionIdPreference;
        this.accordionAnimationPreference = accordionAnimationPreference;

        this.showAllAction = showAllAction;
    	this.showEditedAction = showEditedAction;
    	this.viewBySectionsAction = viewBySectionsAction;
    	this.viewByPropertyNameAction = viewByPropertyNameAction;
    	this.viewByPropertyTypeAction = viewByPropertyTypeAction;

        viewModeProperty.setValue(ViewMode.SECTION);
        viewModeProperty.addListener((obv, previousMode, mode) -> viewModeChanged(previousMode, mode));

        showModeProperty.setValue(ShowMode.ALL);
        showModeProperty.addListener((obv, previousMode, mode) -> showModeChanged());

        expandedSectionProperty.setValue(SectionId.PROPERTIES);
        expandedSectionProperty.addListener((obv, previousSectionId, sectionId) -> expandedSectionChanged());
        
        api.getApiDoc().getDocumentManager().fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));
        api.getApiDoc().getDocumentManager().sceneGraphRevisionDidChange().subscribe(c -> sceneGraphRevisionDidChange());
        api.getApiDoc().getDocumentManager().cssRevisionDidChange().subscribeOn(JavaFxScheduler.platform())
        .subscribe(c -> cssRevisionDidChange());
        
        api.getApiDoc().getDocumentManager().selectionDidChange().subscribe(c -> editorSelectionDidChange());

    }

    @FXML
    protected void initialize() {
    	createLibraryMenu();

    	// init preferences
    	animateAccordion(accordionAnimationPreference.getValue());
    	accordionAnimationPreference.getObservableValue().addListener(
    			(ob, o, n) -> animateAccordion(n));

    	// Add inspector accordion expanded pane listener
    	setExpandedSection(inspectorSectionIdPreference.getValue());
    	inspectorSectionIdPreference.getObservableValue().addListener(
    			(ob, o, n) -> setExpandedSection(n));
    	accordion.expandedPaneProperty().addListener(
    			(ov, t, t1) -> inspectorSectionIdPreference.setValue(getExpandedSectionId()));
    }

    public Accordion getAccordion() {
        return accordion;
    }

    public SectionId getExpandedSectionId() {
        if (!isInspectorLoaded()) {
            return null;
        }
        final TitledPane expandedSection = accordion.getExpandedPane();
        final InspectorPanelController.SectionId result;

        if (expandedSection == null) {
            // all sections are collapsed
            result = InspectorPanelController.SectionId.NONE;
        } else if (expandedSection == propertiesTitledPane) {
            result = InspectorPanelController.SectionId.PROPERTIES;
        } else if (expandedSection == layoutTitledPane) {
            result = InspectorPanelController.SectionId.LAYOUT;
        } else if (expandedSection == codeTitledPane) {
            result = InspectorPanelController.SectionId.CODE;
        } else {
            // may happen if the view mode has been changed
            return null;
        }

        return result;
    }

    public ViewMode getViewMode() {
        return viewModeProperty.getValue();
    }

    public void setViewMode(ViewMode mode) {
        assert mode != null;
        viewModeProperty.setValue(mode);
    }

    private void viewModeChanged(ViewMode previousMode, ViewMode mode) {
        if (!isInspectorLoaded()) {
            return;
        }
        if (previousMode == ViewMode.SECTION) {
            previousExpandedSection = getExpandedSectionId();
        }
        accordion.getPanes().clear();
        switch (mode) {
            case SECTION:
                accordion.getPanes().addAll(propertiesTitledPane, layoutTitledPane, codeTitledPane);
                if (previousExpandedSection != null) {
                    setExpandedSection(previousExpandedSection);
                }
                break;
            case PROPERTY_NAME:
            case PROPERTY_TYPE:
                accordion.getPanes().add(allTitledPane);
                allTitledPane.setExpanded(true);
                rebuild();
                break;
            default:
                throw new IllegalStateException("Unexpected view mode " + mode); //NOI18N
        }
        updateClassNameInSectionTitles();
    }

    public ShowMode getShowMode() {
        return showModeProperty.getValue();
    }

    public void setShowMode(ShowMode mode) {
        assert mode != null;
        showModeProperty.setValue(mode);
    }

    private void showModeChanged() {
        if (!isInspectorLoaded()) {
            return;
        }
        rebuild();
    }

    public SectionId getExpandedSection() {
        return expandedSectionProperty.getValue();
    }

    public void setExpandedSection(SectionId sectionId) {
        assert sectionId != null;
        expandedSectionProperty.setValue(sectionId);
    }

    private void expandedSectionChanged() {
        if (!isInspectorLoaded()) {
            return;
        }
        final TitledPane tp;

        switch (getExpandedSection()) {
            case NONE:
                tp = null;
                break;
            case PROPERTIES:
                tp = propertiesTitledPane;
                break;
            case LAYOUT:
                tp = layoutTitledPane;
                break;
            case CODE:
                tp = codeTitledPane;
                break;
            default:
                throw new IllegalStateException("Unexpected section id " + getExpandedSection()); //NOI18N
            }

        accordion.setExpandedPane(tp);
    }

    public boolean isEditedMode() {
        return getShowMode() == ShowMode.EDITED;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
        searchPatternDidChange();
    }

    public void animateAccordion(boolean animate) {
        accordion.getPanes().forEach(tp -> tp.setAnimated(animate));
    }

    protected void fxomDocumentDidChange(FXOMDocument oldDocument) {
//        System.out.println("FXOM Document changed : " + getEditorController().getFxomDocument());
        if (isInspectorLoaded() && hasFxomDocument()) {
            selectionState.initialize();
            rebuild();
        }
    }

    protected void sceneGraphRevisionDidChange() {
//        System.out.println("Scene graph changed.");
        if (!dragOnGoing) {
            updateInspector();
        }
    }

    protected void cssRevisionDidChange() {
//        System.out.println("CSS changed.");
        Platform.runLater(() -> {
            if (!dragOnGoing) {
                updateInspector();
            }
        });
    }

    protected void editorSelectionDidChange() {
//        System.out.println("Selection changed.");
        // DTL-6570 should be resolved before this assertion is back.
//        assert !editorController.isTextEditingSessionOnGoing();
        if (!dragOnGoing) {
            updateInspector();
        }
    }

    /*
     * AbstractFxmlPanelController
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert propertiesTitledPane != null;
        assert propertiesScroll != null;
        assert propertiesSection != null;
        assert layoutTitledPane != null;
        assert layoutScroll != null;
        assert layoutSection != null;
        assert codeTitledPane != null;
        assert codeScroll != null;
        assert codeSection != null;
        assert allTitledPane != null;
        assert allScroll != null;
        assert allContent != null;
        assert searchStackPane != null;
        assert searchContent != null;
        assert accordion != null;
        assert inspectorRoot != null;

        getViewController().setSearchControl(getSearchController().getRoot());
		getViewController().setContent(super.getRoot());

		getSearchController().textProperty().addListener((ChangeListener<String>) (ov, oldStr, newStr) -> setSearchPattern(newStr));

        propertiesTitledPane.expandedProperty().addListener((ChangeListener<Boolean>) (ov, wasExpanded, expanded) -> handleTitledPane(wasExpanded, expanded, SectionId.PROPERTIES));
        layoutTitledPane.expandedProperty().addListener((ChangeListener<Boolean>) (ov, wasExpanded, expanded) -> handleTitledPane(wasExpanded, expanded, SectionId.LAYOUT));
        codeTitledPane.expandedProperty().addListener((ChangeListener<Boolean>) (ov, wasExpanded, expanded) -> handleTitledPane(wasExpanded, expanded, SectionId.CODE));

        // Clean the potential nodes added for design purpose in fxml
        clearSections();

        // Listen the drag property changes
        getApi().getApiDoc().getDrag().dragSourceProperty().addListener((ChangeListener<DragSource>) (ov, oldVal, newVal) -> {
            if (newVal != null) {
//                    System.out.println("Drag started !");
                dragOnGoing = true;
            } else {
//                    System.out.println("Drag finished.");
                dragOnGoing = false;
                updateInspector();
            }
        });

        // Listen the Scene stylesheets changes
        documentManager.stylesheetConfig().subscribe(s -> updateInspector());

        selectionState = new SelectionState(editorController.getSelection());
        viewModeChanged(null, getViewMode());
        expandedSectionChanged();

        accordion.expandedPaneProperty().addListener((ChangeListener<TitledPane>) (ov, t, t1) -> {
            expandedSectionProperty.setValue(getExpandedSectionId());
        });

        accordion.setPrefSize(300, 700);
        buildExpandedSection();
        updateClassNameInSectionTitles();
        searchResultDividerPosition = inspectorRoot.getDividerPositions()[0];
        searchPatternDidChange();
    }

    private void createLibraryMenu() {
    	MenuButton menuButton = getViewController().getViewMenuButton();

        ToggleGroup showTg = new ToggleGroup();
        ToggleGroup viewTg = new ToggleGroup();

        getViewController().textProperty().set(getResources().getString("inspector"));

        showAll = sceneBuilderFactory.createViewRadioMenuItem(getResources().getString("inspector.show.all"), showTg);
        showAll.setSelected(true);

        showEdited = sceneBuilderFactory.createViewRadioMenuItem(getResources().getString("inspector.show.edited"), showTg);
        separator = sceneBuilderFactory.createSeparatorMenuItem();
        viewAsSections = sceneBuilderFactory.createViewRadioMenuItem(getResources().getString("inspector.view.sections"), viewTg);
        viewAsSections.setSelected(true);
        viewByPropName = sceneBuilderFactory.createViewRadioMenuItem(getResources().getString("inspector.by.property.name"), viewTg);
        viewByPropType = sceneBuilderFactory.createViewRadioMenuItem(getResources().getString("inspector.by.property.type"), viewTg);

        showAll.setOnAction((e) -> showAllAction.checkAndPerform());
        showEdited.setOnAction((e) -> showEditedAction.checkAndPerform());
        viewAsSections.setOnAction((e) -> viewBySectionsAction.checkAndPerform());
        viewByPropName.setOnAction((e) -> viewByPropertyNameAction.checkAndPerform());
        viewByPropType.setOnAction((e) -> viewByPropertyTypeAction.checkAndPerform());

        menuButton.getItems().addAll(showAll, showEdited, separator, viewAsSections, viewByPropName, viewByPropType);
	}

	/*
     * Private
     */
    private void updateInspector() {
        if (isInspectorLoaded() && hasFxomDocument()) {
            SelectionState newSelectionState = new SelectionState(editorController.getSelection());
            if (isInspectorStateChanged(newSelectionState) || isEditedMode()) {
                selectionState = newSelectionState;
                rebuild();
            } else {
                // we may have a property changed here.
                selectionState = newSelectionState;
                updateClassNamesExtraForIncludes();
                reset();
            }
        }
    }

    private void updateClassNamesExtraForIncludes() {
        if(!getSelectedIntrinsics().isEmpty()) {
            updateClassNameInSectionTitles();
        }
    }

    private boolean isInspectorStateChanged(SelectionState newSelectionState) {
        // Inspector state change if one of the following is true:
        // - selected classes change
        // - common parent change
        // - resolve state change
        return (!newSelectionState.getSelectedClasses().equals(selectionState.getSelectedClasses())
                || (newSelectionState.getCommonParentClass() != selectionState.getCommonParentClass())
                || (!newSelectionState.getUnresolvedInstances().equals(selectionState.getUnresolvedInstances())));
    }

    private void searchPatternDidChange() {
        if (isInspectorLoaded()) {
            // Collapse/Expand the search result panel
            if (hasSearchPattern()) {
                if (!inspectorRoot.getItems().contains(searchStackPane)) {
                    inspectorRoot.getItems().add(0, searchStackPane);
                    inspectorRoot.setDividerPositions(searchResultDividerPosition);
                }
            } else {
                // Save the divider position for next search
                searchResultDividerPosition = inspectorRoot.getDividerPositions()[0];
                if (inspectorRoot.getItems().contains(searchStackPane)) {
                    inspectorRoot.getItems().remove(searchStackPane);
                }
            }

            buildFlatContent(searchContent);
        }
    }

    private void rebuild() {
        selectionState.clearSelectionCssState();
        
//        System.out.println("Inspector rebuild() called !");
        // The inspector structure has changed :
        // - selection changed
        // - parent changed
        // - search pattern changed
        // - SceneGraphObject resolved state changed
        // ==> the current section is to be fully rebuilt
        // TBD: we could optimize this by only refreshing values if
        //      same element class + same container class + same search pattern.
        clearSections();
        
        if (getViewMode() == ViewMode.SECTION) {
            buildExpandedSection();
        } else {
            buildFlatContent(allContent);
        }
        updateClassNameInSectionTitles();
        if (hasSearchPattern()) {
            buildFlatContent(searchContent);
        }
    }

    private void reset() {
//        System.out.println("Inspector reset() called !");
        // A property has changed, a reference has changed (e.g. css file).
        // or a selection of an identical node appears
        // ==> For all the editors currently in use:
        // - reset (state, suggested list, ...)
        // - reset the value
//        System.out.println("Refresh all the editors in use...");

        if (session != null) {
            session.forEach((e) -> {
                e.reset(e.getPropertyMeta(), selectionState);
                setEditorValueFromSelection(e);
            }, lastPropertyEditorValueChanged);
            
            lastPropertyEditorValueChanged = null;
        }
        
        //TODO below code is mandatory so uncomment fast
        
//        for (AbstractEditor editor : editorsInUse) {
//
//            if (editor instanceof PropertyEditor) {
//                if (editor == lastPropertyEditorValueChanged) {
//                    // do not reset an editor that just changed its value and initiated the reset
//                    lastPropertyEditorValueChanged = null;
//                    continue;
//                }
//                resetPropertyEditor((PropertyEditor) editor);
////                System.out.println("reset " + ((PropertyEditor) editor).getPropertyNameText());
//            }
//            setEditorValueFromSelection(editor);
//        }
    }

    private void buildExpandedSection() {
        buildSection(getExpandedSectionId());
    }

    /**
     * Builds the section from the provided metadata list.
     *
     * @param sectionId the section id to build
     */
    private void buildSection(SectionId sectionId) {
        if (sectionId == SectionId.NONE) {
            return;
        }
//        System.out.println("\nBuilding section " + sectionId + " - Selection : " + selection.getEntries());
        GridPane gridPane = getSectionContent(sectionId);
        gridPane.getChildren().clear();
        if (handleSelectionMessage(gridPane)) {
            return;
        }

        // Get Metadata
        Set<ValuePropertyMetadata> propMetaAll = getValuePropertyMetadata();

        SortedMap<InspectorPath, ValuePropertyMetadata> propMetaSection = new TreeMap<>(Metadata.getMetadata().INSPECTOR_PATH_COMPARATOR);
        assert propMetaAll != null;

        for (ValuePropertyMetadata valuePropMeta : propMetaAll) {
            InspectorPath inspectorPath = valuePropMeta.getInspectorPath();
            // Check section
            if (!isSameSection(inspectorPath.getSectionTag(), sectionId)) {
                continue;
            }
            if (valuePropMeta.isStaticProperty() && !isStaticPropertyRelevant(valuePropMeta.getName())) {
                continue;
            }
            if (isEditedMode()) {
                if (!isPropertyEdited(valuePropMeta, propMetaAll)) {
                    continue;
                }
            }
            propMetaSection.put(valuePropMeta.getInspectorPath(), valuePropMeta);
        }


        String currentSubSection = ""; //NOI18N
        int lineIndex = 0;
        if (sectionId == SectionId.CODE) {
            // add fx:id here, since it is not a property.
            // It has its own sub section title
            addSubSectionSeparator(gridPane, lineIndex, FXID_SUBSECTION_NAME);
            lineIndex++;
            currentSubSection = FXID_SUBSECTION_NAME;
            lineIndex = addFxIdEditor(gridPane, lineIndex);
        }

        if (propMetaSection.isEmpty()) {
            displayEmptyMessage(gridPane);
            return;
        }

        if (lineIndex == 0 && propMetaSection.isEmpty()) {
            displayEmptyMessage(gridPane);
            return;
        }

        Iterator<Entry<InspectorPath, ValuePropertyMetadata>> iter = propMetaSection.entrySet().iterator();
        //Set<PropertyName> groupProperties = new HashSet<>();
        
        while (iter.hasNext()) {
            // Loop on properties
            Entry<InspectorPath, ValuePropertyMetadata> entry = iter.next();
            InspectorPath inspectorPath = entry.getKey();
            ValuePropertyMetadata propMeta = entry.getValue();
            String newSubSection = inspectorPath.getSubSectionTag();
//            System.out.println(inspectorPath.getSectionTag() + " - " + newSubSection + " - " + propMeta.getName());
            if (!currentSubSection.equalsIgnoreCase(newSubSection)) {
                addSubSectionSeparator(gridPane, lineIndex, newSubSection);
                lineIndex++;
                currentSubSection = newSubSection;
            }
//            if (isGroupedProperty(propMeta.getName())) {
//                // Several properties are grouped in a single editor (e.g. AnchorPane constraints)
//                if (groupProperties.contains(propMeta.getName())) {
//                    continue;
//                }
//                PropertiesEditor propertiesEditor
//                        = getInitializedPropertiesEditor(propMeta.getName(), propMetaSection.values(), groupProperties);
//                if (propertiesEditor == null) {
//                    continue;
//                }
//                lineIndex = addInGridPane(gridPane, propertiesEditor, lineIndex);
//            } else {
//                lineIndex = addInGridPane(gridPane, getInitializedPropertyEditor(propMeta), lineIndex);
//            }
            lineIndex = addInGridPane(gridPane, getInitializedPropertyEditor(propMeta), lineIndex);
        }
    }

    private void addSubSectionSeparator(GridPane gridPane, int lineIndex, String titleStr) {
        Node title = getSubSectionTitle(titleStr);
        gridPane.add(title, 0, lineIndex);
        GridPane.setColumnSpan(title, GridPane.REMAINING);
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setValignment(VPos.CENTER);
        gridPane.getRowConstraints().add(rowConstraint);
    }

//    private PropertiesEditor getInitializedPropertiesEditor(PropertyName groupedPropName,
//            Collection<ValuePropertyMetadata> propMetas, Set<PropertyName> groupProperties) {
//        ValuePropertyMetadata[] propMetaGroup = getGroupedPropertiesMetadata(groupedPropName, propMetas, groupProperties);
//        PropertiesEditor propertiesEditor = getPropertiesEditor(propMetaGroup);
//        if (propertiesEditor == null) {
//            return null;
//        }
//        for (AbstractPropertyEditor propertyEditor : propertiesEditor.getPropertyEditors()) {
//            setEditorValueFromSelection(propertyEditor);
//            handlePropertyEditorChanges(propertyEditor);
//        }
//        return propertiesEditor;
//    }

    private PropertyEditor getInitializedPropertyEditor(ValuePropertyMetadata propMeta) {
        PropertyEditor propertyEditor = getPropertyEditor(propMeta);
        setEditorValueFromSelection(propertyEditor);
        handlePropertyEditorChanges(propertyEditor);
        return propertyEditor;
    }

    private int addFxIdEditor(GridPane gridPane, int lineIndex) {
        PropertyEditor propertyEditor = session.getFxIdEditor(selectionState);
        setFxIdFromSelection(propertyEditor);
        handlePropertyEditorChanges(propertyEditor);
        return addInGridPane(gridPane, propertyEditor, lineIndex);
    }

    private void handlePropertyEditorChanges(PropertyEditor propertyEditor) {
        handleValueChange(propertyEditor);
        handleTransientValueChange(propertyEditor);
        handleEditingChange(propertyEditor);
        handleNavigateRequest(propertyEditor);
    }

//    private boolean isGroupedProperty(PropertyName propName) {
//        // AnchorPane anchors only for now
//        return isAnchorConstraintsProp(propName);
//    }
//
//    private boolean isGroupEdited(Collection<ValuePropertyMetadata> propMetaAll) {
//        // AnchorPane anchors only for now
//        return isAnchorConstraintsEdited(propMetaAll);
//    }

    private boolean isPropertyEdited(ValuePropertyMetadata valuePropMeta, Collection<ValuePropertyMetadata> propMetadatas) {
        PropertyName propName = valuePropMeta.getName();
        //boolean groupedProperty = isGroupedProperty(propName);
        //if (!groupedProperty && !isPropertyEdited(valuePropMeta)) {
        if (!isPropertyEdited(valuePropMeta)) {
            return false;
        }
//        if (groupedProperty) {
//            // We may have some properties edited in a group, some not.
//            // In this case, we want to show all the goup properties.
//            if (!isGroupEdited(new HashSet<>(propMetadatas))) {
//                return false;
//            }
//        }
        return true;
    }


//    private boolean isAnchorConstraintsEdited(Collection<ValuePropertyMetadata> propMetaAll) {
//        for (ValuePropertyMetadata valuePropMeta : propMetaAll) {
//            if (isAnchorConstraintsProp(valuePropMeta.getName())) {
//                if (isPropertyEdited(valuePropMeta)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    private ValuePropertyMetadata[] getGroupedPropertiesMetadata(PropertyName groupedPropName,
//            Collection<ValuePropertyMetadata> propMetas, Set<PropertyName> groupProperties) {
//        // For now, the SB metadata does NOT include this grouping information.
//        // Since we have for now only AnchorPane constraints properties in this case,
//        // this is handled at the inspector level.
//        // We may include this in the metadata in the future if we have a sigificant number
//        // of properties in this case (i.e. if we plan to implement editors for rotateX/Y/Z,
//        // min/max/prefWidth, etc...)
//
//        //
//        // AnchorPane anchors only for now
//        //
//        assert isAnchorConstraintsProp(groupedPropName);
//        int anchorsNb = 4;
//        ArrayList<ValuePropertyMetadata> propMetaGroup = new ArrayList<>();
//        // Create an empty list, to be able to set the entries at the right index.
//        for (int ii = 0; ii < anchorsNb; ii++) {
//            propMetaGroup.add(null);
//        }
//
//        // Loop on properties to find anchors properties
//        for (ValuePropertyMetadata propMeta : propMetas) {
//            PropertyName propName = propMeta.getName();
//            if (!isAnchorConstraintsProp(propName)) {
//                continue;
//            }
//            groupProperties.add(propName);
//            switch (propName.toString()) {
//                case AbstractEditor.topAnchorPropName:
//                    propMetaGroup.set(0, propMeta);
//                    break;
//                case AbstractEditor.rightAnchorPropName:
//                    propMetaGroup.set(1, propMeta);
//                    break;
//                case AbstractEditor.bottomAnchorPropName:
//                    propMetaGroup.set(2, propMeta);
//                    break;
//                case AbstractEditor.leftAnchorPropName:
//                    propMetaGroup.set(3, propMeta);
//                    break;
//                default:
//                    assert false;
//            }
//        }
//        return propMetaGroup.toArray(new ValuePropertyMetadata[propMetaGroup.size()]);
//    }

    private boolean isSameSection(String sectionStr, SectionId sectionId) {
        return sectionStr.equalsIgnoreCase(sectionId.toString());
    }

    private boolean hasSelectedElement() {
        return hasFxomDocument() && (!selectionState.isSelectionEmpty());
    }

    private boolean hasSelectedElementNothingForInspector() {
        return hasFxomDocument() && getSelectedInstances().isEmpty();
    }

    private boolean hasSelectedIntrinsicNothingForInspector() {
        return hasFxomDocument() && getSelectedIntrinsics().isEmpty();
    }


    private Set<FXOMIntrinsic> getSelectedIntrinsics() {
        return selectionState.getSelectedIntrinsics();
    }

    private boolean hasMultipleSelection() {
        return getSelectedInstances().size() > 1;
    }

    private boolean hasUnresolvedInstance() {
        return getUnresolvedInstances().size() > 0;
    }

    private void buildFlatContent(GridPane gridPane) {
//        System.out.println("\nBuilding Flat panel" + " - Selection : " + selection.getEntries());
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
        if (handleSelectionMessage(gridPane)) {
            return;
        }
        if (isSearch(gridPane) && !hasSearchPattern()) {
            addMessage(gridPane, I18N.getString("inspector.message.searchpattern.empty"));
            return;
        }
        boolean isOrderdByType = getViewMode() == ViewMode.PROPERTY_TYPE;

        // Get Metadata
        Set<ValuePropertyMetadata> propMetadatas = getValuePropertyMetadata();
        if (propMetadatas.isEmpty()) {
            addMessage(gridPane, I18N.getString("inspector.message.no.properties"));
            return;
        }
        List<ValuePropertyMetadata> propMetadataList = Arrays.asList(propMetadatas.toArray(new ValuePropertyMetadata[propMetadatas.size()]));
        if (isOrderdByType) {
            Collections.sort(propMetadataList, new ValuePropertyMetadataClassComparator());
        } else {
            Collections.sort(propMetadataList, new ValuePropertyMetadataNameComparator());
        }

        List<ValuePropertyMetadata> orderedPropMetadatas = new ArrayList<>();
        for (ValuePropertyMetadata valuePropMeta : propMetadataList) {
            if (isSearch(gridPane) && !isSearchPatternMatch(valuePropMeta)) {
                continue;
            }
            if (valuePropMeta.isStaticProperty() && !isStaticPropertyRelevant(valuePropMeta.getName())) {
                continue;
            }
            if (isEditedMode()) {
                if (!isPropertyEdited(valuePropMeta, propMetadataList)) {
                    continue;
                }
            }
            orderedPropMetadatas.add(valuePropMeta);
        }

        if (orderedPropMetadatas.isEmpty()) {
            displayEmptyMessage(gridPane);
            return;
        }

        int lineIndex = 0;
        Set<PropertyName> groupProperties = new HashSet<>();
        for (ValuePropertyMetadata propMeta : orderedPropMetadatas) {
//            if (isGroupedProperty(propMeta.getName())) {
//                if (groupProperties.contains(propMeta.getName())) {
//                    continue;
//                }
//                // Several properties are grouped in a single editor (e.g. AnchorPane constraints)
//                PropertiesEditor propertiesEditor
//                        = getInitializedPropertiesEditor(propMeta.getName(), new HashSet<>(orderedPropMetadatas), groupProperties);
//                if (propertiesEditor == null) {
//                    continue;
//                }
//                lineIndex = addInGridPane(gridPane, propertiesEditor, lineIndex);
//            } else {
                lineIndex = addInGridPane(gridPane, getInitializedPropertyEditor(propMeta), lineIndex);
            //}
        }
    }

    private boolean handleSelectionMessage(GridPane gridPane) {
        if (!hasSelectedElement()) {
            addMessage(gridPane, I18N.getString("inspector.message.no.selected"));
            return true;
        }
        if (hasSelectedElementNothingForInspector() && hasSelectedIntrinsicNothingForInspector()) {
            addMessage(gridPane, I18N.getString("inspector.message.no.thingforinspector"));
            return true;
        }
        if (hasUnresolvedInstance()) {
            addMessage(gridPane, I18N.getString("inspector.message.no.resolved"));
            return true;
        }
        return false;
    }

    private void displayEmptyMessage(GridPane gridPane) {
        String messKey;
        if (isSearch(gridPane)) {
            messKey = "label.search.noresults";
        } else if (isEditedMode()) {
            messKey = "inspector.message.no.propertiesedited";
        } else {
            messKey = "inspector.message.no.properties";
        }
        addMessage(gridPane, I18N.getString(messKey));
    }

    private boolean isSearchPatternMatch(ValuePropertyMetadata propMeta) {
        String propSimpleName = propMeta.getName().getName();
        // Check model name
        if (propSimpleName.toLowerCase(Locale.ENGLISH).contains(searchPattern.toLowerCase(Locale.ENGLISH))) {
            return true;
        }

        // Check display name
        return EditorUtils.toDisplayName(propSimpleName).toLowerCase(Locale.ENGLISH).contains(searchPattern.toLowerCase(Locale.ENGLISH));
    }

    private boolean isStaticPropertyRelevant(PropertyName propName) {
        boolean isRelevant;
        if(isIntrinsic()) {
            isRelevant = checkIfStaticPropertyRelevantForIntrinsic(propName);
        }
        else {
            // Check if the static property class is the common parent of the selection
            if (getCommonParent() == null) return false;
            isRelevant =  getCommonParent() == propName.getResidenceClass();
        }
        return isRelevant;
    }

    private boolean isIntrinsic() {
        boolean result = false;
        if(selectionState.getSelection().getHitItem() instanceof FXOMIntrinsic) {
            result = true;
        }
        return result;
    }

    private boolean checkIfStaticPropertyRelevantForIntrinsic(PropertyName propName) {
        FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) selectionState.getSelection().getHitItem();
        return fxomIntrinsic.getParentObject() != null && fxomIntrinsic.getParentProperty().getParentInstance().getSceneGraphObject().getClass() == propName.getResidenceClass();
    }

    private boolean hasSearchPattern() {
        return (searchPattern != null) && !searchPattern.isEmpty();
    }

    private boolean isSearch(GridPane gridPane) {
        return gridPane == searchContent;
    }

    private int addInGridPane(GridPane gridPane, PropertyEditor editor, int lineIndex) {
        RowConstraints row1Constraints = new RowConstraints();
        LayoutFormat editorLayout;
        HBox propNameNode;
        String propNameText;
        
        if (editor instanceof AbstractPropertyEditor) {
            propNameNode = ((AbstractPropertyEditor) editor).getPropNameNode();
            propNameText = ((AbstractPropertyEditor) editor).getPropertyNameText();
            editorLayout = ((AbstractPropertyEditor) editor).getLayoutFormat();
            
            //TODO check if the group code commented below is well handled
        } else {
            // PropertiesEditor
            propNameNode = ((AbstractPropertiesEditor) editor).getNameNode();
            propNameText = ((AbstractPropertiesEditor) editor).getPropertyNameText();
            if (getViewMode() == ViewMode.SECTION) {
                editorLayout = LayoutFormat.SIMPLE_LINE_NO_NAME;
            } else {
                editorLayout = LayoutFormat.DOUBLE_LINE;
            }
        }
        propNameNode.setFocusTraversable(false);
        MenuButton menu = editor.getMenu();
        // For SQE tests
        menu.setId(propNameText + " Menu"); //NOI18N
        Node valueEditor = editor.getValueEditor();
        // For SQE tests
        valueEditor.setId(propNameText + " Value"); //NOI18N

        if (editorLayout == LayoutFormat.DOUBLE_LINE) {
            // We have to wrap the property name and the value editor in a VBox
            row1Constraints.setValignment(VPos.TOP);
            gridPane.getRowConstraints().add(row1Constraints);
            VBox editorBox = new VBox();
            editorBox.getChildren().addAll(propNameNode, valueEditor);
            propNameNode.setAlignment(Pos.CENTER_LEFT);
            GridPane.setColumnSpan(editorBox, 2);
            gridPane.add(editorBox, 0, lineIndex);
        } else {
            // One row
            gridPane.getRowConstraints().add(lineIndex, row1Constraints);
            if (editorLayout != LayoutFormat.SIMPLE_LINE_NO_NAME) {
                gridPane.add(propNameNode, 0, lineIndex);
                if (editorLayout == LayoutFormat.SIMPLE_LINE_CENTERED) {
                    // Property name, valued editor and cog menu are aligned, centered.
                    propNameNode.setAlignment(Pos.CENTER_LEFT);
                } else if (editorLayout == LayoutFormat.SIMPLE_LINE_TOP) {
                    // Property name, valued editor and cog menu are aligned on top.
                    propNameNode.setAlignment(Pos.TOP_LEFT);
                    row1Constraints.setValignment(VPos.TOP);
                } else if (editorLayout == LayoutFormat.SIMPLE_LINE_BOTTOM) {
                    // Property name, valued editor and cog menu are aligned on the bottom.
                    propNameNode.setAlignment(Pos.BOTTOM_LEFT);
                    row1Constraints.setValignment(VPos.BOTTOM);
                }
                GridPane.setColumnSpan(propNameNode, 1);
                GridPane.setColumnSpan(valueEditor, 1);
                gridPane.add(valueEditor, 1, lineIndex);
            } else {
                // LayoutFormat.SIMPLE_LINE_NO_NAME
                row1Constraints.setValignment(VPos.CENTER);
                GridPane.setColumnSpan(valueEditor, 2);
                gridPane.add(valueEditor, 0, lineIndex);
            }
        }

        // Add cog menu
        gridPane.add(menu, 2, lineIndex);

        lineIndex++;
        return lineIndex;
    }


    //TODO not used but, take a closer look to see what is the goal of this method
    // used to get the CssId PropertyEditor to update the value while the SceneBuilder is running
//    private StringEditor getCssIdEditor(){
//        ValuePropertyMetadata metadataForCssIDEditor = new StringPropertyMetadata(new PropertyName("id"), true,
//                null, new InspectorPath("Properties", "JavaFX CSS", 3));
//        StringEditor cssIdEditor = (StringEditor) getPropertyEditor(metadataForCssIDEditor);
//        handlePropertyEditorChanges(cssIdEditor);
//        return cssIdEditor;
//    }

//    private Button createButtonForFxId(){
//        Button button = new Button("Also set CSS-Id with fx:id");
//        button.setOnAction((ActionEvent) -> {
//            String fxId = getSelectedInstance().getFxId();
//            if (fxId == null)
//                return;
//            setSelectedFXOMInstances(getCssIdEditor().getPropertyMeta(), fxId);
//        });
//        return button;
//    }

    private void handleValueChange(PropertyEditor propertyEditor) {
        // Handle the value change
        propertyEditor.addValueListener((ov, oldValue, newValue) -> {
//                System.out.println("Value change : " + newValue);
            
            if (!propertyEditor.isUpdateFromModel()) {
                lastPropertyEditorValueChanged = propertyEditor;
                updateValueInModel(propertyEditor, oldValue, newValue);
            }
            if (propertyEditor.isRuledByCss()) {
                editorController.getMessageLog().logWarningMessage(
                        "inspector.css.overridden", propertyEditor.getPropertyNameText());
            }
        });
    }

    private void handleTransientValueChange(PropertyEditor propertyEditor) {
        // Handle the transient value change (no job here, only the scene graph is updated)
        propertyEditor.addTransientValueListener((ov, oldValue, newValue) -> {
//                System.out.println("Transient value change : " + newValue);
            lastPropertyEditorValueChanged = propertyEditor;
            for (FXOMInstance fxomInstance : getSelectedInstances()) {
                propertyEditor.getPropertyMeta().setValueInSceneGraphObject(fxomInstance, newValue);
            }
        });
    }

    private void updateValueInModel(PropertyEditor propertyEditor, Object oldValue, Object newValue) {
        if (propertyEditor.isUpdateFromModel()) {
            return;
        }
//        System.out.println("Property " + propertyEditor.getPropertyName() + ": Value changed from \"" + oldValue + "\" to \"" + newValue + "\"");
        if (propertyEditor instanceof FxIdEditor) {
            assert (newValue instanceof String) || (newValue == null);
            setSelectedFXOMInstanceFxId(getSelectedObject(), (String) newValue);
        } else if (propertyEditor instanceof ToggleGroupEditor) {
            assert (newValue instanceof String) || (newValue == null);
            setSelectionToggleGroup((String) newValue);
        } else {
            setSelectedFXOMInstances(propertyEditor.getPropertyMeta(), newValue);
        }
    }

    private void handleEditingChange(PropertyEditor propertyEditor) {
        // Handle the editing change
        propertyEditor.addEditingListener((ov, oldValue, newValue) -> {
            if (newValue) {
                // Editing session starting
//                    System.out.println("textEditingSessionDidBegin() called.");
                editorController.textEditingSessionDidBegin(p -> {
                    // requestSessionEnd
                    if (propertyEditor.getCommitListener() != null) {
                        propertyEditor.getCommitListener().handle(null);
                    }
                    boolean hasError = propertyEditor.isInvalidValue();
                    if (!hasError) {
//                                System.out.println("textEditingSessionDidEnd() called (from callback).");
                        if (editorController.isTextEditingSessionOnGoing()) {
                            editorController.textEditingSessionDidEnd();
                        }
                    }
//                            System.out.println("textEditingSessionDidBegin callback returns : " + !hasError);
                    return !hasError;
                });
            } else {
                // Editing session completed
                if (editorController.isTextEditingSessionOnGoing()) {
//                        System.out.println("textEditingSessionDidEnd() called.");
                    editorController.textEditingSessionDidEnd();
                    if (propertyEditor.getCommitListener() != null) {
                        propertyEditor.getCommitListener().handle(null);
                    }
                }
            }
        });

    }

    private void handleNavigateRequest(PropertyEditor propertyEditor) {
        // Handle a navigate request from an editor
        propertyEditor.addNavigateListener((ov, oldStr, newStr) -> {
            if (newStr != null) {
                setFocusToEditor(new PropertyName(newStr));
            }
        });
    }

    private void setSelectedFXOMInstances(ValuePropertyMetadata propMeta, Object value) {
        final PropertyName cacheHintPN = new PropertyName("cacheHint"); //NOI18N
        final Job job;
        if (cacheHintPN.equals(propMeta.getName())) {
            job = new ModifyCacheHintJob(context, propMeta, value, getEditorController()).extend();
        } else {
            job = new ModifySelectionJob(context, propMeta, value, getEditorController()).extend();
        }
//        System.out.println(job.getDescription());
        pushJob(job);
    }

    private void setSelectedFXOMInstanceFxId(FXOMObject fxomObject, String fxId) {
        final Job job = new ModifyFxIdJob(context, fxomObject, fxId, getEditorController()).extend();
        pushJob(job);
    }

    private void setSelectionToggleGroup(String tgId) {
        final Job job = new ModifySelectionToggleGroupJob(context, tgId, getEditorController()).extend();
        pushJob(job);
    }

    private void pushJob(Job job) {
        if (job.isExecutable()) {
            getEditorController().getJobManager().push(job);
        } else {
            System.out.println("Modify job not executable (because no value change?)");
        }
    }

    // Check if a property is edited
    private boolean isPropertyEdited(ValuePropertyMetadata propMeta) {
        for (FXOMInstance instance : getSelectedInstances()) {
            if (!propMeta.isReadWrite()) {
                continue;
            }
            Object value = propMeta.getValueObject(instance);
            Object defaultValue = propMeta.getDefaultValueObject();
            if (!EditorUtils.areEqual(value, defaultValue)) {
                return true;
            }
        }
        return false;
    }

//    // Set the editor value from selection
//    private void setEditorValueFromSelection(AbstractEditor editor) {
//        if (editor instanceof FxIdEditor) {
//            setFxIdFromSelection(editor);
//        } else if (isPropertyEditor(editor)) {
//            setEditorValueFromSelection(editor);
//        } else if (isPropertiesEditor(editor)) {
//            for (AbstractPropertyEditor propertyEditor : ((PropertiesEditor) editor).getPropertyEditors()) {
//                setEditorValueFromSelection(propertyEditor);
//            }
//        }
//    }

    // Set the fx:id from selection
    private void setFxIdFromSelection(PropertyEditor editor) {
        assert editor instanceof FxIdEditor;
        FxIdEditor fxIdEditor = (FxIdEditor) editor;
        if (hasMultipleSelection()) {
            // multi-selection ==> indeterminate
            fxIdEditor.setIndeterminate(true);
            fxIdEditor.setDisable(true);
        } else {
            String instanceFxId = getSelectedObject().getFxId();
            fxIdEditor.setDisable(false);
            fxIdEditor.setUpdateFromModel(true);
            fxIdEditor.reset(null, selectionState);
            fxIdEditor.setValue(instanceFxId);
            fxIdEditor.setUpdateFromModel(false);
        }
    }

    // Set the editor value from selection
    private void setEditorValueFromSelection(PropertyEditor propertyEditor) {

        if (propertyEditor instanceof FxIdEditor) {
            setFxIdFromSelection(propertyEditor);
            return;
        }
        
        // Determine the property value
        Object val = null;
        boolean isIndeterminate = false;
        boolean isReadWrite = true;
        boolean isRuledByCss = false;
        CssPropAuthorInfo cssInfo = null;
        PropertyName propName = propertyEditor.getPropertyName();

        // General case
        boolean first = true;
        for (FXOMInstance instance : getSelectedInstances()) {
            ValuePropertyMetadata propMeta = Metadata.getMetadata().queryValueProperty(instance, propName);
            assert propMeta != null;
            Object newVal = propMeta.getValueObject(instance);
//            System.out.println(propName + " value : " + newVal);
            if (!propMeta.isReadWrite()) {
                isReadWrite = false;
            }
            if (first) {
                val = newVal;
                first = false;
            } else if (!EditorUtils.areEqual(newVal, val)) {
                isIndeterminate = true;
            }

            Map<StyleableProperty, List<Style>> cssState = selectionState.getCssState(instance);
            cssInfo = CssInternal.getCssInfo(cssState, propMeta);
            if (cssInfo != null) {
                isRuledByCss = true;
            }
        }

        propertyEditor.setUpdateFromModel(true);
        if (isRuledByCss && cssInfo != null) {
            propertyEditor.setRuledByCss(true);
            propertyEditor.setCssInfo(cssInfo);
            if(propertyEditor.isDisablePropertyBound()){
                propertyEditor.unbindDisableProperty();
            }
            propertyEditor.setValue(cssInfo.getFxValue()); //adds CSS values to the ValueEditor
            propertyEditor.getValueEditor().setDisable(true); // disables the ValueEditor when CSS is present
        } else {
            propertyEditor.setRuledByCss(false);
            propertyEditor.setCssInfo(null);
            if (propertyEditor.getValueEditor() != null && propertyEditor.getValueEditor().isDisabled()) {
                // if ValueEditor is present and disabled it will enable it
                // it happens when another component is clicked and a ValueEditor was disabled
                if(!propertyEditor.isDisablePropertyBound()){
                    propertyEditor.getValueEditor().setDisable(false);
                }
            }
            if (isIndeterminate) {
                propertyEditor.setIndeterminate(true);
            } else {
                propertyEditor.setValue(val); //sets the default values or values from FXML tags
            }
        }
        propertyEditor.setUpdateFromModel(false);

        if (!(propertyEditor instanceof GenericEditor)) {
            if (!isReadWrite) {
                propertyEditor.setDisable(true);
            } else {
                propertyEditor.setDisable(false);
            }
        }
    }

    private PropertyEditor getPropertyEditor(ValuePropertyMetadata propMeta) {
        PropertyEditor propertyEditor = session.getEditor(propMeta, selectionState);

        // Set all the "Code" properties a double line layout
        if (isSameSection(propMeta.getInspectorPath().getSectionTag(), SectionId.CODE)) {
            propertyEditor.setLayoutFormat(LayoutFormat.DOUBLE_LINE);
        }
        return propertyEditor;
    }

//    private PropertiesEditor getPropertiesEditor(ValuePropertyMetadata[] propMetas) {
//        // AnchorPane only for now
//        for (ValuePropertyMetadata propMeta : propMetas) {
//            if (propMeta == null) {
//                // may happen if search
//                return null;
//            }
//            assert isAnchorConstraintsProp(propMeta.getName());
//        }
//        return makePropertiesEditor(AnchorPaneConstraintsEditor.class, propMetas);
//    }

//    private Map<String, Object> getConstants(DoublePropertyMetadata doublePropMeta) {
//        Map<String, Object> constants = new TreeMap<>();
//        String propNameStr = doublePropMeta.getName().getName();
//        
//        // TODO this kind of details must be part of metadata
//        if (propNameStr.contains("maxWidth") || propNameStr.contains("maxHeight")) { //NOI18N
//            constants.put("MAX_VALUE", Double.MAX_VALUE); //NOI18N
//        }
//        if (doublePropMeta instanceof ComputedSizeDoublePropertyMetadata) {
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//        } else if (doublePropMeta instanceof ComputedAndPrefSizeDoublePropertyMetadata) {
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//            constants.put("USE_PREF_SIZE", Region.USE_PREF_SIZE);
//        } else if (doublePropMeta instanceof NullableCoordinateDoublePropertyMetadata) {
//            constants.put("NULL", null); //NOI18N
//        } else if (doublePropMeta instanceof ProgressDoublePropertyMetadata) {
//            constants.put("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
//        }
//        return constants;
//    }

//    private Map<String, Object> getConstants(IntegerPropertyMetadata integerPropMeta) {
//        Map<String, Object> constants = new TreeMap<>();
//        String propNameStr = integerPropMeta.getName().getName();
//        if (propNameStr.contains("columnSpan") || propNameStr.contains("rowSpan")) { //NOI18N
//            constants.put("REMAINING", GridPane.REMAINING); //NOI18N
//        } else if (propNameStr.contains("prefColumnCount")) {
//            if (getSelectedClasses().size() == 1) {
//                if (getSelectedClass() == TextField.class || getSelectedClass() == PasswordField.class) {
//                    constants.put("DEFAULT_PREF_COLUMN_COUNT", TextField.DEFAULT_PREF_COLUMN_COUNT); //NOI18N
//                } else if (getSelectedClass() == TextArea.class) {
//                    constants.put("DEFAULT_PREF_COLUMN_COUNT", TextArea.DEFAULT_PREF_COLUMN_COUNT); //NOI18N
//                }
//            }
//        } else if (propNameStr.contains("prefRowCount")) {
//            assert getSelectedClass() == TextArea.class;
//            constants.put("DEFAULT_PREF_ROW_COUNT", TextArea.DEFAULT_PREF_ROW_COUNT); //NOI18N
//        }
//        return constants;
//    }

//    private int getMax(IntegerPropertyMetadata integerPropMeta) {
//        String propNameStr = integerPropMeta.getName().getName();
//        if (propNameStr.contains("columnIndex") || propNameStr.contains("columnSpan")) { //NOI18N
//            GridPane gridPane = getGridPane(propNameStr);
//            if (gridPane == null) {
//                // multi-selection from different GridPanes: not supported for now
//                return getMin(integerPropMeta);
//            }
//            int nbColumns = Deprecation.getGridPaneColumnCount(gridPane);
//            if (propNameStr.contains("columnIndex")) {//NOI18N
//                // index start to 0
//                return nbColumns - 1;
//            }
//            if (propNameStr.contains("columnSpan")) {//NOI18N
//                int maxIndex = getSpanPropertyMaxIndex(propNameStr);
//                return nbColumns - maxIndex;
//            }
//        }
//        if (propNameStr.contains("rowIndex") || propNameStr.contains("rowSpan")) { //NOI18N
//            GridPane gridPane = getGridPane(propNameStr);
//            if (gridPane == null) {
//                // multi-selection from different GridPanes: not supported for now
//                return getMin(integerPropMeta);
//            }
//            int nbRow = Deprecation.getGridPaneRowCount(gridPane);
//            if (propNameStr.contains("rowIndex")) {//NOI18N
//                // index start to 0
//                return nbRow - 1;
//            }
//            if (propNameStr.contains("rowSpan")) {//NOI18N
//                int maxIndex = getSpanPropertyMaxIndex(propNameStr);
//                return nbRow - maxIndex;
//            }
//        }
//        return Integer.MAX_VALUE;
//    }

//    private int getMin(IntegerPropertyMetadata integerPropMeta) {
//        String propNameStr = integerPropMeta.getName().getName();
//        if (propNameStr.contains("columnSpan") || propNameStr.contains("rowSpan")) { //NOI18N
//            return 1;
//        }
//        return 0;
//    }

//    private boolean isMultiLinesSupported(Set<Class<?>> selectedClasses, ValuePropertyMetadata propMeta) {
//        String propertyNameStr = propMeta.getName().getName();
//        if (selectedClasses.contains(TextField.class) || selectedClasses.contains(PasswordField.class)) {
//            if (propertyNameStr.equalsIgnoreCase("text")) {
//                return false;
//            }
//        }
//        if (propertyNameStr.equalsIgnoreCase("promptText")) {
//            return false;
//        }
//
//        if (propertyNameStr.equalsIgnoreCase("ellipsisString")) {
//            return false;
//        }
//        return true;
//    }

//    private int getSpanPropertyMaxIndex(String propNameStr) {
//        assert propNameStr.contains("columnSpan") || propNameStr.contains("rowSpan");
//        int maxIndex = 0;
//        for (FXOMInstance instance : getSelectedInstances()) {
//            assert instance.getSceneGraphObject() instanceof Node;
//            Integer index;
//            Node node = (Node) instance.getSceneGraphObject();
//            if (propNameStr.contains("columnSpan")) {//NOI18N
//                index = GridPane.getColumnIndex(node);
//            } else {
//                index = GridPane.getRowIndex(node);
//            }
//            if (index == null) {
//                index = 0;
//            }
//            if (index > maxIndex) {
//                maxIndex = index;
//            }
//        }
//        return maxIndex;
//    }

//    private GridPane getGridPane(String propNameStr) {
//        assert propNameStr.contains("columnIndex") || propNameStr.contains("columnSpan") //NOI18N
//                || propNameStr.contains("rowIndex") || propNameStr.contains("rowSpan");//NOI18N
//            FXOMObject commonParent = selectionState.getCommonParentObject();
//            if (commonParent == null) {
//                return null;
//            }
//            Object parentObj = commonParent.getSceneGraphObject();
//            assert parentObj instanceof GridPane;
//            return (GridPane) parentObj;
//    }

    private boolean isInspectorLoaded() {
        return accordion != null;
    }

    private boolean hasFxomDocument() {
        return getEditorController().getFxomDocument() != null;
    }

    private void addMessage(GridPane gridPane, String mess) {
        Label label = new Label(mess);
        label.getStyleClass().add("inspector-message");
        GridPane.setHalignment(label, HPos.LEFT);
        gridPane.add(label, 0, 0, 3, 1);
    }

    private Set<ValuePropertyMetadata> getValuePropertyMetadata() {
        Set<ValuePropertyMetadata> values = Metadata.getMetadata().queryValueProperties(getSelectedClasses());;
        Set<PropertyName> disabledProperties = getDisabledPropertiesFromMetadata();
        return values.stream().filter(v -> !disabledProperties.contains(v.getName())).collect(Collectors.toSet());
    }
    
    private Set<PropertyName> getDisabledPropertiesFromMetadata() {
        Set<PropertyName> disabled = new HashSet<>();
        getSelectedInstances().stream()
            .filter(fxi -> fxi.getParentObject() != null && fxi.getParentProperty() != null)
            .forEach(fxi -> {
                FXOMObject parent = fxi.getParentObject();
                FXOMProperty property = fxi.getParentProperty();
                ComponentPropertyMetadata cpm = Metadata.getMetadata().queryComponentProperty(parent.getSceneGraphObject().getClass(), property.getName());
                if (cpm != null) {
                    disabled.addAll(cpm.getDisabledProperties());
                }
            });
        return disabled;
    }

    private void clearSections() {
        // Put all the editors used in the editor pools
        session.clear();

        // Put all the subSectionTitles used in its pool
        for (SubSectionTitle subSectionTitle : subSectionTitlesInUse) {
            subSectionTitlePool.push(subSectionTitle);
        }
        subSectionTitlesInUse.clear();

        // Clear section content
        for (SectionId section : sections) {
            GridPane content = getSectionContent(section);
            if (content != null) {
                getSectionContent(section).getChildren().clear();
                getSectionContent(section).getRowConstraints().clear();
            }
        }
        allContent.getChildren().clear();
        allContent.getRowConstraints().clear();
        searchContent.getChildren().clear();
        searchContent.getRowConstraints().clear();

        // Set the scrollbars in upper position
//        propertiesScroll.setVvalue(0);
//        layoutScroll.setVvalue(0);
//        codeScroll.setVvalue(0);
//        allScroll.setVvalue(0);
//        searchScrollPane.setVvalue(0);
    }

    private GridPane getSectionContent(SectionId sectionId) {
        assert sectionId != SectionId.NONE;
        GridPane gp;
        switch (sectionId) {
            case PROPERTIES:
                gp = propertiesSection;
                break;
            case LAYOUT:
                gp = layoutSection;
                break;
            case CODE:
                gp = codeSection;
                break;
            default:
                throw new IllegalStateException("Unexpected section id " + sectionId); //NOI18N
        }
        return gp;
    }

    private void handleTitledPane(boolean wasExpanded, boolean expanded, SectionId sectionId) {
        if (!wasExpanded && expanded) {
            // TitledPane is expanded
            if (getSectionContent(sectionId).getChildren().isEmpty()) {
                buildSection(sectionId);
            }
        }
    }

    private Node getSubSectionTitle(String title) {
        SubSectionTitle subSectionTitle;
        if (subSectionTitlePool.isEmpty()) {
//            System.out.println("Creating NEW subsection title...");
            subSectionTitle = new SubSectionTitle(title);
        } else {
//            System.out.println("Getting subsection title from CACHE...");
            subSectionTitle = subSectionTitlePool.pop();
            subSectionTitle.setTitle(title);
        }
        subSectionTitlesInUse.add(subSectionTitle);
        return subSectionTitle.getNode();
    }

//    private AbstractPropertyEditor makePropertyEditor(Class<? extends AbstractEditor> editorClass, ValuePropertyMetadata propMeta) {
//        AbstractEditor editor;
//        AbstractPropertyEditor propertyEditor = null;
//        Stack<AbstractEditor> editorPool = editorPools.get(editorClass);
//        if ((editorPool != null) && !editorPool.isEmpty()) {
//            editor = editorPool.pop();
//            assert isPropertyEditor(editor);
//            propertyEditor = (AbstractPropertyEditor) editor;
//        }
//
//        propertyEditor = makeOrResetPropertyEditor(editorClass, propMeta, propertyEditor);
//
//        editorsInUse.add(propertyEditor);
//        return propertyEditor;
//    }
//
//    private void resetPropertyEditor(AbstractPropertyEditor propertyEditor) {
//        assert propertyEditor != null;
//        makeOrResetPropertyEditor(propertyEditor.getClass(), propertyEditor.getPropertyMeta(), propertyEditor);
//    }
//
//    private AbstractPropertyEditor makeOrResetPropertyEditor(
//            Class<? extends AbstractEditor> editorClass, ValuePropertyMetadata propMeta, AbstractPropertyEditor propertyEditor) {
//        AbstractPropertyEditor createdPropertyEditor = propertyEditor;
//        if (createdPropertyEditor != null) {
//            createdPropertyEditor.setUpdateFromModel(true);
//        }
//        Set<Class<?>> selectedClasses = getSelectedClasses();
//        if (editorClass == I18nStringEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((I18nStringEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new I18nStringEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == StringEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((StringEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new StringEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == DoubleEditor.class) {
//            assert propMeta instanceof DoublePropertyMetadata;
//            if (createdPropertyEditor != null) {
//                createdPropertyEditor.reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new DoubleEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == IntegerEditor.class) {
//            assert propMeta instanceof IntegerPropertyMetadata;
//            IntegerPropertyMetadata integerPropMeta = (IntegerPropertyMetadata) propMeta;
//            if (createdPropertyEditor != null) {
//                ((IntegerEditor) createdPropertyEditor).reset(propMeta, selectedClasses,
//                        getConstants(integerPropMeta), getMin(integerPropMeta), getMax(integerPropMeta));
//            } else {
//                createdPropertyEditor = new IntegerEditor(propMeta, selectedClasses,
//                        getConstants(integerPropMeta), getMin(integerPropMeta), getMax(integerPropMeta));
//            }
//        } else if (editorClass == BooleanEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((BooleanEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new BooleanEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == EnumEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((EnumEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new EnumEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == InsetsEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((InsetsEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new InsetsEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == BoundedDoubleEditor.class) {
//            assert propMeta instanceof DoublePropertyMetadata;
//            if (createdPropertyEditor != null) {
//                ((BoundedDoubleEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getSelectedInstances());
//            } else {
//                createdPropertyEditor = new BoundedDoubleEditor(propMeta, selectedClasses, getSelectedInstances());
//            }
//        } else if (editorClass == RotateEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((RotateEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new RotateEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == StyleEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((StyleEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getEditorController());
//            } else {
//                createdPropertyEditor = new StyleEditor(propMeta, selectedClasses, getEditorController());
//            }
//        } else if (editorClass == StyleClassEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((StyleClassEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getSelectedInstances(), getEditorController());
//            } else {
//                createdPropertyEditor = new StyleClassEditor(documentManager, propMeta, selectedClasses, getSelectedInstances(), getEditorController());
//            }
//        } else if (editorClass == StylesheetEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((StylesheetEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getEditorController().getFxmlLocation());
//            } else {
//                createdPropertyEditor = new StylesheetEditor(fileSystem, propMeta, selectedClasses, getEditorController().getFxmlLocation());
//            }
//        } else if (editorClass == StringListEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((StringListEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new StringListEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == FxIdEditor.class) {
//            String controllerClass = getControllerClass();
//            if (createdPropertyEditor != null) {
//                ((FxIdEditor) createdPropertyEditor).reset(getSuggestedFxIds(controllerClass), getEditorController());
//            } else {
//                createdPropertyEditor = new FxIdEditor(getSuggestedFxIds(controllerClass), getEditorController());
//            }
//        } else if (editorClass == CursorEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((CursorEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new CursorEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == EventHandlerEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((EventHandlerEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getSuggestedEventHandlers(getControllerClass()));
//            } else {
//                createdPropertyEditor = new EventHandlerEditor(propMeta, selectedClasses, getSuggestedEventHandlers(getControllerClass()));
//            }
//        } else if (editorClass == FunctionalInterfaceEditor.class) {
//            if (createdPropertyEditor != null) {
//                // "getSuggestedEventHandlers" (a method that already existed in SB code) isn't working right. It simply
//                // returns all the methods in the Controller class regardless of if they are good candidates for
//                // EventHandlers. We use if because at least this way we'll present all the methods available as
//                // auto-suggestions.
//                ((FunctionalInterfaceEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getSuggestedEventHandlers(getControllerClass()));
//            } else {
//                createdPropertyEditor = new FunctionalInterfaceEditor(propMeta, selectedClasses, getSuggestedEventHandlers(getControllerClass()));
//            }
//        } else if (editorClass == EffectPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((EffectPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new EffectPopupEditor(propMeta, selectedClasses, getEditorController());
//            }
//        } else if (editorClass == FontPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((FontPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getEditorController());
//            } else {
//                createdPropertyEditor = new FontPopupEditor(propMeta, selectedClasses, getEditorController());
//            }
//        } else if (editorClass == PaintPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((PaintPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new PaintPopupEditor(propMeta, selectedClasses, getEditorController());
//            }
//        } else if (editorClass == ImageEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((ImageEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getEditorController().getFxmlLocation());
//            } else {
//                createdPropertyEditor = new ImageEditor(fileSystem, propMeta, selectedClasses, getEditorController().getFxmlLocation());
//            }
//        } else if (editorClass == BoundsPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((BoundsPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new BoundsPopupEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == Point3DEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((Point3DEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new Point3DEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == DividerPositionsEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((DividerPositionsEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new DividerPositionsEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == TextAlignmentEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((TextAlignmentEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new TextAlignmentEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == KeyCombinationPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((KeyCombinationPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getEditorController());
//            } else {
//                createdPropertyEditor = new KeyCombinationPopupEditor(propMeta, selectedClasses, getEditorController());
//            }
//        } else if (editorClass == ColumnResizePolicyEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((ColumnResizePolicyEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new ColumnResizePolicyEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == Rectangle2DPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((Rectangle2DPopupEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new Rectangle2DPopupEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == ToggleGroupEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((ToggleGroupEditor) createdPropertyEditor).reset(propMeta, selectedClasses, getSuggestedToggleGroups());
//            } else {
//                createdPropertyEditor = new ToggleGroupEditor(propMeta, selectedClasses, getSuggestedToggleGroups());
//            }
//        } else if (editorClass == ButtonTypeEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((ButtonTypeEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new ButtonTypeEditor(propMeta, selectedClasses);
//            }
//        } else if (editorClass == DurationEditor.class) {
//            if (createdPropertyEditor != null) {
//                ((DurationEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new DurationEditor(propMeta, selectedClasses);
//            }
//        }
//        else if(editorClass == IncludeFxmlEditor.class) {
//            createdPropertyEditor = createOrResetIncludeFxmlEditor(createdPropertyEditor, selectedClasses, propMeta);
//        }
//        else if(editorClass == CharsetEditor.class) {
//            createdPropertyEditor = createOrResetCharsetEditor(createdPropertyEditor, selectedClasses, propMeta);
//        } else if (editorClass == ColorPopupEditor.class) {
//            if (createdPropertyEditor != null) {
//                createdPropertyEditor.reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new ColorPopupEditor(propMeta, selectedClasses, getEditorController());
//            }
//        }
//        else {
//            if (createdPropertyEditor != null) {
//                ((GenericEditor) createdPropertyEditor).reset(propMeta, selectedClasses);
//            } else {
//                createdPropertyEditor = new GenericEditor(propMeta, selectedClasses);
//            }
//        }
//        if(createdPropertyEditor != null)
//            createdPropertyEditor.setUpdateFromModel(false);
//
//        return createdPropertyEditor;
//    }
//
//    private AbstractPropertyEditor createOrResetIncludeFxmlEditor(AbstractPropertyEditor propertyEditor, Set<Class<?>> selectedClasses, ValuePropertyMetadata propMeta) {
//        AbstractPropertyEditor newPropertyEditor;
//        if (propertyEditor != null) {
//            newPropertyEditor = propertyEditor;
//            propertyEditor.reset(propMeta, selectedClasses);
//        }
//        else {
//            newPropertyEditor = new IncludeFxmlEditor(fileSystem, propMeta, selectedClasses, getEditorController());
//        }
//        return newPropertyEditor;
//    }
//
//    private AbstractPropertyEditor createOrResetCharsetEditor(AbstractPropertyEditor propertyEditor, Set<Class<?>> selectedClasses, ValuePropertyMetadata propMeta) {
//        AbstractPropertyEditor newPropertyEditor = null;
//        if (propMeta instanceof StringPropertyMetadata) {
//            if (propertyEditor != null) {
//                newPropertyEditor = propertyEditor;
//                ((CharsetEditor) propertyEditor).reset(propMeta, selectedClasses, this.availableCharsets);
//            } else {
//                newPropertyEditor = new CharsetEditor(propMeta, selectedClasses, this.availableCharsets);
//            }
//        }
//        return newPropertyEditor;
//    }
//
//    private PropertiesEditor makePropertiesEditor(Class<? extends AbstractEditor> editorClass, ValuePropertyMetadata[] propMetas) {
//        AbstractEditor editor = null;
//        PropertiesEditor propertiesEditor;
//        Stack<AbstractEditor> editorPool = editorPools.get(editorClass);
//        if ((editorPool != null) && !editorPool.isEmpty()) {
//            editor = editorPool.pop();
//            assert isPropertiesEditor(editor);
//        }
//
//        // Only AnchorPane for now
//        assert editorClass == AnchorPaneConstraintsEditor.class;
//
//        if (editor != null) {
//            assert editor instanceof AnchorPaneConstraintsEditor;
//            ((AnchorPaneConstraintsEditor) editor).reset(
//                    propMetas[0], propMetas[1], propMetas[2], propMetas[3], getSelectedInstances());
//        } else {
//            editor = new AnchorPaneConstraintsEditor("AnchorPane Constraints", propMetas[0], propMetas[1], propMetas[2], propMetas[3], getSelectedInstances());
//        }
//        propertiesEditor = (AnchorPaneConstraintsEditor) editor;
//
//        editorsInUse.add(editor);
//        return propertiesEditor;
//    }

    private static class SubSectionTitle {

        @FXML
        private Label titleLb;

        private Parent root;

        public SubSectionTitle(String title) {
            initialize(title);
        }

        // Separate method to avoid FindBugs warning
        private void initialize(String title) {
//          System.out.println("Loading new SubSection.fxml...");
          root = FXMLUtils.load(this, "SubSection.fxml");
          titleLb.setText(title);
        }

        public void setTitle(String title) {
            titleLb.setText(title);
        }

        public Node getNode() {
            return root;
        }
    }

    private void updateClassNameInSectionTitles() {
        final String intrinsicClassName = "FXOMIntrinsic";
        String selClass = ""; //NOI18N
        if (getSelectedClasses().size() > 1) {
            selClass = I18N.getString("inspector.sectiontitle.multiple");
        } else if (getSelectedClasses().size() == 1) {
            selClass = getSelectedClass().getSimpleName();
            if(intrinsicClassName.equals(selClass)) {
                selClass =  retrieveNameForIntrinsic();
            }

        }

        for (TitledPane titledPane : accordion.getPanes()) {
            Node graphic = titledPane.getGraphic();
            assert graphic instanceof Label;
            if (titledPane == allTitledPane) {
                allTitledPane.setText(null);
            } else {
                if (!selClass.isEmpty() && !selClass.startsWith(" :")) { //NOI18N
                    selClass = " : " + selClass; //NOI18N
                }
            }
            ((Label) graphic).setText(selClass);
        }
    }

    private String retrieveNameForIntrinsic() {
        final String includeTagBinder = "fx:include - ";
        String source = "";
        if(getSelectedIntrinsics().iterator().hasNext()) {
            FXOMIntrinsic fxomIntrinsic = getSelectedIntrinsics().iterator().next();
            Path p = Paths.get(fxomIntrinsic.getSource());
            source = includeTagBinder.concat(p.getFileName().toString());
        }
        return source;
    }

    //
    // Helper methods for SelectionState class
    //
    private Set<FXOMInstance> getSelectedInstances() {
        return selectionState.getSelectedInstances();
    }

    private FXOMObject getSelectedObject() {
        if(getSelectedInstances().size() == 1) {
            return (FXOMInstance) getSelectedInstances().toArray()[0];
        }
        else if(getSelectedIntrinsics().size() == 1) {
            return (FXOMIntrinsic) getSelectedIntrinsics().toArray()[0];
        }
        return null;
    }

    private Set<FXOMInstance> getUnresolvedInstances() {
        return selectionState.getUnresolvedInstances();
    }

    private Set<Class<?>> getSelectedClasses() {
        return selectionState.getSelectedClasses();
    }

    private Class<?> getSelectedClass() {
        assert getSelectedClasses().size() == 1;
        return (Class<?>) getSelectedClasses().toArray()[0];
    }

    private Class<?> getCommonParent() {
        return selectionState.getCommonParentClass();
    }

    /*
     * Set the focus to a given property value editor,
     * and move the scrolllbar so that it is visible.
     * Typically used by CSS analyzer.
     */
    public void setFocusToEditor(PropertyName propName) {
        // Retrieve the editor
        PropertyEditor editor = session.find(propName);
        
        if (editor == null) {
            // editor not found
            return;
        }

        final PropertyEditor editorToFocus = editor;

        final Node valueEditorNode = editorToFocus.getValueEditor();
        // Search the ScrollPane
        ScrollPane sp = null;
        Node node = valueEditorNode.getParent();
        while (node != null) {
            if (node instanceof ScrollPane) {
                sp = (ScrollPane) node;
                break;
            }
            node = node.getParent();
        }
        if (sp == null) {
            return;
        }

        // Position the scrollBar such as the editor is centered in the TitledPane (when possible)
        final ScrollPane scrollPane = sp;
        double editorHeight = valueEditorNode.getLayoutBounds().getHeight();
        final Point2D pt = Deprecation.localToLocal(valueEditorNode, 0, 0, scrollPane.getContent());
        // viewport height
        double vpHeight = scrollPane.getViewportBounds().getHeight();
        // Position of the editor in the scrollPane content
        double selY = pt.getY();
        // Height of the scrollPane content
        double contentHeight = scrollPane.getContent().getLayoutBounds().getHeight();
        // Position of the middle point of the scrollPane content
        double contentMiddle = contentHeight / 2;
        // Manage the editor height depending on its position
        if (selY > contentMiddle) {
            selY += editorHeight;
        } else {
            selY -= editorHeight;
        }
        // Compute the move to apply to position the editor on the middle of the scrollPane content
        double moveContent = selY - contentMiddle;
        // Size ratio between scrollPane content and viewport
        double vpRatio = contentHeight / vpHeight;
        // Move to apply to the editor to position it in the middle of the viewport
        double moveVp = moveContent / vpRatio;
        // Position of the editor in the viewport
        double selYVp = (vpHeight / 2) + moveVp;
        // Position in percent
        double scrollPos = selYVp / vpHeight;
        // Finally, set the scrollBar position
        scrollPane.setVvalue(scrollPos);

        // Set the focus to the editor
        editorToFocus.requestFocus();
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parent getRoot() {
		return getViewController().getRoot();
	}

    public Editor getEditorController() {
        return editorController;
    }

	
}
