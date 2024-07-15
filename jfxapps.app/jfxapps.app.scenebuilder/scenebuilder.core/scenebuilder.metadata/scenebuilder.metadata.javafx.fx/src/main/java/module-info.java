module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.metadata.custom.fx;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires jfxapps.boot.loader;
    requires jfxapps.core.api;
    requires jfxapps.core.fxom;
    requires jfxapps.core.metadata;
    requires scenebuilder.metadata.customization;
    requires spring.context;
}