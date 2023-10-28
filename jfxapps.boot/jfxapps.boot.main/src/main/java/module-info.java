module jfxapps.boot.main {

    exports com.gluonhq.jfxapps.boot.main;
    opens com.gluonhq.jfxapps.boot.main to info.picocli;

    requires jfxapps.boot.loader;
    requires jfxapps.boot.platform;

    requires org.slf4j;
    requires info.picocli;

    // required by javafx
    requires jdk.xml.dom;
    requires jdk.jsobject;
    requires jdk.unsupported;
    requires java.scripting;



}