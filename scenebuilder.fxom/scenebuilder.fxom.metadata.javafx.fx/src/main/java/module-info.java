module scenebuilder.metadata.javafx.fx {
    exports com.oracle.javafx.scenebuilder.metadata.javafx.fx;

    requires scenebuilder.api;
    requires jfxplace.fxom.api;

    requires scenebuilder.fxom.metadata.base;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires spring.context;
}