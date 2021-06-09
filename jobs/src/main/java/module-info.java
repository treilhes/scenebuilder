module scenebuilder.jobs {
    exports com.oracle.javafx.scenebuilder.job.editor.gridpane.v2;
    exports com.oracle.javafx.scenebuilder.job.editor.gridpane;
    exports com.oracle.javafx.scenebuilder.job.editor;
    exports com.oracle.javafx.scenebuilder.job.editor.reference;
    exports com.oracle.javafx.scenebuilder.job;
    exports com.oracle.javafx.scenebuilder.job.editor.atomic;
    exports com.oracle.javafx.scenebuilder.job.editor.togglegroup;
    exports com.oracle.javafx.scenebuilder.job.editor.wrap;
    exports com.oracle.javafx.scenebuilder.job.preferences.global;

    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}