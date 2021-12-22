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
package com.oracle.javafx.scenebuilder.core.dock;

import java.util.List;

import com.oracle.javafx.scenebuilder.api.dock.DockContext;
import com.oracle.javafx.scenebuilder.api.dock.DockType;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.dock.ViewController;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class AbstractDockTypeSplit implements DockType<Node> {

    private final static String VIEW_SOURCE = "View.fxml";

    private final SceneBuilderBeanFactory context;

    private final Orientation orientation;

    public AbstractDockTypeSplit(SceneBuilderBeanFactory context, Orientation orientation) {
        this.context = context;
        this.orientation = orientation;
    }

    @Override
    public DockContext<Node> computeView(View view) {

        var ctrl = context.getBean(ViewController.class);

        Node node = FXMLUtils.load(ctrl, AbstractDockTypeSplit.class, VIEW_SOURCE);

        ctrl.getViewLabel().textProperty().bind(view.getName());

        if (view.getSearchController() != null) {
            ctrl.getViewSearchHost().getChildren().add(view.getSearchController().getRoot());
        }

        Node content = view.getViewController().getRoot();
        ctrl.getViewContentHost().getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        var menuItems = view.getViewMenus().getMenuItems();
        if (menuItems != null && menuItems.size() > 0) {
            ctrl.getViewMenuButton().getItems().addAll(menuItems);
        }

        var dockContext = new DockContext<>(view, ctrl, node, () -> {
            ctrl.getViewLabel().textProperty().unbind();
            if (view.getSearchController() != null) {
                ctrl.getViewSearchHost().getChildren().remove(view.getSearchController().getRoot());
            }
            ctrl.getViewContentHost().getChildren().remove(view.getViewController().getRoot());
            if (menuItems != null && menuItems.size() > 0) {
                ctrl.getViewMenuButton().getItems().removeAll(menuItems);
            }
        });

        return dockContext;
    }

    @Override
    public Node computeRoot(List<DockContext<Node>> views, DockContext<Node> focused) {
        SplitPane sPane = new SplitPane();
        sPane.setOrientation(orientation);

        double coef = 1.0 / views.size();

        for (int i = 0; i < views.size(); i++) {
            var v = views.get(i);
            sPane.setDividerPosition(i, coef * (i + 1));
            sPane.getItems().add(v.getDockContent());
        }

        return sPane;
    }
}
