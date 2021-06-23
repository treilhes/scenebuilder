module scenebuilder.ext.help.features {
    exports com.oracle.javafx.scenebuilder.helpfeatures.menu;
    exports com.oracle.javafx.scenebuilder.helpfeatures.controller;
    exports com.oracle.javafx.scenebuilder.helpfeatures.i18n;
    exports com.oracle.javafx.scenebuilder.helpfeatures;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}