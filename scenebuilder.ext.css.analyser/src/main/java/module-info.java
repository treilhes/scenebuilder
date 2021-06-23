module scenebuilder.ext.css.analyser {
    exports com.oracle.javafx.scenebuilder.cssanalyser.i18n;
    exports com.oracle.javafx.scenebuilder.cssanalyser.control;
    exports com.oracle.javafx.scenebuilder.cssanalyser.mode;
    exports com.oracle.javafx.scenebuilder.cssanalyser;
    exports com.oracle.javafx.scenebuilder.cssanalyser.actions;
    exports com.oracle.javafx.scenebuilder.cssanalyser.controller;
    exports com.oracle.javafx.scenebuilder.cssanalyser.preferences.global;

    requires io.reactivex.rxjava2;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires org.slf4j;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
}