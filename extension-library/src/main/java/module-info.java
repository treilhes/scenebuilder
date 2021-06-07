module scenebuilder.extension.library {
    exports com.oracle.javafx.scenebuilder.extlibrary.i18n;
    exports com.oracle.javafx.scenebuilder.extlibrary.menu;
    exports com.oracle.javafx.scenebuilder.extlibrary.library;
    exports com.oracle.javafx.scenebuilder.extlibrary.importer;
    exports com.oracle.javafx.scenebuilder.extlibrary.controller;
    exports com.oracle.javafx.scenebuilder.extlibrary.library.explorer;
    exports com.oracle.javafx.scenebuilder.extlibrary;
    exports com.oracle.javafx.scenebuilder.extlibrary.library.builtin;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires scenebuilder.extension.store;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.library;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires scenebuilder.filesystem;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}