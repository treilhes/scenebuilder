module scenebuilder.dnd {
    exports com.oracle.javafx.scenebuilder.draganddrop.target; 
    exports com.oracle.javafx.scenebuilder.draganddrop;
    exports com.oracle.javafx.scenebuilder.draganddrop.controller;

    requires javafx.base;
    requires javafx.graphics;
    requires scenebuilder.jobs;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}