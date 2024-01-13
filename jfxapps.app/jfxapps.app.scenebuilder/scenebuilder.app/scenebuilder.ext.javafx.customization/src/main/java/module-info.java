import org.scenebuilder.ext.javafx.customization.JavafxCustomizationExtension;

import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.javafx.customization {
    exports org.scenebuilder.ext.javafx.customization;
    exports org.scenebuilder.ext.javafx.customization.i18n;

    requires transitive jfxapps.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires scenebuilder.metadata.javafx;

    provides Extension with JavafxCustomizationExtension;
}