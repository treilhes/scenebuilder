import com.gluonhq.jfxapps.core.extension.Extension;
import com.oracle.javafx.scenebuilder.inspector.InspectorExtension;

open module scenebuilder.ext.inspector {
    exports com.oracle.javafx.scenebuilder.inspector.controller;
    exports com.oracle.javafx.scenebuilder.inspector;
    exports com.oracle.javafx.scenebuilder.inspector.actions;
    exports com.oracle.javafx.scenebuilder.inspector.i18n;
    exports com.oracle.javafx.scenebuilder.inspector.preferences.document;

    requires scenebuilder.starter;
//    requires io.reactivex.rxjava2;
//    requires java.prefs;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires scenebuilder.core.jobs;
//    requires org.slf4j;
//    requires rxjavafx;
    //requires scenebuilder.ext.sb;
    requires transitive scenebuilder.api;
    requires scenebuilder.ext.editors;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.selection;
    requires scenebuilder.ext.controls.tooling;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with InspectorExtension;
}