package com.oracle.javafx.scenebuilder.api;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.api.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.scene.Node;

public interface CurveEditor<T> {

	Node getSceneGraphObject();

	EditCurveGuide createController(EnumMap<Tunable, Integer> tunableMap);

	Map<PropertyName, Object> getChangeMap();

	List<Double> getPoints();

	void removePoint(EnumMap<Tunable, Integer> tunableMap);

	void addPoint(EnumMap<Tunable, Integer> tunableMap, double x, double y);

	void revertToOriginalState();

	void moveTunable(EnumMap<Tunable, Integer> tunableMap, double x, double y);

	List<PropertyName> getPropertyNames();

	Object getValue(PropertyName pn);


}
