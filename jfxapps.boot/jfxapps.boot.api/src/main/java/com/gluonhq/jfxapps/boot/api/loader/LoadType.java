package com.gluonhq.jfxapps.boot.api.loader;

public enum LoadType {

    /**
     * The application is loaded from the last successful state.
     * Files are not checked for updates and loaded as is from the file system.
     * Only missing extensions are downloaded from the local repository (offline mode).
     * ( Manualy deleted extension will be restored )
     */
    LastSuccessfull,
    /**
     * The application is loaded from the last state.
     * Files are not checked for updates and loaded as is from the file system.
     * Only snapshots, upgraded versions and new extensions from the registry are downloaded
     * from the local repository (offline mode).
     */
    LocalUpdateOnly,
    /**
     * The application is loaded from the last state.
     * Files are not checked for updates and loaded as is from the file system.
     * Only snapshots, upgraded versions and new extensions from the registry are downloaded.
     */
    UpdateOnly,
    /**
     * The application is loaded from the registry definition.
     * Every extension is downloaded from maven  (offline mode).
     */
    LocalFullUpdate,
    /**
     * The application is loaded from the registry definition.
     * Every extension is downloaded from maven.
     */
    FullUpdate
}