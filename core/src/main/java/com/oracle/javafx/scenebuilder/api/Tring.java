package com.oracle.javafx.scenebuilder.api;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

public interface Tring<T> extends Decoration<T> {

	void changeStroke(Paint pringColor);

	Node getRootNode();

}
