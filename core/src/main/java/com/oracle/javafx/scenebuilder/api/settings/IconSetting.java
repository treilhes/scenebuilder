package com.oracle.javafx.scenebuilder.api.settings;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public interface IconSetting {

    void setWindowIcon(Alert alert);

    void setWindowIcon(Stage stage);

}