import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.core.preferences.PreferencesExtension;

open module scenebuilder.core.preferences {
    exports com.oracle.javafx.scenebuilder.core.preferences;
    exports com.oracle.javafx.scenebuilder.core.preferences.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with PreferencesExtension;
}