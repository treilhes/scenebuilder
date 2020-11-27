package com.oracle.javafx.scenebuilder.api;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public interface EditCurveGuide {
	public enum Tunable {
        START,
        END,
        CONTROL1,
        CONTROL2,
        VERTEX,
        SIDE;
    }

	public void addSampleBounds(Node childNode);

	public Point2D makeStraightAngles(Point2D current);

	public Point2D correct(Point2D current);
}
