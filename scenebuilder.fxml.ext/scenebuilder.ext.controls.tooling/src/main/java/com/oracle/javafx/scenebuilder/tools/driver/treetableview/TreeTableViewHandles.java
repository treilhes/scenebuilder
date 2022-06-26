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
package com.oracle.javafx.scenebuilder.tools.driver.treetableview;

import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.DiscardGesture;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractNodeHandles;
import com.oracle.javafx.scenebuilder.tools.driver.treetablecolumn.ResizeTreeTableColumnGesture;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TreeTableViewHandles extends AbstractNodeHandles<Node> implements InitializingBean {

    private final Group grips = new Group();
	//private SceneBuilderBeanFactory context;
	private final DesignHierarchyMask.Factory maskFactory;
    private final ResizeTreeTableColumnGesture.Factory resizeTreeTableColumnGestureFactory;

    public TreeTableViewHandles(
            Driver driver,
            Content contentPanelController,
            FxmlDocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            ResizeGesture.Factory resizeGestureFactory,
            DesignHierarchyMask.Factory maskFactory,
    		ResizeTreeTableColumnGesture.Factory resizeTreeTableColumnGestureFactory) {
        super(driver, contentPanelController, documentManager, discardGestureFactory, resizeGestureFactory, Node.class);
        //this.context = context;
        this.maskFactory = maskFactory;
        this.resizeTreeTableColumnGestureFactory = resizeTreeTableColumnGestureFactory;
    }

    //FIXME replace with jsr250 annotation @PostConstrut
    @Override
    public void afterPropertiesSet() throws Exception {
        getRootNode().getChildren().add(grips); // Above handles
    }

    @Override
    public void initialize() {
        assert getFxomInstance().getSceneGraphObject() instanceof TreeTableView;
    }

    public TreeTableView<?> getTreeTableView() {
        return (TreeTableView<?>) getSceneGraphObject();
    }

    /*
     * AbstractNodeHandles
     */
    @Override
    protected void layoutDecoration() {
        super.layoutDecoration();

        // Adjusts the number of grip lines to the number of dividers
        adjustGripCount();

        // Updates grip positions
        for (int i = 0, count = getTreeTableView().getColumns().size(); i < count; i++) {
            layoutGrip(i);
        }
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        final AbstractGesture result;

        final int gripIndex = grips.getChildren().indexOf(node);
        if (gripIndex != -1) {
            final HierarchyMask m = maskFactory.getMask(getFxomInstance());
            final FXOMObject columnObject = m.getSubComponentAtIndex(m.getMainAccessory(), gripIndex, false);
            assert columnObject instanceof FXOMInstance;
            result = resizeTreeTableColumnGestureFactory.getGesture((FXOMInstance) columnObject);
        } else {
            result = super.findGesture(node);
        }

        return result;
    }


    /*
     * Private
     */

    private void adjustGripCount() {
        final int columnCount = getTreeTableView().getColumns().size();
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

        final TreeTableColumn<?,?> tc = getTreeTableView().getColumns().get(gripIndex);

        if (tc.isVisible()) {
            final TreeTableViewDesignInfoX di = new TreeTableViewDesignInfoX();
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
