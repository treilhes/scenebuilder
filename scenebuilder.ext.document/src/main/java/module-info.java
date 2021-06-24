import com.oracle.javafx.scenebuilder.document.DocumentExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.document {
    exports com.oracle.javafx.scenebuilder.document.preferences.global;
    exports com.oracle.javafx.scenebuilder.document.panel.document;
    exports com.oracle.javafx.scenebuilder.document.panel.hierarchy.treeview;
    exports com.oracle.javafx.scenebuilder.document.panel.hierarchy;
    exports com.oracle.javafx.scenebuilder.document.panel.info;
    exports com.oracle.javafx.scenebuilder.document;
    exports com.oracle.javafx.scenebuilder.document.actions;

    requires scenebuilder.core.drag.and.drop;
    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    requires org.slf4j;
    requires scenebuilder.ext.sb;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with DocumentExtension;
}