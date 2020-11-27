package com.oracle.javafx.scenebuilder.api;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.util.Callback;

public interface InlineEdit {
	public enum Type {

        TEXT_AREA, TEXT_FIELD
    }

	public TextInputControl createTextInputControl(Type type, Node inlineEditingBounds, String text);

	public void startEditingSession(TextInputControl inlineEditor, Node inlineEditingBounds,
			Callback<String, Boolean> requestCommit, Callback<Void, Boolean> requestRevert);
}
