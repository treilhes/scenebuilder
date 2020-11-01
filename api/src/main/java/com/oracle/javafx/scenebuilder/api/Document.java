package com.oracle.javafx.scenebuilder.api;

public interface Document {
	boolean isInited();
	boolean isUnused();
	boolean isDocumentDirty();
	boolean hasContent();
	boolean hasName();
	String getName();
}
