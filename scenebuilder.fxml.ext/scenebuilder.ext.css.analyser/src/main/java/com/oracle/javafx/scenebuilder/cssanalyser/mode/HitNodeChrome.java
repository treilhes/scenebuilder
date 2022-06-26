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

package com.oracle.javafx.scenebuilder.cssanalyser.mode;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.decoration.AbstractDecoration;
import com.oracle.javafx.scenebuilder.core.content.util.RegionRectangle;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
*
* Hit node chrome: <br><img src="doc-files/hit-node-chrome.png" alt="hit node chrome"><br>
* Appears when picking mode is enabled
*
*/
public class HitNodeChrome extends AbstractDecoration<Object> {

    private Node hitNode;
    private final RegionRectangle chrome = new RegionRectangle();
    private Node closestNode;

    public HitNodeChrome(
            Content contentPanelController, 
            FxmlDocumentManager documentManager,
            Node hitNode) {
        super(contentPanelController, documentManager, Object.class);

        assert hitNode != null;
        assert hitNode.getScene() != null;

        this.hitNode = hitNode;

        chrome.setMouseTransparent(true);
        chrome.getRegion().getStyleClass().add("css-pick-chrome"); //NOCHECK
        getRootNode().getChildren().add(chrome);
    }

    @Override
    public void initialize() {
        this.closestNode = findClosestNode();
        assert closestNode != null;
        assert closestNode.getScene() == hitNode.getScene();

    }

    public Node getHitNode() {
        return hitNode;
    }


    /*
     * AbstractDecoration
     */

    @Override
    public Bounds getSceneGraphObjectBounds() {
        return closestNode.getLayoutBounds();
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return closestNode;
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject().getClosestNode();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        startListeningToLayoutBounds(closestNode);
        startListeningToLocalToSceneTransform(closestNode);
        startListeningToBoundsInParent(hitNode);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        stopListeningToLayoutBounds(closestNode);
        stopListeningToLocalToSceneTransform(closestNode);
        stopListeningToBoundsInParent(hitNode);
    }

    @Override
    protected void layoutDecoration() {
        assert chrome.getScene() != null;

        if (getState() != State.CLEAN) {
            chrome.setVisible(false);
        } else {
            assert hitNode.getScene() != null;
            assert hitNode.getScene() == closestNode.getScene();

            //FIXME uncomment and solve
//            final Transform t = getContentPanelController().computeSceneGraphToRudderLayerTransform(hitNode);
//            chrome.getTransforms().clear();
//            chrome.getTransforms().add(t);

            chrome.setLayoutBounds(hitNode.getLayoutBounds());
            chrome.setVisible(true);
        }
    }

    @Override
    public State getState() {
        State result = super.getState();

        if (result == State.CLEAN) {
            final Node newClosestNode = findClosestNode();
            if (closestNode != newClosestNode) {
                result = State.NEEDS_RECONCILE;
            }
        }

        return result;
    }

    @Override
    public void reconcile() {
        super.reconcile();
        hitNode = closestNode = findClosestNode();
    }



    /*
     * Private
     */

    private Node findClosestNode() {
        final FXOMObject nodeObject = getFxomObject().getClosestNode();
        assert nodeObject != null; // At least the root is a Node
        assert nodeObject.getSceneGraphObject() instanceof Node;
        return (Node) nodeObject.getSceneGraphObject();
    }
}
