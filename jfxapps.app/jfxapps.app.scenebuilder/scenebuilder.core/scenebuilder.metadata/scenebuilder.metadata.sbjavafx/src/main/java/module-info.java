module scenebuilder.metadata.sbjavafx {
    exports com.oracle.javafx.scenebuilder.metadata.custom.addon;

    requires transitive scenebuilder.metadata.javafx;
    requires transitive scenebuilder.metadata.javafx.fx;
    requires transitive scenebuilder.metadata.customization;
    requires jfxapps.core.starter;

}