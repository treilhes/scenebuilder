package com.oracle.javafx.scenebuilder.api;

import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Node;

public interface DragSource {

	List<FXOMObject> getDraggedObjects();

	String makeDropJobDescription();

	FXOMObject getHitObject();

	double getHitX();

	double getHitY();

	boolean isSingleImageViewOnly();

	boolean isSingleTooltipOnly();

	boolean isSingleContextMenuOnly();

	boolean isNodeOnly();

	Node makeShadow();

	boolean isAcceptable();

}
