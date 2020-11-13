/*
 * Copyright (c) 2016, 2019, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.app.preferences;

import static com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences.DEFAULT_ROOT_CONTAINER_HEIGHT;
import static com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences.DEFAULT_ROOT_CONTAINER_WIDTH;
import static com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences.recentItemsSizes;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.MainController;
import com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences.CSSAnalyzerColumnsOrder;
import com.oracle.javafx.scenebuilder.app.preferences.global.AccordionAnimationPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.CssTableColumnsOrderingReversedPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.DisplayModePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.DisplayOptionPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsSizePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.ToolThemePreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.WildcardImportsPreference;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.DoubleField;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController.DISPLAY_MODE;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.kit.preferences.global.AlignmentGuidesColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.BackgroundImagePreference.BackgroundImage;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference.GluonTheme;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ParentRingColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.RootContainerWidthPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference.Theme;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker.Mode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Preferences window controller.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class PreferencesWindowController extends AbstractFxmlWindowController {

    @FXML
    private DoubleField rootContainerHeight;
    @FXML
    private DoubleField rootContainerWidth;
    @FXML
    private ChoiceBox<BackgroundImage> backgroundImage;
    @FXML
    private ChoiceBox<ToolTheme> scenebuilderTheme;
    @FXML
    private ChoiceBox<DISPLAY_MODE> libraryDisplayOption;
    @FXML
    private ChoiceBox<DisplayOption> hierarchyDisplayOption;
    @FXML
    private ChoiceBox<CSSAnalyzerColumnsOrder> cssAnalyzerColumnsOrder;
    @FXML
    private MenuButton alignmentGuidesButton;
    @FXML
    private MenuButton parentRingButton;
    @FXML
    private CustomMenuItem alignmentGuidesMenuItem;
    @FXML
    private CustomMenuItem parentRingMenuItem;
    @FXML
    private Rectangle alignmentGuidesGraphic;
    @FXML
    private Rectangle parentRingGraphic;
    @FXML
    private ChoiceBox<Integer> recentItemsSize;
    @FXML
    private ChoiceBox<Theme> themes;
    @FXML
    private ChoiceBox<GluonSwatch> gluonSwatch;
    @FXML
    private CheckBox animateAccordion;
    @FXML
    private CheckBox wildcardImports;

    private PaintPicker alignmentColorPicker;
    private PaintPicker parentRingColorPicker;

    private final Stage ownerWindow;
    private final GlobalPreferences recordGlobal;
    private final RootContainerHeightPreference rootContainerHeightPreference;
	private final RootContainerWidthPreference rootContainerWidthPreference;
	private final BackgroundImagePreference backgroundImagePreference;
	private final AlignmentGuidesColorPreference alignmentGuidesColorPreference;
	private final ParentRingColorPreference parentRingColorPreference;
	private final ToolThemePreference toolThemePreference;
	private final DisplayModePreference displayModePreference;
	
	//private final CssAnalyzerColumnsOrderPreference cssAnalyzerColumnsOrderPreference;
	private final CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversedPreference;
	
	private final ThemePreference themePreference;
	private final GluonSwatchPreference gluonSwatchPreference;
	private final RecentItemsSizePreference recentItemsSizePreference;
	private final RecentItemsPreference recentItemsPreference;
	private final AccordionAnimationPreference accordionAnimationPreference;
	private final WildcardImportsPreference wildcardImportsPreference;
	private final DisplayOptionPreference displayOptionPreference;
	private final GluonThemePreference gluonThemePreference;
	

    public PreferencesWindowController(
    		@Autowired DocumentWindowController documentWindowController,
    		@Autowired GlobalPreferences recordGlobal,
    		@Autowired RootContainerHeightPreference rootContainerHeightPreference,
    		@Autowired RootContainerWidthPreference rootContainerWidthPreference,
    		@Autowired BackgroundImagePreference backgroundImagePreference,
    		@Autowired AlignmentGuidesColorPreference alignmentGuidesColorPreference,
    		@Autowired ParentRingColorPreference parentRingColorPreference,
    		@Autowired ToolThemePreference toolThemePreference,
    		@Autowired DisplayModePreference displayModePreference,
    		@Autowired DisplayOptionPreference displayOptionPreference,
    		
    		//@Autowired CssAnalyzerColumnsOrderPreference cssAnalyzerColumnsOrderPreference,
    		@Autowired CssTableColumnsOrderingReversedPreference cssTableColumnsOrderingReversedPreference,
    		
    		@Autowired ThemePreference themePreference,
    		@Autowired GluonSwatchPreference gluonSwatchPreference,
    		@Autowired GluonThemePreference gluonThemePreference,
    		@Autowired RecentItemsSizePreference recentItemsSizePreference,
    		@Autowired RecentItemsPreference recentItemsPreference,
    		@Autowired AccordionAnimationPreference accordionAnimationPreference,
    		@Autowired WildcardImportsPreference wildcardImportsPreference
    		) {
        super(PreferencesWindowController.class.getResource("Preferences.fxml"), //NOI18N
                I18N.getBundle(), documentWindowController.getStage());
        this.ownerWindow = documentWindowController.getStage();
        this.recordGlobal = recordGlobal;
        this.rootContainerHeightPreference = rootContainerHeightPreference;
        this.rootContainerWidthPreference = rootContainerWidthPreference;
        this.backgroundImagePreference = backgroundImagePreference;
        this.alignmentGuidesColorPreference = alignmentGuidesColorPreference;
        this.parentRingColorPreference = parentRingColorPreference;
        this.toolThemePreference = toolThemePreference;
        this.displayModePreference = displayModePreference;
        this.displayOptionPreference = displayOptionPreference;
        
        //this.cssAnalyzerColumnsOrderPreference = cssAnalyzerColumnsOrderPreference;
        this.cssTableColumnsOrderingReversedPreference = cssTableColumnsOrderingReversedPreference;
        
        this.themePreference = themePreference;
        this.gluonSwatchPreference = gluonSwatchPreference;
        this.gluonThemePreference = gluonThemePreference;
        this.recentItemsSizePreference = recentItemsSizePreference;
        this.recentItemsPreference = recentItemsPreference;
        this.accordionAnimationPreference = accordionAnimationPreference;
        this.wildcardImportsPreference = wildcardImportsPreference;

    }

    /*
     * AbstractModalDialog
     */
    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();

        
        // Root container size
        rootContainerHeight.setText(String.valueOf(rootContainerHeightPreference.getValue()));
        rootContainerHeight.setOnAction(t -> {
            final String value = rootContainerHeight.getText();
			rootContainerHeightPreference.setValue(Double.valueOf(value)).writeToJavaPreferences();
            rootContainerHeight.selectAll();
            // Update UI
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshRootContainerHeight(recordGlobal));
        });
        rootContainerWidth.setText(String.valueOf(rootContainerWidthPreference.getValue()));
        rootContainerWidth.setOnAction(t -> {
            final String value = rootContainerWidth.getText();
            rootContainerWidthPreference.setValue(Double.valueOf(value)).writeToJavaPreferences();
            rootContainerWidth.selectAll();
            // Update UI
//            recordGlobal.refreshRootContainerWidth();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshRootContainerWidth(recordGlobal));
        });

        // Background image
        backgroundImage.getItems().setAll(Arrays.asList(BackgroundImage.class.getEnumConstants()));
        backgroundImage.setValue(backgroundImagePreference.getValue());
        backgroundImage.getSelectionModel().selectedItemProperty().addListener(new BackgroundImageListener());

        // PaintPicker delegate shared by alignmentColorPicker and parentRingColorPicker
        final PaintPicker.Delegate delegate = new PaintPickerDelegate();

        // Alignment guides color
        final Color alignmentColor = recordGlobal.getAlignmentGuidesColor();
        alignmentColorPicker = new PaintPicker(delegate, Mode.COLOR);
        alignmentGuidesGraphic.setFill(alignmentColor);
        alignmentGuidesMenuItem.setContent(alignmentColorPicker);
        alignmentColorPicker.setPaintProperty(alignmentColor);
        alignmentColorPicker.paintProperty().addListener(
                new AlignmentGuidesColorListener(alignmentGuidesGraphic));

        // Parent ring color
        final Color parentRingColor = recordGlobal.getParentRingColor();
        parentRingColorPicker = new PaintPicker(delegate, Mode.COLOR);
        parentRingGraphic.setFill(parentRingColor);
        parentRingMenuItem.setContent(parentRingColorPicker);
        parentRingColorPicker.setPaintProperty(parentRingColor);
        parentRingColorPicker.paintProperty().addListener(
                new ParentRingColorListener(parentRingGraphic));

        // Tool theme
        scenebuilderTheme.getItems().setAll(Arrays.asList(ToolTheme.class.getEnumConstants()));
        scenebuilderTheme.setValue(recordGlobal.getToolTheme());
        scenebuilderTheme.getSelectionModel().selectedItemProperty().addListener(new ToolThemeListener());

        // Library view option
        final DISPLAY_MODE availableDisplayMode[] = new DISPLAY_MODE[]{
            DISPLAY_MODE.LIST, DISPLAY_MODE.SECTIONS};
        libraryDisplayOption.getItems().setAll(Arrays.asList(availableDisplayMode));
        libraryDisplayOption.setValue(recordGlobal.getLibraryDisplayOption());
        libraryDisplayOption.getSelectionModel().selectedItemProperty().addListener(new LibraryOptionListener());

        // Hierarchy display option
        hierarchyDisplayOption.getItems().setAll(Arrays.asList(DisplayOption.class.getEnumConstants()));
        hierarchyDisplayOption.setValue(recordGlobal.getHierarchyDisplayOption());
        hierarchyDisplayOption.getSelectionModel().selectedItemProperty().addListener(new DisplayOptionListener());

        // CSS analyzer column order
        cssAnalyzerColumnsOrder.getItems().setAll(Arrays.asList(CSSAnalyzerColumnsOrder.class.getEnumConstants()));
        cssAnalyzerColumnsOrder.setValue(recordGlobal.getCSSAnalyzerColumnsOrder());
        cssAnalyzerColumnsOrder.getSelectionModel().selectedItemProperty().addListener(new ColumnOrderListener());

        // Theme and Gluon Theme
        themes.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themes.setValue(recordGlobal.getTheme());
        themes.getSelectionModel().selectedItemProperty().addListener(new ThemesListener());

        List<GluonSwatch> gluonSwatches = Arrays.asList(GluonSwatch.class.getEnumConstants());
        // Sort alphabetically
        gluonSwatches.sort((s1, s2) -> s1.toString().compareTo(s2.toString()));
        gluonSwatch.getItems().setAll(gluonSwatches);
        gluonSwatch.setValue(recordGlobal.getSwatch());
        gluonSwatch.getSelectionModel().selectedItemProperty().addListener(new SwatchListener());
        
        // Number of open recent items
        recentItemsSize.getItems().setAll(recentItemsSizes);
        recentItemsSize.setValue(recordGlobal.getRecentItemsSize());
        recentItemsSize.getSelectionModel().selectedItemProperty().addListener(new RecentItemsSizeListener());

        // Accordion Animation
        animateAccordion.setSelected(recordGlobal.isAccordionAnimation());
        animateAccordion.selectedProperty().addListener(new AnimationListener());

        // Wildcard Imports
        wildcardImports.setSelected(recordGlobal.isWildcardImports());
        wildcardImports.selectedProperty().addListener(new WildcardImportListener());
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("prefs.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
        getStage().initOwner(ownerWindow);
        getStage().setResizable(false);
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        super.closeWindow();
    }

    @Override 
    public void onFocus() {}
    
    @FXML
    void resetToDefaultAction(ActionEvent event) {

        // Root container size
        rootContainerHeight.setText(String.valueOf(DEFAULT_ROOT_CONTAINER_HEIGHT));
        rootContainerHeight.getOnAction().handle(new ActionEvent());
        rootContainerWidth.setText(String.valueOf(DEFAULT_ROOT_CONTAINER_WIDTH));
        rootContainerWidth.getOnAction().handle(new ActionEvent());

        // Background image
        backgroundImage.setValue(BackgroundImagePreference.PREFERENCE_DEFAULT_VALUE);

        // Alignment guides color
        alignmentColorPicker.setPaintProperty(AlignmentGuidesColorPreference.PREFERENCE_DEFAULT_VALUE);

        // Parent ring color
        parentRingColorPicker.setPaintProperty(ParentRingColorPreference.PREFERENCE_DEFAULT_VALUE);

        // SceneBuilder theme
        scenebuilderTheme.setValue(ToolThemePreference.PREFERENCE_DEFAULT_VALUE);

        // Library view option
        libraryDisplayOption.setValue(DisplayModePreference.PREFERENCE_DEFAULT_VALUE);

        // Hierarchy display option
        hierarchyDisplayOption.setValue(DisplayOptionPreference.PREFERENCE_DEFAULT_VALUE);

        // CSS analyzer column order
        cssAnalyzerColumnsOrder.setValue(recordGlobal.getDefaultCSSAnalyzerColumnsOrder());

        // Number of open recent items
        recentItemsSize.setValue(RecentItemsSizePreference.PREFERENCE_DEFAULT_VALUE);

        // Default theme
        themes.setValue(ThemePreference.PREFERENCE_DEFAULT_VALUE);

        // Default Gluon swatch
        gluonSwatch.setValue(GluonSwatchPreference.PREFERENCE_DEFAULT_VALUE);

        // Default Accordion Animation
        animateAccordion.setSelected(AccordionAnimationPreference.PREFERENCE_DEFAULT_VALUE);

        // Default Wildcard import
        wildcardImports.setSelected(WildcardImportsPreference.PREFERENCE_DEFAULT_VALUE);
    }

    /**
     * *************************************************************************
     * Static inner class
     * *************************************************************************
     */
    private class BackgroundImageListener implements ChangeListener<BackgroundImage> {

        @Override
        public void changed(ObservableValue<? extends BackgroundImage> observable,
                BackgroundImage oldValue, BackgroundImage newValue) {
            // Update preferences
            backgroundImagePreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshBackgroundImage();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshBackgroundImage(recordGlobal));
        }
    }

    private class ToolThemeListener implements ChangeListener<ToolTheme> {

        @Override
        public void changed(ObservableValue<? extends ToolTheme> observable,
                ToolTheme oldValue, ToolTheme newValue) {
            // Update preferences
            toolThemePreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshToolTheme();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshToolTheme(recordGlobal));
        }
    }

    private class LibraryOptionListener implements ChangeListener<DISPLAY_MODE> {

        @Override
        public void changed(ObservableValue<? extends DISPLAY_MODE> ov, DISPLAY_MODE oldValue, DISPLAY_MODE newValue) {
            // Update preferences
            displayModePreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshLibraryDisplayOption();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshLibraryDisplayOption(recordGlobal));
        }
    }

    private class DisplayOptionListener implements ChangeListener<DisplayOption> {

        @Override
        public void changed(ObservableValue<? extends DisplayOption> observable,
                DisplayOption oldValue, DisplayOption newValue) {
            // Update preferences
            displayOptionPreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshHierarchyDisplayOption();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshHierarchyDisplayOption(recordGlobal));
        }
    }

    private class ColumnOrderListener implements ChangeListener<CSSAnalyzerColumnsOrder> {

        @Override
        public void changed(ObservableValue<? extends CSSAnalyzerColumnsOrder> observable,
                CSSAnalyzerColumnsOrder oldValue, CSSAnalyzerColumnsOrder newValue) {
            // Update preferences
        	//TODO replace indirect call to cssTableColumnsOrderingReversedPreference with cssAnalyzerColumnsOrderPreference
            recordGlobal.setCSSAnalyzerColumnsOrder(newValue);
            cssTableColumnsOrderingReversedPreference.writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshCSSAnalyzerColumnsOrder();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshCssTableColumnsOrderingReversed(recordGlobal));
        }
    }

    private class ThemesListener implements ChangeListener<Theme> {
        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            // Update preferences
            themePreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshTheme();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshTheme(recordGlobal));
        }
    }

    private class SwatchListener implements ChangeListener<GluonSwatch> {
        @Override
        public void changed(ObservableValue<? extends GluonSwatch> observable, GluonSwatch oldValue, GluonSwatch newValue) {
            // Update preferences
            gluonSwatchPreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshSwatch();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshSwatch(recordGlobal));
        }
    }

    private class GluonThemeListener implements ChangeListener<GluonTheme> {
        @Override
        public void changed(ObservableValue<? extends GluonTheme> observable, GluonTheme oldValue, GluonTheme newValue) {
            // Update preferences
            gluonThemePreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshGluonTheme();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshGluonTheme(recordGlobal));
        }
    }

    private class RecentItemsSizeListener implements ChangeListener<Integer> {

        @Override
        public void changed(ObservableValue<? extends Integer> observable,
                Integer oldValue, Integer newValue) {
            // Update preferences
            recordGlobal.setRecentItemsSize(newValue);
            recentItemsSizePreference.writeToJavaPreferences();
            recentItemsPreference.writeToJavaPreferences();
        }
    }

    private class AlignmentGuidesColorListener implements ChangeListener<Paint> {

        private final Rectangle graphic;

        public AlignmentGuidesColorListener(Rectangle graphic) {
            this.graphic = graphic;
        }

        @Override
        public void changed(ObservableValue<? extends Paint> ov, Paint oldValue, Paint newValue) {
            assert newValue instanceof Color;
            // Update preferences
            alignmentGuidesColorPreference.setValue((Color)newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshAlignmentGuidesColor();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshAlignmentGuidesColor(recordGlobal));
            graphic.setFill(newValue);
        }
    }

    private class ParentRingColorListener implements ChangeListener<Paint> {

        private final Rectangle graphic;

        public ParentRingColorListener(Rectangle graphic) {
            this.graphic = graphic;
        }

        @Override
        public void changed(ObservableValue<? extends Paint> ov, Paint oldValue, Paint newValue) {
            assert newValue instanceof Color;
            // Update preferences
            parentRingColorPreference.setValue((Color)newValue).writeToJavaPreferences();
            // Update UI
//            recordGlobal.refreshParentRingColor();
            MainController.applyToAllDocumentWindows(dwc -> dwc.refreshParentRingColor(recordGlobal));
            graphic.setFill(newValue);
        }
    }

    private static class PaintPickerDelegate implements PaintPicker.Delegate {

        @Override
        public void handleError(String warningKey, Object... arguments) {
            // Log a warning in message bar
        }
    }

    private class AnimationListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Update preferences
            accordionAnimationPreference.setValue(newValue).writeToJavaPreferences();
            // Update UI
            MainController.applyToAllDocumentWindows(dwc -> dwc.animateAccordion(newValue));
        }
    }

    private class WildcardImportListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            // Update preferences
            wildcardImportsPreference.setValue(newValue).writeToJavaPreferences();
        }
    }
}
