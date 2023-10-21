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
package com.oracle.javafx.scenebuilder.tools.driver.tab;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.content.gesture.DiscardGesture;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.content.util.BoundsUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.handles.AbstractResilientHandles;
import com.oracle.javafx.scenebuilder.tools.driver.tabpane.TabPaneDesignInfoX;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Handles for Tab need a special treatment.
 *
 * A Tab instance can be transiently disconnected from its parent TabPane:<br/>
 * - Tab.getTabPane() returns null <br/>
 * - TabPane.getTabs().contains() returns false<br/>
 *<br/>
 * When the Tab is disconnected, handles cannot be drawn. This Handles class
 * inherits from {@link AbstractResilientHandles} to take care of this singularity.
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TabHandles extends AbstractResilientHandles<Tab> {

    private TabPane tabPane;
    private Node tabNode; // Skin node representing the tab

    public TabHandles(
            Driver driver,
            Content contentPanelController,
            FxmlDocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            ResizeGesture.Factory resizeGestureFactory) {
        super(driver, contentPanelController, documentManager, discardGestureFactory, resizeGestureFactory, Tab.class);
    }

    @Override
    public void initialize() {
        getSceneGraphObject().tabPaneProperty()
                .addListener((ChangeListener<TabPane>) (ov, v1, v2) -> tabPaneDidChange());

        tabPaneDidChange();
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
        assert tabPane != null;

        if (tabNode == null) {
            tabNode = lookupTabNode();
        }

        // Convert tabNode bounds from tabNode local space to tabPane local space
        final Bounds b = tabNode.getLayoutBounds();
        final Point2D min = CoordinateHelper.localToLocal(tabNode, b.getMinX(), b.getMinY(), tabPane);
        final Point2D max = CoordinateHelper.localToLocal(tabNode, b.getMaxX(), b.getMaxY(), tabPane);

        return BoundsUtils.makeBounds(min, max);
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        assert isReady();
        assert tabPane != null;

        return tabPane;
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject().getParentObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        assert isReady();
        assert tabPane != null;

        if (tabNode == null) {
            tabNode = lookupTabNode();
        }

        startListeningToLayoutBounds(tabPane);
        startListeningToLocalToSceneTransform(tabPane);
        startListeningToBoundsInParent(tabNode);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        assert isReady();
        assert tabPane != null;

        stopListeningToLayoutBounds(tabPane);
        stopListeningToLocalToSceneTransform(tabPane);
        stopListeningToBoundsInParent(tabNode);
    }

    /*
     * Private
     */

    private void tabPaneDidChange() {
        tabPane = getSceneGraphObject().getTabPane();
        setReady(tabPane != null);
    }

    private Node lookupTabNode() {
        assert tabPane != null;

        final TabPaneDesignInfoX di = new TabPaneDesignInfoX();
        return di.getTabNode(tabPane, getSceneGraphObject());
    }
}
