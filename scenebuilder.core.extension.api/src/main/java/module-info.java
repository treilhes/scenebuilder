import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.extension.api {
    exports com.oracle.javafx.scenebuilder.extension;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.instrument;
    requires static lombok;

    uses Extension;
}