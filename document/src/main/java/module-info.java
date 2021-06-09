module scenebuilder.document {
    exports com.oracle.javafx.scenebuilder.document.preferences.global;
    exports com.oracle.javafx.scenebuilder.document.panel.document;
    exports com.oracle.javafx.scenebuilder.document.panel.hierarchy.treeview;
    exports com.oracle.javafx.scenebuilder.document.panel.hierarchy;
    exports com.oracle.javafx.scenebuilder.document.panel.info;
    exports com.oracle.javafx.scenebuilder.document;
    exports com.oracle.javafx.scenebuilder.document.actions;

    requires scenebuilder.dnd;
    requires io.reactivex.rxjava2;
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