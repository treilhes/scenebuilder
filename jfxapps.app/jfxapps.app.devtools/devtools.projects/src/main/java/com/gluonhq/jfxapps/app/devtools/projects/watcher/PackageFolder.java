package com.gluonhq.jfxapps.app.devtools.projects.watcher;

import java.nio.file.Path;

import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;
import com.gluonhq.jfxapps.core.api.fs.watcher.FolderType;
import com.gluonhq.jfxapps.core.api.fs.watcher.Watcher;

public class PackageFolder extends Folder {
    public PackageFolder(Watcher watcher, Folder parent, Path location, FolderType type) {
        super(watcher, parent, location, type);
    }

    @Override
    public String toString() {
        return "PackageFolder [getLocation()=" + getPath() + "]";
    }

}