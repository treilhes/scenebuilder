module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.metadata.javafx.fx;

    requires emc4j.boot.api;
    requires jfxplace.core.fxom;
    requires jfxplace.core.metadata;

    requires scenebuilder.metadata.customization;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires spring.context;
}