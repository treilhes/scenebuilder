import com.oracle.javafx.scenebuilder.core.di.DependencyInjectionExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.di {
    exports com.oracle.javafx.scenebuilder.core.di;
    exports com.oracle.javafx.scenebuilder.core.di.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with DependencyInjectionExtension;
}