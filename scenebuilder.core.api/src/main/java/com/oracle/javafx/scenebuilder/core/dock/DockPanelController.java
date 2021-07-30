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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.DockContext;
import com.oracle.javafx.scenebuilder.api.dock.DockType;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockDockTypePreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DockPanelController implements Dock {

    private final DockManager dockManager;
    private final List<DockType<?>> dockTypes;
    private final LastDockUuidPreference lastDockUuidPreference;
    private final LastDockDockTypePreference lastDockDockTypePreference;
    private final DockViewController viewMenuController;

    @SuppressWarnings("rawtypes")
    private @Getter DockType activeDockType;
    private @Getter @Setter UUID id;
    private @Getter VBox content;

    private final List<DockContext<?>> views = new ArrayList<>();
    private SceneBuilderWindow parentWindow;
    

    // @formatter:off
    public DockPanelController(
            @Autowired DockManager dockManager, 
            @Autowired ViewManager viewManager,
            @Autowired DockViewController viewMenuController,
            @Autowired LastDockUuidPreference lastDockUuidPreference,
            @Autowired LastDockDockTypePreference lastDockDockTypePreference,
            @Autowired List<DockType<?>> dockTypes) {
     // @formatter:on

        this.id = UUID.randomUUID();
        this.dockManager = dockManager;
        this.viewMenuController = viewMenuController;
        this.lastDockUuidPreference = lastDockUuidPreference;
        this.lastDockDockTypePreference = lastDockDockTypePreference;
        this.dockTypes = dockTypes;
        this.content = new VBox();

        VBox.setVgrow(this.content, Priority.ALWAYS);

        assert dockTypes != null && !dockTypes.isEmpty();
        
        updateActiveDockType(dockTypes.get(0));
        

        viewManager.dock().filter(dr -> dr.getTarget().equals(this.getId())).observeOn(JavaFxScheduler.platform())
                .subscribe(dr -> viewAdded(dr.getSource(), dr.isSelect()));

        viewManager.undock().observeOn(JavaFxScheduler.platform()).subscribe(v -> viewDeleted(v));
    }
    
    @SuppressWarnings("rawtypes")
    private void updateActiveDockType(DockType newDockType) {
        lastDockDockTypePreference.getValue().put(this.getId(), newDockType.getNameKey());
        this.activeDockType = newDockType;
    }
    
    public void notifyDockCreated() {
        dockManager.dockCreated().onNext(this);
    }

    private List<MenuItem> createDockMenu(View view) {
        List<MenuItem> items = new ArrayList<>();

        Menu dockViewMenu = new Menu("dock view as");

        ToggleGroup dockViewAsGroup = new ToggleGroup();
        dockTypes.forEach(dt -> {
            RadioMenuItem mi = new RadioMenuItem(I18N.getStringOrDefault(dt.getNameKey(), dt.getNameKey()));
            mi.setToggleGroup(dockViewAsGroup);
            mi.setSelected(dt == activeDockType);
            mi.setOnAction((e) -> changedDockType(dt, view));
            dockViewMenu.getItems().add(mi);
        });

        items.add(dockViewMenu);

        Menu moveMenu = new Menu("Move to");

        dockManager.availableDocks().subscribe(docks -> viewMenuController.performUpdateDockMenu(view, moveMenu, docks));

        items.add(moveMenu);

        MenuItem undockMenu = new MenuItem("Undock");
        undockMenu.setOnAction((e) -> viewMenuController.performUndock(view));
        items.add(undockMenu);

        MenuItem closeMenu = new MenuItem("Close");
        closeMenu.setOnAction((e) -> viewMenuController.performCloseView(view));
        items.add(closeMenu);

        items.add(new SeparatorMenuItem());

        return items;
    }

    private void changedDockType(DockType<?> dockType, View view) {
        updateActiveDockType(dockType);

        SbPlatform.runForDocumentLater(() -> {
            updateViews();
            var dockContext = views.stream().filter(v -> v.getView() == view).findFirst().orElse(null);
            updateDockView(dockContext);
        });
    }

    private void updateViews() {
        final List<DockContext<?>> newViews = new ArrayList<>();
        views.forEach(v -> {
            v.getDisposer().dispose();
            var dockContext = activeDockType.computeView(v.getView());
            dockContext.getController().getViewMenuButton().getItems().addAll(0, createDockMenu(v.getView()));
            newViews.add(dockContext);
        });
        views.clear();
        views.addAll(newViews);
    }

    private void updateDockView(DockContext<?> focused) {
        assert activeDockType != null;
        getContent().getChildren().clear();

        if (views.isEmpty()) {
            return;
        }

        @SuppressWarnings("unchecked")
        Node dockContent = activeDockType.computeRoot(views, focused);
        VBox.setVgrow(dockContent, Priority.ALWAYS);
        getContent().getChildren().add(dockContent);
    }

    private void viewDeleted(View view) {
        assert view != null;
        assert activeDockType != null;
        assert dockTypes.size() > 0;

        DockContext<?> dockContext = views.stream().filter(dc -> dc.getView() == view).findFirst().orElse(null);
        if (dockContext != null) {
            dockContext.getDisposer().dispose();
            views.remove(dockContext);
            viewDeleted(dockContext.getView());

            SbPlatform.runForDocumentLater(() -> {
                updateDockView(null);
            });
        }
    }

    private void viewAdded(View view, boolean select) {
        assert view != null;
        assert activeDockType != null;
        assert dockTypes.size() > 0;

        lastDockUuidPreference.put(view.getId(), this.getId());
        lastDockUuidPreference.writeToJavaPreferences();

        SbPlatform.runForDocumentLater(() -> {
            var dockContext = activeDockType.computeView(view);
            dockContext.getController().getViewMenuButton().getItems().addAll(0, createDockMenu(view));
            views.add(dockContext);
            updateDockView(select ? dockContext : null);
        });
    }

    @Override
    public boolean isWindow() {
        return parentWindow != null;
    }

    @Override
    public SceneBuilderWindow getParentWindow() {
        return parentWindow;
    }

    protected void setParentWindow(SceneBuilderWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    @Override
    public Collection<View> getViews() {
        return views.stream().map(dc -> dc.getView()).collect(Collectors.toUnmodifiableList());
    }
}
