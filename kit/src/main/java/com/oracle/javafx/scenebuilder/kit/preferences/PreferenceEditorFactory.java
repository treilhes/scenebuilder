package com.oracle.javafx.scenebuilder.kit.preferences;

import java.util.EnumSet;
import java.util.function.Function;

import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.DoubleField;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker;
import com.oracle.javafx.scenebuilder.kit.util.control.paintpicker.PaintPicker.Mode;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class PreferenceEditorFactory {

	private PreferenceEditorFactory() {}

	public static Parent newDoubleFieldEditor(Preference<Double> preference) {
		//TODO why using custom component when a javafx one exists ? Maybe for jdk8?
		DoubleField field = new DoubleField();
		field.setText(preference.getValue().toString());
		field.textProperty().addListener((ob, o, n) -> {
			preference.setValue(Double.valueOf(n)).writeToJavaPreferences();
			field.selectAll();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			field.setText(n.toString());
        });
		return field;
	}

	public static Parent newBooleanFieldEditor(Preference<Boolean> preference) {
		CheckBox field = new CheckBox();
		field.setSelected(preference.getValue());
        field.selectedProperty().addListener((ob, o, n) -> {
			preference.setValue(n).writeToJavaPreferences();
        });
        preference.getObservableValue().addListener((ob, o, n) -> {
			field.setSelected(n);
        });
		return field;
	}

	public static <T extends Enum<T>> Parent newEnumFieldEditor(EnumPreference<T> preference) {
		ChoiceBox<T> field = new ChoiceBox<>();
		EnumSet<T> set = EnumSet.allOf(preference.getEnumClass());
		field.getItems().setAll(set);
		field.setValue(preference.getValue());
		field.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			preference.setValue(n).writeToJavaPreferences();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			field.setValue(n);
        });
		return field;
	}

	public static Parent newColorFieldEditor(Preference<Color> preference) {
//		<MenuButton fx:id="alignmentGuidesButton" mnemonicParsing="false" text="" GridPane.columnIndex="1" GridPane.rowIndex="4">
//	      <graphic>
//	        <Rectangle fx:id="alignmentGuidesGraphic" arcHeight="5.0" arcWidth="5.0" fill="DODGERBLUE" height="10.0" stroke="BLACK" strokeType="INSIDE" width="20.0" />
//	      </graphic>
//	      <items>
//	        <CustomMenuItem fx:id="alignmentGuidesMenuItem" hideOnClick="false" mnemonicParsing="false" text="Unspecified Action" />
//	      </items>
//	    </MenuButton>
		
		MenuButton field = new MenuButton();
		Rectangle graphic = new Rectangle();
		graphic.setArcWidth(5);
		graphic.setArcHeight(5);
		graphic.setFill(Color.DODGERBLUE);
		graphic.setHeight(10);
		graphic.setWidth(20);
		graphic.setStroke(Color.BLACK);
		graphic.setStrokeType(StrokeType.INSIDE);
		CustomMenuItem menuItem = new CustomMenuItem();
		
		field.setGraphic(graphic);
		field.getItems().add(menuItem);
        
		final Color color = preference.getValue();
		final PaintPicker.Delegate delegate = new PaintPickerDelegate();
		PaintPicker picker = new PaintPicker(delegate, Mode.COLOR);
		graphic.setFill(color);
		menuItem.setContent(picker);
		picker.setPaintProperty(color);
		picker.paintProperty().addListener((ob, o, n) -> {
			graphic.setFill(n);
			preference.setValue((Color)n).writeToJavaPreferences();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			graphic.setFill(n);
			picker.setPaintProperty(n);
        });
		return field;
	}
	
	private static class PaintPickerDelegate implements PaintPicker.Delegate {

        @Override
        public void handleError(String warningKey, Object... arguments) {
            // Log a warning in message bar
        }
    }

	public static <T> Parent newChoiceFieldEditor(Preference<T> preference, T[] availableValues) {
		ChoiceBox<T> field = new ChoiceBox<>();
		field.getItems().setAll(availableValues);
		field.setValue(preference.getValue());
		field.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			preference.setValue(n).writeToJavaPreferences();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			field.setValue(n);
        });
		return field;
	}
	
	public static <T, U> Parent newChoiceFieldEditor(Preference<T> preference, U[] availableValues, Function<T, U> adapter, Function<U, T> reverseAdapter) {
		ChoiceBox<U> field = new ChoiceBox<>();
		field.getItems().setAll(availableValues);
		field.setValue(adapter.apply(preference.getValue()));
		field.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			preference.setValue(reverseAdapter.apply(n)).writeToJavaPreferences();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			field.setValue(adapter.apply(n));
        });
		return field;
	}
}
