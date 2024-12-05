/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.app.devtools.startup.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;
import org.springframework.context.ApplicationContext;

import com.gluonhq.jfxapps.app.devtools.api.ui.Docks;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.loader.extension.ApplicationExtension;
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
@ViewAttachment(name = "Startup", id = "0e0234cb-a374-4251-b5a1-31bc072187bd", prefDockId = Docks.CENTER_DOCK_ID, openOnStart = false, selectOnStart = false, order = 1000, icon = "startup_tool.png", iconX2 = "startup_tool@2x.png")
public class StartupController extends AbstractFxmlViewController {

    private final ModuleLayerManager moduleLayerManager;
    private final ContextManager contextManager;

    @FXML
    ScrollPane scrollPane;

    protected StartupController(I18N i18n, ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager, ViewMenu viewMenu, ModuleLayerManager moduleLayerManager,
            ContextManager contextManager) {
        super(i18n, scenebuilderManager, documentManager, viewMenu,
                StartupController.class.getResource("Startup.fxml"));
        this.moduleLayerManager = moduleLayerManager;
        this.contextManager = contextManager;
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(StartupController.class.getSimpleName());
        // getRoot().minWidth(400.0);
        // getRoot().minHeight(400.0);
    }

    public void cleanAndPopulate() {

        var trees = getTrees();

        var vBox = new VBox();

        for (var tree : trees) {
            var accordion = createAccordion(tree);
            accordion.setPadding(new Insets(5));
            vBox.getChildren().add(accordion);
        }

        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setContent(vBox);

    }

    @Override
    public void onShow() {
        cleanAndPopulate();
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

    private Accordion createAccordion(EventTree tree) {


        var boot = tree.getBoot();
        var accordion = toAccordion(tree, boot);

        if (!tree.getChildrenContext().isEmpty()) {
            var vBox = new VBox();
            for (var child : tree.getChildrenContext()) {
                var childAccordion = createAccordion(child);
                childAccordion.setPadding(new Insets(5));
                vBox.getChildren().add(childAccordion);
            }
            var bootDuration = tree.getChildrenContextBootDurationn();
            var runtimeDuration = tree.getChildrenContextRuntimeDurationn();
            var title = String.format("Child contexts - Boot: %s ms - Runtime %s ms", bootDuration, runtimeDuration);
            accordion.getPanes().add(new TitledPane(title, vBox));
        }

        var runtime = tree.getRuntime();
        if (!runtime.isEmpty()) {
            var vBox = new VBox();
            for (var child : runtime) {
                var childAccordion = toAccordion(tree, child);
                vBox.getChildren().add(childAccordion);
            }
            var runtimeDuration = tree.getRuntimeDuration();
            var title = String.format("Runtime - %s ms", runtimeDuration);
            accordion.getPanes().add(new TitledPane(title, vBox));
        }

        return accordion;
    }

    private ApplicationContext getBootContext() {
        return contextManager.get(Extension.ROOT_ID).getParent();
    }

    private List<UUID> listApplications(Layer rootLayer) {
        var list = new ArrayList<UUID>();
        for (var layer : rootLayer.getChildren()) {
            var context = contextManager.get(layer.getId());
            var extension = context.getLocalBean(Extension.class);

            if (extension instanceof ApplicationExtension) {
                list.add(layer.getId());
            }
        }
        return list;
    }

    private static Accordion toAccordion(EventTree tree, TimelineEvent event) {

        String id = String.valueOf(event.getStartupStep().getId());
        String name = event.getStartupStep().getName();
        long duration = event.getDuration().toMillis();
        String title = String.format("%s %s - %s ms", id, name, duration);

        var accordion = new Accordion();

        VBox content = new VBox();

        var titledPane = new TitledPane(title, content);

        event.getStartupStep().getTags().forEach((tag) -> {
            String labelValue = String.format("%s : %s", tag.getKey(), tag.getValue());
            content.getChildren().add(new Label(labelValue));
        });

        // Log child events
        List<TimelineEvent> children = tree.getChildrenOf(event);
        for (TimelineEvent child : children) {
            Accordion childAccordion = toAccordion(tree, child);
            content.getChildren().add(childAccordion);
        }

        accordion.getPanes().add(titledPane);

        return accordion;
    }

    private List<EventTree> getTrees() {
        var list = new ArrayList<EventTree>();

        var bootContext = getBootContext();
        var bootTree = eventsToEventTree(bootContext);
        list.add(bootTree);

        var rootLayer = moduleLayerManager.get(Extension.ROOT_ID);
        var applications = listApplications(rootLayer);

        var rootContext = contextManager.get(Extension.ROOT_ID);
        var rootTree = eventsToEventTree(rootContext);

        populateChildLayers(rootLayer, rootTree, applications);

        list.add(rootTree);

        for (UUID applicationId : applications) {
            var applicationLayer = moduleLayerManager.get(applicationId);
            var applicationContext = contextManager.get(applicationId);
            var applicationTree = eventsToEventTree(applicationContext);
            populateChildLayers(applicationLayer, applicationTree, List.of());
            list.add(applicationTree);
        }

        return list;
    }

    private void populateChildLayers(Layer layer, EventTree tree, List<UUID> excluded) {
        for (var childLayer : layer.getChildren()) {
            if (!excluded.contains(childLayer.getId())) {
                var context = contextManager.get(childLayer.getId());
                var childTree = eventsToEventTree(context);
                populateChildLayers(childLayer, childTree, List.of());
                tree.addChildContext(childTree);
            }
        }
    }

    private static EventTree eventsToEventTree(ApplicationContext context) {

        var startup = context.getBean(BufferingApplicationStartup.class);

        StartupTimeline timeline = startup.getBufferedTimeline();
        List<TimelineEvent> events = timeline.getEvents();

        EventTree eventTree = new EventTree();
        events.forEach(eventTree::addEvent);

        return eventTree;
    }

    private static class EventTree {
        private static final String ROOT = "ROOT";
        Map<String, List<TimelineEvent>> parentToChildrenMap = new HashMap<>();
        Map<String, TimelineEvent> idToEventMap = new HashMap<>();
        List<EventTree> children = new ArrayList<>();

        void addEvent(TimelineEvent event) {
            String id = String.valueOf(event.getStartupStep().getId());
            String parentId = event.getStartupStep().getParentId() != null
                    ? String.valueOf(event.getStartupStep().getParentId())
                    : ROOT;
            idToEventMap.put(id, event);
            parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(event);
        }

        void addChildContext(EventTree child) {
            children.add(child);
        }

        TimelineEvent getBoot() {
            var roots = parentToChildrenMap.getOrDefault(ROOT, new ArrayList<>());
            return roots.get(0);
        }

        long getBootDuration() {
            return getBoot().getDuration().toMillis();
        }

        List<TimelineEvent> getRuntime() {
            var roots = parentToChildrenMap.getOrDefault(ROOT, new ArrayList<>());
            return roots.subList(1, roots.size());
        }

        long getRuntimeDuration() {
            return getRuntime().stream().map(tl -> tl.getDuration().toMillis()).reduce(0L, Long::sum);
        }

        List<EventTree> getChildrenContext() {
            return children;
        }

        long getChildrenContextBootDurationn() {
            return children.stream().map(t -> t.getBootDuration()).reduce(0L, Long::sum);
        }

        long getChildrenContextRuntimeDurationn() {
            return children.stream().map(t -> t.getRuntimeDuration()).reduce(0L, Long::sum);
        }

        List<TimelineEvent> getChildrenOf(TimelineEvent event) {
            var id = String.valueOf(event.getStartupStep().getId());
            return parentToChildrenMap.getOrDefault(id, Collections.emptyList());
        }
    }
}
