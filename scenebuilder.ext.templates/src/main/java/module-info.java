import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.template.TemplateExtension;

open module scenebuilder.ext.templates {
    exports com.oracle.javafx.scenebuilder.template.menu;
    exports com.oracle.javafx.scenebuilder.template.i18n;
    exports com.oracle.javafx.scenebuilder.template.controller;
    exports com.oracle.javafx.scenebuilder.template.templates;
    exports com.oracle.javafx.scenebuilder.template;

    requires transitive scenebuilder.core.api;

    requires scenebuilder.ext.defaultx;
    requires scenebuilder.core.jobs;

    requires static lombok;

    provides Extension with TemplateExtension;
}