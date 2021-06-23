module scenebuilder.ext.debug.menu {
    exports com.oracle.javafx.scenebuilder.debugmenu.i18n;
    exports com.oracle.javafx.scenebuilder.debugmenu.controller;
    exports com.oracle.javafx.scenebuilder.debugmenu.menu;
    exports com.oracle.javafx.scenebuilder.debugmenu;

    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}