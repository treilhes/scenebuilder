import com.oracle.javafx.scenebuilder.certmngr.CertificateManagerExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.certificate.manager {
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
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    
    provides Extension with CertificateManagerExtension;
}