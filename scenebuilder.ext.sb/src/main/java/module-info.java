import java.net.spi.URLStreamHandlerProvider;

import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.sb.ScenebuilderContainerExtension;
import com.oracle.javafx.scenebuilder.sb.spi.InMemoryFileURLStreamHandlerProvider;

open module scenebuilder.ext.sb {
    exports com.oracle.javafx.scenebuilder.sb.menu;
    exports com.oracle.javafx.scenebuilder.sb.actions;
    exports com.oracle.javafx.scenebuilder.sb.menu.controller;
    exports com.oracle.javafx.scenebuilder.sb;
    exports com.oracle.javafx.scenebuilder.sb.preferences.global;
    exports com.oracle.javafx.scenebuilder.sb.tooltheme;


//    requires io.reactivex.rxjava2;
//    requires java.prefs;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.graphics;
//    requires lombok;
//    requires org.slf4j;

//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    requires scenebuilder.starter;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires static lombok;

    provides Extension with ScenebuilderContainerExtension;
    provides URLStreamHandlerProvider with InMemoryFileURLStreamHandlerProvider;
}