module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.metadata.javafx.fx;

    requires jfxapps.boot.api;
    requires jfxapps.core.fxom;
    requires jfxapps.core.metadata;

    requires scenebuilder.metadata.customization;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires spring.context;
}