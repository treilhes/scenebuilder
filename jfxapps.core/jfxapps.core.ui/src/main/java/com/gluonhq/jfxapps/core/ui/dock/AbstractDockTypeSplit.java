/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.ui.dock;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockContext;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockType;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewController;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class AbstractDockTypeSplit implements DockType<Node> {

    private final static String VIEW_SOURCE = "View.fxml";

    private final JfxAppContext context;

    private final Orientation orientation;

    private ObjectProperty<DockContext<Node>> focusedProperty;

    public AbstractDockTypeSplit(JfxAppContext context, Orientation orientation) {
        this.context = context;
        this.orientation = orientation;
    }

    @Override
    public DockContext<Node> computeView(DockContext<Node> viewContext) {

        View view = viewContext.getView();
        ViewAttachment viewAttachment = viewContext.getViewAttachment();

        var ctrl = context.getBean(ViewController.class);

        Node node = FXMLUtils.load(ctrl, AbstractDockTypeSplit.class, VIEW_SOURCE);

        ctrl.getViewLabel().textProperty().bind(view.nameProperty());
        node.setOnMouseEntered(e -> view.notifyFocused());

        if (view.getSearchController() != null) {
            ctrl.getViewSearchHost().getChildren().add(view.getSearchController().getRoot());
        }

        Node content = view.getViewController().getRoot();
        ctrl.getViewContentHost().getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        view.populateMenu(ctrl.getViewMenuButton());

        var newViewContext = new DockContext<>(view, viewAttachment, ctrl, node, () -> {
            ctrl.getViewLabel().textProperty().unbind();
            if (view.getSearchController() != null) {
                ctrl.getViewSearchHost().getChildren().remove(view.getSearchController().getRoot());
            }
            ctrl.getViewContentHost().getChildren().remove(view.getViewController().getRoot());
            view.clearMenu(ctrl.getViewMenuButton());
        });

        return newViewContext;
    }

    @Override
    public Node computeRoot(Collection<DockContext<Node>> views) {//, DockContext<Node> focused) {
        SplitPane sPane = new SplitPane();
        sPane.setOrientation(orientation);

        double coef = 1.0 / views.size();

        AtomicInteger index = new AtomicInteger();

        views.forEach(d -> {
            int i = index.getAndIncrement();
            sPane.setDividerPosition(i, coef * (i + 1));
            sPane.getItems().add(d.getDockContent());
        });

        return sPane;
    }

    @Override
    public ObjectProperty<DockContext<Node>> focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(DockContext<Node> focused) {
                    if (focused != null) {
                        Node node = focused.getDockContent();
                        node.requestFocus();
                    }
                    super.set(focused);
                }

            };
        }
        return focusedProperty;
    }


}
