import com.oracle.javafx.scenebuilder.core.di.DependencyInjectionExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module jfxapps.core.di {
    exports com.oracle.javafx.scenebuilder.core.di;
    exports com.oracle.javafx.scenebuilder.core.di.i18n;
    
    requires transitive jfxapps.core.api;
    requires transitive jfxapps.core.extension.api;
    
    provides Extension with DependencyInjectionExtension;
}