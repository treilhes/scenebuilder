import com.treilhes.emc4j.boot.api.loader.extension.Extension;
import com.oracle.javafx.scenebuilder.fxom.merge.ScenebuilderFxomMergeExtension;

module scenebuilder.fxom.merge {
    exports com.oracle.javafx.scenebuilder.fxom.merge;

    requires scenebuilder.api;
    
    provides Extension with ScenebuilderFxomMergeExtension;
}