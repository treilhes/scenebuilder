open module scenebuilder.application {
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

    //opens com.oracle.javafx.scenebuilder.app.splash to javafx.fxml;
    //opens com.oracle.javafx.scenebuilder.app to spring.core;
    
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
    requires scenebuilder.ext.content.editor;
    requires scenebuilder.ext.control.library;
    requires scenebuilder.core.api;
    requires scenebuilder.ext.css.analyser;
    requires scenebuilder.ext.defaultx;
    requires scenebuilder.ext.document;
    requires scenebuilder.ext.editors;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.filesystem;
    requires scenebuilder.ext.inspector;
    requires scenebuilder.core.kit;
    requires scenebuilder.core.library;
    requires scenebuilder.ext.preference.editor;
    requires scenebuilder.ext.preview;
    requires scenebuilder.ext.sb;
    requires scenebuilder.ext.templates;
    requires spring.aop;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
}