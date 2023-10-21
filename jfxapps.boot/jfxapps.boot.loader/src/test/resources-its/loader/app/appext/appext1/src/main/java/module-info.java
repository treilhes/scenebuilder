import com.oracle.javafx.scenebuilder.core.loader.api.Extension;
import com.oracle.javafx.scenebuilder.core.loader.appext1.AppExt1;

module appext1 {
    exports com.oracle.javafx.scenebuilder.core.loader.appext1;

    requires api;
    requires org.moditect.layrry.platform;
    requires scenebuilder.starter;
    //requires scenebuilder.core.extension.api;
    requires spring.context;

    provides Extension with AppExt1;
}