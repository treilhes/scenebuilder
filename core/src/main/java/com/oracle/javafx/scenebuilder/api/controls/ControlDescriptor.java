package com.oracle.javafx.scenebuilder.api.controls;

import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;

public interface ControlDescriptor {
    String getSection();
    //Icon getIcon()
    ComponentClassMetadata getMetadata();
    Class<?> getControlClass();
}
