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


    <#list propertyMetas as property>
<#if property.type == "VALUE">
    public final ValuePropertyMetadata ${property.custom["metadataMemberName"]}PropertyMetadata =
            new ${property.raw.metadataClass.simpleName}(
                PropertyNames.${property.custom["memberName"]}Name,
	<#if property.raw.type.enum == true>
                ${property.raw.type.name?replace("$", ".")}.class,
	</#if>
	<#if property.raw.kind??>
                ${property.raw.kind},
	</#if>
	<#if property.custom["nullEquivalent"]??>
                "property.custom["nullEquivalent"]", /* null equivalent */
	</#if>
                ${property.raw.readWrite}, /* readWrite */
	<#if property.custom["defaultValue"]??>
                ${property.custom["defaultValue"]}, <#if property.raw.readWrite == true>/* defaultValue */</#if><#if property.raw.readWrite == false>/* No defaultValue for R/O property */</#if>
	</#if>

</#if>
    </#list>
    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

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
