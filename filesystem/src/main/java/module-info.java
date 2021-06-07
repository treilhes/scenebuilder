module scenebuilder.filesystem {
    exports com.oracle.javafx.scenebuilder.fs.util;
    exports com.oracle.javafx.scenebuilder.fs.preference.global;
    exports com.oracle.javafx.scenebuilder.fs.controller;
    exports com.oracle.javafx.scenebuilder.fs;
    exports com.oracle.javafx.scenebuilder.fs.menu;

    requires io.reactivex.rxjava2;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}