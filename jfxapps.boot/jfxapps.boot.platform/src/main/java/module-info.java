module jfxapps.boot.platform {
    exports com.gluonhq.jfxapps.boot.platform;
    exports com.gluonhq.jfxapps.boot.platform.config;

    exports com.gluonhq.jfxapps.boot.platform.internal to spring.beans;

    opens com.gluonhq.jfxapps.boot.platform.config to spring.core;

    requires spring.boot;
    requires spring.context;
    requires spring.beans;
}