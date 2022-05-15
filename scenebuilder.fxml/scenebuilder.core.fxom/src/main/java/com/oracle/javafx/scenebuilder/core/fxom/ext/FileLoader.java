package com.oracle.javafx.scenebuilder.core.fxom.ext;

import java.io.File;
import java.io.IOException;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface FileLoader {
    boolean canLoad(File file);
    FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException;
}
