import com.oracle.javafx.scenebuilder.draganddrop.DragAndDropExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.drag.and.drop{
    exports com.oracle.javafx.scenebuilder.draganddrop.target;
    exports com.oracle.javafx.scenebuilder.draganddrop;
    exports com.oracle.javafx.scenebuilder.draganddrop.controller;

    requires scenebuilder.starter;
//    requires javafx.base;
//    requires javafx.graphics;
    requires scenebuilder.core.jobs;
    //requires org.slf4j;
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with DragAndDropExtension;
}