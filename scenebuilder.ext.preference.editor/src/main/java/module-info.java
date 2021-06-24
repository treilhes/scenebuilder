import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.prefedit.PreferenceEditorExtension;

open module scenebuilder.ext.preference.editor {
    exports com.oracle.javafx.scenebuilder.prefedit.menu;
    exports com.oracle.javafx.scenebuilder.prefedit.i18n;
    exports com.oracle.javafx.scenebuilder.prefedit;
    exports com.oracle.javafx.scenebuilder.prefedit.editor;
    exports com.oracle.javafx.scenebuilder.prefedit.controller;

    //opens com.oracle.javafx.scenebuilder.prefedit.editor to spring.core;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.api;
    requires scenebuilder.ext.editors;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    
    provides Extension with PreferenceEditorExtension;
}