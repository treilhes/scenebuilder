module scenebuilder.ext.dummy {
    exports com.oracle.javafx.scenebuilder.dummy;
    exports com.oracle.javafx.scenebuilder.dummy.i18n;
    exports com.oracle.javafx.scenebuilder.dummy.controller;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}