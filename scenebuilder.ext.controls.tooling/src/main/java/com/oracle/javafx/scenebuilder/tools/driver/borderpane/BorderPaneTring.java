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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractNodeTring;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.mask.BorderPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.AccessoryDropTarget;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class BorderPaneTring extends AbstractNodeTring<BorderPane> {

    private static final Logger logger = LoggerFactory.getLogger(BorderPaneTring.class);

    public enum BorderPanePosition {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        CENTER
    }

    private Accessory targetAccessory;
    private final BorderPane borderPane = new BorderPane();
    private final Label topLabel = new Label();
    private final Label bottomLabel = new Label();
    private final Label leftLabel = new Label();
    private final Label rightLabel = new Label();
    private final Label centerLabel = new Label();
    private BorderPaneHierarchyMask borderPaneHierarchyMask;

    private final BorderPaneHierarchyMask.Factory borderPaneHierarchyMaskFactory;

    public BorderPaneTring(
            Content contentPanelController,
            DocumentManager documentManager,
            BorderPaneHierarchyMask.Factory borderPaneHierarchyMaskFactory) {
        super(contentPanelController, documentManager, BorderPane.class);
        this.borderPaneHierarchyMaskFactory = borderPaneHierarchyMaskFactory;
    }

    private BorderPaneHierarchyMask getMask() {
        if (borderPaneHierarchyMask == null || borderPaneHierarchyMask.getFxomObject() != getFxomObject()) {
            borderPaneHierarchyMask = borderPaneHierarchyMaskFactory.getMask(getFxomInstance());
        }
        return borderPaneHierarchyMask;
    }

    @Override
    public void defineDropTarget(DropTarget dropTarget) {
        assert dropTarget instanceof AccessoryDropTarget;

        final AccessoryDropTarget accessoryDropTarget = (AccessoryDropTarget) dropTarget;
        this.targetAccessory = accessoryDropTarget.getAccessory();

        logger.info("target accessory > {}", targetAccessory == null ? "null" : targetAccessory.getName().getName());

        assert (getMask().getTopAccessory().equals(targetAccessory)
                || getMask().getBottomAccessory().equals(targetAccessory)
                || getMask().getLeftAccessory().equals(targetAccessory)
                || getMask().getRightAccessory().equals(targetAccessory)
                || getMask().getCenterAccessory().equals(targetAccessory));
    }

    @Override
    public void initialize() {
        assert this.targetAccessory != null;

        topLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        topLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        bottomLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        bottomLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        leftLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        leftLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rightLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rightLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        centerLabel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        centerLabel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        topLabel.setText(getMask().getTopAccessory().getName().getName().toUpperCase());
        bottomLabel.setText(getMask().getBottomAccessory().getName().getName().toUpperCase());
        leftLabel.setText(getMask().getLeftAccessory().getName().getName().toUpperCase());
        rightLabel.setText(getMask().getRightAccessory().getName().getName().toUpperCase());
        centerLabel.setText(getMask().getCenterAccessory().getName().getName().toUpperCase());

        topLabel.getStyleClass().add(TARGET_RING_CLASS);
        topLabel.getStyleClass().add(BorderPane.class.getSimpleName());
        bottomLabel.getStyleClass().add(TARGET_RING_CLASS);
        bottomLabel.getStyleClass().add(BorderPane.class.getSimpleName());
        leftLabel.getStyleClass().add(TARGET_RING_CLASS);
        leftLabel.getStyleClass().add(BorderPane.class.getSimpleName());
        rightLabel.getStyleClass().add(TARGET_RING_CLASS);
        rightLabel.getStyleClass().add(BorderPane.class.getSimpleName());
        centerLabel.getStyleClass().add(TARGET_RING_CLASS);
        centerLabel.getStyleClass().add(BorderPane.class.getSimpleName());


        topLabel.setVisible(getMask().getAccessory(getMask().getTopAccessory()) == null);
        bottomLabel.setVisible(getMask().getAccessory(getMask().getBottomAccessory()) == null);
        leftLabel.setVisible(getMask().getAccessory(getMask().getLeftAccessory()) == null);
        rightLabel.setVisible(getMask().getAccessory(getMask().getRightAccessory()) == null);
        centerLabel.setVisible(getMask().getAccessory(getMask().getCenterAccessory()) == null);

        borderPane.setTop(topLabel);
        borderPane.setBottom(bottomLabel);
        borderPane.setLeft(leftLabel);
        borderPane.setRight(rightLabel);
        borderPane.setCenter(centerLabel);
        borderPane.setMinWidth(Region.USE_PREF_SIZE);
        borderPane.setMinHeight(Region.USE_PREF_SIZE);
        borderPane.setMaxWidth(Region.USE_PREF_SIZE);
        borderPane.setMaxHeight(Region.USE_PREF_SIZE);

        getRootNode().getChildren().add(0, borderPane);
    }


    public static Bounds computeCenterBounds(BorderPane sceneGraphObject) {
        final Bounds b = sceneGraphObject.getLayoutBounds();

        final double x0 = b.getMinX();
        final double x3 = b.getMaxX();
        final double x1 = x0 + (x3 - x0) * 0.25;
        final double x2 = x0 + (x3 - x0) * 0.75;

        final double y0 = b.getMinY();
        final double y3 = b.getMaxY();
        final double y1 = y0 + (y3 - y0) * 0.25;
        final double y2 = y0 + (y3 - y0) * 0.75;

        return new BoundingBox(x1, y1, x2 - x1, y2 - y1);
    }



    public static Bounds computeAreaBounds(Bounds lb, Bounds cb, BorderPanePosition area) {
        assert lb != null;
        assert cb != null;

        /*
         *      lb.minx                                    lb.maxx
         *              cb.minx                   cb.maxx
         *  lb.miny o----------------------------------------o
         *          |                  Top                   |
         *  cb.miny o-----o-------------------------o--------o
         *          |     |                         |        |
         *          |     |                         |        |
         *          |     |                         |        |
         *          |Left |           Center        |  Right |
         *          |     |                         |        |
         *          |     |                         |        |
         *          |     |                         |        |
         *  cb.maxy o-----o-------------------------o--------o
         *          |                                        |
         *          |                 Bottom                 |
         *          |                                        |
         *  lb.maxy o----------------------------------------o
         *
         */

        final double xmin, ymin, xmax, ymax;
        switch(area) {
            case TOP:
                xmin = lb.getMinX();
                ymin = lb.getMinY();
                xmax = lb.getMaxX();
                ymax = cb.getMinY();
                break;
            case BOTTOM:
                xmin = lb.getMinX();
                ymin = cb.getMaxY();
                xmax = lb.getMaxX();
                ymax = lb.getMaxY();
                break;
            case LEFT:
                xmin = lb.getMinX();
                ymin = cb.getMinY();
                xmax = cb.getMinX();
                ymax = cb.getMaxY();
                break;
            case RIGHT:
                xmin = cb.getMaxX();
                ymin = cb.getMinY();
                xmax = lb.getMaxX();
                ymax = cb.getMaxY();
                break;
            case CENTER:
                xmin = cb.getMinX();
                ymin = cb.getMinY();
                xmax = cb.getMaxX();
                ymax = cb.getMaxY();
                break;
            default:
                // Emergency code
                assert false : "Unexpected area " + area; //NOCHECK
                xmin = cb.getMinX();
                ymin = cb.getMinY();
                xmax = cb.getMaxX();
                ymax = cb.getMaxY();
                break;
        }

        return new BoundingBox(xmin, ymin, xmax - xmin, ymax - ymin);
    }


    /*
     * AbstractGenericTring
     */

    @Override
    protected void layoutDecoration() {

        super.layoutDecoration();

        final Bounds layoutBounds = getSceneGraphObject().getLayoutBounds();
        borderPane.setPrefWidth(layoutBounds.getWidth());
        borderPane.setPrefHeight(layoutBounds.getHeight());


        final Bounds centerBounds = computeCenterBounds(getSceneGraphObject());
        centerLabel.setPrefSize(centerBounds.getWidth(), centerBounds.getHeight());

        final Bounds topBounds = computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.TOP);
        topLabel.setPrefSize(topBounds.getWidth(), topBounds.getHeight());

        final Bounds bottomBounds = computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.BOTTOM);
        bottomLabel.setPrefSize(bottomBounds.getWidth(), bottomBounds.getHeight());

        final Bounds leftBounds = computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.LEFT);
        leftLabel.setPrefSize(leftBounds.getWidth(), leftBounds.getHeight());

        final Bounds rightBounds = computeAreaBounds(layoutBounds, centerBounds, BorderPanePosition.RIGHT);
        rightLabel.setPrefSize(rightBounds.getWidth(), rightBounds.getHeight());

        final Label targetLabel;
        switch(targetAccessory.getName().getName()) {
            case "top":
                targetLabel = topLabel;
                break;
            case "bottom":
                targetLabel = bottomLabel;
                break;
            case "left":
                targetLabel = leftLabel;
                break;
            case "right":
                targetLabel = rightLabel;
                break;
            case "center":
                targetLabel = centerLabel;
                break;
            default:
                // Emergency code
                assert false;
                targetLabel = centerLabel;
                break;
        }

        setupSelectedStyleClass(topLabel, topLabel == targetLabel);
        setupSelectedStyleClass(bottomLabel, bottomLabel == targetLabel);
        setupSelectedStyleClass(leftLabel, leftLabel == targetLabel);
        setupSelectedStyleClass(rightLabel, rightLabel == targetLabel);
        setupSelectedStyleClass(centerLabel, centerLabel == targetLabel);


        // Update (decoration) border pane transform
        borderPane.getTransforms().clear();
        borderPane.getTransforms().add(getSceneGraphObjectToDecorationTransform());
    }



    /*
     * Private
     */

    private static final String SELECTED = "selected"; //NOCHECK

    private static void setupSelectedStyleClass(Label label, boolean selected) {
            final List<String> styleClass = label.getStyleClass();
        if (selected) {
            if (styleClass.contains(SELECTED) == false) {
                    styleClass.add(SELECTED);
            }
        } else {
            if (styleClass.contains(SELECTED)) {
                styleClass.remove(SELECTED);
            }
        }
    }
}
