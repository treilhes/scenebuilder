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
package com.oracle.javafx.scenebuilder.tools.driver.tablecolumn;

import java.util.List;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.DiscardGesture;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractResilientHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tableview.TableViewDesignInfoX;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TableColumnHandles extends AbstractResilientHandles<Object> {

    /*
     * Handles for TableColumn need a special treatment.
     *
     * A TableColumn instance can be transiently disconnected from its parent TableView:
     *  - TableColumn.getTableView() returns null
     *  - TableView.getColumns().contains() returns false
     *
     * When the TableColumn is disconnected, handles cannot be drawn.
     * This Handles class inherits from AbstractResilientHandles to take
     * care of this singularity.
     */

    private final Group grips = new Group();
    private final TableViewDesignInfoX tableViewDesignInfo
            = new TableViewDesignInfoX();
    private TableView<?> tableView;
    private Node columnHeaderNode;
	//private final SceneBuilderBeanFactory context;
    private final DesignHierarchyMask.Factory maskFactory;
    private final ResizeTableColumnGesture.Factory resizeTableColumnGestureFactory;

    public TableColumnHandles(
            Driver driver,
            Content contentPanelController,
            FxmlDocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            ResizeGesture.Factory resizeGestureFactory,
            DesignHierarchyMask.Factory maskFactory,
    		ResizeTableColumnGesture.Factory resizeTableColumnGestureFactory) {
        super(driver, contentPanelController, documentManager, discardGestureFactory, resizeGestureFactory, Object.class);
//        this.context = context;
        this.maskFactory = maskFactory;
        this.resizeTableColumnGestureFactory = resizeTableColumnGestureFactory;
    }

    @Override
    public void initialize() {
        assert getFxomInstance().getSceneGraphObject() instanceof TableColumn;

        getRootNode().getChildren().add(grips); // Above handles

        getTableColumn().tableViewProperty().addListener(
                (ChangeListener<Object>) (ov, v1, v2) -> tableViewOrVisibilityDidChange());
        getTableColumn().visibleProperty().addListener(
                (ChangeListener<Object>) (ov, v1, v2) -> tableViewOrVisibilityDidChange());

        tableViewOrVisibilityDidChange();
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }

    /*
     * AbstractGenericHandles
     */
    @Override
    public Bounds getSceneGraphObjectBounds() {
        assert isReady();
        assert tableView != null;
        assert getTableColumn().isVisible();
        return tableViewDesignInfo.getColumnBounds(getTableColumn());
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        assert isReady();
        assert tableView != null;
        return tableView;
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject().getParentObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        assert isReady();
        assert tableView != null;
        startListeningToLayoutBounds(tableView);
        startListeningToLocalToSceneTransform(tableView);

        assert columnHeaderNode == null;
        columnHeaderNode = tableViewDesignInfo.getColumnNode(getTableColumn());
        startListeningToBoundsInParent(columnHeaderNode);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        assert isReady();
        assert tableView != null;
        stopListeningToLayoutBounds(tableView);
        stopListeningToLocalToSceneTransform(tableView);

        assert columnHeaderNode != null;
        stopListeningToBoundsInParent(columnHeaderNode);
        columnHeaderNode = null;
    }

    @Override
    protected void layoutDecoration() {
        assert tableView != null;

        super.layoutDecoration();

        // Adjusts the number of grip lines to the number of dividers
        adjustGripCount();

        // Updates grip positions
        for (int i = 0, count = getTableColumns().size(); i < count; i++) {
            layoutGrip(i);
        }
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        final AbstractGesture result;

        final int gripIndex = grips.getChildren().indexOf(node);
        if (gripIndex != -1) {
            final FXOMObject parentObject = getFxomInstance().getParentObject();
            final HierarchyMask m = maskFactory.getMask(parentObject);
            final FXOMObject columnObject = m.getSubComponentAtIndex(m.getMainAccessory(), gripIndex, false);
            assert columnObject instanceof FXOMInstance;
            result = resizeTableColumnGestureFactory.getGesture((FXOMInstance)columnObject);
        } else {
            result = super.findGesture(node);
        }

        return result;
    }


    /*
     * Private
     */

    private TableColumn<?,?> getTableColumn() {
        assert getSceneGraphObject() instanceof TableColumn;
        return (TableColumn<?,?>) getSceneGraphObject();
    }

    private void tableViewOrVisibilityDidChange() {
        tableView = getTableColumn().getTableView();
        setReady((tableView != null) && getTableColumn().isVisible());
    }

    private List<?> getTableColumns() {
        final List<?> result;

        final TableColumn<?,?> tableColumn = getTableColumn();
        if (tableColumn.getParentColumn() == null) {
            result = tableView.getColumns();
        } else {
            result = tableColumn.getParentColumn().getColumns();
        }

        return result;
    }



    /*
     * Private (grips)
     */

    private void adjustGripCount() {
        assert tableView != null;

        final int columnCount = getTableColumns().size();
        final List<Node> gripChildren = grips.getChildren();

        while (gripChildren.size() < columnCount) {
            gripChildren.add(makeGripLine());
        }
        while (gripChildren.size() > columnCount) {
            gripChildren.remove(gripChildren.size()-1);
        }
    }

    private Line makeGripLine() {
        final Line result = new Line();
        result.setStrokeWidth(SELECTION_HANDLES_SIZE);
        result.setStroke(Color.TRANSPARENT);
        result.setCursor(Cursor.H_RESIZE);
        attachHandles(result);
        return result;
    }

    private void layoutGrip(int gripIndex) {
        assert grips.getChildren().get(gripIndex) instanceof Line;
        assert getTableColumns().get(gripIndex) instanceof TableColumn<?,?>;

        final List<?> columns = getTableColumns();
        final TableColumn<?,?> tc = (TableColumn<?,?>)columns.get(gripIndex);
        if (tc.isVisible()) {
            final TableViewDesignInfoX di = new TableViewDesignInfoX();
            final Bounds b = di.getColumnHeaderBounds(tc);
            final double startX = b.getMaxX();
            final double startY = b.getMinY();
            final double endY = b.getMaxY();

            final boolean snapToPixel = true;
            final Point2D startPoint = sceneGraphObjectToDecoration(startX, startY, snapToPixel);
            final Point2D endPoint = sceneGraphObjectToDecoration(startX, endY, snapToPixel);

            final Line gripLine = (Line) grips.getChildren().get(gripIndex);
            gripLine.setVisible(true);
            gripLine.setManaged(true);
            gripLine.setStartX(startPoint.getX());
            gripLine.setStartY(startPoint.getY());
            gripLine.setEndX(endPoint.getX());
            gripLine.setEndY(endPoint.getY());
        } else {
            final Line gripLine = (Line) grips.getChildren().get(gripIndex);
            gripLine.setVisible(false);
            gripLine.setManaged(false);
        }
    }


    /*
     * Wrapper to avoid the 'leaking this in constructor' warning emitted by NB.
     */
    private void attachHandles(Node node) {
        attachHandles(node, this);
    }
}
