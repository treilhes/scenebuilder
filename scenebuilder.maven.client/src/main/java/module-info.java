module scenebuilder.maven.client {
    exports com.oracle.javafx.scenebuilder.maven.client.api;

    requires maven.resolver.provider;

    requires java.logging;
    requires java.json;
    requires java.net.http;

    requires org.apache.commons.lang3;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.connector.basic;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.transport.file;
    requires org.apache.maven.resolver.transport.http;
    requires org.apache.maven.resolver.util;

}