module scenebuilder.dummy.ext {
    exports com.oracle.javafx.scenebuilder.dummy;
    exports com.oracle.javafx.scenebuilder.dummy.i18n;
    exports com.oracle.javafx.scenebuilder.dummy.controller;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}