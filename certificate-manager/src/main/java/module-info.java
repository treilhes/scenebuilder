module scenebuilder.certificate.manager {
    exports com.oracle.javafx.scenebuilder.certmngr;
    exports com.oracle.javafx.scenebuilder.certmngr.controller;
    exports com.oracle.javafx.scenebuilder.certmngr.i18n;
    exports com.oracle.javafx.scenebuilder.certmngr.tls;

    requires httpclient;
    requires httpcore;
    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires rxjavafx;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}