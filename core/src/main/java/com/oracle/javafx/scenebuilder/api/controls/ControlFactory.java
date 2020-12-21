package com.oracle.javafx.scenebuilder.api.controls;

public interface ControlFactory<T> {
    ControlDescriptor getDescriptor();
    T createControl();
}
