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
package com.gluonhq.jfxapps.core.appmngr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.loader.OpenCommandEvent;
import com.gluonhq.jfxapps.boot.api.loader.extension.ApplicationExtension;
import com.gluonhq.jfxapps.core.api.application.ApplicationActionFactory;
import com.gluonhq.jfxapps.core.api.application.ApplicationClassloader;
import com.gluonhq.jfxapps.core.api.application.CommandHandler;
import com.gluonhq.jfxapps.core.api.document.DocumentActionFactory;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloaderDispatcher;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadHolder;
import com.gluonhq.jfxapps.core.api.lifecycle.DisposeWithApplication;
import com.gluonhq.jfxapps.core.api.lifecycle.InitWithApplication;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.dialog.ApplicationDialog;

import jakarta.inject.Provider;

@ApplicationSingleton
public class CommandHandlerImpl implements CommandHandler, Application {

	private static final Logger logger = LoggerFactory.getLogger(CommandHandlerImpl.class);

	private final ApplicationActionFactory applicationActionFactory;
	private final DocumentActionFactory documentActionFactory;
	private final JavafxThreadHolder fxThreadHolder;
	private final Set<ApplicationExtension> applications;
	private final ApplicationDialog applicationDialog;

	/**
	 * Commands waiting for the javafx thread to start
	 */
	private final List<OpenCommandEvent> waitingCommands = new ArrayList<>();

	private final JfxAppContext context;

	private final Provider<Optional<List<InitWithApplication>>> initializations;
    private final Provider<Optional<List<DisposeWithApplication>>> finalizations;

    private final ApplicationClassloader applicationClassloader;

    private final ApplicationEvents applicationEvents;

    private final JavafxThreadClassloaderDispatcher dispatcher;




	//@formatter:off
    public CommandHandlerImpl(
            JfxAppContext context,
            ApplicationActionFactory applicationActionFactory,
            DocumentActionFactory documentActionFactory,
            JavafxThreadHolder fxThreadHolder,
            JavafxThreadClassloaderDispatcher dispatcher,
            ApplicationClassloader applicationClassloader,
            ApplicationEvents applicationEvents,
            Set<ApplicationExtension> applications,
            ApplicationDialog applicationDialog,
            Provider<Optional<List<InitWithApplication>>> initializations,
            Provider<Optional<List<DisposeWithApplication>>> finalizations) {
        //@formatter:on
		super();
		this.context = context;
		this.applicationActionFactory = applicationActionFactory;
		this.documentActionFactory = documentActionFactory;
		this.fxThreadHolder = fxThreadHolder;
		this.applicationClassloader = applicationClassloader;
		this.applicationEvents = applicationEvents;
		this.applications = applications;
		this.applicationDialog = applicationDialog;
        this.initializations = initializations;
        this.finalizations = finalizations;
        this.dispatcher = dispatcher;

		this.fxThreadHolder.whenStarted(this::executeStoredCommands);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

	    if (event instanceof OpenCommandEvent command) {
	        logger.info("CMD received " + command.toString());

	        if (!fxThreadHolder.hasStarted()) {
	            waitingCommands.add(command);
	        } else {
	            executeStoredCommands();
	            execute(command);
	        }
	    } else {
	        logger.warn("Received an unsupported event: " + event.getClass().getName());
	    }

	}

    private void initApplication() {
        applicationEvents.newWindow().subscribe((window) -> dispatcher.register(window, applicationClassloader));
        applicationEvents.classloader().set(applicationClassloader);
        initializations.get().ifPresent(l -> l.forEach(a -> a.init()));
    }

    public void finalizeApplication() {
        finalizations.get().ifPresent(f -> f.forEach(a -> a.dispose()));
    }

	/**
	 * Execute the stored commands. This method is called when the JavaFX
	 * application thread is started.
	 */
	private synchronized void executeStoredCommands() {
	    initApplication();

		while (!waitingCommands.isEmpty()) {
			OpenCommandEvent currentArgs = waitingCommands.remove(0);
			execute(currentArgs);
		}
	}

//	private void execute(OpenCommandEvent args) {
//		logger.info("CMD executed " + args.toString());
//
//		UUID targetApplication = args.getTarget();
//		File file = args.getFile();
//
//		if (file != null) {
//
//			try {
//				ApplicationInstance instance = instancesManager.lookupInstance(file.toURL());
//				if (instance == null) {
//					instance = instancesManager.newInstance();
//				}
//				instance.openWindow();
//				instance.loadFromFile(file);
//			} catch (MalformedURLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		} else {
//
//			ApplicationInstance instance = instancesManager.lookupUnusedInstance();
//			if (instance == null) {
//				instance = instancesManager.newInstance();
//			}
//			instance.openWindow();
//			instance.loadBlank();
//		}
//	}

	private void execute(OpenCommandEvent args) {
		logger.info("CMD executed " + args.toString());
		context.getApplicationExecutor().setCurrentScope(this);
		UUID targetApplication = args.getTarget();
		File file = args.getFile();

		try {
		    if (file != null) {
	            applicationActionFactory.lookupUnusedInstance(file.toURI().toURL(), (instance) -> {
	                instance.openWindow();
	                documentActionFactory.loadFile(file).perform();
	            }).perform();
	        } else {
	            applicationActionFactory.lookupUnusedInstance(null, (instance) -> {
	                instance.openWindow();
	                documentActionFactory.loadBlank().perform();
	            }).perform();
	        }
        } catch (Exception e) {
            logger.error("Error while executing command", e);
            applicationDialog.addError("Error while executing command", e.getMessage(), e);
        }
	}
}
