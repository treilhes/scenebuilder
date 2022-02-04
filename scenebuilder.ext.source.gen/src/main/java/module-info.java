import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.sourcegen.SourceGenExtension;

open module scenebuilder.ext.source.gen {
    exports com.oracle.javafx.scenebuilder.sourcegen.util.eventnames;
    exports com.oracle.javafx.scenebuilder.sourcegen.actions;
    exports com.oracle.javafx.scenebuilder.sourcegen.i18n;
    exports com.oracle.javafx.scenebuilder.sourcegen;

    requires scenebuilder.starter;
//    requires io.reactivex.rxjava2;
//    requires javafx.base;
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
    requires static lombok;
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.core;
    requires scenebuilder.core.extension.api;
//    requires spring.beans;
//    requires spring.context;

    provides Extension with SourceGenExtension;
}