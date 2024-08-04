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
package com.gluonhq.jfxapps.core.library.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.library.Library;
import com.gluonhq.jfxapps.core.api.library.LibraryArtifact;
import com.gluonhq.jfxapps.core.api.library.LibraryItem;
import com.gluonhq.jfxapps.core.api.library.Report;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithApplication;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithDocument;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.fs.controller.ClassLoaderController;
import com.gluonhq.jfxapps.core.library.manager.ImportProgressDialogController;
import com.gluonhq.jfxapps.core.library.manager.LibraryDialogController;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;

/**
 *
 *
 */
public abstract class AbstractLibrary<R extends Report, I extends LibraryItem>
        implements Library<R, I>, InitWithApplication, InitWithDocument {

    private final static Logger logger = LoggerFactory.getLogger(AbstractLibrary.class);

    private static final List<String> JAVAFX_MODULES = Arrays.asList("javafx-base", "javafx-graphics",
            "javafx-controls", "javafx-fxml", "javafx-media", "javafx-web", "javafx-swing");

    protected final ObservableList<I> itemsProperty = FXCollections.observableArrayList();
    private final SimpleIntegerProperty explorationCountProperty = new SimpleIntegerProperty();
    private final SimpleObjectProperty<LocalDateTime> explorationDateProperty = new SimpleObjectProperty<>();
    private final ReadOnlyBooleanWrapper firstExplorationCompleted = new ReadOnlyBooleanWrapper(false);
    private SimpleBooleanProperty exploring = new SimpleBooleanProperty();

    private final ObservableList<R> reports = FXCollections.observableArrayList();

    private final LibraryStore store;

    private final NavigableMap<LocalDateTime, Exploration<R>> explorations = new TreeMap<>();

    private final SceneBuilderManager sceneBuilderManager;

    private final ClassLoaderController classLoaderController;

    private final JfxAppContext context;

    private final LibraryStoreConfiguration dialogConfiguration;

    /*
     * Public
     */

    public AbstractLibrary(JfxAppContext context, SceneBuilderManager sceneBuilderManager,
            ClassLoaderController classLoaderController, LibraryStore store,
            LibraryStoreConfiguration dialogConfiguration) {
        this.context = context;
        this.sceneBuilderManager = sceneBuilderManager;
        this.store = store;
        this.classLoaderController = classLoaderController;
        this.dialogConfiguration = dialogConfiguration;
    }

    @Override
    public void init() {
        try {
            if (!store.isReady()) {
                store.create();
            }
            if (store.isReady()) {
                store.load();
                store.startWatching();
            }

            List<Path> sources = store.getArtifacts().stream().flatMap(ma -> ma.getJarList().stream())
                    .collect(Collectors.toList());
            sources.addAll(store.getFilesOrFolders());
            sources.add(store.getFilesFolder());
            classLoaderController.getJarsOrFolders().addAll(sources);
            classLoaderController.updateClassLoader();
        } catch (IOException e) {
            logger.error("Unable to start library {}", this.getClass().getName(), e);
        }
    }

    @Override
    public void initWithDocument() {
        if (!isFirstExplorationCompleted()) {
            store.onStoreUpdated((store) -> exploreStore());

            explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> {
                Exploration<R> current = explorations.get(getExplorationDate());

                Map.Entry<LocalDateTime, Exploration<R>> entry = explorations.lowerEntry(current.getLocalDateTime());
                Exploration<R> previous = entry == null
                        ? new Exploration<R>(LocalDateTime.now(), Collections.emptyList(), null)
                        : entry.getValue();
                userLibraryExplorationDidChange(previous, current);
            });
        }
    }

    public abstract String getLibraryId();

    public abstract Explorer<LibraryArtifact, R> newArtifactExplorer();

    public abstract Explorer<Path, R> newFolderExplorer();

    public abstract Explorer<Path, R> newFileExplorer();

    public abstract List<R> createApplyAndSaveFilter(List<R> reports);

    public abstract List<R> applySavedFilter(List<R> reports);

    protected abstract Collection<I> makeLibraryItems(R reports) throws IOException;

    protected abstract void updateItems(Collection<I> items);

    protected abstract void userLibraryExplorationDidChange(Exploration<R> previous, Exploration<R> current);

    public abstract void unlock(List<Path> pathes);

    public abstract void lock(List<Path> pathes);

    private void exploreStore() {
        final List<LibraryArtifact> artifacts = new ArrayList<>(store.getArtifacts());
        final List<Path> fileOrFolders = new ArrayList<>(store.getFilesOrFolders());

        Stream<Task<List<R>>> artifactStream = artifacts.stream()
                .filter(ma -> JAVAFX_MODULES.stream().noneMatch(ma.getArtifactId()::startsWith))
                .map(ma -> newArtifactExplorer().explore(ma));

        Stream<Task<List<R>>> pathStream = fileOrFolders.stream()
                .filter(p -> JAVAFX_MODULES.stream().noneMatch(m -> p.getFileName().toString().startsWith(m)))
                .map(p -> {
                    if (Files.isDirectory(p)) {
                        return newFolderExplorer().explore(p);
                    } else {
                        return newFileExplorer().explore(p);
                    }
                });

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // go back to fx thread for ui update
        JfxAppPlatform.runOnFxThread(() -> {

            setExploring(true);

            List<Task<List<R>>> tasks = Stream.concat(artifactStream, pathStream).peek(t -> executor.execute(t))
                    .collect(Collectors.toList());
            executor.shutdown();

            Exploration<R> exploration = new Exploration<>(LocalDateTime.now(), tasks, this::updateLibrary);
            explorations.put(exploration.getLocalDateTime(), exploration);

        });
    }

    private void updateLibrary(Exploration<R> explorationResult) {

        // 1) we create a classloader
        // 2) we explore all the jars and folders
        // 3) we construct a list of library items
        // 4) we update the user library with the class loader and items
        // 5) on startup only, we allow opening files that may/may not rely on the user
        // library

        // 1)

        // 2)

        final List<I> newItems = new ArrayList<>();

        List<Path> sources = explorationResult.getReports().stream().map(r -> r.getSource())
                .collect(Collectors.toList());
        sources.add(store.getFilesFolder());

        List<R> reports = applySavedFilter(explorationResult.getReports());

        try {
            classLoaderController.getJarsOrFolders().addAll(sources);
            classLoaderController.updateClassLoader();

            resetBeforeUpdate();

            for (R report : reports) {
                newItems.addAll(makeLibraryItems(report));
            }

            // Remove duplicated items
            updateItems(newItems.stream().distinct().collect(Collectors.toList()));

            getStore().saveConfiguration();
        } catch (IOException e) {
            logger.error("Unable to update the control library", e);
        } finally {
            // 5
            updateReports(new ArrayList<>(explorationResult.getReports()));
            // getOnFinishedUpdatingJarReports().accept(jarOrFolderReports);
            updateExplorationCount();
            updateExplorationDate(explorationResult.getLocalDateTime());
            // Fix for #45: mark end of first exploration
            updateFirstExplorationCompleted();
            setExploring(false);
        }
    }

    @Override
    public ObservableList<I> getItems() {
        return itemsProperty;
    }

//    //TODO move elsewhere
//    @Override
//    public ClassLoader getClassLoader() {
//        return sceneBuilderManager.classloader().get();
//    }

    @Override
    public File getPath() {
        return store.getRoot().toFile();
    }

    public int getExplorationCount() {
        return explorationCountProperty.get();
    }

    public ReadOnlyIntegerProperty explorationCountProperty() {
        return explorationCountProperty;
    }

    @Override
    public LocalDateTime getExplorationDate() {
        return explorationDateProperty.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> explorationDateProperty() {
        return explorationDateProperty;
    }

    void updateReports(Collection<R> newReports) {
        reports.setAll(newReports);
    }

    @Override
    public void setOnUpdatedJarReports(Consumer<List<R>> onFinishedUpdatingJarReports) {
        if (this.explorations.size() > 0) {
            onFinishedUpdatingJarReports.accept(this.reports);
        } else {
            this.reports.addListener((ListChangeListener<R>) (c -> {
                while (c.next()) {
                    onFinishedUpdatingJarReports.accept((List<R>) c.getAddedSubList());
                }
            }));
        }
    }

    @Override
    public final ReadOnlyBooleanProperty firstExplorationCompletedProperty() {
        return firstExplorationCompleted.getReadOnlyProperty();
    }

    public final boolean isFirstExplorationCompleted() {
        return firstExplorationCompleted.get();
    }

    @Override
    public SimpleBooleanProperty exploringProperty() {
        return exploring;
    }

    public boolean isExploring() {
        return exploringProperty().get();
    }

    public void setExploring(boolean value) {
        if (Platform.isFxApplicationThread())
            exploringProperty().set(value);
        else
            JfxAppPlatform.runOnFxThread(() -> setExploring(value));
    }

    protected void setItems(Collection<I> items) {
        if (Platform.isFxApplicationThread()) {
            itemsProperty.setAll(items);
        } else {
            JfxAppPlatform.runOnFxThread(() -> {
                itemsProperty.setAll(items);
            });
        }
    }

    void addItems(Collection<I> items) {
        if (Platform.isFxApplicationThread()) {
            itemsProperty.addAll(items);
        } else {
            JfxAppPlatform.runOnFxThread(() -> itemsProperty.addAll(items));
        }
    }

    void updateExplorationCount() {
        if (Platform.isFxApplicationThread()) {
            explorationCountProperty.add(1);
        } else {
            JfxAppPlatform.runOnFxThread(() -> explorationCountProperty.add(1));
        }
    }

    void updateExplorationDate(LocalDateTime date) {
        if (Platform.isFxApplicationThread()) {
            explorationDateProperty.set(date);
        } else {
            JfxAppPlatform.runOnFxThread(() -> explorationDateProperty.set(date));
        }
    }

    void updateFirstExplorationCompleted() {
        if (Platform.isFxApplicationThread()) {
            firstExplorationCompleted.set(true);
        } else {
            JfxAppPlatform.runOnFxThread(() -> firstExplorationCompleted.set(true));
        }
    }

    public void stopWatching() {
        store.stopWatching();
    }

    public void startWatching() {
        store.startWatching();
    }

    public LibraryStore getStore() {
        return store;
    }

    @Override
    public ObservableList<R> getReports() {
        return reports;
    }

    public InstanceWindow openDialog() {
        LibraryDialogController libraryDialogController = context.getBean(LibraryDialogController.class);
        libraryDialogController.initForLibrary(this);
        libraryDialogController.openWindow();
        return libraryDialogController;
    }

    public LibraryStoreConfiguration getDialogConfiguration() {
        return dialogConfiguration;
    }

    public class Exploration<RE> {

        private final List<Task<List<RE>>> tasks;
        private final AtomicInteger completed = new AtomicInteger();

        private final ObservableList<RE> reports = FXCollections.observableArrayList();
        private final LocalDateTime localDate;
        private final Consumer<Exploration<RE>> updateLibraryCallback;

        public Exploration(LocalDateTime localDate, List<Task<List<RE>>> tasks,
                Consumer<Exploration<RE>> updateLibraryCallback) {
            super();
            this.localDate = localDate;
            this.tasks = tasks;
            this.updateLibraryCallback = updateLibraryCallback;
            listenTasks();
        }

        private void listenTasks() {
            if (tasks.isEmpty()) {
                requestLibraryUpdate();
            }
            tasks.forEach(t -> {
                t.setOnCancelled((wse) -> onTaskEnded());
                t.setOnFailed((wse) -> onTaskEnded());
                t.setOnSucceeded((wse) -> onTaskEnded());

                if (t.isDone()) {
                    onTaskEnded();
                }
            });
        }

        private void onTaskEnded() {
            int count = completed.incrementAndGet();

            if (count == tasks.size()) {
                tasks.stream().filter(t -> t.getState() == State.SUCCEEDED).forEach(t -> reports.addAll(t.getValue()));
                requestLibraryUpdate();
            }
        }

        protected void requestLibraryUpdate() {
            updateLibraryCallback.accept(this);
        }

        public ObservableList<RE> getReports() {
            return reports;
        }

        protected LocalDateTime getLocalDateTime() {
            return localDate;
        }

        @Deprecated
        protected ObservableList<R> getJarReports() {
            return null;
        }

        @Deprecated
        protected ObservableList<Path> getFxmlFileReports() {
            return null;
        }
    }

    public void performAddFilesOrFolders(List<Path> listFilesOrFolders) {
        List<Task<List<R>>> taskList = listFilesOrFolders.stream()
                .filter(p -> JAVAFX_MODULES.stream().noneMatch(m -> p.getFileName().toString().startsWith(m)))
                .map(p -> {
                    if (Files.isRegularFile(p)) {
                        return newFileExplorer().explore(p);
                    } else {
                        return newFolderExplorer().explore(p);
                    }
                }).filter(t -> t != null).collect(Collectors.toList());

        List<Path> sources = processTaskListToAdd(taskList);

        if (sources != null) {
            doThenReLoad(() -> getStore().addAll(sources));
        }
    }

    public boolean performAddArtifact(LibraryArtifact artifact) {

        List<Task<List<R>>> taskList = List.of(artifact).stream()
                .filter(ma -> JAVAFX_MODULES.stream().noneMatch(ma.getArtifactId()::startsWith))
                .map(ma -> newArtifactExplorer().explore(ma)).filter(t -> t != null).collect(Collectors.toList());

        List<Path> sources = processTaskListToAdd(taskList);

        if (sources != null && sources.size() > 0) {
            doThenReLoad(() -> getStore().add(artifact));
            return true;
        }
        return false;
    }

    public void performRemoveFilesOrFolders(List<Path> listFilesOrFolders) {
        unlock(listFilesOrFolders);
        doThenReLoad(() -> listFilesOrFolders.forEach(p -> getStore().remove(p)));
        lock(listFilesOrFolders);
    }

    public void performRemoveArtifact(LibraryArtifact artifact) {
        List<Path> files = artifact.getJarList();
        unlock(files);
        doThenReLoad(() -> getStore().remove(artifact));
        lock(files);
    }

    public void performEditFilesOrFolders(List<Path> listFilesOrFolders) {
        performAddFilesOrFolders(listFilesOrFolders);
    }

    public void performEditArtifact(LibraryArtifact artifact) {
        performAddArtifact(artifact);
    }

    private List<Path> processTaskListToAdd(List<Task<List<R>>> taskList) {
        ImportProgressDialogController progressDialog = context.getBean(ImportProgressDialogController.class);
        // progressDialog.getStage().initOwner(owner);
        progressDialog.execute(taskList);
        progressDialog.showAndWait();

        List<R> results = taskList.stream().filter(t -> t.isDone()).flatMap(t -> t.getValue().stream())
                .collect(Collectors.toList());

        results = createApplyAndSaveFilter(results);

        if (results == null) { // import cancelled
            return null;
        }

        List<Path> sources = results.stream().map(r -> r.getSource()).distinct().collect(Collectors.toList());
        return sources;
    }

    private void doThenReLoad(Runnable runnable) {
        try {
            getStore().stopWatching();
            runnable.run();
        } finally {
            getStore().startWatching();
            try {
                getStore().load();
            } catch (IOException e) {
                logger.error("Unable to reload store", e);
            }
        }
    }

    protected void resetBeforeUpdate() {
        // TODO Auto-generated method stub

    }

}
