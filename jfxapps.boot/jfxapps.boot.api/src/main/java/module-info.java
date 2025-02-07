open module jfxapps.boot.api {

    exports com.gluonhq.jfxapps.boot.api.aop;
    exports com.gluonhq.jfxapps.boot.api.context;
    exports com.gluonhq.jfxapps.boot.api.context.annotation;
    exports com.gluonhq.jfxapps.boot.api.jpa;
    exports com.gluonhq.jfxapps.boot.api.layer;
    exports com.gluonhq.jfxapps.boot.api.loader;
    exports com.gluonhq.jfxapps.boot.api.loader.extension;
    exports com.gluonhq.jfxapps.boot.api.maven;
    exports com.gluonhq.jfxapps.boot.api.platform;
    exports com.gluonhq.jfxapps.boot.api.registry;
    exports com.gluonhq.jfxapps.boot.api.registry.model;
    exports com.gluonhq.jfxapps.boot.api.utils;
    exports com.gluonhq.jfxapps.boot.api.web.client;

    requires jfxapps.boot.starter;

    requires jfxapps.spring.core.patch.link;
    requires jfxapps.hibernate.core.patch.link;
}