module scenebuilder.log.viewer {
    exports com.oracle.javafx.scenebuilder.logviewer.i18n;
    exports com.oracle.javafx.scenebuilder.logviewer.controller;
    exports com.oracle.javafx.scenebuilder.logviewer;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires logback.classic;
    requires logback.core;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}