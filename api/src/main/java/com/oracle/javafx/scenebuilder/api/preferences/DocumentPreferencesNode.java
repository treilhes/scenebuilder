package com.oracle.javafx.scenebuilder.api.preferences;

public interface DocumentPreferencesNode extends PreferencesNode {
	
	void cleanupCorruptedNodes();

	void clearAllDocumentNodes();
}
