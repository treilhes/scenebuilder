import org.scenebuilder.ext.script.LoaderCapabilitiesManagerImpl;
import org.scenebuilder.ext.script.ScenebuilderScriptExtension;

import com.oracle.javafx.scenebuilder.core.fxom.ext.LoaderCapabilitiesManager;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.script {
    exports org.scenebuilder.ext.script;
    exports org.scenebuilder.ext.script.i18n;
    exports org.scenebuilder.ext.script.preference.global;

    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;

    provides Extension with ScenebuilderScriptExtension;
    provides LoaderCapabilitiesManager with LoaderCapabilitiesManagerImpl;
}