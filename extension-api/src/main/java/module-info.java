module scenebuilder.extension.api {
    exports com.oracle.javafx.scenebuilder.extension;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.instrument;
    requires lombok;
}