import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.scenicview.ScenicViewExtension;

open module scenebuilder.ext.scenicview {
    exports com.oracle.javafx.scenebuilder.scenicview;
    exports com.oracle.javafx.scenebuilder.scenicview.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    requires org.scenicview.scenicview;
    
    provides Extension with ScenicViewExtension;
}