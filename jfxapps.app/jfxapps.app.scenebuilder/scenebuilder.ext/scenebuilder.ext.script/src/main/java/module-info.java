import org.scenebuilder.ext.script.LoaderCapabilitiesManagerImpl;
import org.scenebuilder.ext.script.ScenebuilderScriptExtension;

import com.gluonhq.jfxapps.core.extension.Extension;
import com.gluonhq.jfxapps.core.fxom.ext.LoaderCapabilitiesManager;

open module scenebuilder.ext.script {
    exports org.scenebuilder.ext.script;
    exports org.scenebuilder.ext.script.i18n;
    exports org.scenebuilder.ext.script.preference.global;

    requires transitive scenebuilder.fxml.api;
    requires transitive scenebuilder.core.extension.api;

    provides Extension with ScenebuilderScriptExtension;
    provides LoaderCapabilitiesManager with LoaderCapabilitiesManagerImpl;
}