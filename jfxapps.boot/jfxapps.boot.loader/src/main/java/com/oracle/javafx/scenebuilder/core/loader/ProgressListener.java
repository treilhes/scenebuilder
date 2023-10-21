package com.oracle.javafx.scenebuilder.core.loader;

public interface ProgressListener extends com.oracle.javafx.scenebuilder.core.context.ProgressListener{
    @Override
    void notifyProgress(double progress);
}
