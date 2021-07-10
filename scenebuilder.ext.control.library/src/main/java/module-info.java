import com.oracle.javafx.scenebuilder.controllibrary.ControlLibraryExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.control.library {
    exports com.oracle.javafx.scenebuilder.controllibrary.action;
    exports com.oracle.javafx.scenebuilder.controllibrary.menu;
    exports com.oracle.javafx.scenebuilder.controllibrary.controller;
    exports com.oracle.javafx.scenebuilder.controllibrary.library.builtin;
    exports com.oracle.javafx.scenebuilder.controllibrary.panel;
    exports com.oracle.javafx.scenebuilder.controllibrary.drag.source;
    exports com.oracle.javafx.scenebuilder.controllibrary.importer;
    exports com.oracle.javafx.scenebuilder.controllibrary.library.explorer;
    exports com.oracle.javafx.scenebuilder.controllibrary.library;
    exports com.oracle.javafx.scenebuilder.controllibrary.preferences.global;
    exports com.oracle.javafx.scenebuilder.controllibrary;
    
    //opens com.oracle.javafx.scenebuilder.controllibrary.library.builtin to spring.core;
    //opens com.oracle.javafx.scenebuilder.controllibrary.library to spring.core;
    
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires scenebuilder.core.extension.store;
    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    requires scenebuilder.core.library;
    requires org.slf4j;
    requires scenebuilder.ext.sb;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.filesystem;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with ControlLibraryExtension;
}