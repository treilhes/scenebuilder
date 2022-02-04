import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.preview.PreviewExtension;

open module scenebuilder.ext.preview {
    exports com.oracle.javafx.scenebuilder.preview.controller;
    exports com.oracle.javafx.scenebuilder.preview.menu;
    exports com.oracle.javafx.scenebuilder.preview;

    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.core;
    requires scenebuilder.ext.content.editor;

    provides Extension with PreviewExtension;
}