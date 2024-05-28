module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.core.metadata.fx;

    requires javafx.base;
    requires jfxapps.boot.loader;
    requires jfxapps.core.api;
    requires jfxapps.core.fxom;
    requires jfxapps.core.metadata;
    requires scenebuilder.metadata;
    requires spring.context;
}