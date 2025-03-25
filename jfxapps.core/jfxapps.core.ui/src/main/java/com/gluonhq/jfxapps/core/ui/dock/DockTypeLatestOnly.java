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
package com.gluonhq.jfxapps.core.ui.dock;

import java.util.Collection;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockContext;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockType;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewController;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class DockTypeLatestOnly implements DockType<Pane> {

    private final static String VIEW_SOURCE = "Pane.fxml";

    private final JfxAppContext context;

    private ObjectProperty<DockContext<Pane>> focusedProperty;

    public DockTypeLatestOnly(JfxAppContext context) {
        this.context = context;
    }

    @Override
    public String getNameKey() {
        return "viewtype.latestonly";
    }

    @Override
    public DockContext<Pane> computeView(DockContext<Pane> viewContext) {
        View view = viewContext.getView();
        ViewAttachment viewAttachment = viewContext.getViewAttachment();
        var ctrl = context.getBean(ViewController.class);

        Pane pane = FXMLUtils.load(ctrl, DockTypeLatestOnly.class, VIEW_SOURCE);

        if (view.getSearchController() != null) {
            ctrl.getViewSearchHost().getChildren().add(view.getSearchController().getRoot());
        }

        Node content = view.getViewController().getRoot();
        ctrl.getViewContentHost().getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        view.populateMenu(ctrl.getViewMenuButton());

        var dockContext = new DockContext<>(view, viewAttachment, ctrl, pane, () -> {
            if (view.getSearchController() != null) {
                ctrl.getViewSearchHost().getChildren().remove(view.getSearchController().getRoot());
            }
            ctrl.getViewContentHost().getChildren().remove(view.getViewController().getRoot());
            view.clearMenu(ctrl.getViewMenuButton());
        });

        return dockContext;
    }

    @Override
    public Node computeRoot(Collection<DockContext<Pane>> views) {
        var panes = views.stream().map(v -> v.getDockContent()).toList();
        var pane = new Pane();

        if (!panes.isEmpty()) {
            pane.getChildren().add(panes.getLast());
        }
        return pane;
    }

    @Override
    public ObjectProperty<DockContext<Pane>> focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(DockContext<Pane> focused) {
                    super.set(focused);
                }

            };
        }
        return focusedProperty;
    }
}
