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
package com.oracle.javafx.scenebuilder.tools.driver.borderpane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.control.droptarget.AbstractDropTargetProvider;
import com.oracle.javafx.scenebuilder.api.dnd.DropTarget;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.dnd.droptarget.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.tools.driver.borderpane.BorderPaneTring.BorderPanePosition;
import com.oracle.javafx.scenebuilder.tools.mask.BorderPaneHierarchyMask;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public final class BorderPaneDropTargetProvider extends AbstractDropTargetProvider {

    private static final Logger logger = LoggerFactory.getLogger(BorderPaneDropTargetProvider.class);

    private final AccessoryDropTarget.Factory accessoryDropTargetFactory;
    private final BorderPaneHierarchyMask.Factory borderPaneHierarchyMaskFactory;

    public BorderPaneDropTargetProvider(
            BorderPaneHierarchyMask.Factory borderPaneHierarchyMaskFactory,
            AccessoryDropTarget.Factory accessoryDropTargetFactory) {
        super();
        this.borderPaneHierarchyMaskFactory = borderPaneHierarchyMaskFactory;
        this.accessoryDropTargetFactory = accessoryDropTargetFactory;
    }

    @Override
    public DropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        assert fxomObject.getSceneGraphObject() instanceof BorderPane;
        assert fxomObject instanceof FXOMInstance;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final BorderPaneHierarchyMask mask = borderPaneHierarchyMaskFactory.getMask(fxomObject);
        final BorderPane borderPane = (BorderPane) fxomInstance.getSceneGraphObject();
        final Point2D hitPoint = CoordinateHelper.sceneToLocal(fxomInstance, sceneX, sceneY, true /* rootScene */);
        final double hitX = hitPoint.getX();
        final double hitY = hitPoint.getY();

        final Bounds layoutBounds = borderPane.getLayoutBounds();
        final Bounds centerBounds = BorderPaneTring.computeCenterBounds(borderPane);
        final Bounds topBounds = BorderPaneTring.computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.TOP);
        final Bounds bottomBounds = BorderPaneTring.computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.BOTTOM);
        final Bounds leftBounds = BorderPaneTring.computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.LEFT);
        final Bounds rightBounds = BorderPaneTring.computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.RIGHT);

        final Accessory targetAccessory;
        if (centerBounds.contains(hitX, hitY)) {
            targetAccessory = mask.getCenterAccessory();
        } else if (topBounds.contains(hitX, hitY)) {
            targetAccessory = mask.getTopAccessory();
        } else if (bottomBounds.contains(hitX, hitY)) {
            targetAccessory = mask.getBottomAccessory();
        } else if (leftBounds.contains(hitX, hitY)) {
            targetAccessory = mask.getLeftAccessory();
        } else if (rightBounds.contains(hitX, hitY)) {
            targetAccessory = mask.getRightAccessory();
        } else {
            targetAccessory = mask.getCenterAccessory();
        }

        logger.info("target accessory > {}", targetAccessory == null ? "null" : targetAccessory.getName().getName());

        return accessoryDropTargetFactory.getDropTarget(fxomInstance, targetAccessory);
    }

}
