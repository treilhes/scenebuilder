package com.gluonhq.jfxapps.app.devtools.projects.watcher;

import java.nio.file.Path;

import com.gluonhq.jfxapps.core.api.fs.watcher.File;
import com.gluonhq.jfxapps.core.api.fs.watcher.FileType;
import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;

public class JavaFile extends File {

    public JavaFile(Folder parent, Path location, FileType fileType) {
        super(parent, location, fileType);
    }

    @Override
    public String toString() {
        return "JavaFile [getLocation()=" + getPath() + "]";
    }

}