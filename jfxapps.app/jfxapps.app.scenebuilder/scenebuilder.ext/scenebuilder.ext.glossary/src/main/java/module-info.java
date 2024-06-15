import com.gluonhq.jfxapps.core.extension.Extension;
import com.oracle.javafx.scenebuilder.glossary.GlossaryExtension;

open module scenebuilder.ext.glossary {
    exports com.oracle.javafx.scenebuilder.glossary;
    exports com.oracle.javafx.scenebuilder.glossary.i18n;
    
    //requires scenebuilder.core.extension.api;
    requires transitive scenebuilder.api;
    //requires spring.context;
    //requires javafx.base;
    
    provides Extension with GlossaryExtension;
}