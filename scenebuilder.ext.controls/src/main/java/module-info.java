module scenebuilder.ext.controls {
    exports com.oracle.javafx.scenebuilder.controls.contextmenu;
    exports com.oracle.javafx.scenebuilder.controls;
    exports com.oracle.javafx.scenebuilder.controls.metadata;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}