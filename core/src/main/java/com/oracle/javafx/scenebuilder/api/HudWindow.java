package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.util.CardinalPoint;

import javafx.scene.Node;

public interface HudWindow {

	void updatePopupLocation();

	void closeWindow();

	void setRowCount(int i);

	void setNameAtRowIndex(String string, int i);

	void setRelativePosition(CardinalPoint e);

	void openWindow(Node sceneGraphObject);

	void setValueAtRowIndex(String value, int i);

}
