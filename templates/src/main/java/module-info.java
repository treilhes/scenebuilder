module scenebuilder.templates {
    exports com.oracle.javafx.scenebuilder.template.menu;
    exports com.oracle.javafx.scenebuilder.template.i18n;
    exports com.oracle.javafx.scenebuilder.template.controller;
    exports com.oracle.javafx.scenebuilder.template.templates;
    exports com.oracle.javafx.scenebuilder.template;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires scenebuilder.core;
    requires scenebuilder.defaultx;
    requires scenebuilder.extension.api;
    requires scenebuilder.jobs;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}