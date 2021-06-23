module scenebuilder.ext.preview {
    exports com.oracle.javafx.scenebuilder.preview.controller;
    exports com.oracle.javafx.scenebuilder.preview.menu;
    exports com.oracle.javafx.scenebuilder.preview;

    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires rxjavafx;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}