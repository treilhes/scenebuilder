package com.oracle.javafx.scenebuilder.api;

import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public interface Resizer<T extends Node> {

	public enum Feature {
        FREE,
        WIDTH_ONLY,
        HEIGHT_ONLY,
        SCALING
    }

	T getSceneGraphObject();

	Map<? extends PropertyName, ? extends Object> getChangeMap();

	void revertToOriginalSize();

	Bounds computeBounds(double candidateWidth, double candidateHeight);

	void changeWidth(double width);

	void changeHeight(double height);

	Feature getFeature();

	List<PropertyName> getPropertyNames();

	Object getValue(PropertyName pn);

}
