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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager.DockRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastViewVisibilityPreference;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import lombok.Getter;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DockViewController implements InitWithDocument {

    private final ApplicationContext context;
    private final DockManager dockManager;
    private final ViewManager viewManager;
    private final LastDockUuidPreference lastDockUuidPreference;
    private final DockWindowFactory dockWindowFactory;
    private final DockNameHelper dockNameHelper;
    
    
    private final Map<Class<? extends View>, ViewItem> viewItems = new HashMap<>();
    private final Map<UUID, Dock> activeDocks = new HashMap<>();
    private final Map<UUID, Dock> createdDocks = new HashMap<>();
    private final Map<SceneBuilderWindow, Boolean> activeWindows = new HashMap<>();
    private final LastViewVisibilityPreference lastViewVisibilityPreference;
    
    public DockViewController(
            @Autowired ApplicationContext context,
            @Autowired DockManager dockManager,
            @Autowired ViewManager viewManager,
            @Autowired DockWindowFactory dockWindowFactory,
            @Autowired DockNameHelper dockNameHelper,
            @Autowired LastViewVisibilityPreference lastViewVisibilityPreference,
            @Autowired LastDockUuidPreference lastDockUuidPreference
            ) {
        this.context = context;
        this.dockManager = dockManager;
        this.viewManager = viewManager;
        this.lastDockUuidPreference = lastDockUuidPreference;
        this.lastViewVisibilityPreference = lastViewVisibilityPreference;
        this.dockWindowFactory = dockWindowFactory;
        this.dockNameHelper = dockNameHelper;

        Arrays.stream(context.getBeanNamesForType(View.class))
                .map(name -> context.getType(name))
                .map(cls -> new ViewItem((Class<? extends View>)cls))
                .forEach(vi -> viewItems.put(vi.getViewClass(), vi));
        
        dockManager.dockCreated().subscribe(d -> {
            activeDocks.put(d.getId(), d);
            createdDocks.put(d.getId(), d);
            
            if (d.isWindow()) {
                activeWindows.put(d.getParentWindow(), true);
            }
            dockManager.availableDocks().onNext(Collections.unmodifiableCollection(activeDocks.values()));
        });
        
        dockManager.dockHide().subscribe(d -> {
            activeDocks.remove(d.getId());
            d.getViews().forEach(v -> performCloseView(v));
            if (d.isWindow()) {
                activeWindows.put(d.getParentWindow(), false);
            }
            dockManager.availableDocks().onNext(Collections.unmodifiableCollection(activeDocks.values()));
        });
        
        dockManager.dockShow().subscribe(d -> {
            activeDocks.put(d.getId(), d);
            if (d.isWindow()) {
                activeWindows.put(d.getParentWindow(), true);
            }
            dockManager.availableDocks().onNext(Collections.unmodifiableCollection(activeDocks.values()));
        });
    }
    
    @Override
    public void init() {
        performResetDockAndViews();
    }
    
    public void performResetDockAndViews() {
        activeDocks.values().forEach(d -> d.getViews().forEach(v -> performCloseView(v)));
        viewItems.values().stream().sorted(Comparator.comparing(ViewItem::getOrder)).forEach( vi -> {
            if (vi.openOnStart) {
                performOpenView(vi, vi.isSelectOnStart());
            }
        });
    }
    
    public void performLoadDockAndViewsPreferences() {
        var pref = new HashMap<>(lastViewVisibilityPreference.getValue());
        activeDocks.values().forEach(d -> d.getViews().forEach(v -> performCloseView(v)));
        if (!pref.isEmpty()) {
            viewItems.values().stream()
            .filter( vi -> Boolean.TRUE.equals(pref.get(vi.getViewId())))
            .forEach( vi -> {
                performOpenView(vi);
            });
        } else {
            viewItems.values().stream().sorted(Comparator.comparing(ViewItem::getOrder)).forEach( vi -> {
                if (vi.openOnStart) {
                    performOpenView(vi, vi.isSelectOnStart());
                }
            });
        }
    }

    protected Collection<ViewItem> getViewItems() {
        return viewItems.values();
    }

    public void performOpenView(ViewItem vi) {
        performOpenView(vi, true);
    }
    private void performOpenView(ViewItem vi, boolean selectView) {
        
        View view = context.getBean(vi.getViewClass());
        
        performCloseView(view);
        
        // get last saved dock target
        UUID targetDock = lastDockUuidPreference.get(view.getId());
        
        if (targetDock == null) {// no preference so use the default for view
            targetDock = vi.getPrefDockId();
        }
        
        if (targetDock == null) {// still nothing so target a new window
            performUndock(view);
            return;
        }
        
        Dock dock = createdDocks.get(targetDock);
        final UUID checkTargetDock = targetDock;
        
        if (dock == null) { 
            // the dock is not a default one, so we create a window and update
            // all other views using the same dockid to the new dockid
            DockWindowController dwc = dockWindowFactory.newDockWindow();
            lastDockUuidPreference.getValue().replaceAll((k,v) -> v.equals(checkTargetDock) ? dwc.getDock().getId() : v);
            
            viewManager.dock().onNext(new DockRequest(view, dwc.getDock().getId(), selectView));
            
            dwc.openWindow();
        } else {
            viewManager.dock().onNext(new DockRequest(view, targetDock, selectView));
            
            if (dock.isWindow() && !activeWindows.get(dock.getParentWindow())) {
                // if the target is a inactive window, activate it if needed
                dockManager.dockShow().onNext(dock);
                dock.getParentWindow().openWindow();
            }
        }
        lastViewVisibilityPreference.put(view.getId(), Boolean.TRUE);
    }
    
    public void performCloseView(View view) {
        viewManager.undock().onNext(view);
        lastViewVisibilityPreference.put(view.getId(), Boolean.FALSE);
    }
    
    public void performUndock(View view) {
        viewManager.undock().onNext(view);
        
        DockWindowController dwc = dockWindowFactory.newDockWindow();
        activeWindows.put(dwc, true);
        
        viewManager.dock().onNext(new DockRequest(view, dwc.getDock().getId(), true));
        
        dwc.openWindow();
    }

    public void performUpdateDockMenu(View view, Menu moveMenu, Collection<Dock> dockList) {
        moveMenu.getItems().clear();
        dockList.forEach(dock ->{
            MenuItem mvi = new MenuItem(dockNameHelper.getName(dock.getId()));
            mvi.setOnAction((e) -> {
                viewManager.undock().onNext(view);
                viewManager.dock().onNext(new DockRequest(view, dock.getId(), true));
            });
            moveMenu.getItems().add(mvi);
        });
    }
    
    protected static class ViewItem {
        
        private final @Getter Class<? extends View> viewClass;
        private final @Getter String viewName;
        private final @Getter UUID viewId;
        private final @Getter UUID prefDockId;
        private final @Getter boolean openOnStart;
        private final @Getter boolean selectOnStart;
        private final @Getter int order;
        
        public ViewItem(Class<? extends View> viewClass) {
            super();
            this.viewClass = viewClass;
            this.viewId = View.getId(viewClass);
            this.viewName = View.getViewName(viewClass);
            this.prefDockId = View.getPrefDockId(viewClass);
            this.selectOnStart = View.isSelectOnStart(viewClass);
            this.openOnStart = View.isOpenOnStart(viewClass);
            this.order = View.getOrder(viewClass);
        }
        
        
    }

}

