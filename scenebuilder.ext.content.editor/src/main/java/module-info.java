import com.oracle.javafx.scenebuilder.contenteditor.ContentEditorExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.content.editor {
    exports com.oracle.javafx.scenebuilder.contenteditor.preferences.global;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key;
    exports com.oracle.javafx.scenebuilder.contenteditor.actions;
    exports com.oracle.javafx.scenebuilder.contenteditor.guides;
    exports com.oracle.javafx.scenebuilder.contenteditor;
    exports com.oracle.javafx.scenebuilder.contenteditor.menu;
    exports com.oracle.javafx.scenebuilder.contenteditor.gesture;
    exports com.oracle.javafx.scenebuilder.contenteditor.controller;
    exports com.oracle.javafx.scenebuilder.contenteditor.i18n;
    exports com.oracle.javafx.scenebuilder.contenteditor.gesture.mouse;
    
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.drag.and.drop;
    requires io.reactivex.rxjava2;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    requires org.slf4j;
    requires scenebuilder.ext.sb;
    
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with ContentEditorExtension;
}