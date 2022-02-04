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
package com.oracle.javafx.scenebuilder.drivers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.drivers.anchorpane.AnchorPaneRelocater;
import com.oracle.javafx.scenebuilder.drivers.arc.ArcResizer;
import com.oracle.javafx.scenebuilder.drivers.borderpane.BorderPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.borderpane.BorderPaneTring;
import com.oracle.javafx.scenebuilder.drivers.canvas.CanvasResizer;
import com.oracle.javafx.scenebuilder.drivers.circle.CircleResizer;
import com.oracle.javafx.scenebuilder.drivers.common.DefaultZDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.common.GenericParentTring;
import com.oracle.javafx.scenebuilder.drivers.common.MainAccessoryDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.cubiccurve.CubicCurveEditor;
import com.oracle.javafx.scenebuilder.drivers.cubiccurve.CubicCurveHandles;
import com.oracle.javafx.scenebuilder.drivers.ellipse.EllipseResizer;
import com.oracle.javafx.scenebuilder.drivers.flowpane.FlowPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridPaneDropTarget;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridPaneHandles;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridPanePring;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridPaneTring;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.drivers.gridpane.ResizeColumnGesture;
import com.oracle.javafx.scenebuilder.drivers.gridpane.ResizeRowGesture;
import com.oracle.javafx.scenebuilder.drivers.gridpane.gesture.SelectAndMoveInGridGesture;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.AddColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.AddColumnJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.AddRowConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.AddRowJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.DeleteColumnJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.DeleteGridSelectionJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.DeleteRowJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.InsertColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.InsertColumnJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.InsertRowConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.InsertRowJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.MoveCellContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.MoveColumnContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.MoveColumnJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.MoveRowContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.MoveRowJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.ReIndexColumnContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.ReIndexRowContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.RemoveColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.RemoveColumnContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.RemoveRowConstraintsJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.RemoveRowContentJob;
import com.oracle.javafx.scenebuilder.drivers.gridpane.job.SpanJob;
import com.oracle.javafx.scenebuilder.drivers.hbox.HBoxDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.hbox.HBoxTring;
import com.oracle.javafx.scenebuilder.drivers.imageview.ImageViewDropTarget;
import com.oracle.javafx.scenebuilder.drivers.imageview.ImageViewDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.imageview.ImageViewResizer;
import com.oracle.javafx.scenebuilder.drivers.line.LineEditor;
import com.oracle.javafx.scenebuilder.drivers.line.LineHandles;
import com.oracle.javafx.scenebuilder.drivers.node.NodeDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.node.NodeHandles;
import com.oracle.javafx.scenebuilder.drivers.node.NodeIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.node.NodeOutline;
import com.oracle.javafx.scenebuilder.drivers.node.NodePring;
import com.oracle.javafx.scenebuilder.drivers.node.NodeResizeGuide;
import com.oracle.javafx.scenebuilder.drivers.node.NodeShadow;
import com.oracle.javafx.scenebuilder.drivers.node.NodeTring;
import com.oracle.javafx.scenebuilder.drivers.node.ResizeRudder;
import com.oracle.javafx.scenebuilder.drivers.pane.PaneRelocater;
import com.oracle.javafx.scenebuilder.drivers.polygon.PolygonEditor;
import com.oracle.javafx.scenebuilder.drivers.polygon.PolygonHandles;
import com.oracle.javafx.scenebuilder.drivers.polyline.PolylineEditor;
import com.oracle.javafx.scenebuilder.drivers.polyline.PolylineHandles;
import com.oracle.javafx.scenebuilder.drivers.quadcurve.QuadCurveEditor;
import com.oracle.javafx.scenebuilder.drivers.quadcurve.QuadCurveHandles;
import com.oracle.javafx.scenebuilder.drivers.rectangle.RectangleResizer;
import com.oracle.javafx.scenebuilder.drivers.region.RegionResizer;
import com.oracle.javafx.scenebuilder.drivers.scene.SceneHandles;
import com.oracle.javafx.scenebuilder.drivers.scene.SceneIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.scene.ScenePring;
import com.oracle.javafx.scenebuilder.drivers.splitpane.AdjustDividerGesture;
import com.oracle.javafx.scenebuilder.drivers.splitpane.SplitPaneHandles;
import com.oracle.javafx.scenebuilder.drivers.subscene.SubSceneResizer;
import com.oracle.javafx.scenebuilder.drivers.tab.TabHandles;
import com.oracle.javafx.scenebuilder.drivers.tab.TabInlineEditorBounds;
import com.oracle.javafx.scenebuilder.drivers.tab.TabIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.tab.TabPring;
import com.oracle.javafx.scenebuilder.drivers.tab.TabTring;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.ResizeTableColumnGesture;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.TableColumnHandles;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.TableColumnInlineEditorBounds;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.TableColumnIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.TableColumnPring;
import com.oracle.javafx.scenebuilder.drivers.tablecolumn.TableColumnTring;
import com.oracle.javafx.scenebuilder.drivers.tableview.TableViewHandles;
import com.oracle.javafx.scenebuilder.drivers.tableview.TableViewPickRefiner;
import com.oracle.javafx.scenebuilder.drivers.tabpane.TabPanePickRefiner;
import com.oracle.javafx.scenebuilder.drivers.text.TextResizer;
import com.oracle.javafx.scenebuilder.drivers.textflow.TextFlowDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.toolbar.ToolBarDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.ResizeTreeTableColumnGesture;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.TreeTableColumnHandles;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.TreeTableColumnInlineEditorBounds;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.TreeTableColumnIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.TreeTableColumnPring;
import com.oracle.javafx.scenebuilder.drivers.treetablecolumn.TreeTableColumnTring;
import com.oracle.javafx.scenebuilder.drivers.treetableview.TreeTableViewHandles;
import com.oracle.javafx.scenebuilder.drivers.treetableview.TreeTableViewPickRefiner;
import com.oracle.javafx.scenebuilder.drivers.vbox.VBoxDropTargetProvider;
import com.oracle.javafx.scenebuilder.drivers.vbox.VBoxTring;
import com.oracle.javafx.scenebuilder.drivers.webview.WebViewResizer;
import com.oracle.javafx.scenebuilder.drivers.window.WindowHandles;
import com.oracle.javafx.scenebuilder.drivers.window.WindowIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.drivers.window.WindowPring;
import com.oracle.javafx.scenebuilder.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.SelectWithPringGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;

public class BaseToolingExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("d4784eb4-144c-41d8-9107-9112b883bfc3");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AddColumnConstraintsJob.class,
                AddColumnConstraintsJob.Factory.class,
                AddColumnJob.class,
                AddColumnJob.Factory.class,
                AddRowConstraintsJob.class,
                AddRowConstraintsJob.Factory.class,
                AddRowJob.class,
                AddRowJob.Factory.class,
                AdjustDividerGesture.class,
                AdjustDividerGesture.Factory.class,
                AnchorPaneRelocater.class,
                ArcResizer.class,
                BorderPaneDropTargetProvider.class,
                BorderPaneTring.class,
                CanvasResizer.class,
                CircleResizer.class,
                CubicCurveEditor.class,
                CubicCurveHandles.class,
                DefaultZDropTargetProvider.class,
                DeleteColumnJob.class,
                DeleteColumnJob.Factory.class,
                DeleteGridSelectionJob.class,
                DeleteGridSelectionJob.Factory.class,
                DeleteRowJob.class,
                DeleteRowJob.Factory.class,
                DriverExtensionInitializer.class,
                EditCurveGesture.class,
                EditCurveGesture.Factory.class,
                EllipseResizer.class,
                FlowPaneDropTargetProvider.class,
                GenericParentTring.class,
                GridPaneDropTarget.class,
                GridPaneDropTarget.Factory.class,
                GridPaneDropTargetProvider.class,
                GridPaneHandles.class,
                GridPanePring.class,
                GridPaneTring.class,
                GridSelectionGroup.class,
                GridSelectionGroup.Factory.class,
                HBoxDropTargetProvider.class,
                HBoxTring.class,
                ImageViewDropTarget.class,
                ImageViewDropTarget.Factory.class,
                ImageViewDropTargetProvider.class,
                ImageViewResizer.class,
                InsertColumnConstraintsJob.class,
                InsertColumnConstraintsJob.Factory.class,
                InsertColumnJob.Factory.class,
                InsertRowConstraintsJob.class,
                InsertRowConstraintsJob.Factory.class,
                InsertRowJob.class,
                InsertRowJob.Factory.class,
                LineEditor.class,
                LineHandles.class,
                MainAccessoryDropTargetProvider.class,
                MoveCellContentJob.class,
                MoveCellContentJob.Factory.class,
                MoveColumnContentJob.class,
                MoveColumnContentJob.Factory.class,
                MoveColumnJob.class,
                MoveColumnJob.Factory.class,
                MoveRowContentJob.class,
                MoveRowContentJob.Factory.class,
                MoveRowJob.class,
                MoveRowJob.Factory.class,
                NodeDropTargetProvider.class,
                NodeHandles.class,
                NodeIntersectsBoundsCheck.class,
                NodeOutline.class,
                NodePring.class,
                NodeResizeGuide.class,
                NodeShadow.class,
                NodeTring.class,
                PaneRelocater.class,
                PolygonEditor.class,
                PolygonHandles.class,
                PolylineEditor.class,
                PolylineHandles.class,
                QuadCurveEditor.class,
                QuadCurveHandles.class,
                ReIndexColumnContentJob.class,
                ReIndexColumnContentJob.Factory.class,
                ReIndexRowContentJob.class,
                ReIndexRowContentJob.Factory.class,
                RectangleResizer.class,
                RegionResizer.class,
                RemoveColumnConstraintsJob.class,
                RemoveColumnConstraintsJob.Factory.class,
                RemoveColumnContentJob.class,
                RemoveColumnContentJob.Factory.class,
                RemoveRowConstraintsJob.class,
                RemoveRowConstraintsJob.Factory.class,
                RemoveRowContentJob.class,
                RemoveRowContentJob.Factory.class,
                ResizeColumnGesture.class,
                ResizeColumnGesture.Factory.class,
                ResizeGesture.class,
                ResizeGesture.Factory.class,
                ResizeRowGesture.class,
                ResizeRowGesture.Factory.class,
                ResizeRudder.class,
                ResizeTableColumnGesture.class,
                ResizeTableColumnGesture.Factory.class,
                ResizeTreeTableColumnGesture.class,
                ResizeTreeTableColumnGesture.Factory.class,
                SceneHandles.class,
                SceneIntersectsBoundsCheck.class,
                ScenePring.class,
                SelectAndMoveInGridGesture.class,
                SelectAndMoveInGridGesture.Factory.class,
                SelectWithPringGesture.class,
                SelectWithPringGesture.Factory.class,
                SpanJob.class,
                SpanJob.Factory.class,
                SplitPaneHandles.class,
                SubSceneResizer.class,
                TabHandles.class,
                TabInlineEditorBounds.class,
                TabIntersectsBoundsCheck.class,
                TabPanePickRefiner.class,
                TabPring.class,
                TabTring.class,
                TableColumnHandles.class,
                TableColumnInlineEditorBounds.class,
                TableColumnIntersectsBoundsCheck.class,
                TableColumnPring.class,
                TableColumnTring.class,
                TableViewHandles.class,
                TableViewPickRefiner.class,
                TextFlowDropTargetProvider.class,
                TextResizer.class,
                ToolBarDropTargetProvider.class,
                TreeTableColumnHandles.class,
                TreeTableColumnInlineEditorBounds.class,
                TreeTableColumnIntersectsBoundsCheck.class,
                TreeTableColumnPring.class,
                TreeTableColumnTring.class,
                TreeTableViewHandles.class,
                TreeTableViewPickRefiner.class,
                VBoxDropTargetProvider.class,
                VBoxTring.class,
                WebViewResizer.class,
                WindowHandles.class,
                WindowIntersectsBoundsCheck.class,
                WindowPring.class
            );
     // @formatter:on
    }
}
