package com.oracle.javafx.scenebuilder.api.menubar;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public interface MenuAttachment {
	String getTargetId();
	PositionRequest getPositionRequest();
	MenuItem getMenuItem();

	static MenuAttachment separator(String targetId, PositionRequest positionRequest) {
	    return new MenuAttachment() {

            @Override
            public String getTargetId() {
                return targetId;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return positionRequest;
            }

            @Override
            public MenuItem getMenuItem() {
                return new SeparatorMenuItem();
            }
        };
	}
}
