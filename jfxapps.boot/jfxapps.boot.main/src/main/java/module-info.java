open module jfxapps.boot.main {

    exports com.gluonhq.jfxapps.boot.main;

    requires jfxapps.boot.loader;
    requires jfxapps.boot.platform;
    requires jfxapps.boot.registry;
    requires jfxapps.boot.layer;
    requires jfxapps.boot.maven;

    requires spring.boot;
    requires spring.boot.autoconfigure;

    requires org.slf4j;
    requires info.picocli;

    // required by javafx
    requires jdk.xml.dom;
    requires jdk.jsobject;
    requires jdk.unsupported;
    requires java.scripting;


    requires java.sql;
    requires java.instrument;

    requires jakarta.persistence;


    //requires jakarta.xml.bind;

}