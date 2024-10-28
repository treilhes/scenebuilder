package com.oracle.javafx.scenebuilder.api.editors;

import java.util.List;

import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;

public interface PropertyEditorFactory {

    PropertyEditor newEditor(PropertyMetadata propMeta);

    void releaseEditors(List<PropertyEditor> editorsInUse);

    void releaseEditor(PropertyEditor editor);

    PropertyEditorFactorySession newSession();

}