module scenebuilder.extension.store {
    exports com.oracle.javafx.scenebuilder.extstore.fs;
    exports com.oracle.javafx.scenebuilder.extstore;
    exports com.oracle.javafx.scenebuilder.extstore.i18n;

    requires javafx.graphics;
    requires org.slf4j;
    requires scenebuilder.core;
    requires scenebuilder.extension.api;
    requires spring.beans;
    requires spring.context;
}