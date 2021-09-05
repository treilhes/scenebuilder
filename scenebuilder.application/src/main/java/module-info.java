import com.oracle.javafx.scenebuilder.app.AppExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.application {
    exports com.oracle.javafx.scenebuilder.app.settings;
    exports com.oracle.javafx.scenebuilder.app.i18n;
    exports com.oracle.javafx.scenebuilder.app;
    
    requires transitive scenebuilder.core.api;
    requires scenebuilder.core.launcher;
    
    provides Extension with AppExtension;
}