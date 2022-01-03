open module scenebuilder.core.metadata {
    exports com.oracle.javafx.scenebuilder.core.metadata;
    exports com.oracle.javafx.scenebuilder.core.metadata.fx;
    exports com.oracle.javafx.scenebuilder.core.metadata.klass;
    exports com.oracle.javafx.scenebuilder.core.metadata.property;
    exports com.oracle.javafx.scenebuilder.core.metadata.property.value;
    exports com.oracle.javafx.scenebuilder.core.metadata.property.value.list;
    exports com.oracle.javafx.scenebuilder.core.metadata.util;
    exports com.oracle.javafx.scenebuilder.core.metadata.property.value.effect;
    exports com.oracle.javafx.scenebuilder.core.metadata.property.value.keycombination;
    exports com.oracle.javafx.scenebuilder.core.metadata.property.value.paint;

    requires scenebuilder.starter;
    requires transitive scenebuilder.core.extension.api;
    requires transitive scenebuilder.core.fxom;
//
//    requires spring.context;
//    requires spring.beans;
//
//    requires javafx.graphics;
//    requires transitive javafx.controls;
//    requires java.desktop;
//    requires javafx.fxml;
//
    requires scenebuilder.core.utils;

    requires static lombok;
}