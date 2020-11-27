package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

public interface Pring<T> extends Decoration<T>{

	void changeStroke(Paint pringColor);

	Node getRootNode();

	FXOMObject getFxomObject();

	Gesture findGesture(Node hitNode);

}
