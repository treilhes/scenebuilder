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
package com.gluonhq.jfxapps.core.ui.dock.type;

import java.util.Arrays;
import java.util.Collection;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockContext;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockContextDisposer;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.type.DockType;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
public class DockTypeAccordion implements DockType<TitledPane>, com.gluonhq.jfxapps.core.api.ui.controller.dock.type.Accordion {

    private final static String VIEW_SOURCE = "TitledPane.fxml";

    private final JfxAppContext context;

    private ObjectProperty<DockContext<TitledPane>> focusedProperty;

    public DockTypeAccordion(JfxAppContext context) {
        this.context = context;
    }

    @Override
    public String getNameKey() {
        return "viewtype.accordion";
    }

    @Override
    public boolean isMultiViews() {
        return true;
    }

    @Override
    public DockContext<TitledPane> computeView(DockContext<TitledPane> viewContext) {
        var view = viewContext.getView();
        var viewAttachment = viewContext.getViewAttachment();

        var viewController = context.getBean(ViewController.class);
        var searchController = view.getSearchController();
        var contentController = view.getContentController();

        TitledPane titledPane = FXMLUtils.load(viewController, DockTypeAccordion.class, VIEW_SOURCE);

        var searchHost = viewController.getViewSearchHost();
        var contentHost = viewController.getViewContentHost();
        var viewMenu = viewController.getViewMenuButton();

        viewController.getViewLabel().textProperty().bind(view.nameProperty());
        titledPane.setOnMouseEntered(e -> view.notifyFocused());

        if (searchController != null) {
            var searchRoot = searchController.getRoot();
            searchHost.getChildren().add(searchRoot);
        }

        var contentRoot = contentController.getRoot();
        contentHost.getChildren().add(contentRoot);

        VBox.setVgrow(contentRoot, Priority.ALWAYS);

        view.populateMenu(viewMenu);

        DockContextDisposer disposer = () -> {
            viewController.getViewLabel().textProperty().unbind();

            if (searchController != null) {
                var searchNode = searchController.getRoot();
                searchHost.getChildren().remove(searchNode);
            }

            contentHost.getChildren().remove(contentController.getRoot());
            view.clearMenu(viewMenu);
        };

        return new DockContext<>(view, viewAttachment, viewController, titledPane, disposer);
    }

    @Override
    public Node computeRoot(Collection<DockContext<TitledPane>> views) {//, DockContext<TitledPane> focused) {
        TitledPane[] panes = views.stream().map(v -> v.getDockContent()).toArray(TitledPane[]::new);

        if (panes != null && panes.length == 1) {
            panes[0].setCollapsible(false);
        } else {
            Arrays.stream(panes).forEach(tp -> tp.setCollapsible(true));
        }
        var accordion = new Accordion(panes);

//        if (focused == null && !accordion.getPanes().isEmpty()) {
//            accordion.setExpandedPane(accordion.getPanes().get(0));
//        } else if (focused != null) {
//            accordion.setExpandedPane(focused.getDockContent());
//        }

        return accordion;
    }

    @Override
    public ObjectProperty<DockContext<TitledPane>> focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(DockContext<TitledPane> focused) {
                    if (focused != null && focused.getDockContent() != null && focused.getDockContent().getParent() != null) {
                        TitledPane titlePane = focused.getDockContent();
                        Accordion accordion = (Accordion)titlePane.getParent();
                        accordion.setExpandedPane(titlePane);
                    }
                    super.set(focused);
                }

            };
        }
        return focusedProperty;
    }

}
