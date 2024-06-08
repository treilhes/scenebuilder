module scenebuilder.metadata {
    exports com.oracle.javafx.scenebuilder.metadata.custom.fx;
    exports com.oracle.javafx.scenebuilder.metadata.custom;

    requires transitive jfxapps.core.metadata;
    requires jfxapps.app.starter;
}