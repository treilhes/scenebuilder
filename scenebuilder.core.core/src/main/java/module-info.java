import com.oracle.javafx.scenebuilder.core.ScenebuilderCoreExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.core {
    exports com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog;
    exports com.oracle.javafx.scenebuilder.core.mask;
    exports com.oracle.javafx.scenebuilder.core.editors;
    exports com.oracle.javafx.scenebuilder.core.dock.preferences.document;
    exports com.oracle.javafx.scenebuilder.core.i18n;
    exports com.oracle.javafx.scenebuilder.core.action.editor;
    exports com.oracle.javafx.scenebuilder.core.controls;
    exports com.oracle.javafx.scenebuilder.core.content.util;
    exports com.oracle.javafx.scenebuilder.core.clipboard.internal;
    exports com.oracle.javafx.scenebuilder.core.dock;
    exports com.oracle.javafx.scenebuilder.core.di;
    exports com.oracle.javafx.scenebuilder.core.doc;
    exports com.oracle.javafx.scenebuilder.core.editor.drag.source;
    exports com.oracle.javafx.scenebuilder.core;
    exports com.oracle.javafx.scenebuilder.core.util;
    exports com.oracle.javafx.scenebuilder.core.guides;

    requires transitive scenebuilder.core.api;
    requires transitive scenebuilder.core.extension.api;

    requires static lombok;

    provides Extension with ScenebuilderCoreExtension;
}