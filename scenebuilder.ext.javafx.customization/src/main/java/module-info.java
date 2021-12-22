import com.oracle.javafx.scenebuilder.extension.Extension;
import org.scenebuilder.ext.javafx.customization.JavafxCustomizationExtension;

open module scenebuilder.ext.javafx.customization {
    exports org.scenebuilder.ext.javafx.customization;
    exports org.scenebuilder.ext.javafx.customization.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires scenebuilder.metadata.javafx;
    
    provides Extension with JavafxCustomizationExtension;
}