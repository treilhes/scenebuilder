package com.oracle.javafx.scenebuilder.api;

import javafx.stage.Stage;

public interface Document {
	boolean isInited();
	boolean isUnused();
	boolean isDocumentDirty();
	boolean hasContent();
	boolean hasName();
	String getName();
	Stage getStage();
}
