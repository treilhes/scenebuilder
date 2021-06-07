module scenebuilder.debug.menu {
    exports com.oracle.javafx.scenebuilder.debugmenu.i18n;
    exports com.oracle.javafx.scenebuilder.debugmenu.controller;
    exports com.oracle.javafx.scenebuilder.debugmenu.menu;
    exports com.oracle.javafx.scenebuilder.debugmenu;

    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires scenebuilder.jobs;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}