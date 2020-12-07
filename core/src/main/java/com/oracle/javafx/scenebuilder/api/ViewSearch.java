package com.oracle.javafx.scenebuilder.api;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;

public interface ViewSearch {

    Parent getPanelRoot();

    StringProperty textProperty();

    void requestFocus();

}
