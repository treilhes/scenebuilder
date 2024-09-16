open module jfxapps.boot.maven {

    exports com.gluonhq.jfxapps.boot.maven.client.config;
    exports com.gluonhq.jfxapps.boot.maven.client.type;

    requires jfxapps.boot.api;
    requires jfxapps.boot.platform;
    requires jfxapps.boot.starter;

    requires maven.resolver.provider;

    requires org.apache.commons.lang3;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.connector.basic;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.transport.file;
    requires org.apache.maven.resolver.transport.http;
    requires org.apache.maven.resolver.util;
    requires java.compiler;

}