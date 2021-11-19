import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.extlibrary.ExtensionLibraryExtension;

open module scenebuilder.ext.extension.library {
    exports com.oracle.javafx.scenebuilder.extlibrary.i18n;
    exports com.oracle.javafx.scenebuilder.extlibrary.menu;
    exports com.oracle.javafx.scenebuilder.extlibrary.library;
    exports com.oracle.javafx.scenebuilder.extlibrary.importer;
    exports com.oracle.javafx.scenebuilder.extlibrary.controller;
    exports com.oracle.javafx.scenebuilder.extlibrary.library.explorer;
    exports com.oracle.javafx.scenebuilder.extlibrary;
    exports com.oracle.javafx.scenebuilder.extlibrary.library.builtin;

    requires scenebuilder.starter;
//    requires com.fasterxml.jackson.core;
//    requires com.fasterxml.jackson.databind;
    requires scenebuilder.core.extension.store;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires scenebuilder.core.library;
//    requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.filesystem;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with ExtensionLibraryExtension;
}