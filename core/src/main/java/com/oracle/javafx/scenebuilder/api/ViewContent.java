package com.oracle.javafx.scenebuilder.api;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;

public interface ViewContent {

    void makePanel();

    void setSearchControl(Parent panelRoot);

    void setContent(Parent panelRoot);

    Parent getPanelRoot();

    MenuButton getViewMenuButton();

    StringProperty textProperty();

}
