package com.oracle.javafx.scenebuilder.api.editors;

import java.util.function.Consumer;

import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

public interface PropertyEditorFactorySession {

    PropertyEditor getEditor(ValuePropertyMetadata propMeta, SelectionState selectionState);

    void clear();

    void reset(SelectionState selectionState, PropertyEditor... excludedEditors);

    void forEach(Consumer<PropertyEditor> doSomething, PropertyEditor... excludedEditors);

    PropertyEditor getFxIdEditor(SelectionState selectionState);

    PropertyEditor getControllerClassEditor(SelectionState selectionState);

    PropertyEditor find(PropertyName propName);

}