module scenebuilder.ext.source.gen {
    exports com.oracle.javafx.scenebuilder.sourcegen.util.eventnames;
    exports com.oracle.javafx.scenebuilder.sourcegen.actions;
    exports com.oracle.javafx.scenebuilder.sourcegen.i18n;
    exports com.oracle.javafx.scenebuilder.sourcegen;

    requires io.reactivex.rxjava2;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires scenebuilder.core.api;
    requires scenebuilder.core.extension.api;
    requires spring.beans;
    requires spring.context;
    
}