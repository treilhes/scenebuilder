module jfxapps.boot.api {
    exports com.gluonhq.jfxapps.boot.api.context;
    exports com.gluonhq.jfxapps.boot.api.context.annotation;
    exports com.gluonhq.jfxapps.boot.api.layer;
    exports com.gluonhq.jfxapps.boot.api.loader;
    exports com.gluonhq.jfxapps.boot.api.loader.extension;
    exports com.gluonhq.jfxapps.boot.api.maven;
    exports com.gluonhq.jfxapps.boot.api.platform;

    requires jfxapps.boot.starter;

    requires jfxapps.spring.core.patch.link;
    requires jfxapps.hibernate.core.patch.link;
}