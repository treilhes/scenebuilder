import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.launcher.LauncherExtension;

open module scenebuilder.core.launcher {
    exports com.oracle.javafx.scenebuilder.launcher;
    exports com.oracle.javafx.scenebuilder.launcher.i18n;
    exports com.oracle.javafx.scenebuilder.launcher.splash;
    exports com.oracle.javafx.scenebuilder.launcher.app;
    exports com.oracle.javafx.scenebuilder.launcher.actions;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires spring.boot;
    requires scenebuilder.core.filesystem;
    
    provides Extension with LauncherExtension;
}