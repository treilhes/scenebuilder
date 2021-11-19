import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.logviewer.LogViewerExtension;

open module scenebuilder.ext.log.viewer {
    exports com.oracle.javafx.scenebuilder.logviewer.i18n;
    exports com.oracle.javafx.scenebuilder.logviewer.controller;
    exports com.oracle.javafx.scenebuilder.logviewer;

    requires scenebuilder.starter;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
//    requires logback.classic;
//    requires logback.core;
//    requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;

    provides Extension with LogViewerExtension;
}