import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.template.TemplateExtension;

open module scenebuilder.ext.templates {
    exports com.oracle.javafx.scenebuilder.template.menu;
    exports com.oracle.javafx.scenebuilder.template.i18n;
    exports com.oracle.javafx.scenebuilder.template.controller;
    exports com.oracle.javafx.scenebuilder.template.templates;
    exports com.oracle.javafx.scenebuilder.template;

    requires scenebuilder.starter;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires static lombok;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.ext.defaultx;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.jobs;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with TemplateExtension;
}