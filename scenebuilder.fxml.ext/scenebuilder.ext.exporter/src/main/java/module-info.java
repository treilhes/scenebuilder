import com.oracle.javafx.scenebuilder.exporter.ExporterExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.exporter {
    exports com.oracle.javafx.scenebuilder.exporter.i18n;
    exports com.oracle.javafx.scenebuilder.exporter.controller;
    exports com.oracle.javafx.scenebuilder.exporter.menu;
    exports com.oracle.javafx.scenebuilder.exporter.format;
    exports com.oracle.javafx.scenebuilder.exporter;

    requires scenebuilder.starter;
//    requires io.reactivex.rxjava2;
//    requires java.desktop;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.graphics;
//    requires javafx.swing;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;

    provides Extension with ExporterExtension;
}