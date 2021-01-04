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
package com.oracle.javafx.scenebuilder.controls.metadata;

import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_3D;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_CHARTS;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_CONTAINERS;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_CONTROLS;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_MENU;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_MISCELLANEOUS;
import static com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames.TAG_SHAPES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

import javafx.geometry.Orientation;
import javafx.scene.Scene;

//@Component
public class ComponentClassMetadatas {

    private static final String FX8 = "FX8";
    private static final String HORIZONTAL = I18N.getString("label.qualifier.horizontal");
    private static final String VERTICAL = I18N.getString("label.qualifier.vertical");
    // Abstract Component Classes
    
    @Component
    public static class NodeMetadata extends ComponentClassMetadata<javafx.scene.Node> {
        protected NodeMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.Node.class, null);
            getProperties().add(valueCatalog.accessibleHelpPropertyMetadata);
            getProperties().add(valueCatalog.accessibleRole_PARENT_PropertyMetadata);
            getProperties().add(valueCatalog.accessibleRoleDescriptionPropertyMetadata);
            getProperties().add(valueCatalog.accessibleTextPropertyMetadata);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.blendModePropertyMetadata);
            getProperties().add(valueCatalog.boundsInLocalPropertyMetadata);
            getProperties().add(valueCatalog.boundsInParentPropertyMetadata);
            getProperties().add(valueCatalog.cachePropertyMetadata);
            getProperties().add(valueCatalog.cacheHintPropertyMetadata);
            getProperties().add(componentCatalog.clipPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.cursor_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.depthTestPropertyMetadata);
            getProperties().add(valueCatalog.disablePropertyMetadata);
            getProperties().add(valueCatalog.effectPropertyMetadata);
            getProperties().add(valueCatalog.effectiveNodeOrientationPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_false_PropertyMetadata);
            getProperties().add(valueCatalog.idPropertyMetadata);
            getProperties().add(valueCatalog.layoutBoundsPropertyMetadata);
            getProperties().add(valueCatalog.layoutXPropertyMetadata);
            getProperties().add(valueCatalog.layoutYPropertyMetadata);
            getProperties().add(valueCatalog.mouseTransparentPropertyMetadata);
            getProperties().add(valueCatalog.nodeOrientation_INHERIT_PropertyMetadata);
            getProperties().add(valueCatalog.onContextMenuRequestedPropertyMetadata);
            getProperties().add(valueCatalog.onDragDetectedPropertyMetadata);
            getProperties().add(valueCatalog.onDragDonePropertyMetadata);
            getProperties().add(valueCatalog.onDragDroppedPropertyMetadata);
            getProperties().add(valueCatalog.onDragEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onDragExitedPropertyMetadata);
            getProperties().add(valueCatalog.onDragOverPropertyMetadata);
            getProperties().add(valueCatalog.onInputMethodTextChangedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyPressedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyTypedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseClickedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragExitedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDraggedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragOverPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onMouseExitedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseMovedPropertyMetadata);
            getProperties().add(valueCatalog.onMousePressedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onRotatePropertyMetadata);
            getProperties().add(valueCatalog.onRotationFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onRotationStartedPropertyMetadata);
            getProperties().add(valueCatalog.onScrollPropertyMetadata);
            getProperties().add(valueCatalog.onScrollFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onScrollStartedPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeDownPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeLeftPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeRightPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeUpPropertyMetadata);
            getProperties().add(valueCatalog.onTouchMovedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchPressedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchStationaryPropertyMetadata);
            getProperties().add(valueCatalog.onZoomPropertyMetadata);
            getProperties().add(valueCatalog.onZoomFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onZoomStartedPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_true_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.rotatePropertyMetadata);
            getProperties().add(valueCatalog.rotationAxisPropertyMetadata);
            getProperties().add(valueCatalog.scaleXPropertyMetadata);
            getProperties().add(valueCatalog.scaleYPropertyMetadata);
            getProperties().add(valueCatalog.scaleZPropertyMetadata);
            getProperties().add(valueCatalog.stylePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_empty_PropertyMetadata);
            getProperties().add(valueCatalog.translateXPropertyMetadata);
            getProperties().add(valueCatalog.translateYPropertyMetadata);
            getProperties().add(valueCatalog.translateZPropertyMetadata);
            getProperties().add(valueCatalog.visiblePropertyMetadata);
            getProperties().add(valueCatalog.SplitPane_resizableWithParentPropertyMetadata);
            
//            getProperties().add(valueCatalog.AnchorPane_bottomAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_leftAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_rightAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_topAnchorPropertyMetadata);
            
            getProperties().add(valueCatalog.AnchorPane_AnchorPropertyGroupMetadata);
            
            getProperties().add(valueCatalog.BorderPane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.BorderPane_marginPropertyMetadata);
            getProperties().add(valueCatalog.FlowPane_marginPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_columnIndexPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_columnSpanPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_halignmentPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_hgrowPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_marginPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_rowIndexPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_rowSpanPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_valignmentPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_vgrowPropertyMetadata);
            getProperties().add(valueCatalog.HBox_hgrowPropertyMetadata);
            getProperties().add(valueCatalog.HBox_marginPropertyMetadata);
            getProperties().add(valueCatalog.StackPane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.StackPane_marginPropertyMetadata);
            getProperties().add(valueCatalog.TilePane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.TilePane_marginPropertyMetadata);
            getProperties().add(valueCatalog.VBox_marginPropertyMetadata);
            getProperties().add(valueCatalog.VBox_vgrowPropertyMetadata);
        }
    }

    @Component
    public static class ParentMetadata extends ComponentClassMetadata<javafx.scene.Parent> {
        protected ParentMetadata(@Autowired NodeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.Parent.class, parent);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.stylesheetsPropertyMetadata);
        }
    }

    @Component
    public static class RegionMetadata extends ComponentClassMetadata<javafx.scene.layout.Region> {
        protected RegionMetadata(@Autowired ParentMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.layout.Region.class, parent);
            getProperties().add(valueCatalog.cacheShapePropertyMetadata);
            getProperties().add(valueCatalog.centerShapePropertyMetadata);
            getProperties().add(valueCatalog.height_Double_ro_PropertyMetadata);
            getProperties().add(valueCatalog.insetsPropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.opaqueInsetsPropertyMetadata);
            getProperties().add(valueCatalog.paddingPropertyMetadata);
            getProperties().add(valueCatalog.prefHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.scaleShapePropertyMetadata);
            getProperties().add(componentCatalog.shapePropertyMetadata);
            getProperties().add(valueCatalog.snapToPixelPropertyMetadata);
            getProperties().add(valueCatalog.width_Double_ro_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Region.png"),
                            getClass().getResource("nodeicons/Region@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class PaneMetadata extends ComponentClassMetadata<javafx.scene.layout.Pane> {
        protected PaneMetadata(@Autowired RegionMetadata parent,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.layout.Pane.class, parent);
            getProperties().add(componentCatalog.children_empty_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Pane.png"),
                            getClass().getResource("nodeicons/Pane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class ControlMetadata extends ComponentClassMetadata<javafx.scene.control.Control> {
        protected ControlMetadata(@Autowired RegionMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Control.class, parent);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(componentCatalog.contextMenuPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c25_PropertyMetadata);
            getProperties().add(componentCatalog.tooltipPropertyMetadata);
        }
    }

    @Component
    public static class LabeledMetadata extends ComponentClassMetadata<javafx.scene.control.Labeled> {
        protected LabeledMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Labeled.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TOGGLE_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.alignment_CENTER_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.contentDisplayPropertyMetadata);
            getProperties().add(valueCatalog.ellipsisStringPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.fontPropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(valueCatalog.graphicTextGapPropertyMetadata);
            getProperties().add(valueCatalog.labelPaddingPropertyMetadata);
            getProperties().add(valueCatalog.lineSpacingPropertyMetadata);
            getProperties().add(valueCatalog.mnemonicParsing_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c41_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(valueCatalog.textAlignmentPropertyMetadata);
            getProperties().add(valueCatalog.textFillPropertyMetadata);
            getProperties().add(valueCatalog.textOverrunPropertyMetadata);
            getProperties().add(valueCatalog.underlinePropertyMetadata);
            getProperties().add(valueCatalog.wrapTextPropertyMetadata);
        }
    }

    @Component
    public static class ButtonBaseMetadata extends ComponentClassMetadata<javafx.scene.control.ButtonBase> {
        protected ButtonBaseMetadata(@Autowired LabeledMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ButtonBase.class, parent);
            getProperties().add(valueCatalog.accessibleRole_RADIO_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onActionPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c41_PropertyMetadata);
        }
    }

    @Component
    public static class ComboBoxBaseMetadata extends ComponentClassMetadata<javafx.scene.control.ComboBoxBase> {
        protected ComboBoxBaseMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ComboBoxBase.class, parent);
            getProperties().add(valueCatalog.accessibleRole_DATE_PICKER_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onActionPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.promptTextPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c5_PropertyMetadata);
            getProperties().add(valueCatalog.value_Object_PropertyMetadata);
        }
    }

    @Component
    public static class PopupWindowMetadata extends ComponentClassMetadata<javafx.stage.PopupWindow> {
        protected PopupWindowMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.stage.PopupWindow.class, null);
            getProperties().add(valueCatalog.anchorLocationPropertyMetadata);
            getProperties().add(valueCatalog.anchorXPropertyMetadata);
            getProperties().add(valueCatalog.anchorYPropertyMetadata);
            getProperties().add(valueCatalog.autoFixPropertyMetadata);
            getProperties().add(valueCatalog.autoHide_false_PropertyMetadata);
            getProperties().add(valueCatalog.consumeAutoHidingEventsPropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.hideOnEscapePropertyMetadata);
            getProperties().add(valueCatalog.onAutoHidePropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.x_NaN_PropertyMetadata);
            getProperties().add(valueCatalog.y_NaN_PropertyMetadata);
        }
    }

    @Component
    public static class PopupControlMetadata extends ComponentClassMetadata<javafx.scene.control.PopupControl> {
        protected PopupControlMetadata(@Autowired PopupWindowMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.PopupControl.class, parent);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.idPropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.prefHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.stylePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_empty_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.x_NaN_PropertyMetadata);
            getProperties().add(valueCatalog.y_NaN_PropertyMetadata);
        }
    }

    @Component
    public static class TextInputControlMetadata extends ComponentClassMetadata<javafx.scene.control.TextInputControl> {
        protected TextInputControlMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TextInputControl.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_FIELD_PropertyMetadata);
            getProperties().add(valueCatalog.editable_true_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.fontPropertyMetadata);
            getProperties().add(valueCatalog.length_Integer_ro_PropertyMetadata);
            getProperties().add(valueCatalog.promptTextPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c51_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(componentCatalog.textFormatterPropertyMetadata);
        }
    }

    @Component
    public static class TableColumnBaseMetadata extends ComponentClassMetadata<javafx.scene.control.TableColumnBase> {
        protected TableColumnBaseMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TableColumnBase.class, null);
            getProperties().add(componentCatalog.contextMenuPropertyMetadata);
            getProperties().add(valueCatalog.editable_true_PropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(valueCatalog.idPropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_500000_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_1000_PropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_8000_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.sortablePropertyMetadata);
            getProperties().add(componentCatalog.sortNodePropertyMetadata);
            getProperties().add(valueCatalog.stylePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c42_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(valueCatalog.visiblePropertyMetadata);
            getProperties().add(valueCatalog.width_Double_ro_PropertyMetadata);
        }
    }

    @Component
    public static class MenuItemMetadata extends ComponentClassMetadata<javafx.scene.control.MenuItem> {
        protected MenuItemMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.MenuItem.class, null);
            getProperties().add(valueCatalog.acceleratorPropertyMetadata);
            getProperties().add(valueCatalog.disablePropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(valueCatalog.idPropertyMetadata);
            getProperties().add(valueCatalog.mnemonicParsing_true_PropertyMetadata);
            getProperties().add(valueCatalog.onActionPropertyMetadata);
            getProperties().add(valueCatalog.onMenuValidationPropertyMetadata);
            getProperties().add(valueCatalog.stylePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c36_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(valueCatalog.visiblePropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/MenuItem.fxml"), null, null,
                            getClass().getResource("nodeicons/MenuItem.png"),
                            getClass().getResource("nodeicons/MenuItem@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class TextFieldMetadata extends ComponentClassMetadata<javafx.scene.control.TextField> {
        protected TextFieldMetadata(@Autowired TextInputControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.TextField.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_FIELD_PropertyMetadata);
            getProperties().add(valueCatalog.alignment_CENTER_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onActionPropertyMetadata);
            getProperties().add(valueCatalog.prefColumnCount_12_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c47_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/TextField.png"),
                            getClass().getResource("nodeicons/TextField@2x.png"), TAG_CONTROLS));
        }
    }

    // FIXME you can't add progressindicator from gluon and from javafx legacy
    // control together in the same fxml
    @Component
    public static class ProgressIndicatorMetadata
            extends ComponentClassMetadata<javafx.scene.control.ProgressIndicator> {
        protected ProgressIndicatorMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ProgressIndicator.class, parent);
            getProperties().add(valueCatalog.accessibleRole_PROGRESS_INDICATOR_PropertyMetadata);
            getProperties().add(valueCatalog.indeterminate_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.progressPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c50_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ProgressIndicator.fxml"), null, null,
                            getClass().getResource("nodeicons/ProgressIndicator.png"),
                            getClass().getResource("nodeicons/ProgressIndicator@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ToggleButtonMetadata extends ComponentClassMetadata<javafx.scene.control.ToggleButton> {
        protected ToggleButtonMetadata(@Autowired ButtonBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ToggleButton.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TOGGLE_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.alignment_CENTER_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.selected_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c44_PropertyMetadata);
            getProperties().add(valueCatalog.toggleGroupPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ToggleButton.fxml"), null, null,
                            getClass().getResource("nodeicons/ToggleButton.png"),
                            getClass().getResource("nodeicons/ToggleButton@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class AxisMetadata extends ComponentClassMetadata<javafx.scene.chart.Axis> {
        protected AxisMetadata(@Autowired RegionMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.Axis.class, parent);
            getProperties().add(valueCatalog.animatedPropertyMetadata);
            getProperties().add(valueCatalog.autoRangingPropertyMetadata);
            getProperties().add(valueCatalog.labelPropertyMetadata);
            getProperties().add(valueCatalog.side_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c45_PropertyMetadata);
            getProperties().add(valueCatalog.tickLabelFillPropertyMetadata);
            getProperties().add(valueCatalog.tickLabelFontPropertyMetadata);
            getProperties().add(valueCatalog.tickLabelGapPropertyMetadata);
            getProperties().add(valueCatalog.tickLabelRotationPropertyMetadata);
            getProperties().add(valueCatalog.tickLabelsVisiblePropertyMetadata);
            getProperties().add(valueCatalog.tickLengthPropertyMetadata);
            getProperties().add(valueCatalog.tickMarksPropertyMetadata);
            getProperties().add(valueCatalog.tickMarkVisiblePropertyMetadata);
            getProperties().add(valueCatalog.zeroPositionPropertyMetadata);
        }
    }

    @Component
    public static class ChartMetadata extends ComponentClassMetadata<javafx.scene.chart.Chart> {
        protected ChartMetadata(@Autowired RegionMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.Chart.class, parent);
            getProperties().add(valueCatalog.animatedPropertyMetadata);
            getProperties().add(valueCatalog.legendSidePropertyMetadata);
            getProperties().add(valueCatalog.legendVisiblePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);
            getProperties().add(valueCatalog.titlePropertyMetadata);
            getProperties().add(valueCatalog.titleSidePropertyMetadata);

            getQualifiers().put(Qualifier.HIDDEN,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Chart.png"),
                            getClass().getResource("nodeicons/Chart@2x.png"), null));
        }
    }

    @Component
    public static class ValueAxisMetadata extends ComponentClassMetadata<javafx.scene.chart.ValueAxis> {
        protected ValueAxisMetadata(@Autowired AxisMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.ValueAxis.class, parent);
            getProperties().add(valueCatalog.lowerBoundPropertyMetadata);
            getProperties().add(valueCatalog.minorTickCount_5_PropertyMetadata);
            getProperties().add(valueCatalog.minorTickLengthPropertyMetadata);
            getProperties().add(valueCatalog.minorTickVisiblePropertyMetadata);
            getProperties().add(valueCatalog.scalePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c45_PropertyMetadata);
            getProperties().add(valueCatalog.tickLabelFormatterPropertyMetadata);
            getProperties().add(valueCatalog.upperBoundPropertyMetadata);
            getProperties().add(valueCatalog.zeroPositionPropertyMetadata);
        }
    }

    @Component
    public static class XYChartMetadata extends ComponentClassMetadata<javafx.scene.chart.XYChart> {
        protected XYChartMetadata(@Autowired ChartMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.chart.XYChart.class, parent);
            getProperties().add(valueCatalog.alternativeColumnFillVisiblePropertyMetadata);
            getProperties().add(valueCatalog.alternativeRowFillVisiblePropertyMetadata);
            getProperties().add(valueCatalog.horizontalGridLinesVisiblePropertyMetadata);
            getProperties().add(valueCatalog.horizontalZeroLineVisiblePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);
            getProperties().add(valueCatalog.verticalGridLinesVisiblePropertyMetadata);
            getProperties().add(valueCatalog.verticalZeroLineVisiblePropertyMetadata);
            getProperties().add(componentCatalog.xAxisPropertyMetadata);
            getProperties().add(componentCatalog.yAxisPropertyMetadata);
        }
    }

    @Component
    public static class ShapeMetadata extends ComponentClassMetadata<javafx.scene.shape.Shape> {
        protected ShapeMetadata(@Autowired NodeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Shape.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.fill_BLACK_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.smoothPropertyMetadata);
            getProperties().add(valueCatalog.stroke_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.strokeDashOffsetPropertyMetadata);
            getProperties().add(valueCatalog.strokeLineCapPropertyMetadata);
            getProperties().add(valueCatalog.strokeLineJoinPropertyMetadata);
            getProperties().add(valueCatalog.strokeMiterLimitPropertyMetadata);
            getProperties().add(valueCatalog.strokeTypePropertyMetadata);
            getProperties().add(valueCatalog.strokeWidthPropertyMetadata);
        }
    }

    @Component
    public static class PathElementMetadata extends ComponentClassMetadata<javafx.scene.shape.PathElement> {
        protected PathElementMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.PathElement.class, null);
            getProperties().add(valueCatalog.absolutePropertyMetadata);
        }
    }

    @Component
    public static class CameraMetadata extends ComponentClassMetadata<javafx.scene.Camera> {
        protected CameraMetadata(@Autowired NodeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.Camera.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.farClipPropertyMetadata);
            getProperties().add(valueCatalog.nearClipPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
        }
    }

    @Component
    public static class LightBaseMetadata extends ComponentClassMetadata<javafx.scene.LightBase> {
        protected LightBaseMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.LightBase.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.colorPropertyMetadata);
            getProperties().add(valueCatalog.lightOnPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(componentCatalog.scopePropertyMetadata);
        }
    }

    @Component
    public static class Shape3DMetadata extends ComponentClassMetadata<javafx.scene.shape.Shape3D> {
        protected Shape3DMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Shape3D.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.cullFacePropertyMetadata);
            getProperties().add(valueCatalog.drawModePropertyMetadata);
            getProperties().add(valueCatalog.materialPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
        }
    }

    @Component
    public static class WindowMetadata extends ComponentClassMetadata<javafx.stage.Window> {
        protected WindowMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.stage.Window.class, null);
            getProperties().add(valueCatalog.height_Double_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.width_Double_COMPUTED_PropertyMetadata);
        }
    }

    // Other Component Classes (in alphabetical order)

    @Component
    public static class SwingNodeMetadata extends ComponentClassMetadata<javafx.embed.swing.SwingNode> {
        protected SwingNodeMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.embed.swing.SwingNode.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/SwingNode.png"),
                            getClass().getResource("nodeicons/SwingNode@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class AmbientLightMetadata extends ComponentClassMetadata<javafx.scene.AmbientLight> {
        protected AmbientLightMetadata(@Autowired LightBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.AmbientLight.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/AmbientLight.fxml"), null, FX8,
                            getClass().getResource("nodeicons/AmbientLight.png"),
                            getClass().getResource("nodeicons/AmbientLight@2x.png"), TAG_3D));
        }
    }

    @Component
    public static class GroupMetadata extends ComponentClassMetadata<javafx.scene.Group> {
        protected GroupMetadata(@Autowired ParentMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.Group.class, parent);
            getProperties().add(valueCatalog.autoSizeChildrenPropertyMetadata);
            getProperties().add(componentCatalog.children_empty_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Group.png"),
                            getClass().getResource("nodeicons/Group@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class ParallelCameraMetadata extends ComponentClassMetadata<javafx.scene.ParallelCamera> {
        protected ParallelCameraMetadata(@Autowired CameraMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.ParallelCamera.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/ParallelCamera.png"),
                            getClass().getResource("nodeicons/ParallelCamera@2x.png"), TAG_3D));
        }
    }

    @Component
    public static class PerspectiveCameraMetadata extends ComponentClassMetadata<javafx.scene.PerspectiveCamera> {
        protected PerspectiveCameraMetadata(@Autowired CameraMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.PerspectiveCamera.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.fieldOfViewPropertyMetadata);
            getProperties().add(valueCatalog.fixedEyeAtCameraZeroPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.verticalFieldOfViewPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/PerspectiveCamera.png"),
                            getClass().getResource("nodeicons/PerspectiveCamera@2x.png"), TAG_3D));
        }
    }

    @Component
    public static class PointLightMetadata extends ComponentClassMetadata<javafx.scene.PointLight> {
        protected PointLightMetadata(@Autowired LightBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.PointLight.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/PointLight.fxml"), null, FX8,
                            getClass().getResource("nodeicons/PointLight.png"),
                            getClass().getResource("nodeicons/PointLight@2x.png"), TAG_3D));
        }
    }

    @Component
    public static class SubSceneMetadata extends ComponentClassMetadata<javafx.scene.SubScene> {
        protected SubSceneMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.SubScene.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.fill_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SubScene.fxml"), null, FX8,
                            getClass().getResource("nodeicons/SubScene.png"),
                            getClass().getResource("nodeicons/SubScene@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class CanvasMetadata extends ComponentClassMetadata<javafx.scene.canvas.Canvas> {
        protected CanvasMetadata(@Autowired NodeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.canvas.Canvas.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Canvas.fxml"), null, null,
                            getClass().getResource("nodeicons/Canvas.png"),
                            getClass().getResource("nodeicons/Canvas@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class AreaChartMetadata extends ComponentClassMetadata<javafx.scene.chart.AreaChart> {
        protected AreaChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.AreaChart.class, parent);
            getProperties().add(valueCatalog.createSymbolsPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/AreaChart.fxml"), null, null,
                            getClass().getResource("nodeicons/AreaChart.png"),
                            getClass().getResource("nodeicons/AreaChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class BarChartMetadata extends ComponentClassMetadata<javafx.scene.chart.BarChart> {
        protected BarChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.BarChart.class, parent);
            getProperties().add(valueCatalog.barGapPropertyMetadata);
            getProperties().add(valueCatalog.categoryGapPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c1_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/BarChart.fxml"), null, null,
                            getClass().getResource("nodeicons/BarChart.png"),
                            getClass().getResource("nodeicons/BarChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class BubbleChartMetadata extends ComponentClassMetadata<javafx.scene.chart.BubbleChart> {
        protected BubbleChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.BubbleChart.class, parent);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/BubbleChart.fxml"), null, null,
                            getClass().getResource("nodeicons/BubbleChart.png"),
                            getClass().getResource("nodeicons/BubbleChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class CategoryAxisMetadata extends ComponentClassMetadata<javafx.scene.chart.CategoryAxis> {
        protected CategoryAxisMetadata(@Autowired AxisMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.CategoryAxis.class, parent);
            getProperties().add(valueCatalog.categoriesPropertyMetadata);
            getProperties().add(valueCatalog.categorySpacingPropertyMetadata);
            getProperties().add(valueCatalog.endMarginPropertyMetadata);
            getProperties().add(valueCatalog.gapStartAndEndPropertyMetadata);
            getProperties().add(valueCatalog.startMarginPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c45_PropertyMetadata);
            getProperties().add(valueCatalog.zeroPositionPropertyMetadata);

            getQualifiers().put(Qualifier.HIDDEN,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/CategoryAxis.png"),
                            getClass().getResource("nodeicons/CategoryAxis@2x.png"), null));
        }
    }

    @Component
    public static class LineChartMetadata extends ComponentClassMetadata<javafx.scene.chart.LineChart> {
        protected LineChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.LineChart.class, parent);
            getProperties().add(valueCatalog.axisSortingPolicyPropertyMetadata);
            getProperties().add(valueCatalog.createSymbolsPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/LineChart.fxml"), null, null,
                            getClass().getResource("nodeicons/LineChart.png"),
                            getClass().getResource("nodeicons/LineChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class NumberAxisMetadata extends ComponentClassMetadata<javafx.scene.chart.NumberAxis> {
        protected NumberAxisMetadata(@Autowired ValueAxisMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.NumberAxis.class, parent);
            getProperties().add(valueCatalog.forceZeroInRangePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c45_PropertyMetadata);
            getProperties().add(valueCatalog.tickUnitPropertyMetadata);

            getQualifiers().put(Qualifier.HIDDEN,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/NumberAxis.png"),
                            getClass().getResource("nodeicons/NumberAxis@2x.png"), null));
        }
    }

    @Component
    public static class PieChartMetadata extends ComponentClassMetadata<javafx.scene.chart.PieChart> {
        protected PieChartMetadata(@Autowired ChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.PieChart.class, parent);
            getProperties().add(valueCatalog.clockwisePropertyMetadata);
            getProperties().add(valueCatalog.labelLineLengthPropertyMetadata);
            getProperties().add(valueCatalog.labelsVisiblePropertyMetadata);
            getProperties().add(valueCatalog.startAnglePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/PieChart.png"),
                            getClass().getResource("nodeicons/PieChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class ScatterChartMetadata extends ComponentClassMetadata<javafx.scene.chart.ScatterChart> {
        protected ScatterChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.ScatterChart.class, parent);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ScatterChart.fxml"), null, null,
                            getClass().getResource("nodeicons/ScatterChart.png"),
                            getClass().getResource("nodeicons/ScatterChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class StackedAreaChartMetadata extends ComponentClassMetadata<javafx.scene.chart.StackedAreaChart> {
        protected StackedAreaChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.StackedAreaChart.class, parent);
            getProperties().add(valueCatalog.createSymbolsPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c37_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/StackedAreaChart.fxml"), null, null,
                            getClass().getResource("nodeicons/StackedAreaChart.png"),
                            getClass().getResource("nodeicons/StackedAreaChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class StackedBarChartMetadata extends ComponentClassMetadata<javafx.scene.chart.StackedBarChart> {
        protected StackedBarChartMetadata(@Autowired XYChartMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.chart.StackedBarChart.class, parent);
            getProperties().add(valueCatalog.categoryGapPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c12_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/StackedBarChart.fxml"), null, null,
                            getClass().getResource("nodeicons/StackedBarChart.png"),
                            getClass().getResource("nodeicons/StackedBarChart@2x.png"), TAG_CHARTS));
        }
    }

    @Component
    public static class AccordionMetadata extends ComponentClassMetadata<javafx.scene.control.Accordion> {
        protected AccordionMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Accordion.class, parent);
            getProperties().add(componentCatalog.panesPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c4_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Accordion.fxml"), null, null,
                            getClass().getResource("nodeicons/Accordion.png"),
                            getClass().getResource("nodeicons/Accordion@2x.png"), TAG_CONTAINERS));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(getClass().getResource("fxml/AccordionEmpty.fxml"), Qualifier.EMPTY, null,
                            getClass().getResource("nodeicons/Accordion.png"),
                            getClass().getResource("nodeicons/Accordion@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class ButtonMetadata extends ComponentClassMetadata<javafx.scene.control.Button> {
        protected ButtonMetadata(@Autowired ButtonBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Button.class, parent);
            getProperties().add(valueCatalog.accessibleRole_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.cancelButtonPropertyMetadata);
            getProperties().add(valueCatalog.defaultButtonPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c17_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Button.fxml"), null, null,
                            getClass().getResource("nodeicons/Button.png"),
                            getClass().getResource("nodeicons/Button@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ButtonBarMetadata extends ComponentClassMetadata<javafx.scene.control.ButtonBar> {
        protected ButtonBarMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ButtonBar.class, parent);
            getProperties().add(valueCatalog.buttonMinWidthPropertyMetadata);
            getProperties().add(valueCatalog.buttonOrderPropertyMetadata);
            getProperties().add(componentCatalog.buttonsPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c35_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ButtonBar.fxml"), null, FX8,
                            getClass().getResource("nodeicons/ButtonBar.png"),
                            getClass().getResource("nodeicons/ButtonBar@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class CheckBoxMetadata extends ComponentClassMetadata<javafx.scene.control.CheckBox> {
        protected CheckBoxMetadata(@Autowired ButtonBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.CheckBox.class, parent);
            getProperties().add(valueCatalog.accessibleRole_CHECK_BOX_PropertyMetadata);
            getProperties().add(valueCatalog.allowIndeterminatePropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.indeterminate_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.selected_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c10_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/CheckBox.fxml"), null, null,
                            getClass().getResource("nodeicons/CheckBox.png"),
                            getClass().getResource("nodeicons/CheckBox@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class CheckMenuItemMetadata extends ComponentClassMetadata<javafx.scene.control.CheckMenuItem> {
        protected CheckMenuItemMetadata(@Autowired MenuItemMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.CheckMenuItem.class, parent);
            getProperties().add(valueCatalog.selected_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c28_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/CheckMenuItem.fxml"), null, null,
                            getClass().getResource("nodeicons/CheckMenuItem.png"),
                            getClass().getResource("nodeicons/CheckMenuItem@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class ChoiceBoxMetadata extends ComponentClassMetadata<javafx.scene.control.ChoiceBox> {
        protected ChoiceBoxMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ChoiceBox.class, parent);
            getProperties().add(valueCatalog.accessibleRole_COMBO_BOX_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c43_PropertyMetadata);
            getProperties().add(valueCatalog.value_Object_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ChoiceBox.fxml"), null, null,
                            getClass().getResource("nodeicons/ChoiceBox.png"),
                            getClass().getResource("nodeicons/ChoiceBox@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ColorPickerMetadata extends ComponentClassMetadata<javafx.scene.control.ColorPicker> {
        protected ColorPickerMetadata(@Autowired ComboBoxBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ColorPicker.class, parent);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c5_PropertyMetadata);
            getProperties().add(valueCatalog.value_Color_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ColorPicker.fxml"), null, null,
                            getClass().getResource("nodeicons/ColorPicker.png"),
                            getClass().getResource("nodeicons/ColorPicker@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ComboBoxMetadata extends ComponentClassMetadata<javafx.scene.control.ComboBox> {
        protected ComboBoxMetadata(@Autowired ComboBoxBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ComboBox.class, parent);
            getProperties().add(valueCatalog.accessibleRole_COMBO_BOX_PropertyMetadata);
            getProperties().add(valueCatalog.buttonCellPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(componentCatalog.placeholderPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c11_PropertyMetadata);
            getProperties().add(valueCatalog.visibleRowCountPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ComboBox.fxml"), null, null,
                            getClass().getResource("nodeicons/ComboBox.png"),
                            getClass().getResource("nodeicons/ComboBox@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ContextMenuMetadata extends ComponentClassMetadata<javafx.scene.control.ContextMenu> {
        protected ContextMenuMetadata(@Autowired PopupControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ContextMenu.class, parent);
            getProperties().add(valueCatalog.autoHide_true_PropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(componentCatalog.items_MenuItem_PropertyMetadata);
            getProperties().add(valueCatalog.onActionPropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c8_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.x_NaN_PropertyMetadata);
            getProperties().add(valueCatalog.y_NaN_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ContextMenu.fxml"), null, null,
                            getClass().getResource("nodeicons/ContextMenu.png"),
                            getClass().getResource("nodeicons/ContextMenu@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class CustomMenuItemMetadata extends ComponentClassMetadata<javafx.scene.control.CustomMenuItem> {
        protected CustomMenuItemMetadata(@Autowired MenuItemMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.CustomMenuItem.class, parent);
            getProperties().add(componentCatalog.content_Node_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.hideOnClick_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c27_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/CustomMenuItem.fxml"), null, null,
                            getClass().getResource("nodeicons/CustomMenuItem.png"),
                            getClass().getResource("nodeicons/CustomMenuItem@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class DatePickerMetadata extends ComponentClassMetadata<javafx.scene.control.DatePicker> {
        protected DatePickerMetadata(@Autowired ComboBoxBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.DatePicker.class, parent);
            getProperties().add(valueCatalog.accessibleRole_DATE_PICKER_PropertyMetadata);
            getProperties().add(valueCatalog.editable_true_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.showWeekNumbersPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c9_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/DatePicker.png"),
                            getClass().getResource("nodeicons/DatePicker@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class DialogPaneMetadata extends ComponentClassMetadata<javafx.scene.control.DialogPane> {
        protected DialogPaneMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.DialogPane.class, parent);
            getProperties().add(valueCatalog.buttonTypesPropertyMetadata);
            getProperties().add(componentCatalog.children_c1_PropertyMetadata);
            getProperties().add(componentCatalog.content_Node_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.contentTextPropertyMetadata);
            getProperties().add(componentCatalog.expandableContentPropertyMetadata);
            getProperties().add(valueCatalog.expanded_false_PropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(componentCatalog.headerPropertyMetadata);
            getProperties().add(valueCatalog.headerTextPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c30_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/DialogPane.fxml"), null, FX8,
                            getClass().getResource("nodeicons/DialogPane.png"),
                            getClass().getResource("nodeicons/DialogPane@2x.png"), TAG_CONTAINERS));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(null, Qualifier.EMPTY, FX8, getClass().getResource("nodeicons/DialogPane.png"),
                            getClass().getResource("nodeicons/DialogPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class HyperlinkMetadata extends ComponentClassMetadata<javafx.scene.control.Hyperlink> {
        protected HyperlinkMetadata(@Autowired ButtonBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Hyperlink.class, parent);
            getProperties().add(valueCatalog.accessibleRole_HYPERLINK_PropertyMetadata);
            getProperties().add(valueCatalog.cursor_HAND_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.mnemonicParsing_false_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c25_PropertyMetadata);
            getProperties().add(valueCatalog.visitedPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Hyperlink.fxml"), null, null,
                            getClass().getResource("nodeicons/Hyperlink.png"),
                            getClass().getResource("nodeicons/Hyperlink@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class LabelMetadata extends ComponentClassMetadata<javafx.scene.control.Label> {
        protected LabelMetadata(@Autowired LabeledMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Label.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_PropertyMetadata);
            getProperties().add(componentCatalog.labelForPropertyMetadata);
            getProperties().add(valueCatalog.mnemonicParsing_false_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c3_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Label.fxml"), null, null,
                            getClass().getResource("nodeicons/Label.png"),
                            getClass().getResource("nodeicons/Label@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ListViewMetadata extends ComponentClassMetadata<javafx.scene.control.ListView> {
        protected ListViewMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ListView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_LIST_VIEW_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.fixedCellSizePropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onEditCancelPropertyMetadata);
            getProperties().add(valueCatalog.onEditCommitPropertyMetadata);
            getProperties().add(valueCatalog.onEditStartPropertyMetadata);
            getProperties().add(valueCatalog.onScrollToPropertyMetadata);
            getProperties().add(valueCatalog.orientation_VERTICAL_PropertyMetadata);
            getProperties().add(componentCatalog.placeholderPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c34_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/ListView.png"),
                            getClass().getResource("nodeicons/ListView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class MenuMetadata extends ComponentClassMetadata<javafx.scene.control.Menu> {
        protected MenuMetadata(@Autowired MenuItemMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Menu.class, parent);
            getProperties().add(componentCatalog.items_MenuItem_PropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c29_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Menu.fxml"), null, null,
                            getClass().getResource("nodeicons/Menu.png"),
                            getClass().getResource("nodeicons/Menu@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class MenuBarMetadata extends ComponentClassMetadata<javafx.scene.control.MenuBar> {
        protected MenuBarMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.MenuBar.class, parent);
            getProperties().add(valueCatalog.accessibleRole_MENU_BAR_PropertyMetadata);
            getProperties().add(componentCatalog.menusPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c18_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/MenuBar.fxml"), null, null,
                            getClass().getResource("nodeicons/MenuBar.png"),
                            getClass().getResource("nodeicons/MenuBar@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class MenuButtonMetadata extends ComponentClassMetadata<javafx.scene.control.MenuButton> {
        protected MenuButtonMetadata(@Autowired ButtonBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.MenuButton.class, parent);
            getProperties().add(valueCatalog.accessibleRole_MENU_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(componentCatalog.items_MenuItem_PropertyMetadata);
            getProperties().add(valueCatalog.popupSidePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c52_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/MenuButton.fxml"), null, null,
                            getClass().getResource("nodeicons/MenuButton.png"),
                            getClass().getResource("nodeicons/MenuButton@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class PaginationMetadata extends ComponentClassMetadata<javafx.scene.control.Pagination> {
        protected PaginationMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Pagination.class, parent);
            getProperties().add(valueCatalog.accessibleRole_PAGINATION_PropertyMetadata);
            getProperties().add(valueCatalog.currentPageIndexPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.maxPageIndicatorCountPropertyMetadata);
            getProperties().add(valueCatalog.pageCountPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c39_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Pagination.png"),
                            getClass().getResource("nodeicons/Pagination@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class PasswordFieldMetadata extends ComponentClassMetadata<javafx.scene.control.PasswordField> {
        protected PasswordFieldMetadata(@Autowired TextFieldMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.PasswordField.class, parent);
            getProperties().add(valueCatalog.accessibleRole_PASSWORD_FIELD_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c53_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/PasswordField.png"),
                            getClass().getResource("nodeicons/PasswordField@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ProgressBarMetadata extends ComponentClassMetadata<javafx.scene.control.ProgressBar> {
        protected ProgressBarMetadata(@Autowired ProgressIndicatorMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ProgressBar.class, parent);
            getProperties().add(valueCatalog.accessibleRole_PROGRESS_INDICATOR_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c13_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ProgressBar.fxml"), null, null,
                            getClass().getResource("nodeicons/ProgressBar.png"),
                            getClass().getResource("nodeicons/ProgressBar@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class RadioButtonMetadata extends ComponentClassMetadata<javafx.scene.control.RadioButton> {
        protected RadioButtonMetadata(@Autowired ToggleButtonMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.RadioButton.class, parent);
            getProperties().add(valueCatalog.accessibleRole_RADIO_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c41_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/RadioButton.fxml"), null, null,
                            getClass().getResource("nodeicons/RadioButton.png"),
                            getClass().getResource("nodeicons/RadioButton@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class RadioMenuItemMetadata extends ComponentClassMetadata<javafx.scene.control.RadioMenuItem> {
        protected RadioMenuItemMetadata(@Autowired MenuItemMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.RadioMenuItem.class, parent);
            getProperties().add(valueCatalog.selected_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c7_PropertyMetadata);
            getProperties().add(valueCatalog.toggleGroupPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/RadioMenuItem.fxml"), null, null,
                            getClass().getResource("nodeicons/RadioMenuItem.png"),
                            getClass().getResource("nodeicons/RadioMenuItem@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class SceneMetadata extends ComponentClassMetadata<Scene> {
        protected SceneMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.Scene.class, null);
            getProperties().add(valueCatalog.fill_WHITE_PropertyMetadata);
            getProperties().add(valueCatalog.onContextMenuRequestedPropertyMetadata);
            getProperties().add(valueCatalog.onDragDetectedPropertyMetadata);
            getProperties().add(valueCatalog.onDragDonePropertyMetadata);
            getProperties().add(valueCatalog.onDragDroppedPropertyMetadata);
            getProperties().add(valueCatalog.onDragEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onDragExitedPropertyMetadata);
            getProperties().add(valueCatalog.onDragOverPropertyMetadata);
            getProperties().add(valueCatalog.onInputMethodTextChangedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyPressedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onKeyTypedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseClickedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragExitedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDraggedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragOverPropertyMetadata);
            getProperties().add(valueCatalog.onMouseDragReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseEnteredPropertyMetadata);
            getProperties().add(valueCatalog.onMouseExitedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseMovedPropertyMetadata);
            getProperties().add(valueCatalog.onMousePressedPropertyMetadata);
            getProperties().add(valueCatalog.onMouseReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onRotatePropertyMetadata);
            getProperties().add(valueCatalog.onRotationFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onRotationStartedPropertyMetadata);
            getProperties().add(valueCatalog.onScrollFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onScrollPropertyMetadata);
            getProperties().add(valueCatalog.onScrollStartedPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeDownPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeLeftPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeRightPropertyMetadata);
            getProperties().add(valueCatalog.onSwipeUpPropertyMetadata);
            getProperties().add(valueCatalog.onTouchMovedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchPressedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchReleasedPropertyMetadata);
            getProperties().add(valueCatalog.onTouchStationaryPropertyMetadata);
            getProperties().add(valueCatalog.onZoomFinishedPropertyMetadata);
            getProperties().add(valueCatalog.onZoomPropertyMetadata);
            getProperties().add(valueCatalog.onZoomStartedPropertyMetadata);
            getProperties().add(componentCatalog.root_scene_PropertyMetadata);
            getProperties().add(valueCatalog.stylesheetsPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Scene.fxml"), null, null,
                            getClass().getResource("nodeicons/Scene.png"),
                            getClass().getResource("nodeicons/Scene@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class ScrollBarMetadata extends ComponentClassMetadata<javafx.scene.control.ScrollBar> {
        protected ScrollBarMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.ScrollBar.class, parent);
            getProperties().add(valueCatalog.accessibleRole_SCROLL_BAR_PropertyMetadata);
            getProperties().add(valueCatalog.blockIncrementPropertyMetadata);
            getProperties().add(valueCatalog.maxPropertyMetadata);
            getProperties().add(valueCatalog.minPropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c33_PropertyMetadata);
            getProperties().add(valueCatalog.unitIncrementPropertyMetadata);
            getProperties().add(valueCatalog.value_Double_PropertyMetadata);
            getProperties().add(valueCatalog.visibleAmountPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ScrollBarH.fxml"), HORIZONTAL, null,
                            getClass().getResource("nodeicons/ScrollBar-h.png"),
                            getClass().getResource("nodeicons/ScrollBar-h@2x.png"), TAG_CONTROLS));

            getQualifiers().put(VERTICAL,
                    new Qualifier(getClass().getResource("fxml/ScrollBarV.fxml"), VERTICAL, null,
                            getClass().getResource("nodeicons/ScrollBar-v.png"),
                            getClass().getResource("nodeicons/ScrollBar-v@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ScrollPaneMetadata extends ComponentClassMetadata<javafx.scene.control.ScrollPane> {
        protected ScrollPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ScrollPane.class, parent);
            getProperties().add(valueCatalog.accessibleRole_SCROLL_PANE_PropertyMetadata);
            getProperties().add(componentCatalog.content_Node_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.fitToHeightPropertyMetadata);
            getProperties().add(valueCatalog.fitToWidthPropertyMetadata);
            getProperties().add(valueCatalog.hbarPolicyPropertyMetadata);
            
            //getProperties().add(valueCatalog.hmaxPropertyMetadata);
            //getProperties().add(valueCatalog.hminPropertyMetadata);
            //getProperties().add(valueCatalog.hvaluePropertyMetadata);
            getProperties().add(valueCatalog.hGroupPropertyMetadata);
            
            getProperties().add(valueCatalog.minViewportHeightPropertyMetadata);
            getProperties().add(valueCatalog.minViewportWidthPropertyMetadata);
            getProperties().add(valueCatalog.pannablePropertyMetadata);
            getProperties().add(valueCatalog.prefViewportHeightPropertyMetadata);
            getProperties().add(valueCatalog.prefViewportWidthPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c38_PropertyMetadata);
            getProperties().add(valueCatalog.vbarPolicyPropertyMetadata);
            getProperties().add(valueCatalog.viewportBoundsPropertyMetadata);
            
            //getProperties().add(valueCatalog.vmaxPropertyMetadata);
            //getProperties().add(valueCatalog.vminPropertyMetadata);
            //getProperties().add(valueCatalog.vvaluePropertyMetadata);
            getProperties().add(valueCatalog.vGroupPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ScrollPane.fxml"), null, null,
                            getClass().getResource("nodeicons/ScrollPane.png"),
                            getClass().getResource("nodeicons/ScrollPane@2x.png"), TAG_CONTAINERS));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(null, Qualifier.EMPTY, null, getClass().getResource("nodeicons/ScrollPane.png"),
                            getClass().getResource("nodeicons/ScrollPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class SeparatorMetadata extends ComponentClassMetadata<javafx.scene.control.Separator> {
        protected SeparatorMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Separator.class, parent);
            getProperties().add(valueCatalog.halignment_CENTER_PropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c31_PropertyMetadata);
            getProperties().add(valueCatalog.valignment_CENTER_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SeparatorH.fxml"), HORIZONTAL, null,
                            getClass().getResource("nodeicons/Separator-h.png"),
                            getClass().getResource("nodeicons/Separator-h@2x.png"), TAG_CONTROLS));

            getQualifiers().put(VERTICAL,
                    new Qualifier(getClass().getResource("fxml/SeparatorV.fxml"), VERTICAL, null,
                            getClass().getResource("nodeicons/Separator-v.png"),
                            getClass().getResource("nodeicons/Separator-v@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class SeparatorMenuItemMetadata
            extends ComponentClassMetadata<javafx.scene.control.SeparatorMenuItem> {
        protected SeparatorMenuItemMetadata(@Autowired CustomMenuItemMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.SeparatorMenuItem.class, parent);
            getProperties().add(componentCatalog.content_Node_SEPARATOR_PropertyMetadata);
            getProperties().add(valueCatalog.hideOnClick_false_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c23_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SeparatorMenuItem.fxml"), null, null,
                            getClass().getResource("nodeicons/SeparatorMenuItem.png"),
                            getClass().getResource("nodeicons/SeparatorMenuItem@2x.png"), TAG_MENU));
        }
    }

    @Component
    public static class SliderMetadata extends ComponentClassMetadata<javafx.scene.control.Slider> {
        protected SliderMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Slider.class, parent);
            getProperties().add(valueCatalog.accessibleRole_SLIDER_PropertyMetadata);
            getProperties().add(valueCatalog.blockIncrementPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.labelFormatterPropertyMetadata);
            getProperties().add(valueCatalog.majorTickUnitPropertyMetadata);
            getProperties().add(valueCatalog.maxPropertyMetadata);
            getProperties().add(valueCatalog.minPropertyMetadata);
            getProperties().add(valueCatalog.minorTickCount_3_PropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.showTickLabelsPropertyMetadata);
            getProperties().add(valueCatalog.showTickMarksPropertyMetadata);
            getProperties().add(valueCatalog.snapToTicksPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c40_PropertyMetadata);
            getProperties().add(valueCatalog.value_Double_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SliderH.fxml"), HORIZONTAL, null,
                            getClass().getResource("nodeicons/Slider-h.png"),
                            getClass().getResource("nodeicons/Slider-h@2x.png"), TAG_CONTROLS));

            getQualifiers().put(VERTICAL,
                    new Qualifier(getClass().getResource("fxml/SliderV.fxml"), VERTICAL, null,
                            getClass().getResource("nodeicons/Slider-v.png"),
                            getClass().getResource("nodeicons/Slider-v@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class SpinnerMetadata extends ComponentClassMetadata<javafx.scene.control.Spinner> {
        protected SpinnerMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.Spinner.class, parent);
            getProperties().add(valueCatalog.accessibleRole_SPINNER_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c24_PropertyMetadata);
            getProperties().add(valueCatalog.value_Object_ro_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/Spinner.png"),
                            getClass().getResource("nodeicons/Spinner@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class SplitMenuButtonMetadata extends ComponentClassMetadata<javafx.scene.control.SplitMenuButton> {
        protected SplitMenuButtonMetadata(@Autowired MenuButtonMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.SplitMenuButton.class, parent);
            getProperties().add(valueCatalog.accessibleRole_SPLIT_MENU_BUTTON_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c2_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SplitMenuButton.fxml"), null, null,
                            getClass().getResource("nodeicons/SplitMenuButton.png"),
                            getClass().getResource("nodeicons/SplitMenuButton@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class SplitPaneMetadata extends ComponentClassMetadata<javafx.scene.control.SplitPane> {
        protected SplitPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.SplitPane.class, parent);
            getProperties().add(valueCatalog.dividerPositionsPropertyMetadata);
            getProperties().add(componentCatalog.items_Node_PropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c14_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SplitPaneH.fxml"), HORIZONTAL, null,
                            getClass().getResource("nodeicons/SplitPane-h.png"),
                            getClass().getResource("nodeicons/SplitPane-h@2x.png"), TAG_CONTAINERS,
                            (javafx.scene.control.SplitPane o) -> o.getOrientation() == Orientation.HORIZONTAL));

            getQualifiers().put(VERTICAL,
                    new Qualifier(getClass().getResource("fxml/SplitPaneV.fxml"), VERTICAL, null,
                            getClass().getResource("nodeicons/SplitPane-v.png"),
                            getClass().getResource("nodeicons/SplitPane-v@2x.png"), TAG_CONTAINERS,
                            (javafx.scene.control.SplitPane o) -> o.getOrientation() == Orientation.VERTICAL));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(null, Qualifier.EMPTY, null, getClass().getResource("nodeicons/SplitPane-h.png"),
                            getClass().getResource("nodeicons/SplitPane-h@2x.png"), TAG_CONTAINERS,
                            (javafx.scene.control.SplitPane o) -> o.getOrientation() == Orientation.HORIZONTAL));
        }
    }

    @Component
    public static class StageMetadata extends ComponentClassMetadata<javafx.stage.Stage> {
        protected StageMetadata(@Autowired WindowMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.stage.Stage.class, parent);
            getProperties().add(valueCatalog.alwaysOnTopPropertyMetadata);
            getProperties().add(componentCatalog.scene_stage_PropertyMetadata);
            getProperties().add(valueCatalog.fullScreenPropertyMetadata);
            getProperties().add(valueCatalog.fullScreenExitHintPropertyMetadata);
            getProperties().add(valueCatalog.iconifiedPropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_SIZE_PropertyMetadata);
            getProperties().add(valueCatalog.maximizedPropertyMetdata);
            getProperties().add(valueCatalog.maxWidth_SIZE_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_SIZE_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_SIZE_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_PropertyMetadata);
            getProperties().add(valueCatalog.titlePropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Stage.fxml"), null, null,
                            getClass().getResource("nodeicons/Stage.png"),
                            getClass().getResource("nodeicons/Stage@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class TabMetadata extends ComponentClassMetadata<javafx.scene.control.Tab> {
        protected TabMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Tab.class, null);
            getProperties().add(valueCatalog.closablePropertyMetadata);
            getProperties().add(componentCatalog.content_Node_NULL_PropertyMetadata);
            getProperties().add(componentCatalog.contextMenuPropertyMetadata);
            getProperties().add(valueCatalog.disablePropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(valueCatalog.idPropertyMetadata);
            getProperties().add(valueCatalog.onClosedPropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onSelectionChangedPropertyMetadata);
            getProperties().add(valueCatalog.selected_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.stylePropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c19_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(componentCatalog.tooltipPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Tab.fxml"), null, null,
                            getClass().getResource("nodeicons/Tab.png"), getClass().getResource("nodeicons/Tab@2x.png"),
                            TAG_CONTAINERS));
        }
    }

    @Component
    public static class TabPaneMetadata extends ComponentClassMetadata<javafx.scene.control.TabPane> {
        protected TabPaneMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TabPane.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TAB_PANE_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.rotateGraphicPropertyMetadata);
            getProperties().add(valueCatalog.side_TOP_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c6_PropertyMetadata);
            getProperties().add(valueCatalog.tabClosingPolicyPropertyMetadata);
            getProperties().add(valueCatalog.tabMaxHeightPropertyMetadata);
            getProperties().add(valueCatalog.tabMaxWidthPropertyMetadata);
            getProperties().add(valueCatalog.tabMinHeightPropertyMetadata);
            getProperties().add(valueCatalog.tabMinWidthPropertyMetadata);
            getProperties().add(componentCatalog.tabsPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TabPane.fxml"), null, null,
                            getClass().getResource("nodeicons/TabPane.png"),
                            getClass().getResource("nodeicons/TabPane@2x.png"), TAG_CONTAINERS));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(null, Qualifier.EMPTY, null, getClass().getResource("nodeicons/TabPane.png"),
                            getClass().getResource("nodeicons/TabPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class TableColumnMetadata extends ComponentClassMetadata<javafx.scene.control.TableColumn> {
        protected TableColumnMetadata(@Autowired TableColumnBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TableColumn.class, parent);
            getProperties().add(componentCatalog.columns_TableColumn_PropertyMetadata);
            getProperties().add(valueCatalog.onEditCancelPropertyMetadata);
            getProperties().add(valueCatalog.onEditCommitPropertyMetadata);
            getProperties().add(valueCatalog.onEditStartPropertyMetadata);
            getProperties().add(valueCatalog.sortType_SortType_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TableColumn.fxml"), null, null,
                            getClass().getResource("nodeicons/TableColumn.png"),
                            getClass().getResource("nodeicons/TableColumn@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class TableViewMetadata extends ComponentClassMetadata<javafx.scene.control.TableView> {
        protected TableViewMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TableView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TABLE_VIEW_PropertyMetadata);
            getProperties().add(valueCatalog.columnResizePolicy_TABLEVIEW_UNCONSTRAINED_PropertyMetadata);
            getProperties().add(componentCatalog.columns_TableColumn_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.fixedCellSizePropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onScrollToPropertyMetadata);
            getProperties().add(valueCatalog.onScrollToColumnPropertyMetadata);
            getProperties().add(valueCatalog.onSortPropertyMetadata);
            getProperties().add(componentCatalog.placeholderPropertyMetadata);
            getProperties().add(componentCatalog.sortOrderPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c49_PropertyMetadata);
            getProperties().add(valueCatalog.tableMenuButtonVisiblePropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TableView.fxml"), null, null,
                            getClass().getResource("nodeicons/TableView.png"),
                            getClass().getResource("nodeicons/TableView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class TextAreaMetadata extends ComponentClassMetadata<javafx.scene.control.TextArea> {
        protected TextAreaMetadata(@Autowired TextInputControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.TextArea.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_AREA_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.prefColumnCount_40_PropertyMetadata);
            getProperties().add(valueCatalog.prefRowCountPropertyMetadata);
            getProperties().add(valueCatalog.scrollLeftPropertyMetadata);
            getProperties().add(valueCatalog.scrollTopPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c51_PropertyMetadata);
            getProperties().add(valueCatalog.wrapTextPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/TextArea.png"),
                            getClass().getResource("nodeicons/TextArea@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class TextFormatterMetadata extends ComponentClassMetadata<javafx.scene.control.TextFormatter> {
        protected TextFormatterMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.TextFormatter.class, null);
            getProperties().add(valueCatalog.value_Object_PropertyMetadata);
        }
    }

    @Component
    public static class TitledPaneMetadata extends ComponentClassMetadata<javafx.scene.control.TitledPane> {
        protected TitledPaneMetadata(@Autowired LabeledMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TitledPane.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TITLED_PANE_PropertyMetadata);
            getProperties().add(valueCatalog.animatedPropertyMetadata);
            getProperties().add(valueCatalog.collapsiblePropertyMetadata);
            getProperties().add(componentCatalog.content_Node_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.expanded_true_PropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.mnemonicParsing_false_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c26_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TitledPane.fxml"), null, null,
                            getClass().getResource("nodeicons/TitledPane.png"),
                            getClass().getResource("nodeicons/TitledPane@2x.png"), TAG_CONTAINERS));

            getQualifiers().put(Qualifier.EMPTY,
                    new Qualifier(null, Qualifier.EMPTY, null, getClass().getResource("nodeicons/TitledPane.png"),
                            getClass().getResource("nodeicons/TitledPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class ToolBarMetadata extends ComponentClassMetadata<javafx.scene.control.ToolBar> {
        protected ToolBarMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.ToolBar.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TOOL_BAR_PropertyMetadata);
            getProperties().add(componentCatalog.items_Node_PropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c16_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ToolBar.fxml"), null, null,
                            getClass().getResource("nodeicons/ToolBar.png"),
                            getClass().getResource("nodeicons/ToolBar@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class TooltipMetadata extends ComponentClassMetadata<javafx.scene.control.Tooltip> {
        protected TooltipMetadata(@Autowired PopupControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.Tooltip.class, parent);
            getProperties().add(valueCatalog.contentDisplayPropertyMetadata);
            getProperties().add(valueCatalog.fontPropertyMetadata);
            getProperties().add(componentCatalog.graphicPropertyMetadata);
            getProperties().add(valueCatalog.graphicTextGapPropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.onCloseRequestPropertyMetadata);
            getProperties().add(valueCatalog.onHiddenPropertyMetadata);
            getProperties().add(valueCatalog.onHidingPropertyMetadata);
            getProperties().add(valueCatalog.onShowingPropertyMetadata);
            getProperties().add(valueCatalog.onShownPropertyMetadata);
            getProperties().add(valueCatalog.opacityPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c15_PropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(valueCatalog.textAlignmentPropertyMetadata);
            getProperties().add(valueCatalog.textOverrunPropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.wrapTextPropertyMetadata);
            getProperties().add(valueCatalog.x_NaN_PropertyMetadata);
            getProperties().add(valueCatalog.y_NaN_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Tooltip.fxml"), null, null,
                            getClass().getResource("nodeicons/Tooltip.png"),
                            getClass().getResource("nodeicons/Tooltip@2x.png"), TAG_MISCELLANEOUS));
        }
    }

    @Component
    public static class TreeTableColumnMetadata extends ComponentClassMetadata<javafx.scene.control.TreeTableColumn> {
        protected TreeTableColumnMetadata(@Autowired TableColumnBaseMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TreeTableColumn.class, parent);
            getProperties().add(componentCatalog.columns_TreeTableColumn_PropertyMetadata);
            getProperties().add(valueCatalog.onEditCancelPropertyMetadata);
            getProperties().add(valueCatalog.onEditCommitPropertyMetadata);
            getProperties().add(valueCatalog.onEditStartPropertyMetadata);
            getProperties().add(valueCatalog.sortType_SortType_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TreeTableColumn.fxml"), null, FX8,
                            getClass().getResource("nodeicons/TreeTableColumn.png"),
                            getClass().getResource("nodeicons/TreeTableColumn@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class TreeTableViewMetadata extends ComponentClassMetadata<javafx.scene.control.TreeTableView> {
        protected TreeTableViewMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.control.TreeTableView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TREE_TABLE_VIEW_PropertyMetadata);
            getProperties().add(valueCatalog.columnResizePolicy_TREETABLEVIEW_UNCONSTRAINED_PropertyMetadata);
            getProperties().add(componentCatalog.columns_TreeTableColumn_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.expandedItemCountPropertyMetadata);
            getProperties().add(valueCatalog.fixedCellSizePropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onScrollToPropertyMetadata);
            getProperties().add(valueCatalog.onScrollToColumnPropertyMetadata);
            getProperties().add(valueCatalog.onSortPropertyMetadata);
            getProperties().add(componentCatalog.placeholderPropertyMetadata);
            getProperties().add(valueCatalog.showRootPropertyMetadata);
            getProperties().add(valueCatalog.sortModePropertyMetadata);
            getProperties().add(componentCatalog.sortOrderPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c32_PropertyMetadata);
            getProperties().add(valueCatalog.tableMenuButtonVisiblePropertyMetadata);
            getProperties().add(componentCatalog.treeColumnPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/TreeTableView.fxml"), null, FX8,
                            getClass().getResource("nodeicons/TreeTableView.png"),
                            getClass().getResource("nodeicons/TreeTableView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class TreeViewMetadata extends ComponentClassMetadata<javafx.scene.control.TreeView> {
        protected TreeViewMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.control.TreeView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TREE_VIEW_PropertyMetadata);
            getProperties().add(valueCatalog.editable_false_PropertyMetadata);
            getProperties().add(valueCatalog.expandedItemCountPropertyMetadata);
            getProperties().add(valueCatalog.fixedCellSizePropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.onEditCancelPropertyMetadata);
            getProperties().add(valueCatalog.onEditCommitPropertyMetadata);
            getProperties().add(valueCatalog.onEditStartPropertyMetadata);
            getProperties().add(valueCatalog.onScrollToPropertyMetadata);
            getProperties().add(valueCatalog.showRootPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c22_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/TreeView.png"),
                            getClass().getResource("nodeicons/TreeView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ImageViewMetadata extends ComponentClassMetadata<javafx.scene.image.ImageView> {
        protected ImageViewMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.image.ImageView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_IMAGE_VIEW_PropertyMetadata);
            getProperties().add(valueCatalog.fitHeightPropertyMetadata);
            getProperties().add(valueCatalog.fitWidthPropertyMetadata);
            getProperties().add(valueCatalog.imagePropertyMetadata);
            getProperties().add(valueCatalog.nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.preserveRatio_false_PropertyMetadata);
            getProperties().add(valueCatalog.smoothPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c20_PropertyMetadata);
            getProperties().add(valueCatalog.viewportPropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/ImageView.fxml"), null, null,
                            getClass().getResource("nodeicons/ImageView.png"),
                            getClass().getResource("nodeicons/ImageView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class AnchorPaneMetadata extends ComponentClassMetadata<javafx.scene.layout.AnchorPane> {
        protected AnchorPaneMetadata(@Autowired PaneMetadata parent) {
            super(javafx.scene.layout.AnchorPane.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/AnchorPane.png"),
                            getClass().getResource("nodeicons/AnchorPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class BorderPaneMetadata extends ComponentClassMetadata<javafx.scene.layout.BorderPane> {
        protected BorderPaneMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.layout.BorderPane.class, parent);
            getProperties().add(componentCatalog.bottomPropertyMetadata);
            getProperties().add(componentCatalog.centerPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(componentCatalog.leftPropertyMetadata);
            getProperties().add(componentCatalog.rightPropertyMetadata);
            getProperties().add(componentCatalog.topPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/BorderPane.png"),
                            getClass().getResource("nodeicons/BorderPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class ColumnConstraintsMetadata
            extends ComponentClassMetadata<javafx.scene.layout.ColumnConstraints> {
        protected ColumnConstraintsMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.ColumnConstraints.class, null);
            getProperties().add(valueCatalog.fillWidthPropertyMetadata);
            getProperties().add(valueCatalog.halignment_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.hgrowPropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.percentWidthPropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_COMPUTED_PropertyMetadata);

        }
    }

    @Component
    public static class FlowPaneMetadata extends ComponentClassMetadata<javafx.scene.layout.FlowPane> {
        protected FlowPaneMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.FlowPane.class, parent);
            getProperties().add(valueCatalog.alignment_TOP_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.columnHalignmentPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.hgapPropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.prefWrapLengthPropertyMetadata);
            getProperties().add(valueCatalog.rowValignmentPropertyMetadata);
            getProperties().add(valueCatalog.vgapPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/FlowPane.png"),
                            getClass().getResource("nodeicons/FlowPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class GridPaneMetadata extends ComponentClassMetadata<javafx.scene.layout.GridPane> {
        protected GridPaneMetadata(@Autowired PaneMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.layout.GridPane.class, parent);
            getProperties().add(valueCatalog.alignment_TOP_LEFT_PropertyMetadata);
            getProperties().add(componentCatalog.columnConstraintsPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.gridLinesVisiblePropertyMetadata);
            getProperties().add(valueCatalog.hgapPropertyMetadata);
            getProperties().add(componentCatalog.rowConstraintsPropertyMetadata);
            getProperties().add(valueCatalog.vgapPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/GridPane.fxml"), null, null,
                            getClass().getResource("nodeicons/GridPane.png"),
                            getClass().getResource("nodeicons/GridPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class HBoxMetadata extends ComponentClassMetadata<javafx.scene.layout.HBox> {
        protected HBoxMetadata(@Autowired PaneMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.HBox.class, parent);
            getProperties().add(valueCatalog.alignment_TOP_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.fillHeightPropertyMetadata);
            getProperties().add(valueCatalog.spacingPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/HBox.png"),
                            getClass().getResource("nodeicons/HBox@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class RowConstraintsMetadata extends ComponentClassMetadata<javafx.scene.layout.RowConstraints> {
        protected RowConstraintsMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.RowConstraints.class, null);
            getProperties().add(valueCatalog.fillHeightPropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.percentHeightPropertyMetadata);
            getProperties().add(valueCatalog.prefHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.valignment_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.vgrowPropertyMetadata);
        }
    }

    @Component
    public static class StackPaneMetadata extends ComponentClassMetadata<javafx.scene.layout.StackPane> {
        protected StackPaneMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.StackPane.class, parent);
            getProperties().add(valueCatalog.alignment_CENTER_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/StackPane.png"),
                            getClass().getResource("nodeicons/StackPane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class TilePaneMetadata extends ComponentClassMetadata<javafx.scene.layout.TilePane> {
        protected TilePaneMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.TilePane.class, parent);
            getProperties().add(valueCatalog.alignment_TOP_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.hgapPropertyMetadata);
            getProperties().add(valueCatalog.orientation_HORIZONTAL_PropertyMetadata);
            getProperties().add(valueCatalog.prefColumnsPropertyMetadata);
            getProperties().add(valueCatalog.prefRowsPropertyMetadata);
            getProperties().add(valueCatalog.prefTileHeightPropertyMetadata);
            getProperties().add(valueCatalog.prefTileWidthPropertyMetadata);
            getProperties().add(valueCatalog.tileAlignmentPropertyMetadata);
            getProperties().add(valueCatalog.tileHeightPropertyMetadata);
            getProperties().add(valueCatalog.tileWidthPropertyMetadata);
            getProperties().add(valueCatalog.vgapPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/TilePane.png"),
                            getClass().getResource("nodeicons/TilePane@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class VBoxMetadata extends ComponentClassMetadata<javafx.scene.layout.VBox> {
        protected VBoxMetadata(@Autowired PaneMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.layout.VBox.class, parent);
            getProperties().add(valueCatalog.alignment_TOP_LEFT_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.fillWidthPropertyMetadata);
            getProperties().add(valueCatalog.spacingPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/VBox.png"),
                            getClass().getResource("nodeicons/VBox@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class MediaViewMetadata extends ComponentClassMetadata<javafx.scene.media.MediaView> {
        protected MediaViewMetadata(@Autowired NodeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.media.MediaView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.fitHeightPropertyMetadata);
            getProperties().add(valueCatalog.fitWidthPropertyMetadata);
            getProperties().add(valueCatalog.nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata);
            getProperties().add(valueCatalog.onErrorPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.preserveRatio_true_PropertyMetadata);
            getProperties().add(valueCatalog.smoothPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c46_PropertyMetadata);
            getProperties().add(valueCatalog.viewportPropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/MediaView.fxml"), null, null,
                            getClass().getResource("nodeicons/MediaView.png"),
                            getClass().getResource("nodeicons/MediaView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class ArcMetadata extends ComponentClassMetadata<javafx.scene.shape.Arc> {
        protected ArcMetadata(@Autowired ShapeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Arc.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.centerXPropertyMetadata);
            getProperties().add(valueCatalog.centerYPropertyMetadata);
            getProperties().add(valueCatalog.length_Double_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.radiusXPropertyMetadata);
            getProperties().add(valueCatalog.radiusYPropertyMetadata);
            getProperties().add(valueCatalog.startAnglePropertyMetadata);
            getProperties().add(valueCatalog.typePropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Arc.fxml"), null, null,
                            getClass().getResource("nodeicons/Arc.png"), getClass().getResource("nodeicons/Arc@2x.png"),
                            TAG_SHAPES));
        }
    }

    @Component
    public static class ArcToMetadata extends ComponentClassMetadata<javafx.scene.shape.ArcTo> {
        protected ArcToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.ArcTo.class, parent);
            getProperties().add(valueCatalog.largeArcFlagPropertyMetadata);
            getProperties().add(valueCatalog.radiusXPropertyMetadata);
            getProperties().add(valueCatalog.radiusYPropertyMetadata);
            getProperties().add(valueCatalog.sweepFlagPropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.XAxisRotationPropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/ArcTo.png"),
                            getClass().getResource("nodeicons/ArcTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class BoxMetadata extends ComponentClassMetadata<javafx.scene.shape.Box> {
        protected BoxMetadata(@Autowired Shape3DMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Box.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.depthPropertyMetadata);
            getProperties().add(valueCatalog.height_Double_200_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_200_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Box.fxml"), null, FX8,
                            getClass().getResource("nodeicons/Box.png"), getClass().getResource("nodeicons/Box@2x.png"),
                            TAG_SHAPES));
        }
    }

    @Component
    public static class CircleMetadata extends ComponentClassMetadata<javafx.scene.shape.Circle> {
        protected CircleMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Circle.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.centerXPropertyMetadata);
            getProperties().add(valueCatalog.centerYPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.radius_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Circle.fxml"), null, null,
                            getClass().getResource("nodeicons/Circle.png"),
                            getClass().getResource("nodeicons/Circle@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class ClosePathMetadata extends ComponentClassMetadata<javafx.scene.shape.ClosePath> {
        protected ClosePathMetadata(@Autowired PathElementMetadata parent) {
            super(javafx.scene.shape.ClosePath.class, parent);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/ClosePath.png"),
                            getClass().getResource("nodeicons/ClosePath@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class CubicCurveMetadata extends ComponentClassMetadata<javafx.scene.shape.CubicCurve> {
        protected CubicCurveMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.CubicCurve.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.controlX1PropertyMetadata);
            getProperties().add(valueCatalog.controlX2PropertyMetadata);
            getProperties().add(valueCatalog.controlY1PropertyMetadata);
            getProperties().add(valueCatalog.controlY2PropertyMetadata);
            getProperties().add(valueCatalog.endXPropertyMetadata);
            getProperties().add(valueCatalog.endYPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.startXPropertyMetadata);
            getProperties().add(valueCatalog.startYPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/CubicCurve.fxml"), null, null,
                            getClass().getResource("nodeicons/CubicCurve.png"),
                            getClass().getResource("nodeicons/CubicCurve@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class CubicCurveToMetadata extends ComponentClassMetadata<javafx.scene.shape.CubicCurveTo> {
        protected CubicCurveToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.CubicCurveTo.class, parent);
            getProperties().add(valueCatalog.controlX1PropertyMetadata);
            getProperties().add(valueCatalog.controlX2PropertyMetadata);
            getProperties().add(valueCatalog.controlY1PropertyMetadata);
            getProperties().add(valueCatalog.controlY2PropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/CubicCurveTo.png"),
                            getClass().getResource("nodeicons/CubicCurveTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class CylinderMetadata extends ComponentClassMetadata<javafx.scene.shape.Cylinder> {
        protected CylinderMetadata(@Autowired Shape3DMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Cylinder.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.divisionsPropertyMetadata);
            getProperties().add(valueCatalog.height_Double_200_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.radius_100_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Cylinder.fxml"), null, FX8,
                            getClass().getResource("nodeicons/Cylinder.png"),
                            getClass().getResource("nodeicons/Cylinder@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class EllipseMetadata extends ComponentClassMetadata<javafx.scene.shape.Ellipse> {
        protected EllipseMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Ellipse.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.centerXPropertyMetadata);
            getProperties().add(valueCatalog.centerYPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.radiusXPropertyMetadata);
            getProperties().add(valueCatalog.radiusYPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Ellipse.fxml"), null, null,
                            getClass().getResource("nodeicons/Ellipse.png"),
                            getClass().getResource("nodeicons/Ellipse@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class HLineToMetadata extends ComponentClassMetadata<javafx.scene.shape.HLineTo> {
        protected HLineToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.HLineTo.class, parent);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/HLineTo.png"),
                            getClass().getResource("nodeicons/HLineTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class LineMetadata extends ComponentClassMetadata<javafx.scene.shape.Line> {
        protected LineMetadata(@Autowired ShapeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Line.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.endXPropertyMetadata);
            getProperties().add(valueCatalog.endYPropertyMetadata);
            getProperties().add(valueCatalog.fill_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.startXPropertyMetadata);
            getProperties().add(valueCatalog.startYPropertyMetadata);
            getProperties().add(valueCatalog.stroke_BLACK_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Line.fxml"), null, null,
                            getClass().getResource("nodeicons/Line.png"),
                            getClass().getResource("nodeicons/Line@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class LineToMetadata extends ComponentClassMetadata<javafx.scene.shape.LineTo> {
        protected LineToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.LineTo.class, parent);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/LineTo.png"),
                            getClass().getResource("nodeicons/LineTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class MeshViewMetadata extends ComponentClassMetadata<javafx.scene.shape.MeshView> {
        protected MeshViewMetadata(@Autowired Shape3DMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.MeshView.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.meshPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/MeshView.png"),
                            getClass().getResource("nodeicons/MeshView@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class MoveToMetadata extends ComponentClassMetadata<javafx.scene.shape.MoveTo> {
        protected MoveToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.MoveTo.class, parent);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/MoveTo.png"),
                            getClass().getResource("nodeicons/MoveTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class PathMetadata extends ComponentClassMetadata<javafx.scene.shape.Path> {
        protected PathMetadata(@Autowired ShapeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog,
                @Autowired ComponentPropertyMetadataCatalog componentCatalog) {
            super(javafx.scene.shape.Path.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(componentCatalog.elementsPropertyMetadata);
            getProperties().add(valueCatalog.fill_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.fillRulePropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.stroke_BLACK_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Path.fxml"), null, null,
                            getClass().getResource("nodeicons/Path.png"),
                            getClass().getResource("nodeicons/Path@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class PolygonMetadata extends ComponentClassMetadata<javafx.scene.shape.Polygon> {
        protected PolygonMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Polygon.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.pointsPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Polygon.fxml"), null, null,
                            getClass().getResource("nodeicons/Polygon.png"),
                            getClass().getResource("nodeicons/Polygon@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class PolylineMetadata extends ComponentClassMetadata<javafx.scene.shape.Polyline> {
        protected PolylineMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Polyline.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.fill_NULL_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.pointsPropertyMetadata);
            getProperties().add(valueCatalog.stroke_BLACK_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Polyline.fxml"), null, null,
                            getClass().getResource("nodeicons/Polyline.png"),
                            getClass().getResource("nodeicons/Polyline@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class QuadCurveMetadata extends ComponentClassMetadata<javafx.scene.shape.QuadCurve> {
        protected QuadCurveMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.QuadCurve.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.controlXPropertyMetadata);
            getProperties().add(valueCatalog.controlYPropertyMetadata);
            getProperties().add(valueCatalog.endXPropertyMetadata);
            getProperties().add(valueCatalog.endYPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.startXPropertyMetadata);
            getProperties().add(valueCatalog.startYPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/QuadCurve.fxml"), null, null,
                            getClass().getResource("nodeicons/QuadCurve.png"),
                            getClass().getResource("nodeicons/QuadCurve@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class QuadCurveToMetadata extends ComponentClassMetadata<javafx.scene.shape.QuadCurveTo> {
        protected QuadCurveToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.QuadCurveTo.class, parent);
            getProperties().add(valueCatalog.controlXPropertyMetadata);
            getProperties().add(valueCatalog.controlYPropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/QuadCurveTo.png"),
                            getClass().getResource("nodeicons/QuadCurveTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class RectangleMetadata extends ComponentClassMetadata<javafx.scene.shape.Rectangle> {
        protected RectangleMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Rectangle.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.arcHeightPropertyMetadata);
            getProperties().add(valueCatalog.arcWidthPropertyMetadata);
            getProperties().add(valueCatalog.height_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_0_PropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Rectangle.fxml"), null, null,
                            getClass().getResource("nodeicons/Rectangle.png"),
                            getClass().getResource("nodeicons/Rectangle@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class SVGPathMetadata extends ComponentClassMetadata<javafx.scene.shape.SVGPath> {
        protected SVGPathMetadata(@Autowired ShapeMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.SVGPath.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.content_String_PropertyMetadata);
            getProperties().add(valueCatalog.fillRulePropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/SVGPath.fxml"), null, null,
                            getClass().getResource("nodeicons/SVGPath.png"),
                            getClass().getResource("nodeicons/SVGPath@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class SphereMetadata extends ComponentClassMetadata<javafx.scene.shape.Sphere> {
        protected SphereMetadata(@Autowired Shape3DMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.Sphere.class, parent);
            getProperties().add(valueCatalog.accessibleRole_NODE_PropertyMetadata);
            getProperties().add(valueCatalog.divisionsPropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.radius_100_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Sphere.fxml"), null, FX8,
                            getClass().getResource("nodeicons/Sphere.png"),
                            getClass().getResource("nodeicons/Sphere@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class TextMetadata extends ComponentClassMetadata<javafx.scene.text.Text> {
        protected TextMetadata(@Autowired ShapeMetadata parent, @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.text.Text.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_PropertyMetadata);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.boundsTypePropertyMetadata);
            getProperties().add(valueCatalog.fontPropertyMetadata);
            getProperties().add(valueCatalog.fontSmoothingType_GRAY_PropertyMetadata);
            getProperties().add(valueCatalog.lineSpacingPropertyMetadata);
            getProperties().add(valueCatalog.strikethroughPropertyMetadata);
            getProperties().add(valueCatalog.textPropertyMetadata);
            getProperties().add(valueCatalog.textAlignmentPropertyMetadata);
            getProperties().add(valueCatalog.textOriginPropertyMetadata);
            getProperties().add(valueCatalog.underlinePropertyMetadata);
            getProperties().add(valueCatalog.wrappingWidthPropertyMetadata);
            getProperties().add(valueCatalog.x_0_PropertyMetadata);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/Text.fxml"), null, null,
                            getClass().getResource("nodeicons/Text.png"),
                            getClass().getResource("nodeicons/Text@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class TextFlowMetadata extends ComponentClassMetadata<javafx.scene.text.TextFlow> {
        protected TextFlowMetadata(@Autowired PaneMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.text.TextFlow.class, parent);
            getProperties().add(valueCatalog.accessibleRole_TEXT_PropertyMetadata);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.lineSpacingPropertyMetadata);
            getProperties().add(valueCatalog.textAlignmentPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, FX8, getClass().getResource("nodeicons/TextFlow.png"),
                            getClass().getResource("nodeicons/TextFlow@2x.png"), TAG_CONTAINERS));
        }
    }

    @Component
    public static class VLineToMetadata extends ComponentClassMetadata<javafx.scene.shape.VLineTo> {
        protected VLineToMetadata(@Autowired PathElementMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.shape.VLineTo.class, parent);
            getProperties().add(valueCatalog.y_0_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/VLineTo.png"),
                            getClass().getResource("nodeicons/VLineTo@2x.png"), TAG_SHAPES));
        }
    }

    @Component
    public static class HTMLEditorMetadata extends ComponentClassMetadata<javafx.scene.web.HTMLEditor> {
        protected HTMLEditorMetadata(@Autowired ControlMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.web.HTMLEditor.class, parent);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.htmlTextPropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c21_PropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/HTMLEditor.fxml"), null, null,
                            getClass().getResource("nodeicons/HTMLEditor.png"),
                            getClass().getResource("nodeicons/HTMLEditor@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class WebViewMetadata extends ComponentClassMetadata<javafx.scene.web.WebView> {
        protected WebViewMetadata(@Autowired ParentMetadata parent,
                @Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(javafx.scene.web.WebView.class, parent);
            getProperties().add(valueCatalog.contextMenuEnabledPropertyMetadata);
            getProperties().add(valueCatalog.focusTraversable_true_PropertyMetadata);
            getProperties().add(valueCatalog.fontScalePropertyMetadata);
            getProperties().add(valueCatalog.fontSmoothingType_LCD_PropertyMetadata);
            getProperties().add(valueCatalog.height_Double_ro_PropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_MAX_PropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_MAX_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_0_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_0_PropertyMetadata);
            getProperties().add(valueCatalog.nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata);
            getProperties().add(valueCatalog.pickOnBounds_false_PropertyMetadata);
            getProperties().add(valueCatalog.prefHeight_60000_PropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_80000_PropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.styleClass_c48_PropertyMetadata);
            getProperties().add(valueCatalog.width_Double_ro_PropertyMetadata);
            getProperties().add(valueCatalog.zoomPropertyMetadata);

            getQualifiers().put(Qualifier.DEFAULT,
                    new Qualifier(getClass().getResource("fxml/WebView.fxml"), null, null,
                            getClass().getResource("nodeicons/WebView.png"),
                            getClass().getResource("nodeicons/WebView@2x.png"), TAG_CONTROLS));
        }
    }

    @Component
    public static class IncludeElementMetadata extends ComponentClassMetadata<FXOMIntrinsic> {
        protected IncludeElementMetadata(@Autowired ValuePropertyMetadataCatalog valueCatalog) {
            super(FXOMIntrinsic.class, null);
//            getProperties().add(valueCatalog.AnchorPane_bottomAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_leftAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_rightAnchorPropertyMetadata);
//            getProperties().add(valueCatalog.AnchorPane_topAnchorPropertyMetadata);
            
            getProperties().add(valueCatalog.AnchorPane_AnchorPropertyGroupMetadata);
            
            getProperties().add(valueCatalog.BorderPane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.FlowPane_marginPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_columnIndexPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_columnSpanPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_halignmentPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_hgrowPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_rowIndexPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_rowSpanPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_valignmentPropertyMetadata);
            getProperties().add(valueCatalog.GridPane_vgrowPropertyMetadata);
            getProperties().add(valueCatalog.HBox_hgrowPropertyMetadata);
            getProperties().add(valueCatalog.StackPane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.TilePane_alignmentPropertyMetadata);
            getProperties().add(valueCatalog.VBox_vgrowPropertyMetadata);
            getProperties().add(valueCatalog.layoutXPropertyMetadata);
            getProperties().add(valueCatalog.layoutYPropertyMetadata);
            getProperties().add(valueCatalog.maxHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.maxWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.minWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.prefHeight_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.prefWidth_COMPUTED_PropertyMetadata);
            getProperties().add(valueCatalog.rotatePropertyMetadata);
            getProperties().add(valueCatalog.rotationAxisPropertyMetadata);
            getProperties().add(valueCatalog.scaleXPropertyMetadata);
            getProperties().add(valueCatalog.scaleYPropertyMetadata);
            getProperties().add(valueCatalog.scaleZPropertyMetadata);
            getProperties().add(valueCatalog.translateXPropertyMetadata);
            getProperties().add(valueCatalog.translateYPropertyMetadata);
            getProperties().add(valueCatalog.translateZPropertyMetadata);
            getProperties().add(valueCatalog.layoutBoundsPropertyMetadata);
            getProperties().add(valueCatalog.boundsInLocalPropertyMetadata);
            getProperties().add(valueCatalog.boundsInParentPropertyMetadata);
            getProperties().add(valueCatalog.baselineOffsetPropertyMetadata);
            getProperties().add(valueCatalog.resizable_Boolean_ro_PropertyMetadata);
            getProperties().add(valueCatalog.contentBiasPropertyMetadata);
            getProperties().add(valueCatalog.snapToPixelPropertyMetadata);
            getProperties().add(valueCatalog.effectiveNodeOrientationPropertyMetadata);
            getProperties().add(valueCatalog.includeFxmlPropertyMetadata);

            // TODO associated image are only valid for Type.FX_INCLUDE
            // other types does not have associated images
            // FX_REFERENCE,
            // FX_COPY,
            // UNDEFINED
            getQualifiers().put(Qualifier.HIDDEN,
                    new Qualifier(null, null, null, getClass().getResource("nodeicons/Included.png"),
                            getClass().getResource("nodeicons/Included@2x.png"), TAG_CONTROLS));
        }
    }

}
