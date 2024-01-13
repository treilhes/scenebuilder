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
package com.oracle.javafx.scenebuilder.sourcegen.util.eventnames;

/**
 * Collects all the event type names possible to be set in the Code section.
 */
public class EventTypeNames {

    // Main
    public static final String ON_ACTION = "onAction"; // NOI18N

    // DragDrop
    public static final String ON_DRAG_DETECTED = "onDragDetected"; // NOI18N
    public static final String ON_DRAG_DONE = "onDragDone"; // NOI18N
    public static final String ON_DRAG_DROPPED = "onDragDropped"; // NOI18N
    public static final String ON_DRAG_ENTERED = "onDragEntered"; // NOI18N
    public static final String ON_DRAG_EXITED = "onDragExited"; // NOI18N
    public static final String ON_DRAG_OVER = "onDragOver"; // NOI18N
    public static final String ON_MOUSE_DRAG_ENTERED = "onMouseDragEntered"; // NOI18N
    public static final String ON_MOUSE_DRAG_EXITED = "onMouseDragExited"; // NOI18N
    public static final String ON_MOUSE_DRAG_OVER = "onMouseDragOver"; // NOI18N
    public static final String ON_MOUSE_DRAG_RELEASED = "onMouseDragReleased"; // NOI18N

    // Keyboard
    public static final String ON_INPUT_METHOD_TEXT_CHANGED = "onInputMethodTextChanged"; // NOI18N
    public static final String ON_KEY_PRESSED = "onKeyPressed"; // NOI18N
    public static final String ON_KEY_RELEASED = "onKeyReleased"; // NOI18N
    public static final String ON_KEY_TYPED = "onKeyTyped"; // NOI18N

    // Mouse
    public static final String ON_CONTEXT_MENU_REQUESTED = "onContextMenuRequested"; // NOI18N
    public static final String ON_MOUSE_CLICKED = "onMouseClicked"; // NOI18N
    public static final String ON_MOUSE_DRAGGED = "onMouseDragged"; // NOI18N
    public static final String ON_MOUSE_ENTERED = "onMouseEntered"; // NOI18N
    public static final String ON_MOUSE_EXITED = "onMouseExited"; // NOI18N
    public static final String ON_MOUSE_MOVED = "onMouseMoved"; // NOI18N
    public static final String ON_MOUSE_PRESSED = "onMousePressed"; // NOI18N
    public static final String ON_MOUSE_RELEASED = "onMouseReleased"; // NOI18N
    public static final String ON_SCROLL = "onScroll"; // NOI18N
    public static final String ON_SCROLL_STARTED = "onScrollStarted"; // NOI18N
    public static final String ON_SCROLL_FINISHED = "onScrollFinished"; // NOI18N

    // Rotation
    public static final String ON_ROTATE = "onRotate"; // NOI18N
    public static final String ON_ROTATION_FINISHED = "onRotationFinished"; // NOI18N
    public static final String ON_ROTATION_STARTED = "onRotationStarted"; // NOI18N

    // Swipe
    public static final String ON_SWIPE_LEFT = "onSwipeLeft"; // NOI18N
    public static final String ON_SWIPE_RIGHT = "onSwipeRight"; // NOI18N
    public static final String ON_SWIPE_UP = "onSwipeUp"; // NOI18N
    public static final String ON_SWIPE_DOWN = "onSwipeDown"; // NOI18N

    // Touch
    public static final String ON_TOUCH_MOVED = "onTouchMoved"; // NOI18N
    public static final String ON_TOUCH_PRESSED = "onTouchPressed"; // NOI18N
    public static final String ON_TOUCH_RELEASED = "onTouchReleased"; // NOI18N
    public static final String ON_TOUCH_STATIONARY = "onTouchStationary"; // NOI18N

    // Zoom
    public static final String ON_ZOOM = "onZoom"; // NOI18N
    public static final String ON_ZOOM_STARTED = "onZoomStarted"; // NOI18N
    public static final String ON_ZOOM_FINISHED = "onZoomFinished"; // NOI18N

    // should be used in a static way
    private EventTypeNames() {
    }
}