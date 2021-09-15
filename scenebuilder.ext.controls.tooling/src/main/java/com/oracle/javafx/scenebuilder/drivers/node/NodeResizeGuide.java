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
package com.oracle.javafx.scenebuilder.drivers.node;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.control.resizer.AbstractResizeGuide;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.ResizeGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.guides.ResizingGuideController;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * 
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class NodeResizeGuide extends AbstractResizeGuide<Node> {

    private ResizingGuideController resizingGuideController;

    public NodeResizeGuide(Content contentPanelController) {
        super(contentPanelController, Node.class);
        
    }

    @Override
    public void initialize() {
    }

    @Override
    protected void layoutDecoration() {
        //resizingGuideController.match(targetBounds);
    }

    

    @Override
    public void update() {
        super.update();
        AbstractGesture gesture = AbstractGesture.lookupGesture(getSceneGraphObject());
        
        if (gesture != null && gesture instanceof ResizeGesture) {
            ResizeGesture resizeGesture = (ResizeGesture)gesture;
            setupResizingGuideController(resizeGesture);
            
         // Compute mouse displacement in local coordinates of scene graph object
            final double startSceneX = resizeGesture.getMousePressedEvent().getSceneX();
            final double startSceneY = resizeGesture.getMousePressedEvent().getSceneY();
            final double currentSceneX = resizeGesture.getLastMouseEvent().getSceneX();
            final double currentSceneY = resizeGesture.getLastMouseEvent().getSceneY();
            final Point2D start = CoordinateHelper.sceneToLocal(getFxomObject(), startSceneX, startSceneY, true /* rootScene */);
            final Point2D current = CoordinateHelper.sceneToLocal(getFxomObject(), currentSceneX, currentSceneY, true /* rootScene */);
            final double rawDeltaX, rawDeltaY;
            if ((start != null) && (current != null)) {
                rawDeltaX = current.getX() - start.getX();
                rawDeltaY = current.getY() - start.getY();
            } else {
                // sceneGraphObject is bizarrely configured (eg it has scaleX=0)
                // We use the scene coordinates
                rawDeltaX = currentSceneX - startSceneX;
                rawDeltaY = currentSceneY - startSceneY;
            }
            
         // Clamps deltaX/deltaY relatively to tunable.
            // Example: tunable == E => clampDeltaX = rawDeltaX, clampDeltaY = 0.0
            final Point2D clampDelta = resizeGesture.clampVector(rawDeltaX, rawDeltaY);
            final double clampDeltaX = clampDelta.getX();
            final double clampDeltaY = clampDelta.getY();

            // Compute candidateBounds
            final Bounds layoutBounds = getSceneGraphObject().getLayoutBounds();
            final Bounds resizedBounds = resizeGesture.getResizedBounds(layoutBounds, clampDeltaX, clampDeltaY);
            final Bounds candidateBounds;
            if (resizeGesture.isSnapRequired()) {
                final double ratio = layoutBounds.getHeight() / layoutBounds.getWidth();
                candidateBounds = resizeGesture.snapBounds(resizedBounds, ratio);
            } else {
                candidateBounds = resizedBounds;
            }

            // Computes new layout bounds from the candidate bounds
            final double candidateWidth = candidateBounds.getWidth();
            final double candidateHeight = candidateBounds.getHeight();
            final Bounds newLayoutBounds = new BoundingBox(0, 0, Math.round(candidateWidth), Math.round(candidateHeight));

            resizingGuideController.match(newLayoutBounds);
        }
    }

    private void setupResizingGuideController(ResizeGesture resizeGesture) {
        resizingGuideController = new ResizingGuideController(
                //resizeGesture.isMatchWidth(), resizeGesture.isMatchHeight(), getContentPanelController().getGuidesColor());
                resizeGesture.isMatchWidth(), resizeGesture.isMatchHeight(), Color.PINK);

        addToResizingGuideController(getFxomObject().getFxomDocument().getFxomRoot());

        getRootNode().getChildren().clear();
        
        final Group guideGroup = resizingGuideController.getGuideGroup();
        assert guideGroup.isMouseTransparent();
        
        getRootNode().getChildren().add(guideGroup);
    }


    private void addToResizingGuideController(FXOMObject fxomObject) {
        assert fxomObject != null;

        if (fxomObject != getFxomObject()) {
            if (fxomObject.getSceneGraphObject() instanceof Node) {
                final Node sceneGraphNode = (Node) fxomObject.getSceneGraphObject();
                resizingGuideController.addSampleBounds(sceneGraphNode);
            }

            final DesignHierarchyMask m = new DesignHierarchyMask(fxomObject);
            if (m.isAcceptingSubComponent()) {
                for (int i = 0, count = m.getSubComponentCount(); i < count; i++) {
                    addToResizingGuideController(m.getSubComponentAtIndex(i));
                }
            }
        }
    }

}
