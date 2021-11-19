import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.sourceview.SourceViewExtension;

open module scenebuilder.ext.source.view {
    exports com.oracle.javafx.scenebuilder.sourceview.actions;
    exports com.oracle.javafx.scenebuilder.sourceview.controller;
    exports com.oracle.javafx.scenebuilder.sourceview;
    exports com.oracle.javafx.scenebuilder.sourceview.i18n;

    requires scenebuilder.starter;
//    requires io.reactivex.rxjava2;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires static lombok;
//    requires rxjavafx;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;
    requires scenebuilder.core.filesystem;

    provides Extension with SourceViewExtension;
}