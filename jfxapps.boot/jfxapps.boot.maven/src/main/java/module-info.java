open module jfxapps.boot.maven {
    exports com.gluonhq.jfxapps.boot.maven.client.api;
    exports com.gluonhq.jfxapps.boot.maven.client.config;
    exports com.gluonhq.jfxapps.boot.maven.client.type;

    requires maven.resolver.provider;
    //requires maven.model.builder;

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
    requires org.slf4j;

    requires spring.context;
    requires spring.beans;
    requires spring.data.jpa;
    requires spring.data.commons;

    requires jakarta.persistence;
    requires jakarta.annotation;
    requires jfxapps.boot.platform;
    requires org.mapstruct;
    requires java.compiler;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires jfxapps.boot.maven.am;
    //requires maven.model;
    //requires plexus.utils;
}