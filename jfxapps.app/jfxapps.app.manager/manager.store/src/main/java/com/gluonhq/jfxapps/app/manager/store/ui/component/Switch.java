/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.app.manager.store.ui.component;

import java.util.function.Supplier;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Switch {

    private enum Direction {
        LeftToRight, RightToLeft
    }

    private final StackPane container;
    private final ObservableList<Node> screenStack;

    public Switch(StackPane container) {
        this.container = container;
        this.screenStack = FXCollections.observableArrayList();

        initialize();
    }

    private void initialize() {
        screenStack.addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    container.getChildren().addAll(c.getAddedSubList());
                }
                if (c.wasRemoved()) {
                    container.getChildren().removeAll(c.getRemoved());
                }
            }
        });
    }

    private TranslateTransition slideTransition(Node from, Node to, double width, Direction direction) {

        width = Math.abs(width);

        from.setTranslateX(0);

        switch (direction) {
        case LeftToRight:
            to.setTranslateX(width);
            break;
        case RightToLeft:
            to.setTranslateX(-width);
            break;
        }

        TranslateTransition hideScreen = new TranslateTransition(Duration.millis(500), from);
        switch (direction) {
        case LeftToRight:
            hideScreen.setToX(-width);
            break;
        case RightToLeft:
            hideScreen.setToX(width);
            break;
        }

        TranslateTransition showScreen = new TranslateTransition(Duration.millis(500), to);
        showScreen.setToX(0);

        hideScreen.play();
        showScreen.play();

        return hideScreen;
    }

    public void next(Supplier<Node> nodeSupplier) {
        next(nodeSupplier.get());
    }

    public void next(Node node) {

        node.getStyleClass().add("--fx-background-color: blue;");
        if (screenStack.isEmpty()) {
            screenStack.add(node);
            return;
        }

        double width = container.getBoundsInLocal().getWidth();
        var last = screenStack.getLast();

        screenStack.add(node);


        last.setCache(true);
        node.setCache(true);
        slideTransition(last, node, width, Direction.LeftToRight).setOnFinished(e -> {
            last.setCache(false);
            node.setCache(false);
        });
    }

    public void back() {
        if (screenStack.size() <= 1) {
            return;
        }

        double width = container.getBoundsInLocal().getWidth();
        var last = screenStack.getLast();
        var previous = screenStack.get(screenStack.size() - 2);

        last.setCache(true);
        previous.setCache(true);
        slideTransition(last, previous, width, Direction.RightToLeft).setOnFinished(e -> {
            last.setCache(false);
            previous.setCache(false);
            screenStack.remove(last);
        });

    }

    public void reset() {
        screenStack.clear();
    }
}
