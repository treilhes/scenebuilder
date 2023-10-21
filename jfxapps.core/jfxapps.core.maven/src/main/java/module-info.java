open module scenebuilder.core.maven {
    exports com.oracle.javafx.scenebuilder.library.maven.artifact;
    exports com.oracle.javafx.scenebuilder.library.maven;
    exports com.oracle.javafx.scenebuilder.library.maven.impl;
    exports com.oracle.javafx.scenebuilder.library.preferences.global;
    exports com.oracle.javafx.scenebuilder.library.maven.repository;

    requires transitive scenebuilder.core.api;
    requires jakarta.annotation;
    requires scenebuilder.boot.maven;

}