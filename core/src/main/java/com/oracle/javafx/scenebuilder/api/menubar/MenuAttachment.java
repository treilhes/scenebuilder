package com.oracle.javafx.scenebuilder.api.menubar;

import javafx.scene.control.MenuItem;

public interface MenuAttachment {
	String getTargetId();
	PositionRequest getPositionRequest();
	MenuItem getMenuItem();
}
