import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.library.LibraryExtension;

open module scenebuilder.core.library {
    exports com.oracle.javafx.scenebuilder.library.manager;
    exports com.oracle.javafx.scenebuilder.library.maven;
    exports com.oracle.javafx.scenebuilder.library.api;
    exports com.oracle.javafx.scenebuilder.library.maven.preset;
    exports com.oracle.javafx.scenebuilder.library;
    exports com.oracle.javafx.scenebuilder.library.maven.search;
    exports com.oracle.javafx.scenebuilder.library.store;
    exports com.oracle.javafx.scenebuilder.library.util;
    exports com.oracle.javafx.scenebuilder.library.preferences.global;
    exports com.oracle.javafx.scenebuilder.library.maven.repository;

    //opens com.oracle.javafx.scenebuilder.library.preferences.global to spring.core;

    requires scenebuilder.starter;

//    requires aether.api;
//    requires aether.connector.basic;
//    requires aether.impl;
//    requires aether.spi;
//    requires aether.transport.file;
//    requires aether.transport.http;
//    requires aether.util;
//    requires com.fasterxml.jackson.annotation;
//    requires com.fasterxml.jackson.core;
//    requires com.fasterxml.jackson.databind;
//    requires commons.codec;
//    requires httpclient;
//    requires httpcore;
//    requires java.desktop;
//    requires java.logging;
//    requires java.prefs;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
//    requires javafx.swing;
//    requires javax.json.api;
//    requires maven.aether.provider;
//    requires org.apache.commons.lang3;
//    requires org.slf4j;
//    requires plexus.utils;
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.core;
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.extension.store;
    requires scenebuilder.core.filesystem;
//    requires spring.beans;
//    requires spring.context;
//    requires spring.core;

    provides Extension with LibraryExtension;
}