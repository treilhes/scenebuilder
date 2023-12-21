import com.oracle.javafx.scenebuilder.extension.Extension;
import $package.${extensionCamelCasedName}Extension;

open module ${artifactId} {
    exports ${package};
    exports ${package}.i18n;
    
    requires transitive jfxapps.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with ${extensionCamelCasedName}Extension;
}