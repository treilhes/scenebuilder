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

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.ui.dock.DockContext;
import com.gluonhq.jfxapps.core.api.ui.dock.DockType;
import com.gluonhq.jfxapps.core.api.ui.dock.View;
import com.gluonhq.jfxapps.core.api.ui.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.dock.ViewController;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Singleton
@Lazy
public class DockTypeTab implements DockType<Tab> {

    private final static String VIEW_SOURCE = "Tab.fxml";

    private final JfxAppContext context;

    private ObjectProperty<DockContext<Tab>> focusedProperty;

    public DockTypeTab(JfxAppContext context) {
        this.context = context;
    }

    @Override
    public String getNameKey() {
        return "viewtype.tabbed";
    }

    @Override
    public DockContext<Tab> computeView(DockContext<Tab> viewContext) {
        View view = viewContext.getView();
        ViewAttachment viewAttachment = viewContext.getViewAttachment();
        var ctrl = context.getBean(ViewController.class);

        Tab tab = FXMLUtils.load(ctrl, DockTypeTab.class, VIEW_SOURCE);

        tab.textProperty().bind(view.nameProperty());
        tab.setOnSelectionChanged(e -> {
            if (tab.getTabPane() != null && tab.getTabPane().getScene() != null && tab.isSelected()) {
                view.notifyFocused();
            }
        });
        if (view.getSearchController() != null) {
            ctrl.getViewSearchHost().getChildren().add(view.getSearchController().getRoot());
        }

        Node content = view.getViewController().getRoot();
        ctrl.getViewContentHost().getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        view.populateMenu(ctrl.getViewMenuButton());

        var dockContext = new DockContext<>(view, viewAttachment, ctrl, tab, () -> {
            tab.textProperty().unbind();
            if (view.getSearchController() != null) {
                ctrl.getViewSearchHost().getChildren().remove(view.getSearchController().getRoot());
            }
            ctrl.getViewContentHost().getChildren().remove(view.getViewController().getRoot());
            view.clearMenu(ctrl.getViewMenuButton());
        });

        return dockContext;
    }

    @Override
    public Node computeRoot(Collection<DockContext<Tab>> views) {//, DockContext<Tab> focused) {
        Tab[] panes = views.stream().map(v -> v.getDockContent()).toArray(Tab[]::new);
        var tabs = new TabPane(panes);
//
//        if (focused == null && !tabs.getTabs().isEmpty()) {
//            tabs.getSelectionModel().select(tabs.getTabs().get(0));
//        } else if (focused != null) {
//            tabs.getSelectionModel().select(focused.getDockContent());
//        }

        return tabs;
    }

    @Override
    public ObjectProperty<DockContext<Tab>> focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new SimpleObjectProperty<>() {

                @Override
                public void set(DockContext<Tab> focused) {
                    if (focused != null) {
                        Tab tab = focused.getDockContent();
                        TabPane tabPane = tab.getTabPane();
                        tabPane.getSelectionModel().select(tab);
                    }
                    super.set(focused);
                }

            };
        }
        return focusedProperty;
    }
}
