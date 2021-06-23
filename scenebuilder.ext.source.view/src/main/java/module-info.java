module scenebuilder.ext.source.view {
    exports com.oracle.javafx.scenebuilder.sourceview.actions;
    exports com.oracle.javafx.scenebuilder.sourceview.controller;
    exports com.oracle.javafx.scenebuilder.sourceview;
    exports com.oracle.javafx.scenebuilder.sourceview.i18n;

    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires rxjavafx;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.ext.sb;
    requires spring.beans;
    requires spring.context;
}