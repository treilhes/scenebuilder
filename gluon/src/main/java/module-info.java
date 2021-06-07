module scenebuilder.gluon {
    exports com.oracle.javafx.scenebuilder.gluon.setting;
    exports com.oracle.javafx.scenebuilder.gluon.dialog;
    exports com.oracle.javafx.scenebuilder.gluon.editor.job;
    exports com.oracle.javafx.scenebuilder.gluon.preferences.document;
    exports com.oracle.javafx.scenebuilder.gluon.theme;
    exports com.oracle.javafx.scenebuilder.gluon;
    exports com.oracle.javafx.scenebuilder.gluon.menu;
    exports com.oracle.javafx.scenebuilder.gluon.i18n;
    exports com.oracle.javafx.scenebuilder.gluon.registration;
    exports com.oracle.javafx.scenebuilder.gluon.alert;
    exports com.oracle.javafx.scenebuilder.gluon.template;
    exports com.oracle.javafx.scenebuilder.gluon.metadata;
    exports com.oracle.javafx.scenebuilder.gluon.preferences.global;
    exports com.oracle.javafx.scenebuilder.gluon.controller;

    requires charm.glisten;
    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javax.json.api;
    requires scenebuilder.jobs;
    requires scenebuilder.library;
    requires org.slf4j;
    requires scenebuilder.control.library;
    requires scenebuilder.controls;
    requires scenebuilder.core;
    requires scenebuilder.defaultx;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}