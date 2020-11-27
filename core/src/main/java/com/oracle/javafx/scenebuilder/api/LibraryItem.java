package com.oracle.javafx.scenebuilder.api;

import java.net.URL;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

public interface LibraryItem {

	Library getLibrary();

	FXOMDocument instantiate();

	String getName();

	String getSection();

	String getFxmlText();

	URL getIconURL();

}
