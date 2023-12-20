import com.oracle.javafx.scenebuilder.core.loader.extension.Extension;

import app.editor.extext.EditorExtExtExtension;

module it.app.editor.extext {
    exports app.editor.extext;
    exports app.editor.extext.api;
    exports app.editor.extext.exported;

    opens app.editor.extext.internal to spring.beans;

    requires scenebuilder.boot.loader;
    requires it.app.editor.ext;

    provides Extension with EditorExtExtExtension;

}