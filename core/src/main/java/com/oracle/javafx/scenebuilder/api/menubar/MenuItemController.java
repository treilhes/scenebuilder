package com.oracle.javafx.scenebuilder.api.menubar;

public abstract class MenuItemController {

    public abstract boolean canPerform();

    public abstract void perform();

    public String getTitle() {
        return null;
    }

    public boolean isSelected() {
        return false;
    }
}
