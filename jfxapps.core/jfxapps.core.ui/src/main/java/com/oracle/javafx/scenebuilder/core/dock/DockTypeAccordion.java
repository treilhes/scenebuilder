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
package com.oracle.javafx.scenebuilder.core.dock;

import java.util.Arrays;
import java.util.Collection;

import com.oracle.javafx.scenebuilder.api.ui.dock.DockContext;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockType;
import com.oracle.javafx.scenebuilder.api.ui.dock.View;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewController;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Lazy;
import com.oracle.javafx.scenebuilder.core.context.annotation.Singleton;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@Singleton
@Lazy
public class DockTypeAccordion implements DockType<TitledPane> {

    private final static String VIEW_SOURCE = "TitledPane.fxml";

    private final SbContext context;

    private ObjectProperty<DockContext<TitledPane>> focusedProperty;

    public DockTypeAccordion(SbContext context) {
        this.context = context;
    }

    @Override
    public String getNameKey() {
        return "viewtype.accordion";
    }

    @Override
    public DockContext<TitledPane> computeView(DockContext<TitledPane> viewContext) {
        View view = viewContext.getView();
        ViewAttachment viewAttachment = viewContext.getViewAttachment();

        var ctrl = context.getBean(ViewController.class);

        TitledPane titledPane = FXMLUtils.load(ctrl, DockTypeAccordion.class, VIEW_SOURCE);

        ctrl.getViewLabel().textProperty().bind(view.nameProperty());
        titledPane.setOnMouseEntered(e -> view.notifyFocused());

        if (view.getSearchController() != null) {
            ctrl.getViewSearchHost().getChildren().add(view.getSearchController().getRoot());
        }

        Node content = view.getViewController().getRoot();
        ctrl.getViewContentHost().getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        view.populateMenu(ctrl.getViewMenuButton());

        var dockContext = new DockContext<>(view, viewAttachment, ctrl, titledPane, () -> {
            ctrl.getViewLabel().textProperty().unbind();
            if (view.getSearchController() != null) {
                ctrl.getViewSearchHost().getChildren().remove(view.getSearchController().getRoot());
            }
            ctrl.getViewContentHost().getChildren().remove(view.getViewController().getRoot());
            view.clearMenu(ctrl.getViewMenuButton());
        });

        return dockContext;
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
                    if (focused != null) {
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
