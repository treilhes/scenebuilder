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
package com.oracle.javafx.scenebuilder.core.loader.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.core.context.MultipleProgressListener;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.loader.ApplicationManager;
import com.oracle.javafx.scenebuilder.core.loader.BootException;
import com.oracle.javafx.scenebuilder.core.loader.ExtensionReport;
import com.oracle.javafx.scenebuilder.core.loader.OpenCommandEvent;
import com.oracle.javafx.scenebuilder.core.loader.ProgressListener;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionContentProvider;
import com.oracle.javafx.scenebuilder.core.loader.content.ExtensionValidation;
import com.oracle.javafx.scenebuilder.core.loader.extension.EditorExtension;
import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.core.loader.internal.context.ContextBootstraper;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.LayerBootstraper;
import com.oracle.javafx.scenebuilder.core.loader.internal.layer.ModuleLayerManager;
import com.oracle.javafx.scenebuilder.core.loader.model.AbstractExtension;
import com.oracle.javafx.scenebuilder.core.loader.model.Application;
import com.oracle.javafx.scenebuilder.core.loader.model.Editor;
import com.oracle.javafx.scenebuilder.core.loader.model.LoadState;
import com.oracle.javafx.scenebuilder.core.loader.model.ModelStore;

public class ApplicationManagerImpl implements ApplicationManager {

    private final static String DEFAULT_APPLICATION_FILE = "./application.json";

    private final static Logger logger = LoggerFactory.getLogger(ApplicationManagerImpl.class);

    private final ModelStore store;
    //private final ApplicationUpdater updater;
    private final ModuleLayerManager layerManager;
    private final ContextBootstraper contexts;
    private final LayerBootstraper layers;

    private Application application = null;

    private Map<UUID, ExtensionReport> state = new HashMap<>();

    public ApplicationManagerImpl(Path root) {
        super();
        this.layerManager = ModuleLayerManager.get();
        //this.updater = new ApplicationUpdaterImpl(root);
        this.contexts = new ContextBootstraper(this.layerManager);
        this.layers = new LayerBootstraper(root, this.layerManager);
        this.store = new ModelStore();
    }

    @Override
    public Application getApplication() {
        return application == null ? null : application.clone();
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public void loadApplication() {
        try (InputStream input = new FileInputStream(DEFAULT_APPLICATION_FILE)){
            loadApplication(input);
        } catch (FileNotFoundException e) {
            logger.error("Application file not found : {}", DEFAULT_APPLICATION_FILE, e);
        } catch (IOException e) {
            logger.error("Error while loading application file : {}", DEFAULT_APPLICATION_FILE, e);
        }
    }

    @Override
    public void loadApplication(InputStream jsonStream) throws IOException {
        this.application = this.store.read(jsonStream);
    }

    @Override
    public void saveApplication() {
        try {
            this.store.write(Path.of(DEFAULT_APPLICATION_FILE), getApplication());
        } catch (IOException e) {
            logger.error("Unable to save application to json file {}", DEFAULT_APPLICATION_FILE, e);
        }
    }

    @Override
    public boolean hasSavedApplication() {
        return Files.exists(Path.of(DEFAULT_APPLICATION_FILE));
    }

    @Override
    public void load() {
        load(null);
    }

    @Override
    public void load(ProgressListener progressListener) {

        logger.info("Loading root layer");

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        Layer appLayer = null;
        try {
            appLayer = layers.load(null, application, listener);
        } catch (Throwable e) {
            application.setLoadState(LoadState.Error);
            reportOf(application.getId()).error("Loading error", e);
        }
        logger.info("Loading root layer done");

        if (appLayer != null) {
            application.setLoadState(LoadState.Loaded);
            loadExtensionTree(appLayer, application.getExtensions(), listener);
            loadExtensionTree(appLayer, application.getEditors(), listener);
        }

    }

    public void loadEditor(UUID editorId) {
        loadEditor(editorId, null);
    }
    public void loadEditor(UUID editorId, ProgressListener progressListener) {

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        Layer appLayer = layers.get(application.getId());

        if (appLayer != null) {
            Optional<Editor> optionalEditor = application.getEditors().stream().filter(e -> e.getId().equals(editorId)).findAny();
            Editor editor = optionalEditor.orElseThrow();
            loadExtensionTree(appLayer, Set.of(editor), listener);
        }

    }

    private void loadExtensionTree(Layer parentLayer, Set<? extends AbstractExtension<?>> extensionSet, MultipleProgressListener progressListener) {
        extensionSet.forEach(ext -> {
            if (ext.getLoadState() != LoadState.Deleted || ext.getLoadState() != LoadState.Disabled) {
                logger.info("Loading extension layer {}", ext.getId());
                Layer layer = null;

                try {
                    layer = layers.load(parentLayer, ext, progressListener);
                } catch (Throwable e) {
                    ext.setLoadState(LoadState.Error);
                    reportOf(ext.getId()).error("", e);
                }
                logger.info("Loading extension layer {} done", ext.getId());

                if (layer != null) {
                    ext.setLoadState(LoadState.Loaded);
                    loadExtensionTree(layer, ext.getExtensions(), progressListener);
                }
            }
        });
    }


    @Override
    public void start() throws BootException {
        if (application == null) {
            loadApplication();
        }
        start(null);
    }

    @Override
    public void start(ProgressListener progressListener) throws BootException {

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

         // start app root
        if (application.getLoadState() == LoadState.Loaded && !contexts.exists(application)) {
            startExtensionTree(null, Set.of(application), listener);
        } else {
            logger.error("Application layer not loaded");
        }

        ExtensionReport rootReport = getReport(Extension.ROOT_ID);
        if (rootReport.hasError()) {
            throw new BootException("Unable to boot root extension", rootReport);
        }
    }

    @Override
    public void startEditor(UUID editorId) {
        startEditor(editorId, null);
    }

    @Override
    public void startEditor(UUID editorId, ProgressListener progressListener) {

        Optional<Editor> optionalEditor = application.getEditors().stream().filter(e -> e.getId().equals(editorId)).findAny();
        Editor editor = optionalEditor.orElseThrow();

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        if (contexts.exists(editor)) {
            logger.warn("Extension context already exists for {}", editor.getId());
            return;
        }

        SbContext parentContext = contexts.get(application);
        startExtensionTree(parentContext, Set.of(editor), listener);

    }

    private void startExtensionTree(SbContext parentContext, Set<? extends AbstractExtension<?>> extensionSet, MultipleProgressListener progressListener) {
        extensionSet.forEach(ext -> {
            try {
                List<Object> singletonInstances = List.of(this);
                SbContext extContext = contexts.create(parentContext, ext, singletonInstances, progressListener);
                startExtensionTree(extContext, ext.getExtensions(), progressListener);
            } catch (Throwable e) {
                ext.setLoadState(LoadState.Error);
                reportOf(ext.getId()).error("", e);
                System.out.println();
            }
        });

    }

    @Override
    public void stop() {
        application.getEditors().forEach(e -> stopEditor(e.getId()));
        stopExtensionTree(Set.of(application));
    }


    @Override
    public void stopEditor(UUID editorId) {

        Optional<Editor> optionalEditor = application.getEditors().stream().filter(e -> e.getId().equals(editorId)).findAny();
        Editor editor = optionalEditor.orElseThrow();

        if (!contexts.exists(editor)) {
            logger.warn("Editor context does not exists for {}", editor.getId());
            return;
        }

        stopExtensionTree(Set.of(editor));
    }

    private void stopExtensionTree(Set<? extends AbstractExtension<?>> extensionSet) {
        extensionSet.forEach(ext -> {
            contexts.close(ext);
            stopExtensionTree(ext.getExtensions());
        });

    }

    @Override
    public void unload() {
        application.getEditors().forEach(e -> unloadEditor(e.getId()));
        unloadExtensionTree(Set.of(application));
    }


    @Override
    public void unloadEditor(UUID editorId) {

        Optional<Editor> optionalEditor = application.getEditors().stream().filter(e -> e.getId().equals(editorId)).findAny();
        Editor editor = optionalEditor.orElseThrow();

        if (!contexts.exists(editor)) {
            logger.warn("Editor context does not exists for {}", editor.getId());
            return;
        }

        unloadExtensionTree(Set.of(editor));
    }

    private void unloadExtensionTree(Set<? extends AbstractExtension<?>> extensionSet) {
        extensionSet.forEach(ext -> {
            if (contexts.exists(ext)) {
                throw new ExtensionStateException("Can't unload extension, stop it first!");
            }
            unloadExtensionTree(ext.getExtensions());
            Layer layer = layers.get(ext.getId());
            if (layer != null) {
                layers.close(layer.getId());
                ext.setLoadState(LoadState.Unloaded);
            }
        });
    }

    @Override
    public void remove(UUID extensionId) {

        Map<UUID, AbstractExtension<?>> flattened = flatten(application);

        AbstractExtension<?> extension = flattened.get(extensionId);

        if (extension != null) {
            extension.setLoadState(LoadState.Deleted);
            //stopExtensionTree(Set.of(extension));
            unloadExtensionTree(Set.of(extension));
        } else {
            logger.warn("Extension not found for {}", extensionId);
        }
    }

    @Override
    public void disable(UUID extensionId) {
//        Application application = updater.getLoaded();
//        Map<UUID, AbstractExtension<?>> flattened = flatten(application);
//
//        AbstractExtension<?> extension = flattened.get(extensionId);
//
//        if (extension != null) {
//            extension.setLoadState(LoadState.Disabled);
//            stopExtensionTree(extension.getExtensions());
//            contexts.close(extension);
//            updater.update(application);
//        } else {
//            logger.warn("Extension not found for {}", extensionId);
//        }
    }

    @Override
    public void add(ExtensionContentProvider provider, ExtensionValidation validation) {
        //layerManager.

    }


    private static Map<UUID, AbstractExtension<?>> flatten(Application application) {
        Map<UUID, AbstractExtension<?>> flattened = new HashMap<>();
        flattened.put(application.getId(), application);
        flatten(flattened, application.getExtensions());
        flatten(flattened, application.getEditors());
        return flattened;
    }

    private static void flatten(Map<UUID, AbstractExtension<?>> flattened, Set<? extends AbstractExtension<?>> extensionSet) {
        extensionSet.forEach(ext -> {
            flattened.put(ext.getId(), ext);
            flatten(flattened, ext.getExtensions());
        });
    }

    @Override
    public ExtensionReport getReport(UUID id) {
        return reportOf(id);
    }


    public void logState() {
        Application application = getApplication();

        printState(application);

        application.getEditors().forEach(this::printState);
    }

    public void printState(AbstractExtension<?> ext) {

        Layer layer = layerManager.get(ext.getId());

        logger.info("{} : {}", ext.getClass().getName(), ext.getId());
        logger.info("> state : {}", ext.getLoadState());
        logger.info("> layer : {}", layer);

        if (layer != null) {
            layer.jars().forEach(j -> {
                logger.info(">> jar : {}", j.getFileName().toString());
            });
            layer.modules().forEach(m -> {
                logger.info(">> module : {}", m.get().getName());
            });
            layer.automaticModules().forEach(m -> {
                logger.info(">> auto : {}", m.get().getName());
            });
            layer.unnamedModules().forEach(m -> {
                logger.info(">> unnamed : {}", m.get().getName());
            });
        } else {
            reportOf(ext.getId()).getThrowable().ifPresent(e -> logger.error(">> error : {}", e.getMessage(), e));
        }

        SbContext ctx = contexts.get(ext);

        logger.info("> context : {}", ctx);
        if (ctx != null) {
            logger.info(">> beans : {}", ctx.getBeanDefinitionNames().length);
        } else {
            reportOf(ext.getId()).getThrowable().ifPresent(e -> logger.error(">> error : {}", e.getMessage(), e));
        }

        ext.getExtensions().forEach(this::printState);
    }

    private ExtensionReport reportOf(UUID id) {
        return state.compute(id, (i, report) -> report == null ? new ExtensionReport(i) : report);
    }

    private ExtensionReport clearedReportOf(UUID id) {
        return state.compute(id, (i, report) -> new ExtensionReport(i));
    }

    @Override
    public Set<EditorExtension> getEditors() {
        Layer appLayer = layers.get(Extension.ROOT_ID);
        return appLayer.getChildren().stream()
                .flatMap(l -> l.extensions().stream())
                .filter(EditorExtension.class::isInstance)
                .map(EditorExtension.class::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<SbContext> getContext(UUID extensionId) {
        return Optional.ofNullable(contexts.get(extensionId));
    }

    @Override
    public void send(OpenCommandEvent parameters) {
        if (parameters.getTarget() == null) {
            send(Extension.ROOT_ID, parameters);
        } else {
            send(parameters.getTarget(), parameters);
        }
    }

    private void send(UUID editorId, OpenCommandEvent parameters) {
        getContext(editorId).ifPresent(c -> c.publishEvent(parameters));
    }
}
