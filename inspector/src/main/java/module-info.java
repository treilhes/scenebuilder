module scenebuilder.inspector {
    exports com.oracle.javafx.scenebuilder.inspector.controller;
    exports com.oracle.javafx.scenebuilder.inspector;
    exports com.oracle.javafx.scenebuilder.inspector.actions;
    exports com.oracle.javafx.scenebuilder.inspector.i18n;
    exports com.oracle.javafx.scenebuilder.inspector.preferences.document;

    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.jobs;
    requires org.slf4j;
    requires rxjavafx;
    requires scenebuilder.sb;
    requires scenebuilder.core;
    requires scenebuilder.editors;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}