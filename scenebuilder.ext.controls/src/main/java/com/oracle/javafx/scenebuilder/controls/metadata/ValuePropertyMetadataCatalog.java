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

import java.util.Arrays;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.metadata.PropertyNames;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.AnchorPropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BoundsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.CursorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleArrayPropertyMetadata.DividerPositionsDoubleArrayPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleBoundedPropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.AngleDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.ComputedAndPrefSizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.ComputedSizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.NullableCoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.OpacityDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.PercentageDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.ProgressDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata.TextAlignmentEnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.FontPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.InsetsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata.GridColumnIndexIntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata.GridColumnSpanIntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata.GridRowIndexIntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata.GridRowSpanIntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata.PositiveIntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ListCellPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.MaterialPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.MeshPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ObjectPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Point3DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Rectangle2DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringConverterPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.IdStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.MultilineI18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.StyleStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ToggleGroupPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TreeTableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.effect.EffectPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination.KeyCombinationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ButtonTypeListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.DoubleListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.SourceStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.StyleClassStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata.StylesheetsStringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.TickMarkListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

@Component
public class ValuePropertyMetadataCatalog {

    // Property Metadata

    //sectionTags

    public static final String PROPERTIES = "Properties"; //NOCHECK
    private static final String LAYOUT = "Layout"; //NOCHECK
    private static final String CODE = "Code"; //NOCHECK
    
    //subSectionTags
    private static final String ACCESSIBILITY = "Accessibility"; //NOCHECK
    public static final String SPECIFIC = "Specific"; //NOCHECK
    private static final String INCLUDE_FXML_FILE = "Include FXML file"; //NOCHECK
    private static final String V_BOX_CONSTRAINTS = "VBox Constraints"; //NOCHECK
    private static final String TILE_PANE_CONSTRAINTS = "Tile Pane Constraints"; //NOCHECK
    private static final String STACK_PANE_CONSTRAINTS = "Stack Pane Constraints"; //NOCHECK
    private static final String H_BOX_CONSTRAINTS = "HBox Constraints"; //NOCHECK
    private static final String GRID_PANE_CONSTRAINTS = "Grid Pane Constraints"; //NOCHECK
    private static final String FLOW_PANE_CONSTRAINTS = "Flow Pane Constraints"; //NOCHECK
    private static final String BORDER_PANE_CONSTRAINTS = "Border Pane Constraints"; //NOCHECK
    private static final String ANCHOR_PANE_CONSTRAINTS = "Anchor Pane Constraints"; //NOCHECK
    private static final String SPLIT_PANE_CONSTRAINTS = "Split Pane Constraints"; //NOCHECK
    private static final String STROKE = "Stroke"; //NOCHECK
    private static final String BOTTOM = "BOTTOM"; //NOCHECK
    private static final String TRANSFORMS = "Transforms"; //NOCHECK
    private static final String DEFAULT_PREF_ROW_COUNT = "DEFAULT_PREF_ROW_COUNT"; //NOCHECK
    private static final String DEFAULT_PREF_COLUMN_COUNT = "DEFAULT_PREF_COLUMN_COUNT"; //NOCHECK
    private static final String ZOOM = "Zoom"; //NOCHECK
    private static final String TOUCH = "Touch"; //NOCHECK
    private static final String SWIPE = "Swipe"; //NOCHECK
    private static final String ROTATION = "Rotation"; //NOCHECK
    private static final String KEYBOARD = "Keyboard"; //NOCHECK
    private static final String EDIT = "Edit"; //NOCHECK
    private static final String DRAG_DROP = "DragDrop"; //NOCHECK
    private static final String MOUSE = "Mouse"; //NOCHECK
    private static final String CLOSING = "Closing"; //NOCHECK
    private static final String HIDE_SHOW = "HideShow"; //NOCHECK
    private static final String MAIN = "Main"; //NOCHECK
    private static final String JAVA_FX_CSS = "JavaFX CSS"; //NOCHECK
    private static final String INTERNAL = "Internal"; //NOCHECK
    private static final String PAGINATION = "Pagination"; //NOCHECK
    private static final String _3D = "3D"; //NOCHECK
    private static final String TEXT = "Text"; //NOCHECK
    private static final String GRAPHIC = "Graphic"; //NOCHECK
    private static final String SIZE = "Size"; //NOCHECK
    private static final String BOUNDS = "Bounds"; //NOCHECK
    private static final String POSITION = "Position"; //NOCHECK
    private static final String EXTRAS = "Extras"; //NOCHECK
    public static final String NODE = "Node"; //NOCHECK
    
    //values
    private static final String EMPTY = ""; //NOCHECK
    private static final String INHERIT = "INHERIT"; //NOCHECK
    
    public final ValuePropertyMetadata absolutePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.absoluteName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 0));
    public final ValuePropertyMetadata acceleratorPropertyMetadata =
            new KeyCombinationPropertyMetadata(
                PropertyNames.acceleratorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 1));
    public final ValuePropertyMetadata accessibleHelpPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.accessibleHelpName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 1));
    public final ValuePropertyMetadata accessibleRole_NODE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.NODE, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.BUTTON, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TOGGLE_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TOGGLE_BUTTON, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_CHECK_BOX_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.CHECK_BOX, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_COMBO_BOX_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.COMBO_BOX, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_DATE_PICKER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.DATE_PICKER, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_HYPERLINK_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.HYPERLINK, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_IMAGE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.IMAGE_VIEW, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_LIST_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.LIST_VIEW, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_MENU_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.MENU_BAR, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_MENU_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.MENU_BUTTON, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_PARENT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PARENT, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_PAGINATION_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PAGINATION, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_PASSWORD_FIELD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PASSWORD_FIELD, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_PROGRESS_INDICATOR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PROGRESS_INDICATOR, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_RADIO_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.RADIO_BUTTON, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_SCROLL_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SCROLL_BAR, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_SCROLL_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SCROLL_PANE, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_SLIDER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SLIDER, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_SPINNER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SPINNER, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_SPLIT_MENU_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SPLIT_MENU_BUTTON, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TAB_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TAB_PANE, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TABLE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TABLE_VIEW, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_AREA_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT_AREA, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_FIELD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT_FIELD, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TITLED_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TITLED_PANE, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TOOL_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TOOL_BAR, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TREE_TABLE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TREE_TABLE_VIEW, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRole_TREE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TREE_VIEW, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 2));
    public final ValuePropertyMetadata accessibleRoleDescriptionPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.accessibleRoleDescriptionName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 3));
    public final ValuePropertyMetadata accessibleTextPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.accessibleTextName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, ACCESSIBILITY, 0));
    public final ValuePropertyMetadata alignment_TOP_LEFT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.TOP_LEFT, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 0));
    public final ValuePropertyMetadata alignment_CENTER_LEFT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER_LEFT, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 0));
    public final ValuePropertyMetadata alignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 0));
    public final ValuePropertyMetadata allowIndeterminatePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.allowIndeterminateName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 2));
    public final ValuePropertyMetadata alternativeColumnFillVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alternativeColumnFillVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 88));
    public final ValuePropertyMetadata alternativeRowFillVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alternativeRowFillVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 94));
    public final ValuePropertyMetadata alwaysOnTopPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alwaysOnTopName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 9));
    public final ValuePropertyMetadata anchorLocationPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.anchorLocationName,
                javafx.stage.PopupWindow.AnchorLocation.class,
                true, /* readWrite */
                javafx.stage.PopupWindow.AnchorLocation.CONTENT_TOP_LEFT, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 11));
    public final ValuePropertyMetadata anchorXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.anchorXName,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 9));
    public final ValuePropertyMetadata anchorYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.anchorYName,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 10));
    public final ValuePropertyMetadata animatedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.animatedName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 43));
    public final ValuePropertyMetadata arcHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.arcHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 44));
    public final ValuePropertyMetadata arcWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.arcWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 3));
    public final ValuePropertyMetadata autoFixPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoFixName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 4));
    public final ValuePropertyMetadata autoHide_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoHideName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 45));
    public final ValuePropertyMetadata autoHide_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoHideName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 45));
    public final ValuePropertyMetadata autoRangingPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoRangingName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 72));
    public final ValuePropertyMetadata autoSizeChildrenPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoSizeChildrenName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 0));
    public final ValuePropertyMetadata axisSortingPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.axisSortingPolicyName,
                javafx.scene.chart.LineChart.SortingPolicy.class,
                true, /* readWrite */
                javafx.scene.chart.LineChart.SortingPolicy.X_AXIS, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 130));
    public final ValuePropertyMetadata barGapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.barGapName,
                true, /* readWrite */
                4.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 77));
    public final ValuePropertyMetadata baselineOffsetPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.baselineOffsetName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, EXTRAS, 1));
    public final ValuePropertyMetadata blendModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.blendModeName,
                javafx.scene.effect.BlendMode.class,
                "SRC_OVER", /* null equivalent */ //NOCHECK
                true, /* readWrite */
                new InspectorPath(PROPERTIES, EXTRAS, 0));
    public final ValuePropertyMetadata blockIncrementPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.blockIncrementName,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 73));
    public final ValuePropertyMetadata boundsInLocalPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.boundsInLocalName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, BOUNDS, 2));
    public final ValuePropertyMetadata boundsInParentPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.boundsInParentName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, BOUNDS, 3));
    public final ValuePropertyMetadata boundsTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.boundsTypeName,
                javafx.scene.text.TextBoundsType.class,
                true, /* readWrite */
                javafx.scene.text.TextBoundsType.LOGICAL, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 2));
    public final ValuePropertyMetadata buttonCellPropertyMetadata =
            new ListCellPropertyMetadata(
                PropertyNames.buttonCellName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 40));
    public final ValuePropertyMetadata buttonMinWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.buttonMinWidthName,
                true, /* readWrite */
                70.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 0));
    public final ValuePropertyMetadata buttonOrderPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.buttonOrderName,
                true, /* readWrite */
                "L_HE+U+FBIX_NCYOA_R", /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, SPECIFIC, 5));
    public final ValuePropertyMetadata buttonTypesPropertyMetadata =
            new ButtonTypeListPropertyMetadata(
                PropertyNames.buttonTypesName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 18));
    public final ValuePropertyMetadata cachePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cacheName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 2));
    public final ValuePropertyMetadata cacheHintPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.cacheHintName,
                javafx.scene.CacheHint.class,
                true, /* readWrite */
                javafx.scene.CacheHint.DEFAULT, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 3));
    public final ValuePropertyMetadata cacheShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cacheShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 7));
    public final ValuePropertyMetadata cancelButtonPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cancelButtonName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 46));
    public final ValuePropertyMetadata categoriesPropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.categoriesName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 78));
    public final ValuePropertyMetadata categoryGapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.categoryGapName,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 79));
    public final ValuePropertyMetadata categorySpacingPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.categorySpacingName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 80));
    public final ValuePropertyMetadata centerShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.centerShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 8));
    public final ValuePropertyMetadata centerXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.centerXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 0));
    public final ValuePropertyMetadata centerYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.centerYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 1));
    public final ValuePropertyMetadata clockwisePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.clockwiseName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 89));
    public final ValuePropertyMetadata closablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.closableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 6));
    public final ValuePropertyMetadata collapsiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.collapsibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 62));
    public final ValuePropertyMetadata colorPropertyMetadata =
            new ColorPropertyMetadata(
                PropertyNames.colorName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 7));
    public final ValuePropertyMetadata columnHalignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.columnHalignmentName,
                javafx.geometry.HPos.class,
                true, /* readWrite */
                javafx.geometry.HPos.LEFT, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 63));
    public final ValuePropertyMetadata columnResizePolicy_TABLEVIEW_UNCONSTRAINED_PropertyMetadata =
            new TableViewResizePolicyPropertyMetadata(
                PropertyNames.columnResizePolicyName,
                true, /* readWrite */
                javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 34));
    public final ValuePropertyMetadata columnResizePolicy_TREETABLEVIEW_UNCONSTRAINED_PropertyMetadata =
            new TreeTableViewResizePolicyPropertyMetadata(
                PropertyNames.columnResizePolicyName,
                true, /* readWrite */
                javafx.scene.control.TreeTableView.UNCONSTRAINED_RESIZE_POLICY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 34));
    public final ValuePropertyMetadata consumeAutoHidingEventsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.consumeAutoHidingEventsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 64));
    public final ValuePropertyMetadata content_String_PropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.contentName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 10));
    public final ValuePropertyMetadata contentBiasPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.contentBiasName,
                javafx.geometry.Orientation.class,
                "NONE", /* null equivalent */ //NOCHECK
                false, /* readWrite */
                new InspectorPath(LAYOUT, EXTRAS, 4));
    public final ValuePropertyMetadata contentDisplayPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.contentDisplayName,
                javafx.scene.control.ContentDisplay.class,
                true, /* readWrite */
                javafx.scene.control.ContentDisplay.LEFT, /* defaultValue */
                new InspectorPath(PROPERTIES, GRAPHIC, 1));
    public final ValuePropertyMetadata contentTextPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.contentTextName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 2));
    public final ValuePropertyMetadata contextMenuEnabledPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.contextMenuEnabledName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 11));
    public final ValuePropertyMetadata controlXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 14));
    public final ValuePropertyMetadata controlX1PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlX1Name,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 16));
    public final ValuePropertyMetadata controlX2PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlX2Name,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 18));
    public final ValuePropertyMetadata controlYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 15));
    public final ValuePropertyMetadata controlY1PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlY1Name,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 17));
    public final ValuePropertyMetadata controlY2PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.controlY2Name,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 19));
    public final ValuePropertyMetadata createSymbolsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.createSymbolsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 81));
    public final ValuePropertyMetadata cullFacePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.cullFaceName,
                javafx.scene.shape.CullFace.class,
                true, /* readWrite */
                javafx.scene.shape.CullFace.BACK, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 8));
    public final ValuePropertyMetadata currentPageIndexPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.currentPageIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath(PROPERTIES, PAGINATION, 0));
    public final ValuePropertyMetadata cursor_HAND_PropertyMetadata =
            new CursorPropertyMetadata(
                PropertyNames.cursorName,
                true, /* readWrite */
                javafx.scene.Cursor.HAND, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 13));
    public final ValuePropertyMetadata cursor_NULL_PropertyMetadata =
            new CursorPropertyMetadata(
                PropertyNames.cursorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 13));
    public final ValuePropertyMetadata defaultButtonPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.defaultButtonName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 13));
    public final ValuePropertyMetadata depthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.depthName,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 16));
    public final ValuePropertyMetadata depthTestPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.depthTestName,
                javafx.scene.DepthTest.class,
                true, /* readWrite */
                javafx.scene.DepthTest.INHERIT, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 4));
    public final ValuePropertyMetadata disablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.disableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 1));
    public final ValuePropertyMetadata dividerPositionsPropertyMetadata =
            new DividerPositionsDoubleArrayPropertyMetadata(
                PropertyNames.dividerPositionsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 15));
    public final ValuePropertyMetadata divisionsPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.divisionsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, _3D, 10));
    public final ValuePropertyMetadata drawModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.drawModeName,
                javafx.scene.shape.DrawMode.class,
                true, /* readWrite */
                javafx.scene.shape.DrawMode.FILL, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 9));
    public final ValuePropertyMetadata editable_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.editableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 16));
    public final ValuePropertyMetadata editable_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.editableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 16));
    public final ValuePropertyMetadata effectPropertyMetadata =
            new EffectPropertyMetadata(
                PropertyNames.effectName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 14));
    public final ValuePropertyMetadata effectiveNodeOrientationPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.effectiveNodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, EXTRAS, 7));
    public final ValuePropertyMetadata ellipsisStringPropertyMetadata =
            new I18nStringPropertyMetadata(
                PropertyNames.ellipsisStringName,
                true, /* readWrite */
                "...", /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, TEXT, 12));
    public final ValuePropertyMetadata endMarginPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.endMarginName,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 95));
    public final ValuePropertyMetadata endXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.endXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 7));
    public final ValuePropertyMetadata endYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.endYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 8));
    public final ValuePropertyMetadata expanded_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.expandedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 17));
    public final ValuePropertyMetadata expanded_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.expandedName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 17));
    public final ValuePropertyMetadata expandedItemCountPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.expandedItemCountName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 19));
    
    public final ValuePropertyMetadata farClipPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.farClipName,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 3));
    public final ValuePropertyMetadata fieldOfViewPropertyMetadata =
            new AngleDoublePropertyMetadata(
                PropertyNames.fieldOfViewName,
                true, /* readWrite */
                30.0, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 4));
    public final ValuePropertyMetadata fill_NULL_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 20));
    public final ValuePropertyMetadata fill_BLACK_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 20));
    public final ValuePropertyMetadata fill_WHITE_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 20));
    public final ValuePropertyMetadata fillHeightPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fillHeightName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 0));
    public final ValuePropertyMetadata fillRulePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fillRuleName,
                javafx.scene.shape.FillRule.class,
                true, /* readWrite */
                javafx.scene.shape.FillRule.NON_ZERO, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 21));
    public final ValuePropertyMetadata fillWidthPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fillWidthName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 1));
    public final ValuePropertyMetadata fitHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.fitHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 15));
    public final ValuePropertyMetadata fitToHeightPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fitToHeightName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 11));
    public final ValuePropertyMetadata fitToWidthPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fitToWidthName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 10));
    public final ValuePropertyMetadata fitWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.fitWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 14));
    public final ValuePropertyMetadata fixedCellSizePropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.fixedCellSizeName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 128));
    public final ValuePropertyMetadata fixedEyeAtCameraZeroPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fixedEyeAtCameraZeroName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, _3D, 6));
    public final ValuePropertyMetadata focusTraversable_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.focusTraversableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 6));
    public final ValuePropertyMetadata focusTraversable_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.focusTraversableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 6));
    public final ValuePropertyMetadata fontPropertyMetadata =
            new FontPropertyMetadata(
                PropertyNames.fontName,
                true, /* readWrite */
                javafx.scene.text.Font.getDefault(), /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 5));
    public final ValuePropertyMetadata fontScalePropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.fontScaleName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 0));
    public final ValuePropertyMetadata fontSmoothingType_GRAY_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fontSmoothingTypeName,
                javafx.scene.text.FontSmoothingType.class,
                true, /* readWrite */
                javafx.scene.text.FontSmoothingType.GRAY, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 6));
    public final ValuePropertyMetadata fontSmoothingType_LCD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fontSmoothingTypeName,
                javafx.scene.text.FontSmoothingType.class,
                true, /* readWrite */
                javafx.scene.text.FontSmoothingType.LCD, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 6));
    public final ValuePropertyMetadata forceZeroInRangePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.forceZeroInRangeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 122));
    public final ValuePropertyMetadata fullScreenPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fullScreenName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 10));
    public final ValuePropertyMetadata fullScreenExitHintPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.fullScreenExitHintName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 11));
    public final ValuePropertyMetadata gapStartAndEndPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.gapStartAndEndName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 99));
    public final ValuePropertyMetadata graphicTextGapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.graphicTextGapName,
                true, /* readWrite */
                4.0, /* defaultValue */
                new InspectorPath(PROPERTIES, GRAPHIC, 0));
    public final ValuePropertyMetadata gridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.gridLinesVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 22));
    public final ValuePropertyMetadata halignment_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.halignmentName,
                javafx.geometry.HPos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, SPECIFIC, 4));
    public final ValuePropertyMetadata halignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.halignmentName,
                javafx.geometry.HPos.class,
                true, /* readWrite */
                javafx.geometry.HPos.CENTER, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 4));
    public final ValuePropertyMetadata hbarPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.hbarPolicyName,
                javafx.scene.control.ScrollPane.ScrollBarPolicy.class,
                true, /* readWrite */
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 48));
    public final ValuePropertyMetadata headerTextPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.headerTextName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 1));
    public final ValuePropertyMetadata height_Double_200_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.heightName,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 8));
    public final ValuePropertyMetadata height_Double_0_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.heightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 8));
    public final ValuePropertyMetadata height_Double_COMPUTED_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.heightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 8));
    public final ValuePropertyMetadata height_Double_ro_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.heightName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, SIZE, 8));
    public final ValuePropertyMetadata hgapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.hgapName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, INTERNAL, 0));
    public final ValuePropertyMetadata hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.hgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, SPECIFIC, 2));
    public final ValuePropertyMetadata hideOnClick_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnClickName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 23));
    public final ValuePropertyMetadata hideOnClick_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnClickName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 23));
    public final ValuePropertyMetadata hideOnEscapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnEscapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 65));
    public final ValuePropertyMetadata hmaxPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.hmaxName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 108));
    public final ValuePropertyMetadata hminPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.hminName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 104));
    public final ValuePropertyMetadata hvaluePropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.hvalueName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 106));
    
    public final PropertyMetadata hGroupPropertyMetadata =
            new DoubleBoundedPropertyGroupMetadata(
                PropertyNames.hGroupName,
                hminPropertyMetadata,
                hvaluePropertyMetadata,
                hmaxPropertyMetadata);
    
    public final ValuePropertyMetadata horizontalGridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.horizontalGridLinesVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 100));
    public final ValuePropertyMetadata horizontalZeroLineVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.horizontalZeroLineVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 110));
    public final ValuePropertyMetadata htmlTextPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.htmlTextName,
                true, /* readWrite */
                "<html><head></head><body contenteditable=\"true\"></body></html>", /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, SPECIFIC, 24));
    
    public final ValuePropertyMetadata iconifiedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.iconifiedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 12));
    public final ValuePropertyMetadata idPropertyMetadata =
            new IdStringPropertyMetadata(
                PropertyNames.idName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 3));
    public final ValuePropertyMetadata imagePropertyMetadata =
            new ImagePropertyMetadata(
                PropertyNames.imageName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 25));
    public final ValuePropertyMetadata indeterminate_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.indeterminateName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 49));
    public final ValuePropertyMetadata indeterminate_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.indeterminateName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 49));
    public final ValuePropertyMetadata insetsPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.insetsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, EXTRAS, 5));
    public final ValuePropertyMetadata labelPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.labelName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 26));
    public final ValuePropertyMetadata labelFormatterPropertyMetadata =
            new StringConverterPropertyMetadata(
                PropertyNames.labelFormatterName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 113));
    public final ValuePropertyMetadata labelLineLengthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.labelLineLengthName,
                true, /* readWrite */
                20.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 111));
    public final ValuePropertyMetadata labelPaddingPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.labelPaddingName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, EXTRAS, 6));
    public final ValuePropertyMetadata labelsVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.labelsVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 101));
    public final ValuePropertyMetadata largeArcFlagPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.largeArcFlagName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 126));
    public final ValuePropertyMetadata layoutBoundsPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.layoutBoundsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, BOUNDS, 0));
    public final ValuePropertyMetadata layoutXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.layoutXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 2));
    public final ValuePropertyMetadata layoutYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.layoutYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 3));
    public final ValuePropertyMetadata legendSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.legendSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.BOTTOM, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 74));
    public final ValuePropertyMetadata legendVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.legendVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 66));
    public final ValuePropertyMetadata length_Double_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.lengthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 18));
    public final ValuePropertyMetadata length_Integer_ro_PropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.lengthName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, SIZE, 18));
    public final ValuePropertyMetadata lightOnPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.lightOnName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 7));
    public final ValuePropertyMetadata lineSpacingPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.lineSpacingName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 15));
    public final ValuePropertyMetadata lowerBoundPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.lowerBoundName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 90));
    public final ValuePropertyMetadata majorTickUnitPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.majorTickUnitName,
                true, /* readWrite */
                25.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 91));
    public final ValuePropertyMetadata materialPropertyMetadata =
            new MaterialPropertyMetadata(
                PropertyNames.materialName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 0));
    public final ValuePropertyMetadata maxPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.maxName,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 50));
    public final ValuePropertyMetadata maxHeight_COMPUTED_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.maxHeightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 6));
    public final ValuePropertyMetadata maxHeight_MAX_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.maxHeightName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 6));
    public final ValuePropertyMetadata maxHeight_SIZE_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.maxHeightName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 6));
    public final ValuePropertyMetadata maxPageIndicatorCountPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.maxPageIndicatorCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath(PROPERTIES, PAGINATION, 1));
    public final ValuePropertyMetadata maxWidth_COMPUTED_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.maxWidthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 5));
    public final ValuePropertyMetadata maxWidth_500000_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.maxWidthName,
                true, /* readWrite */
                5000.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 5));
    public final ValuePropertyMetadata maxWidth_MAX_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.maxWidthName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 5));
    public final ValuePropertyMetadata maxWidth_SIZE_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.maxWidthName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 5));
    public final ValuePropertyMetadata maximizedPropertyMetdata =
            new BooleanPropertyMetadata(
                PropertyNames.maximizedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 13));
    public final ValuePropertyMetadata meshPropertyMetadata =
            new MeshPropertyMetadata(
                PropertyNames.meshName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 1));
    public final ValuePropertyMetadata minPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.minName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 29));
    public final ValuePropertyMetadata minHeight_COMPUTED_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.minHeightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 2));
    public final ValuePropertyMetadata minHeight_0_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.minHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 2));
    public final ValuePropertyMetadata minHeight_SIZE_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.minHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 2));
    public final ValuePropertyMetadata minorTickCount_3_PropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.minorTickCountName,
                true, /* readWrite */
                3, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 96));
    public final ValuePropertyMetadata minorTickCount_5_PropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.minorTickCountName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 96));
    public final ValuePropertyMetadata minorTickLengthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.minorTickLengthName,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 121));
    public final ValuePropertyMetadata minorTickVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.minorTickVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 119));
    public final ValuePropertyMetadata minViewportHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.minViewportHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 7));
    public final ValuePropertyMetadata minViewportWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.minViewportWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 6));
    public final ValuePropertyMetadata minWidth_COMPUTED_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.minWidthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 1));
    public final ValuePropertyMetadata minWidth_1000_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.minWidthName,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 1));
    public final ValuePropertyMetadata minWidth_0_PropertyMetadata =
            new ComputedAndPrefSizeDoublePropertyMetadata(
                PropertyNames.minWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 1));
    public final ValuePropertyMetadata minWidth_SIZE_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.minWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 1));
    public final ValuePropertyMetadata mnemonicParsing_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mnemonicParsingName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 1));
    public final ValuePropertyMetadata mnemonicParsing_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mnemonicParsingName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 1));
    public final ValuePropertyMetadata mouseTransparentPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mouseTransparentName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 7));
    public final ValuePropertyMetadata nearClipPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.nearClipName,
                true, /* readWrite */
                0.1, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 2));
    public final ValuePropertyMetadata nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.nodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                true, /* readWrite */
                javafx.geometry.NodeOrientation.LEFT_TO_RIGHT, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 4));
    public final ValuePropertyMetadata nodeOrientation_INHERIT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.nodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                true, /* readWrite */
                javafx.geometry.NodeOrientation.INHERIT, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 4));
    public final ValuePropertyMetadata onActionPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onActionName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MAIN, 0));
    public final ValuePropertyMetadata onAutoHidePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onAutoHideName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, HIDE_SHOW, 2));
    public final ValuePropertyMetadata onClosedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onClosedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, CLOSING, 1));
    public final ValuePropertyMetadata onCloseRequestPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onCloseRequestName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, CLOSING, 0));
    public final ValuePropertyMetadata onContextMenuRequestedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onContextMenuRequestedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 0));
    public final ValuePropertyMetadata onDragDetectedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDetectedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 0));
    public final ValuePropertyMetadata onDragDonePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDoneName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 1));
    public final ValuePropertyMetadata onDragDroppedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDroppedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 2));
    public final ValuePropertyMetadata onDragEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 3));
    public final ValuePropertyMetadata onDragExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 4));
    public final ValuePropertyMetadata onDragOverPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragOverName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 5));
    public final ValuePropertyMetadata onEditCancelPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditCancelName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, EDIT, 2));
    public final ValuePropertyMetadata onEditCommitPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditCommitName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, EDIT, 1));
    public final ValuePropertyMetadata onEditStartPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditStartName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, EDIT, 0));
    public final ValuePropertyMetadata onErrorPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onErrorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MAIN, 2));
    public final ValuePropertyMetadata onHiddenPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onHiddenName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, HIDE_SHOW, 0));
    public final ValuePropertyMetadata onHidingPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onHidingName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, HIDE_SHOW, 1));
    public final ValuePropertyMetadata onInputMethodTextChangedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onInputMethodTextChangedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, KEYBOARD, 0));
    public final ValuePropertyMetadata onKeyPressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyPressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, KEYBOARD, 1));
    public final ValuePropertyMetadata onKeyReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, KEYBOARD, 2));
    public final ValuePropertyMetadata onKeyTypedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyTypedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, KEYBOARD, 3));
    public final ValuePropertyMetadata onMenuValidationPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMenuValidationName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MAIN, 1));
    public final ValuePropertyMetadata onMouseClickedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseClickedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 1));
    public final ValuePropertyMetadata onMouseDragEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 6));
    public final ValuePropertyMetadata onMouseDragExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 7));
    public final ValuePropertyMetadata onMouseDraggedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDraggedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 2));
    public final ValuePropertyMetadata onMouseDragOverPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragOverName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 8));
    public final ValuePropertyMetadata onMouseDragReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, DRAG_DROP, 9));
    public final ValuePropertyMetadata onMouseEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 3));
    public final ValuePropertyMetadata onMouseExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 4));
    public final ValuePropertyMetadata onMouseMovedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseMovedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 5));
    public final ValuePropertyMetadata onMousePressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMousePressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 6));
    public final ValuePropertyMetadata onMouseReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 7));
    
    public final ValuePropertyMetadata onRotatePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotateName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ROTATION, 0));
    public final ValuePropertyMetadata onRotationFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotationFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ROTATION, 2));
    public final ValuePropertyMetadata onRotationStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotationStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ROTATION, 1));
    public final ValuePropertyMetadata onScrollPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 8));
    public final ValuePropertyMetadata onScrollFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 10));
    public final ValuePropertyMetadata onScrollStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 9));
    public final ValuePropertyMetadata onScrollToPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollToName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 11));
    public final ValuePropertyMetadata onScrollToColumnPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollToColumnName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MOUSE, 12));
    public final ValuePropertyMetadata onSelectionChangedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSelectionChangedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, EDIT, 3));
    public final ValuePropertyMetadata onShowingPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onShowingName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, HIDE_SHOW, 3));
    public final ValuePropertyMetadata onShownPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onShownName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, HIDE_SHOW, 4));
    public final ValuePropertyMetadata onSortPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSortName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, MAIN, 3));
    public final ValuePropertyMetadata onSwipeDownPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeDownName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, SWIPE, 3));
    public final ValuePropertyMetadata onSwipeLeftPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeLeftName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, SWIPE, 0));
    public final ValuePropertyMetadata onSwipeRightPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeRightName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, SWIPE, 1));
    public final ValuePropertyMetadata onSwipeUpPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeUpName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, SWIPE, 2));
    public final ValuePropertyMetadata onTouchMovedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchMovedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, TOUCH, 0));
    public final ValuePropertyMetadata onTouchPressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchPressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, TOUCH, 1));
    public final ValuePropertyMetadata onTouchReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, TOUCH, 2));
    public final ValuePropertyMetadata onTouchStationaryPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchStationaryName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, TOUCH, 3));
    public final ValuePropertyMetadata onZoomPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ZOOM, 0));
    public final ValuePropertyMetadata onZoomFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ZOOM, 2));
    public final ValuePropertyMetadata onZoomStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(CODE, ZOOM, 1));
    public final ValuePropertyMetadata opacityPropertyMetadata =
            new OpacityDoublePropertyMetadata(
                PropertyNames.opacityName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 2));
    public final ValuePropertyMetadata opaqueInsetsPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.opaqueInsetsName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 12));
    public final ValuePropertyMetadata orientation_HORIZONTAL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.orientationName,
                javafx.geometry.Orientation.class,
                true, /* readWrite */
                javafx.geometry.Orientation.HORIZONTAL, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 3));
    public final ValuePropertyMetadata orientation_VERTICAL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.orientationName,
                javafx.geometry.Orientation.class,
                true, /* readWrite */
                javafx.geometry.Orientation.VERTICAL, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 3));
    public final ValuePropertyMetadata paddingPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.paddingName,
                true, /* readWrite */
                javafx.geometry.Insets.EMPTY, /* defaultValue */
                new InspectorPath(LAYOUT, INTERNAL, 2));
    public final ValuePropertyMetadata pageCountPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.pageCountName,
                true, /* readWrite */
                2147483647, /* defaultValue */
                new InspectorPath(PROPERTIES, PAGINATION, 2));
    public final ValuePropertyMetadata pannablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pannableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 30));
    public final ValuePropertyMetadata percentHeightPropertyMetadata =
            new PercentageDoublePropertyMetadata(
                PropertyNames.percentHeightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 20));
    public final ValuePropertyMetadata percentWidthPropertyMetadata =
            new PercentageDoublePropertyMetadata(
                PropertyNames.percentWidthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 19));
    public final ValuePropertyMetadata pickOnBounds_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pickOnBoundsName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 8));
    public final ValuePropertyMetadata pickOnBounds_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pickOnBoundsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, EXTRAS, 8));
    public final ValuePropertyMetadata pointsPropertyMetadata =
            new DoubleListPropertyMetadata(
                PropertyNames.pointsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 4));
    public final ValuePropertyMetadata popupSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.popupSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.BOTTOM, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 31));
    //TODO this constant was created only if getSelectedClasses().size() == 1 // check why ?
    public final ValuePropertyMetadata prefColumnCount_40_PropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.prefColumnCountName,
                true, /* readWrite */
                40, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 9))
                .addConstant(DEFAULT_PREF_COLUMN_COUNT, TextField.DEFAULT_PREF_COLUMN_COUNT);
    //TODO this constant was created only if getSelectedClasses().size() == 1 // check why ?
    public final ValuePropertyMetadata prefColumnCount_12_PropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.prefColumnCountName,
                true, /* readWrite */
                12, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 9))
                .addConstant(DEFAULT_PREF_COLUMN_COUNT, TextField.DEFAULT_PREF_COLUMN_COUNT);
    public final ValuePropertyMetadata prefColumnsPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.prefColumnsName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 18));
    public final ValuePropertyMetadata prefHeight_COMPUTED_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.prefHeightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 4));
    public final ValuePropertyMetadata prefHeight_60000_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.prefHeightName,
                true, /* readWrite */
                600.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 4));
    public final ValuePropertyMetadata prefRowCountPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.prefRowCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 10))
                .addConstant(DEFAULT_PREF_ROW_COUNT, TextArea.DEFAULT_PREF_ROW_COUNT);
    public final ValuePropertyMetadata prefRowsPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.prefRowsName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 19));
    public final ValuePropertyMetadata prefTileHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.prefTileHeightName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 17));
    public final ValuePropertyMetadata prefTileWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.prefTileWidthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 16));
    public final ValuePropertyMetadata prefViewportHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.prefViewportHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 9));
    public final ValuePropertyMetadata prefViewportWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.prefViewportWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 8));
    public final ValuePropertyMetadata prefWidth_COMPUTED_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.prefWidthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 3));
    public final ValuePropertyMetadata prefWidth_8000_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.prefWidthName,
                true, /* readWrite */
                80.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 3));
    public final ValuePropertyMetadata prefWidth_80000_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.prefWidthName,
                true, /* readWrite */
                800.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 3));
    public final ValuePropertyMetadata prefWrapLengthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.prefWrapLengthName,
                true, /* readWrite */
                400.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 75));
    public final ValuePropertyMetadata preserveRatio_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.preserveRatioName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 32));
    public final ValuePropertyMetadata preserveRatio_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.preserveRatioName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 32));
    public final ValuePropertyMetadata progressPropertyMetadata =
            new ProgressDoublePropertyMetadata(
                PropertyNames.progressName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 33));
    public final ValuePropertyMetadata promptTextPropertyMetadata =
            new I18nStringPropertyMetadata(
                PropertyNames.promptTextName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 3));
    public final ValuePropertyMetadata radius_0_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.radiusName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 11));
    public final ValuePropertyMetadata radius_100_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.radiusName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 11));
    public final ValuePropertyMetadata radiusXPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.radiusXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 12));
    public final ValuePropertyMetadata radiusYPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.radiusYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 13));
    public final ValuePropertyMetadata resizable_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.resizableName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, EXTRAS, 3));
    public final ValuePropertyMetadata resizable_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.resizableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 3));
    public final ValuePropertyMetadata rotatePropertyMetadata =
            new AngleDoublePropertyMetadata(
                PropertyNames.rotateName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 0));
    public final ValuePropertyMetadata rotateGraphicPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.rotateGraphicName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 67));
    public final ValuePropertyMetadata rotationAxisPropertyMetadata =
            new Point3DPropertyMetadata(
                PropertyNames.rotationAxisName,
                true, /* readWrite */
                new javafx.geometry.Point3D(0.0, 0.0, 1.0), /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 1));
    public final ValuePropertyMetadata rowValignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.rowValignmentName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.CENTER, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 51));
    public final ValuePropertyMetadata scalePropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scaleName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 82));
    public final ValuePropertyMetadata scaleShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.scaleShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 9));
    public final ValuePropertyMetadata scaleXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scaleXName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 2));
    public final ValuePropertyMetadata scaleYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scaleYName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 3));
    public final ValuePropertyMetadata scaleZPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scaleZName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 4));
    public final ValuePropertyMetadata scrollLeftPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scrollLeftName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 16));
    public final ValuePropertyMetadata scrollTopPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.scrollTopName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 17));
    
    
    public final ValuePropertyMetadata selected_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.selectedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 35));
    public final ValuePropertyMetadata selected_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.selectedName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 35));
    public final ValuePropertyMetadata showRootPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showRootName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 52));
    public final ValuePropertyMetadata showTickLabelsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showTickLabelsName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 83));
    public final ValuePropertyMetadata showTickMarksPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showTickMarksName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 76));
    public final ValuePropertyMetadata showWeekNumbersPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showWeekNumbersName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 127));
    public final ValuePropertyMetadata side_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sideName,
                javafx.geometry.Side.class,
                BOTTOM, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(PROPERTIES, SPECIFIC, 36));
    public final ValuePropertyMetadata side_TOP_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.TOP, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 36));
    public final ValuePropertyMetadata smoothPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.smoothName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 53));
    public final ValuePropertyMetadata snapToPixelPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.snapToPixelName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 5));
    public final ValuePropertyMetadata snapToTicksPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.snapToTicksName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 102));
    public final ValuePropertyMetadata sortablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.sortableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 54));
    public final ValuePropertyMetadata sortModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sortModeName,
                javafx.scene.control.TreeSortMode.class,
                true, /* readWrite */
                javafx.scene.control.TreeSortMode.ALL_DESCENDANTS, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 56));
    public final ValuePropertyMetadata sortType_SortType_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sortTypeName,
                javafx.scene.control.TableColumn.SortType.class,
                true, /* readWrite */
                javafx.scene.control.TableColumn.SortType.ASCENDING, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 68));
    public final ValuePropertyMetadata spacingPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.spacingName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, INTERNAL, 3));
    public final ValuePropertyMetadata startAnglePropertyMetadata =
            new AngleDoublePropertyMetadata(
                PropertyNames.startAngleName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 17));
    public final ValuePropertyMetadata startMarginPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.startMarginName,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 92));
    public final ValuePropertyMetadata startXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.startXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 5));
    public final ValuePropertyMetadata startYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.startYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 6));
    public final ValuePropertyMetadata strikethroughPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.strikethroughName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 13));
    public final ValuePropertyMetadata stroke_BLACK_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.strokeName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 0));
    public final ValuePropertyMetadata stroke_NULL_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.strokeName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 0));
    public final ValuePropertyMetadata strokeDashOffsetPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.strokeDashOffsetName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 6));
    public final ValuePropertyMetadata strokeLineCapPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeLineCapName,
                javafx.scene.shape.StrokeLineCap.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeLineCap.SQUARE, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 3));
    public final ValuePropertyMetadata strokeLineJoinPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeLineJoinName,
                javafx.scene.shape.StrokeLineJoin.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeLineJoin.MITER, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 4));
    public final ValuePropertyMetadata strokeMiterLimitPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.strokeMiterLimitName,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 5));
    public final ValuePropertyMetadata strokeTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeTypeName,
                javafx.scene.shape.StrokeType.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeType.CENTERED, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 2));
    public final ValuePropertyMetadata strokeWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.strokeWidthName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, STROKE, 1));
    public final ValuePropertyMetadata stylePropertyMetadata =
            new StyleStringPropertyMetadata(
                PropertyNames.styleName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 0));
    public final ValuePropertyMetadata styleClass_c4_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("accordion"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c37_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c45_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("axis"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c1_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart","bar-chart"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c17_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("button"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c35_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("button-bar"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c41_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("radio-button"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c10_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("check-box"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c28_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","check-menu-item"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c43_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("choice-box"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c5_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","color-picker"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c11_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","combo-box"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c8_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("context-menu"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c25_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("hyperlink"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c27_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","custom-menu-item"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c9_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","date-picker"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c30_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("dialog-pane"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c21_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("html-editor"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c20_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("image-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c3_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("label"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c34_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("list-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c46_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("media-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c29_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","menu"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c18_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-bar"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c52_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-button"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c36_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_empty_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c39_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("pagination"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c53_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-field","password-field"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c13_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("progress-bar"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c50_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("progress-indicator"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c7_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","radio-menu-item"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c33_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("scroll-bar"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c38_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("scroll-pane"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c31_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("separator"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c23_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","custom-menu-item","separator-menu-item"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c40_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("slider"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c24_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("spinner"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c2_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("split-menu-button"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c14_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("split-pane"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c12_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart","stacked-bar-chart"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c19_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tab"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c6_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tab-pane"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c42_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("table-column"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c49_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("table-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c51_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-area"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c47_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-field"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c26_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("titled-pane"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c44_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("toggle-button"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c16_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tool-bar"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c15_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tooltip"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c32_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tree-table-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c22_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tree-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata styleClass_c48_PropertyMetadata =
            new StyleClassStringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("web-view"), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 1));
    public final ValuePropertyMetadata stylesheetsPropertyMetadata =
            new StylesheetsStringListPropertyMetadata(
                PropertyNames.stylesheetsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, JAVA_FX_CSS, 2));
    public final ValuePropertyMetadata sweepFlagPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.sweepFlagName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 124));
    public final ValuePropertyMetadata tabClosingPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.tabClosingPolicyName,
                javafx.scene.control.TabPane.TabClosingPolicy.class,
                true, /* readWrite */
                javafx.scene.control.TabPane.TabClosingPolicy.SELECTED_TAB, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 57));
    public final ValuePropertyMetadata tableMenuButtonVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tableMenuButtonVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 58));
    public final ValuePropertyMetadata tabMaxHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tabMaxHeightName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 15));
    public final ValuePropertyMetadata tabMaxWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tabMaxWidthName,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 14));
    public final ValuePropertyMetadata tabMinHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tabMinHeightName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 13));
    public final ValuePropertyMetadata tabMinWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tabMinWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 12));
    public final ValuePropertyMetadata textPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.textName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 4));
    public final ValuePropertyMetadata textAlignmentPropertyMetadata =
            new TextAlignmentEnumerationPropertyMetadata(
                PropertyNames.textAlignmentName,
                true, /* readWrite */
                javafx.scene.text.TextAlignment.LEFT, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 9));
    public final ValuePropertyMetadata textFillPropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.textFillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 7));
    public final ValuePropertyMetadata textOriginPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.textOriginName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.BASELINE, /* defaultValue */
                new InspectorPath(LAYOUT, EXTRAS, 6));
    public final ValuePropertyMetadata textOverrunPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.textOverrunName,
                javafx.scene.control.OverrunStyle.class,
                true, /* readWrite */
                javafx.scene.control.OverrunStyle.ELLIPSIS, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 11));
    public final ValuePropertyMetadata tickLabelFillPropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.tickLabelFillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 97));
    public final ValuePropertyMetadata tickLabelFontPropertyMetadata =
            new FontPropertyMetadata(
                PropertyNames.tickLabelFontName,
                true, /* readWrite */
                javafx.scene.text.Font.font("System",8.0), /* defaultValue */ //NOCHECK
                new InspectorPath(PROPERTIES, SPECIFIC, 93));
    public final ValuePropertyMetadata tickLabelFormatterPropertyMetadata =
            new StringConverterPropertyMetadata(
                PropertyNames.tickLabelFormatterName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 86));
    public final ValuePropertyMetadata tickLabelGapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tickLabelGapName,
                true, /* readWrite */
                3.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 103));
    public final ValuePropertyMetadata tickLabelRotationPropertyMetadata =
            new AngleDoublePropertyMetadata(
                PropertyNames.tickLabelRotationName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 112));
    public final ValuePropertyMetadata tickLabelsVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tickLabelsVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 85));
    public final ValuePropertyMetadata tickLengthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tickLengthName,
                true, /* readWrite */
                8.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 116));
    public final ValuePropertyMetadata tickMarksPropertyMetadata =
            new TickMarkListPropertyMetadata(
                PropertyNames.tickMarksName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 84));
    public final ValuePropertyMetadata tickMarkVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tickMarkVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 114));
    public final ValuePropertyMetadata tickUnitPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tickUnitName,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 120));
    public final ValuePropertyMetadata tileAlignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.tileAlignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 59));
    public final ValuePropertyMetadata tileHeightPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tileHeightName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, SPECIFIC, 21));
    public final ValuePropertyMetadata tileWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.tileWidthName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, SPECIFIC, 20));
    
    public final ValuePropertyMetadata titlePropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.titleName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 37));
    public final ValuePropertyMetadata titleSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.titleSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.TOP, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 60));
    public final ValuePropertyMetadata toggleGroupPropertyMetadata =
            new ToggleGroupPropertyMetadata(
                PropertyNames.toggleGroupName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 38));
    public final ValuePropertyMetadata translateXPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.translateXName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 5));
    public final ValuePropertyMetadata translateYPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.translateYName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 6));
    public final ValuePropertyMetadata translateZPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.translateZName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, TRANSFORMS, 7));
    public final ValuePropertyMetadata typePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.typeName,
                javafx.scene.shape.ArcType.class,
                true, /* readWrite */
                javafx.scene.shape.ArcType.OPEN, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 61));
    public final ValuePropertyMetadata underlinePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.underlineName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 14));
    public final ValuePropertyMetadata unitIncrementPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.unitIncrementName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 69));
    public final ValuePropertyMetadata upperBoundPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.upperBoundName,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 98));
    public final ValuePropertyMetadata userAgentStylesheetPropertyMetadata =
            new MultilineI18nStringPropertyMetadata(
                PropertyNames.userAgentStylesheetName,
                true, /* readWrite */
                EMPTY, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 129));
    public final ValuePropertyMetadata valignment_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.valignmentName,
                javafx.geometry.VPos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, SPECIFIC, 5));
    public final ValuePropertyMetadata valignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.valignmentName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.CENTER, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 5));
    public final ValuePropertyMetadata value_Object_PropertyMetadata =
            new ObjectPropertyMetadata(
                PropertyNames.valueName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 70));
    public final ValuePropertyMetadata value_Color_PropertyMetadata =
            new ColorPropertyMetadata(
                PropertyNames.valueName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 70));
    public final ValuePropertyMetadata value_Double_PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.valueName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 70));
    public final ValuePropertyMetadata value_Object_ro_PropertyMetadata =
            new ObjectPropertyMetadata(
                PropertyNames.valueName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 70));
    public final ValuePropertyMetadata vbarPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.vbarPolicyName,
                javafx.scene.control.ScrollPane.ScrollBarPolicy.class,
                true, /* readWrite */
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 71));
    public final ValuePropertyMetadata verticalFieldOfViewPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalFieldOfViewName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, _3D, 5));
    public final ValuePropertyMetadata verticalGridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalGridLinesVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 115));
    public final ValuePropertyMetadata verticalZeroLineVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalZeroLineVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 117));
    public final ValuePropertyMetadata vgapPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.vgapName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, INTERNAL, 1));
    public final ValuePropertyMetadata vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.vgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, SPECIFIC, 3));
    public final ValuePropertyMetadata viewportPropertyMetadata =
            new Rectangle2DPropertyMetadata(
                PropertyNames.viewportName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, SPECIFIC, 0));
    public final ValuePropertyMetadata viewportBoundsPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.viewportBoundsName,
                true, /* readWrite */
                new javafx.geometry.BoundingBox(0.0, 0.0, 0.0, 0.0), /* defaultValue */
                new InspectorPath(LAYOUT, BOUNDS, 1));
    public final ValuePropertyMetadata visiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.visibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(PROPERTIES, NODE, 5));
    public final ValuePropertyMetadata visibleAmountPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.visibleAmountName,
                true, /* readWrite */
                15.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 87));
    public final ValuePropertyMetadata visibleRowCountPropertyMetadata =
            new PositiveIntegerPropertyMetadata(
                PropertyNames.visibleRowCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 41));
    public final ValuePropertyMetadata visitedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.visitedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 42));
    public final ValuePropertyMetadata vmaxPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.vmaxName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 109));
    public final ValuePropertyMetadata vminPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.vminName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 105));
    public final ValuePropertyMetadata vvaluePropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.vvalueName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 107));
    
    public final PropertyMetadata vGroupPropertyMetadata =
            new DoubleBoundedPropertyGroupMetadata(
                PropertyNames.vGroupName,
                vminPropertyMetadata,
                vvaluePropertyMetadata,
                vmaxPropertyMetadata);
    
    public final ValuePropertyMetadata width_Double_200_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.widthName,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 7));
    public final ValuePropertyMetadata width_Double_0_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.widthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 7));
    public final ValuePropertyMetadata width_Double_COMPUTED_PropertyMetadata =
            new ComputedSizeDoublePropertyMetadata(
                PropertyNames.widthName,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath(LAYOUT, SIZE, 7));
    public final ValuePropertyMetadata width_Double_ro_PropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.widthName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(LAYOUT, SIZE, 7));
    public final ValuePropertyMetadata wrappingWidthPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.wrappingWidthName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 10));
    public final ValuePropertyMetadata wrapTextPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.wrapTextName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath(PROPERTIES, TEXT, 8));
    public final ValuePropertyMetadata x_0_PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.xName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 12));
    public final ValuePropertyMetadata x_NaN_PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.xName,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 12));
    public final ValuePropertyMetadata XAxisRotationPropertyMetadata =
            new AngleDoublePropertyMetadata(
                PropertyNames.XAxisRotationName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 125));
    public final ValuePropertyMetadata y_0_PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.yName,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 13));
    public final ValuePropertyMetadata y_NaN_PropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.yName,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath(LAYOUT, POSITION, 13));
    public final ValuePropertyMetadata zeroPositionPropertyMetadata =
            new CoordinateDoublePropertyMetadata(
                PropertyNames.zeroPositionName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath(PROPERTIES, SPECIFIC, 118));
    public final ValuePropertyMetadata zoomPropertyMetadata =
            new SizeDoublePropertyMetadata(
                PropertyNames.zoomName,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath(PROPERTIES, SPECIFIC, 123));
    public final ValuePropertyMetadata SplitPane_resizableWithParentPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.SplitPane_resizableWithParentName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath(LAYOUT, SPLIT_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata AnchorPane_bottomAnchorPropertyMetadata =
            new NullableCoordinateDoublePropertyMetadata(
                PropertyNames.AnchorPane_bottomAnchorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, ANCHOR_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata AnchorPane_leftAnchorPropertyMetadata =
            new NullableCoordinateDoublePropertyMetadata(
                PropertyNames.AnchorPane_leftAnchorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, ANCHOR_PANE_CONSTRAINTS, 1));
    public final ValuePropertyMetadata AnchorPane_rightAnchorPropertyMetadata =
            new NullableCoordinateDoublePropertyMetadata(
                PropertyNames.AnchorPane_rightAnchorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, ANCHOR_PANE_CONSTRAINTS, 2));
    public final ValuePropertyMetadata AnchorPane_topAnchorPropertyMetadata =
            new NullableCoordinateDoublePropertyMetadata(
                PropertyNames.AnchorPane_topAnchorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, ANCHOR_PANE_CONSTRAINTS, 3));
    public final PropertyMetadata AnchorPane_AnchorPropertyGroupMetadata = 
            new AnchorPropertyGroupMetadata(
                PropertyNames.AnchorPane_anchorsGroupName, 
                AnchorPane_topAnchorPropertyMetadata,  
                AnchorPane_rightAnchorPropertyMetadata,
                AnchorPane_bottomAnchorPropertyMetadata,
                AnchorPane_leftAnchorPropertyMetadata);
    public final ValuePropertyMetadata BorderPane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.BorderPane_alignmentName,
                javafx.geometry.Pos.class,
                "AUTOMATIC", /* null equivalent */ //NOCHECK
                true, /* readWrite */
                new InspectorPath(LAYOUT, BORDER_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata BorderPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.BorderPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, BORDER_PANE_CONSTRAINTS, 1));
    public final ValuePropertyMetadata FlowPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.FlowPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, FLOW_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata GridPane_columnIndexPropertyMetadata =
            new GridColumnIndexIntegerPropertyMetadata(
                PropertyNames.GridPane_columnIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 1));
    public final ValuePropertyMetadata GridPane_columnSpanPropertyMetadata =
            new GridColumnSpanIntegerPropertyMetadata(
                PropertyNames.GridPane_columnSpanName,
                true, /* readWrite */
                1, /* defaultValue */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 3))
                .setMin(1)
                .addConstant("REMAINING", GridPane.REMAINING); //NOCHECK
    public final ValuePropertyMetadata GridPane_halignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_halignmentName,
                javafx.geometry.HPos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 7));
    public final ValuePropertyMetadata GridPane_hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_hgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 4));
    public final ValuePropertyMetadata GridPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.GridPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 8));
    public final ValuePropertyMetadata GridPane_rowIndexPropertyMetadata =
            new GridRowIndexIntegerPropertyMetadata(
                PropertyNames.GridPane_rowIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata GridPane_rowSpanPropertyMetadata =
            new GridRowSpanIntegerPropertyMetadata(
                PropertyNames.GridPane_rowSpanName,
                true, /* readWrite */
                1, /* defaultValue */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 2))
                .setMin(1)
                .addConstant("REMAINING", GridPane.REMAINING); //NOCHECK
    public final ValuePropertyMetadata GridPane_valignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_valignmentName,
                javafx.geometry.VPos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 6));
    public final ValuePropertyMetadata GridPane_vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_vgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, GRID_PANE_CONSTRAINTS, 5));
    public final ValuePropertyMetadata HBox_hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.HBox_hgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, H_BOX_CONSTRAINTS, 0));
    public final ValuePropertyMetadata HBox_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.HBox_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, H_BOX_CONSTRAINTS, 1));
    public final ValuePropertyMetadata StackPane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.StackPane_alignmentName,
                javafx.geometry.Pos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, STACK_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata StackPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.StackPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, STACK_PANE_CONSTRAINTS, 1));
    public final ValuePropertyMetadata TilePane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.TilePane_alignmentName,
                javafx.geometry.Pos.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, TILE_PANE_CONSTRAINTS, 0));
    public final ValuePropertyMetadata TilePane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.TilePane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, TILE_PANE_CONSTRAINTS, 1));
    public final ValuePropertyMetadata VBox_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.VBox_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath(LAYOUT, V_BOX_CONSTRAINTS, 1));
    public final ValuePropertyMetadata VBox_vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.VBox_vgrowName,
                javafx.scene.layout.Priority.class,
                INHERIT, /* null equivalent */
                true, /* readWrite */
                new InspectorPath(LAYOUT, V_BOX_CONSTRAINTS, 0));

    public final ValuePropertyMetadata includeFxmlPropertyMetadata =
            new SourceStringListPropertyMetadata(
                PropertyNames.    source,
                    true, /* readWrite */
                    Collections.emptyList(), /* defaultValue */
                    new InspectorPath(PROPERTIES, INCLUDE_FXML_FILE, 2));


}
