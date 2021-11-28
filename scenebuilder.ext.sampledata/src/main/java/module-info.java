import com.oracle.javafx.scenebuilder.ext.sampledata.SampleDataExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.ext.sampledata {
    exports com.oracle.javafx.scenebuilder.ext.sampledata;
    exports com.oracle.javafx.scenebuilder.ext.sampledata.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with SampleDataExtension;
}