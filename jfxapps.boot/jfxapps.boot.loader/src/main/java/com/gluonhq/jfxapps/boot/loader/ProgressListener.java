package com.gluonhq.jfxapps.boot.loader;

public interface ProgressListener extends com.gluonhq.jfxapps.boot.context.ProgressListener{
    @Override
    void notifyProgress(double progress);
}
