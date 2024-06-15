/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.control.handles;

import java.net.URL;

import com.gluonhq.jfxapps.core.api.Gesture;
import com.gluonhq.jfxapps.core.api.content.decoration.AbstractDecoration;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture.Factory;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.oracle.javafx.scenebuilder.api.control.Handles;

import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 *
 *
 */
public abstract class AbstractHandles<T> extends AbstractDecoration<T> implements Handles<T> {

    public static final String SELECTION_RECT = "selection-rect"; //NOCHECK
    public static final String SELECTION_WIRE = "selection-wire"; //NOCHECK
    public static final String SELECTION_PIPE = "selection-pipe"; //NOCHECK
    public static final String SELECTION_HANDLES = "selection-handles"; //NOCHECK
    public static final String SELECTION_HANDLES_DIM = "selection-handles-dim"; //NOCHECK
    public static final double SELECTION_HANDLES_SIZE = 10.0; // pixels

    private static Image squareHandleImage = null;
    private static Image sideHandleImage = null;
    private static Image squareHandleDimImage = null;
    private static Image sideHandleDimImage = null;

    private boolean enabled = true;
    protected final Factory discardGestureFactory;

    public AbstractHandles(
            Workspace workspace,
            DocumentManager documentManager,
            DiscardGesture.Factory discardGestureFactory,
            Class<T> sceneGraphClass) {
        super(workspace, documentManager, sceneGraphClass);
        this.discardGestureFactory = discardGestureFactory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled){
            this.enabled = enabled;
            enabledDidChange();
        }
    }

    @Override
    public Gesture findEnabledGesture(Node node) {
        final Gesture result;

        if (enabled) {
            result = findGesture(node);
        } else {
            result = discardGestureFactory.getGesture();
        }

        return result;
    }

    public abstract Gesture findGesture(Node node);

    public abstract void enabledDidChange();

    private static final String HANDLES = "HANDLES";

    public static AbstractHandles<?> lookupHandles(Node node) {
        assert node != null;
        assert node.isMouseTransparent() == false;

        final AbstractHandles<?> result;
        final Object value = node.getProperties().get(HANDLES);
        if (value instanceof AbstractHandles) {
            result = (AbstractHandles<?>) value;
        } else {
            assert value == null;
            result = null;
        }

        return result;
    }

    public static void attachHandles(Node node, AbstractHandles<?> handles) {
        assert node != null;
        assert node.isMouseTransparent() == false;
        assert lookupHandles(node) == null;

        if (handles == null) {
            node.getProperties().remove(HANDLES);
        } else {
            node.getProperties().put(HANDLES, handles);
        }
    }

    public synchronized static Image getCornerHandleImage() {
        if (squareHandleImage == null) {
            final URL url = AbstractHandles.class.getResource("corner-handle.png");
            squareHandleImage = new Image(url.toString());
        }
        return squareHandleImage;
    }

    public synchronized static Image getSideHandleImage() {
        if (sideHandleImage == null) {
            final URL url = AbstractHandles.class.getResource("side-handle.png");
            sideHandleImage = new Image(url.toString());
        }
        return sideHandleImage;
    }

    public synchronized static Image getCornerHandleDimImage() {
        if (squareHandleDimImage == null) {
            final URL url = AbstractHandles.class.getResource("corner-handle-dim.png");
            squareHandleDimImage = new Image(url.toString());
        }
        return squareHandleDimImage;
    }

    public synchronized static Image getSideHandleDimImage() {
        if (sideHandleDimImage == null) {
            final URL url = AbstractHandles.class.getResource("side-handle-dim.png");
            sideHandleDimImage = new Image(url.toString());
        }
        return sideHandleDimImage;
    }

    @Override
    public void update(SelectionGroup selectionGroup) {

    }


}
