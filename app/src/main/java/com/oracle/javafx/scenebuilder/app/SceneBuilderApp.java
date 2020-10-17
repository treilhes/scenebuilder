package com.oracle.javafx.scenebuilder.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class SceneBuilderApp {

	/**
	 * TODO is this comment still relevant?
     * Normally ignored in correctly deployed JavaFX application.
     * But on Mac OS, this method seems to be called by the javafx launcher.
     */
	public static void main(String[] args) {
		Application.launch(SceneBuilderBootstrap.class, args);
	}

}
