module scenebuilder.image.library {
    exports com.oracle.javafx.scenebuilder.imagelibrary.menu;
    exports com.oracle.javafx.scenebuilder.imagelibrary.action;
    exports com.oracle.javafx.scenebuilder.imagelibrary.library;
    exports com.oracle.javafx.scenebuilder.imagelibrary.library.builtin;
    exports com.oracle.javafx.scenebuilder.imagelibrary.controller;
    exports com.oracle.javafx.scenebuilder.imagelibrary.panel;
    exports com.oracle.javafx.scenebuilder.imagelibrary;
    exports com.oracle.javafx.scenebuilder.imagelibrary.drag.source;
    exports com.oracle.javafx.scenebuilder.imagelibrary.importer;
    exports com.oracle.javafx.scenebuilder.imagelibrary.library.explorer;
    exports com.oracle.javafx.scenebuilder.imagelibrary.preferences.global;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.jobs;
    requires scenebuilder.library;
    requires lombok;
    requires org.apache.fontbox;
    requires org.slf4j;
    requires scenebuilder.sb;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires scenebuilder.extension.store;
    requires scenebuilder.filesystem;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}