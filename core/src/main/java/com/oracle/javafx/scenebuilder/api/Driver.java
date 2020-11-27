package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public interface Driver {

	FXOMObject refinePick(Node sceneGraphNode, double sceneX, double sceneY, FXOMObject match);

	CurveEditor<?> makeCurveEditor(FXOMObject fxomInstance);

	Resizer<?> makeResizer(FXOMObject fxomObject);

	DropTarget makeDropTarget(FXOMObject hitObject, double hitX, double hitY);

	Pring<?> makePring(FXOMObject scopeObject);

	boolean intersectsBounds(FXOMObject fxomObject, Bounds bounds);

	Tring<?> makeTring(DropTarget dropTarget);

	Handles<?> makeHandles(FXOMObject incomingObject);

	Node getInlineEditorBounds(FXOMObject fxomObject);

}
