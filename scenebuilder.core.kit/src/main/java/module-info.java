import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.kit.KitExtension;

open module scenebuilder.core.kit {
    exports com.oracle.javafx.scenebuilder.kit.editor.messagelog;
    exports com.oracle.javafx.scenebuilder.kit.glossary;
    exports com.oracle.javafx.scenebuilder.kit.i18n;
    exports com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;
    exports com.oracle.javafx.scenebuilder.kit.editor;
    exports com.oracle.javafx.scenebuilder.kit.editor.util;
    exports com.oracle.javafx.scenebuilder.kit;
    exports com.oracle.javafx.scenebuilder.kit.selectionbar;
    exports com.oracle.javafx.scenebuilder.kit.editor.report;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.jobs;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with KitExtension;
}