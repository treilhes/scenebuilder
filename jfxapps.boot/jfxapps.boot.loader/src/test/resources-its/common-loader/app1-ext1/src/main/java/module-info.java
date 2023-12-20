import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.editor.ext.EditorExtExtension;

module it.app.editor.ext {

    exports app.editor.ext;
    exports app.editor.ext.api;
    exports app.editor.ext.exported;

    opens app.editor.ext.internal to spring.beans;

    requires scenebuilder.boot.loader;
    requires it.app.editor;

    provides Extension with EditorExtExtension;
}