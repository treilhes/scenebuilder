import com.oracle.javafx.scenebuilder.app.AppExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.application {
    exports com.oracle.javafx.scenebuilder.app.settings;
    exports com.oracle.javafx.scenebuilder.app.preferences;
    exports com.oracle.javafx.scenebuilder.app.i18n;
    exports com.oracle.javafx.scenebuilder.app;

    requires io.reactivex.rxjava2;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires rxjavafx;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.drag.and.drop;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.extension.store;
    requires scenebuilder.core.filesystem;
    requires scenebuilder.core.jobs;
    requires scenebuilder.core.layout;
    requires scenebuilder.core.library;
    requires scenebuilder.ext.content.editor;
    requires scenebuilder.ext.control.library;
    requires scenebuilder.ext.css.analyser;
    requires scenebuilder.ext.defaultx;
    requires scenebuilder.ext.document;
    requires scenebuilder.ext.editors;
    requires scenebuilder.ext.inspector;
    requires scenebuilder.ext.preference.editor;
    requires scenebuilder.ext.preview;
    requires scenebuilder.ext.sb;
    requires scenebuilder.ext.templates;
    requires spring.aop;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires scenebuilder.core.launcher;
    
    provides Extension with AppExtension;
}