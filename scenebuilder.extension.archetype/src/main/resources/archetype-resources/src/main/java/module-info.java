import com.oracle.javafx.scenebuilder.extension.Extension;
import $package.${extensionCamelCasedName}Extension;

open module ${artifactId} {
    exports ${package};
    exports ${package}.i18n;
    
    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.api;
    requires spring.context;
    
    provides Extension with ${extensionCamelCasedName}Extension;
}