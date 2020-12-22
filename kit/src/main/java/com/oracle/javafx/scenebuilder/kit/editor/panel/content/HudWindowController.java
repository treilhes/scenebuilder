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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.CardinalPoint;
import com.oracle.javafx.scenebuilder.api.HudWindow;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlPopupController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.util.LineEquation;

import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.WindowEvent;


/**
 *
 * The Hud Window: <br><img src="doc-files\hud-window.png" alt="hud window"><br>
 * Appears when resizing a node in the Editor using the resizing handle<br>
 * Shows dynamic sizing informations while dragging the resizing handle
 * @treatAsPrivate
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class HudWindowController extends AbstractFxmlPopupController implements HudWindow {

    /** The grid pane. */
    @FXML private GridPane gridPane;

    /** The row constraint 0. */
    @FXML private RowConstraints rowConstraint0; // assigned by text editing fxml

    /** The row count. */
    private int rowCount;

    /**
     * Instantiates a new hud window controller.
     */
    public HudWindowController() {
        super(HudWindowController.class.getResource("HudWindow.fxml"));
    }

    /** The relative position. */
    private CardinalPoint relativePosition = CardinalPoint.SE;

    /**
     * Gets the relative position.
     *
     * @return the relative position
     */
    public CardinalPoint getRelativePosition() {
        return relativePosition;
    }

    /**
     * Sets the relative position.
     *
     * @param relativePosition the new relative position
     */
    @Override
    public void setRelativePosition(CardinalPoint relativePosition) {
        this.relativePosition = relativePosition;
    }

    /**
     * Gets the row count.
     *
     * @return the row count
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount the new row count
     */
    @Override
    public void setRowCount(int rowCount) {
        // We force fxml to load so that we can call reconfigureGridPane().
        getRoot();
        assert gridPane != null;

        this.rowCount = rowCount;
        reconfigureGridPane();
    }

    /**
     * Sets the name at row index.
     *
     * @param name the name
     * @param rowIndex the row index
     */
    @Override
    public void setNameAtRowIndex(String name, int rowIndex) {
        assert (0 <= rowIndex);
        assert (rowIndex < gridPane.getRowConstraints().size());

        final int nameChildIndex = rowIndex * 2;
        final Label nameLabel = (Label) gridPane.getChildren().get(nameChildIndex);
        nameLabel.setText(name);
    }

    /**
     * Sets the value at row index.
     *
     * @param value the value
     * @param rowIndex the row index
     */
    @Override
    public void setValueAtRowIndex(String value, int rowIndex) {
        assert (0 <= rowIndex);
        assert (rowIndex < gridPane.getRowConstraints().size());

        final int valueChildIndex = rowIndex * 2+1;
        final Label valueLabel = (Label) gridPane.getChildren().get(valueChildIndex);
        valueLabel.setText(value);
    }

    /*
     * AbstractFxmlPopupController
     */

    /**
     * Controller did load fxml.
     */
    @Override
    public void controllerDidLoadFxml() {
        assert gridPane != null;
        assert rowConstraint0 != null;

        getRoot().setMouseTransparent(true);

        // We start with no row so remove any row constraints put at design time.
        assert rowCount == 0;
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
        reconfigureGridPane();
    }

    /**
     * Controller did create popup.
     */
    @Override
    protected void controllerDidCreatePopup() {

        // We must fully control the popup location and visibility
        getPopup().setAutoFix(false);
        getPopup().setAutoHide(false);

        // This must be false else the "ESC" key is consumed by the
        // popup and cannot be received by the content panel gestures.
        getPopup().setConsumeAutoHidingEvents(false);
    }

    /**
     * On hidden.
     *
     * @param event the event
     */
    @Override
    protected void onHidden(WindowEvent event) {
    }

    /**
     * Anchor bounds did change.
     */
    @Override
    protected void anchorBoundsDidChange() {
        updatePopupLocation();
    }

    /**
     * Anchor transform did change.
     */
    @Override
    protected void anchorTransformDidChange() {
        updatePopupLocation();
    }

    /**
     * Anchor XY did change.
     */
    @Override
    protected void anchorXYDidChange() {
        updatePopupLocation();
    }

    /**
     * Update popup location.
     */
    @Override
    public void updatePopupLocation() {
        // At exit time, closeRequestHandler() is not always called.
        // So this method can be invoked after the anchor has been removed the
        // scene. This looks like a bug in FX...
        // Anway we protect ourself by checking.
        if (getAnchor() != null && getAnchor().getScene() != null) {
            final Point2D popupLocation = computePopupLocation();
            getPopup().setX(popupLocation.getX());
            getPopup().setY(popupLocation.getY());
        }
    }



    /*
     * Private
     */


    /**
     * Compute popup location.
     *
     * @return the point 2 D
     */
    private Point2D computePopupLocation() {

        /*
         *
         *       O-----+               O-----+               O-----+
         *       | NW  |               |  N  |               | NE  |
         *       +-----k               +--k--+               k-----+
         *
         *
         *                    o-----------o-----------o
         *                    |                       |
         *       O-----+      |                       |      O-----+
         *       |  W  k      o         anchor        o      k  E  |
         *       +-----+      |          node         |      +-----+
         *                    |                       |
         *                    o-----------o-----------o
         *
         *
         *       O-----k               O--k--+               O--k--+
         *       | SW  |               |  S  |               | SE  |
         *       +-----+               +-----+               +-----+
         */

        final Bounds anchorBounds = getAnchor().getLayoutBounds();
        final Bounds usefulBounds = clampBounds(anchorBounds, 1.0, 1.0);
        assert usefulBounds.getWidth() > 0.0;
        assert usefulBounds.getHeight() > 0.0;
        final Point2D p0 = relativePosition.getPosition(usefulBounds);
        final Point2D p1 = relativePosition.getOpposite().getPosition(usefulBounds);
        final Point2D sp0 = getAnchor().localToScreen(p0);
        final Point2D sp1 = getAnchor().localToScreen(p1);
        assert sp0 != null;
        assert sp1 != null;
        final LineEquation leq = new LineEquation(sp0, sp1);


        final Point2D k = leq.pointAtOffset(-30.0);

        final double ox, oy; // Point O on the diagram above
        final Bounds popupBounds = getRoot().getLayoutBounds();
        switch(relativePosition) {
            case N:
                ox = k.getX() - popupBounds.getWidth() / 2.0;
                oy = k.getY() - popupBounds.getHeight();
                break;
            case NE:
                ox = k.getX();
                oy = k.getY() - popupBounds.getHeight();
                break;
            case E:
                ox = k.getX();
                oy = k.getY() - popupBounds.getHeight() / 2.0;
                break;
            case SE:
                ox = k.getX();
                oy = k.getY();
                break;
            case S:
                ox = k.getX() - popupBounds.getWidth() / 2.0;
                oy = k.getY();
                break;
            case SW:
                ox = k.getX() - popupBounds.getWidth();
                oy = k.getY();
                break;
            case W:
                ox = k.getX() - popupBounds.getWidth();
                oy = k.getY() - popupBounds.getHeight() / 2.0;
                break;
            case NW:
                ox = k.getX() - popupBounds.getWidth();
                oy = k.getY() - popupBounds.getHeight();
                break;
            default:
                assert false : "unexpected cardinal point:" + this;
                ox = k.getX();
                oy = k.getY();
                break;
        }

        return new Point2D(ox, oy);
    }



    /**
     * Reconfigure grid pane.
     */
    private void reconfigureGridPane() {

        final List<RowConstraints> rowConstraints = gridPane.getRowConstraints();
        if (rowCount < rowConstraints.size()) {
            // There's too many rows : let's remove some
            while (rowCount < rowConstraints.size()) {
                removeLastRow();
            }
        } else {
            // There's not enough rows : let's add some
            while (rowCount > rowConstraints.size()) {
                appendRow();
            }
        }
    }


    /**
     * Append row.
     */
    private void appendRow() {
        final int newRowIndex = gridPane.getRowConstraints().size();

        // Add an entry to gridPane.rowConstraints.
        // We clone rowConstraint0 and add it to gridPane.
        final RowConstraints rc = new RowConstraints();
        rc.setFillHeight(rowConstraint0.isFillHeight());
        rc.setMaxHeight(rowConstraint0.getMaxHeight());
        rc.setMinHeight(rowConstraint0.getMinHeight());
        rc.setPercentHeight(rowConstraint0.getPercentHeight());
        rc.setPrefHeight(rowConstraint0.getPrefHeight());
        rc.setValignment(rowConstraint0.getValignment());
        rc.setVgrow(rowConstraint0.getVgrow());
        gridPane.getRowConstraints().add(rc);

        // Add two Labels to gridPane.children
        final Label nameLabel = new Label();
        final Label valueLabel = new Label();
        nameLabel.getStyleClass().add("hud-property-label");
        valueLabel.getStyleClass().add("hud-value-label");
        gridPane.getChildren().add(nameLabel);
        gridPane.getChildren().add(valueLabel);
        GridPane.setRowIndex(nameLabel, newRowIndex);
        GridPane.setRowIndex(valueLabel, newRowIndex);
        GridPane.setColumnIndex(nameLabel, 0);
        GridPane.setColumnIndex(valueLabel, 1);
    }


    /**
     * Removes the last row.
     */
    private void removeLastRow() {
        assert gridPane.getRowConstraints().size() >= 1;
        assert gridPane.getChildren().size() >= 2;

        final int lastRowIndex = gridPane.getRowConstraints().size()-1;
        gridPane.getRowConstraints().remove(lastRowIndex);
        gridPane.getChildren().remove(lastRowIndex * 2 + 1);
        gridPane.getChildren().remove(lastRowIndex * 2 + 0);
    }

    /**
     * Returns bounds whose width and height are at least the specified minima.
     * @param b bounds
     * @param minWidth minimum width
     * @param minHeight minimum height
     * @return b if its width/height are above minWidth/minHeight or new adjusted bounds
     */
    private static Bounds clampBounds(Bounds b, double minWidth, double minHeight) {
        assert b != null;
        assert minWidth > 0.0;
        assert minHeight > 0.0;

        final Bounds result;

        if ((b.getWidth() >= minWidth) && (b.getHeight() >= minHeight)) {
            // Fast track
            result = b;
        } else {
            final double minX, width;
            if (b.getWidth() >= minWidth) {
                minX = b.getMinX();
                width = b.getWidth();
            } else {
                minX = b.getMinX() - minWidth / 2.0;
                width = minWidth;
            }

            final double minY, height;
            if (b.getHeight() >= minHeight) {
                minY = b.getMinY();
                height = b.getHeight();
            } else {
                minY = b.getMinY() - minHeight / 2.0;
                height = minHeight;
            }

            result = new BoundingBox(minX, minY, width, height);
        }

        return result;
    }
}
