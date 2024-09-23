package com.gluonhq.jfxapps.core.api.ui.controller.menu;

import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;

import javafx.scene.control.MenuButton;

public interface ViewMenu {

    void clearMenu(View view, MenuButton menuButton);

    void buildMenu(View view, MenuButton menuButton);

}