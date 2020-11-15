/*
 * Copyright (c) 2016, 2017, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.app;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.app.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.registration.RegistrationWindowController;
import com.oracle.javafx.scenebuilder.app.tracking.Tracking;
import com.oracle.javafx.scenebuilder.app.welcomedialog.WelcomeDialogWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.preferences.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.kit.selectionbar.SelectionBarController;
import com.oracle.javafx.scenebuilder.kit.util.control.effectpicker.EffectPicker;

import javafx.application.Platform;
import javafx.stage.Stage;

@ComponentScan(basePackageClasses = { com.oracle.javafx.scenebuilder.kit.i18n.I18N.class,
		com.oracle.javafx.scenebuilder.app.i18n.I18N.class, I18N.class, PreferencesContext.class,
		MavenArtifactsPreferences.class, MavenSetting.class, Tracking.class, RegistrationWindowController.class,
		WelcomeDialogWindowController.class, PreferencesController.class, BuiltinLibrary.class, Metadata.class,
		MenuBarController.class, EditorController.class, SelectionBarController.class,
		SceneBuilderBeanFactory.class }, basePackages = { "com.oracle.javafx.scenebuilder.app.settings",
				"com.oracle.javafx.scenebuilder.api.preferences", "com.oracle.javafx.scenebuilder.app.preferences",
				"com.oracle.javafx.scenebuilder.kit.preferences", "com.oracle.javafx.scenebuilder.kit.library.user",
				"com.oracle.javafx.scenebuilder.api.subjects", "com.oracle.javafx.scenebuilder.app",
				"com.oracle.javafx.scenebuilder.gluon.preferences" 
				})
public class SceneBuilderBootstrap extends JavafxApplication {

	private static final CountDownLatch launchLatch = new CountDownLatch(1);

	public SceneBuilderBootstrap() {
		// set design time flag
		java.beans.Beans.setDesignTime(true);

		/*
		 * We spawn our two threads for handling background startup.
		 */
		final Runnable p0 = () -> backgroundStartPhase0();
		final Runnable p1 = () -> {
			try {
				launchLatch.await();
				backgroundStartPhase2();
			} catch (InterruptedException x) {
				// JavaFX thread has been interrupted. Simply exits.
			}
		};
		final Thread phase0 = new Thread(p0, "Phase 0"); // NOI18N
		final Thread phase1 = new Thread(p1, "Phase 1"); // NOI18N
		phase0.setDaemon(true);
		phase1.setDaemon(true);

		// Note : if you suspect a race condition bug, comment the two next
		// lines to make startup fully sequential.
		phase0.start();
		phase1.start();
	}

	/*
	 * Application
	 */
	@Override
	public void start(Stage stage) throws Exception {
		super.start(stage);
		launchLatch.countDown();
		setApplicationUncaughtExceptionHandler();

	}

	private void setApplicationUncaughtExceptionHandler() {
		if (Thread.getDefaultUncaughtExceptionHandler() == null) {
			// Register a Default Uncaught Exception Handler for the application
			Thread.setDefaultUncaughtExceptionHandler(new SceneBuilderUncaughtExceptionHandler());
		}
	}

	private static class SceneBuilderUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			// Print the details of the exception in SceneBuilder log file
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "An exception was thrown:", e); // NOI18N
		}
	}

	/*
	 * Background startup
	 * 
	 * To speed SB startup, we create two threads which anticipate some
	 * initialization tasks and offload the JFX thread: - 'Phase 0' thread executes
	 * tasks that do not require JFX initialization - 'Phase 1' thread executes
	 * tasks that requires JFX initialization
	 * 
	 * Tasks executed here must be carefully chosen: 1) they must be thread-safe 2)
	 * they should be order-safe : whether they are executed in background or by the
	 * JFX thread should make no difference.
	 * 
	 * Currently we simply anticipate creation of big singleton instances (like
	 * Metadata, Preferences...)
	 */

	private void backgroundStartPhase0() {
		assert Platform.isFxApplicationThread() == false; // Warning

		// PreferencesController.getSingleton();
		// Metadata.getMetadata();
	}

	private void backgroundStartPhase2() {
		assert Platform.isFxApplicationThread() == false; // Warning
		assert launchLatch.getCount() == 0; // i.e JavaFX is initialized

		// BuiltinLibrary.getLibrary();
//        if (EditorPlatform.IS_MAC) {
//            MenuBarController.getSystemMenuBarController();
//        }
		EffectPicker.getEffectClasses();
	}

	@Bean
	@Qualifier("userlibrary")
	@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
	public UserLibrary getUserLibrary(@Autowired MavenArtifactsPreferences mavenPreferences, @Autowired UILogger logger,
			@Autowired BuiltinLibrary builtinLibrary) {
		// Creates the user library
		return new UserLibrary(AppPlatform.getUserLibraryFolder(), mavenPreferences, logger, builtinLibrary);
//                () -> mavenPreferences.getArtifactsPathsWithDependencies(),
//                () -> mavenPreferences.getArtifactsFilter());
	}

	@Bean
	public LazyInitTargetSource firstLinkedInstanceSource() {
		LazyInitTargetSource lazyInit = new LazyInitTargetSource();
		lazyInit.setTargetBeanName("documentWindowController");
		return lazyInit;
	}

	@Bean
	public ProxyFactoryBean proxyFactoryBean() {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTargetSource(firstLinkedInstanceSource());
		return proxyFactoryBean;
	}

}
