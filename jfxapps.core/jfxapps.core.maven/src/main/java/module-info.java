open module scenebuilder.core.maven {
    exports com.gluonhq.jfxapps.core.maven.artifact;
    exports com.gluonhq.jfxapps.core.maven;
    exports com.gluonhq.jfxapps.core.maven.impl;
    exports com.gluonhq.jfxapps.core.maven.preferences.global;
    exports com.gluonhq.jfxapps.core.maven.repository;

    requires transitive jfxapps.core.api;
    requires jakarta.annotation;
    requires jfxapps.boot.maven;

}