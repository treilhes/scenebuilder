module scenebuilder.ext.update.manager {
    exports com.oracle.javafx.scenebuilder.updatemgr.i18n;
    exports com.oracle.javafx.scenebuilder.updatemgr;

    requires scenebuilder.core.extension.api;
    requires scenebuilder.core.api;
    requires spring.context;
}