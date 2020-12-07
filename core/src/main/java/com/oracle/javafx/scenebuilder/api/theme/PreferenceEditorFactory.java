package com.oracle.javafx.scenebuilder.api.theme;

import java.util.function.Function;

import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

// TODO move this interface in com.oracle.javafx.scenebuilder.api.preferences
public interface PreferenceEditorFactory {

	Parent newDoubleFieldEditor(Preference<Double> preference);

	Parent newBooleanFieldEditor(Preference<Boolean> preference);

	<T extends Enum<T>> Parent newEnumFieldEditor(EnumPreference<T> preference);

	<T extends Enum<T>> Parent newEnumFieldEditor(EnumPreference<T> preference, Function<T, Node> createGraphic);

	Parent newColorFieldEditor(Preference<Color> preference);

	<T> Parent newChoiceFieldEditor(Preference<T> preference, T[] availableValues);

	<T> Parent newChoiceFieldEditor(Preference<T> preference, T[] availableValues, Function<T, String> toString);

	<T, U> Parent newChoiceFieldEditor(Preference<T> preference, U[] availableValues, Function<T, U> adapter,
			Function<U, T> reverseAdapter);

}