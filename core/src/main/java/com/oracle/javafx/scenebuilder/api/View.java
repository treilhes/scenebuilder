package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.api.subjects.ViewManager;

import javafx.scene.Parent;

public interface View {
	String getName();
	Parent getRoot();
	ViewManager getViewManager();
}
