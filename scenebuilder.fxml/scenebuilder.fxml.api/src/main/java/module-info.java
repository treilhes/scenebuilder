import com.oracle.javafx.scenebuilder.extension.Extension;
import org.scenebuilder.fxml.api.FxmlApiExtension;

open module scenebuilder.fxml.api {
    exports org.scenebuilder.fxml.api;
    exports org.scenebuilder.fxml.api.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with FxmlApiExtension;
}