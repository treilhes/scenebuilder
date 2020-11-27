package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Node;

public interface Handles<T> extends Decoration<T> {

	FXOMObject getFxomObject();

	void setEnabled(boolean enabled);

	Node getRootNode();

	Gesture findEnabledGesture(Node hitNode);

}
