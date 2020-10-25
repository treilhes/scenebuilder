package com.oracle.javafx.scenebuilder.api;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Parent;

public interface FxmlController {
	URL getFxmlURL();
	ResourceBundle getResources();
	void setPanelRoot(Parent panelRoot);
	void controllerDidLoadFxml();
}
