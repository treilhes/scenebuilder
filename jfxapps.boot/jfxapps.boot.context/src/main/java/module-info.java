open module jfxapps.boot.context {

    exports com.gluonhq.jfxapps.boot.context.bpp;
    exports com.gluonhq.jfxapps.boot.context.config;
    exports com.gluonhq.jfxapps.boot.context.scope;

    exports com.gluonhq.jfxapps.boot.context.impl to spring.beans;

    requires jfxapps.boot.api;
    requires jfxapps.boot.starter;

}
