module scenebuilder.defaultx {
    exports com.oracle.javafx.scenebuilder.ext.theme.group;
    exports com.oracle.javafx.scenebuilder.ext.controller;
    exports com.oracle.javafx.scenebuilder.ext.menu;
    exports com.oracle.javafx.scenebuilder.ext;
    exports com.oracle.javafx.scenebuilder.ext.theme;
    exports com.oracle.javafx.scenebuilder.ext.theme.global;
    exports com.oracle.javafx.scenebuilder.ext.actions;
    exports com.oracle.javafx.scenebuilder.ext.theme.document;

    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}