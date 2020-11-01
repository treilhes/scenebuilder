package com.oracle.javafx.scenebuilder.api;

import java.util.List;

import javafx.scene.Parent;

public interface DockType {
	String getNameKey();
	Parent computeRoot(List<View> views);
}
