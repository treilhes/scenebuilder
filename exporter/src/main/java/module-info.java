module scenebuilder.exporter {
    exports com.oracle.javafx.scenebuilder.exporter.i18n;
    exports com.oracle.javafx.scenebuilder.exporter.controller;
    exports com.oracle.javafx.scenebuilder.exporter.menu;
    exports com.oracle.javafx.scenebuilder.exporter.format;
    exports com.oracle.javafx.scenebuilder.exporter;

    requires io.reactivex.rxjava2;
    requires java.desktop;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}