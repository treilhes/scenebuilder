module app {
    exports com.oracle.javafx.scenebuilder.app.settings;
    exports com.oracle.javafx.scenebuilder.app.splash;
    exports com.oracle.javafx.scenebuilder.app.about;
    exports com.oracle.javafx.scenebuilder.app.preferences;
    exports com.oracle.javafx.scenebuilder.app.preferences.document;
    exports com.oracle.javafx.scenebuilder.app.i18n;
    exports com.oracle.javafx.scenebuilder.app.menubar;
    exports com.oracle.javafx.scenebuilder.app;
    exports com.oracle.javafx.scenebuilder.app.welcomedialog;
    exports com.oracle.javafx.scenebuilder.app.util;
    exports com.oracle.javafx.scenebuilder.app.message;

    opens com.oracle.javafx.scenebuilder.app.splash to javafx.fxml;
    opens com.oracle.javafx.scenebuilder.app to spring.core;
    
    requires io.reactivex.rxjava2;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires org.slf4j;
    requires rxjavafx;
    requires scenebuilder.content.editor;
    requires scenebuilder.control.library;
    requires scenebuilder.core;
    requires scenebuilder.css.analyser;
    requires scenebuilder.defaultx;
    requires scenebuilder.document;
    requires scenebuilder.editors;
    requires scenebuilder.extension.api;
    requires scenebuilder.filesystem;
    requires scenebuilder.inspector;
    requires scenebuilder.kit;
    requires scenebuilder.library;
    requires scenebuilder.preference.editor;
    requires scenebuilder.preview;
    requires scenebuilder.sb;
    requires scenebuilder.templates;
    requires spring.aop;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
}