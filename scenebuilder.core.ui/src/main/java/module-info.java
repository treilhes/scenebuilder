import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.ui.BaseUiExtension;

open module scenebuilder.core.layout {
    exports com.oracle.javafx.scenebuilder.ui.preferences.document;
    exports com.oracle.javafx.scenebuilder.ui.editor.messagelog;
    exports com.oracle.javafx.scenebuilder.ui.menubar;
    exports com.oracle.javafx.scenebuilder.ui.controller;
    exports com.oracle.javafx.scenebuilder.ui.dialog;
    exports com.oracle.javafx.scenebuilder.ui.i18n;
    exports com.oracle.javafx.scenebuilder.ui.message;
    exports com.oracle.javafx.scenebuilder.ui.selectionbar;

    requires io.reactivex.rxjava2;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.filesystem;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with BaseUiExtension;
}