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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.DockContext;
import com.oracle.javafx.scenebuilder.api.dock.DockType;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DockPanelController implements Dock {

    private DockManager dockManager;

    @Autowired
    private ViewManager viewManager;

    @Autowired
    private List<DockType<?>> dockTypes;
    private DockType dockType;
    
    private @Getter @Setter UUID id;
    private @Getter String label;
    private @Getter VBox content;

    private final List<DockContext<?>> views = new ArrayList<>();
    
    public DockPanelController(@Autowired DockManager dockManager, @Autowired ViewManager viewManager,
            @Autowired List<DockType<?>> dockTypes) {
        this.id = UUID.randomUUID();
        this.label = "xxxx";
        this.dockManager = dockManager;
        this.viewManager = viewManager;
        this.dockTypes = dockTypes;
        this.content = new VBox();
        
        VBox.setVgrow(this.content, Priority.ALWAYS);

        assert dockTypes != null && !dockTypes.isEmpty();
        this.dockType = dockTypes.get(0);
        
        dockManager.dockCreated().onNext(this);
        
        viewManager.dock().filter(dr -> dr.getTarget().equals(this.getId())).subscribe(dr -> viewAdded(dr.getSource(), dr.isSelect()));
        viewManager.undock().subscribe(v -> viewDeleted(v));
    }
    
    
    
    private List<MenuItem> createDockMenu(View view) {
        List<MenuItem> items = new ArrayList<>();
        
        Menu dockViewMenu = new Menu("dock view as");
        
        ToggleGroup dockViewAsGroup = new ToggleGroup();
        dockTypes.forEach(dt -> {
            RadioMenuItem mi = new RadioMenuItem(I18N.getStringOrDefault(dt.getNameKey(), dt.getNameKey()));
            mi.setToggleGroup(dockViewAsGroup);
            mi.setSelected(dt == dockType);
            mi.setOnAction((e) -> changedDockType(dt, view));
            dockViewMenu.getItems().add(mi);
        });
        
        items.add(dockViewMenu);
        
        Menu undockMenu = new Menu("Undock to");
        
        MenuItem mi = new MenuItem("New window");
        mi.setOnAction((e) -> undockToNewWindow(view));
        undockMenu.getItems().add(mi);
        
        items.add(undockMenu);
        
        return items;
    }
    private void undockToNewWindow(View view) {
        
        viewManager.undock().onNext(view);
        
    }
    private void changedDockType(DockType<?> dockType, View view) {
        this.dockType = dockType;
        
        Platform.runLater(() -> {
            updateViews();
            var dockContext = views.stream().filter(v -> v.getView() == view).findFirst().orElse(null);
            updateDockView(dockContext);
        });
    }
    private void updateViews() {
        final List<DockContext<?>> newViews = new ArrayList<>();
        views.forEach(v -> {
            v.getDisposer().dispose();
            var dockContext = dockType.computeView(v.getView());
            dockContext.getController().getViewMenuButton().getItems().addAll(0, createDockMenu(v.getView()));
            newViews.add(dockContext);
        });
        views.clear();
        views.addAll(newViews);
    }
    
    private void updateDockView(DockContext<?> focused) {
        assert dockType != null;
        getContent().getChildren().clear();
        
        if (views.isEmpty()) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        Node dockContent = dockType.computeRoot(views, focused);
        VBox.setVgrow(dockContent, Priority.ALWAYS);
        getContent().getChildren().add(dockContent);
    }
    
    private void viewDeleted(View view) {
        assert view != null;
        assert dockType != null;
        assert dockTypes.size() > 0;

        DockContext<?> dockContext = views.stream().filter(dc -> dc.getView() == view).findFirst().orElse(null);
        if (dockContext != null) {
            dockContext.getDisposer().dispose();
            views.remove(dockContext);
            viewDeleted(dockContext.getView());
            
            Platform.runLater(() -> {
                updateDockView(null);
            });
        }
    }

    private void viewAdded(View view, boolean select) {
        assert view != null;
        assert dockType != null;
        assert dockTypes.size() > 0;

        Platform.runLater(() -> {
            var dockContext = dockType.computeView(view);
            dockContext.getController().getViewMenuButton().getItems().addAll(0, createDockMenu(view));
            views.add(dockContext);
            updateDockView(select ? dockContext : null);
        });
    }
}
