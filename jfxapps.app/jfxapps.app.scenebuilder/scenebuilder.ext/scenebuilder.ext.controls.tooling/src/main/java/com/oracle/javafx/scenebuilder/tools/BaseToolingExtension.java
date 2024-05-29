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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.gluonhq.jfxapps.core.extension.AbstractExtension;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.SelectWithPringGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.AddColumnAfterAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.AddColumnBeforeAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.AddRowAboveAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.AddRowBelowAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.DecreaseColumnSpanAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.DecreaseRowSpanAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.DeleteActionContextMenuProvider;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.GridPaneMenuProvider;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.IncreaseColumnSpanAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.IncreaseRowSpanAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.MoveColumnAfterAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.MoveColumnBeforeAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.MoveRowAboveAction;
import com.oracle.javafx.scenebuilder.tools.action.gridpane.MoveRowBelowAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.UnWrapAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInAnchorPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInBorderPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInButtonBarAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInDialogPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInFlowPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInGridPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInGroupAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInHBoxAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInMenuItemProvider;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInSceneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInScrollPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInSplitPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInStackPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInStageAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInTabPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInTextFlowAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInTilePaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInTitledPaneAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInToolBarAction;
import com.oracle.javafx.scenebuilder.tools.action.wrap.WrapInVBoxAction;
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
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneDropTarget;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneHandles;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPanePring;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridPaneTring;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.ResizeColumnGesture;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.ResizeRowGesture;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.gesture.SelectAndMoveInGridGesture;
import com.oracle.javafx.scenebuilder.tools.driver.hbox.HBoxDropTargetProvider;
import com.oracle.javafx.scenebuilder.tools.driver.hbox.HBoxTring;
import com.oracle.javafx.scenebuilder.tools.driver.imageview.ImageViewDropTarget;
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
import com.oracle.javafx.scenebuilder.tools.driver.splitpane.AdjustDividerGesture;
import com.oracle.javafx.scenebuilder.tools.driver.splitpane.SplitPaneHandles;
import com.oracle.javafx.scenebuilder.tools.driver.subscene.SubSceneResizer;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabInlineEditorBounds;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabIntersectsBoundsCheck;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabPring;
import com.oracle.javafx.scenebuilder.tools.driver.tab.TabTring;
import com.oracle.javafx.scenebuilder.tools.driver.tablecolumn.ResizeTableColumnGesture;
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
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.ResizeTreeTableColumnGesture;
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
import com.oracle.javafx.scenebuilder.tools.job.gridpane.AddColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.AddColumnJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.AddRowConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.AddRowJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.DeleteColumnJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.DeleteGridSelectionJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.DeleteRowJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertColumnJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertRowConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertRowJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveCellContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveColumnContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveColumnJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveRowContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveRowJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.ReIndexColumnContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.ReIndexRowContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.RemoveColumnConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.RemoveColumnContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.RemoveRowConstraintsJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.RemoveRowContentJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.SpanJob;
import com.oracle.javafx.scenebuilder.tools.job.togglegroup.ModifySelectionToggleGroupJob;
import com.oracle.javafx.scenebuilder.tools.job.togglegroup.ModifyToggleGroupJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.UnwrapJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInAnchorPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInBorderPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInButtonBarJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInDialogPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInFlowPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInGridPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInGroupJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInHBoxJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInJobFactory;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInSceneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInScrollPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInSplitPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInStackPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInStageJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInTabPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInTextFlowJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInTilePaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInTitledPaneJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInToolBarJob;
import com.oracle.javafx.scenebuilder.tools.job.wrap.WrapInVBoxJob;
import com.oracle.javafx.scenebuilder.tools.mask.BorderPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

public class BaseToolingExtension extends AbstractExtension {

    @Override
    public UUID getId() {
        return UUID.fromString("d4784eb4-144c-41d8-9107-9112b883bfc3");
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
     // @formatter:off
        return Arrays.asList(
                AddColumnAfterAction.class,
                AddColumnBeforeAction.class,
                AddColumnConstraintsJob.class,
                AddColumnConstraintsJob.Factory.class,
                AddColumnJob.class,
                AddColumnJob.Factory.class,
                AddRowAboveAction.class,
                AddRowBelowAction.class,
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
                BorderPaneHierarchyMask.class,
                BorderPaneHierarchyMask.Factory.class,
                CanvasResizer.class,
                CircleResizer.class,
                CubicCurveEditor.class,
                CubicCurveHandles.class,
                DecreaseColumnSpanAction.class,
                DecreaseRowSpanAction.class,
                DefaultZDropTargetProvider.class,
                DeleteActionContextMenuProvider.class,
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
                GridPaneHierarchyMask.class,
                GridPaneHierarchyMask.Factory.class,
                GridPanePring.class,
                GridPaneTring.class,
                GridSelectionGroup.class,
                GridSelectionGroup.Factory.class,
                GridPaneMenuProvider.class,
                HBoxDropTargetProvider.class,
                HBoxTring.class,
                ImageViewDropTarget.class,
                ImageViewDropTarget.Factory.class,
                ImageViewDropTargetProvider.class,
                ImageViewResizer.class,
                IncreaseColumnSpanAction.class,
                IncreaseRowSpanAction.class,
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
                MoveColumnAfterAction.class,
                MoveColumnBeforeAction.class,
                MoveColumnContentJob.class,
                MoveColumnContentJob.Factory.class,
                MoveColumnJob.class,
                MoveColumnJob.Factory.class,
                MoveRowAboveAction.class,
                MoveRowBelowAction.class,
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
                WindowPring.class,
                UnwrapJob.class,
                UnwrapJob.Factory.class,
                WrapInAnchorPaneJob.class,
                WrapInAnchorPaneJob.Factory.class,
                WrapInBorderPaneJob.class,
                WrapInBorderPaneJob.Factory.class,
                WrapInButtonBarJob.class,
                WrapInButtonBarJob.Factory.class,
                WrapInDialogPaneJob.class,
                WrapInDialogPaneJob.Factory.class,
                WrapInFlowPaneJob.class,
                WrapInFlowPaneJob.Factory.class,
                WrapInGridPaneJob.class,
                WrapInGridPaneJob.Factory.class,
                WrapInGroupJob.class,
                WrapInGroupJob.Factory.class,
                WrapInHBoxJob.class,
                WrapInHBoxJob.Factory.class,
                WrapInJobFactory.class,
                WrapInPaneJob.class,
                WrapInPaneJob.Factory.class,
                WrapInSceneJob.class,
                WrapInSceneJob.Factory.class,
                WrapInScrollPaneJob.class,
                WrapInScrollPaneJob.Factory.class,
                WrapInSplitPaneJob.class,
                WrapInSplitPaneJob.Factory.class,
                WrapInStackPaneJob.class,
                WrapInStackPaneJob.Factory.class,
                WrapInStageJob.class,
                WrapInStageJob.Factory.class,
                WrapInTabPaneJob.class,
                WrapInTabPaneJob.Factory.class,
                WrapInTextFlowJob.class,
                WrapInTextFlowJob.Factory.class,
                WrapInTilePaneJob.class,
                WrapInTilePaneJob.Factory.class,
                WrapInTitledPaneJob.class,
                WrapInTitledPaneJob.Factory.class,
                WrapInToolBarJob.class,
                WrapInToolBarJob.Factory.class,
                WrapInVBoxJob.class,
                WrapInVBoxJob.Factory.class,
                ModifySelectionToggleGroupJob.class,
                ModifySelectionToggleGroupJob.Factory.class,
                ModifyToggleGroupJob.class,
                ModifyToggleGroupJob.Factory.class,
                UnWrapAction.class,
                WrapInAnchorPaneAction.class,
                WrapInBorderPaneAction.class,
                WrapInButtonBarAction.class,
                WrapInDialogPaneAction.class,
                WrapInFlowPaneAction.class,
                WrapInGridPaneAction.class,
                WrapInGroupAction.class,
                WrapInHBoxAction.class,
                WrapInMenuItemProvider.class,
                WrapInPaneAction.class,
                WrapInSceneAction.class,
                WrapInScrollPaneAction.class,
                WrapInSplitPaneAction.class,
                WrapInStackPaneAction.class,
                WrapInStageAction.class,
                WrapInTabPaneAction.class,
                WrapInTextFlowAction.class,
                WrapInTilePaneAction.class,
                WrapInTitledPaneAction.class,
                WrapInToolBarAction.class,
                WrapInVBoxAction.class
            );
     // @formatter:on
    }
}
