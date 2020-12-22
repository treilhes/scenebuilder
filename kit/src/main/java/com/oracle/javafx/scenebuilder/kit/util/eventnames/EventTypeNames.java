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
package com.oracle.javafx.scenebuilder.kit.util.eventnames;

/**
 * Collects all the event type names possible to be set in the Code section.
 */
public class EventTypeNames {

    // Main
    public static final String ON_ACTION = "onAction";

    // DragDrop
    public static final String ON_DRAG_DETECTED = "onDragDetected";
    public static final String ON_DRAG_DONE = "onDragDone";
    public static final String ON_DRAG_DROPPED = "onDragDropped";
    public static final String ON_DRAG_ENTERED = "onDragEntered";
    public static final String ON_DRAG_EXITED = "onDragExited";
    public static final String ON_DRAG_OVER = "onDragOver";
    public static final String ON_MOUSE_DRAG_ENTERED = "onMouseDragEntered";
    public static final String ON_MOUSE_DRAG_EXITED = "onMouseDragExited";
    public static final String ON_MOUSE_DRAG_OVER = "onMouseDragOver";
    public static final String ON_MOUSE_DRAG_RELEASED = "onMouseDragReleased";

    // Keyboard
    public static final String ON_INPUT_METHOD_TEXT_CHANGED = "onInputMethodTextChanged";
    public static final String ON_KEY_PRESSED = "onKeyPressed";
    public static final String ON_KEY_RELEASED = "onKeyReleased";
    public static final String ON_KEY_TYPED = "onKeyTyped";

    // Mouse
    public static final String ON_CONTEXT_MENU_REQUESTED = "onContextMenuRequested";
    public static final String ON_MOUSE_CLICKED = "onMouseClicked";
    public static final String ON_MOUSE_DRAGGED = "onMouseDragged";
    public static final String ON_MOUSE_ENTERED = "onMouseEntered";
    public static final String ON_MOUSE_EXITED = "onMouseExited";
    public static final String ON_MOUSE_MOVED = "onMouseMoved";
    public static final String ON_MOUSE_PRESSED = "onMousePressed";
    public static final String ON_MOUSE_RELEASED = "onMouseReleased";
    public static final String ON_SCROLL = "onScroll";
    public static final String ON_SCROLL_STARTED = "onScrollStarted";
    public static final String ON_SCROLL_FINISHED = "onScrollFinished";

    // Rotation
    public static final String ON_ROTATE = "onRotate";
    public static final String ON_ROTATION_FINISHED = "onRotationFinished";
    public static final String ON_ROTATION_STARTED = "onRotationStarted";

    // Swipe
    public static final String ON_SWIPE_LEFT = "onSwipeLeft";
    public static final String ON_SWIPE_RIGHT = "onSwipeRight";
    public static final String ON_SWIPE_UP = "onSwipeUp";
    public static final String ON_SWIPE_DOWN = "onSwipeDown";

    // Touch
    public static final String ON_TOUCH_MOVED = "onTouchMoved";
    public static final String ON_TOUCH_PRESSED = "onTouchPressed";
    public static final String ON_TOUCH_RELEASED = "onTouchReleased";
    public static final String ON_TOUCH_STATIONARY = "onTouchStationary";

    // Zoom
    public static final String ON_ZOOM = "onZoom";
    public static final String ON_ZOOM_STARTED = "onZoomStarted";
    public static final String ON_ZOOM_FINISHED = "onZoomFinished";

    // should be used in a static way
    private EventTypeNames() {
    }
}