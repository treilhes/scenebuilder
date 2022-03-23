/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.Dock;
import com.oracle.javafx.scenebuilder.api.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.dock.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.dock.ViewAttachmentProvider;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;
import com.oracle.javafx.scenebuilder.api.subjects.ViewManager.DockRequest;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastDockUuidPreference;
import com.oracle.javafx.scenebuilder.core.dock.preferences.document.LastViewVisibilityPreference;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DockViewControllerImpl implements InitWithDocument, DockViewController {

    private final static Logger logger = LoggerFactory.getLogger(DockViewControllerImpl.class);

    private final SceneBuilderBeanFactory context;
    private final DockManager dockManager;
    private final ViewManager viewManager;
    private final LastDockUuidPreference lastDockUuidPreference;
    private final DockWindowFactory dockWindowFactory;

    private final Map<Class<? extends View>, ViewAttachment> viewItems = new HashMap<>();
    private final Map<UUID, Dock> activeDocks = new HashMap<>();
    private final Map<UUID, Dock> createdDocks = new HashMap<>();
    private final Map<SceneBuilderWindow, Boolean> activeWindows = new HashMap<>();
    private final LastViewVisibilityPreference lastViewVisibilityPreference;

    public DockViewControllerImpl(
            SceneBuilderBeanFactory context,
            DockManager dockManager,
            ViewManager viewManager,
            DockWindowFactory dockWindowFactory,
            @Autowired(required = false) List<ViewAttachmentProvider> viewProviders,
            LastViewVisibilityPreference lastViewVisibilityPreference,
            LastDockUuidPreference lastDockUuidPreference
            ) {
        this.context = context;
        this.dockManager = dockManager;
        this.viewManager = viewManager;
        this.lastDockUuidPreference = lastDockUuidPreference;
        this.lastViewVisibilityPreference = lastViewVisibilityPreference;
        this.dockWindowFactory = dockWindowFactory;

        if (viewProviders != null) {
            viewProviders.stream().flatMap(vp -> vp.views().stream()).forEach(va -> viewItems.put(va.getViewClass(), va));
        }

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
    public void initWithDocument() {
        performResetDockAndViews();
    }

    @Override
    public void performResetDockAndViews() {
        activeDocks.values().forEach(d -> d.getViews().forEach(v -> performCloseView(v)));
        viewItems.values().stream().sorted(Comparator.comparing(ViewAttachment::getOrder)).forEach( vi -> {
            if (vi.isOpenOnStart()) {
                performOpenView(vi, vi.isSelectedOnStart());
            }
        });
    }

    @Override
    public void performLoadDockAndViewsPreferences() {
        var pref = new HashMap<>(lastViewVisibilityPreference.getValue());
        List<View> views = activeDocks.values().stream().flatMap(d -> d.getViews().stream()).collect(Collectors.toList());
        views.forEach(this::performCloseView);

        if (!pref.isEmpty()) {
            viewItems.values().stream()
            .filter( vi -> Boolean.TRUE.equals(pref.get(vi.getId())))
            .forEach( vi -> {
                performOpenView(vi);
            });
        } else {
            viewItems.values().stream().sorted(Comparator.comparing(ViewAttachment::getOrder)).forEach( vi -> {
                if (vi.isOpenOnStart()) {
                    performOpenView(vi, vi.isSelectedOnStart());
                }
            });
        }
    }

    @Override
    public Collection<ViewAttachment> getViewItems() {
        return viewItems.values();
    }

    @Override
    public void performOpenView(View view) {
        ViewAttachment vi = viewItems.get(view.getClass());

        if (vi == null) {
            logger.error("Unknown view {}", view.getClass());
        } else {
            performOpenView(vi, true);
        }
    }

    @Override
    public void performOpenView(ViewAttachment vi) {
        performOpenView(vi, true);
    }
    private void performOpenView(ViewAttachment vi, boolean selectView) {

        View view = context.getBean(vi.getViewClass());

        performCloseView(view);

        view.visibleProperty().set(true);
        view.visibleProperty().addListener((ob, o , n) -> { if (n == false) this.performCloseView(view);});

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

            viewManager.dock().onNext(new DockRequest(vi, view, dwc.getDock().getId(), selectView));

            dwc.openWindow();
        } else {
            viewManager.dock().onNext(new DockRequest(vi, view, targetDock, selectView));

            if (dock.isWindow() && !activeWindows.get(dock.getParentWindow())) {
                // if the target is a inactive window, activate it if needed
                dockManager.dockShow().onNext(dock);
                dock.getParentWindow().openWindow();
            }
        }
        lastViewVisibilityPreference.put(view.getId(), Boolean.TRUE);
    }

    @Override
    public void performCloseView(View view) {
        viewManager.undock().onNext(view);
        lastViewVisibilityPreference.put(view.getId(), Boolean.FALSE);
        view.visibleProperty().set(false);
    }

    @Override
    public void performUndock(View view) {
        viewManager.undock().onNext(view);

        DockWindowController dwc = dockWindowFactory.newDockWindow();
        activeWindows.put(dwc, true);

        ViewAttachment va = viewItems.get(view.getClass());
        viewManager.dock().onNext(new DockRequest(va, view, dwc.getDock().getId(), true));

        dwc.openWindow();
    }

    @Override
    public void performDock(View view, UUID targetDockId) {
        viewManager.undock().onNext(view);
        ViewAttachment va = viewItems.get(view.getClass());
        viewManager.dock().onNext(new DockRequest(va, view, targetDockId, true));
    }

    @Override
    public Dock getDock(UUID dockId) {
        return createdDocks.get(dockId);
    }
}

