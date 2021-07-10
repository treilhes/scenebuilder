import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.updatemgr.UpdateManagerExtension;

open module scenebuilder.ext.update.manager {
    exports com.oracle.javafx.scenebuilder.updatemgr.i18n;
    exports com.oracle.javafx.scenebuilder.updatemgr;

    requires scenebuilder.core.extension.api;
    requires transitive scenebuilder.core.api;
    requires spring.context;
    
    provides Extension with UpdateManagerExtension;
}