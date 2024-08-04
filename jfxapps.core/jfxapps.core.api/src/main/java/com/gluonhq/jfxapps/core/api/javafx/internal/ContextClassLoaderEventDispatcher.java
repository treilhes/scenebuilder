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
package com.gluonhq.jfxapps.core.api.javafx.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * This class is used to intercept javafx events in order to set the context class loader
 * to the class loader of the currently scoped application instance.
 */
@Singleton
public class ContextClassLoaderEventDispatcher implements EventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ContextClassLoaderEventDispatcher.class);

    private final JavafxThreadClassloaderDispatcherImpl dispatcher;
    private EventDispatcher originalDispatcher;


    public ContextClassLoaderEventDispatcher(JavafxThreadClassloaderDispatcherImpl dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setup(EventDispatcher originalDispatcher) {
        this.originalDispatcher = originalDispatcher;
    }

    private Window windowFromSource(Object source) {
        Window window = switch (source) {
            case null -> null;
            case Node o -> windowFromSource(o.getScene().getWindow());
            case Scene o -> windowFromSource(o.getWindow());
            case MenuItem o -> windowFromSource(o.getParentPopup());
            case PopupWindow o -> windowFromSource(o.getOwnerWindow());
            case Window o -> o;
            default -> null;
        };

        return window == null ? null : window.getScene().getWindow();
    }
    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {

        Window window = windowFromSource(event.getTarget());

        try {
            if (event instanceof WindowEvent we
                    && we.getEventType() == WindowEvent.WINDOW_HIDDEN
                    && window == we.getSource() && window == we.getTarget()) {
                tail.append((e,t) -> {
                    dispatcher.unregister(window);
                    return e;
                });
            }
            return dispatcher.callWith(window, () -> originalDispatcher.dispatchEvent(event, tail));
        } catch (Exception e) {
            log.error("Error dispatching event", event, e);
        }

        return null;
    }
}