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
package com.oracle.javafx.scenebuilder.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.CurveEditor;
import com.oracle.javafx.scenebuilder.api.control.DropTargetProvider;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.InlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.PickRefiner;
import com.oracle.javafx.scenebuilder.api.control.Pring;
import com.oracle.javafx.scenebuilder.api.control.Relocater;
import com.oracle.javafx.scenebuilder.api.control.ResizeGuide;
import com.oracle.javafx.scenebuilder.api.control.Resizer;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.control.Shadow;
import com.oracle.javafx.scenebuilder.api.control.Tring;
import com.oracle.javafx.scenebuilder.api.control.driver.DriverExtensionRegistry;
import com.oracle.javafx.scenebuilder.api.control.inlineedit.SimilarInlineEditorBounds;
import com.oracle.javafx.scenebuilder.api.control.intersect.IntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.api.control.outline.Outline;
import com.oracle.javafx.scenebuilder.api.control.pickrefiner.NoPickRefiner;
import com.oracle.javafx.scenebuilder.tools.driver.anchorpane.AnchorPaneRelocater;
import com.oracle.javafx.scenebuilder.tools.driver.arc.ArcResizer;
import com.oracle.javafx.scenebuilder.tools.driver.borderpane.BorderPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.borderpane.BorderPaneTring;
import com.oracle.javafx.scenebuilder.tools.driver.canvas.CanvasResizer;
import com.oracle.javafx.scenebuilder.tools.driver.circle.CircleResizer;
import com.oracle.javafx.scenebuilder.tools.driver.common.DefaultZDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.common.GenericParentTring;
import com.oracle.javafx.scenebuilder.tools.driver.common.MainAccessoryDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.cubiccurve.CubicCurveEditor;
import com.oracle.javafx.scenebuilder.tools.driver.cubiccurve.CubicCurveHandles;
import com.oracle.javafx.scenebuilder.tools.driver.ellipse.EllipseResizer;
import com.oracle.javafx.scenebuilder.tools.driver.flowpane.FlowPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneHandles;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPanePring;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneTring;
import com.oracle.javafx.scenebuilder.tools.driver.hbox.HBoxDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.hbox.HBoxTring;
import com.oracle.javafx.scenebuilder.tools.driver.imageview.ImageViewDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.imageview.ImageViewResizer;
import com.oracle.javafx.scenebuilder.tools.driver.line.LineEditor;
import com.oracle.javafx.scenebuilder.tools.driver.line.LineHandles;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeHandles;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeOutline;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodePring;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeResizeGuide;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeShadow;
import com.oracle.javafx.scenebuilder.tools.driver.node.NodeTring;
import com.oracle.javafx.scenebuilder.tools.driver.node.ResizeRudder;
import com.oracle.javafx.scenebuilder.tools.driver.pane.PaneRelocater;
import com.oracle.javafx.scenebuilder.tools.driver.polygon.PolygonEditor;
import com.oracle.javafx.scenebuilder.tools.driver.polygon.PolygonHandles;
import com.oracle.javafx.scenebuilder.tools.driver.polyline.PolylineEditor;
import com.oracle.javafx.scenebuilder.tools.driver.polyline.PolylineHandles;
import com.oracle.javafx.scenebuilder.tools.driver.quadcurve.QuadCurveEditor;
import com.oracle.javafx.scenebuilder.tools.driver.quadcurve.QuadCurveHandles;
import com.oracle.javafx.scenebuilder.tools.driver.rectangle.RectangleResizer;
import com.oracle.javafx.scenebuilder.tools.driver.region.RegionResizer;
import com.oracle.javafx.scenebuilder.tools.driver.scene.SceneHandles;
import com.oracle.javafx.scenebuilder.tools.driver.scene.SceneIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.scene.ScenePring;
import com.oracle.javafx.scenebuilder.tools.driver.splitpane.SplitPaneHandles;
import com.oracle.javafx.scenebuilder.tools.driver.subscene.SubSceneResizer;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabInlineEditorBounds;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabPring;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabTring;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.TableColumnHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.TableColumnInlineEditorBounds;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.TableColumnIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.TableColumnPring;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.TableColumnTring;
import com.oracle.javafx.scenebuilder.tools.driver.tableview.TableViewHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tableview.TableViewPickRefiner;
import com.oracle.javafx.scenebuilder.tools.driver.tabpane.TabPanePickRefiner;
import com.oracle.javafx.scenebuilder.tools.driver.text.TextResizer;
import com.oracle.javafx.scenebuilder.tools.driver.textflow.TextFlowDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.toolbar.ToolBarDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.TreeTableColumnHandles;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.TreeTableColumnInlineEditorBounds;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.TreeTableColumnIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.TreeTableColumnPring;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.TreeTableColumnTring;
import com.oracle.javafx.scenebuilder.tools.driver.treetableview.TreeTableViewHandles;
import com.oracle.javafx.scenebuilder.tools.driver.treetableview.TreeTableViewPickRefiner;
import com.oracle.javafx.scenebuilder.tools.driver.vbox.VBoxDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.vbox.VBoxTring;
import com.oracle.javafx.scenebuilder.tools.driver.webview.WebViewResizer;
import com.oracle.javafx.scenebuilder.tools.driver.window.WindowHandles;
import com.oracle.javafx.scenebuilder.tools.driver.window.WindowIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.window.WindowPring;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Labeled;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

@Component
public class DriverExtensionInitializer {

    public DriverExtensionInitializer(@Autowired DriverExtensionRegistry registry) {
        super();
        registry.registerExtension(CurveEditor.class);
        registry.registerExtension(DropTargetProvider.class);
        registry.registerExtension(Handles.class);
        registry.registerExtension(InlineEditorBounds.class);
        registry.registerExtension(IntersectsBoundsCheck.class);
        registry.registerExtension(Outline.class);
        registry.registerExtension(PickRefiner.class);
        registry.registerExtension(Pring.class);
        registry.registerExtension(Relocater.class);
        registry.registerExtension(Resizer.class);
        registry.registerExtension(Rudder.class);
        registry.registerExtension(Tring.class);
        registry.registerExtension(ResizeGuide.class);
        registry.registerExtension(Shadow.class);

        // AnchorPane
        registry.registerImplementationClass(Relocater.class, AnchorPane.class, AnchorPaneRelocater.class);

        // Arc
        registry.registerImplementationClass(Resizer.class, Arc.class, ArcResizer.class);

        // BorderPane
        registry.registerImplementationClass(DropTargetProvider.class, BorderPane.class,
                BorderPaneDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, BorderPane.class, BorderPaneTring.class);

        // Canvas
        registry.registerImplementationClass(Resizer.class, Canvas.class, CanvasResizer.class);

        // Circle
        registry.registerImplementationClass(Resizer.class, Circle.class, CircleResizer.class);

        // ComboBox
        registry.registerImplementationClass(InlineEditorBounds.class, ComboBox.class, SimilarInlineEditorBounds.class);

        // ContextMenu
        // TODO add feature instead of nothing show the parent outline
        registry.registerImplementationClass(Pring.class, ContextMenu.class, null);

        // CubicCurve
        registry.registerImplementationClass(CurveEditor.class, CubicCurve.class, CubicCurveEditor.class);
        registry.registerImplementationClass(Handles.class, CubicCurve.class, CubicCurveHandles.class);

        // Ellipse
        registry.registerImplementationClass(Resizer.class, Ellipse.class, EllipseResizer.class);

        // FlowPane
        registry.registerImplementationClass(DropTargetProvider.class, FlowPane.class, FlowPaneDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, FlowPane.class, GenericParentTring.class);

        // GridPane
        registry.registerImplementationClass(DropTargetProvider.class, GridPane.class, GridPaneDropTargetProvider.class);
        registry.registerImplementationClass(Handles.class, GridPane.class, GridPaneHandles.class);
        registry.registerImplementationClass(Pring.class, GridPane.class, GridPanePring.class);
        registry.registerImplementationClass(Tring.class, GridPane.class, GridPaneTring.class);

        // HBox
        registry.registerImplementationClass(DropTargetProvider.class, HBox.class, HBoxDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, HBox.class, HBoxTring.class);

        // ImageView
        registry.registerImplementationClass(Resizer.class, ImageView.class, ImageViewResizer.class);
        registry.registerImplementationClass(DropTargetProvider.class, ImageView.class, ImageViewDropTargetProvider.class);

        // Line
        registry.registerImplementationClass(CurveEditor.class, Line.class, LineEditor.class);
        registry.registerImplementationClass(Handles.class, Line.class, LineHandles.class);

        // Labeled
        registry.registerImplementationClass(InlineEditorBounds.class, Labeled.class, SimilarInlineEditorBounds.class);

        // Node
        registry.registerImplementationClass(CurveEditor.class, Node.class, null);
        registry.registerImplementationClass(DropTargetProvider.class, Node.class, NodeDropTargetProvider.class);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, Node.class, NodeIntersectsBoundsCheck.class);
        registry.registerImplementationClass(Handles.class, Node.class, NodeHandles.class);
        registry.registerImplementationClass(Outline.class, Node.class, NodeOutline.class);
        registry.registerImplementationClass(PickRefiner.class, Node.class, NoPickRefiner.class);
        registry.registerImplementationClass(Pring.class, Node.class, NodePring.class);
        registry.registerImplementationClass(Rudder.class, Node.class, ResizeRudder.class);
        registry.registerImplementationClass(Tring.class, Node.class, NodeTring.class);
        registry.registerImplementationClass(ResizeGuide.class, Node.class, NodeResizeGuide.class);
        registry.registerImplementationClass(Shadow.class, Node.class, NodeShadow.class);

        // Pane
        registry.registerImplementationClass(Relocater.class, Pane.class, PaneRelocater.class);

        // Parent


        // Polygon
        registry.registerImplementationClass(CurveEditor.class, Polygon.class, PolygonEditor.class);
        registry.registerImplementationClass(Handles.class, Polygon.class, PolygonHandles.class);

        // Polyline
        registry.registerImplementationClass(CurveEditor.class, Polyline.class, PolylineEditor.class);
        registry.registerImplementationClass(Handles.class, Polyline.class, PolylineHandles.class);

        //PopupWindow
        registry.registerImplementationClass(Handles.class, PopupWindow.class, null);


        // QuadCurve
        registry.registerImplementationClass(CurveEditor.class, QuadCurve.class, QuadCurveEditor.class);
        registry.registerImplementationClass(Handles.class, QuadCurve.class, QuadCurveHandles.class);

        // Rectangle
        registry.registerImplementationClass(Resizer.class, Rectangle.class, RectangleResizer.class);

        // Region
        registry.registerImplementationClass(Resizer.class, Region.class, RegionResizer.class);

        // Scene
        registry.registerImplementationClass(CurveEditor.class, Scene.class, null);
        registry.registerImplementationClass(DropTargetProvider.class, Scene.class, MainAccessoryDropTargetProvider.class);
        registry.registerImplementationClass(InlineEditorBounds.class, Scene.class, null);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, Scene.class, SceneIntersectsBoundsCheck.class);
        registry.registerImplementationClass(Handles.class, Scene.class, SceneHandles.class);
        registry.registerImplementationClass(PickRefiner.class, Scene.class, NoPickRefiner.class);
        registry.registerImplementationClass(Pring.class, Scene.class, ScenePring.class);
        registry.registerImplementationClass(Resizer.class, Scene.class, null);
        registry.registerImplementationClass(Tring.class, Scene.class, null);

        // SplitPane
        registry.registerImplementationClass(Handles.class, SplitPane.class, SplitPaneHandles.class);

        // SubScene
        registry.registerImplementationClass(Resizer.class, SubScene.class, SubSceneResizer.class);

        // Tab
        registry.registerImplementationClass(Handles.class, Tab.class, TabHandles.class);
        // TODO TabOutline does not implement Outline, to check why
        // registry.registerImplementationClass(Outline.class, Tab.class,
        // TabOutline.class);
        registry.registerImplementationClass(Pring.class, Tab.class, TabPring.class);
        registry.registerImplementationClass(Tring.class, Tab.class, TabTring.class);
        registry.registerImplementationClass(Resizer.class, Tab.class, null);
        registry.registerImplementationClass(CurveEditor.class, Tab.class, null);
        registry.registerImplementationClass(PickRefiner.class, Tab.class, NoPickRefiner.class);
        registry.registerImplementationClass(DropTargetProvider.class, Tab.class, MainAccessoryDropTargetProvider.class);
        registry.registerImplementationClass(InlineEditorBounds.class, Tab.class, TabInlineEditorBounds.class);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, Tab.class, TabIntersectsBoundsCheck.class);



        // TableColumn
        registry.registerImplementationClass(CurveEditor.class, TableColumn.class, null);
        registry.registerImplementationClass(Handles.class, TableColumn.class, TableColumnHandles.class);
        // TODO(elp) : implement TableColumnDriver.refinePick()
        registry.registerImplementationClass(PickRefiner.class, TableColumn.class, NoPickRefiner.class);
        registry.registerImplementationClass(Pring.class, TableColumn.class, TableColumnPring.class);
        // TODO TableColumnResizer is not registered and does not implement Resizer, why?
        registry.registerImplementationClass(Resizer.class, TableColumn.class, null);
        registry.registerImplementationClass(Tring.class, TableColumn.class, TableColumnTring.class);
        registry.registerImplementationClass(DropTargetProvider.class, TableColumn.class, DefaultZDropTargetProvider.class);
        registry.registerImplementationClass(InlineEditorBounds.class, TableColumn.class, TableColumnInlineEditorBounds.class);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, TableColumn.class, TableColumnIntersectsBoundsCheck.class);

        // TableView
        registry.registerImplementationClass(Handles.class, TableView.class, TableViewHandles.class);
        registry.registerImplementationClass(Resizer.class, TableView.class, RegionResizer.class);
        registry.registerImplementationClass(PickRefiner.class, TableView.class, TableViewPickRefiner.class);

        // TabPane
        registry.registerImplementationClass(Resizer.class, TabPane.class, RegionResizer.class);
        registry.registerImplementationClass(PickRefiner.class, TabPane.class, TabPanePickRefiner.class);

        // Text
        registry.registerImplementationClass(InlineEditorBounds.class, Text.class, SimilarInlineEditorBounds.class);
        registry.registerImplementationClass(Resizer.class, Text.class, TextResizer.class);

        // TextFlow
        registry.registerImplementationClass(DropTargetProvider.class, TextFlow.class, TextFlowDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, TextFlow.class, GenericParentTring.class);

        // TextInputControl
        registry.registerImplementationClass(InlineEditorBounds.class, TextInputControl.class,
                SimilarInlineEditorBounds.class);

        // TitledPane
        registry.registerImplementationClass(InlineEditorBounds.class, TitledPane.class,
                SimilarInlineEditorBounds.class);

        // ToolBar
        registry.registerImplementationClass(DropTargetProvider.class, ToolBar.class, ToolBarDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, ToolBar.class, GenericParentTring.class);

        // Tooltip
        // TODO add feature instead of nothing show the parent outline
        registry.registerImplementationClass(Pring.class, Tooltip.class, null);

        // TreeTableColumn
        registry.registerImplementationClass(CurveEditor.class, TreeTableColumn.class, null);
        registry.registerImplementationClass(Handles.class, TreeTableColumn.class, TreeTableColumnHandles.class);
        // TODO(elp) : implement TableColumnDriver.refinePick()
        registry.registerImplementationClass(PickRefiner.class, TreeTableColumn.class, NoPickRefiner.class);
        registry.registerImplementationClass(Pring.class, TreeTableColumn.class, TreeTableColumnPring.class);
        // TODO TreeTableColumnResizer is not registered and does not implement Resizer, why?
        registry.registerImplementationClass(Resizer.class, TreeTableColumn.class, null);
        registry.registerImplementationClass(Tring.class, TreeTableColumn.class, TreeTableColumnTring.class);
        registry.registerImplementationClass(DropTargetProvider.class, TreeTableColumn.class, DefaultZDropTargetProvider.class);
        registry.registerImplementationClass(InlineEditorBounds.class, TreeTableColumn.class, TreeTableColumnInlineEditorBounds.class);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, TreeTableColumn.class, TreeTableColumnIntersectsBoundsCheck.class);

        // TreeTableView
        registry.registerImplementationClass(Handles.class, TreeTableView.class, TreeTableViewHandles.class);
        registry.registerImplementationClass(Resizer.class, TreeTableView.class, RegionResizer.class);
        registry.registerImplementationClass(PickRefiner.class, TreeTableView.class, TreeTableViewPickRefiner.class);

        // VBox
        registry.registerImplementationClass(DropTargetProvider.class, VBox.class, VBoxDropTargetProvider.class);
        registry.registerImplementationClass(Tring.class, VBox.class, VBoxTring.class);

        // WebView
        registry.registerImplementationClass(Resizer.class, WebView.class, WebViewResizer.class);

        // Window
        registry.registerImplementationClass(Handles.class, Window.class, WindowHandles.class);
        registry.registerImplementationClass(Tring.class, Window.class, null);
        registry.registerImplementationClass(Pring.class, Window.class, WindowPring.class);
        registry.registerImplementationClass(Resizer.class, Window.class, null);
        registry.registerImplementationClass(CurveEditor.class, Window.class, null);
        registry.registerImplementationClass(PickRefiner.class, Window.class, NoPickRefiner.class);
        registry.registerImplementationClass(DropTargetProvider.class, Window.class, MainAccessoryDropTargetProvider.class);
        registry.registerImplementationClass(InlineEditorBounds.class, Window.class, null);
        registry.registerImplementationClass(IntersectsBoundsCheck.class, Window.class, WindowIntersectsBoundsCheck.class);

    }

}
