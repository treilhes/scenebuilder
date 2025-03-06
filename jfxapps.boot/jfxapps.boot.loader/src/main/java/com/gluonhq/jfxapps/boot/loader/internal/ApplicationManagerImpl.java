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
package com.gluonhq.jfxapps.boot.loader.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.loader.ApplicationManager;
import com.gluonhq.jfxapps.boot.api.loader.BootException;
import com.gluonhq.jfxapps.boot.api.loader.ExtensionReport;
import com.gluonhq.jfxapps.boot.api.loader.LoadType;
import com.gluonhq.jfxapps.boot.api.loader.OpenCommandEvent;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.api.splash.SplashScreenProvider;
import com.gluonhq.jfxapps.boot.api.utils.ProgressListener;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper;
import com.gluonhq.jfxapps.boot.loader.internal.layer.LayerBootstraper;
import com.gluonhq.jfxapps.boot.loader.model.LoadState;
import com.gluonhq.jfxapps.boot.loader.model.LoadableContent;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationManagerImpl.
 */
@Component
public class ApplicationManagerImpl implements ApplicationManager {

	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(ApplicationManagerImpl.class);

	private final static UUID ROOT_ID = com.gluonhq.jfxapps.boot.api.loader.extension.Extension.ROOT_ID;

	private final JfxAppContext context;

	private final JfxAppsPlatform platform;

	/** The layer manager. */
	private final ModuleLayerManager layerManager;

	/** The contexts. */
	private final ContextBootstraper contexts;

	/** The layers. */
	private final LayerBootstraper layers;

	/** The application. */
	private Map<UUID, LoadableContent> startedApplications = new HashMap<>();

	/** The state. */
	private Map<UUID, ExtensionReport> state = new HashMap<>();

	private final StateProvider stateProvider;

	private final SplashScreenProvider splashScreenProvider;

	private final Optional<ApplicationStartup> startup;

	private boolean started = false;

	/**
	 * Instantiates a new application manager impl.
	 *
	 * @param layerManager
	 * @param contexts
	 * @param layers
	 */
	// @formatter:off
    protected ApplicationManagerImpl(
    		JfxAppContext context,
    		JfxAppsPlatform platform,
    		ModuleLayerManager layerManager,
    		ContextBootstraper contexts,
            LayerBootstraper layers,
            StateProvider stateProvider,
    		SplashScreenProvider splashScreenProvider,
            Optional<ApplicationStartup> startup) {
    	// @formatter:on
		super();
		this.context = context;
		this.platform = platform;
		this.layerManager = layerManager;
		this.contexts = contexts;
		this.layers = layers;
		this.stateProvider = stateProvider;
		this.splashScreenProvider = splashScreenProvider;
		this.startup = startup;
	}

	@Override
	public void start(LoadType loadType) throws BootException {
		startApplication(ROOT_ID, loadType);
	}

	/**
	 * Start an application by : - loading the ModuleLayer of the application and
	 * it's extensions. - starting the JfxAppContext of the application and it's
	 * extensions.
	 *
	 * @param applicationId the application id
	 */
	@Override
	public void startApplication(UUID applicationId, LoadType loadType) {
		Objects.requireNonNull(applicationId, "applicationId is null");

		if (!isStarted(applicationId)) {

			var splash = splashScreenProvider.getSplashScreen(applicationId);
			var steps = splash != null ? splash.asSubSteps(3) : null;

			var stateProgress = steps != null ? steps.get(0) : null;
			var loadProgress = steps != null ? steps.get(1) : null;
			var launchProgress = steps != null ? steps.get(2) : null;

			if (splash != null) {
				stateProgress.notifyStart();
			}

			var appDef = startup.map(s -> s.start("application.manager.main.state"));
			var application = stateProvider.applicationState(applicationId, loadType);
			appDef.ifPresent(s -> s.tag("Load definition", applicationId.toString()).end());

			if (application == null) {
				throw new RuntimeException("Application not found for " + applicationId);
			}
			var appLoad = startup.map(s -> s.start("application.manager.main.load"));

			if (splash != null) {
				stateProgress.notifyFinish();
				loadProgress.notifyStart();
			}
			// load all layers
			loadApplication(application, loadType, loadProgress);
			appLoad.ifPresent(s -> s.tag("Load Context", applicationId.toString()).end());

			if (splash != null) {
				loadProgress.notifyFinish();
				launchProgress.notifyStart();
			}

			var appStart = startup.map(s -> s.start("application.manager.main.start"));

			try {
				// start all contexts
				launchApplication(application, launchProgress);
			} catch (BootException e) {
				appStart.ifPresent(s -> s.tag("error", e.getMessage()));
				logger.error("Unable to boot application {}", applicationId, e);
			}
			appStart.ifPresent(s -> s.tag("Boot Context", applicationId.toString()).end());

			if (splash != null) {
				launchProgress.notifyFinish();
			}

			stateProvider.saveState(application.getExtension());
			startedApplications.put(applicationId, application);
		} else {
			logger.warn("JfxApps {} already started bypassing start", applicationId);
		}
	}

	/**
	 * Stop.
	 */
	@Override
	public void stop() {
		LoadableContent root = startedApplications.get(ROOT_ID);

		// stop all applications
		startedApplications.values().stream().filter(a -> a != root).forEach(e -> stopApplication(e.getId()));

		// stop root application
		stopExtensionTree(Set.of(root));

		unload();

		startedApplications.clear();
	}

	/**
	 * Stop editor.
	 *
	 * @param editorId the editor id
	 */
	@Override
	public void stopApplication(UUID editorId) {

		LoadableContent application = startedApplications.get(editorId);

		if (application == null) {
			logger.warn("Application not started for {}", editorId);
			return;
		}

		if (!contexts.exists(application)) {
			logger.warn("Application context does not exists for {}", application.getId());
			return;
		}

		stopExtensionTree(Set.of(application));
		unloadApplication(editorId);
	}

	/**
	 * Load editor.
	 *
	 * @param applicationId    the editor id
	 * @param progressListener the progress listener
	 * @return
	 */
	public void loadApplication(LoadableContent application, LoadType loadType, ProgressListener progressListener) {
		Objects.nonNull(application);

		UUID applicationId = application.getId();

		MultipleProgressListener listener = new MultipleProgressListener(progressListener);

		Layer parentLayer = ROOT_ID.equals(applicationId) ? null : layers.get(ROOT_ID);

		if (parentLayer == null && !ROOT_ID.equals(applicationId)) {
			throw new RuntimeException("Root layer not found");
		}

		GroupTaskExecutor executor = new GroupTaskExecutor(platform.getAvailableProcessors());
		loadExtensionTree(executor, parentLayer, Set.of(application), listener);
		executor.shutdown();

	}

	/**
	 * Load extension tree.
	 *
	 * @param parentLayer      the parent layer
	 * @param extensionSet     the extension set
	 * @param progressListener the progress listener
	 */
	private void loadExtensionTree(GroupTaskExecutor executor, Layer parentLayer,
			Set<? extends LoadableContent> extensionSet, MultipleProgressListener progressListener) {

		List<Runnable> extensionLoadings = extensionSet.stream().map(ext -> {
			Runnable runnable = () -> {
				if (ext.getLoadState() != LoadState.Deleted && ext.getLoadState() != LoadState.Disabled) {
					logger.info("Loading extension layer {}", ext.getId());
					Layer layer = null;

					try {
						layer = layers.load(parentLayer, ext, progressListener);
					} catch (Throwable e) {
						logger.error("Unable to load extension layer {}", ext.getId(), e);
						ext.setLoadState(LoadState.Error);
						reportOf(ext.getId()).error("", e);
					}
					logger.info("Loading extension layer {} done", ext.getId());

					if (layer != null) {
						ext.setLoadState(LoadState.Loaded);
						loadExtensionTree(executor, layer, ext.getExtensions(), progressListener);
					}
				}
			};
			return runnable;
		}).toList();

		try {
			String loadKey = parentLayer == null ? "ROOT" : parentLayer.getId().toString();
			executor.submitGroupTasks(loadKey, extensionLoadings);
		} catch (InterruptedException e) {
			logger.error("Unable to load extension, interrupted", e);
		}
	}

	/**
	 * Start editor.
	 *
	 * @param applicationId    the application id
	 * @param progressListener the progress listener
	 * @throws BootException
	 */
	private void launchApplication(LoadableContent application, ProgressListener progressListener) throws BootException {

		Objects.requireNonNull(application, "application is null");

		MultipleProgressListener listener = new MultipleProgressListener(progressListener);

		if (application.getLoadState() != LoadState.Loaded) {
			throw new RuntimeException("Application layer not loaded for " + application.getId());
		}

		if (contexts.exists(application)) {
			logger.warn("Extension context already exists for {}", application.getId());
			return;
		}

		final JfxAppContext parentContext;
		if (ROOT_ID.equals(application.getId())) {
			parentContext = context;
		} else {
			parentContext = contexts.get(ROOT_ID);
		}

		GroupTaskExecutor executor = new GroupTaskExecutor(platform.getAvailableProcessors());
		launchExtensionTree(executor, parentContext, Set.of(application), listener);
		executor.shutdown();

		ExtensionReport rootReport = getReport(application.getId());
		if (rootReport.hasError()) {
			throw new BootException("Unable to boot root extension", rootReport);
		}
	}

	/**
	 * Start extension tree.
	 *
	 * @param parentContext    the parent context
	 * @param extensionSet     the extension set
	 * @param progressListener the progress listener
	 */
	private void launchExtensionTree(GroupTaskExecutor executor, JfxAppContext parentContext,
			Set<? extends LoadableContent> extensionSet, MultipleProgressListener progressListener) {

		List<Runnable> extensionStartings = extensionSet.stream().map(ext -> {
			Runnable runnable = () -> {
				try {
					List<Object> singletonInstances = List.of(this);
					JfxAppContext extContext = contexts.create(parentContext, ext, singletonInstances,
							progressListener);
					launchExtensionTree(executor, extContext, ext.getExtensions(), progressListener);
				} catch (Throwable e) {
					ext.setLoadState(LoadState.Error);
					reportOf(ext.getId()).error("", e);
				}
			};
			return runnable;
		}).toList();
		try {
			executor.submitGroupTasks(parentContext == null ? "ROOT" : parentContext.getId(), extensionStartings);
		} catch (InterruptedException e) {
			logger.error("Unable to start extension, interrupted", e);
		}
	}

	/**
	 * Stop extension tree.
	 *
	 * @param extensionSet the extension set
	 */
	private void stopExtensionTree(Set<? extends LoadableContent> extensionSet) {
		extensionSet.forEach(ext -> {
			contexts.close(ext);
			stopExtensionTree(ext.getExtensions());
		});

	}

	/**
	 * Unload.
	 */
	public void unload() {
		LoadableContent root = startedApplications.get(ROOT_ID);

		startedApplications.values().stream().filter(a -> a != root).forEach(e -> unloadApplication(e.getId()));

		unloadExtensionTree(Set.of(root));
	}

	/**
	 * Unload editor.
	 *
	 * @param applicationId the application id
	 */
	public void unloadApplication(UUID applicationId) {

		LoadableContent application = startedApplications.get(applicationId);

		if (application == null) {
			logger.warn("Application not started for {}", applicationId);
			return;
		}

		if (!contexts.exists(application)) {
			logger.warn("Editor context does not exists for {}", application.getId());
			return;
		}

		unloadExtensionTree(Set.of(application));
	}

	/**
	 * Unload extension tree.
	 *
	 * @param extensionSet the extension set
	 */
	private void unloadExtensionTree(Set<? extends LoadableContent> extensionSet) {
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

//    /**
//     * Removes the.
//     *
//     * @param extensionId the extension id
//     */
//    private void remove(UUID extensionId) {
//
//        Map<UUID, Extension> flattened = flatten(appContainer);
//
//        Extension extension = flattened.get(extensionId);
//
//        if (extension != null) {
//            extension.setLoadState(LoadState.Deleted);
//            // stopExtensionTree(Set.of(extension));
//            unloadExtensionTree(Set.of(extension));
//        } else {
//            logger.warn("Extension not found for {}", extensionId);
//        }
//    }

//    /**
//     * Disable.
//     *
//     * @param extensionId the extension id
//     */
//    private void disable(UUID extensionId) {
//        Application application = updater.getLoaded();
//        Map<UUID, AbstractExtension> flattened = flatten(application);
//
//        AbstractExtension extension = flattened.get(extensionId);
//
//        if (extension != null) {
//            extension.setLoadState(LoadState.Disabled);
//            stopExtensionTree(extension.getExtensions());
//            contexts.close(extension);
//            updater.update(application);
//        } else {
//            logger.warn("Extension not found for {}", extensionId);
//        }
//    }

//    /**
//     * Flatten.
//     *
//     * @param application the application
//     * @return the map
//     */
//    private static Map<UUID, Extension> flatten(JfxApps application) {
//        Map<UUID, Extension> flattened = new HashMap<>();
//        flattened.put(application.getId(), application);
//        flatten(flattened, application.getExtensions());
//        flatten(flattened, application.getApplications());
//        return flattened;
//    }

//    /**
//     * Flatten.
//     *
//     * @param flattened    the flattened
//     * @param extensionSet the extension set
//     */
//    private static void flatten(Map<UUID, Extension> flattened,
//            Set<? extends Extension> extensionSet) {
//        extensionSet.forEach(ext -> {
//            flattened.put(ext.getId(), ext);
//            flatten(flattened, ext.getExtensions());
//        });
//    }

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

//    /**
//     * Log state.
//     */
//    public void logState() {
//        JfxApps application = getState();
//
//        printState(application);
//
//        application.getApplications().forEach(this::printState);
//    }

	/**
	 * Prints the state.
	 *
	 * @param ext the ext
	 */
	public void printState(LoadableContent ext) {

		Layer layer = layerManager.get(ext.getId());

		StringBuilder builder = new StringBuilder();

		builder.append(String.format("%s : %s", ext.getClass().getName(), ext.getId())).append("\n");
		builder.append(String.format("> state : %s", ext.getLoadState())).append("\n");
		builder.append(String.format("> layer : %s", layer)).append("\n");

		if (layer != null) {
			layer.jars().forEach(j -> {
				builder.append(String.format(">> jar : %s", j.getFileName())).append("\n");
				;
			});
			layer.modules().forEach(m -> {
				builder.append(String.format(">> module : %s", m.get().getName())).append("\n");
				;
			});
			layer.automaticModules().forEach(m -> {
				builder.append(String.format(">> auto : %s", m.get().getName())).append("\n");
				;
			});
			layer.unnamedModules().forEach(m -> {
				builder.append(String.format(">> unnamed : %s", m.get().getName())).append("\n");
				;
			});
		} else {
			reportOf(ext.getId()).getThrowable()
					.ifPresent(e -> builder.append(String.format(">> error : %s", e.getMessage(), e)).append("\n"));
		}

		JfxAppContext ctx = contexts.get(ext);

		builder.append(String.format("> context : %s", ctx)).append("\n");
		if (ctx != null) {
			builder.append(String.format(">> beans : %s", ctx.getBeanDefinitionNames().length)).append("\n");

			for (String name : ctx.getBeanDefinitionNames()) {
				builder.append(String.format("      %s", name)).append("\n");
			}

		} else {
			reportOf(ext.getId()).getThrowable()
					.ifPresent(e -> builder.append(String.format(">> error : %s", e.getMessage(), e)).append("\n"));
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
	private Optional<JfxAppContext> getContext(UUID extensionId) {
		return Optional.ofNullable(contexts.get(extensionId));
	}

	/**
	 * Send.
	 *
	 * @param parameters the parameters
	 */
	@Override
	public void send(OpenCommandEvent parameters) {

		UUID target = ROOT_ID;

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

	private boolean isStarted(UUID applicationId) {
		return startedApplications.containsKey(applicationId);
	}

}
