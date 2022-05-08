import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.core.editor.EditorExtension;

open module scenebuilder.core.editor {
    exports com.oracle.javafx.scenebuilder.core.editor;
    exports com.oracle.javafx.scenebuilder.core.editor.i18n;
    
    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;
    
    provides Extension with EditorExtension;
}