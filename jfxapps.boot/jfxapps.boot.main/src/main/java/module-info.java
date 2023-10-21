module scenebuilder.boot.main {

    exports com.oracle.javafx.scenebuilder.boot.main;
    opens com.oracle.javafx.scenebuilder.boot.main to info.picocli;

    requires scenebuilder.boot.loader;
    requires scenebuilder.boot.platform;

    requires org.slf4j;
    requires info.picocli;

    // required by javafx
    requires jdk.xml.dom;
    requires jdk.jsobject;
    requires jdk.unsupported;
    requires java.scripting;



}