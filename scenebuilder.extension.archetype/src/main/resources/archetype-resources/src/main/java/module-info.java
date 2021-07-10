import com.oracle.javafx.scenebuilder.extension.Extension;
import $package.${extensionCamelCasedName}Extension;

open module ${artifactId} {
    exports ${package};
    exports ${package}.i18n;
    
    requires transitive scenebuilder.core.api;
    
    provides Extension with ${extensionCamelCasedName}Extension;
}