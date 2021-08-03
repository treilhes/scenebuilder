import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.welcome.WelcomeExtension;

open module scenebuilder.ext.welcome {
    exports com.oracle.javafx.scenebuilder.welcome;
    exports com.oracle.javafx.scenebuilder.welcome.i18n;
    exports com.oracle.javafx.scenebuilder.welcome.controller;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires scenebuilder.ext.templates;
    requires scenebuilder.core.filesystem;
    requires scenebuilder.core.launcher;
    
    provides Extension with WelcomeExtension;
}