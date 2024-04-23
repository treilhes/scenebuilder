package com.gluonhq.jfxapps.core.fxom.ext;

import java.io.File;
import java.io.IOException;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

public interface FileLoader {
    boolean canLoad(File file);
    FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException;
}
