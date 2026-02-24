module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.metadata.javafx.fx;

    requires emc4j.boot.api;
    requires jfxplace.fxom.api;

    requires scenebuilder.metadata.customization;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires spring.context;
}