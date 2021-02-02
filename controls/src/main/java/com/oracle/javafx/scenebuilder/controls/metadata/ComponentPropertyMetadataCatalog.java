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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.AxisMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ButtonBaseMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ColumnConstraintsMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ContextMenuMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.MenuItemMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.MenuMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.NodeMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.PathElementMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.RowConstraintsMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.SceneMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ShapeMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TabMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TableColumnMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TextFormatterMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TitledPaneMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.ToggleButtonMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TooltipMetadata;
import com.oracle.javafx.scenebuilder.controls.metadata.ComponentClassMetadatas.TreeTableColumnMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.PropertyNames;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;

@Component
public class ComponentPropertyMetadataCatalog {

    // Property Metadata
    
    public final ComponentPropertyMetadata bottomPropertyMetadata;
    public final ComponentPropertyMetadata buttonsPropertyMetadata;
    public final ComponentPropertyMetadata centerPropertyMetadata;
    public final ComponentPropertyMetadata children_c1_PropertyMetadata;
    public final ComponentPropertyMetadata children_empty_PropertyMetadata;
    public final ComponentPropertyMetadata clipPropertyMetadata;
    public final ComponentPropertyMetadata columnConstraintsPropertyMetadata;
    public final ComponentPropertyMetadata columns_TableColumn_PropertyMetadata;
    public final ComponentPropertyMetadata columns_TreeTableColumn_PropertyMetadata;
    public final ComponentPropertyMetadata content_Node_NULL_PropertyMetadata;
    public final ComponentPropertyMetadata content_Node_SEPARATOR_PropertyMetadata;
    public final ComponentPropertyMetadata contextMenuPropertyMetadata;
    public final ComponentPropertyMetadata elementsPropertyMetadata;
    public final ComponentPropertyMetadata expandableContentPropertyMetadata;
    public final ComponentPropertyMetadata graphicPropertyMetadata;
    public final ComponentPropertyMetadata headerPropertyMetadata;
    public final ComponentPropertyMetadata items_MenuItem_PropertyMetadata;
    public final ComponentPropertyMetadata items_Node_PropertyMetadata;
    public final ComponentPropertyMetadata labelForPropertyMetadata;
    public final ComponentPropertyMetadata leftPropertyMetadata;
    public final ComponentPropertyMetadata menusPropertyMetadata;
    public final ComponentPropertyMetadata panesPropertyMetadata;
    public final ComponentPropertyMetadata placeholderPropertyMetadata;
    public final ComponentPropertyMetadata rightPropertyMetadata;
    public final ComponentPropertyMetadata root_scene_PropertyMetadata;
    public final ComponentPropertyMetadata rowConstraintsPropertyMetadata;
    public final ComponentPropertyMetadata scene_stage_PropertyMetadata;
    public final ComponentPropertyMetadata scopePropertyMetadata;
    public final ComponentPropertyMetadata shapePropertyMetadata;
    public final ComponentPropertyMetadata sortNodePropertyMetadata;
    public final ComponentPropertyMetadata sortOrderPropertyMetadata;
    public final ComponentPropertyMetadata tabsPropertyMetadata;
    public final ComponentPropertyMetadata textFormatterPropertyMetadata;
    public final ComponentPropertyMetadata tooltipPropertyMetadata;
    public final ComponentPropertyMetadata topPropertyMetadata;
    public final ComponentPropertyMetadata treeColumnPropertyMetadata;
    public final ComponentPropertyMetadata xAxisPropertyMetadata;
    public final ComponentPropertyMetadata yAxisPropertyMetadata;
    
    public ComponentPropertyMetadataCatalog(
            @Lazy @Autowired NodeMetadata nodeMetadata,
            @Lazy @Autowired ButtonBaseMetadata buttonBaseMetadata,
            @Lazy @Autowired ColumnConstraintsMetadata columnConstraintsMetadata,
            @Lazy @Autowired TableColumnMetadata tableColumnMetadata,
            @Lazy @Autowired TreeTableColumnMetadata treeTableColumnMetadata,
            @Lazy @Autowired ContextMenuMetadata contextMenuMetadata,
            @Lazy @Autowired PathElementMetadata pathElementMetadata,
            @Lazy @Autowired MenuItemMetadata menuItemMetadata,
            @Lazy @Autowired MenuMetadata menuMetadata,
            @Lazy @Autowired TitledPaneMetadata titledPaneMetadata,
            @Lazy @Autowired RowConstraintsMetadata rowConstraintsMetadata,
            @Lazy @Autowired SceneMetadata sceneMetadata,
            @Lazy @Autowired ShapeMetadata shapeMetadata,
            @Lazy @Autowired TabMetadata tabMetadata,
            @Lazy @Autowired TextFormatterMetadata textFormatterMetadata,
            @Lazy @Autowired ToggleButtonMetadata toggleButtonMetadata,
            @Lazy @Autowired TooltipMetadata tooltipMetadata,
            @Lazy @Autowired AxisMetadata axisMetadata
            ) {
        bottomPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.bottomName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/BorderPane-bottom.png"),
                    getClass().getResource("subcompicons/BorderPane-bottom@2x.png"))
                .setResizeNeededWhenTopElement(true);
        buttonsPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.buttonsName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        centerPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.centerName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/BorderPane-center.png"),
                    getClass().getResource("subcompicons/BorderPane-center@2x.png"))
                .setResizeNeededWhenTopElement(true);
        children_c1_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.childrenName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null)
                .setResizeNeededWhenTopElement(true);
        children_empty_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.childrenName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        clipPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.clipName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null);
        columnConstraintsPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.columnConstraintsName,
                    columnConstraintsMetadata,
                    true, /* collection */
                    null,
                    null);
        columns_TableColumn_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.columnsName,
                    tableColumnMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        columns_TreeTableColumn_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.columnsName,
                    treeTableColumnMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        content_Node_NULL_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.contentName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/DialogPane-content.png"),
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        content_Node_SEPARATOR_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.contentName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null)
                .setResizeNeededWhenTopElement(true);
        contextMenuPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.contextMenuName,
                    contextMenuMetadata,
                    false, /* unique */
                    null,
                    null);
        elementsPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.elementsName,
                    pathElementMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        expandableContentPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.expandableContentName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/DialogPane-expandable_content.png"),
                    null);
        graphicPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.graphicName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/DialogPane-graphic.png"),
                    null);
        headerPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.headerName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/DialogPane-header.png"),
                    null);
        items_MenuItem_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.itemsName,
                    menuItemMetadata,
                    true, /* collection */
                    null,
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        items_Node_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.itemsName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        labelForPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.labelForName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null);
        leftPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.leftName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/BorderPane-left.png"),
                    getClass().getResource("subcompicons/BorderPane-left@2x.png"))
                .setResizeNeededWhenTopElement(true);
        menusPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.menusName,
                    menuMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        panesPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.panesName,
                    titledPaneMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        placeholderPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.placeholderName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null);
        rightPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.rightName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/BorderPane-right.png"),
                    getClass().getResource("subcompicons/BorderPane-right@2x.png"))
                .setResizeNeededWhenTopElement(true);
        root_scene_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.rootName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        rowConstraintsPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.rowConstraintsName,
                    rowConstraintsMetadata,
                    true, /* collection */
                    null,
                    null);
        scene_stage_PropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.sceneName,
                    sceneMetadata,
                    false, /* unique */
                    null,
                    null,
                    true)
                .setResizeNeededWhenTopElement(true);
        scopePropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.scopeName,
                    nodeMetadata,
                    true, /* collection */
                    null,
                    null);
        shapePropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.shapeName,
                    shapeMetadata,
                    false, /* unique */
                    null,
                    null);
        sortNodePropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.sortNodeName,
                    nodeMetadata,
                    false, /* unique */
                    null,
                    null);
        sortOrderPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.sortOrderName,
                    tableColumnMetadata,
                    true, /* collection */
                    null,
                    null);
        tabsPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.tabsName,
                    tabMetadata,
                    true, /* collection */
                    null,
                    null,
                    true);
        textFormatterPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.textFormatterName,
                    textFormatterMetadata,
                    false, /* unique */
                    null,
                    null);
        tooltipPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.tooltipName,
                    tooltipMetadata,
                    false, /* unique */
                    null,
                    null);
        topPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.topName,
                    nodeMetadata,
                    false, /* unique */
                    getClass().getResource("subcompicons/BorderPane-top.png"),
                    getClass().getResource("subcompicons/BorderPane-top@2x.png"))
                .setResizeNeededWhenTopElement(true);
        treeColumnPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.treeColumnName,
                    treeTableColumnMetadata,
                    false, /* unique */
                    null,
                    null);
        xAxisPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.    xAxisName,
                    axisMetadata,
                    false, /* unique */
                    null,
                    null);
        yAxisPropertyMetadata = new ComponentPropertyMetadata(
                    PropertyNames.    yAxisName,
                    axisMetadata,
                    false, /* unique */
                    null,
                    null);
    }
}
