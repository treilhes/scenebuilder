open module jfxapps.boot.registry {

    exports com.gluonhq.jfxapps.boot.registry;
    exports com.gluonhq.jfxapps.boot.registry.config;

    requires transitive jfxapps.registry.model;
    requires transitive jfxapps.boot.maven;
    requires jfxapps.boot.layer;
    requires jfxapps.boot.platform;

    requires jakarta.persistence;
    requires com.fasterxml.jackson.annotation;
    requires org.hibernate.validator;
    requires jakarta.validation;
    requires jakarta.annotation;
    requires spring.context;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.boot.autoconfigure;
    requires org.mapstruct;
    requires java.compiler;
    requires spring.boot;
    requires org.hibernate.orm.core;

    requires org.slf4j;
}