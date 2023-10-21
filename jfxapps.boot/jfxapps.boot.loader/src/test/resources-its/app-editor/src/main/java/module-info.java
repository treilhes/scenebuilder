import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.editor.EditorExtension;

module it.app.editor {
    exports app.editor;
    exports app.editor.api;
    exports app.editor.exported;

    opens app.editor.internal to spring.beans;

    requires scenebuilder.boot.loader;
    requires it.app.root;

    provides Extension with EditorExtension;
}