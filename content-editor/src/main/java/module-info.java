module scenebuilder.content.editor {
    exports com.oracle.javafx.scenebuilder.contenteditor.preferences.global;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key;
    exports com.oracle.javafx.scenebuilder.contenteditor.actions;
    exports com.oracle.javafx.scenebuilder.contenteditor.guides;
    exports com.oracle.javafx.scenebuilder.contenteditor;
    exports com.oracle.javafx.scenebuilder.contenteditor.gesture;
    exports com.oracle.javafx.scenebuilder.contenteditor.controller;
    exports com.oracle.javafx.scenebuilder.contenteditor.i18n;
    exports com.oracle.javafx.scenebuilder.contenteditor.gesture.mouse;

    requires scenebuilder.dnd;
    requires io.reactivex.rxjava2;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.jobs;
    requires org.slf4j;
    requires scenebuilder.sb;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}