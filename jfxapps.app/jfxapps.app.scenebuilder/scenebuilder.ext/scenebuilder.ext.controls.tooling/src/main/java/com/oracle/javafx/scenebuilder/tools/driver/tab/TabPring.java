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

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.content.gesture.AbstractGesture;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.SelectWithPringGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.SelectWithPringGesture.Factory;
import com.oracle.javafx.scenebuilder.tools.driver.tabpane.TabPaneDesignInfoX;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Paint;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class TabPring extends AbstractPring<Tab> {

    //
    //           2              3
    //           +--------------+
    //     0     |              |                 5
    //     +-----+              +-----------------+
    //     |     1              4                 |
    //     |                                      |
    //     |                                      |
    //     |                                      |
    //     +--------------------------------------+
    //     7                                      6
    //


    private TabOutline tabOutline;

    private Node tabNode; // Skin node representing the tab

    private final Factory selectWithPringGestureFactory;

    public TabPring(
            Content contentPanelController,
            ApplicationInstanceEvents documentManager,
            SelectWithPringGesture.Factory selectWithPringGestureFactory) {
        super(contentPanelController, documentManager, Tab.class);
        this.selectWithPringGestureFactory = selectWithPringGestureFactory;
    }

    @Override
    public void initialize() {
        assert getFxomInstance().getSceneGraphObject().isInstanceOf(Tab.class);

        tabOutline = new TabOutline(getSceneGraphObject());
        tabOutline.getRingPath().getStyleClass().add(PARENT_RING_CLASS);
        getRootNode().getChildren().add(tabOutline.getRingPath());

        attachPring(tabOutline.getRingPath());
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }

    /*
     * AbstractPring
     */

    @Override
    protected void layoutDecoration() {
        tabOutline.layout(this);
    }

    @Override
    public void changeStroke(Paint stroke) {
        tabOutline.getRingPath().setStroke(stroke);
    }


    /*
     * AbstractDecoration
     */

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return getSceneGraphObject().getTabPane().getLayoutBounds();
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return getSceneGraphObject().getTabPane();
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject().getParentObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        assert tabNode == null;

        final TabPane tabPane = getSceneGraphObject().getTabPane();
        startListeningToLayoutBounds(tabPane);
        startListeningToLocalToSceneTransform(tabPane);

        final TabPaneDesignInfoX di = new TabPaneDesignInfoX();
        tabNode = di.getTabNode(tabPane, getSceneGraphObject());
        startListeningToBoundsInParent(tabNode);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        assert tabNode != null;

        final TabPane tabPane = getSceneGraphObject().getTabPane();
        stopListeningToLayoutBounds(tabPane);
        stopListeningToLocalToSceneTransform(tabPane);
        stopListeningToBoundsInParent(tabNode);

        tabNode = null;
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        final AbstractGesture result;

        if (node == tabOutline.getRingPath()) {
            result = selectWithPringGestureFactory.getGesture(getFxomInstance());
        } else {
            result = null;
        }

        return result;
    }

    /*
     * Wraper to avoid the 'leaking this in constructor' warning emitted by NB.
     */
    private void attachPring(Node node) {
        attachPring(node, this);
    }
}
