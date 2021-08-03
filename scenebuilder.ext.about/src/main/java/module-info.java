import com.oracle.javafx.scenebuilder.about.AboutExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.about {
    exports com.oracle.javafx.scenebuilder.about;
    exports com.oracle.javafx.scenebuilder.about.i18n;
    exports com.oracle.javafx.scenebuilder.about.actions;
    exports com.oracle.javafx.scenebuilder.about.controller;
    exports com.oracle.javafx.scenebuilder.about.menu;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with AboutExtension;
}