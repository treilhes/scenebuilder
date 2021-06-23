import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.fs.FileSystemExtension;

module scenebuilder.core.filesystem {
    exports com.oracle.javafx.scenebuilder.fs.util;
    exports com.oracle.javafx.scenebuilder.fs.preference.global;
    exports com.oracle.javafx.scenebuilder.fs.controller;
    exports com.oracle.javafx.scenebuilder.fs;
    exports com.oracle.javafx.scenebuilder.fs.menu;

    requires io.reactivex.rxjava2;
    requires java.logging;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    
    provides Extension with FileSystemExtension;
}