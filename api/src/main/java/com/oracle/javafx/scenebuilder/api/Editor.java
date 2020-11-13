package com.oracle.javafx.scenebuilder.api;

import java.net.URL;

public interface Editor {
	
	/**
     * Returns null or the location of the fxml being edited.
     * 
     * @return null or the location of the fxml being edited.
     */
	public URL getFxmlLocation();
}
