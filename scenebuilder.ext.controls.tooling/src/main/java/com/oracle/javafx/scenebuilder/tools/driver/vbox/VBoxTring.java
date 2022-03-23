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
package com.oracle.javafx.scenebuilder.tools.driver.vbox;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractNodeTring;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.AccessoryDropTarget;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class VBoxTring extends AbstractNodeTring<VBox> {

    private int targetIndex;
    private final Line crackLine = new Line();

    public VBoxTring(
            Content contentPanelController,
            DocumentManager documentManager) {
        super(contentPanelController, documentManager, VBox.class);
        crackLine.getStyleClass().add(TARGET_CRACK_CLASS);
        crackLine.setMouseTransparent(true);
        getRootNode().getChildren().add(0, crackLine);
    }

    
    @Override
    public void defineDropTarget(DropTarget dropTarget) {
        assert dropTarget instanceof AccessoryDropTarget; 
        assert dropTarget.getTargetObject() instanceof FXOMInstance;
        assert dropTarget.getTargetObject().getSceneGraphObject() instanceof VBox;
        
        final AccessoryDropTarget zDropTarget = (AccessoryDropTarget) dropTarget;
        final int targetIndex;
        if (zDropTarget.getBeforeChild() == null) {
            targetIndex = -1;
        } else {
            targetIndex = zDropTarget.getBeforeChild().getIndexInParentProperty();
        }
        assert targetIndex >= -1;
        this.targetIndex = targetIndex;
    }

    
    @Override
    public void initialize() {
        assert targetIndex >= -1;
    }
    
    /*
     * AbstractGenericTring
     */

    @Override
    protected void layoutDecoration() {

        super.layoutDecoration();

        final VBox vbox = getSceneGraphObject();
        final int childCount = vbox.getChildren().size();

        if (childCount == 0) {
            // No crack line
            crackLine.setVisible(false);

        } else {
            // Computes the crack y

            final double crackY;
            final List<Node> children = vbox.getChildren();
            if (targetIndex == -1) {
                final Node child = children.get(childCount-1);
                final Bounds cb = child.localToParent(child.getLayoutBounds());
                crackY = cb.getMaxY();
            } else {
                final Node child = children.get(targetIndex);
                final Bounds cb = child.localToParent(child.getLayoutBounds());
                crackY = cb.getMinY();
            }

            // Updates the crack line
            final boolean snapToPixel = true;
            final Bounds b = getSceneGraphObject().getLayoutBounds();
            final Point2D p0 = sceneGraphObjectToDecoration(b.getMinX(), crackY, snapToPixel);
            final Point2D p1 = sceneGraphObjectToDecoration(b.getMaxX(), crackY, snapToPixel);

            crackLine.setVisible(true);
            crackLine.setStartX(p0.getX());
            crackLine.setStartY(p0.getY());
            crackLine.setEndX(p1.getX());
            crackLine.setEndY(p1.getY());
        }
    }
}
