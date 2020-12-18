package com.oracle.javafx.scenebuilder.api;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;

public interface ViewSearch {

    Parent getRoot();

    StringProperty textProperty();

    void requestFocus();

}
