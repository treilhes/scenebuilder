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
package com.gluonhq.jfxapps.core.api.dnd;

import java.net.URL;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Window;

/**
 *
 */
public abstract class AbstractDragSource implements DragSource{

    private static URL stylesheet = null;

    /**
     * Returns the URL of the CSS style associated to DragSource class.
     * This stylesheet contains rules shareable by all other components of
     * SB.
     *
     * @return URL of DragSource class style sheet (never null).
     */
    public synchronized static URL getStylesheet() {
        if (stylesheet == null) {
            stylesheet = AbstractDragSource.class.getResource("DragSource.css");
            assert stylesheet != null;
        }
        return stylesheet;
    }

    private final Window ownerWindow;

    public AbstractDragSource(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    public Window getOwnerWindow() {
        return ownerWindow;
    }

    @Override
    public abstract boolean isAcceptable();
    @Override
    public abstract List<? extends FXOMObject> getDraggedObjects();
    @Override
    public abstract FXOMObject getHitObject();
    @Override
    public abstract double getHitX();
    @Override
    public abstract double getHitY();

    @Override
    public abstract ClipboardContent makeClipboardContent();

    @Override
    public abstract Image makeDragView();

    @Override
    public abstract Node makeShadow();
    @Override
    public abstract String makeDropJobDescription();


//    @Override
//    @Deprecated
//    public abstract boolean isNodeOnly();
//    @Override
//    @Deprecated
//    public abstract boolean isSingleImageViewOnly();
//    @Override
//    @Deprecated
//    public abstract boolean isSingleTooltipOnly();
//    @Override
//    @Deprecated
//    public abstract boolean isSingleContextMenuOnly();

}
