import com.oracle.javafx.scenebuilder.api.ApiExtension;
import com.oracle.javafx.scenebuilder.core.CoreExtension;
import com.oracle.javafx.scenebuilder.extension.Extension;

open module scenebuilder.core.api {
    exports com.oracle.javafx.scenebuilder.api.action;
    exports com.oracle.javafx.scenebuilder.api.alert;
    exports com.oracle.javafx.scenebuilder.api.clipboard;
    exports com.oracle.javafx.scenebuilder.api.content.gesture;
    exports com.oracle.javafx.scenebuilder.api.content.mode;
    exports com.oracle.javafx.scenebuilder.api.content;
    exports com.oracle.javafx.scenebuilder.api.control.curve;
    exports com.oracle.javafx.scenebuilder.api.control.decoration;
    exports com.oracle.javafx.scenebuilder.api.control.driver;
    exports com.oracle.javafx.scenebuilder.api.control.droptarget;
    exports com.oracle.javafx.scenebuilder.api.control.effect;
    exports com.oracle.javafx.scenebuilder.api.control.handles;
    exports com.oracle.javafx.scenebuilder.api.control.inlineedit;
    exports com.oracle.javafx.scenebuilder.api.control.intersect;
    exports com.oracle.javafx.scenebuilder.api.control.outline;
    exports com.oracle.javafx.scenebuilder.api.control.pickrefiner;
    exports com.oracle.javafx.scenebuilder.api.control.pring;
    exports com.oracle.javafx.scenebuilder.api.control.relocater;
    exports com.oracle.javafx.scenebuilder.api.control.resizer;
    exports com.oracle.javafx.scenebuilder.api.control.rudder;
    exports com.oracle.javafx.scenebuilder.api.control.tring;
    exports com.oracle.javafx.scenebuilder.api.control;
    exports com.oracle.javafx.scenebuilder.api.controls;
    exports com.oracle.javafx.scenebuilder.api.css;
    exports com.oracle.javafx.scenebuilder.api.dock;
    exports com.oracle.javafx.scenebuilder.api.editor.job;
    exports com.oracle.javafx.scenebuilder.api.i18n;
    exports com.oracle.javafx.scenebuilder.api.library;
    exports com.oracle.javafx.scenebuilder.api.lifecycle;
    exports com.oracle.javafx.scenebuilder.api.menubar;
    exports com.oracle.javafx.scenebuilder.api.preferences.type;
    exports com.oracle.javafx.scenebuilder.api.preferences;
    exports com.oracle.javafx.scenebuilder.api.script;
    exports com.oracle.javafx.scenebuilder.api.settings;
    exports com.oracle.javafx.scenebuilder.api.subjects;
    exports com.oracle.javafx.scenebuilder.api.template;
    exports com.oracle.javafx.scenebuilder.api.theme;
    exports com.oracle.javafx.scenebuilder.api.tooltheme;
    exports com.oracle.javafx.scenebuilder.api.util;
    exports com.oracle.javafx.scenebuilder.api;
    exports com.oracle.javafx.scenebuilder.core.action.editor;
    exports com.oracle.javafx.scenebuilder.core.content.util;
    exports com.oracle.javafx.scenebuilder.core.controls;
    exports com.oracle.javafx.scenebuilder.core.di;
    exports com.oracle.javafx.scenebuilder.core.doc;
    exports com.oracle.javafx.scenebuilder.core.dock.preferences.document;
    exports com.oracle.javafx.scenebuilder.core.dock;
    exports com.oracle.javafx.scenebuilder.core.editor.drag.source;
    exports com.oracle.javafx.scenebuilder.core.editor.images;
    exports com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog;
    exports com.oracle.javafx.scenebuilder.core.editor.selection;
    exports com.oracle.javafx.scenebuilder.core.editors;
    exports com.oracle.javafx.scenebuilder.core.guides;
    exports com.oracle.javafx.scenebuilder.core;
    exports com.oracle.javafx.scenebuilder.editors.drag.target;
    exports com.oracle.javafx.scenebuilder.core.ui;
    exports com.oracle.javafx.scenebuilder.core.clipboard.internal;
    exports com.oracle.javafx.scenebuilder.core.mask;
    exports com.oracle.javafx.scenebuilder.core.util;
    //opens com.oracle.javafx.scenebuilder.api.util to spring.core;
    //opens com.oracle.javafx.scenebuilder.core.metadata to spring.core;
    //opens com.oracle.javafx.scenebuilder.api to spring.core;

    requires transitive scenebuilder.starter;
    requires transitive scenebuilder.core.extension.api;
    requires transitive scenebuilder.core.fxom;
    requires transitive scenebuilder.core.metadata;
    requires transitive scenebuilder.core.utils;

    requires static lombok;

    provides Extension with CoreExtension, ApiExtension;

}