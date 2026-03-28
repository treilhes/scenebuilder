import com.oracle.javafx.scenebuilder.app.ScenebuilderEditorExtension;
import com.treilhes.emc4j.boot.api.loader.extension.Extension;

module scenebuilder.fxom.editor {
    exports com.oracle.javafx.scenebuilder.app;
    exports com.oracle.javafx.scenebuilder.app.editors;

    requires transitive jfxplace.core.api;
    requires transitive scenebuilder.api;
    requires scenebuilder.starter;

    //requires jfxplace.core.utils;

    provides Extension with ScenebuilderEditorExtension;
}