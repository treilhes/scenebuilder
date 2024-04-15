open module jfxapps.boot.main {

    exports com.gluonhq.jfxapps.boot.main;

    requires jfxapps.boot.loader;
    requires jfxapps.boot.platform;
    requires jfxapps.boot.registry;
    requires jfxapps.boot.layer;
    requires jfxapps.boot.maven;
    requires jfxapps.boot.jpa;
    requires jfxapps.boot.starter;

    requires info.picocli;

}