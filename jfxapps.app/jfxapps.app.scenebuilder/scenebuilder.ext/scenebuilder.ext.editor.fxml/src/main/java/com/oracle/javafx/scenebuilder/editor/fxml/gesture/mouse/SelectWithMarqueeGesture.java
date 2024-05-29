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
package com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.HierarchyMask.Accessory;
import com.gluonhq.jfxapps.core.api.content.gesture.AbstractMouseGesture;
import com.gluonhq.jfxapps.core.api.content.gesture.GestureFactory;
import com.gluonhq.jfxapps.core.api.content.mode.Layer;
import com.gluonhq.jfxapps.core.api.content.mode.ModeManager;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.Pring;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.editor.fxml.controller.EditModeController;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SelectWithMarqueeGesture extends AbstractMouseGesture {

    private FXOMObject hitObject;
    private FXOMObject scopeObject;
    private Pring<?> scopeHilit;
    private final Set<FXOMObject> candidates = new HashSet<>();
    private final Rectangle marqueeRect = new Rectangle();
    private final Driver driver;
    private Layer<Rudder> rudderLayer;
    private ModeManager modeManager;
    private final DesignHierarchyMask.Factory maskFactory;
    private final Selection selection;
    private final FxmlDocumentManager documentManager;

    protected SelectWithMarqueeGesture(
            FxmlDocumentManager documentManager,
            Driver driver,
            Selection selection,
            DesignHierarchyMask.Factory maskFactory,
            @Lazy Content contentPanelController,
            @Lazy EditModeController editMode) {
        super(contentPanelController);
        this.documentManager = documentManager;
        this.driver = driver;
        this.selection = selection;
        this.maskFactory = maskFactory;
        rudderLayer = editMode.getLayer(Rudder.class);
        assert rudderLayer != null;
    }

    public void setupGestureParameters() {

    }
    // TODO move this method to setupGestureParameters like other gesture, but check the patential performance penalty in EditModeController
    public void setup(FXOMObject hitObject, FXOMObject scopeObject) {
        assert (hitObject == null) || (hitObject.isDescendantOf(scopeObject) == false);
        this.hitObject = hitObject;
        this.scopeObject = scopeObject;
        marqueeRect.getStyleClass().add("marquee");
    }

    public FXOMObject getHitObject() {
        return hitObject;
    }

    /*
     * AbstractMouseGesture
     */

    @Override
    protected void mousePressed() {
    }

    @Override
    protected void mouseDragStarted() {
        selection.clear();
        collectCandidates();
        showScopeHilit();
        showMarqueeRect();
    }

    @Override
    protected void mouseDragged() {
        updateMarqueeRect();
        updateSelection();
    }

    @Override
    protected void mouseDragEnded() {
        candidates.clear();
        hideScopeHilit();
        hideMarqueeRect();
    }

    @Override
    protected void mouseReleased() {
        // Mouse has not been dragged
        // If an object is below the mouse, then we select it.
        // Else we unselect all.
        if (isMouseDidDrag() == false) {
            if (hitObject != null) {
                selection.select(hitObject);
            } else {
                selection.clear();
            }
        }
    }

    @Override
    protected void keyEvent(KeyEvent e) {
    }

    @Override
    protected void userDidCancel() {
    }


    /*
     * Private
     */

    private void showScopeHilit() {
        if (scopeObject != null) {

            assert driver != null;
            scopeHilit = driver.makePring(scopeObject);
            //scopeHilit.changeStroke(contentPanelController.getPringColor());
            rudderLayer.getLayerUI().getChildren().add(scopeHilit.getRootNode());
        }
    }


    private void hideScopeHilit() {
        if (scopeHilit != null) {

            assert rudderLayer.getLayerUI().getChildren().contains(scopeHilit.getRootNode());
            rudderLayer.getLayerUI().getChildren().remove(scopeHilit.getRootNode());
            scopeHilit = null;
        }
    }

    private void showMarqueeRect() {

        rudderLayer.getLayerUI().getChildren().add(marqueeRect);
        updateMarqueeRect();
    }

    private void updateMarqueeRect() {
        final double xPressed = getMousePressedEvent().getSceneX();
        final double yPressed = getMousePressedEvent().getSceneY();
        final double xCurrent = getLastMouseEvent().getSceneX();
        final double yCurrent = getLastMouseEvent().getSceneY();

        final double xMin = Math.min(xPressed, xCurrent);
        final double yMin = Math.min(yPressed, yCurrent);
        final double xMax = Math.max(xPressed, xCurrent);
        final double yMax = Math.max(yPressed, yCurrent);


        final Point2D p0 = rudderLayer.getLayerUI().sceneToLocal(xMin, yMin, true /* rootScene */);
        final Point2D p1 = rudderLayer.getLayerUI().sceneToLocal(xMax, yMax, true /* rootScene */);

        marqueeRect.setX(p0.getX());
        marqueeRect.setY(p0.getY());
        marqueeRect.setWidth(p1.getX() - p0.getX());
        marqueeRect.setHeight(p1.getY() - p0.getY());
    }

    private void hideMarqueeRect() {
        rudderLayer.getLayerUI().getChildren().remove(marqueeRect);
    }


    private void updateSelection() {
        final double xPressed = getMousePressedEvent().getSceneX();
        final double yPressed = getMousePressedEvent().getSceneY();
        final double xCurrent = getLastMouseEvent().getSceneX();
        final double yCurrent = getLastMouseEvent().getSceneY();

        final double xMin = Math.min(xPressed, xCurrent);
        final double yMin = Math.min(yPressed, yCurrent);
        final double xMax = Math.max(xPressed, xCurrent);
        final double yMax = Math.max(yPressed, yCurrent);
        final BoundingBox marqueeBounds
                = new BoundingBox(xMin, yMin, xMax - xMin, yMax - yMin);

        final Set<FXOMObject> winners = new HashSet<>();
        for (FXOMObject candidate : candidates) {
            if ((driver != null) && driver.intersectsBounds(candidate, marqueeBounds)) {
                winners.add(candidate);
            }
        }

        selection.select(winners);
    }


    private void collectCandidates() {
        if (scopeObject == null) {
            // Only one candidate : the root object
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            if ((fxomDocument != null) && (fxomDocument.getFxomRoot() != null)) {
                candidates.add(fxomDocument.getFxomRoot());
            }
        } else {
            final HierarchyMask m = maskFactory.getMask(scopeObject);

            List<Accessory> allAccessories = new ArrayList<>(m.getAccessories());
            if (m.getMainAccessory() != null && !allAccessories.contains(m.getMainAccessory())) {
                allAccessories.add(0, m.getMainAccessory());
            }

            for (Accessory accessory : allAccessories) {
                if (m.isAcceptingAccessory(accessory)) {
                    final List<FXOMObject> fxomObjects = m.getAccessories(accessory, false);
                    fxomObjects.stream()
                        .filter(f -> f != null)
                        .forEach(candidates::add);
                }
            }
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<SelectWithMarqueeGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public SelectWithMarqueeGesture getGesture() { //FXOMObject hitObject, FXOMObject scopeObject) {
            return create(SelectWithMarqueeGesture.class, null); //g -> g.setupGestureParameters(hitObject, scopeObject));
        }
    }

}
