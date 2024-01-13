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
package com.gluonhq.jfxapps.boot.loader.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.layer.Layer;
import com.gluonhq.jfxapps.boot.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.loader.BootException;
import com.gluonhq.jfxapps.boot.loader.ExtensionReport;
import com.gluonhq.jfxapps.boot.loader.OpenCommandEvent;
import com.gluonhq.jfxapps.boot.loader.ProgressListener;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper;
import com.gluonhq.jfxapps.boot.loader.internal.layer.LayerBootstraper;
import com.gluonhq.jfxapps.boot.loader.model.AbstractExtension;
import com.gluonhq.jfxapps.boot.loader.model.Application;
import com.gluonhq.jfxapps.boot.loader.model.JfxApps;
import com.gluonhq.jfxapps.boot.loader.model.LoadState;

import jakarta.annotation.PostConstruct;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationManagerImpl.
 */
@Component
public class ApplicationManagerImpl implements ApplicationManager {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(ApplicationManagerImpl.class);

    /** The layer manager. */
    private final ModuleLayerManager layerManager;

    /** The contexts. */
    private final ContextBootstraper contexts;

    /** The layers. */
    private final LayerBootstraper layers;

    /** The application. */
    private JfxApps appContainer = null;

    /** The state. */
    private Map<UUID, ExtensionReport> state = new HashMap<>();

    private final StateProvider stateProvider;

    private boolean started = false;

    /**
     * Instantiates a new application manager impl.
     *
     * @param layerManager
     * @param contexts
     * @param layers
     */
    protected ApplicationManagerImpl(ModuleLayerManager layerManager, ContextBootstraper contexts,
            LayerBootstraper layers, StateProvider stateProvider) {
        super();
        this.layerManager = layerManager;
        this.contexts = contexts;
        this.layers = layers;
        this.stateProvider = stateProvider;
    }

    @PostConstruct
    public void init() {
        appContainer = stateProvider.bootState();
    }

    /**
     * Gets the application state.
     *
     * @return the application state
     */
    @Override
    public JfxApps getState() {
        return appContainer == null ? null : appContainer.clone();
    }

    @Override
    public void start() throws BootException {
        if (!isStarted()) {
            load(null);
            start(null);
            started = true;
        } else {
            logger.warn("JfxApps already started bypassing start");
        }
    }

    /**
     * Start editor.
     *
     * @param editorId the editor id
     */
    @Override
    public void startApplication(UUID editorId) {
        loadApplication(editorId, null);
        startApplication(editorId, null);
    }

    /**
     * Stop.
     */
    @Override
    public void stop() {
        appContainer.getApplications().forEach(e -> stopApplication(e.getId()));
        stopExtensionTree(Set.of(appContainer));
        unload();
    }

    /**
     * Stop editor.
     *
     * @param editorId the editor id
     */
    @Override
    public void stopApplication(UUID editorId) {

        Optional<Application> optionalEditor = appContainer.getApplications().stream()
                .filter(e -> e.getId().equals(editorId)).findAny();
        Application editor = optionalEditor.orElseThrow();

        if (!contexts.exists(editor)) {
            logger.warn("Editor context does not exists for {}", editor.getId());
            return;
        }

        stopExtensionTree(Set.of(editor));
        unloadApplication(editorId);
    }

    /**
     * Load.
     *
     * @param progressListener the progress listener
     */
    private void load(ProgressListener progressListener) {

        logger.info("Loading root layer");

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        Layer appLayer = null;
        try {
            appLayer = layers.load(null, appContainer, listener);
        } catch (Throwable e) {
            logger.error("Unable to load layer", e);
            appContainer.setLoadState(LoadState.Error);
            reportOf(appContainer.getId()).error("Loading error", e);
        }
        logger.info("Loading root layer done");

        if (appLayer != null) {
            appContainer.setLoadState(LoadState.Loaded);
            loadExtensionTree(appLayer, appContainer.getExtensions(), listener);

            // applications musn't be loaded automaticaly, but it is convenient for now
            // loadExtensionTree(appLayer, appContainer.getApplications(), listener);
        }

    }

    /**
     * Load editor.
     *
     * @param editorId         the editor id
     * @param progressListener the progress listener
     */
    public void loadApplication(UUID editorId, ProgressListener progressListener) {

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        Layer appLayer = layers.get(appContainer.getId());

        if (appLayer != null) {
            Optional<Application> optionalEditor = appContainer.getApplications().stream()
                    .filter(e -> e.getId().equals(editorId)).findAny();
            Application editor = optionalEditor.orElseThrow();
            loadExtensionTree(appLayer, Set.of(editor), listener);
        }

    }

    /**
     * Load extension tree.
     *
     * @param parentLayer      the parent layer
     * @param extensionSet     the extension set
     * @param progressListener the progress listener
     */
    private void loadExtensionTree(Layer parentLayer, Set<? extends AbstractExtension<?>> extensionSet,
            MultipleProgressListener progressListener) {
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

    /**
     * Start.
     *
     * @param progressListener the progress listener
     * @throws BootException the boot exception
     */
    private void start(ProgressListener progressListener) throws BootException {

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        // start app root
        if (appContainer.getLoadState() == LoadState.Loaded && !contexts.exists(appContainer)) {
            startExtensionTree(null, Set.of(appContainer), listener);
        } else {
            logger.error("Application layer not loaded");
        }

        ExtensionReport rootReport = getReport(Extension.ROOT_ID);
        if (rootReport.hasError()) {
            throw new BootException("Unable to boot root extension", rootReport);
        }
    }

    /**
     * Start editor.
     *
     * @param applicationId    the application id
     * @param progressListener the progress listener
     */
    public void startApplication(UUID applicationId, ProgressListener progressListener) {

        Optional<Application> optionalApplication = appContainer.getApplications().stream()
                .filter(e -> e.getId().equals(applicationId)).findAny();
        Application app = optionalApplication.orElseThrow();

        MultipleProgressListener listener = new MultipleProgressListener(progressListener);

        if (contexts.exists(app)) {
            logger.warn("Extension context already exists for {}", app.getId());
            return;
        }

        SbContext parentContext = contexts.get(appContainer);
        startExtensionTree(parentContext, Set.of(app), listener);

    }

    /**
     * Start extension tree.
     *
     * @param parentContext    the parent context
     * @param extensionSet     the extension set
     * @param progressListener the progress listener
     */
    private void startExtensionTree(SbContext parentContext, Set<? extends AbstractExtension<?>> extensionSet,
            MultipleProgressListener progressListener) {
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

    /**
     * Stop extension tree.
     *
     * @param extensionSet the extension set
     */
    private void stopExtensionTree(Set<? extends AbstractExtension<?>> extensionSet) {
        extensionSet.forEach(ext -> {
            contexts.close(ext);
            stopExtensionTree(ext.getExtensions());
        });

    }

    /**
     * Unload.
     */
    public void unload() {
        appContainer.getApplications().forEach(e -> unloadApplication(e.getId()));
        unloadExtensionTree(Set.of(appContainer));
    }

    /**
     * Unload editor.
     *
     * @param editorId the editor id
     */
    public void unloadApplication(UUID editorId) {

        Optional<Application> optionalEditor = appContainer.getApplications().stream()
                .filter(e -> e.getId().equals(editorId)).findAny();
        Application editor = optionalEditor.orElseThrow();

        if (!contexts.exists(editor)) {
            logger.warn("Editor context does not exists for {}", editor.getId());
            return;
        }

        unloadExtensionTree(Set.of(editor));
    }

    /**
     * Unload extension tree.
     *
     * @param extensionSet the extension set
     */
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

    /**
     * Removes the.
     *
     * @param extensionId the extension id
     */
    private void remove(UUID extensionId) {

        Map<UUID, AbstractExtension<?>> flattened = flatten(appContainer);

        AbstractExtension<?> extension = flattened.get(extensionId);

        if (extension != null) {
            extension.setLoadState(LoadState.Deleted);
            // stopExtensionTree(Set.of(extension));
            unloadExtensionTree(Set.of(extension));
        } else {
            logger.warn("Extension not found for {}", extensionId);
        }
    }

    /**
     * Disable.
     *
     * @param extensionId the extension id
     */
    private void disable(UUID extensionId) {
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

    /**
     * Flatten.
     *
     * @param application the application
     * @return the map
     */
    private static Map<UUID, AbstractExtension<?>> flatten(JfxApps application) {
        Map<UUID, AbstractExtension<?>> flattened = new HashMap<>();
        flattened.put(application.getId(), application);
        flatten(flattened, application.getExtensions());
        flatten(flattened, application.getApplications());
        return flattened;
    }

    /**
     * Flatten.
     *
     * @param flattened    the flattened
     * @param extensionSet the extension set
     */
    private static void flatten(Map<UUID, AbstractExtension<?>> flattened,
            Set<? extends AbstractExtension<?>> extensionSet) {
        extensionSet.forEach(ext -> {
            flattened.put(ext.getId(), ext);
            flatten(flattened, ext.getExtensions());
        });
    }

    /**
     * Gets the report.
     *
     * @param id the id
     * @return the report
     */
    @Override
    public ExtensionReport getReport(UUID id) {
        return reportOf(id);
    }

    /**
     * Log state.
     */
    public void logState() {
        JfxApps application = getState();

        printState(application);

        application.getApplications().forEach(this::printState);
    }

    /**
     * Prints the state.
     *
     * @param ext the ext
     */
    public void printState(AbstractExtension<?> ext) {

        Layer layer = layerManager.get(ext.getId());

        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s : %s", ext.getClass().getName(), ext.getId())).append("\n");
        builder.append(String.format("> state : %s", ext.getLoadState())).append("\n");
        builder.append(String.format("> layer : %s", layer)).append("\n");

        if (layer != null) {
            layer.jars().forEach(j -> {
                builder.append(String.format(">> jar : %s", j.getFileName())).append("\n");;
            });
            layer.modules().forEach(m -> {
                builder.append(String.format(">> module : %s", m.get().getName())).append("\n");;
            });
            layer.automaticModules().forEach(m -> {
                builder.append(String.format(">> auto : %s", m.get().getName())).append("\n");;
            });
            layer.unnamedModules().forEach(m -> {
                builder.append(String.format(">> unnamed : %s", m.get().getName())).append("\n");;
            });
        } else {
            reportOf(ext.getId()).getThrowable().ifPresent(e -> builder.append(String.format(">> error : %s", e.getMessage(), e)).append("\n"));
        }

        SbContext ctx = contexts.get(ext);

        builder.append(String.format("> context : %s", ctx)).append("\n");
        if (ctx != null) {
            builder.append(String.format(">> beans : %s", ctx.getBeanDefinitionNames().length)).append("\n");

            for (String name:ctx.getBeanDefinitionNames()) {
                builder.append(String.format("      %s", name)).append("\n");
            }

        } else {
            reportOf(ext.getId()).getThrowable().ifPresent(e -> builder.append(String.format(">> error : %s", e.getMessage(), e)).append("\n"));
        }

        logger.info("State of {}: \n{}", ext, builder);

        ext.getExtensions().forEach(this::printState);
    }

    /**
     * Report of.
     *
     * @param id the id
     * @return the extension report
     */
    private ExtensionReport reportOf(UUID id) {
        return state.compute(id, (i, report) -> report == null ? new ExtensionReport(i) : report);
    }

    /**
     * Cleared report of.
     *
     * @param id the id
     * @return the extension report
     */
    private ExtensionReport clearedReportOf(UUID id) {
        return state.compute(id, (i, report) -> new ExtensionReport(i));
    }

    /**
     * Gets the context.
     *
     * @param extensionId the extension id
     * @return the context
     */
    private Optional<SbContext> getContext(UUID extensionId) {
        return Optional.ofNullable(contexts.get(extensionId));
    }

    /**
     * Send.
     *
     * @param parameters the parameters
     */
    @Override
    public void send(OpenCommandEvent parameters) {

        UUID target = Extension.ROOT_ID;

        if (parameters.getTarget() != null) {
            target = parameters.getTarget();
        }

        send(target, parameters);
    }

    /**
     * Send.
     *
     * @param editorId   the editor id
     * @param parameters the parameters
     */
    private void send(UUID editorId, OpenCommandEvent parameters) {
        getContext(editorId).ifPresent(c -> c.publishEvent(parameters));
    }

    public boolean isStarted() {
        return started;
    }

}
