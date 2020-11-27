package com.oracle.javafx.scenebuilder.api;

import javafx.scene.input.InputEvent;

public interface Gesture {

	void start(InputEvent e, Observer observer);

	public static interface Observer {
        public void gestureDidTerminate(Gesture gesture);
    }
}
