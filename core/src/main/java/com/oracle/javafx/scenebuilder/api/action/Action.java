package com.oracle.javafx.scenebuilder.api.action;

import javafx.scene.input.KeyCombination;

public interface Action {
	String getUniqueId();
	String getName();
	String getDescription();
	KeyCombination getWishedAccelerator();
	boolean canPerform();
	void perform();
	void checkAndPerform();

	ExtendedAction<?> extend();
}
