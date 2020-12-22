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
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BoundsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.CursorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoubleArrayPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.FontPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.InsetsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ListCellPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.MaterialPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.MeshPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ObjectPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Point3DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.Rectangle2DPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringConverterPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ToggleGroupPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TreeTableViewResizePolicyPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.effect.EffectPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination.KeyCombinationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ButtonTypeListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.DoubleListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.TickMarkListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

@Component
public class ValuePropertyMetadataCatalog {

    // Property Metadata

    public final ValuePropertyMetadata absolutePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.absoluteName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 0));
    public final ValuePropertyMetadata acceleratorPropertyMetadata =
            new KeyCombinationPropertyMetadata(
                PropertyNames.acceleratorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 1));
    public final ValuePropertyMetadata accessibleHelpPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.accessibleHelpName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 1));
    public final ValuePropertyMetadata accessibleRole_NODE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.NODE, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.BUTTON, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TOGGLE_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TOGGLE_BUTTON, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_CHECK_BOX_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.CHECK_BOX, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_COMBO_BOX_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.COMBO_BOX, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_DATE_PICKER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.DATE_PICKER, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_HYPERLINK_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.HYPERLINK, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_IMAGE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.IMAGE_VIEW, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_LIST_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.LIST_VIEW, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_MENU_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.MENU_BAR, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_MENU_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.MENU_BUTTON, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_PARENT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PARENT, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_PAGINATION_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PAGINATION, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_PASSWORD_FIELD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PASSWORD_FIELD, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_PROGRESS_INDICATOR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.PROGRESS_INDICATOR, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_RADIO_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.RADIO_BUTTON, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_SCROLL_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SCROLL_BAR, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_SCROLL_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SCROLL_PANE, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_SLIDER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SLIDER, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_SPINNER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SPINNER, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_SPLIT_MENU_BUTTON_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.SPLIT_MENU_BUTTON, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TAB_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TAB_PANE, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TABLE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TABLE_VIEW, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_AREA_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT_AREA, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TEXT_FIELD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TEXT_FIELD, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TITLED_PANE_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TITLED_PANE, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TOOL_BAR_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TOOL_BAR, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TREE_TABLE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TREE_TABLE_VIEW, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRole_TREE_VIEW_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.accessibleRoleName,
                javafx.scene.AccessibleRole.class,
                true, /* readWrite */
                javafx.scene.AccessibleRole.TREE_VIEW, /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 2));
    public final ValuePropertyMetadata accessibleRoleDescriptionPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.accessibleRoleDescriptionName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 3));
    public final ValuePropertyMetadata accessibleTextPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.accessibleTextName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Accessibility", 0));
    public final ValuePropertyMetadata alignment_TOP_LEFT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.TOP_LEFT, /* defaultValue */
                new InspectorPath("Properties", "Node", 0));
    public final ValuePropertyMetadata alignment_CENTER_LEFT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER_LEFT, /* defaultValue */
                new InspectorPath("Properties", "Node", 0));
    public final ValuePropertyMetadata alignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.alignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER, /* defaultValue */
                new InspectorPath("Properties", "Node", 0));
    public final ValuePropertyMetadata allowIndeterminatePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.allowIndeterminateName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 2));
    public final ValuePropertyMetadata alternativeColumnFillVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alternativeColumnFillVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 88));
    public final ValuePropertyMetadata alternativeRowFillVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alternativeRowFillVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 94));
    public final ValuePropertyMetadata alwaysOnTopPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.alwaysOnTopName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Extras", 9));
    public final ValuePropertyMetadata anchorLocationPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.anchorLocationName,
                javafx.stage.PopupWindow.AnchorLocation.class,
                true, /* readWrite */
                javafx.stage.PopupWindow.AnchorLocation.CONTENT_TOP_LEFT, /* defaultValue */
                new InspectorPath("Layout", "Position", 11));
    public final ValuePropertyMetadata anchorXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.anchorXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath("Layout", "Position", 9));
    public final ValuePropertyMetadata anchorYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.anchorYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath("Layout", "Position", 10));
    public final ValuePropertyMetadata animatedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.animatedName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 43));
    public final ValuePropertyMetadata arcHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.arcHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 44));
    public final ValuePropertyMetadata arcWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.arcWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 3));
    public final ValuePropertyMetadata autoFixPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoFixName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 4));
    public final ValuePropertyMetadata autoHide_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoHideName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 45));
    public final ValuePropertyMetadata autoHide_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoHideName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 45));
    public final ValuePropertyMetadata autoRangingPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoRangingName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 72));
    public final ValuePropertyMetadata autoSizeChildrenPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.autoSizeChildrenName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Extras", 0));
    public final ValuePropertyMetadata axisSortingPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.axisSortingPolicyName,
                javafx.scene.chart.LineChart.SortingPolicy.class,
                true, /* readWrite */
                javafx.scene.chart.LineChart.SortingPolicy.X_AXIS, /* defaultValue */
                new InspectorPath("Properties", "Specific", 130));
    public final ValuePropertyMetadata barGapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.barGapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                4.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 77));
    public final ValuePropertyMetadata baselineOffsetPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.baselineOffsetName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Extras", 1));
    public final ValuePropertyMetadata blendModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.blendModeName,
                javafx.scene.effect.BlendMode.class,
                "SRC_OVER", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Properties", "Extras", 0));
    public final ValuePropertyMetadata blockIncrementPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.blockIncrementName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 73));
    public final ValuePropertyMetadata boundsInLocalPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.boundsInLocalName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Bounds", 2));
    public final ValuePropertyMetadata boundsInParentPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.boundsInParentName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Bounds", 3));
    public final ValuePropertyMetadata boundsTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.boundsTypeName,
                javafx.scene.text.TextBoundsType.class,
                true, /* readWrite */
                javafx.scene.text.TextBoundsType.LOGICAL, /* defaultValue */
                new InspectorPath("Layout", "Extras", 2));
    public final ValuePropertyMetadata buttonCellPropertyMetadata =
            new ListCellPropertyMetadata(
                PropertyNames.buttonCellName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 40));
    public final ValuePropertyMetadata buttonMinWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.buttonMinWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                70.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 0));
    public final ValuePropertyMetadata buttonOrderPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.buttonOrderName,
                true, /* readWrite */
                "L_HE+U+FBIX_NCYOA_R", /* defaultValue */
                new InspectorPath("Properties", "Specific", 5));
    public final ValuePropertyMetadata buttonTypesPropertyMetadata =
            new ButtonTypeListPropertyMetadata(
                PropertyNames.buttonTypesName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "Specific", 18));
    public final ValuePropertyMetadata cachePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cacheName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Extras", 2));
    public final ValuePropertyMetadata cacheHintPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.cacheHintName,
                javafx.scene.CacheHint.class,
                true, /* readWrite */
                javafx.scene.CacheHint.DEFAULT, /* defaultValue */
                new InspectorPath("Properties", "Extras", 3));
    public final ValuePropertyMetadata cacheShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cacheShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Node", 7));
    public final ValuePropertyMetadata cancelButtonPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.cancelButtonName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 46));
    public final ValuePropertyMetadata categoriesPropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.categoriesName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "Specific", 78));
    public final ValuePropertyMetadata categoryGapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.categoryGapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 79));
    public final ValuePropertyMetadata categorySpacingPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.categorySpacingName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 80));
    public final ValuePropertyMetadata centerShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.centerShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Node", 8));
    public final ValuePropertyMetadata centerXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.centerXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 0));
    public final ValuePropertyMetadata centerYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.centerYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 1));
    public final ValuePropertyMetadata clockwisePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.clockwiseName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 89));
    public final ValuePropertyMetadata closablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.closableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 6));
    public final ValuePropertyMetadata collapsiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.collapsibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 62));
    public final ValuePropertyMetadata colorPropertyMetadata =
            new ColorPropertyMetadata(
                PropertyNames.colorName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath("Properties", "Specific", 7));
    public final ValuePropertyMetadata columnHalignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.columnHalignmentName,
                javafx.geometry.HPos.class,
                true, /* readWrite */
                javafx.geometry.HPos.LEFT, /* defaultValue */
                new InspectorPath("Properties", "Specific", 63));
    public final ValuePropertyMetadata columnResizePolicy_TABLEVIEW_UNCONSTRAINED_PropertyMetadata =
            new TableViewResizePolicyPropertyMetadata(
                PropertyNames.columnResizePolicyName,
                true, /* readWrite */
                javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY, /* defaultValue */
                new InspectorPath("Properties", "Specific", 34));
    public final ValuePropertyMetadata columnResizePolicy_TREETABLEVIEW_UNCONSTRAINED_PropertyMetadata =
            new TreeTableViewResizePolicyPropertyMetadata(
                PropertyNames.columnResizePolicyName,
                true, /* readWrite */
                javafx.scene.control.TreeTableView.UNCONSTRAINED_RESIZE_POLICY, /* defaultValue */
                new InspectorPath("Properties", "Specific", 34));
    public final ValuePropertyMetadata consumeAutoHidingEventsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.consumeAutoHidingEventsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 64));
    public final ValuePropertyMetadata content_String_PropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.contentName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Specific", 10));
    public final ValuePropertyMetadata contentBiasPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.contentBiasName,
                javafx.geometry.Orientation.class,
                "NONE", /* null equivalent */
                false, /* readWrite */
                new InspectorPath("Layout", "Extras", 4));
    public final ValuePropertyMetadata contentDisplayPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.contentDisplayName,
                javafx.scene.control.ContentDisplay.class,
                true, /* readWrite */
                javafx.scene.control.ContentDisplay.LEFT, /* defaultValue */
                new InspectorPath("Properties", "Graphic", 1));
    public final ValuePropertyMetadata contentTextPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.contentTextName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Text", 2));
    public final ValuePropertyMetadata contextMenuEnabledPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.contextMenuEnabledName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 11));
    public final ValuePropertyMetadata controlXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 14));
    public final ValuePropertyMetadata controlX1PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlX1Name,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 16));
    public final ValuePropertyMetadata controlX2PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlX2Name,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 18));
    public final ValuePropertyMetadata controlYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 15));
    public final ValuePropertyMetadata controlY1PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlY1Name,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 17));
    public final ValuePropertyMetadata controlY2PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.controlY2Name,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 19));
    public final ValuePropertyMetadata createSymbolsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.createSymbolsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 81));
    public final ValuePropertyMetadata cullFacePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.cullFaceName,
                javafx.scene.shape.CullFace.class,
                true, /* readWrite */
                javafx.scene.shape.CullFace.BACK, /* defaultValue */
                new InspectorPath("Properties", "3D", 8));
    public final ValuePropertyMetadata currentPageIndexPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.currentPageIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath("Properties", "Pagination", 0));
    public final ValuePropertyMetadata cursor_HAND_PropertyMetadata =
            new CursorPropertyMetadata(
                PropertyNames.cursorName,
                true, /* readWrite */
                javafx.scene.Cursor.HAND, /* defaultValue */
                new InspectorPath("Properties", "Node", 13));
    public final ValuePropertyMetadata cursor_NULL_PropertyMetadata =
            new CursorPropertyMetadata(
                PropertyNames.cursorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Node", 13));
    public final ValuePropertyMetadata defaultButtonPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.defaultButtonName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 13));
    public final ValuePropertyMetadata depthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.depthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 16));
    public final ValuePropertyMetadata depthTestPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.depthTestName,
                javafx.scene.DepthTest.class,
                true, /* readWrite */
                javafx.scene.DepthTest.INHERIT, /* defaultValue */
                new InspectorPath("Properties", "Extras", 4));
    public final ValuePropertyMetadata disablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.disableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Node", 1));
    public final ValuePropertyMetadata dividerPositionsPropertyMetadata =
            new DoubleArrayPropertyMetadata(
                PropertyNames.dividerPositionsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "Specific", 15));
    public final ValuePropertyMetadata divisionsPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.divisionsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "3D", 10));
    public final ValuePropertyMetadata drawModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.drawModeName,
                javafx.scene.shape.DrawMode.class,
                true, /* readWrite */
                javafx.scene.shape.DrawMode.FILL, /* defaultValue */
                new InspectorPath("Properties", "3D", 9));
    public final ValuePropertyMetadata editable_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.editableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 16));
    public final ValuePropertyMetadata editable_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.editableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 16));
    public final ValuePropertyMetadata effectPropertyMetadata =
            new EffectPropertyMetadata(
                PropertyNames.effectName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Node", 14));
    public final ValuePropertyMetadata effectiveNodeOrientationPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.effectiveNodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Extras", 7));
    public final ValuePropertyMetadata ellipsisStringPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.ellipsisStringName,
                true, /* readWrite */
                "...", /* defaultValue */
                new InspectorPath("Properties", "Text", 12));
    public final ValuePropertyMetadata endMarginPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.endMarginName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 95));
    public final ValuePropertyMetadata endXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.endXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 7));
    public final ValuePropertyMetadata endYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.endYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 8));
    public final ValuePropertyMetadata expanded_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.expandedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 17));
    public final ValuePropertyMetadata expanded_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.expandedName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 17));
    public final ValuePropertyMetadata expandedItemCountPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.expandedItemCountName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 19));
    
    public final ValuePropertyMetadata farClipPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.farClipName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath("Properties", "3D", 3));
    public final ValuePropertyMetadata fieldOfViewPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.fieldOfViewName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.ANGLE,
                true, /* readWrite */
                30.0, /* defaultValue */
                new InspectorPath("Properties", "3D", 4));
    public final ValuePropertyMetadata fill_NULL_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 20));
    public final ValuePropertyMetadata fill_BLACK_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath("Properties", "Specific", 20));
    public final ValuePropertyMetadata fill_WHITE_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.fillName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath("Properties", "Specific", 20));
    public final ValuePropertyMetadata fillHeightPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fillHeightName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Specific", 0));
    public final ValuePropertyMetadata fillRulePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fillRuleName,
                javafx.scene.shape.FillRule.class,
                true, /* readWrite */
                javafx.scene.shape.FillRule.NON_ZERO, /* defaultValue */
                new InspectorPath("Properties", "Specific", 21));
    public final ValuePropertyMetadata fillWidthPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fillWidthName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Specific", 1));
    public final ValuePropertyMetadata fitHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.fitHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 15));
    public final ValuePropertyMetadata fitToHeightPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fitToHeightName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Specific", 11));
    public final ValuePropertyMetadata fitToWidthPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fitToWidthName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Specific", 10));
    public final ValuePropertyMetadata fitWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.fitWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 14));
    public final ValuePropertyMetadata fixedCellSizePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.fixedCellSizeName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 128));
    public final ValuePropertyMetadata fixedEyeAtCameraZeroPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fixedEyeAtCameraZeroName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "3D", 6));
    public final ValuePropertyMetadata focusTraversable_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.focusTraversableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Node", 6));
    public final ValuePropertyMetadata focusTraversable_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.focusTraversableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Node", 6));
    public final ValuePropertyMetadata fontPropertyMetadata =
            new FontPropertyMetadata(
                PropertyNames.fontName,
                true, /* readWrite */
                javafx.scene.text.Font.getDefault(), /* defaultValue */
                new InspectorPath("Properties", "Text", 5));
    public final ValuePropertyMetadata fontScalePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.fontScaleName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Text", 0));
    public final ValuePropertyMetadata fontSmoothingType_GRAY_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fontSmoothingTypeName,
                javafx.scene.text.FontSmoothingType.class,
                true, /* readWrite */
                javafx.scene.text.FontSmoothingType.GRAY, /* defaultValue */
                new InspectorPath("Properties", "Text", 6));
    public final ValuePropertyMetadata fontSmoothingType_LCD_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.fontSmoothingTypeName,
                javafx.scene.text.FontSmoothingType.class,
                true, /* readWrite */
                javafx.scene.text.FontSmoothingType.LCD, /* defaultValue */
                new InspectorPath("Properties", "Text", 6));
    public final ValuePropertyMetadata forceZeroInRangePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.forceZeroInRangeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 122));
    public final ValuePropertyMetadata fullScreenPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.fullScreenName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Extras", 10));
    public final ValuePropertyMetadata fullScreenExitHintPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.fullScreenExitHintName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Layout", "Extras", 11));
    public final ValuePropertyMetadata gapStartAndEndPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.gapStartAndEndName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 99));
    public final ValuePropertyMetadata graphicTextGapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.graphicTextGapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                4.0, /* defaultValue */
                new InspectorPath("Properties", "Graphic", 0));
    public final ValuePropertyMetadata gridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.gridLinesVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 22));
    public final ValuePropertyMetadata halignment_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.halignmentName,
                javafx.geometry.HPos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Specific", 4));
    public final ValuePropertyMetadata halignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.halignmentName,
                javafx.geometry.HPos.class,
                true, /* readWrite */
                javafx.geometry.HPos.CENTER, /* defaultValue */
                new InspectorPath("Layout", "Specific", 4));
    public final ValuePropertyMetadata hbarPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.hbarPolicyName,
                javafx.scene.control.ScrollPane.ScrollBarPolicy.class,
                true, /* readWrite */
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, /* defaultValue */
                new InspectorPath("Properties", "Specific", 48));
    public final ValuePropertyMetadata headerTextPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.headerTextName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Text", 1));
    public final ValuePropertyMetadata height_Double_200_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.heightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 8));
    public final ValuePropertyMetadata height_Double_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.heightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 8));
    public final ValuePropertyMetadata height_Double_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.heightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 8));
    public final ValuePropertyMetadata height_Double_ro_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.heightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Size", 8));
    public final ValuePropertyMetadata hgapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.hgapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Internal", 0));
    public final ValuePropertyMetadata hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.hgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Specific", 2));
    public final ValuePropertyMetadata hideOnClick_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnClickName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 23));
    public final ValuePropertyMetadata hideOnClick_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnClickName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 23));
    public final ValuePropertyMetadata hideOnEscapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.hideOnEscapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 65));
    public final ValuePropertyMetadata hmaxPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.hmaxName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 108));
    public final ValuePropertyMetadata hminPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.hminName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 104));
    public final ValuePropertyMetadata horizontalGridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.horizontalGridLinesVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 100));
    public final ValuePropertyMetadata horizontalZeroLineVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.horizontalZeroLineVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 110));
    public final ValuePropertyMetadata htmlTextPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.htmlTextName,
                true, /* readWrite */
                "<html><head></head><body contenteditable=\"true\"></body></html>", /* defaultValue */
                new InspectorPath("Properties", "Specific", 24));
    public final ValuePropertyMetadata hvaluePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.hvalueName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 106));
    public final ValuePropertyMetadata iconifiedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.iconifiedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Extras", 12));
    public final ValuePropertyMetadata idPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.idName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 3));
    public final ValuePropertyMetadata imagePropertyMetadata =
            new ImagePropertyMetadata(
                PropertyNames.imageName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 25));
    public final ValuePropertyMetadata indeterminate_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.indeterminateName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 49));
    public final ValuePropertyMetadata indeterminate_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.indeterminateName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 49));
    public final ValuePropertyMetadata insetsPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.insetsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Extras", 5));
    public final ValuePropertyMetadata labelPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.labelName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Specific", 26));
    public final ValuePropertyMetadata labelFormatterPropertyMetadata =
            new StringConverterPropertyMetadata(
                PropertyNames.labelFormatterName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 113));
    public final ValuePropertyMetadata labelLineLengthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.labelLineLengthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                20.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 111));
    public final ValuePropertyMetadata labelPaddingPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.labelPaddingName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Extras", 6));
    public final ValuePropertyMetadata labelsVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.labelsVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 101));
    public final ValuePropertyMetadata largeArcFlagPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.largeArcFlagName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 126));
    public final ValuePropertyMetadata layoutBoundsPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.layoutBoundsName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Bounds", 0));
    public final ValuePropertyMetadata layoutXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.layoutXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 2));
    public final ValuePropertyMetadata layoutYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.layoutYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 3));
    public final ValuePropertyMetadata legendSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.legendSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.BOTTOM, /* defaultValue */
                new InspectorPath("Properties", "Specific", 74));
    public final ValuePropertyMetadata legendVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.legendVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 66));
    public final ValuePropertyMetadata length_Double_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.lengthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 18));
    public final ValuePropertyMetadata length_Integer_ro_PropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.lengthName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Size", 18));
    public final ValuePropertyMetadata lightOnPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.lightOnName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "3D", 7));
    public final ValuePropertyMetadata lineSpacingPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.lineSpacingName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Text", 15));
    public final ValuePropertyMetadata lowerBoundPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.lowerBoundName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 90));
    public final ValuePropertyMetadata majorTickUnitPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.majorTickUnitName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                25.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 91));
    public final ValuePropertyMetadata materialPropertyMetadata =
            new MaterialPropertyMetadata(
                PropertyNames.materialName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "3D", 0));
    public final ValuePropertyMetadata maxPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 50));
    public final ValuePropertyMetadata maxHeight_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 6));
    public final ValuePropertyMetadata maxHeight_MAX_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Size", 6));
    public final ValuePropertyMetadata maxHeight_SIZE_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Size", 6));
    public final ValuePropertyMetadata maxPageIndicatorCountPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.maxPageIndicatorCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath("Properties", "Pagination", 1));
    public final ValuePropertyMetadata maxWidth_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 5));
    public final ValuePropertyMetadata maxWidth_500000_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                5000.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 5));
    public final ValuePropertyMetadata maxWidth_MAX_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Size", 5));
    public final ValuePropertyMetadata maxWidth_SIZE_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.maxWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Size", 5));
    public final ValuePropertyMetadata maximizedPropertyMetdata =
            new BooleanPropertyMetadata(
                PropertyNames.maximizedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Layout", "Extras", 13));
    public final ValuePropertyMetadata meshPropertyMetadata =
            new MeshPropertyMetadata(
                PropertyNames.meshName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "3D", 1));
    public final ValuePropertyMetadata minPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 29));
    public final ValuePropertyMetadata minHeight_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 2));
    public final ValuePropertyMetadata minHeight_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 2));
    public final ValuePropertyMetadata minHeight_SIZE_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 2));
    public final ValuePropertyMetadata minorTickCount_3_PropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.minorTickCountName,
                true, /* readWrite */
                3, /* defaultValue */
                new InspectorPath("Properties", "Specific", 96));
    public final ValuePropertyMetadata minorTickCount_5_PropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.minorTickCountName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath("Properties", "Specific", 96));
    public final ValuePropertyMetadata minorTickLengthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minorTickLengthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 121));
    public final ValuePropertyMetadata minorTickVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.minorTickVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 119));
    public final ValuePropertyMetadata minViewportHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minViewportHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 7));
    public final ValuePropertyMetadata minViewportWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minViewportWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 6));
    public final ValuePropertyMetadata minWidth_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 1));
    public final ValuePropertyMetadata minWidth_1000_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 1));
    public final ValuePropertyMetadata minWidth_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_PREF_SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 1));
    public final ValuePropertyMetadata minWidth_SIZE_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.minWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 1));
    public final ValuePropertyMetadata mnemonicParsing_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mnemonicParsingName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Extras", 1));
    public final ValuePropertyMetadata mnemonicParsing_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mnemonicParsingName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Extras", 1));
    public final ValuePropertyMetadata mouseTransparentPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.mouseTransparentName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Extras", 7));
    public final ValuePropertyMetadata nearClipPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.nearClipName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.1, /* defaultValue */
                new InspectorPath("Properties", "3D", 2));
    public final ValuePropertyMetadata nodeOrientation_LEFT_TO_RIGHT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.nodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                true, /* readWrite */
                javafx.geometry.NodeOrientation.LEFT_TO_RIGHT, /* defaultValue */
                new InspectorPath("Properties", "Node", 4));
    public final ValuePropertyMetadata nodeOrientation_INHERIT_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.nodeOrientationName,
                javafx.geometry.NodeOrientation.class,
                true, /* readWrite */
                javafx.geometry.NodeOrientation.INHERIT, /* defaultValue */
                new InspectorPath("Properties", "Node", 4));
    public final ValuePropertyMetadata onActionPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onActionName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Main", 0));
    public final ValuePropertyMetadata onAutoHidePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onAutoHideName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "HideShow", 2));
    public final ValuePropertyMetadata onClosedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onClosedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Closing", 1));
    public final ValuePropertyMetadata onCloseRequestPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onCloseRequestName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Closing", 0));
    public final ValuePropertyMetadata onContextMenuRequestedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onContextMenuRequestedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 0));
    public final ValuePropertyMetadata onDragDetectedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDetectedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 0));
    public final ValuePropertyMetadata onDragDonePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDoneName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 1));
    public final ValuePropertyMetadata onDragDroppedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragDroppedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 2));
    public final ValuePropertyMetadata onDragEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 3));
    public final ValuePropertyMetadata onDragExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 4));
    public final ValuePropertyMetadata onDragOverPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onDragOverName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 5));
    public final ValuePropertyMetadata onEditCancelPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditCancelName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Edit", 2));
    public final ValuePropertyMetadata onEditCommitPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditCommitName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Edit", 1));
    public final ValuePropertyMetadata onEditStartPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onEditStartName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Edit", 0));
    public final ValuePropertyMetadata onErrorPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onErrorName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Main", 2));
    public final ValuePropertyMetadata onHiddenPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onHiddenName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "HideShow", 0));
    public final ValuePropertyMetadata onHidingPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onHidingName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "HideShow", 1));
    public final ValuePropertyMetadata onInputMethodTextChangedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onInputMethodTextChangedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Keyboard", 0));
    public final ValuePropertyMetadata onKeyPressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyPressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Keyboard", 1));
    public final ValuePropertyMetadata onKeyReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Keyboard", 2));
    public final ValuePropertyMetadata onKeyTypedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onKeyTypedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Keyboard", 3));
    public final ValuePropertyMetadata onMenuValidationPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMenuValidationName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Main", 1));
    public final ValuePropertyMetadata onMouseClickedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseClickedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 1));
    public final ValuePropertyMetadata onMouseDragEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 6));
    public final ValuePropertyMetadata onMouseDragExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 7));
    public final ValuePropertyMetadata onMouseDraggedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDraggedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 2));
    public final ValuePropertyMetadata onMouseDragOverPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragOverName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 8));
    public final ValuePropertyMetadata onMouseDragReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseDragReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "DragDrop", 9));
    public final ValuePropertyMetadata onMouseEnteredPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseEnteredName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 3));
    public final ValuePropertyMetadata onMouseExitedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseExitedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 4));
    public final ValuePropertyMetadata onMouseMovedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseMovedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 5));
    public final ValuePropertyMetadata onMousePressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMousePressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 6));
    public final ValuePropertyMetadata onMouseReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onMouseReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 7));
    
    public final ValuePropertyMetadata onRotatePropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotateName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Rotation", 0));
    public final ValuePropertyMetadata onRotationFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotationFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Rotation", 2));
    public final ValuePropertyMetadata onRotationStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onRotationStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Rotation", 1));
    public final ValuePropertyMetadata onScrollPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 8));
    public final ValuePropertyMetadata onScrollFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 10));
    public final ValuePropertyMetadata onScrollStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 9));
    public final ValuePropertyMetadata onScrollToPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollToName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 11));
    public final ValuePropertyMetadata onScrollToColumnPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onScrollToColumnName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Mouse", 12));
    public final ValuePropertyMetadata onSelectionChangedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSelectionChangedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Edit", 3));
    public final ValuePropertyMetadata onShowingPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onShowingName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "HideShow", 3));
    public final ValuePropertyMetadata onShownPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onShownName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "HideShow", 4));
    public final ValuePropertyMetadata onSortPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSortName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Main", 3));
    public final ValuePropertyMetadata onSwipeDownPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeDownName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Swipe", 3));
    public final ValuePropertyMetadata onSwipeLeftPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeLeftName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Swipe", 0));
    public final ValuePropertyMetadata onSwipeRightPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeRightName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Swipe", 1));
    public final ValuePropertyMetadata onSwipeUpPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onSwipeUpName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Swipe", 2));
    public final ValuePropertyMetadata onTouchMovedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchMovedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Touch", 0));
    public final ValuePropertyMetadata onTouchPressedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchPressedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Touch", 1));
    public final ValuePropertyMetadata onTouchReleasedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchReleasedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Touch", 2));
    public final ValuePropertyMetadata onTouchStationaryPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onTouchStationaryName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Touch", 3));
    public final ValuePropertyMetadata onZoomPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Zoom", 0));
    public final ValuePropertyMetadata onZoomFinishedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomFinishedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Zoom", 2));
    public final ValuePropertyMetadata onZoomStartedPropertyMetadata =
            new EventHandlerPropertyMetadata(
                PropertyNames.onZoomStartedName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Code", "Zoom", 1));
    public final ValuePropertyMetadata opacityPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.opacityName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.OPACITY,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Node", 2));
    public final ValuePropertyMetadata opaqueInsetsPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.opaqueInsetsName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Node", 12));
    public final ValuePropertyMetadata orientation_HORIZONTAL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.orientationName,
                javafx.geometry.Orientation.class,
                true, /* readWrite */
                javafx.geometry.Orientation.HORIZONTAL, /* defaultValue */
                new InspectorPath("Properties", "Node", 3));
    public final ValuePropertyMetadata orientation_VERTICAL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.orientationName,
                javafx.geometry.Orientation.class,
                true, /* readWrite */
                javafx.geometry.Orientation.VERTICAL, /* defaultValue */
                new InspectorPath("Properties", "Node", 3));
    public final ValuePropertyMetadata paddingPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.paddingName,
                true, /* readWrite */
                javafx.geometry.Insets.EMPTY, /* defaultValue */
                new InspectorPath("Layout", "Internal", 2));
    public final ValuePropertyMetadata pageCountPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.pageCountName,
                true, /* readWrite */
                2147483647, /* defaultValue */
                new InspectorPath("Properties", "Pagination", 2));
    public final ValuePropertyMetadata pannablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pannableName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 30));
    public final ValuePropertyMetadata percentHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.percentHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.PERCENTAGE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 20));
    public final ValuePropertyMetadata percentWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.percentWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.PERCENTAGE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 19));
    public final ValuePropertyMetadata pickOnBounds_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pickOnBoundsName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Extras", 8));
    public final ValuePropertyMetadata pickOnBounds_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.pickOnBoundsName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Extras", 8));
    public final ValuePropertyMetadata pointsPropertyMetadata =
            new DoubleListPropertyMetadata(
                PropertyNames.pointsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Layout", "Position", 4));
    public final ValuePropertyMetadata popupSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.popupSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.BOTTOM, /* defaultValue */
                new InspectorPath("Properties", "Specific", 31));
    public final ValuePropertyMetadata prefColumnCount_40_PropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.prefColumnCountName,
                true, /* readWrite */
                40, /* defaultValue */
                new InspectorPath("Layout", "Size", 9));
    public final ValuePropertyMetadata prefColumnCount_12_PropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.prefColumnCountName,
                true, /* readWrite */
                12, /* defaultValue */
                new InspectorPath("Layout", "Size", 9));
    public final ValuePropertyMetadata prefColumnsPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.prefColumnsName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath("Layout", "Specific", 18));
    public final ValuePropertyMetadata prefHeight_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 4));
    public final ValuePropertyMetadata prefHeight_60000_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                600.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 4));
    public final ValuePropertyMetadata prefRowCountPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.prefRowCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath("Layout", "Size", 10));
    public final ValuePropertyMetadata prefRowsPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.prefRowsName,
                true, /* readWrite */
                5, /* defaultValue */
                new InspectorPath("Layout", "Specific", 19));
    public final ValuePropertyMetadata prefTileHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefTileHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 17));
    public final ValuePropertyMetadata prefTileWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefTileWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 16));
    public final ValuePropertyMetadata prefViewportHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefViewportHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 9));
    public final ValuePropertyMetadata prefViewportWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefViewportWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 8));
    public final ValuePropertyMetadata prefWidth_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 3));
    public final ValuePropertyMetadata prefWidth_8000_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                80.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 3));
    public final ValuePropertyMetadata prefWidth_80000_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                800.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 3));
    public final ValuePropertyMetadata prefWrapLengthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.prefWrapLengthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                400.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 75));
    public final ValuePropertyMetadata preserveRatio_false_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.preserveRatioName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 32));
    public final ValuePropertyMetadata preserveRatio_true_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.preserveRatioName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 32));
    public final ValuePropertyMetadata progressPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.progressName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.PROGRESS,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 33));
    public final ValuePropertyMetadata promptTextPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.promptTextName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Text", 3));
    public final ValuePropertyMetadata radius_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.radiusName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 11));
    public final ValuePropertyMetadata radius_100_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.radiusName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 11));
    public final ValuePropertyMetadata radiusXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.radiusXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 12));
    public final ValuePropertyMetadata radiusYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.radiusYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 13));
    public final ValuePropertyMetadata resizable_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.resizableName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Extras", 3));
    public final ValuePropertyMetadata resizable_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.resizableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Extras", 3));
    public final ValuePropertyMetadata rotatePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.rotateName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.ANGLE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 0));
    public final ValuePropertyMetadata rotateGraphicPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.rotateGraphicName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 67));
    public final ValuePropertyMetadata rotationAxisPropertyMetadata =
            new Point3DPropertyMetadata(
                PropertyNames.rotationAxisName,
                true, /* readWrite */
                new javafx.geometry.Point3D(0.0, 0.0, 1.0), /* defaultValue */
                new InspectorPath("Layout", "Transforms", 1));
    public final ValuePropertyMetadata rowValignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.rowValignmentName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.CENTER, /* defaultValue */
                new InspectorPath("Properties", "Specific", 51));
    public final ValuePropertyMetadata scalePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scaleName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 82));
    public final ValuePropertyMetadata scaleShapePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.scaleShapeName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Node", 9));
    public final ValuePropertyMetadata scaleXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scaleXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 2));
    public final ValuePropertyMetadata scaleYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scaleYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 3));
    public final ValuePropertyMetadata scaleZPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scaleZName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 4));
    public final ValuePropertyMetadata scrollLeftPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scrollLeftName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Text", 16));
    public final ValuePropertyMetadata scrollTopPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.scrollTopName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Text", 17));
    
    
    public final ValuePropertyMetadata selected_Boolean_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.selectedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 35));
    public final ValuePropertyMetadata selected_Boolean_ro_PropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.selectedName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 35));
    public final ValuePropertyMetadata showRootPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showRootName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 52));
    public final ValuePropertyMetadata showTickLabelsPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showTickLabelsName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 83));
    public final ValuePropertyMetadata showTickMarksPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showTickMarksName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 76));
    public final ValuePropertyMetadata showWeekNumbersPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.showWeekNumbersName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 127));
    public final ValuePropertyMetadata side_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sideName,
                javafx.geometry.Side.class,
                "BOTTOM", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Properties", "Specific", 36));
    public final ValuePropertyMetadata side_TOP_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.TOP, /* defaultValue */
                new InspectorPath("Properties", "Specific", 36));
    public final ValuePropertyMetadata smoothPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.smoothName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 53));
    public final ValuePropertyMetadata snapToPixelPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.snapToPixelName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Extras", 5));
    public final ValuePropertyMetadata snapToTicksPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.snapToTicksName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 102));
    public final ValuePropertyMetadata sortablePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.sortableName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 54));
    public final ValuePropertyMetadata sortModePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sortModeName,
                javafx.scene.control.TreeSortMode.class,
                true, /* readWrite */
                javafx.scene.control.TreeSortMode.ALL_DESCENDANTS, /* defaultValue */
                new InspectorPath("Properties", "Specific", 56));
    public final ValuePropertyMetadata sortType_SortType_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.sortTypeName,
                javafx.scene.control.TableColumn.SortType.class,
                true, /* readWrite */
                javafx.scene.control.TableColumn.SortType.ASCENDING, /* defaultValue */
                new InspectorPath("Properties", "Specific", 68));
    public final ValuePropertyMetadata spacingPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.spacingName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Internal", 3));
    public final ValuePropertyMetadata startAnglePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.startAngleName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.ANGLE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 17));
    public final ValuePropertyMetadata startMarginPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.startMarginName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 92));
    public final ValuePropertyMetadata startXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.startXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 5));
    public final ValuePropertyMetadata startYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.startYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 6));
    public final ValuePropertyMetadata strikethroughPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.strikethroughName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Text", 13));
    public final ValuePropertyMetadata stroke_BLACK_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.strokeName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 0));
    public final ValuePropertyMetadata stroke_NULL_PropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.strokeName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 0));
    public final ValuePropertyMetadata strokeDashOffsetPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.strokeDashOffsetName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 6));
    public final ValuePropertyMetadata strokeLineCapPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeLineCapName,
                javafx.scene.shape.StrokeLineCap.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeLineCap.SQUARE, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 3));
    public final ValuePropertyMetadata strokeLineJoinPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeLineJoinName,
                javafx.scene.shape.StrokeLineJoin.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeLineJoin.MITER, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 4));
    public final ValuePropertyMetadata strokeMiterLimitPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.strokeMiterLimitName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                10.0, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 5));
    public final ValuePropertyMetadata strokeTypePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.strokeTypeName,
                javafx.scene.shape.StrokeType.class,
                true, /* readWrite */
                javafx.scene.shape.StrokeType.CENTERED, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 2));
    public final ValuePropertyMetadata strokeWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.strokeWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Stroke", 1));
    public final ValuePropertyMetadata stylePropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.styleName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 0));
    public final ValuePropertyMetadata styleClass_c4_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("accordion"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c37_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c45_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("axis"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c1_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart","bar-chart"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c17_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("button"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c35_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("button-bar"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c41_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("radio-button"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c10_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("check-box"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c28_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","check-menu-item"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c43_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("choice-box"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c5_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","color-picker"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c11_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","combo-box"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c8_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("context-menu"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c25_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("hyperlink"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c27_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","custom-menu-item"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c9_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("combo-box-base","date-picker"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c30_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("dialog-pane"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c21_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("html-editor"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c20_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("image-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c3_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("label"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c34_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("list-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c46_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("media-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c29_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","menu"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c18_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-bar"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c52_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-button"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c36_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_empty_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c39_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("pagination"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c53_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-field","password-field"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c13_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("progress-bar"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c50_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("progress-indicator"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c7_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","radio-menu-item"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c33_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("scroll-bar"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c38_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("scroll-pane"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c31_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("separator"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c23_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("menu-item","custom-menu-item","separator-menu-item"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c40_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("slider"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c24_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("spinner"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c2_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("split-menu-button"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c14_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("split-pane"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c12_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("chart","stacked-bar-chart"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c19_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tab"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c6_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tab-pane"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c42_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("table-column"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c49_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("table-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c51_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-area"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c47_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("text-input","text-field"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c26_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("titled-pane"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c44_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("toggle-button"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c16_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tool-bar"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c15_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tooltip"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c32_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tree-table-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c22_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("tree-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata styleClass_c48_PropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.styleClassName,
                true, /* readWrite */
                Arrays.asList("web-view"), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 1));
    public final ValuePropertyMetadata stylesheetsPropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.stylesheetsName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "JavaFX CSS", 2));
    public final ValuePropertyMetadata sweepFlagPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.sweepFlagName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 124));
    public final ValuePropertyMetadata tabClosingPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.tabClosingPolicyName,
                javafx.scene.control.TabPane.TabClosingPolicy.class,
                true, /* readWrite */
                javafx.scene.control.TabPane.TabClosingPolicy.SELECTED_TAB, /* defaultValue */
                new InspectorPath("Properties", "Specific", 57));
    public final ValuePropertyMetadata tableMenuButtonVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tableMenuButtonVisibleName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 58));
    public final ValuePropertyMetadata tabMaxHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tabMaxHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Specific", 15));
    public final ValuePropertyMetadata tabMaxWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tabMaxWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                Double.MAX_VALUE, /* defaultValue */
                new InspectorPath("Layout", "Specific", 14));
    public final ValuePropertyMetadata tabMinHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tabMinHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 13));
    public final ValuePropertyMetadata tabMinWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tabMinWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Specific", 12));
    public final ValuePropertyMetadata textPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.textName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Text", 4));
    public final ValuePropertyMetadata textAlignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.textAlignmentName,
                javafx.scene.text.TextAlignment.class,
                true, /* readWrite */
                javafx.scene.text.TextAlignment.LEFT, /* defaultValue */
                new InspectorPath("Properties", "Text", 9));
    public final ValuePropertyMetadata textFillPropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.textFillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath("Properties", "Text", 7));
    public final ValuePropertyMetadata textOriginPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.textOriginName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.BASELINE, /* defaultValue */
                new InspectorPath("Layout", "Extras", 6));
    public final ValuePropertyMetadata textOverrunPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.textOverrunName,
                javafx.scene.control.OverrunStyle.class,
                true, /* readWrite */
                javafx.scene.control.OverrunStyle.ELLIPSIS, /* defaultValue */
                new InspectorPath("Properties", "Text", 11));
    public final ValuePropertyMetadata tickLabelFillPropertyMetadata =
            new PaintPropertyMetadata(
                PropertyNames.tickLabelFillName,
                true, /* readWrite */
                javafx.scene.paint.Color.BLACK, /* defaultValue */
                new InspectorPath("Properties", "Specific", 97));
    public final ValuePropertyMetadata tickLabelFontPropertyMetadata =
            new FontPropertyMetadata(
                PropertyNames.tickLabelFontName,
                true, /* readWrite */
                javafx.scene.text.Font.font("System",8.0), /* defaultValue */
                new InspectorPath("Properties", "Specific", 93));
    public final ValuePropertyMetadata tickLabelFormatterPropertyMetadata =
            new StringConverterPropertyMetadata(
                PropertyNames.tickLabelFormatterName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 86));
    public final ValuePropertyMetadata tickLabelGapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tickLabelGapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                3.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 103));
    public final ValuePropertyMetadata tickLabelRotationPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tickLabelRotationName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.ANGLE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 112));
    public final ValuePropertyMetadata tickLabelsVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tickLabelsVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 85));
    public final ValuePropertyMetadata tickLengthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tickLengthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                8.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 116));
    public final ValuePropertyMetadata tickMarksPropertyMetadata =
            new TickMarkListPropertyMetadata(
                PropertyNames.tickMarksName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "Specific", 84));
    public final ValuePropertyMetadata tickMarkVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.tickMarkVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 114));
    public final ValuePropertyMetadata tickUnitPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tickUnitName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                5.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 120));
    public final ValuePropertyMetadata tileAlignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.tileAlignmentName,
                javafx.geometry.Pos.class,
                true, /* readWrite */
                javafx.geometry.Pos.CENTER, /* defaultValue */
                new InspectorPath("Properties", "Specific", 59));
    public final ValuePropertyMetadata tileHeightPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tileHeightName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Specific", 21));
    public final ValuePropertyMetadata tileWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.tileWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Specific", 20));
    
    public final ValuePropertyMetadata titlePropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.titleName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Specific", 37));
    public final ValuePropertyMetadata titleSidePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.titleSideName,
                javafx.geometry.Side.class,
                true, /* readWrite */
                javafx.geometry.Side.TOP, /* defaultValue */
                new InspectorPath("Properties", "Specific", 60));
    public final ValuePropertyMetadata toggleGroupPropertyMetadata =
            new ToggleGroupPropertyMetadata(
                PropertyNames.toggleGroupName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 38));
    public final ValuePropertyMetadata translateXPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.translateXName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 5));
    public final ValuePropertyMetadata translateYPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.translateYName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 6));
    public final ValuePropertyMetadata translateZPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.translateZName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Transforms", 7));
    public final ValuePropertyMetadata typePropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.typeName,
                javafx.scene.shape.ArcType.class,
                true, /* readWrite */
                javafx.scene.shape.ArcType.OPEN, /* defaultValue */
                new InspectorPath("Properties", "Specific", 61));
    public final ValuePropertyMetadata underlinePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.underlineName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Text", 14));
    public final ValuePropertyMetadata unitIncrementPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.unitIncrementName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 69));
    public final ValuePropertyMetadata upperBoundPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.upperBoundName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                100.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 98));
    public final ValuePropertyMetadata userAgentStylesheetPropertyMetadata =
            new StringPropertyMetadata(
                PropertyNames.userAgentStylesheetName,
                true, /* readWrite */
                "", /* defaultValue */
                new InspectorPath("Properties", "Specific", 129));
    public final ValuePropertyMetadata valignment_NULL_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.valignmentName,
                javafx.geometry.VPos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Specific", 5));
    public final ValuePropertyMetadata valignment_CENTER_PropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.valignmentName,
                javafx.geometry.VPos.class,
                true, /* readWrite */
                javafx.geometry.VPos.CENTER, /* defaultValue */
                new InspectorPath("Layout", "Specific", 5));
    public final ValuePropertyMetadata value_Object_PropertyMetadata =
            new ObjectPropertyMetadata(
                PropertyNames.valueName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Properties", "Specific", 70));
    public final ValuePropertyMetadata value_Color_PropertyMetadata =
            new ColorPropertyMetadata(
                PropertyNames.valueName,
                true, /* readWrite */
                javafx.scene.paint.Color.WHITE, /* defaultValue */
                new InspectorPath("Properties", "Specific", 70));
    public final ValuePropertyMetadata value_Double_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.valueName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 70));
    public final ValuePropertyMetadata value_Object_ro_PropertyMetadata =
            new ObjectPropertyMetadata(
                PropertyNames.valueName,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 70));
    public final ValuePropertyMetadata vbarPolicyPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.vbarPolicyName,
                javafx.scene.control.ScrollPane.ScrollBarPolicy.class,
                true, /* readWrite */
                javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, /* defaultValue */
                new InspectorPath("Properties", "Specific", 71));
    public final ValuePropertyMetadata verticalFieldOfViewPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalFieldOfViewName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "3D", 5));
    public final ValuePropertyMetadata verticalGridLinesVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalGridLinesVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 115));
    public final ValuePropertyMetadata verticalZeroLineVisiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.verticalZeroLineVisibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Specific", 117));
    public final ValuePropertyMetadata vgapPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.vgapName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Internal", 1));
    public final ValuePropertyMetadata vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.vgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Specific", 3));
    public final ValuePropertyMetadata viewportPropertyMetadata =
            new Rectangle2DPropertyMetadata(
                PropertyNames.viewportName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Specific", 0));
    public final ValuePropertyMetadata viewportBoundsPropertyMetadata =
            new BoundsPropertyMetadata(
                PropertyNames.viewportBoundsName,
                true, /* readWrite */
                new javafx.geometry.BoundingBox(0.0, 0.0, 0.0, 0.0), /* defaultValue */
                new InspectorPath("Layout", "Bounds", 1));
    public final ValuePropertyMetadata visiblePropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.visibleName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Properties", "Node", 5));
    public final ValuePropertyMetadata visibleAmountPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.visibleAmountName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                15.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 87));
    public final ValuePropertyMetadata visibleRowCountPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.visibleRowCountName,
                true, /* readWrite */
                10, /* defaultValue */
                new InspectorPath("Properties", "Specific", 41));
    public final ValuePropertyMetadata visitedPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.visitedName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Specific", 42));
    public final ValuePropertyMetadata vmaxPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.vmaxName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 109));
    public final ValuePropertyMetadata vminPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.vminName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 105));
    public final ValuePropertyMetadata vvaluePropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.vvalueName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 107));
    public final ValuePropertyMetadata width_Double_200_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.widthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                2.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 7));
    public final ValuePropertyMetadata width_Double_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.widthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 7));
    public final ValuePropertyMetadata width_Double_COMPUTED_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.widthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.USE_COMPUTED_SIZE,
                true, /* readWrite */
                -1.0, /* defaultValue */
                new InspectorPath("Layout", "Size", 7));
    public final ValuePropertyMetadata width_Double_ro_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.widthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Layout", "Size", 7));
    public final ValuePropertyMetadata wrappingWidthPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.wrappingWidthName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Text", 10));
    public final ValuePropertyMetadata wrapTextPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.wrapTextName,
                true, /* readWrite */
                false, /* defaultValue */
                new InspectorPath("Properties", "Text", 8));
    public final ValuePropertyMetadata x_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.xName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 12));
    public final ValuePropertyMetadata x_NaN_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.xName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath("Layout", "Position", 12));
    public final ValuePropertyMetadata XAxisRotationPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.XAxisRotationName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.ANGLE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 125));
    public final ValuePropertyMetadata y_0_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.yName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                0.0, /* defaultValue */
                new InspectorPath("Layout", "Position", 13));
    public final ValuePropertyMetadata y_NaN_PropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.yName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                true, /* readWrite */
                Double.NaN, /* defaultValue */
                new InspectorPath("Layout", "Position", 13));
    public final ValuePropertyMetadata zeroPositionPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.zeroPositionName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.COORDINATE,
                false, /* readWrite */
                null, /* No defaultValue for R/O property */
                new InspectorPath("Properties", "Specific", 118));
    public final ValuePropertyMetadata zoomPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.zoomName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.SIZE,
                true, /* readWrite */
                1.0, /* defaultValue */
                new InspectorPath("Properties", "Specific", 123));
    public final ValuePropertyMetadata SplitPane_resizableWithParentPropertyMetadata =
            new BooleanPropertyMetadata(
                PropertyNames.SplitPane_resizableWithParentName,
                true, /* readWrite */
                true, /* defaultValue */
                new InspectorPath("Layout", "Split Pane Constraints", 0));
    public final ValuePropertyMetadata AnchorPane_bottomAnchorPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.AnchorPane_bottomAnchorName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.NULLABLE_COORDINATE,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Anchor Pane Constraints", 0));
    public final ValuePropertyMetadata AnchorPane_leftAnchorPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.AnchorPane_leftAnchorName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.NULLABLE_COORDINATE,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Anchor Pane Constraints", 1));
    public final ValuePropertyMetadata AnchorPane_rightAnchorPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.AnchorPane_rightAnchorName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.NULLABLE_COORDINATE,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Anchor Pane Constraints", 2));
    public final ValuePropertyMetadata AnchorPane_topAnchorPropertyMetadata =
            new DoublePropertyMetadata(
                PropertyNames.AnchorPane_topAnchorName,
                com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.DoubleKind.NULLABLE_COORDINATE,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Anchor Pane Constraints", 3));
    public final ValuePropertyMetadata BorderPane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.BorderPane_alignmentName,
                javafx.geometry.Pos.class,
                "AUTOMATIC", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Border Pane Constraints", 0));
    public final ValuePropertyMetadata BorderPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.BorderPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Border Pane Constraints", 1));
    public final ValuePropertyMetadata FlowPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.FlowPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Flow Pane Constraints", 0));
    public final ValuePropertyMetadata GridPane_columnIndexPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.GridPane_columnIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath("Layout", "Grid Pane Constraints", 1));
    public final ValuePropertyMetadata GridPane_columnSpanPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.GridPane_columnSpanName,
                true, /* readWrite */
                1, /* defaultValue */
                new InspectorPath("Layout", "Grid Pane Constraints", 3));
    public final ValuePropertyMetadata GridPane_halignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_halignmentName,
                javafx.geometry.HPos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Grid Pane Constraints", 7));
    public final ValuePropertyMetadata GridPane_hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_hgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Grid Pane Constraints", 4));
    public final ValuePropertyMetadata GridPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.GridPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Grid Pane Constraints", 8));
    public final ValuePropertyMetadata GridPane_rowIndexPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.GridPane_rowIndexName,
                true, /* readWrite */
                0, /* defaultValue */
                new InspectorPath("Layout", "Grid Pane Constraints", 0));
    public final ValuePropertyMetadata GridPane_rowSpanPropertyMetadata =
            new IntegerPropertyMetadata(
                PropertyNames.GridPane_rowSpanName,
                true, /* readWrite */
                1, /* defaultValue */
                new InspectorPath("Layout", "Grid Pane Constraints", 2));
    public final ValuePropertyMetadata GridPane_valignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_valignmentName,
                javafx.geometry.VPos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Grid Pane Constraints", 6));
    public final ValuePropertyMetadata GridPane_vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.GridPane_vgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Grid Pane Constraints", 5));
    public final ValuePropertyMetadata HBox_hgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.HBox_hgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "HBox Constraints", 0));
    public final ValuePropertyMetadata HBox_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.HBox_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "HBox Constraints", 1));
    public final ValuePropertyMetadata StackPane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.StackPane_alignmentName,
                javafx.geometry.Pos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Stack Pane Constraints", 0));
    public final ValuePropertyMetadata StackPane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.StackPane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Stack Pane Constraints", 1));
    public final ValuePropertyMetadata TilePane_alignmentPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.TilePane_alignmentName,
                javafx.geometry.Pos.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "Tile Pane Constraints", 0));
    public final ValuePropertyMetadata TilePane_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.TilePane_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "Tile Pane Constraints", 1));
    public final ValuePropertyMetadata VBox_marginPropertyMetadata =
            new InsetsPropertyMetadata(
                PropertyNames.VBox_marginName,
                true, /* readWrite */
                null, /* defaultValue */
                new InspectorPath("Layout", "VBox Constraints", 1));
    public final ValuePropertyMetadata VBox_vgrowPropertyMetadata =
            new EnumerationPropertyMetadata(
                PropertyNames.VBox_vgrowName,
                javafx.scene.layout.Priority.class,
                "INHERIT", /* null equivalent */
                true, /* readWrite */
                new InspectorPath("Layout", "VBox Constraints", 0));

    public final ValuePropertyMetadata includeFxmlPropertyMetadata =
            new StringListPropertyMetadata(
                PropertyNames.    source,
                    true, /* readWrite */
                    Collections.emptyList(), /* defaultValue */
                    new InspectorPath("Properties", "Include FXML file", 2));


}
