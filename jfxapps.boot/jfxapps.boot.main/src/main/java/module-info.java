module jfxapps.boot.main {

    exports com.gluonhq.jfxapps.boot.main;
    opens com.gluonhq.jfxapps.boot.main.command to info.picocli;
    opens com.gluonhq.jfxapps.boot.main.config;

    requires jfxapps.boot.loader;
    requires jfxapps.boot.platform;
    requires jfxapps.boot.registry;
    requires jfxapps.boot.layer;

    requires spring.boot.autoconfigure;
    requires org.slf4j;
    requires info.picocli;

    // required by javafx
    requires jdk.xml.dom;
    requires jdk.jsobject;
    requires jdk.unsupported;
    requires java.scripting;

    requires spring.boot;
    requires java.sql;
    requires java.instrument;
    requires jfxapps.boot.maven;


    //requires jakarta.xml.bind;

}