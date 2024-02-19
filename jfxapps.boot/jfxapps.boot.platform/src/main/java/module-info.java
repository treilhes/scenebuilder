open module jfxapps.boot.platform {

    exports com.gluonhq.jfxapps.boot.platform;
    exports com.gluonhq.jfxapps.boot.platform.config;
    exports com.gluonhq.jfxapps.boot.platform.internal to spring.beans;

    requires spring.boot;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires java.net.http;
    requires spring.boot.autoconfigure;
    requires com.fasterxml.jackson.core;
    requires jakarta.inject;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
}