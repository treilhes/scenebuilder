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
package com.gluonhq.jfxapps.core.api.ui.controller;

import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPopupController.
 */
public abstract class AbstractPopupController extends AbstractCommonUiController {

    /** The popup. */
    private Popup popup;

    /** The anchor. */
    private Node anchor;

    /** The anchor window. */
    private Window anchorWindow;

    public AbstractPopupController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager) {
        super(scenebuilderManager, documentManager);
    }

    /**
     * Gets the popup.
     *
     * @return the popup
     */
    public Popup getPopup() {
        assert Platform.isFxApplicationThread();

        if (popup == null) {
            popup = new Popup();
            popup.getContent().add(getRoot());
            popup.setOnHidden(onHiddenHandler);
            controllerDidCreatePopup();
        }

        return popup;
    }


    /**
     * Open window.
     *
     * @param anchor the anchor
     */
    public void openWindow(Node anchor) {
        assert Platform.isFxApplicationThread();
        assert anchor != null;
        assert anchor.getScene() != null;
        assert anchor.getScene().getWindow() != null;

        this.anchor = anchor;
        this.anchorWindow = anchor.getScene().getWindow();

        this.anchor.layoutBoundsProperty().addListener(layoutBoundsListener);
        this.anchor.localToSceneTransformProperty().addListener(localToSceneTransformListener);
        this.anchorWindow.xProperty().addListener(xyListener);

        getPopup().show(this.anchor.getScene().getWindow());
        anchorBoundsDidChange();
        updatePopupLocation();
    }

    /**
     * Close window.
     */
    public void closeWindow() {
        assert Platform.isFxApplicationThread();
        getPopup().hide();
        // Note : Popup.hide() will invoke onHiddenHandler() which
        // will remove listeners set by openWindow.
    }

    /**
     * Checks if is window opened.
     *
     * @return true, if is window opened
     */
    public boolean isWindowOpened() {
        return (popup == null) ? false : popup.isShowing();
    }

    /**
     * Gets the anchor.
     *
     * @return the anchor
     */
    public Node getAnchor() {
        return anchor;
    }


    /*
     * To be implemented by subclasses
     */

//    /**
//     * Creates the FX object composing the panel.
//     * This routine is called by {@link AbstractPopupController#getRoot}.
//     * It *must* invoke {@link AbstractPanelController#setPanelRoot}.
//     *
//     */
//    protected abstract void makeRoot();

    /**
     * On hidden.
     *
     * @param event the event
     */
    protected abstract void onHidden(WindowEvent event);

    /**
     * Anchor bounds did change.
     */
    protected abstract void anchorBoundsDidChange();

    /**
     * Anchor transform did change.
     */
    protected abstract void anchorTransformDidChange();

    /**
     * Anchor XY did change.
     */
    protected abstract void anchorXYDidChange();

    /**
     * Controller did create popup.
     */
    protected void controllerDidCreatePopup() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
    }

    /**
     * Update popup location.
     */
    protected abstract void updatePopupLocation();


    /*
     * For subclasses
     */

    /*
     * Private
     */

    /** The layout bounds listener. */
    private final ChangeListener<Bounds> layoutBoundsListener
    = (ov, t, t1) -> anchorBoundsDidChange();


    /** The local to scene transform listener. */
    private final ChangeListener<Transform> localToSceneTransformListener
    = (ov, t, t1) -> anchorTransformDidChange();


    /** The xy listener. */
    private final ChangeListener<Number> xyListener
    = (ov, t, t1) -> anchorXYDidChange();

    /** The on hidden handler. */
    private final EventHandler<WindowEvent> onHiddenHandler = e -> {
        assert anchor != null;

        onHidden(e);

        anchor.layoutBoundsProperty().removeListener(layoutBoundsListener);
        anchor.localToSceneTransformProperty().removeListener(localToSceneTransformListener);
        anchorWindow.xProperty().removeListener(xyListener);

        anchor = null;
        anchorWindow = null;
    };
}
