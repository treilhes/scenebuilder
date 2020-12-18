package com.oracle.javafx.scenebuilder.api;

import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

public interface Content extends FXOMDocument.SceneGraphHolder {

	Editor getEditorController();

	SubScene getContentSubScene();

	Node getGlassLayer();

	Driver lookupDriver(FXOMObject fxomInstance);

	FXOMObject pick(double hitX, double hitY, Set<FXOMObject> pickExcludes);

	HudWindow getHudWindowController();

	boolean isGuidesVisible();

	Group getHandleLayer();

	Group getRudderLayer();

	Transform computeSceneGraphToRudderLayerTransform(Node sceneGraphObject);

	Paint getGuidesColor();

	Parent getRoot();

	Pane getWorkspacePane();

	double getScaling();

	void setScaling(double min);

	Paint getPringColor();

	boolean isContentDisplayable();

	void endInteraction();

	Group getPringLayer();

	void scrollToSelection();

	void reveal(FXOMObject fxomObject);

	FXOMObject pick(double sceneX, double sceneY);

	void beginInteraction();

}
