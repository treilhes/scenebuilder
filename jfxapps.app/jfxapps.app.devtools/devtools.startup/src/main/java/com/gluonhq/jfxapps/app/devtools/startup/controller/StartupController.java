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
package com.gluonhq.jfxapps.app.devtools.startup.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.metrics.buffering.StartupTimeline.TimelineEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
@ViewAttachment(name = "Startup", id = "0e0234cb-a374-4251-b5a1-31bc072187bd", prefDockId = Docks.CENTER_DOCK_ID, openOnStart = false, selectOnStart = false, order = 1000, icon = "startup_tool.png", iconX2 = "startup_tool@2x.png")
public class StartupController extends AbstractFxmlViewController {

    private final ModuleLayerManager moduleLayerManager;
    private final ContextManager contextManager;
    private final Optional<ApplicationStartup> startup;

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox result;

    @FXML
    TextField filterText;

    @FXML
    Label globalSums;

    private Pattern filterPattern;
    private List<EventTree> trees;


    //@formatter:off
    protected StartupController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenu,
            ModuleLayerManager moduleLayerManager,
            ContextManager contextManager,
            Optional<ApplicationStartup> startup) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenu,
                StartupController.class.getResource("Startup.fxml"));
        this.moduleLayerManager = moduleLayerManager;
        this.contextManager = contextManager;
        this.startup = startup;
    }

    @FXML
    public void initialize() {
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(StartupController.class.getSimpleName());
    }

    public void cleanAndPopulate() {

        var createStep = startup.map(s -> s.start("startup.create.tree"));
        trees = getTrees();
        createStep.ifPresent(s -> s.end());

        var updateStep = startup.map(s -> s.start("startup.update.ui"));
        result.getChildren().clear();

        long bootAll = 0;
        long runtimeAll = 0;

        for (var tree : trees) {
            var accordion = createAccordion(tree);
            accordion.setPadding(new Insets(5));
            result.getChildren().add(accordion);

            bootAll += tree.getBootDuration();
            runtimeAll += tree.getRuntimeDuration();
        }

        String global = String.format("Boots: %s ms, Runtimes %s ms, Global: %s ms", bootAll, runtimeAll, bootAll + runtimeAll);
        globalSums.setText(global);

        updateStep.ifPresent(s -> s.end());




    }

    @Override
    public void onShow() {
        cleanAndPopulate();
    }

    @Override
    public void onHidden() {
        // TODO Auto-generated method stub

    }

    @FXML
    void applyFilter(ActionEvent event) {
        try {
            filterPattern = Pattern.compile(filterText.getText());
            cleanAndPopulate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearFilter(ActionEvent event) {
        if (filterPattern != null) {
            filterPattern = null;
            cleanAndPopulate();
        }
    }

    @FXML
    void exportToFile(ActionEvent event) {
        if (trees != null) {
            StringBuilder builder = new StringBuilder();
            trees.stream().forEach(t -> exportTree(t, builder));
            try {
                Files.writeString(Path.of("export.txt"), builder, StandardOpenOption.CREATE);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void exportTree(EventTree tree, StringBuilder builder) {
        var boot = tree.getBoot();
        if (boot != null) {
            toString(tree, boot, builder);
        }

        if (!tree.getChildrenContext().isEmpty()) {
            for (var child : tree.getChildrenContext()) {
                exportTree(child, builder);
            }
            var bootDuration = tree.getChildrenContextBootDurationn();
            var runtimeDuration = tree.getChildrenContextRuntimeDurationn();
            var title = String.format("Child contexts - Boot: %s ms - Runtime %s ms", bootDuration, runtimeDuration);
            builder.append(title).append("\n");
        }

        var runtime = tree.getRuntime();
        if (!runtime.isEmpty()) {
            for (var child : runtime) {
                toString(tree, child, builder);
            }
            var runtimeDuration = tree.getRuntimeDuration();
            var title = String.format("Runtime - %s ms", runtimeDuration);
            builder.append(title).append("\n");
        }
    }

    private Accordion createAccordion(EventTree tree) {

        var boot = tree.getBoot();
        var accordion = boot == null ? new Accordion() : toAccordion(tree, boot);

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

    private static void toString(EventTree tree, TimelineEvent event, StringBuilder builder) {

        String id = String.valueOf(event.getStartupStep().getId());
        String name = event.getStartupStep().getName();
        long duration = event.getDuration().toMillis();
        String title = String.format("%s %s - %s ms", id, name, duration);

        builder.append(title).append(" ");

        event.getStartupStep().getTags().forEach((tag) -> {
            String labelValue = String.format("%s : %s", tag.getKey(), tag.getValue());
            builder.append(labelValue);
        });
        builder.append("\n");

        // Log child events
        List<TimelineEvent> children = tree.getChildrenOf(event);
        for (TimelineEvent child : children) {
            toString(tree, child, builder);
        }

    }

    private List<EventTree> getTrees() {
        var trees = new ArrayList<EventTree>();

        var bootContext = getBootContext();
        var bootTree = eventsToEventTree(bootContext);
        trees.add(bootTree);

        var rootLayer = moduleLayerManager.get(Extension.ROOT_ID);
        var applications = listApplications(rootLayer);

        var rootContext = contextManager.get(Extension.ROOT_ID);
        var rootTree = eventsToEventTree(rootContext);

        populateChildLayers(rootLayer, rootTree, applications);

        trees.add(rootTree);

        for (UUID applicationId : applications) {
            var applicationLayer = moduleLayerManager.get(applicationId);
            var applicationContext = contextManager.get(applicationId);
            var applicationTree = eventsToEventTree(applicationContext);
            populateChildLayers(applicationLayer, applicationTree, List.of());
            trees.add(applicationTree);
        }

        var step = startup.map(s -> s.start("startup.filter.tree"));
        if (filterPattern != null) {
            var filtered = new ArrayList<EventTree>();
            for(var item:trees) {
                filtered.add(filterTree(item, filterPattern));
            }
            trees = filtered;
        }
        step.ifPresent(s -> s.end());
        return trees;
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

    private EventTree eventsToEventTree(ApplicationContext context) {

        var startup = context.getBean(BufferingApplicationStartup.class);

        StartupTimeline timeline = startup.getBufferedTimeline();
        List<TimelineEvent> events = timeline.getEvents();

        EventTree eventTree = new EventTree();
        events.forEach(eventTree::addEvent);

        return eventTree;
    }

    private EventTree filterTree(EventTree source, Pattern pattern) {
        EventTree filtered = new EventTree();

        var values = new ArrayList<>(source.idToEventMap.values());

        for (var value:values) {
            String id = String.valueOf(value.getStartupStep().getId());

            if (value != null && isFilterMatching(value.getStartupStep())) {
                var event = source.idToEventMap.remove(id);

                if (event == null) {
                    continue;
                }

                filtered.addEvent(event);

                // get nodes upper in the tree
                var current = value;
                while (current != null && current.getStartupStep().getParentId() != null) {
                    String pId = String.valueOf(current.getStartupStep().getParentId());
                    var parent = source.idToEventMap.remove(pId);

                    if (parent != null) {
                        filtered.addEvent(parent);
                    }
                    current = parent;
                }

                //get nodes lower in tree
                List<TimelineEvent> children = source.parentToChildrenMap.remove(id);
                if (children != null) {
                    moveChildren(source, filtered, children);
                }
            }
        }

        return filtered;
    }

    private void moveChildren(EventTree source, EventTree target, List<TimelineEvent> children) {
        for (var child:children) {
            String id = String.valueOf(child.getStartupStep().getId());
            var event = source.idToEventMap.remove(id);
            if (event != null) {
                target.addEvent(child);
                var subChildren = source.parentToChildrenMap.remove(id);
                if (subChildren != null) {
                    moveChildren(source, target, subChildren);
                }
            }
        }
    }

    private boolean isFilterMatching(StartupStep step) {

        if (filterPattern.matcher(step.getName()).matches()) {
            return true;
        }

        for (var tag : step.getTags()) {
            if (filterPattern.matcher(tag.getKey()).matches() || filterPattern.matcher(tag.getValue()).matches()) {
                return true;
            }
        }
        return false;
    }

    private static class EventTree {
        private static final String BOOT = "BOOT";
        private static final String ROOT = "ROOT";
        Map<String, List<TimelineEvent>> parentToChildrenMap = new HashMap<>();
        Map<String, TimelineEvent> idToEventMap = new HashMap<>();
        List<EventTree> children = new ArrayList<>();

        void addEvent(TimelineEvent event) {
            String id = String.valueOf(event.getStartupStep().getId());
            String parentId = event.getStartupStep().getParentId() != null
                    ? String.valueOf(event.getStartupStep().getParentId())
                    : (event.getStartupStep().getId() == 0 ? BOOT : ROOT);
            idToEventMap.put(id, event);
            parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(event);
        }

        void addChildContext(EventTree child) {
            children.add(child);
        }

        TimelineEvent getBoot() {
            var boot = parentToChildrenMap.getOrDefault(BOOT, new ArrayList<>());
            return boot.isEmpty() ? null : boot.get(0);
        }

        long getBootDuration() {
            return getBoot() == null ? 0 : getBoot().getDuration().toMillis();
        }

        List<TimelineEvent> getRuntime() {
            return parentToChildrenMap.getOrDefault(ROOT, new ArrayList<>());
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
