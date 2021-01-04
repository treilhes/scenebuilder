/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.preferences;

import java.util.EnumSet;
import java.util.function.Function;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.core.controls.DoubleField;
import com.oracle.javafx.scenebuilder.editors.control.paintpicker.PaintPicker;
import com.oracle.javafx.scenebuilder.editors.control.paintpicker.PaintPicker.Mode;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;
import javafx.util.StringConverter;

@Component
@Lazy
public class PreferenceEditorFactoryImpl implements PreferenceEditorFactory {

	private PreferenceEditorFactoryImpl() {}

	@Override
	public Parent newDoubleFieldEditor(Preference<Double> preference) {
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

	@Override
	public Parent newBooleanFieldEditor(Preference<Boolean> preference) {
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

	@Override
	public <T extends Enum<T>> Parent newEnumFieldEditor(EnumPreference<T> preference) {
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

	@Override
	public <T extends Enum<T>> Parent newEnumFieldEditor(EnumPreference<T> preference, Function<T, Node> createGraphic) {
		ComboBox<T> field = new ComboBox<>();
		Callback<ListView<T>, ListCell<T>> cellFactory = null;

		if (createGraphic != null) {
			cellFactory = newCellFactory(createGraphic);
			field.setCellFactory(cellFactory);
			field.setButtonCell(cellFactory.call(null));
		}

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

	@Override
	public Parent newColorFieldEditor(Preference<Color> preference) {
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

	private class PaintPickerDelegate implements PaintPicker.Delegate {

        @Override
        public void handleError(String warningKey, Object... arguments) {
            // Log a warning in message bar
        }
    }

	@Override
	public <T> Parent newChoiceFieldEditor(Preference<T> preference, T[] availableValues) {
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

	@Override
	public <T> Parent newChoiceFieldEditor(Preference<T> preference, T[] availableValues, Function<T, String> toString) {
		ChoiceBox<T> field = new ChoiceBox<>();
		field.getItems().setAll(availableValues);
		field.setValue(preference.getValue());
		field.setConverter(new StringConverter<T>() {
			@Override
			public String toString(T object) {
				return toString.apply(object);
			}

			@Override
			public T fromString(String string) {
				return null;
			}
		});
		field.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			preference.setValue(n).writeToJavaPreferences();
        });
		preference.getObservableValue().addListener((ob, o, n) -> {
			field.setValue(n);
        });
		return field;
	}

	@Override
	public <T, U> Parent newChoiceFieldEditor(Preference<T> preference, U[] availableValues, Function<T, U> adapter, Function<U, T> reverseAdapter) {
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

	private <T> Callback<ListView<T>, ListCell<T>> newCellFactory(Function<T, Node> createGraphic) {
		return new Callback<ListView<T>, ListCell<T>>() {
		    @Override
		    public ListCell<T> call(ListView<T> p) {
		        return new ListCell<T>() {
		            @Override
		            protected void updateItem(T item, boolean empty) {
		                super.updateItem(item, empty);
		                if (!empty) {
		                	setText(item.toString());
			                setGraphic(createGraphic.apply(item));
		                }
		            }
		        };
		    }
		};
	}
}
