module manager.api {
    exports com.gluonhq.jfxapps.app.manager.api;
    exports com.gluonhq.jfxapps.app.manager.api.ui;

    requires transitive jfxapps.core.api;
    requires manager.model;
}