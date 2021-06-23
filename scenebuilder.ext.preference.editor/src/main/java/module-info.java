module scenebuilder.ext.preference.editor {
    exports com.oracle.javafx.scenebuilder.prefedit.menu;
    exports com.oracle.javafx.scenebuilder.prefedit.i18n;
    exports com.oracle.javafx.scenebuilder.prefedit;
    exports com.oracle.javafx.scenebuilder.prefedit.editor;
    exports com.oracle.javafx.scenebuilder.prefedit.controller;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.api;
    requires scenebuilder.ext.editors;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}